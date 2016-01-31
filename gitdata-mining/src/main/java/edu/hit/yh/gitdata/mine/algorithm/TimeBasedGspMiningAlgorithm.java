package edu.hit.yh.gitdata.mine.algorithm;

import java.util.ArrayList;
import java.util.List;

import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.BehaviorPattern;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;


/**
 * 1、对长度为1的项进行计数，设初始的相对时间为0；
 * 2、对长度为K的项进行连接操作，得到长度为K+1的候选集项，但是这是基于相对时间的序列，得把相对时间考虑进去，那就是说在生成候选集时，需要再回到artifactlist中考察，得到靠谱的候选项
 * 连接操作的步骤：（1）、比较第一个连接项除去头和第二个连接项去掉尾中间的项是否相同，如果相同则将两项连接
 * 3、对连接项进行支持度计数，计数时，可以把计数项的任意一点作为初始点来进行记录（在每一个统计artifact中附带一个hashmap，负责记录这个项里包不包含我们所有的项）
 * @author DHAO
 *
 */
public class TimeBasedGspMiningAlgorithm extends AbstractGspMiningAlgorithm<BehaviorPattern> {

	
	/**
	 * 初始化算法类，输入序列的最小支持度
	 * 
	 * @param surpport
	 */
	public TimeBasedGspMiningAlgorithm(int surpport) {
		super.setSurpport(surpport);
	}
	
	
	/** 
	 * 算法的执行模块
	 *  
	 * 基于相对时间的的GSP执行，扫描数据集合->筛选出长度为K的序列模式->连接长度为K的序列模式生成K+1的候选序列模式->再次扫描数据库->...
	 */ 
	@Override
	public void execute(Object... args) {
		
		int nowLength = 1;
		boolean  algorithmEndFlag = false;
		List<Artifact<SimpleBehavior>> artifactList = this.buildArtifacts(getRepo(), getArtifactType());
		List<BehaviorPattern> preBehaviorPatterns = new ArrayList<BehaviorPattern>();
		List<BehaviorPattern> resultBehaviorPatterns  = new ArrayList<BehaviorPattern>();
		/**
		 * 如果当前的候选序列中还有behavior则算法继续进行
		 * 1、对当前的候选序列，在artifactList中进行扫描，计数
		 * 2、筛选掉surpport小于最小支持度的BehaviorPattern
		 * 3、留下的list继续做连接操作，进而生成新的候选BehaviorPatternList，而上一个候选BehaviorPatternList，则加入到result中
		 * 4、对第二个序列进行1的操作
		 */
		while(!algorithmEndFlag){
			
		}
		
		
		
	}

	@Override
	public <T> List<Artifact<T>> buildArtifacts(String repo, String ArtifactType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BehaviorPattern> joinOperation(List<BehaviorPattern> patternlist) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BehaviorPattern> pruning(List<BehaviorPattern> patternlist,
			Object artifacts) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
