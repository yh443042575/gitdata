package edu.hit.yh.gitdata.mine.module;

import com.mysql.jdbc.StringUtils;

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
		
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TimeBasedBehavior)){
				return false;
			}
			TimeBasedBehavior timeBasedBehavior = (TimeBasedBehavior)obj;
			if(this.actor.equals(timeBasedBehavior.getActor())
					/*&&(isSameTarget(this.target, timeBasedBehavior.getTarget()))*/
					&&this.eventType.equals(timeBasedBehavior.getEventType())
					&&this.relativeTime.equals(timeBasedBehavior.getRelativeTime())){
				return true;
			}
			return false;
		}
		
		/**
		 * 与SimpleBehavior比较，只查看动作发起者，动作和接收者是否为一样
		 * @param obj
		 * @return
		 */
		public boolean simplEquals(Object obj) {
			
			if(obj instanceof SimpleBehavior){
				SimpleBehavior simpleBehavior = (SimpleBehavior)obj;
				if(this.actor.equals(simpleBehavior.getActor())
						/*&&(isSameTarget(this.target, simpleBehavior.getTarget()))*/
						&&this.eventType.equals(simpleBehavior.getEventType())){
					return true;
				}
			}else if(obj instanceof TimeBasedBehavior) {
				TimeBasedBehavior timeBasedBehavior = (TimeBasedBehavior)obj;
				if(this.actor.equals(timeBasedBehavior.getActor())
						/*&&(isSameTarget(this.target, simpleBehavior.getTarget()))*/
						&&this.eventType.equals(timeBasedBehavior.getEventType())){
					return true;
				}
			}
			return false;
		}
		
		private boolean isSameTarget(String tar1,String tar2){
			
			if(StringUtils.isNullOrEmpty(tar1)&&StringUtils.isNullOrEmpty(tar2)){
				return true;
			}else if(StringUtils.isNullOrEmpty(tar1)&&StringUtils.isNullOrEmpty(tar2)){
				tar1.replace("@", "");
				tar1.replace("+", "");
				tar2.replace("@", "");
				tar2.replace("+", "");
				String []t1 = tar1.split(" ");
				String []t2 = tar2.split(" ");
				boolean flag = false;
				for(String t:t1){
					for(String m:t2){
						flag = false;
						if(t.equals(m)){
							flag = true;
							break;
						}
						if(!flag){
							return false;
						}
					}
				}
			}
			
			return true;
		}
		
		
		
}
