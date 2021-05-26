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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.entity.Customer;

/**
 * The Class DeleteCardHandler.
 */
public class FetchPayeeHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData) throws EOTException {

		System.out.println("******** AddPayeeHandler *************");

		String payeeAccountNumber = new String(plainData[dataOffset++]);
		
		Customer payee = eotMobileDao.getCustomerFromMobileNo(payeeAccountNumber);
		if(payee == null){
			throw new EOTException(ErrorConstants.PAYEE_DOES_NOT_EXIST);
		}
		String firstName = payee.getFirstName();
		String middleName = payee.getMiddleName();
		String lastName = payee.getLastName();
		String payeeName = firstName;
		if(middleName != null && middleName.trim().length()>0)
			payeeName = payeeName + " " + middleName;
		if(lastName != null && lastName.trim().length()>0)
			payeeName = payeeName + " " + lastName;

		ArrayList<String> data = new ArrayList<String>();
		data.add(payeeAccountNumber);
		data.add(payeeName);
		byte[][] response = new byte[data.size()][];
		for(int i = 0; i < response.length; i++) {
			try {
				response[i] = data.get(i).getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				throw new EOTException(ErrorConstants.SERVICE_ERROR);
			}
		}
		return response;
	}

}
