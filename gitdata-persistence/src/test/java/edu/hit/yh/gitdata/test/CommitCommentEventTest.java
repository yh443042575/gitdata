package edu.hit.yh.gitdata.test;

import org.hibernate.Session;

import edu.hit.yh.gitdata.githubDataModel.HibernateUtil;


public class CommitCommentEventTest {

	public static void main(String[] args) {
		
		
		/*利用静态类获取数据的session*/
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		//session.save(createEvent);
		session.getTransaction().commit();
		session.close();
		HibernateUtil.closeSessionFactory();
		
		
		/*AnnotationConfiguration cfg = new AnnotationConfiguration();
		SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.create(true,true);*/
		
		
	}

}
