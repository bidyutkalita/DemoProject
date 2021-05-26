/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: AddBankAccountHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:23 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.Bank;
import com.eot.entity.Branch;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class AddBankAccountHandler.
 */
public class AddBankAccountHandler extends BaseHandler {

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

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData) throws EOTException {

		System.out.println("******** AddCardHandler *************");

		String accountAlias = new String(plainData[dataOffset++]);
		String accountNumber = new String(plainData[dataOffset++]);
		Integer bankId = Integer.parseInt(new String(plainData[dataOffset++]));
		Long branchId = Long.parseLong(new String(plainData[dataOffset++]));

		Bank bank = eotMobileDao.getBankFromBankId(bankId);
		Branch branch = eotMobileDao.getBranchFromBranchId(branchId);

		if(customer.getBankCustomerId()== null){
			throw new EOTException(ErrorConstants.INVALID_BANK_CUSTOMER_ID);
		}

		CustomerBankAccount customerBankAccount = eotMobileDao.getCustomerBankAccountFromAccountNumber(accountNumber);

		if(customerBankAccount != null){
			throw new EOTException(ErrorConstants.ACCOUNT_NUMBER_EXIST);
		}

		customerBankAccount = new CustomerBankAccount();
		customerBankAccount.setAlias(accountAlias);
		customerBankAccount.setReferenceId(customer.getCustomerId()+"");
		customerBankAccount.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		customerBankAccount.setAccountHolderName(customer.getFirstName());
		customerBankAccount.setStatus(Constants.ACTIVE);
		CustomerAccount customerAccount = (CustomerAccount) customer.getCustomerAccounts().iterator().next();
	//	customerBankAccount.setBank(customerAccount.getBank());
		customerBankAccount.setCreatedDate(new Date());
		customerBankAccount.setUpdatedDate(new Date());

		eotMobileDao.save(customerBankAccount);

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(customerAccount.getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getBranch().getBranchId().toString());

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(referenceType);
		serviceChargeDebitDTO.setRequestID(requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(requestType.toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

		} catch (EOTCoreException e) {
//			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}

		return packResponse(customer.getDefaultLanguage());
	}

	/**
	 * Pack response.
	 * 
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 */
	public byte[][] packResponse(String defaultLang) {

		String message = messageSource.getMessage("ADD_BANK_ACCOUNT_SUCCESS", null, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};

	}
}
