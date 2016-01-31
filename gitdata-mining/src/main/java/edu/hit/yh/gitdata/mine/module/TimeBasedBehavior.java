package edu.hit.yh.gitdata.mine.module;

import lombok.Data;

/**
 * 基于相对时间的行为序列
 * @author DHAO
 *
 */
@Data
public class TimeBasedBehavior {

		//动作发起者
		private String actor;
		
		//动作接收者
		private String target;
		
		//动作类型
		private String eventType;
		
		//具体的操作
		private String action;
		
		//行为发起时间
		private String createdAt;
	
		//较上一次行为的相对时间
		private String relativeTime;
		
}
