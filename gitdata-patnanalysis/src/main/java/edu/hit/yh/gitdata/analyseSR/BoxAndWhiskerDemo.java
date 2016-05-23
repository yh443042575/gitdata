package edu.hit.yh.gitdata.analyseSR;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BoxAndWhiskerDemo extends ApplicationFrame{

	 /** 
     *  
     */  
    private static final long serialVersionUID = -3205574763811416266L;  
    /** 
     * Creates a new demo. 
     * 
     * @param title  the frame title. 
     */  
    public BoxAndWhiskerDemo(final String title) {  
        super(title);  
        
        /**
         * 未完成需要传数据
         */
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(null);  
        final CategoryAxis xAxis = new CategoryAxis("Type");  
        final NumberAxis yAxis = new NumberAxis("Value");  
        yAxis.setAutoRangeIncludesZero(false);  
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();  
        renderer.setFillBox(false);  
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());  
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);  
        final JFreeChart chart = new JFreeChart(  
            "Box-and-Whisker Demo",  
            new Font("SansSerif", Font.BOLD, 14),  
            plot,  
            true  
        );  
        final ChartPanel chartPanel = new ChartPanel(chart);  
        chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));  
        setContentPane(chartPanel);  
    }  
    /** 
     * Creates a sample dataset. 
     *  
     * @return A sample dataset. 
     */  
    private BoxAndWhiskerCategoryDataset createSampleDataset(List<double[]> rawData) {  
          
        final int seriesCount = rawData.size();  
        final int categoryCount = 1;  
        double[] data = null;  
        final DefaultBoxAndWhiskerCategoryDataset dataset   
            = new DefaultBoxAndWhiskerCategoryDataset();  
        for (int i = 0; i < seriesCount; i++) {  
            data = rawData.get(i);              
            for (int j = 0; j < categoryCount; j++) {  
                final List list = new ArrayList();  
                for (int k = 0; k < data.length; k++) {  
                    list.add(new Double(data[k]));  
                }  
                dataset.add(list, "Series " + i, " Type " + j);  
            }  
        }  
        return dataset;  
    }  
    /** 
     * For testing from the command line. 
     * 
     * @param args  ignored. 
     */  
    public static void main(final String[] args) {  
        final BoxAndWhiskerDemo demo = new BoxAndWhiskerDemo("Box-and-Whisker Chart Demo");  
        demo.pack();  
        RefineryUtilities.centerFrameOnScreen(demo);  
        demo.setVisible(true);  
    }  
	
}
