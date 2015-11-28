package edu.hit.yh.gitdata.mine.module;

import java.util.List;

import lombok.Data;

/**
 * artifact类，其中包含的是一个artifact下的所有人的行为以及针对的target
 * @author DHAO
 *
 */
@Data
public class Artifact<T> {

	/**
	 * 某个repo下面的artifactID,因为issue和pull可以互相转
	 */
	String ArtifactId;
	
	/**
	 * 此artifact的所有动作发起者
	 */
	List<String> actors ;
	
	/**
	 * 此artifact下的所有动作接收者
	 */
	List<String> targets ;
	
	/**
	 * artifact下所有动作的序列
	 */
	List<T> behaviorSeq ;
	
}
