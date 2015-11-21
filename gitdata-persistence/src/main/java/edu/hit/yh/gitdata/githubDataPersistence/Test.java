package edu.hit.yh.gitdata.githubDataPersistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 程序的测试空间，不做主体出现
 * @author ASUS
 *
 */
public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println(Integer.MIN_VALUE);
		
		System.out.println(Integer.MIN_VALUE/1024/1024/1024);
		
		byte[] dst = new byte[1024];
		byte[] cutPoint = {125,13,10,123};
		int cutFlag;
		File file = new File("D://jsonType.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(dst);
		/*String string = new String(dst, "utf-8");
		System.out.println(string.split("}\\n\\r{").length)*/;
		long time1 = System.currentTimeMillis();
		/*判断分割数据点，将多余的返还到offset中*/
		for(int i = dst.length-3;i>-1;i--){
			if(dst[i]==cutPoint[0]){
				if(dst[i+1]==cutPoint[1]){
					if(dst[i+2]==cutPoint[2]){
						if(dst[i+3]==cutPoint[3]){
							cutFlag = i;
							System.out.println(i);
							System.out.println(System.currentTimeMillis()-time1+"ms");
							break;
						}
					}
				}
			}
		}
		
		
	}

}
