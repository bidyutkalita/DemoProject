/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BalanceEnquiryHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:31 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.BasicBankingServiceClientStub;
import com.eot.dtos.basic.BalanceEnquiryDTO;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class BalanceEnquiryHandler.
 */
public class BalanceEnquiryHandler extends BaseHandler {

	/** The basic banking service client stub. */
	@Autowired
	private BasicBankingServiceClientStub basicBankingServiceClientStub;

	/**
	 * Sets the basic banking service client stub.
	 * 
	 * @param basicBankingServiceClientStub
	 *            the new basic banking service client stub
	 */
	public void setBasicBankingServiceClientStub(
			BasicBankingServiceClientStub basicBankingServiceClientStub) {
		this.basicBankingServiceClientStub = basicBankingServiceClientStub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public  byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData) throws EOTException {

		System.out.println("******** BalanceEnquiryHandler *************");

		String accountAlias = new String(plainData[dataOffset++]);
		Integer aliasType = Integer.parseInt(new String(plainData[dataOffset++]));

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		if( Constants.ALIAS_TYPE_CARD_ACC == aliasType ){

			CustomerCard card = eotMobileDao.getCustomerCardFromAlias(customer.getCustomerId(), accountAlias);
			if(card == null){
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}
			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if( Constants.ALIAS_TYPE_MOBILE_ACC == aliasType ){

			CustomerAccount account = eotMobileDao.getAccountFromAccountAlias(customer.getCustomerId(), accountAlias);
			if(account == null){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		} else if( Constants.ALIAS_TYPE_BANK_ACC == aliasType ){

			CustomerBankAccount account = eotMobileDao.getBankAccountFromAccountAlias(customer.getCustomerId(), accountAlias);
			if(account == null){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getBankAccountNumber());
	//		accountDto.setBankCode(account.getBank().getBankId().toString());

		}  

		accountDto.setAccountAlias(accountAlias);
		accountDto.setAccountType(aliasType.toString());

		BalanceEnquiryDTO balanceEnquiryDTO = new BalanceEnquiryDTO();

		balanceEnquiryDTO.setTransactionType(transactionType.toString());
		balanceEnquiryDTO.setCustomerAccount(accountDto);
		balanceEnquiryDTO.setReferenceID(customer.getCustomerId().toString());
		balanceEnquiryDTO.setReferenceType(referenceType);
		balanceEnquiryDTO.setRequestID(requestID.toString());
		balanceEnquiryDTO.setChannelType(Constants.EOT_CHANNEL);
		balanceEnquiryDTO.setAmount(0D);

		try {
			balanceEnquiryDTO = basicBankingServiceClientStub.balanceEnquiry(balanceEnquiryDTO);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(balanceEnquiryDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

		} catch (EOTCoreException e) {
//			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}

		try {
			return packResponse(new DecimalFormat("0").format(balanceEnquiryDTO.getBalance()),customer.getDefaultLanguage());
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

	}

	/**
	 * Pack response.
	 * 
	 * @param accountBalance
	 *            the account balance
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String accountBalance , String defaultLang) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("BAL_ENQ_SUCCESS", new String[]{accountBalance}, new Locale(defaultLang));

		return new byte[][] {message.getBytes("UTF-8")};

	}

}
