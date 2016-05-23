package edu.hit.yh.gitdata.analyse.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import edu.hit.yh.gitdata.mine.constant.DirConstant;

public class ExportSvgUtil {

	/**
	 * 将jfreechart保存成svg格式
	 * @param chart
	 * @param wigth
	 * @param height
	 * @param file
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws SVGGraphics2DIOException 
	 */
	public static void saveChartAsSvg(JFreeChart chart,int wigth,int height,File file) throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException{
		DOMImplementation domImpl = GenericDOMImplementation  
                .getDOMImplementation();  
        // Create an instance of org.w3c.dom.Document  
        Document document = domImpl.createDocument(null, "svg", null);  
        // Create an instance of the SVG Generator  
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);  
        // set the precision to avoid a null pointer exception in Batik 1.5  
        svgGenerator.getGeneratorContext().setPrecision(6);  
        // Ask the chart to render into the SVG Graphics2D implementation  
        chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, wigth,height), null);  
        // Finally, stream out SVG to a file using UTF-8 character to  
        // byte encoding  
        boolean useCSS = true;  
        Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");  
        svgGenerator.stream(out, useCSS);  
	}
	
}
