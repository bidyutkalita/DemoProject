/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SetDefaultEOTAccHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:54 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.Locale;

import com.eot.banking.exception.EOTException;
import com.eot.entity.Customer;

// TODO: Auto-generated Javadoc
/**
 * The Class SetDefaultEOTAccHandler.
 */
public class SetDefaultEOTAccHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData ) throws EOTException {
		System.out.println("******** setDefaultEotAcc *************");
		
		String accountAlias = new String(plainData[dataOffset]);
		
		Customer customer = eotMobileDao.getCustomer(applicationId);
		if(customer==null){
			throw new EOTException(5011);
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
		String message = messageSource.getMessage("SET_DEFAULT_ACC_SUCCESS", null , new Locale(defaultLang));
		
		return new byte[][] {message.getBytes()};
	}

}
