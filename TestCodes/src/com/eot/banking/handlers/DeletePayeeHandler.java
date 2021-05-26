/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: DeleteCardHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:57 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.Locale;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.entity.Payee;
import com.eot.entity.TransactionType;

/**
 * The Class DeleteCardHandler.
 */
public class DeletePayeeHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData ) throws EOTException {

		String payeeAlias = new String(plainData[dataOffset++]);

		Payee payee = eotMobileDao.getPayeeFromAlias(customer.getCustomerId(), payeeAlias);

		if(payee == null){
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}

		payee.setStatus(Constants.INACTIVE);

		eotMobileDao.update(payee);

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

		String message = messageSource.getMessage("DELETE_PAYEE_SUCCESS", null, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}
}
