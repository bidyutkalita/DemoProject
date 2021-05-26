/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: ChangePinHandler.java,v 1.0
 *
 * Date Author Changes
 * 21 Oct, 2015, 2:59:44 PM Sambit Created
 */
package com.eot.banking.handlers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.entity.CustomerAccount;
import com.eot.entity.ExchangeRate;

/**
 * The Class ChangePinHandler.
 */
public class ExchangeRateHandler extends BaseHandler {

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

		System.out.println("******** ExchangeRateHandler *************");

		String accountAlias = new String(plainData[dataOffset++]);
		Integer currencyId = Integer.parseInt(new String(plainData[dataOffset++]));

		CustomerAccount account = eotMobileDao.getAccountFromAccountAlias(customer.getCustomerId(), accountAlias);
		if(account == null){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}
		ExchangeRate exchangeRate = eotMobileDao.getExchangeRateFromCurrecyIdAndBankId(currencyId,account.getBank().getBankId());

		if (exchangeRate == null) {
			throw new EOTException(ErrorConstants.EXCHANGE_RATE_NOT_AVAILABLE);
		}
		return packResponse(exchangeRate.getBuyingRate().toString(),exchangeRate.getSellingRate().toString(),customer.getDefaultLanguage()); 

	}

	/**
	 * Pack response.
	 * 
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 */
	public byte[][] packResponse(String buyingRate,String sellingRate,String defaultLang) {

		String message = messageSource.getMessage("EXCHANGE_RATE", new String[]{buyingRate,sellingRate}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}

}
