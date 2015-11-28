package edu.hit.yh.gitdata.mine.algorithm;

import java.util.List;



import edu.hit.yh.gitdata.mine.module.Artifact;
import lombok.Getter;
import lombok.Setter;

/**
 * 算法的一个抽象方法，其中T为行为模式
 * @author DHAO
 *
 * @param <P> 行为模式，里面包含了一组行为的list
 */
public abstract class AbstractGspMiningAlgorithm<P> {

	//算法的支持度
	private int surpport;
	
	//算法所要挖掘的reposiroty
	private String programName;
	
	//算法所要挖掘的artifactType
	private String artifactType;
	
	// 算法的总执行
	public abstract void execute(Object... args);

	public abstract List<Artifact> buildArtifacts(String repo,String ArtifactType); 
	
	//算法的剪枝操作
	public abstract List<P> pruning(List<P> patternlist);
	
	//连接操作
	public abstract List<P> joinOperation(List<P> patternlist);

	public int getSurpport() {
		return surpport;
	}

	public void setSurpport(int surpport) {
		this.surpport = surpport;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getArtifactType() {
		return artifactType;
	}

	public void setArtifactType(String artifactType) {
		this.artifactType = artifactType;
	}
	
	
	

}
