package edu.hit.yh.gitdata.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 测试正则表达式，获取@的人名
 * @author DHAO
 *
 */
public class RegxTest {

	public static void main(String[] args) {

		/**
		 * 获取人名
		 */
		/*Pattern pattern = Pattern.compile("@[a-zA-Z]+");
		String content = "abcasd @com msdas";
		Matcher matcher = pattern.matcher(content);*/
		
		/**
		 * 获取commitComment + artifactId
		 */
		/*Pattern pattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/commit/[a-z[0-9]]+");
		String content = "https://github.com/jquery/jquery/commit/93043d002a5ec8646cbd9d5658434848a1d07a49#src/data.js-P46";
		Matcher matcher = pattern.matcher(content);*/
		
		/**
		 * 获取issuesComment + artifactId
		 */
		/*Pattern pattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/issues/[a-z[0-9]]+");
		String content = "https://github.com/jquery/jquery/issues/1112#issuecomment-11955025";
		Matcher matcher = pattern.matcher(content);*/
		
		
		Pattern pattern = Pattern.compile("[a-zA-Z]+/[a-zA-Z]+/pull/[a-z[0-9]]+|[a-zA-Z]+/[a-zA-Z]+/issues/[a-z[0-9]]+");
		String content = "https://github.com/jquery/jquery/issues/1106.diff";
		Matcher matcher = pattern.matcher(content);
		
		
		
		while(matcher.find()){
			System.out.println(matcher.group());
		}		
		
	}

}
