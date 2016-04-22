package edu.hit.yh.gitdata.analyse.constant;

import java.util.Arrays;
import java.util.List;

public class AnalyseConstant {

	/**
	 * 个性化指数，高于这个个性化指数的模式则称为这个项目里具有代表性的模式
	 */
	public static Double INDIVIDUATION_INDEX = 2.0;
	
	public static List<String> ANALYSE_RESULT_HEADER =  Arrays.asList("类标号","距今时间","协作总时间","协作总步数","间隔时间最大值","间隔时间最小值","间隔时间中位数","参与开发者人数");
	
}
