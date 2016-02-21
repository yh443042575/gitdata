package edu.hit.yh.gitdata.mine.module;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 抽象用户的基本行为，这里用户不再用具体的人名组成，取而代之的为数字 
 * @author DHAO
 *
 */
@Data
public class AbstractActorBehavior {

	
		//动作发起者
		private Integer actor;
		
		//动作接收者
		private List<Integer> target = new ArrayList<Integer>();
		
		//动作类型
		private String eventType;
		
		//具体的操作
		private String action;
		
		//行为发起时间
		private String createdAt;
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof SimpleBehavior)){
				return false;
			}
			SimpleBehavior simpleBehavior = (SimpleBehavior)obj;
			if(this.eventType.equals(simpleBehavior.getEventType())
					&&this.action.equals(simpleBehavior.getAction())){
				return true;
			}
			return false;
		}
	
}
