/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Stats;

/**
 *
 * @author insan3
 */
public class PieData {

    private String labels[];
    private Double perc[];
    private int trajNum;

    public PieData(String[] labels, Double[] perc, int i) {
        this.labels = labels;
        this.perc = perc;
        this.trajNum=i;
    }
    
    public PieData() {
        this.labels = labels;
        this.perc = perc;
    }

    public String[] getLabels() {
        return labels;
    }

    public Double[] getPerc() {
        return perc;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public void setPerc(Double[] perc) {
        this.perc = perc;
    }

    public int getTrajNum() {
        return trajNum;
    }

 
    
}
