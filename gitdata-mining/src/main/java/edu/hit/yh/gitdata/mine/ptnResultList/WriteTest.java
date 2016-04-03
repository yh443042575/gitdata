package edu.hit.yh.gitdata.mine.ptnResultList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 用来测试在相对路径下建立文件
 * @author Dhao
 *
 */
public class WriteTest {

	public static void main(String args[]) throws IOException{
		File file = new File("src/main/java/edu/hit/yh/gitdata/mine/ptnResultList/test.txt");
		if(!file.exists()){
			file.createNewFile();
			System.out.println();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write("hello world");
		bw.flush();
		bw.close();
	}
	
	
	
}
