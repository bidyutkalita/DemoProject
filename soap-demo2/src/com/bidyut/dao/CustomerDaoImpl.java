package com.bidyut.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.bidyut.model.ApiCreadentials;
import com.bidyut.model.ApiLogs;
import com.bidyut.model.Customer;



public class CustomerDaoImpl implements CustomerDao {
	
	private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public CustomerDaoImpl()
    {
    	Configuration configuration = new Configuration().configure();
        // 2. create sessionfactory
        this.sessionFactory = configuration.buildSessionFactory();
        // 3. Get Session object
//        Session session = sessionFactory.openSession();
    }
    
	@Override
	public void save(Customer p) {
		
		// 1. configuring hibernate
        Configuration configuration = new Configuration().configure();

        // 2. create sessionfactory
        SessionFactory sessionFactory = configuration.buildSessionFactory();

        // 3. Get Session object
        Session session = sessionFactory.openSession();
		
		
//		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(p);
		
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> list() {
		Session session = this.sessionFactory.openSession();
		List<Customer> personList = session.createQuery("from CustomerModel").list();
		session.close();
		return personList;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Customer getCustomerByCode(String code) {
		Session session = this.sessionFactory.openSession();
		List<Customer> personList = session.createQuery("from Customer where code="+code).list();
		session.close();
		if(!personList.isEmpty())
			return personList.get(0);
		else return null;
	}
	
	@Override
	public ApiCreadentials validateRequest(String username, String password) {

		
		Configuration configuration = new Configuration().configure();

        // 2. create sessionfactory
        SessionFactory sessionFactory = configuration.buildSessionFactory();

        // 3. Get Session object
        Session session = sessionFactory.openSession();
        
        
//		Session session = this.sessionFactory.openSession();
		List<ApiCreadentials> credList = session.createQuery("from ApiCreadentials where username='"+username +"' and password ='"+password+"'").list();
		session.close();
		if(!credList.isEmpty())
			return credList.get(0);
		else return null;
	}
	
	@Override
	public ApiLogs checkDuplicate(String transactionId) {
		Session session = this.sessionFactory.openSession();
		List<ApiLogs> list = session.createQuery("from ApiLogs where transactionId='"+transactionId +"'").list();
		session.close();
		if(!list.isEmpty())
			return list.get(0);
		else return null;
	}

	@Override
	public void saveApiLogs(ApiLogs p) {
		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(p);
		
		tx.commit();
		session.close();
	}
	
	@Override
	public ApiLogs loadApiLogs(String transactionId) {
		Session session = this.sessionFactory.openSession();
		List<ApiLogs> list = session.createQuery("from ApiLogs where transactionId='"+transactionId +"'").list();
		session.close();
		if(!list.isEmpty())
			return list.get(0);
		else return null;
	}
	
	
}
