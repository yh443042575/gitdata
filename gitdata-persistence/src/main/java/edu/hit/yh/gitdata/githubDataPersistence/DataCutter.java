package edu.hit.yh.gitdata.githubDataPersistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.oracle.nio.BufferSecrets;

/**
 * 分割githubArchive中下载来的大数据
 * 1、能够将大数据切割成小块
 * 2、分析每一个小块中的数据，识别出所有我们所需要的行为的json
 * 
 * @author DHAO
 *
 */
public class DataCutter {

	/* 切割出的每个buffer大小,当前一次读取4M数据 */
	static int BUFFER_SIZE = 4 * 1024 * 1024;

	/*
	 * 截取的项目名称 private String program;
	 */
	/* 原始大文件的地址 */
	private long fileLength;
	private int offset = 0;
	private boolean fileScannedFinishFlag = false;
	/**
	 * 切割点的byte数组，用ASCII码翻译来是：}\回车\换行{
	 */
	byte[] cutPoint = { 125, 13, 10, 123 };
	private RandomAccessFile randomAccessFile;
	private FileChannel fc;
	private MappedByteBuffer in;
	/* 用于存放截取文件的片段 */
	private byte[] dst;

	public DataCutter(File file) {

		fileLength = file.length();
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			fc = randomAccessFile.getChannel();
			in = fc.map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* 根据偏移量去切割数据,同时修改offset */
	private byte[] cut() throws IOException {
		dst = new byte[BUFFER_SIZE];
		if (fileLength - offset >= BUFFER_SIZE) {
			for (int i = 0; i < BUFFER_SIZE; i++)
				dst[i] = in.get(offset + i);

		} else {
			for (int i = 0; i < fileLength - offset; i++)
				dst[i] = in.get(offset + i);
			fileScannedFinishFlag = true;
		}
		offset += BUFFER_SIZE;

		for (int i = dst.length - 3; i > -1; i--) {
			if (dst[i] == cutPoint[0]) {
				if (dst[i + 1] == cutPoint[1]) {
					if (dst[i + 2] == cutPoint[2]) {
						if (dst[i + 3] == cutPoint[3]) {
							/* 检测到分割点后将多余的偏移量移回 */
							offset -= dst.length - i;
							break;
						}
					}
				}
			}
		}
		return dst;
	}

	/*
	 * 接收得到的byte数组，过滤出我们想要的相关的项目的json
	 */
	@SuppressWarnings("static-access")
	public List<String> extractJsonData(String program) throws IOException {

		List<String> resultList = new ArrayList<String>();

		int upFlag;
		int downFlag;
		int scannedFlag = 0;// 保存扫描过的位置的尾部，每一次找到新的json之后都更新一次

		Pattern pattern = Pattern.compile(program+"\"|"+program+"/");// "https://github\\.com/"+

		while (!fileScannedFinishFlag) {
			String partition = new String(this.cut());

			Matcher matcher = pattern.matcher(partition);

			while (matcher.find()) {

				upFlag = matcher.start();
				downFlag = matcher.end();
				if (upFlag > scannedFlag) {
					while (upFlag >= 2) {
						if (((int) partition.charAt(upFlag - 2) == 125)
								&& ((int) partition.charAt(upFlag - 1) == 10)
								&& ((int) partition.charAt(upFlag) == 123)) {
							break;
						} else {
							upFlag--;
						}
					}
					while ((downFlag < (partition.length() - 1))) {

						try {
							if (((int) partition.charAt(downFlag + 1) == 10)
									&& ((int) partition.charAt(downFlag) == 125)) {
								break;
							} else {
								downFlag++;
							}
						} catch (Exception e) {
							System.out.println(partition.length());
							System.out.println(downFlag);
						}

					}
					if (upFlag >= 0 && downFlag < (partition.length() - 1)) {
						String string = partition.substring(upFlag,
								downFlag + 1);
						// System.out.println(string);
						resultList.add(string);
					}
					scannedFlag = downFlag;
				}
			}
		}
		try {
			this.clean(in);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fc.close();
		randomAccessFile.close();
		return resultList;
	}

	/**
	 * 解除MappedByteBuffer对文件的映射，如果不解除，则文件会一直被映射持有引用，导致删除不掉，从而磁盘会爆掉
	 * 
	 * @param buffer
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void clean(final Object buffer) throws Exception {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				try {
					Method getCleanerMethod = buffer.getClass().getMethod(
							"cleaner", new Class[0]);
					getCleanerMethod.setAccessible(true);
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod
							.invoke(buffer, new Object[0]);
					cleaner.clean();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});

	}

}
