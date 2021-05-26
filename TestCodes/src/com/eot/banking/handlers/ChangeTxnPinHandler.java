/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ChangeTxnPinHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:47 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.CustomerAccount;
import com.eot.entity.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class ChangeTxnPinHandler.
 */
public class ChangeTxnPinHandler extends BaseHandler {

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;
	
	/**
	 * Sets the utility services cleint sub.
	 * 
	 * @param utilityServicesCleintSub
	 *            the new utility services cleint sub
	 */
	public void setUtilityServicesCleintSub(UtilityServicesCleintSub utilityServicesCleintSub) {
		this.utilityServicesCleintSub = utilityServicesCleintSub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData) throws EOTException {

		System.out.println("******** ChangePinHandler *************");

		String oldTxnPin = new String(plainData[dataOffset++]);
		String newTxnPin = new String(plainData[dataOffset++]);
		
		System.out.println("oldTxnPin : "+oldTxnPin);
		System.out.println("newTxnPin : "+newTxnPin);
		System.out.println("customer.getTransactionPin() : "+customer.getTransactionPin());
		
		if( ! oldTxnPin.equals( customer.getTransactionPin()) ){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		
		if(appMaster.getStatus() == Constants.APP_STATUS_ACTIVATION_SC_DEBITED){
			appMaster.setStatus(Constants.APP_STATUS_ACTIVATED);
			eotMobileDao.update(appMaster);
		}

		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		CustomerAccount account = accountList.get(0) ;

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());
		
		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(referenceType);
		serviceChargeDebitDTO.setRequestID(requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(transactionType.toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
			
			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);
			
		} catch (EOTCoreException e) {
//			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}
		
		customer.setActive(Constants.CHANGE_TXN_PIN_REQ);
		customer.setTransactionPin(newTxnPin);
		eotMobileDao.update(customer);

		try {
			return packResponse(customer.getDefaultLanguage());
		} catch (UnsupportedEncodingException e) {
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		} 

	}

	/**
	 * Pack response.
	 * 
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String defaultLang) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("CHANGE_TXN_PIN_SUCCESS", null, new Locale(defaultLang));
		return new byte[][] {message.getBytes()};

	}

}
