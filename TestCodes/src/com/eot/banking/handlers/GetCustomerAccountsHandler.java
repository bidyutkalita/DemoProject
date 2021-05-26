/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: GetCustomerAccountsHandler.java,v 1.0
*
* Date Author Changes
* 3 Nov, 2015, 1:19:44 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.dtos.sms.WebOTPAlertDTO;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerCard;
import com.eot.smsclient.SmsServiceException;
import com.eot.smsclient.webservice.SmsServiceClientStub;

// TODO: Auto-generated Javadoc
/**
 * The Class GetCustomerAccountsHandler.
 */
public class GetCustomerAccountsHandler extends BaseHandler {
	
	/** The sms service client stub. */
	@Autowired
	private SmsServiceClientStub smsServiceClientStub ; 
	
	/** The otp for sale enabled. */
	@Autowired
	private boolean otpForSaleEnabled = false ;

	/**
	 * Sets the sms service client stub.
	 * 
	 * @param smsServiceClientStub
	 *            the new sms service client stub
	 */
	public void setSmsServiceClientStub(SmsServiceClientStub smsServiceClientStub) {
		this.smsServiceClientStub = smsServiceClientStub;
	}

	/**
	 * Sets the otp for sale enabled.
	 * 
	 * @param otpForSaleEnabled
	 *            the new otp for sale enabled
	 */
	public void setOtpForSaleEnabled(boolean otpForSaleEnabled) {
		this.otpForSaleEnabled = otpForSaleEnabled;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData ) throws EOTException {

		System.out.println("******** GetCustomerAccountsHandler *************");

		String mobileNumber = new String(plainData[dataOffset++]);

		Customer customer = eotMobileDao.getCustomerFromMobileNo(mobileNumber);

		if(customer==null){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		
		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0 ){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		if(accountList.get(0).getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}
		
		if(otpForSaleEnabled){

			WebOTPAlertDTO dto=new WebOTPAlertDTO();
			dto.setLocale(customer.getDefaultLanguage());
			dto.setMobileNo(customer.getMobileNumber());
			dto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
			dto.setReferenceId(customer.getCustomerId().toString());
			dto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			dto.setScheduleDate(Calendar.getInstance());

			try {
				smsServiceClientStub.webOTPAlert(dto);
			} catch (SmsServiceException e) {
				throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
			}

		}
		
//		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());
		List<CustomerCard> cardList = eotMobileDao.getCardDetails(customer.getCustomerId());
		
		return packResponse( accountList, cardList);

	}

	/**
	 * Pack response.
	 * 
	 * @param eotAccounts
	 *            the eot accounts
	 * @param cards
	 *            the cards
	 * @return the byte[][]
	 */
	public byte[][] packResponse( List<CustomerAccount> eotAccounts, List<CustomerCard> cards) {

		ArrayList<String> data = new ArrayList<String>();
		data.add(eotAccounts.size() + "");
		for (CustomerAccount eotAccount : eotAccounts) {
			data.add(eotAccount.getAccount().getAlias());
		}
		data.add(cards.size() + "");
		for (CustomerCard card : cards) {
			data.add(card.getAlias());
		}

		System.out.println("data - " + data );
		byte[][] response = new byte[data.size()][];
		for(int i = 0; i < response.length; i++) {
			response[i] = data.get(i).getBytes();
		}

		return response;
	}
}
