package com.eot.kcb.dao;

import com.eot.kcb.model.BankAccountMapping;

public interface AccountDao {
	
	public BankAccountMapping getBankAccountMappingByAccountNo(String accountNumber);
	public void save(BankAccountMapping bankAccountMapping);
	void deLink(BankAccountMapping bankAccountMapping);
	
	
	

}
