package edu.hit.yh.gitdata.githubDataPersistence;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class DataCutterTest {

	@Test
	public void testExtractJsonData() {
		File file = new File("D:/githubRawData/2014-04-10-14.json");
		DataCutter dataCutter = new DataCutter(file);
		
		try {
			List<String> list = dataCutter.extractJsonData("jquery/jquery");
			System.out.println(list.get(0));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
