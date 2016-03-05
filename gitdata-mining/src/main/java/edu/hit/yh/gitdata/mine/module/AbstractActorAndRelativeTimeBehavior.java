package edu.hit.yh.gitdata.mine.module;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 基于相对时间与抽象用户的挖掘基本元素（行为）
 * @author DHAO
 *
 */
@Data
public class AbstractActorAndRelativeTimeBehavior {
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
			
			//该动作与上一个动作之间相差的相对时间
			private String relativeTime;
			
			@Override
			public boolean equals(Object obj) {
				
				if(obj instanceof SimpleBehavior){
					SimpleBehavior simpleBehavior = (SimpleBehavior)obj;
					if(this.eventType.equals(simpleBehavior.getEventType())){
						return true;
					}
				}else if(obj instanceof AbstractActorAndRelativeTimeBehavior) {
					AbstractActorAndRelativeTimeBehavior abstractActorAndRelativeTimeBehavior = (AbstractActorAndRelativeTimeBehavior)obj;
					if(this.eventType.equals(abstractActorAndRelativeTimeBehavior.getEventType())){
						return true;
					}
				}
				
				return false;
			}
}
