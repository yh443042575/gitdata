package edu.hit.yh.gitdata.mine.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.mysql.jdbc.StringUtils;

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
				&&(isSameTarget(this.target, simpleBehavior.getTarget()))
				&&this.eventType.equals(simpleBehavior.getEventType())){
			return true;
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

	
	
	
	
	public static void main(String args[]) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = "2013-11-16T22:25:58Z";
		String time2= "2013-11-16T22:25:58Z";
		time = time.replaceAll("[T-Z]", " ");
		time2 = time2.replaceAll("[T-Z]", " ");
		
		Date date = sdf.parse(time);
		Date date2 = sdf.parse(time2);
		
		System.out.println(date2.after(date));
		
	}

	
}
