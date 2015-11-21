package edu.hit.yh.gitdata.githubDataModel;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * 辅助类，单例模式的SessionFactory
 * @author DHAO
 *
 */
public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildFactory();	

	private static SessionFactory buildFactory(){
		
		try {
			
			return new AnnotationConfiguration().configure().buildSessionFactory();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExceptionInInitializerError();
		}
		
	}
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	public static void closeSessionFactory(){
		sessionFactory.close();	
	}

}

