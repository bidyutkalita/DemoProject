/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: TransferDirectHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:01 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.DateUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.BankingServiceClientStub;
import com.eot.dtos.banking.TransferDirectDTO;
import com.eot.entity.ClearingHousePoolMember;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class TransferDirectHandler.
 */
public class TransferDirectHandler extends BaseHandler {

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
		String txnPin = new String(plainData[dataOffset++]);

		if(!txnPin.equals(customer.getTransactionPin().toString())){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		if((customer.getCountry().getIsdCode()+customer.getMobileNumber()).equalsIgnoreCase(toMobileNo)){
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
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

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

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

		List<ClearingHousePoolMember> chPoolList = eotMobileDao.getClearingHouse(Integer.parseInt(accountDto.getBankCode()),
				otherAccount.getBank().getBankId());

		if(chPoolList==null){
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}

		/*ClearingHousePool clearingHousePool = eotMobileDao.getClearingHouse(chPoolList.get(0).getClearingPoolId());

		if(clearingHousePool==null){
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}*/

		com.eot.dtos.common.Account otherAccountDto = new com.eot.dtos.common.Account();
		otherAccountDto.setAccountAlias(toMobileNo);
		otherAccountDto.setAccountNO(otherAccount.getAccountNumber());
		otherAccountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		otherAccountDto.setBankCode(otherAccount.getBank().getBankId().toString());
		otherAccountDto.setBranchCode(otherAccount.getBranch().getBranchId().toString());

		TransferDirectDTO transferDirectDTO = new TransferDirectDTO();

		transferDirectDTO.setCustomerAccount(accountDto);
		transferDirectDTO.setOtherAccount(otherAccountDto);
		transferDirectDTO.setAmount(amount.doubleValue());
		transferDirectDTO.setChannelType(Constants.EOT_CHANNEL);
		transferDirectDTO.setRequestID(requestID.toString());
		transferDirectDTO.setReferenceID(customer.getCustomerId().toString());
		transferDirectDTO.setReferenceType(referenceType);
		transferDirectDTO.setTransactionType(transactionType+"");
		//		transferDirectDTO.setTransDesc(transDesc);

		try {
			transferDirectDTO = bankingServiceClientStub.transferDirect(transferDirectDTO);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(transferDirectDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

			return packResponse(fromAlias, toMobileNo, amount,transferDirectDTO.getTransactionNO(), customer.getDefaultLanguage());

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
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String fromAlias, String toMobileNo,Long amount,String txnRefNo,String defaultLang) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("TRF_DIR_SUCCESS", new String[]{DateUtil.formattedDateAndTime(new Date()),fromAlias,toMobileNo,amount.toString(),txnRefNo}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}
}
