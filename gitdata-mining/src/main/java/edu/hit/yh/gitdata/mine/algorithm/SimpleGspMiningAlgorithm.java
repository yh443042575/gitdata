package edu.hit.yh.gitdata.mine.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;
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
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Object... args) {

		int nowLength = 1;
		boolean algorithmEndFlag = false;
		List<Artifact<SimpleBehavior>> artifactList = this.buildArtifacts(getRepo(), getArtifactType());
		HibernateUtil.closeSessionFactory();
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
					for(int j = 0;j<behaviorSeq.size();j++){
						if(!addBehaviorPatternCount(behaviorSeq.get(j),preBehaviorPatterns)){//判断当前的行为是否已经统计过
							BehaviorPattern<SimpleBehavior> behaviorPattern = new BehaviorPattern<SimpleBehavior>();
							behaviorPattern.setBehaviorList(new ArrayList<SimpleBehavior>());
							preBehaviorPatterns.add(behaviorPattern);
							behaviorPattern.getBehaviorList().add(behaviorSeq.get(j));
							behaviorPattern.addSurpport();
						}
					}
				}
				preBehaviorPatterns = this.pruning(preBehaviorPatterns,artifactList);
			}else {/*如果不是记录长度为1的行为模式，则要把当前的候选序列存入总的result中，
					然后做连接操作，then,对得到的新的候选序列进行计数*/
				List<BehaviorPattern> tempList = new ArrayList<BehaviorPattern>(preBehaviorPatterns);
				resultBehaviorPatterns.addAll(tempList);//这里要深拷贝
				preBehaviorPatterns = new ArrayList<BehaviorPattern>();
				preBehaviorPatterns = this.joinOperation(tempList);
				preBehaviorPatterns = this.pruning(preBehaviorPatterns,artifactList);
				if(preBehaviorPatterns.size()==0){//如果找不到候选序列了，说明算法结束了
					algorithmEndFlag = true;
				}
			}
			nowLength++;
			
		}
		for(BehaviorPattern<SimpleBehavior> bp:resultBehaviorPatterns){
			List<SimpleBehavior> sList = bp.getBehaviorList();
			System.out.print("surpport="+bp.getSurpport()+" ");
			for(SimpleBehavior s :sList){
				System.out.print(s.getActor()+" "+s.getEventType()+" "+s.getTarget()+" |");
			}
			System.out.println();
		}
		//resultBehaviorPatterns.forEach(System.out::println);
		
	}
	
	
	/**
	 * 判断当前的behavior在不在list中（已完成————未测试）
	 * 扫描当前的preBehaviorPatterns，如果存在，则计数+1，返回true
	 * 如果不在preBehaviorPatterns，则直接返回true
	 * @param simpleBehavior
	 * @param preBehaviorPatterns
	 * @return
	 */
	private boolean addBehaviorPatternCount(SimpleBehavior simpleBehavior,
			List<BehaviorPattern> preBehaviorPatterns) {
		for(BehaviorPattern behaviorPattern:preBehaviorPatterns){
			List<SimpleBehavior> list = behaviorPattern.getBehaviorList();
			if(simpleBehavior.equals(list.get(0))){
				behaviorPattern.addSurpport();
				return true;
			}
		}
		return false;
	}

	/**
	 * 对当前的候选patternlist进行剪枝操作（已完成————未测试）
	 * 统计当前的patternlist中每一个behaviorPattern的支持度，不满足最小支持度的全都放弃
	 * 四层循环，略有心虚。。。。。>_<
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	@Override
	public List<BehaviorPattern> pruning(List<BehaviorPattern> patternlist,Object artifacts) {
		//算法所能容忍的最小支持度
		int surpport = getSurpport();
		//算法执行时所需要的原始数据
		List<Artifact<SimpleBehavior>> artifactList = (List<Artifact<SimpleBehavior>>) artifacts;
		//经过剪枝所得到的最后的结果，作为下次运算的候选序列
		List<BehaviorPattern> preBehaviorPatterns = new ArrayList<BehaviorPattern>(); 
		
		for(BehaviorPattern<SimpleBehavior> behaviorPattern:patternlist){//待验证的每一个patternList
			for(Artifact<SimpleBehavior> artifact:artifactList){//验证的总artifact集合
				int point = 0;
				List<SimpleBehavior> behaviorSeq = artifact.getBehaviorSeq();
				for(SimpleBehavior preSimpleBehavior:behaviorPattern.getBehaviorList()){
					int num = behaviorPattern.getBehaviorList().indexOf(preSimpleBehavior);
					//如果还有搜索下去的必要
					if((behaviorSeq.size()-point)>=
							(behaviorPattern.getBehaviorList().size()-num)){
						for(;point<behaviorSeq.size();point++){
							if(preSimpleBehavior.equals(behaviorSeq.get(point))){
								if(num!=behaviorPattern.getBehaviorList().size()-1){//如果还没有检查完当前behaviorPattern则继续
									break;
								}else{//如果扫描成功，则当前的behaviorPattern支持度+1
									behaviorPattern.addSurpport();
								}
							}
						}
					}else{
						break;
					}
					
				}
			}
		}
		
		//List<BehaviorPattern> list = patternlist.stream().filter(o->o.getSurpport()>=this.getSurpport());
		List<BehaviorPattern> list =  new ArrayList<BehaviorPattern>();
		for(BehaviorPattern<SimpleBehavior> behaviorPattern:patternlist){
			if(behaviorPattern.getSurpport()>=this.getSurpport()){
				list.add(behaviorPattern);
			}
		}
		return 	list;
	}

	/**
	 * 对现有的pattern做连接操作（已完成——未测试）
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public  List<BehaviorPattern> joinOperation(List<BehaviorPattern> patternlist) {
		List<BehaviorPattern> tempList = new ArrayList<BehaviorPattern>(patternlist);
		List<BehaviorPattern> resultList = new ArrayList<BehaviorPattern>();
		Collections.copy(tempList, patternlist);
		System.out.println("进行连接操作...");
		for(BehaviorPattern behaviorPattern1:tempList){
			for(BehaviorPattern behaviorPattern2:patternlist){
				if(tempList.indexOf(behaviorPattern1)!=patternlist.indexOf(behaviorPattern2)&&isAbleToJoin(behaviorPattern1, behaviorPattern2)){//如果两个行为模式是可以join的
					List<SimpleBehavior> simpleBehaviorList1 =  behaviorPattern1.getBehaviorList();
					List<SimpleBehavior> simpleBehaviorList2 =  behaviorPattern2.getBehaviorList();
					List<SimpleBehavior> joinList = new ArrayList<SimpleBehavior>(simpleBehaviorList1);
					Collections.copy(joinList, simpleBehaviorList1);
					joinList.add(simpleBehaviorList2.get(simpleBehaviorList2.size()-1));
					List<SimpleBehavior> simpleBehaviorList3 = joinList;  
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
	 * 判断两个pattern能不能一起join（已完成——未测试）
	 * @param behaviorPattern1
	 * @param behaviorPattern2
	 * @return
	 */
	private boolean isAbleToJoin(BehaviorPattern behaviorPattern1, BehaviorPattern behaviorPattern2) {
		List<SimpleBehavior> simpleBehaviorList1 =  behaviorPattern1.getBehaviorList();
		List<SimpleBehavior> simpleBehaviorList2 =  behaviorPattern2.getBehaviorList();
		
		if(simpleBehaviorList1.size()==1){
			if(simpleBehaviorList1.get(0).equals(simpleBehaviorList2.get(0))&&//若list长度等于1 并且，二者并不是同时的同一个行为,可以连接
					simpleBehaviorList1.get(0).getCreatedAt().equals(
							simpleBehaviorList2.get(0).getCreatedAt())){
				return false;
			}else{
				return true;
			}
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

		return artifactList;
	}
	
	public static void main(String args[]){
		
		SimpleGspMiningAlgorithm simpleGspMiningAlgorithm = new SimpleGspMiningAlgorithm(20);
		simpleGspMiningAlgorithm.setRepo("golang/go/");
		simpleGspMiningAlgorithm.setArtifactType("Issue");
		simpleGspMiningAlgorithm.execute(null);
	}
	
}
