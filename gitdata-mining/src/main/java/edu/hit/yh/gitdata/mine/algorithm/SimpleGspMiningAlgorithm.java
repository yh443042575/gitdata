package edu.hit.yh.gitdata.mine.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.BehaviorPattern;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;
import edu.hit.yh.gitdata.mine.util.ArtifactUtil;
import edu.hit.yh.gitdata.mine.util.ArtifactUtilTest;

public class SimpleGspMiningAlgorithm extends
		AbstractGspMiningAlgorithm<BehaviorPattern> {

	/**
	 * 初始化算法类，输入序列的最小支持度
	 * 
	 * @param surpport
	 */
	public SimpleGspMiningAlgorithm(int surpport) {
		super.setSurpport(surpport);
	}

	@Override
	public void execute(Object... args) {
		// TODO
		// 简单的GSP执行，扫描数据库->筛选出长度为K的序列模式->连接长度为K的序列模式生成K+1的候选序列模式->再次扫描数据库->...
		List<List<BehaviorPattern<SimpleBehavior>>> rawDataList = this
				.getRawData();

	}

	@Override
	public List<BehaviorPattern> pruning(List<BehaviorPattern> patternlist) {
		// TODO 传入长度为K的候选序列模式，筛选出支持度满足支持度大于等于K的模式，作为序列模式
		if (patternlist.size() == 0) {// 如果初始时pattern个数为0，则扫描数据库，只要大于支持度的项全取出来

		} else {// 同样扫描数据库，通过时间可以位移的方法来判断我们序列模式的计数

		}

		return null;
	}

	@Override
	public List<BehaviorPattern> joinOperation(List<BehaviorPattern> patternlist) {
		// TODO 对现有的pattern做连接操作
		return null;
	}

	private List<List<BehaviorPattern<SimpleBehavior>>> getRawData() {
		if (getArtifactType().isEmpty() || getProgramName().isEmpty()) {
			return null;
		}
		List<List<BehaviorPattern<SimpleBehavior>>> rawDataList = new ArrayList<List<BehaviorPattern<SimpleBehavior>>>();
		// TODO 从数据库中取出某个repo中的某类artifactType,并装载到list中

		return rawDataList;

	}

	/**
	 * 从数据库中取出指定的repo下的某个类型的artifact，这个list是原始的数据，所有的统计支持度都是从这里下手
	 * artifactType 分为：
	 * 1、issue
	 * 2、pullrequest
	 * 3、commit
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Artifact<SimpleBehavior>> buildArtifacts(String repo,
			String ArtifactType) {
        
		List<Artifact<SimpleBehavior>> artifactList = null;
		Class clazz = ArtifactUtil.class;
		Method method = null;
		try {
			method = clazz.getMethod("get" + ArtifactType + "SimpleBehavior",
					String.class);
			artifactList = (List<Artifact<SimpleBehavior>>)method.invoke(clazz.newInstance(), repo);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return ArtifactUtil.getIssueSimpleBehavior(repo);
	}

}
