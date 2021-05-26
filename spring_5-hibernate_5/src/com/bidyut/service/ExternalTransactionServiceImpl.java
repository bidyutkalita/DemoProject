package com.bidyut.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bidyut.common.Constants;
import com.bidyut.common.CoreUrls;
import com.bidyut.dao.ExternalTransactionDao;
import com.bidyut.dto.DepositReqDTO;
import com.bidyut.dto.DepositeResponse;
import com.eot.dtos.banking.DepositDTO;
import com.eot.dtos.common.Header;
import com.eot.entity.Account;
import com.eot.entity.BusinessPartner;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.ExternalTransaction;

@Service("externalTransactionService")
public class ExternalTransactionServiceImpl implements ExternalTransactionService {
	
	@Autowired
	ExternalTransactionDao externalTransactionDao;
	
	@Override
	@Transactional
	public DepositeResponse processNileBatDeposit(DepositReqDTO reqDTO)
	{
		DepositeResponse batDepositeResponse = new DepositeResponse();
		Customer cust = externalTransactionDao.getCustomerByMobileNumber(reqDTO.getCountryCode()+reqDTO.getMobileNumber());
		if(cust==null)
		{
			batDepositeResponse.setResponseMessage("No Customer found");
			batDepositeResponse.setResponseCode(01);
			return batDepositeResponse;
		}
		CustomerAccount custAcc= externalTransactionDao.getCustomerAccountWithCustomerId(cust.getCustomerId());

		com.eot.dtos.common.Account customerAccount = new com.eot.dtos.common.Account();
		customerAccount.setAccountAlias(custAcc.getAccount().getAlias());
		customerAccount.setAccountNO(custAcc.getAccountNumber());
		customerAccount.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		customerAccount.setBankCode(custAcc.getBank().getBankId().toString());
		customerAccount.setBranchCode(custAcc.getBranch().getBranchId().toString());

		BusinessPartner bp = externalTransactionDao.getBusinessPartnerByPartnerType(Constants.BP_TYPE_NILE_BET);

		Account remittanceAcc = externalTransactionDao.getAccount(bp.getAccountNumber());

		com.eot.dtos.common.Account otherAcc = new com.eot.dtos.common.Account();
		otherAcc.setAccountAlias(remittanceAcc.getAlias());
		otherAcc.setAccountNO(remittanceAcc.getAccountNumber());//only for testing
		otherAcc.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAcc.setBankCode(custAcc.getBank().getBankId().toString());
		otherAcc.setBranchCode(custAcc.getBranch().getBranchId().toString());
		
		
		ExternalTransaction externalTransaction = new ExternalTransaction();
		externalTransaction.setMobileNumber(cust.getMobileNumber());
		externalTransaction.setAmount(reqDTO.getAmount());
		externalTransaction.setBeneficiaryMobileNumber(reqDTO.getCountryCode()+reqDTO.getMobileNumber());
		externalTransaction.setCustomerName(cust.getFirstName()+" "+cust.getLastName());
		externalTransaction.setServiceChargeAmount(0);
		externalTransaction.setTransactionType(Constants.TXN_TYPE_BETTING_DEPOSIT);
		externalTransaction.setTransactionDate(new Date());
		externalTransaction.setStatus(Constants.STATUS_INITIATED);
		externalTransaction.setReferencedId(cust.getCustomerId());
		externalTransaction.setEntityName("betting");
		externalTransactionDao.save(externalTransaction);
		
		DepositDTO depositeDTO = new DepositDTO();
		depositeDTO.setCustomerAccount(customerAccount);
		depositeDTO.setOtherAccount(otherAcc);
		depositeDTO.setAmount(reqDTO.getAmount());
		depositeDTO.setTransactionType(Constants.TXN_TYPE_BETTING_DEPOSIT+"");
		depositeDTO.setReferenceID(cust.getCustomerId()+"");
		depositeDTO.setChannelType(Constants.EOT_CHANNEL);
		depositeDTO.setMobileNumber(reqDTO.getCountryCode()+reqDTO.getMobileNumber());
		depositeDTO.setTellerID("1");
		depositeDTO.setBalance(0d);
		
		externalTransaction=externalTransactionDao.getExternalTransaction(reqDTO.getCountryCode()+reqDTO.getMobileNumber());

		try {
		depositeDTO = processRequest(CoreUrls.NILE_BETTING_DEPOSITE, depositeDTO, DepositDTO.class);
		}catch (Exception e) {
			e.printStackTrace();
			externalTransaction.setStatus(Constants.STATUS_FAILED);
			externalTransactionDao.update(externalTransaction);
			batDepositeResponse.setResponseMessage("FAIL");
			batDepositeResponse.setResponseCode(01);
			return batDepositeResponse;
		}
		if (depositeDTO.getErrorCode() != 0) {
			if(depositeDTO.getTransactionNO()!=null)
				externalTransaction.setStatus(Constants.STATUS_FAILED);
			externalTransactionDao.update(externalTransaction);
			batDepositeResponse.setResponseMessage("FAIL");
			batDepositeResponse.setResponseCode(01);
			return batDepositeResponse;

		}
		
		batDepositeResponse.setResponseMessage("success");
		batDepositeResponse.setResponseCode(00);
		return batDepositeResponse;
	}
	
	public <T extends com.eot.dtos.common.Header> T processRequest(String url, T obj, Class<T> type) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		obj.setRequestChannel(Constants.REQUEST_CHANNEL_MOBILE);
		obj = restTemplate.postForObject(url, obj, type);
		return obj;
	}


}
