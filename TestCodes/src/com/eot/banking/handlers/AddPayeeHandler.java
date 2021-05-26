/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: AddCardHandler.java,v 1.0
 *
 * Date Author Changes
 * 28 Oct, 2015, 3:46:51 PM Sambit Created
 */
package com.eot.banking.handlers;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.Payee;
import com.eot.entity.TransactionType;
import com.eot.smsclient.webservice.SmsServiceClientStub;

// TODO: Auto-generated Javadoc
/**
 * The Class AddCardHandler.
 */
public class AddPayeeHandler extends BaseHandler {

	/** The sms service client stub. */
	@Autowired
	private SmsServiceClientStub smsServiceClientStub ;

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;

	/**
	 * Sets the utility services cleint sub.
	 * 
	 * @param utilityServicesCleintSub
	 *            the new utility services cleint sub
	 */
	public void setUtilityServicesCleintSub(UtilityServicesCleintSub utilityServicesCleintSub) {
		this.utilityServicesCleintSub = utilityServicesCleintSub;
	}

	/**
	 * Sets the sms service client stub.
	 * 
	 * @param smsServiceClientStub
	 *            the new sms service client stub
	 */
	public void setSmsServiceClientStub(SmsServiceClientStub smsServiceClientStub) {
		this.smsServiceClientStub = smsServiceClientStub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData) throws EOTException {

		System.out.println("******** AddPayeeHandler *************");

		String payeeAlias = new String(plainData[dataOffset++]);
		String payeeAccountNumber = new String(plainData[dataOffset++]);
		String payeeName = new String(plainData[dataOffset++]);
		Integer payeeType = new Integer(new String(plainData[dataOffset++]));
		String txnPin = new String(plainData[dataOffset++]);

		if(!txnPin.equals(customer.getTransactionPin().toString())){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		
		Customer newPayee = null;
		Payee payee = eotMobileDao.getPayeeFromAlias(customer.getCustomerId(), payeeAlias);
		if(payee != null && payee.getStatus().intValue() != Constants.INACTIVE){
			throw new EOTException(ErrorConstants.PAYEE_EXIST);
		}
		newPayee = eotMobileDao.getCustomerFromMobileNo(payeeAccountNumber);
		payee = new Payee();
		payee.setAccountHolderName(payeeName);
		payee.setAccountNumber(payeeAccountNumber);
		payee.setAlias(payeeAlias);

		CustomerAccount payeeAccount = (CustomerAccount) newPayee.getCustomerAccounts().iterator().next();
		payee.setBankCode(payeeAccount.getBank().getBankCode());
		payee.setBranchCode(payeeAccount.getBranch().getBranchCode());
		payee.setPayeeType(payeeType);
		payee.setStatus(Constants.ACTIVE);
		payee.setReferenceType(customer.getType());
		payee.setReferenceId(customer.getCustomerId().toString());
		payee.setCreatedDate(new Date());
		payee.setUpdatedDate(new Date());

		eotMobileDao.save(payee);

		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(accountList.get(0).getAccountNumber());
		accountDto.setBankCode(accountList.get(0).getBank().getBankId().toString());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(referenceType);
		serviceChargeDebitDTO.setRequestID(requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(requestType.toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			serviceChargeDebitDTO =	utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);

		} catch (EOTCoreException e) {
//			e.printStackTrace();
			eotMobileDao.delete(payee);
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(requestType);
		mobileRequest.setTransactionType(transactionType);

		return packResponse(payeeAlias,customer.getDefaultLanguage());
	}

	/**
	 * Pack response.
	 * 
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 */
	public byte[][] packResponse(String payeeAlias,String defaultLang) {

		String message = messageSource.getMessage("ADD_PAYEE_SUCCESS",  new String[]{payeeAlias}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};

	}
}
