package edu.hit.yh.gitdata.mine.algorithm;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import edu.hit.yh.gitdata.mine.module.Artifact;
import edu.hit.yh.gitdata.mine.module.SimpleBehavior;

/**
 * 算法的一个抽象方法，其中T为行为模式
 * @author DHAO
 *
 * @param <P> 行为模式，里面包含了一组行为的list
 */
public abstract class AbstractGspMiningAlgorithm<P> {

	//算法的支持度
	@Getter
	@Setter
	private int surpport;
	
	//算法所要挖掘的reposiroty
	@Getter
	@Setter
	private String repo;
	
	//算法所要挖掘的artifactType
	@Getter
	@Setter
	private String artifactType;
	
	// 算法的总执行
	public abstract void execute(Object... args);

	public abstract<T> List<Artifact<T>> buildArtifacts(String repo,String ArtifactType);
	
	//算法的剪枝操作
	public abstract List<P> pruning(List<P> patternlist);
	
	//连接操作
	public abstract List<P> joinOperation(List<P> patternlist);


	

}
