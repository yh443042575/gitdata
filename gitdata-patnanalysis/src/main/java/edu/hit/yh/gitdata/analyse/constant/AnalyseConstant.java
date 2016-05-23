package edu.hit.yh.gitdata.analyse.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AnalyseConstant {

	/**
	 * 个性化指数，高于这个个性化指数的模式则称为这个项目里具有代表性的模式
	 */
	public static Double INDIVIDUATION_INDEX = 2.0;
	
	/**
	 * 供聚类分析的原始数据表的表头
	 */
	public static List<String> ANALYSE_RESULT_HEADER =  Arrays.asList("类标号","协作总时间","协作总步数","间隔时间最大值","间隔时间最小值","间隔时间中位数","参与开发者人数","评论数");
	
	/**
	 * 供蜘蛛图形显示的表头
	 */
	public static List<String> SPIDER_HEADER =  Arrays.asList("协作总时间","协作总步数","间隔时间最大值","间隔时间最小值","间隔时间中位数","参与开发者人数","评论数");
	
	/**
	 * 供蜘蛛图形显示的表头，英文版
	 */
	public static List<String> SPIDER_HEADER_IN_ENGLISH =  Arrays.asList("CD","CS","MaxI","MinI","MedI","NP","NC");
	
	/**
	 * 我们采集数据的属性个数（也就是上面蜘蛛表头的属性个数）
	 */
	public static int PROPERTY_NUM = 7;
	
	public static HashMap<String, String> boxplotUnitMap = new HashMap<String, String>();
	
	static{
		boxplotUnitMap.put("Time from project biuld", "month");
		boxplotUnitMap.put("CD", "days");
		boxplotUnitMap.put("CS", "number");
		boxplotUnitMap.put("MaxI", "days");
		boxplotUnitMap.put("MinI", "hours");
		boxplotUnitMap.put("MedI", "hours");
		boxplotUnitMap.put("NP", "number");
		boxplotUnitMap.put("NC", "number");
		
	}
}
