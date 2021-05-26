package com.bidyut.dao;

import java.util.List;

import com.bidyut.model.ApiCreadentials;
import com.bidyut.model.ApiLogs;
import com.bidyut.model.Customer;



public interface CustomerDao {
	public void save(Customer cust);

	public List<Customer> list();

	public Customer getCustomerByCode(String code);

	public ApiCreadentials validateRequest(String username, String password);

	ApiLogs checkDuplicate(String transactionId);

	void saveApiLogs(ApiLogs p);

	ApiLogs loadApiLogs(String transactionId);

}
