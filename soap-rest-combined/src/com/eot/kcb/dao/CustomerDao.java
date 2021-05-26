package com.eot.kcb.dao;

import java.util.List;

import com.eot.kcb.model.ApiCreadentials;
import com.eot.kcb.model.ApiLogs;
import com.eot.kcb.model.Customer;



public interface CustomerDao {
	public void save(Customer cust);

	public List<Customer> list();

	public Customer getCustomerByCode(String code);

	public ApiCreadentials validateRequest(String username, String password);

	ApiLogs checkDuplicate(String transactionId);

	void saveApiLogs(ApiLogs p);

	ApiLogs loadApiLogs(String transactionId);

}
