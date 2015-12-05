package edu.hit.yh.gitdata.mine.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.BehaviorPattern;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;
import edu.hit.yh.gitdata.mine.util.ArtifactUtil;

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

	/** 
	 * 算法的执行模块
	 *  
	 * 简单的GSP执行，扫描数据集合->筛选出长度为K的序列模式->连接长度为K的序列模式生成K+1的候选序列模式->再次扫描数据库->...
	 */ 
	@Override
	public void execute(Object... args) {

		int nowLength = 1;
		boolean algorithmEndFlag = false;
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
		while(!algorithmEndFlag){//如果候选的序列中还有可以连接的项，则继续进行算法
			
			if(nowLength==1){//如果是第一次来扫描全表的话，因为BehaviorPatternList没有数据,这时需要扫描全ArtifactList
				
				for(int i = 0;i<artifactList.size();i++){//对Artifact中的每一个behaviorSeq
					
					List<SimpleBehavior> behaviorSeq = artifactList.get(i).getBehaviorSeq();
					for(int j = 0;i<behaviorSeq.size();j++){
						if(!addBehaviorPatternCount(behaviorSeq.get(j),preBehaviorPatterns)){//判断当前的行为是否已经统计过
							BehaviorPattern<SimpleBehavior> behaviorPattern = new BehaviorPattern<SimpleBehavior>();
							preBehaviorPatterns.add(behaviorPattern);
							behaviorPattern.getBehaviorList().add(behaviorSeq.get(j));
							behaviorPattern.addSurpport();
						}
					}
				}
				
			}else {/*如果不是记录长度为1的行为模式，则要把当前的候选序列存入总的result中，
					然后做连接操作，then,对得到的新的候选序列进行计数*/
				resultBehaviorPatterns.addAll(preBehaviorPatterns);//这里要深拷贝
				preBehaviorPatterns = this.joinOperation(preBehaviorPatterns);
				preBehaviorPatterns = this.pruning(preBehaviorPatterns);
			}
		}
	}
	
	
	/**
	 * 判断当前的behavior在不在list中（未完成）
	 * 扫描当前的preBehaviorPatterns，如果存在，则计数+1，返回true
	 * 如果不在preBehaviorPatterns，则直接返回true
	 * @param simpleBehavior
	 * @param preBehaviorPatterns
	 * @return
	 */
	private boolean addBehaviorPatternCount(SimpleBehavior simpleBehavior,
			List<BehaviorPattern> preBehaviorPatterns) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 对当前的候选patternlist进行剪枝操作（未完成）
	 */
	@Override
	public List<BehaviorPattern> pruning(List<BehaviorPattern> patternlist) {
		
		if (patternlist.size() == 0) {// 如果初始时pattern个数为0，则扫描artifactList，只要大于支持度的项全取出来
			
		} else {// 同样扫描数据库，通过时间可以位移的方法来判断我们序列模式的计数

		}
		
		return null;
	}

	/**
	 * 对现有的pattern做连接操作（未完成）
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public  List<BehaviorPattern> joinOperation(List<BehaviorPattern> patternlist) {
		List<BehaviorPattern> tempList = new ArrayList<BehaviorPattern>(patternlist);
		List<BehaviorPattern> resultList = new ArrayList<BehaviorPattern>();
		Collections.copy(tempList, patternlist);
		
		for(BehaviorPattern behaviorPattern1:tempList){
			for(BehaviorPattern behaviorPattern2:patternlist){
				if(isAbleToJoin(behaviorPattern1,behaviorPattern2)){//如果两个行为模式是可以join的
					List<SimpleBehavior> simpleBehaviorList1 =  behaviorPattern1.getBehaviorList();
					List<SimpleBehavior> simpleBehaviorList2 =  behaviorPattern2.getBehaviorList();
					simpleBehaviorList1.add(simpleBehaviorList2.get(simpleBehaviorList2.size()-1));
					List<SimpleBehavior> simpleBehaviorList3 = simpleBehaviorList1;  
					BehaviorPattern behaviorPattern = new BehaviorPattern<SimpleBehavior>();
					behaviorPattern.setBehaviorList(simpleBehaviorList3);
					behaviorPattern.setSurpport(0);
					resultList.add(behaviorPattern);
				}
			}
		}
		return resultList;
	}
	/**
	 * 判断两个pattern能不能一起join
	 * @param behaviorPattern1
	 * @param behaviorPattern2
	 * @return
	 */
	private boolean isAbleToJoin(BehaviorPattern behaviorPattern1, BehaviorPattern behaviorPattern2) {
		List<SimpleBehavior> simpleBehaviorList1 =  behaviorPattern1.getBehaviorList();
		List<SimpleBehavior> simpleBehaviorList2 =  behaviorPattern2.getBehaviorList();
		
		if(simpleBehaviorList1.size()==1){
			return true;
		}
		
		for(int i=1;i<simpleBehaviorList1.size();i++){
			if(!simpleBehaviorList1.get(i).equals(simpleBehaviorList2.get(i-1))){
				return false;
			}
		}
		return true;
	}

	/**
	 * 从数据库中取出指定的repo下的某个类型的artifact，这个list是原始的数据，所有的统计支持度都是从这里下手（已完成-未测试）
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
	
	public static void main(String args[]){
		
		List<String> list1 = new ArrayList<String>();
		
		list1.add("呵呵");
		List<String> list2 = new ArrayList<String>(list1);
		Collections.copy(list2, list1);
		list1.remove(0);
		
		System.out.println(list1.size());
		System.out.println(list2.size());
	
	}

}
