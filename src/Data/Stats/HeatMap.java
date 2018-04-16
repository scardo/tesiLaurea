/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Stats;

import Data.Datasource;
import Windows.Main;
import java.awt.Color;
import java.awt.Graphics;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.postgis.LineString;
import org.postgis.PGgeometry;
import org.postgis.Point;

/**
 *
 * @author insan3
 */
public class HeatMap extends JMapViewer {

    private double dayTime[];
    private LinkedList<Raster> rasterList;

    public HeatMap() {
        super(new MemoryTileCache(), 4);
        dayTime = new double[2];
        rasterList = new LinkedList<Raster>();

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

    @Override
    public void paintComponent(Graphics g1) {
        super.paintComponent(g1);
     
        
        if (this.rasterList!=null) {
            int minMax[]=this.getMinMax();
            
            for (Raster r : this.rasterList) {
                
                java.awt.Point p = this.getMapPosition(r.getRaster()[1].y, r.getRaster()[0].x, false);
                java.awt.Point p1 = this.getMapPosition(r.getRaster()[0].y, r.getRaster()[1].x, false);

                int width = p1.x - p.x;
                int height = p1.y - p.y;
                
                
                Color c=new Color(0, 0, 0, 255);
                int diff=(minMax[1]-minMax[0])/3;
                
                if(r.getCount()<minMax[0]+diff){
                    c=new Color(0, 255, 0, 80);
                }else if(r.getCount()<minMax[0]+2*diff){
                    c=new Color(255, 255, 0, 80);
                }else{
                    c=new Color(255, 0, 0, 80);
                }
                
                
                g1.setColor(c); 
                g1.fillRect(p.x, p.y, width, height);

            }
        }
    }


    public void updateRasterList(Point p[], int zoomLevel, int depth) {
        this.rasterList.clear();

        if (zoomLevel <= zoomLevel + depth) {
            //start the recursive method
            recursiveUpdateRasterList(Datasource.getInstance().getTrajIDFiltered(p, dayTime), p, zoomLevel, depth);
        }

    }


    
    private void recursiveUpdateRasterList(LinkedList<Integer> trajID, Point p[], int zoomLevel, int depth) {
        int count = Datasource.getInstance().getNumTraj(p,trajID);
        if (count > 0) {
            if (zoomLevel == this.getZoom() + depth) {
                //fixed point

                this.rasterList.add(new Raster(p, count));

            } else {
                for (int i = 0; i < 4; i++) {
                    //recursive step

                    recursiveUpdateRasterList(trajID, getSubMap(p)[i], zoomLevel + 1, depth);

                }
            }
        }
    }
    
    public void dynamicUpdateRasterList(Point p[]) {
        this.rasterList.clear();

        
            //start the recursive method
        recursiveDynamicUpdateRasterList(Datasource.getInstance().getTrajIDFiltered(p, dayTime), p, 0);
        

    }
    
    private void recursiveDynamicUpdateRasterList(LinkedList<Integer> trajID, Point p[], int depth) {
        int count = Datasource.getInstance().getNumTraj(p,trajID);
        if (count > 0) {
            if (count<=3 || DistanceCalculator.distanceInMeters(p[0].y,p[0].x,p[1].y,p[1].x)<=30) {
                //fixed point
                
                this.rasterList.add(new Raster(p, depth));

            } else {
                for (int i = 0; i < 4; i++) {
                    //recursive step

                    recursiveDynamicUpdateRasterList(trajID, getSubMap(p)[i], depth+1);
                    
                }
            }
        }
    }
    
    private Point[][] getSubMap(Point in[]) {
        Point out[][] = new Point[4][2];
        Point middle = new Point();
        middle.setX((in[0].x + in[1].x) / 2);
        middle.setY((in[0].y + in[1].y) / 2);

        //Q0
        out[0][0] = new Point();
        out[0][1] = new Point();
        out[0][0].setX(in[0].x);
        out[0][0].setY(middle.y);
        out[0][1].setX(middle.x);
        out[0][1].setY(in[1].y);

        //Q1
        out[1][0] = new Point();
        out[1][1] = new Point();
        out[1][0].setX(middle.x);
        out[1][0].setY(middle.y);
        out[1][1].setX(in[1].x);
        out[1][1].setY(in[1].y);

        //Q2
        out[2][0] = new Point();
        out[2][1] = new Point();
        out[2][0].setX(middle.x);
        out[2][0].setY(in[0].y);
        out[2][1].setX(in[1].x);
        out[2][1].setY(middle.y);

        //Q3
        out[3][0] = new Point();
        out[3][1] = new Point();
        out[3][0].setX(in[0].x);
        out[3][0].setY(in[0].y);
        out[3][1].setX(middle.x);
        out[3][1].setY(middle.y);

        return out;
    }

    @Override
    public void setZoom(int i) {
        if (i >= 13 && i <= 19) {
            super.setZoom(i);
        }

    }

    public void setDayTime(double[] dayTime) {
        this.dayTime = dayTime;

    }

    public void clearRasterList() {
        this.rasterList.clear();
    }
    
    private int[] getMinMax(){
        int out[]={Integer.MAX_VALUE,Integer.MIN_VALUE};
        
        for (Raster r : this.rasterList) {
            if(r.getCount()<out[0]){
                out[0]=r.getCount();
            }
            if(r.getCount()>out[1]){
                out[1]=r.getCount();
            }
        }
        
        return out;
    }

}
