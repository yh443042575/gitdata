package edu.hit.yh.gitdata.mine.module;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 初始挖掘数据
 * @author DHAO
 *
 * @param <T>
 */
@Data
public class SimpleBehavior {

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
	

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SimpleBehavior)){
			return false;
		}
		SimpleBehavior simpleBehavior = (SimpleBehavior)obj;
		if(this.actor.equals(simpleBehavior.getActor())
				&&(this.target.equals(simpleBehavior.getTarget())||(this.target==null&&simpleBehavior.getTarget()==null))
				&&this.eventType.equals(simpleBehavior.getEventType())){
			return true;
		}
		return false;
	}
}
