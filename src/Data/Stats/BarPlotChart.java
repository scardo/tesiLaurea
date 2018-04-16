package Data.Stats;

import Data.Datasource;
import javax.swing.ImageIcon;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.postgis.LineString;

/**
 *
 * @author insan3
 */
public class BarPlotChart {
    private BarPlotData data;
    private JFreeChart chart;
    
    /**
     * 
     * @param title
     * @param geom Bounds
     * @param dayTime DayTime filter
     * @param start true if you want to plot the start time
     * 
     */
    public BarPlotChart(String title, LineString geom, double dayTime[], boolean start) {
        
        //do the query

        this.data=Datasource.getInstance().getHourDistribution(geom,dayTime,start);

            
        this.chart = createChart(createDataset(data.getLabels(), data.getValues()), title);

        
    }
    private CategoryDataset createDataset(String labels[], Double values[]) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < labels.length; i++) {
            dataset.addValue(values[i], labels[i],"");
        }

        return dataset;
    }
    
    private JFreeChart createChart(CategoryDataset dataset, String title) {
        JFreeChart ch =ChartFactory.createBarChart(
         title,           
         "Hours",            
         "Number of Traj",            
         dataset,          
         PlotOrientation.VERTICAL,           
         true, true, false);
        
        return ch;
    }
    
    public ImageIcon getImageIcon(int x, int y){
        
        return new ImageIcon(this.chart.createBufferedImage(x, y));
    }
}
