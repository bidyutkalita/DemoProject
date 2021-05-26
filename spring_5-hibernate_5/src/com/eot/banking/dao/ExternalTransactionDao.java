package com.eot.banking.dao;

import com.eot.entity.Account;
import com.eot.entity.BusinessPartner;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.ExternalTransaction;

public interface ExternalTransactionDao {

	Customer getCustomerByMobileNumber(String string);

	CustomerAccount getCustomerAccountWithCustomerId(Long customerId);

	BusinessPartner getBusinessPartnerByPartnerType(int partnerType);

	Account getAccount(String accountNumber);

	void save(Object obj);

	void update(Object obj);

	ExternalTransaction getExternalTransaction(String payeeMobileNumber);

}
