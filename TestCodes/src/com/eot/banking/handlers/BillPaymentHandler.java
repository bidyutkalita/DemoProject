/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BillPaymentHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:38 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.utilities.BillPaymentDTO;
import com.eot.entity.Biller;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.SenelecBills;
import com.eot.entity.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class BillPaymentHandler.
 */
public class BillPaymentHandler extends BaseHandler {

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
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		String fromAlias = new String(plainData[dataOffset++]);
		Integer fromAliasType = Integer.parseInt(new String(plainData[dataOffset++]));
		String policyNumber = new String(plainData[dataOffset++]);
		Long amount = Long.parseLong(new String(plainData[dataOffset++]));
		String txnPin = new String(plainData[dataOffset++]);
		String billerId =  new String(plainData[dataOffset++]);

		if(!txnPin.equals(customer.getTransactionPin().toString())){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		SenelecBills bill = eotMobileDao.getElectricityBillFromPolicyId(policyNumber);

		if(amount < bill.getRecordAmount()){
			throw new EOTException(ErrorConstants.NO_PARTIAL_PAYMENTS);
		}else if(amount > bill.getRecordAmount()){
			throw new EOTException(ErrorConstants.INVALID_BILL_AMOUNT);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		com.eot.dtos.common.Account billerAcountDto = new com.eot.dtos.common.Account();

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
		
		Biller biller = eotMobileDao.getBiller(Integer.parseInt(billerId));
		
		if(biller == null){
			throw new EOTException(ErrorConstants.INVALID_BILLER);
		}
		
		billerAcountDto.setAccountNO(biller.getAccount().getAccountNumber());
		billerAcountDto.setAccountType(Constants.ALIAS_TYPE_OTHER+"");
		billerAcountDto.setBankCode(biller.getBank().getBankId().toString());

		BillPaymentDTO billPaymentDTO = new BillPaymentDTO();

		billPaymentDTO.setCustomerAccount(accountDto);
		billPaymentDTO.setOtherAccount(billerAcountDto);
		billPaymentDTO.setAmount(amount.doubleValue());
		billPaymentDTO.setChannelType(Constants.EOT_CHANNEL);
		billPaymentDTO.setRequestID(requestID.toString());
		billPaymentDTO.setReferenceID(customer.getCustomerId().toString());
		billPaymentDTO.setReferenceType(referenceType);
		billPaymentDTO.setTransactionType(transactionType+"");
		billPaymentDTO.setBillerID(billerId);

		try {

			billPaymentDTO = utilityServicesCleintSub.billPayment(billPaymentDTO);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(billPaymentDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

			bill.setAmountPaid(amount.doubleValue());
			bill.setMobileNumber(customer.getMobileNumber());
			if(bill.getRecordAmount()<=amount.doubleValue()){
				bill.setStatus(1);
			}else{
				bill.setStatus(2);
			}
			bill.setTransaction(txn);

			eotMobileDao.update(bill);

			return packResponse(fromAlias, biller.getBillerName(),amount, customer.getDefaultLanguage());

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
	 * @param billerName
	 *            the biller name
	 * @param amount
	 *            the amount
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String fromAlias, String billerName,Long amount,String defaultLang) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("BILL_PAY_SUCCESS", new String[]{fromAlias,billerName,amount.toString()}, new Locale(defaultLang));

		return new byte[][] {message.getBytes("UTF-8")};
	}
}
