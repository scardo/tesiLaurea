/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Stats;

import org.postgis.Point;

/**
 *
 * @author insan3
 */
public class Raster {
    private Point raster[];
    private int count;

    public Raster(Point raster[], int count) {
        this.raster = raster;
        this.count = count;
    }

    public Point[] getRaster() {
        return raster;
    }

    public int getCount() {
        return count;
    }

    public void setRaster(Point raster[]) {
        this.raster = raster;
    }

    public void setCount(int count) {
        this.count = count;
    }
    

}
