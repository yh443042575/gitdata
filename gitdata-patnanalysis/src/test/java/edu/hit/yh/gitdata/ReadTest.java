package edu.hit.yh.gitdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * 读取文件测试
 */
public class ReadTest {
	
	public static void main(String args[]) throws IOException{
		File file = new File("/Users/mac/git/gitdata/gitdata-mining/src/main/java/edu/hit/yh/gitdata/mine/ptnResultList/test.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		System.out.println(br.readLine());
		
	}
	
	
}
