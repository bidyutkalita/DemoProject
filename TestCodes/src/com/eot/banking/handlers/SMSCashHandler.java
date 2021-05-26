/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SMSCashHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:58 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.BankingServiceClientStub;
import com.eot.dtos.banking.SMSCashDTO;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class SMSCashHandler.
 */
public class SMSCashHandler extends BaseHandler {

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
		Long amount = Long.parseLong(new String(plainData[dataOffset++]));
		String txnPin = new String(plainData[dataOffset++]);

		if(!txnPin.equals(customer.getTransactionPin().toString())){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		accountDto.setAccountAlias(fromAlias);
		accountDto.setAccountType(fromAliasType.toString());

		if( Constants.ALIAS_TYPE_CARD_ACC == fromAliasType ){

			CustomerCard card = eotMobileDao.getCustomerCardFromAlias(customer.getCustomerId(), fromAlias);
			if(card == null){
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if( Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType ){

			CustomerAccount account = eotMobileDao.getAccountFromAccountAlias(customer.getCustomerId(), fromAlias);
			if(account == null){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			CustomerCard virtualCard = eotMobileDao.getVirtualCardforBank(account.getBank().getBankId().toString());

			if(virtualCard == null){
				throw new EOTException(ErrorConstants.BANK_CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		}

		SMSCashDTO smsCashDTO = new SMSCashDTO();

		smsCashDTO.setCustomerAccount(accountDto);
		smsCashDTO.setAmount(amount.doubleValue());
		smsCashDTO.setChannelType(Constants.EOT_CHANNEL);
		smsCashDTO.setRequestID(requestID.toString());
		smsCashDTO.setReferenceID(customer.getCustomerId().toString());
		smsCashDTO.setReferenceType(referenceType);
		smsCashDTO.setTransactionType(transactionType+"");
		smsCashDTO.setPayeeMobileNumber(toMobileNo);

		try {

			smsCashDTO = bankingServiceClientStub.smsCash(smsCashDTO);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(smsCashDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

			return packResponse(fromAlias, toMobileNo, amount, customer.getDefaultLanguage(),smsCashDTO.getSmsCashPin());

		} catch (EOTCoreException e) {
//			e.printStackTrace();
			System.out.println(e.getMessageKey());
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
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
	 * @param amount
	 *            the amount
	 * @param defaultLang
	 *            the default lang
	 * @param smsCashPin
	 *            the sms cash pin
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String fromAlias, String toMobileNo,Long amount,String defaultLang,String smsCashPin) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("SMS_CASH_SUCCESS", new String[]{fromAlias,toMobileNo,amount.toString(),smsCashPin}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}

}
