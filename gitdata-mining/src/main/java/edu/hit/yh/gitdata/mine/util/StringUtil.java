package edu.hit.yh.gitdata.mine.util;

public class StringUtil {

	/**
	 * 将对象转换成字符串，如果对象为空的话，就返回null字符串
	 * @param obj
	 * @return
	 */
	public static String objectToString(Object obj){
		
		if(obj == null){
			return "null";
		}else {
			return obj.toString();
		}
		
	}
	
}
