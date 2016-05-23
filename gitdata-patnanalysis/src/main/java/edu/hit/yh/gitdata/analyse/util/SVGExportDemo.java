package edu.hit.yh.gitdata.analyse.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import edu.hit.yh.gitdata.mine.constant.DirConstant;


public class SVGExportDemo {

	public static void main(String[] args) throws IOException {  
        // create a dataset...  
        DefaultPieDataset data = new DefaultPieDataset();  
        data.setValue("Category 1", new Double(43.2));  
        data.setValue("Category 2", new Double(27.9));  
        data.setValue("Category 3", new Double(79.5));  
        // create a chart  
        JFreeChart chart = ChartFactory.createPieChart("Sample Pie Chart",  
                data, true, true, true);  
        // THE FOLLOWING CODE BASED ON THE EXAMPLE IN THE BATIK DOCUMENTATION...  
        // Get a DOMImplementation  
        DOMImplementation domImpl = GenericDOMImplementation  
                .getDOMImplementation();  
        // Create an instance of org.w3c.dom.Document  
        Document document = domImpl.createDocument(null, "svg", null);  
        // Create an instance of the SVG Generator  
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);  
        // set the precision to avoid a null pointer exception in Batik 1.5  
        svgGenerator.getGeneratorContext().setPrecision(6);  
        // Ask the chart to render into the SVG Graphics2D implementation  
        chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, 1100,700), null);  
        // Finally, stream out SVG to a file using UTF-8 character to  
        // byte encoding  
        boolean useCSS = true;  
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(  
                DirConstant.CHART_TO_SVG+"test.svg")), "UTF-8");  
        svgGenerator.stream(out, useCSS);  
    }  
	
}
