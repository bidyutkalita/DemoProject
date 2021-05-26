/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: LoginHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:32 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.entity.Customer;

// TODO: Auto-generated Javadoc
/**
 * The Class LoginHandler.
 */
public class LoginHandler extends BaseHandler {

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;

	/**
	 * Sets the utility services cleint sub.
	 * 
	 * @param utilityServicesCleintSub
	 *            the new utility services cleint sub
	 */
	public void setUtilityServicesCleintSub( UtilityServicesCleintSub utilityServicesCleintSub ) {
		this.utilityServicesCleintSub = utilityServicesCleintSub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData) throws EOTException {

		System.out.println("******** Login Handler *************");

		String loginPin = new String(plainData[dataOffset++]);

		Customer customer = eotMobileDao.getCustomer(applicationId);

		if(customer==null){
			throw new EOTException(5011);
		}
		
		if( ! loginPin.equals(customer.getLoginPin()) ){
			throw new EOTException(ErrorConstants.INVALID_USER_PIN);
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

		String message = messageSource.getMessage("LOGIN_SUCCESSFUL", null, new Locale(defaultLang));
		
		return new byte[][] {message.getBytes()};
	}

}
