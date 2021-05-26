/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: DeleteBankAccountHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:54 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.Locale;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.TransactionType;

// TODO: Auto-generated Javadoc
/**
 * The Class DeleteBankAccountHandler.
 */
public class DeleteBankAccountHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData ) throws EOTException {

		String accountAlias = new String(plainData[dataOffset++]);
		
		CustomerBankAccount customerBankAccount = eotMobileDao.getBankAccountFromAccountAlias(customer.getCustomerId(), accountAlias);

		if(customerBankAccount == null){
			throw new EOTException(ErrorConstants.ACCOUNT_NUMBER_EXIST);
		}
		customerBankAccount.setStatus(Constants.INACTIVE);
		eotMobileDao.update(customerBankAccount);
		
		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(requestType);
		mobileRequest.setTransactionType(transactionType);

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

		String message = messageSource.getMessage("DELETE_BANK_ACCOUNT_SUCCESS", null, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}
}
