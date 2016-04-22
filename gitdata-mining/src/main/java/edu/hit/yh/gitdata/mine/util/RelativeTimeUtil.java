package edu.hit.yh.gitdata.mine.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 此类用于将实际的时间换算成相对时间
 * 
 * @author DHAO
 *
 */
public class RelativeTimeUtil {

	/**
	 * 计算两个时间之间的相对时间
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static String calculateRelativeTime(String time1, String time2) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time1 = time1.replaceAll("[T-Z]", " ");
		time2 = time2.replaceAll("[T-Z]", " ");

		try {
			if (time1 == null || time2 == null || time1.equals("null")
					|| time2.equals("null")) {
				return "between 1 and 3";
			}
			Date date1 = sdf.parse(time1);
			Date date2 = sdf.parse(time2);

			long time = date2.getTime() - date1.getTime();
			long quotient = time / (86400000L);

			if (quotient < 0) {// 不合法的时间
				return "unleagle";
			} else if (quotient <= 1) {// 1天之内
				return "less than 1";
			} else if (quotient > 1 && quotient <= 3) {// 1天~1周之间
				return "between 1 and 3";
			} else if (quotient > 3 && quotient <= 5) {// 3天~5天之间
				return "between 3 and 5";
			} else if (quotient > 5 && quotient <= 7) {// 5天~7天之间
				return "between 5 and 7";
			} else if (quotient > 7 && quotient <= 14) {// 7天~14天之间
				return "between 1 and 2 week";
			} else if (quotient > 14 && quotient <= 21) {// 14天~21天之间
				return "between 2 and 3 week";
			} else if (quotient > 21 && quotient <= 28) {// 21天~28天之间
				return "between 3 and 4 week";
			} else if (quotient > 14) {// 大于两周
				return "more than 1 month";
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public static Double getTimeUnitNum(String time1, String time2,long timeUnit) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time1 = time1.replaceAll("[T-Z]", " ");
		time2 = time2.replaceAll("[T-Z]", " ");
		
		Double days = -1.0;
		try {
			if (time1 == null || time2 == null || time1.equals("null")
					|| time2.equals("null")) {
				return -1.0;
			}
			Date date1;
			date1 = sdf.parse(time1);
			Date date2 = sdf.parse(time2);

			Long time = date2.getTime() - date1.getTime();
			days = (double)time / ((double)timeUnit);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return days;

	}

	public static void main(String args[]) {
		String resultString = RelativeTimeUtil.calculateRelativeTime(
				"2014-10-08T21:16:49Z", "2014-10-08T21:16:51Z");
		System.out.println(resultString);
		
		long time = 500;
		long x = 33;
		double y = (double)time/(double)x;
		System.out.println(y);
		
		
		
		
	}

}
