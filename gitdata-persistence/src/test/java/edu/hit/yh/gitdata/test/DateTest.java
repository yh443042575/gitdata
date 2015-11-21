package edu.hit.yh.gitdata.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTest {

	public static void main(String[] args) {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd-HH");
		Date date = calendar.getTime();
		System.out.println(sdf.format(date));

	}

}
