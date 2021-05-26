/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: MobileTopupHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:38 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.DateUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.utilities.VoucherTopupDTO;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.Operator;

// TODO: Auto-generated Javadoc
/**
 * The Class MobileTopupHandler.
 */
public class MobileTopupHandler extends BaseHandler {

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
	public  byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		System.out.println("******** MobileTopupHandler *************");		

		String accountAlias = new String(plainData[dataOffset++]);
		Integer aliasType = Integer.parseInt(new String(plainData[dataOffset++]));
		Long operatorId = Long.parseLong(new String(plainData[dataOffset++]));
		Long amount = Long.parseLong(new String(plainData[dataOffset++]));
		String txnPin = new String(plainData[dataOffset++]);

		if( ! txnPin.equals(customer.getTransactionPin()) ){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		Operator operator = eotMobileDao.findOperator(operatorId);

		if(operator==null){
			throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		com.eot.dtos.common.Account operatorAccountDto = new com.eot.dtos.common.Account();

		operatorAccountDto.setAccountNO(operator.getAccount().getAccountNumber());
		operatorAccountDto.setAccountType(Constants.ALIAS_TYPE_OTHER+"");
		operatorAccountDto.setBankCode(operator.getBank().getBankId()+"");

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

		}  

		accountDto.setAccountAlias(accountAlias);
		accountDto.setAccountType(aliasType.toString());

		VoucherTopupDTO voucherTopupDTO = new VoucherTopupDTO();

		voucherTopupDTO.setTransactionType(transactionType.toString());
		voucherTopupDTO.setCustomerAccount(accountDto);
		voucherTopupDTO.setOtherAccount(operatorAccountDto);
		voucherTopupDTO.setAmount(amount.doubleValue());
		voucherTopupDTO.setChannelType(Constants.EOT_CHANNEL);
		voucherTopupDTO.setReferenceID(customer.getCustomerId().toString());
		voucherTopupDTO.setReferenceType(referenceType);
		voucherTopupDTO.setCustomerAccount(accountDto);
		voucherTopupDTO.setOperatorID(operatorId+"");
		try {
			voucherTopupDTO = utilityServicesCleintSub.voucherTopup(voucherTopupDTO);
			return packResponse(operator.getOperatorName(),voucherTopupDTO.getVoucherNo(), customer.getDefaultLanguage(),accountAlias,amount);
		}catch (EOTCoreException e) {
//			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

	}

	/**
	 * Pack response.
	 * 
	 * @param operatorName
	 *            the operator name
	 * @param voucherNumber
	 *            the voucher number
	 * @param defaultLang
	 *            the default lang
	 * @param alias
	 *            the alias
	 * @param amount
	 *            the amount
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String operatorName,String voucherNumber, String defaultLang ,String alias,Long amount) throws UnsupportedEncodingException {

		String date = DateUtil.formatDateToStr(new Date()); 

		String message = messageSource.getMessage("TOPUP_SUCCESS", new String[]{operatorName,voucherNumber,date,alias,amount.toString()}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};

	}
}
