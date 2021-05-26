package com.bidyut.dao;

import com.bidyut.model.BankAccountMapping;

public interface AccountDao {
	
	public BankAccountMapping getBankAccountMappingByAccountNo(String accountNumber);
	public void save(BankAccountMapping bankAccountMapping);
	void deLink(BankAccountMapping bankAccountMapping);
	
	
	

}
