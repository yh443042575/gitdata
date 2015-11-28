package edu.hit.yh.gitdata.test;

import edu.hit.yh.gitdata.githubDataAnalyzer.MainAnalyzer;

public class MainAnalyzerTest {
/*
	@Test
	public void testStartAnalyze() {
		MainAnalyzer mainAnalyzer = new MainAnalyzer();
		mainAnalyzer.startAnalyze();				
	}*/

	/**
	 * 解析tar.gz文件的入口
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String args[]) throws InterruptedException{
		MainAnalyzer mainAnalyzer = new MainAnalyzer();
		mainAnalyzer.startAnalyze();
	}
	
}
