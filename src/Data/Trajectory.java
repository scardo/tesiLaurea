package Data;

import Windows.Main;
import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.postgis.LineString;
import org.postgis.PGgeometry;

/**
 *
 * @author alessandropandolfo
 */
@SuppressWarnings("UnusedAssignment")
public class Trajectory {

    private ArrayList<Trajectory> intersects;
    private String id_t;
    private static int colorRapp = 1;

    public static void setColorRapp(int rapp) {
        colorRapp = rapp;
    }

    public ArrayList<Trajectory> getIntersects() {
        return intersects;
    }

    public Trajectory() {
        super();
        this.id_t = "";
        intersects = null;
    }
    
    public boolean isIntersection(String id){
        if(intersects!=null){
            for (Trajectory intersect : intersects) {
                if(id.equals(intersect.id_t)){
                    return true;
                }
            }
        }
        return false;
    }

    public Trajectory(String trId) {
        super();
        this.id_t = trId;
        intersects = null;
    }

    public String getId_t() {
        return id_t;
    }

    public PGgeometry getGeometry() {
        return Datasource.getInstance().getGeom(this);
    }

    public void setId_t(String id_t) {
        this.id_t = id_t;
    }

    /**
     *
     * @param geom
     * @return double[] List of the points of a trajectory={ownerId, lat, lon,
     * alt}
     * @throws SQLException
     */
    public ArrayList<double[]> pointsList(String geom) throws SQLException {
        return Datasource.getInstance().pointsOfTrajectory(this, geom);
    }

    /**
     * Get the bounds coord of a trajectory list
     *
     * @param in trajectory list
     * @return double[] out= {maxLat, maxLon, minLat, minLon}
     * @throws SQLException
     */
    public static double[] getTrajectoryBounds(ArrayList<Trajectory> in) throws SQLException {
        double out[] = new double[4];

        out = Datasource.getInstance().getSpatialBorder(in);

        return out;
    }
    public static double[] tb = null;

    public static double[] settb(ArrayList<Trajectory> t, LineString geom) throws SQLException {
        tb = timeBounds(t, geom);
        return tb;
    }

    //0:min, 1:max
    public static double[] timeBounds(ArrayList<Trajectory> t, LineString geom) throws SQLException {
        double time[] = new double[2];
        time = Datasource.getInstance().getTimeBorder(t, geom);
        return time;
    }

    public double[] timeBoundsOfTraj() throws SQLException {
        double time[] = new double[2];
        time = Datasource.getInstance().getTimeBorderOfTrajectory(this, null);
        return time;
    }

    public static int cprec = 0;

    public static Color getTimeColor(double pt) throws IOException {

        LocalDateTime dt = (new Timestamp((long) pt)).toLocalDateTime();
        if (colorRapp == 0) {//colora in base alle ore del giorno 
            return selectHourColor(dt.getHour());
        }
        if (colorRapp == 1) {//colora in base al giorno della settimana
            return selectDayOfWeekColor(dt.getDayOfWeek().getValue());
        }
        if (colorRapp == 2) {//colora in base al giorno del mese
            return selectDayOfMonthColor(dt.getDayOfMonth());
        }
        if (colorRapp == 3) {//colora in base al mese
            selectMonthColor(dt.getMonthValue());
        }
        return Color.WHITE;
    }

    public static Color selectHourColor(int hour) {
        switch (hour) {
            case 0:
            case 1:
            case 2:
            case 3:
                return new Color(0, 0, 255 - ((255 / 3) * (3 - hour)));
            case 4:
                return new Color(50, 50, 255);
            case 5:
                return new Color(100, 100, 255);
            case 6:
                return new Color(175, 175, 255);
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return new Color(255, 175 + (15 * (hour - 7)), 0);
            case 12:
                return new Color(255, 0, 0);
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                return new Color(255, 175 - (30 * (hour - 12)), 0);
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return new Color(0, 255 - ((255 / 6) * (hour - 17)), 0);
        }
        return Color.WHITE;
    }

    public static Color selectDayOfWeekColor(int day) {
        switch (day) {
            case 1:
                return Color.GRAY;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.BLACK;
            case 6:
                return Color.RED;
            case 7:
                return Color.ORANGE;
        }
        return Color.WHITE;
    }

    public static Color selectDayOfMonthColor(int day) {
        switch (day) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return new Color(255 - (25 * (day - 1)), 0, 0);
            case 10:           
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                return new Color(0, 255 - (25 * ((day % 10))), 0);
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
                return new Color(0, 0, 255 - (25 * ((day % 10))));
            case 30:
                return new Color(0, 0, 25);
            case 31:
                return new Color(0, 0, 0);
        }
        return Color.WHITE;
    }

    public static Color selectMonthColor(int month) {
        switch (month) {
            case 1:
                return Color.GRAY;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.MAGENTA;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.CYAN;
            case 6:
                return Color.BLUE;
            case 7:
                return Color.ORANGE;
            case 8:
                return Color.RED;
            case 9:
                return Color.PINK;
            case 10:
                return Color.LIGHT_GRAY;
            case 11:
                return Color.DARK_GRAY;
            case 12:
                return Color.BLACK;
        }
        return Color.WHITE;
    }

    public void spaceIntersectionsWithTrajectory(MapPanel map) {
        LineString geom = null;
        ICoordinate p1 = map.getPosition(0, map.getHeight());
        ICoordinate p2 = map.getPosition(map.getWidth(), 0);
        try {
            geom = (LineString) PGgeometry.geomFromString("LINESTRING ("
                    + p1.getLon() + " " + p1.getLat() + ", "
                    + p1.getLon() + " " + p2.getLat() + ", "
                    + p2.getLon() + " " + p2.getLat() + ", "
                    + p2.getLon() + " " + p1.getLat() + ", "
                    + p1.getLon() + " " + p1.getLat() + " "
                    + ")");

        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        Datasource ds = Datasource.getInstance();
        String str="";
        for (Trajectory tra : map.getTrajectory()) {
            str+=tra.getId_t()+",";
        }
        str=str.substring(0, str.length()-1);
        intersects = ds.spaceIntersect(this);
        System.out.println("sixe=" + intersects.size());
    }

    public static double getTimeColorUnit() {
        int maxColor = 255 * 3 - 100;
        double bounds[] = tb;
        double range = (bounds[1] - bounds[0]) / (60000);
        return maxColor / range;
    }

}
