package edu.hit.yh.gitdata.analyseSR;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.bytecode.internal.javassist.FastClass;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;

import edu.hit.yh.gitdata.analyse.constant.AnalyseConstant;
import edu.hit.yh.gitdata.analyse.util.ExportSvgUtil;

public class MyBoxPlot extends ApplicationFrame {

	/** 
     *  
     */  
    private static final long serialVersionUID = -3205574763811416266L;  
    
    private Shape createEllipse(Point2D point, double oRadius) {
        Ellipse2D dot = new Ellipse2D.Double(point.getX(), point.getY(), oRadius*1.0, oRadius*1.0);
        return dot;
}
    
    
    /** 
     * Creates a new demo. 
     * 
     * @param title  the frame title. 
     */  
    public MyBoxPlot(final String title ,List<double[]> data,String dir) {  
        super(title);  
        
        /**
         * 未完成需要传数据
         */
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(data);  
        final CategoryAxis xAxis = new CategoryAxis("");  
        final NumberAxis yAxis = new NumberAxis(AnalyseConstant.boxplotUnitMap.get(title));  
        yAxis.setAutoRangeIncludesZero(false);  
        final MyBoxAndWhiskerRenderer renderer = new MyBoxAndWhiskerRenderer(); 
        renderer.setFillBox(false);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());  
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        
        final JFreeChart chart = new JFreeChart(  
            null,  
            new Font("SansSerif", Font.BOLD, 10),  
            plot,  
            true  
        );
        xAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 14));
        yAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 14));
        yAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        chart.setBackgroundPaint(Color.WHITE);
        final ChartPanel chartPanel = new ChartPanel(chart);  
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 430));  
        setContentPane(chartPanel); 
        
        File file = new File(dir+title+".svg");
        try {
			//ChartUtilities.saveChartAsPNG(file, chart, 430, 430);
			ExportSvgUtil.saveChartAsSvg(chart, 520, 430, file);
        	//CreatePDF.saveChartAsPDF(file, chart, 430, 430, new DefaultFontMapper());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
                dataset.add(list,"Project", CalculateUtil.REPO_LIST_SIMPLE.get(i));  
            
            }  
        }
        return dataset;  
    }  
}
