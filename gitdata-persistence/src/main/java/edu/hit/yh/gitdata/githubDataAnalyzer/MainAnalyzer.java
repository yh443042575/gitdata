package edu.hit.yh.gitdata.githubDataAnalyzer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hibernate.Session;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
import edu.hit.yh.gitdata.githubDataPersistence.DataCutter;

/**
 * 解压文件，找到我们想要的相关项目的数据， 并调用持久化函数将相对应的event持久化到数据库中
 * 
 * @author DHAO
 *
 */
public class MainAnalyzer {

	public void startAnalyze() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(2);
		ExecutorService exec = Executors.newFixedThreadPool(2);
		Session session1 = HibernateUtil.getSessionFactory().openSession();
		Session session2 = HibernateUtil.getSessionFactory().openSession();
		UnzipAndDispatcher unzipAndDispatcher1 = new UnzipAndDispatcher(
				"D://githubRawData", "2014-02-19-11", "2014-05-03-5",
				countDownLatch, session1);
		UnzipAndDispatcher unzipAndDispatcher2 = new UnzipAndDispatcher(
				"D://githubRawData", "2013-06-27-6", "2013-12-31-23",
				countDownLatch, session2);
		long time = System.currentTimeMillis();
		exec.execute(unzipAndDispatcher1);
		exec.execute(unzipAndDispatcher2);
		countDownLatch.await();
		HibernateUtil.closeSessionFactory();
		exec.shutdown();
		System.out.println("存储用时： " + (System.currentTimeMillis() - time));
		// unzipAndDispatcher.run();

	}

}

/**
 * 负责解压文件，并且抽取我们想要的json，调度给相对的eventAnalyzer去解析
 * 
 * @author DHAO
 *
 */
class UnzipAndDispatcher implements Runnable {
	/* 文件路径 */
	private String filePath;
	/* 扫描初始日期 */
	private String startDate;
	/* 扫描结束日期 */
	private String stopDate;

	private CountDownLatch countDownLatch;

	private Session session;

	public UnzipAndDispatcher(String filePath, String startDate,
			String stopDate, CountDownLatch countDownLatch, Session session) {
		this.startDate = startDate;
		this.stopDate = stopDate;
		this.filePath = filePath;
		this.countDownLatch = countDownLatch;
		this.session = session;
	}

	private EventAnalyzer eventAnalyzer = new EventAnalyzer();

	/* 某一个压缩包的文件还未分析完则为true，分析完了则设置为false */
	private boolean flag = true;

	public void run() {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
		/* 将calendar设置为扫描截止时间 */
		try {
			calendar.setTime(sdf.parse(stopDate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/* 保留截止时间，用于while循环比较 */
		long stopDateMillis = calendar.getTimeInMillis();
		/* 将calendar设置为扫描开始时间 */
		try {
			calendar.setTime(sdf.parse(startDate));
		} catch (ParseException e) {
			System.out.println("日期转换问题...");
			e.printStackTrace();
		}
		/* 根据文件路径 */

		while (flag) {
			String file = sdf.format(calendar.getTime());
			if (file.charAt(file.length() - 2) == '0') {
				file = file.substring(0, file.length() - 2)
						+ file.charAt(file.length() - 1);
			}
			file = filePath + "/" + file;
			/* 解压文件 */
			UnzipTool.doUncompressFile(file + ".json.gz");
			/* 对解压后的文件进行映射 */
			File file2 = new File(file + ".json");
			if (file2.exists()) {
				System.out.println("开始解析"+file2.getName());
				DataCutter dataCutter = new DataCutter(file2);
				/* 切割原始数据，获得跟项目有关的json */
				List<String> jsonData;
				try {

					String repo = "jquery/jquery";

					jsonData = dataCutter.extractJsonData(repo);

					/* 循环解析json，将json数据进行持久化 */
					synchronized (session) {
						session.beginTransaction();
						for (String json : jsonData) {
							eventAnalyzer.analyzeJson(json, session, repo);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						session.getTransaction().commit();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println("解析成功");
			}
			if (calendar.getTimeInMillis() < stopDateMillis) {
				/* 如果没到指定日期，则继续分析下一个小时的数据 */
				calendar.setTimeInMillis(calendar.getTimeInMillis() + 3600 * 1000);
			} else {
				flag = false;
			}
			if (file2.exists()) {
				try {
					java.nio.file.Files.delete(file2.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("很奇怪，文件不存在");
			}
		}
		this.setFlag(false);
		System.out.println(" end of the " + Thread.currentThread().getName());
		countDownLatch.countDown();
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStopDate() {
		return stopDate;
	}

	public void setStopDate(String stopDate) {
		this.stopDate = stopDate;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
