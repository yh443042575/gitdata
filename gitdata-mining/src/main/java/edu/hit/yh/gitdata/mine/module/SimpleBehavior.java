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
	
	
}
