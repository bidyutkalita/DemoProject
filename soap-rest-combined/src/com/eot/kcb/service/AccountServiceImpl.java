package com.eot.kcb.service;

import java.util.Date;

import com.eot.kcb.dao.AccountDao;
import com.eot.kcb.dao.AccountDaoImpl;
import com.eot.kcb.dto.AccountDeLinkRequestDTO;
import com.eot.kcb.dto.AccountLinkRequestDTO;
import com.eot.kcb.dto.AccountValidationResponse;
import com.eot.kcb.model.BankAccountMapping;


public class AccountServiceImpl implements AccountService {

	private AccountDao accountDao= new AccountDaoImpl();

	@Override
	public AccountValidationResponse getAccountMappingByAccountNo(String acccountNo) {
		AccountValidationResponse accountValidationResponse = null;

		BankAccountMapping accountMapping = accountDao.getBankAccountMappingByAccountNo(acccountNo);

		if (accountMapping != null) {
			accountValidationResponse = new AccountValidationResponse();
			accountValidationResponse.setAccountNumber(accountMapping.getAccountNumber());
			accountValidationResponse.setAccountStatus(accountMapping.getAccountStatus());
			accountValidationResponse.setAccountTypes(accountMapping.getAccountType());
			accountValidationResponse.setLinkedStatus(accountMapping.getLinkedStatus());
			accountValidationResponse.setTransactionID(accountMapping.getId() + "");
			accountValidationResponse.setTransactionTimeStamp(new Date().toString());

		}
		return accountValidationResponse;
	}
	
	
	@Override
	public boolean save(AccountLinkRequestDTO accountLinkRequestDTO) {
		
		BankAccountMapping bankAccountMapping = new BankAccountMapping();
		BankAccountMapping accountMapping=new AccountDaoImpl().getBankAccountMappingByAccountNo(accountLinkRequestDTO.getAccountNumber());
		if(accountMapping!=null)
		{
			bankAccountMapping.setId(accountMapping.getId());
			return false;
			
		}
		bankAccountMapping.setAccountNumber(accountLinkRequestDTO.getAccountNumber());
		bankAccountMapping.setAccountStatus("Active");
		bankAccountMapping.setAccountType(accountLinkRequestDTO.getAccountTitle());
		bankAccountMapping.setBankName(accountLinkRequestDTO.getBankName());
		bankAccountMapping.setBranchCode(accountLinkRequestDTO.getBranchCode());
		bankAccountMapping.setCountryCode(accountLinkRequestDTO.getCountryCode());
		bankAccountMapping.setiDNumber(accountLinkRequestDTO.getiDNumber());
		bankAccountMapping.setiDType(accountLinkRequestDTO.getiDType());
		bankAccountMapping.setiMSI(accountLinkRequestDTO.getiMSI());
		bankAccountMapping.setLinkedStatus("Y");
		bankAccountMapping.setMobileNumber(accountLinkRequestDTO.getAuthorizedMobileNumber());
		bankAccountMapping.setNationality(accountLinkRequestDTO.getNationality());
		bankAccountMapping.setReferenceID(accountLinkRequestDTO.getReferenceID());

		accountDao.save(bankAccountMapping);
		return true;
		

	
	}
	
	@Override
	public boolean deLink(AccountDeLinkRequestDTO accountDeLinkRequestDTO) {
		
		BankAccountMapping bankAccountMapping = new BankAccountMapping();
		BankAccountMapping accountMapping=new AccountDaoImpl().getBankAccountMappingByAccountNo(accountDeLinkRequestDTO.getAccountNumber());
		try {
		if(accountMapping!=null)
		{
			bankAccountMapping.setId(accountMapping.getId());
			accountDao.deLink(bankAccountMapping);
			return true;
		}else
			return false;
		}catch (Exception e) {
			return false;
		}
	}
	
	

}
