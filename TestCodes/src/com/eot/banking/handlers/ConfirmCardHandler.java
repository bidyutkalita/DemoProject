/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ConfirmCardHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:52 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.Locale;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.entity.Customer;
import com.eot.entity.CustomerCard;
import com.eot.entity.TransactionType;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfirmCardHandler.
 */
public class ConfirmCardHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData) throws EOTException {

		String cardAlias = new String(plainData[dataOffset++]);
		String confirmationCode = new String(plainData[dataOffset++]);

		Customer customer = eotMobileDao.getCustomer(applicationId);

		if(customer==null){
			throw new EOTException(5011);
		}
		
		CustomerCard card = eotMobileDao.getCustomerCardForConfirmation(customer.getCustomerId(), cardAlias);
		
		if(card == null){
			throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
		}
		
		if(!card.getNameOnCard().equalsIgnoreCase(confirmationCode)){
			throw new EOTException(ErrorConstants.INVALID_CONFIMATION_CODE);
		}

		card.setStatus(Constants.CARD_STATUS_CONFIRMED);
		
		eotMobileDao.update(card);

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

		String message = messageSource.getMessage("CONFIRM_CARD_SUCCESS", null, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};

	}
}
