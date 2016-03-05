package edu.hit.yh.gitdata.mine.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class SimpleBehaviorComparator implements Comparator<SimpleBehavior> {

	/**
	 * 根据日期的比较，
	 * @param o1
	 * @param o2
	 * @return
	 */
	@Override
	public int compare(SimpleBehavior o1, SimpleBehavior o2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time1= o1.getCreatedAt().replaceAll("[T-Z]", " ");
		String time2= o2.getCreatedAt().replaceAll("[T-Z]", " ");

		try {
			System.out.println(time1);
			System.out.println(time2);
			Date date1 = sdf.parse(time1);
			Date date2 = sdf.parse(time2);
			if(date1.equals(date2)){
				return 0;
			}else {
				if(date1.before(date2)){
					return -1;
				}else {
					return 1;
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
}
