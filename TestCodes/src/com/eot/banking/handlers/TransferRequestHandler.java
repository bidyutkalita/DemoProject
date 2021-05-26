/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: TransferRequestHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:04 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.webservice.BankingServiceClientStub;
import com.eot.entity.CustomerAccount;

// TODO: Auto-generated Javadoc
/**
 * The Class TransferRequestHandler.
 */
public class TransferRequestHandler extends BaseHandler {

	/** The banking service client stub. */
	@Autowired
	private BankingServiceClientStub bankingServiceClientStub;

	/**
	 * Sets the banking service client stub.
	 * 
	 * @param bankingServiceClientStub
	 *            the new banking service client stub
	 */
	public void setBankingServiceClientStub(BankingServiceClientStub bankingServiceClientStub) {
		this.bankingServiceClientStub = bankingServiceClientStub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		String fromAlias = new String(plainData[dataOffset++]);
		Integer fromAliasType = Integer.parseInt(new String(plainData[dataOffset++]));
		String toMobileNo = new String(plainData[dataOffset++]);
//		String transDesc = new String(plainData[dataOffset++]);
		Long amount = Long.parseLong(new String(plainData[dataOffset++]));
		
		if((customer.getCountry().getIsdCode()+customer.getMobileNumber()).equalsIgnoreCase(toMobileNo)){
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}
		
		CustomerAccount otherAccount = eotMobileDao.getPayeeAccountFromMobileNo(toMobileNo);

		if(otherAccount == null){
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}
		
		if( otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED ){  // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}
		
		if(otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}
		
		try {
			
		    String payeeName = otherAccount.getCustomer().getFirstName().concat(" ").concat(otherAccount.getCustomer().getMiddleName()).concat(" ").concat(otherAccount.getCustomer().getLastName());
		    
			return packResponse(fromAlias, toMobileNo,payeeName, amount, customer.getDefaultLanguage());
			
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
		
	}

	/**
	 * Pack response.
	 * 
	 * @param fromAlias
	 *            the from alias
	 * @param toMobileNo
	 *            the to mobile no
	 * @param payeeName
	 *            the payee name
	 * @param amount
	 *            the amount
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String fromAlias, String toMobileNo,String payeeName,Long amount,String defaultLang) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("TRF_DIR_CONFIRM", new String[]{fromAlias,toMobileNo,payeeName,amount.toString()}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}
}
