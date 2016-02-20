package edu.hit.yh.gitdata.mine.module;

/**
 * 抽象用户的基本行为，这里用户不再用具体的人名组成，取而代之的为数字 
 * @author DHAO
 *
 */
public class AbstractActorBehavior {

	
		//动作发起者
		private Integer actor;
		
		//动作接收者
		private Integer target;
		
		//动作类型
		private String eventType;
		
		//具体的操作
		private String action;
		
		//行为发起时间
		private String createdAt;
	
}
