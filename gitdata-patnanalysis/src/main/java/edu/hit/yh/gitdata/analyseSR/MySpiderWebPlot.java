package edu.hit.yh.gitdata.analyseSR;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.hit.yh.gitdata.analyse.util.ExportSvgUtil;
import edu.hit.yh.gitdata.mine.constant.DirConstant;

public class MySpiderWebPlot extends SpiderWebPlot {
	private int ticks = DEFAULT_TICKS;
	private static final int DEFAULT_TICKS = 5;
	private NumberFormat format = NumberFormat.getInstance();
	private static final double PERPENDICULAR = 90;
	private static final double TICK_SCALE = 0.015;
	private int valueLabelGap = DEFAULT_GAP;
	private static final int DEFAULT_GAP = 10;
	private static final double THRESHOLD = 150;

	MySpiderWebPlot(CategoryDataset createCategoryDataset) {
		super(createCategoryDataset);
	}

	@Override
	protected void drawLabel(final Graphics2D g2, final Rectangle2D plotArea,
			final double value, final int cat, final double startAngle,
			final double extent) {
		super.drawLabel(g2, plotArea, value, cat, startAngle, extent);
		final FontRenderContext frc = g2.getFontRenderContext();
		final double[] transformed = new double[2];
		final double[] transformer = new double[2];
		final Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);

		for (int i = 1; i <= ticks; i++) {

			final Point2D point1 = arc1.getEndPoint();

			final double deltaX = plotArea.getCenterX();
			final double deltaY = plotArea.getCenterY();
			double labelX = point1.getX() - deltaX;
			double labelY = point1.getY() - deltaY;

			final double scale = ((double) i / (double) ticks);
			final AffineTransform tx = AffineTransform.getScaleInstance(scale,
					scale);
			final AffineTransform pointTrans = AffineTransform
					.getScaleInstance(scale + TICK_SCALE, scale + TICK_SCALE);
			transformer[0] = labelX;
			transformer[1] = labelY;
			pointTrans.transform(transformer, 0, transformed, 0, 1);
			final double pointX = transformed[0] + deltaX;
			final double pointY = transformed[1] + deltaY;
			tx.transform(transformer, 0, transformed, 0, 1);
			labelX = transformed[0] + deltaX;
			labelY = transformed[1] + deltaY;

			double rotated = (PERPENDICULAR);

			AffineTransform rotateTrans = AffineTransform.getRotateInstance(
					Math.toRadians(rotated), labelX, labelY);
			transformer[0] = pointX;
			transformer[1] = pointY;
			rotateTrans.transform(transformer, 0, transformed, 0, 1);
			final double x1 = transformed[0];
			final double y1 = transformed[1];

			rotated = (-PERPENDICULAR);
			rotateTrans = AffineTransform.getRotateInstance(
					Math.toRadians(rotated), labelX, labelY);

			rotateTrans.transform(transformer, 0, transformed, 0, 1);

			final Composite saveComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					1.0f));

			g2.draw(new Line2D.Double(transformed[0], transformed[1], x1, y1));

			if (startAngle == this.getStartAngle()) {
				final String label = format
						.format(((double) i / (double) ticks)
								* this.getMaxValue());
				final Rectangle2D labelBounds = getLabelFont().getStringBounds(
						label, frc);

				final LineMetrics lm = getLabelFont()
						.getLineMetrics(label, frc);
				final double ascent = lm.getAscent();
				if (Math.abs(labelX - plotArea.getCenterX()) < THRESHOLD) {
					labelX += valueLabelGap;
					labelY += ascent / (float) 2;
				} else if (Math.abs(labelY - plotArea.getCenterY()) < THRESHOLD) {
					labelY += valueLabelGap;
				} else if (labelX >= plotArea.getCenterX()) {
					if (labelY < plotArea.getCenterY()) {
						labelX += valueLabelGap;
						labelY += valueLabelGap;
					} else {
						labelX -= valueLabelGap;
						labelY += valueLabelGap;
					}
				} else {
					if (labelY > plotArea.getCenterY()) {
						labelX -= valueLabelGap;
						labelY -= valueLabelGap;
					} else {
						labelX += valueLabelGap;
						labelY -= valueLabelGap;
					}
				}
				g2.setPaint(getLabelPaint());
				g2.setFont(new Font("SansSerif", Font.BOLD, 20));
				g2.drawString(label, (float) labelX, (float) labelY);
			}
			g2.setComposite(saveComposite);
		}
	}

	
	/**
	 * dimName 为蛛网上每个坐标的名字，
	 * resultMap中一个infomap相当于一个网，
	 * infoMap中的一个key是dimName的坐标
	 * 
	 * @param resultMap
	 * @param dimNameList
	 * @return
	 */
	@SuppressWarnings("unused")
	public static DefaultCategoryDataset createDataSet2(
			Map<String, Map<String, Object>> resultMap, List<String> dimNameList) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();// 创建默认的种类数据类型就可以了，蜘蛛图的每个维度可以看成一种类型
		Set<String> keySet = resultMap.keySet();
		for (String key : keySet) {
			Map<String, Object> infoMap = resultMap.get(key);
			for (String dimName : dimNameList) {
				if (infoMap.get(dimName) == null) {
					continue;
				}
				double score = (Double) infoMap.get(dimName);
				dataset.addValue(score,
						key,
						dimName);
			}
		}
		return dataset;
	}

	public static void main(String args[]) {
		
		
		Map<String, Object> subGraph1 = new HashMap<String, Object>();
		subGraph1.put("time", 0.5);
		subGraph1.put("age", 0.7);
		subGraph1.put("length",0.3);
		Map<String,  Map<String, Object>> graph = new HashMap<String, Map<String, Object>>();
		graph.put("test&22", subGraph1);
		Map<String, Object> subGraph2 = new HashMap<String, Object>();
		subGraph2.put("time", 0.8);
		subGraph2.put("age", 0.6);
		subGraph2.put("length",0.8);
		graph.put("all&value", subGraph2);
		List<String> dimName = new ArrayList<String>();
		dimName.add("time");
		dimName.add("age");
		dimName.add("length");
		
		DefaultCategoryDataset categoryDataset =  createDataSet2(graph,dimName);
		MySpiderWebPlot mySpiderWebPlot = new MySpiderWebPlot(categoryDataset);
		mySpiderWebPlot.setMaxValue(1.0);
		JFreeChart chart = new JFreeChart(mySpiderWebPlot);
		ChartFrame chartFrame=new ChartFrame("某公司人员组织数据图",chart); 
		
		chartFrame.setVisible(true);
		File file = new File(DirConstant.CHART_TO_SVG + "jfreechart1.pdf");  
		try {
			ExportSvgUtil.saveChartAsSvg(chart, 400, 300, file);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVGGraphics2DIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
