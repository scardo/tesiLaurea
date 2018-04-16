package Data.Stats;

/**
 *
 * @author insan3
 */
public class BarPlotData {
    private Double values[];
    private String[] labels;

    public BarPlotData(Double[] values, String[] labels) {
        this.values = values;
        this.labels = labels;
    }

    public Double[] getValues() {
        return values;
    }

    public String[] getLabels() {
        return labels;
    }
    
    
}
