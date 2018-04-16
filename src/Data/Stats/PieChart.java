package Data.Stats;

import Data.Datasource;
import javax.swing.ImageIcon;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.postgis.LineString;

public class PieChart {

    private JFreeChart chart;
    private PieData data;
    /**
     * 
     * @param title
     * @param geom Bounds
     * @param dayTime DayTime filter
     */
    public PieChart(String title, LineString geom, double dayTime[]) {
        
        //do the query
        this.data=Datasource.getInstance().getWeekDistribution(geom,dayTime);
        this.chart = createChart(createDataset(data.getLabels(), data.getPerc()), title);

        
    }

    private PieDataset createDataset(String labels[], Double perc[]) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (int i = 0; i < labels.length; i++) {
            dataset.setValue(labels[i], perc[i]);
        }

        return dataset;
    }

    private JFreeChart createChart(PieDataset dataset, String title) {
        JFreeChart ch = ChartFactory.createPieChart(
                title, // chart title 
                dataset, // data    
                true, // include legend   
                true,
                false);

        return ch;
    }
    
    public int getTrajNum(){
        return this.data.getTrajNum();
    }
    
    
    public ImageIcon getImageIcon(int x, int y){
        
        return new ImageIcon(this.chart.createBufferedImage(x, y));
    }
    
    
    

}
