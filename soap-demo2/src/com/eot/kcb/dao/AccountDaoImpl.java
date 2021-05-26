package com.eot.kcb.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.eot.kcb.model.BankAccountMapping;


public class AccountDaoImpl implements AccountDao {
	
	private SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sessionFactory) {
		
        this.sessionFactory = sessionFactory;
    }
	public AccountDaoImpl()
	{
		Configuration configuration = new Configuration().configure();
        // 2. create sessionfactory
        this.sessionFactory = configuration.buildSessionFactory();
        // 3. Get Session object
//        Session session = sessionFactory.openSession();
	}
	
	@Override
	public BankAccountMapping getBankAccountMappingByAccountNo(String accountNumber) {
		
		Session session = this.sessionFactory.openSession();
		BankAccountMapping bankAccountMapping=null;
		List<BankAccountMapping> accountList = session.createQuery("from BankAccountMapping where accountNumber ='"+accountNumber+"'").list();
		if(!accountList.isEmpty())
			bankAccountMapping=accountList.get(0);
		session.close();
		return bankAccountMapping;
	}
	
	@Override
	public void save(BankAccountMapping bankAccountMapping) {
		Session session = this.sessionFactory.openSession();
		
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(bankAccountMapping);
		
		tx.commit();
		session.close();
		
	}
	
	@Override
	public void deLink(BankAccountMapping bankAccountMapping) {
		Session session = this.sessionFactory.openSession();
		
		Transaction tx = session.beginTransaction();
//		session.saveOrUpdate(bankAccountMapping);
		session.delete(bankAccountMapping);
		
		tx.commit();
		session.close();
		
	}

}
