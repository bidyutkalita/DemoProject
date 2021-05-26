package com.bidyut.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bidyut.common.Constants;
import com.eot.entity.Account;
import com.eot.entity.BusinessPartner;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.ExternalTransaction;

@Repository("externalTransactionDao")
public class ExternalTransactionDaoImpl implements ExternalTransactionDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
//	@Transactional
	public Customer getCustomerByMobileNumber(String mobileNumber) {

		Query query = sessionFactory.getCurrentSession()
				.createQuery("from Customer cs where concat(cs.country.isdCode,mobileNumber)=:mobileNumber")
				.setParameter("mobileNumber", mobileNumber);
		List<Customer> list = query.list();
		return list.size() > 0 ? list.get(0) : null;
	}
	@Override
//	@Transactional
	public CustomerAccount getCustomerAccountWithCustomerId(Long customerId) {
		Query query=sessionFactory.getCurrentSession().createQuery("from CustomerAccount customerAccount where customerAccount.customer.customerId =:customerId")
				.setParameter("customerId",customerId);
		List<CustomerAccount> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}
	
	
	@Override
	public BusinessPartner getBusinessPartnerByPartnerType(int partnerType) {
		Query query=sessionFactory.getCurrentSession().createQuery("from BusinessPartner where partnerType=:partnerType")
				.setParameter("partnerType",partnerType);
		List<BusinessPartner> list=query.list();
		return list.size()>0? list.get(0) : null ;
	}
	
	@Override
	public Account getAccount(String accountNumber) {
		Query query=sessionFactory.getCurrentSession().createQuery("from Account as acc where acc.accountNumber=:accountNumber");
		query.setParameter("accountNumber",accountNumber);
		List<Account> list= query.list();	
		return list.size()>0 ? list.get(0) :null;
	}
	@Override
//	@Transactional
	public void save(Object obj) {
		sessionFactory.getCurrentSession().save(obj);
	}
	
	@Override
//	@Transactional
	public void update(Object obj) {
		sessionFactory.getCurrentSession().update(obj);
	}
	
	@SuppressWarnings("unchecked")
	@Override
//	@Transactional
	public ExternalTransaction getExternalTransaction(String payeeMobileNumber) {
		Query query=sessionFactory.getCurrentSession().createQuery("from ExternalTransaction as ee where ee.beneficiaryMobileNumber=:payeeMobileNumber and ee.status=:status order by ee.externalTxnId desc ")
				.setParameter("payeeMobileNumber",payeeMobileNumber)
				.setParameter("status",Constants.STATUS_INITIATED);
		List<ExternalTransaction> list = query.list();	
		return list.size()>0 ? list.get(0) :null;
	}
	

}
