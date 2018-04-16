package Data;

import Windows.Main;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.postgis.LineString;
import org.postgis.PGgeometry;

/**
 *
 * @author alessandropandolfo
 */
@SuppressWarnings("UnusedAssignment")
public class MapPanel extends JMapViewer {

    private boolean load;
    private Trajectory selected;

    public void setSelected(Trajectory selected) {
        this.selected = selected;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }
    private ArrayList<Trajectory> trajectory;
    private double[] timeRange;

    public ArrayList<Trajectory> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(ArrayList<Trajectory> trajectory) {
        this.trajectory = trajectory;
    }

    /*
    uso costruttore con parametri di jmapviewer inquanto quello senza parametri
    crea un DefaultMapController non funzionante
     */
    public MapPanel() {
        super(new MemoryTileCache(), 4);
        trajectory = null;
        timeRange = null;
        load = false;
        selected = null;
    }


    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        
        ArrayList<double[]> points = new ArrayList<>();
        double timeBounds[] = new double[2];
        Graphics2D g = (Graphics2D) g1;
        g.setStroke(new BasicStroke(this.getZoom() / 4));
        if (load) {
            load = false;
            trajectory = Datasource.getInstance().getTrajectoryFromDB(getVisibleZone(), timeRange);

        }
        if (trajectory != null) {
            System.out.println("N of traj: " + trajectory.size());
            for (Trajectory trajectory1 : trajectory) {
                System.out.print(trajectory1.getId_t() + ", ");
            }
            System.out.println("\n");
        }
        String geom = null;
        ICoordinate p1 = this.getPosition(0, this.getHeight());
        ICoordinate p2 = this.getPosition(this.getWidth(), 0);

        Color cI = null;
        Color cF = null;
        if (trajectory != null && trajectory.size() > 0) {
            System.out.println("draw");
            for (Trajectory t : trajectory) {
                try {
                    geom = "LINESTRING ("
                            + p1.getLon() + " " + p1.getLat() + ", "
                            + p1.getLon() + " " + p2.getLat() + ", "
                            + p2.getLon() + " " + p2.getLat() + ", "
                            + p2.getLon() + " " + p1.getLat() + ", "
                            + p1.getLon() + " " + p1.getLat() + " "
                            + ")";
                    points = t.pointsList(geom);
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                int j = 0;
                int i = 0;
                for (; i < points.size() - 1; i++) {
                    Point punto1 = this.getMapPosition(points.get(i)[1], points.get(i)[2], false);
                    Point punto2 = this.getMapPosition(points.get(i + 1)[1], points.get(i + 1)[2], false);
                    if (((int) punto1.getX() != (int) punto2.getX()) || ((int) punto1.getY() != (int) punto2.getY())) {

                        try {
                            if (selected != null) {
                                if (points.get(i)[4] != 0 || points.get(i + 1)[4] != 0) {
                                    if (t.getId_t().equals(selected.getId_t())) {
                                        cI = Color.RED;
                                        cF = Color.RED;
                                    } else {
                                        if (selected.isIntersection(t.getId_t())) {
                                            cI = Color.BLACK;
                                            cF = Color.BLACK;
                                        }
                                    }

                                    GradientPaint gp = new GradientPaint(punto1, cI, punto2, cF);
                                    g.setPaint(gp);

                                    g.drawLine((int) punto1.getX(), (int) punto1.getY(), (int) punto2.getX(), (int) punto2.getY());
                                }
                            } else {
                                if (points.get(i)[4] != 0 || points.get(i + 1)[4] != 0) {
                                    if (points.get(i)[4] != 0 && points.get(i + 1)[4] != 0) {
                                        cI = Trajectory.getTimeColor(points.get(i)[4]);
                                        cF = Trajectory.getTimeColor(points.get(i + 1)[4]);
                                    } else if (points.get(i)[4] != 0) {
                                        cI = Trajectory.getTimeColor(points.get(i)[4]);
                                        cF = Trajectory.getTimeColor(points.get(i)[4]);
                                    } else {
                                        cI = Trajectory.getTimeColor(points.get(i + 1)[4]);
                                        cF = Trajectory.getTimeColor(points.get(i + 1)[4]);
                                    }
                                    GradientPaint gp = new GradientPaint(punto1, cI, punto2, cF);
                                    g.setPaint(gp);

                                    g.drawLine((int) punto1.getX(), (int) punto1.getY(), (int) punto2.getX(), (int) punto2.getY());
                                }
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(MapPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }

            }
        }
    }

    public void setTimeRange(double[] timeRange) {
        this.timeRange = timeRange;
    }

    public double[] getTimeRange() {
        return timeRange;
    }

    public Trajectory selectTrajectory(int x, int y) {
        org.postgis.Point punto = null;
        try {
            punto = (org.postgis.Point) PGgeometry.geomFromString("POINT(" + this.getPosition(x, y).getLon()
                    + " " + this.getPosition(x, y).getLat() + ")");

        } catch (SQLException ex) {
            Logger.getLogger(Trajectory.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return Datasource.getInstance().getSelectedTra(punto, trajectory);
    }

    public LineString getVisibleZone() {
        LineString geom = null;
        ICoordinate p1 = this.getPosition(0, this.getHeight());
        ICoordinate p2 = this.getPosition(this.getWidth(), 0);
        try {
            geom = (LineString) PGgeometry.geomFromString("LINESTRING ("
                    + p1.getLon() + " " + p1.getLat() + ", "
                    + p1.getLon() + " " + p2.getLat() + ", "
                    + p2.getLon() + " " + p2.getLat() + ", "
                    + p2.getLon() + " " + p1.getLat() + ", "
                    + p1.getLon() + " " + p1.getLat() + " "
                    + ")");

        } catch (SQLException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return geom;
    }

    public double[] getBoundsValue() {
        double bounds[] = new double[4];

        bounds[2] = this.getPosition((int) this.getBounds().getMaxX(), (int) this.getBounds().getMaxY()).getLat();
        bounds[1] = this.getPosition((int) this.getBounds().getMaxX(), (int) this.getBounds().getMaxY()).getLon();
        bounds[0] = this.getPosition((int) this.getBounds().getMinX(), (int) this.getBounds().getMinY()).getLat();
        bounds[3] = this.getPosition((int) this.getBounds().getMinX(), (int) this.getBounds().getMinY()).getLon();
        return bounds;
    }

}
