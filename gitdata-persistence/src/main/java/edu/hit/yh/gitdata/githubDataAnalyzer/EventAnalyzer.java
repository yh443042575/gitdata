package edu.hit.yh.gitdata.githubDataAnalyzer;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.Session;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class EventAnalyzer {

	private JsonParser jsonParser;
	private JsonObject jsonObject;
	
	public EventAnalyzer(){
		jsonParser = new JsonParser();
	}
	
	//分析json字符串，将对应的json解析成object，并利用反射调用DataPersistence类，将Event事件持久化
	public void analyzeJson(String eventJson,Session session){	
		
		try {
			jsonObject = (JsonObject)jsonParser.parse(eventJson);
			String type = jsonObject.get("type").getAsString();
			if(!type.equals("GistEvent")&&!type.equals("GollumEvent")&&!type.equals("PublicEvent")&&!type.equals("DownloadEvent")){
			Class<?> event = Class.forName("edu.hit.yh.gitdata.githubDataPersistence.DataPersistence");
			Method method = event.getMethod("construct"+type, JsonObject.class);
			//System.out.println("construct"+type);
			session.save(method.invoke(event.newInstance(), jsonObject));
			}

		} catch (ClassNotFoundException e) {
			System.out.println("反射建立Event类出错");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println("EventAnalyzer..找不到对应的方法");
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("EventAnalyzer..调用方法不正确");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			System.out.println(eventJson);
		}
		
	}
	
	
	
	
}
