package edu.hit.yh.gitdata.githubDataDownload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;



/**
 * 负责
 * 
 * @author DHAO
 *
 */
public class DataDownloader {

	private String ip;

	private int port;

	public DataDownloader(String ip, int port) throws IOException {
		super();
		this.ip = ip;
		this.port = port;

	}

	/**
	 * 根据给定日期与下载量开启文件下载
	 * 
	 * @param path
	 * @param date
	 * @param days
	 * @throws ParseException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void download(String path, String date, int days, int hour)
			throws ParseException, IOException, InterruptedException {
		int failedTime=0;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(simpleDateFormat.parse(date));
		for (int i = 0; i < days; i++) {
			date = simpleDateFormat.format(calendar.getTime());
			/**
			 * 开启24小时下载
			 */
			for (; hour < 24; hour++) {
				
				String fileName = date + "-" + hour + ".json.gz";
				System.out.println("正在下载 " + fileName + " 文件，请稍等...");
				// File file = new File(path + "/" + fileName);
				RandomAccessFile randomAccessFile = new RandomAccessFile(path
						+ "/" + fileName, "rw");
				FileOutputStream fileOutputStream = new FileOutputStream(
						randomAccessFile.getFD());
				FileChannel fileChannel = fileOutputStream.getChannel();
				/**
				 * 发送请求
				 */
				HttpClient httpClient = new HttpClient();
				httpClient.getHttpConnectionManager().getParams()
						.setSoTimeout(30000);
				httpClient.getHostConfiguration().setProxy("127.0.0.1", 8087);
				try {
					GetMethod method = new GetMethod(
							"http://data.githubarchive.org/" + fileName);
					httpClient.executeMethod(method);
					byte dst[] = method.getResponseBody();
					ByteBuffer buffer = ByteBuffer.wrap(dst);
					fileChannel.write(buffer);
					long time5 = System.currentTimeMillis();
				} catch (java.net.SocketTimeoutException e) {
					// TODO: handle exception
					fileOutputStream.close();
					fileChannel.close();
					System.out.println("超时重传");
					this.download(path, date, days - i, hour);
				}
				if (randomAccessFile.length() < 100 * 1024&&failedTime<3) {
					hour--;
					fileOutputStream.close();
					fileChannel.close();
					System.out.println("下载可能失败..重试中");
					failedTime++;
				} else if(randomAccessFile.length() < 100 * 1024&&failedTime>2) {
					
					fileOutputStream.close();
					fileChannel.close();
					System.out.println("下载成功");
					failedTime = 0;
				}else {
					fileOutputStream.close();
					fileChannel.close();
					System.out.println("下载成功！");
					Thread.sleep(1000);
					failedTime = 0;
				}
			}
			calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400 * 1000);
			hour = 0;
		}

	}

}
