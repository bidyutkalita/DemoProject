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

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;

// TODO: Auto-generated Javadoc
/**
 * The Class ChangePinHandler.
 */
public class ChangePinHandler extends BaseHandler {

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

		System.out.println("******** ChangePinHandler *************");

		String oldPin = new String(plainData[dataOffset++]);
		String newPin = new String(plainData[dataOffset++]);

		Customer customer = eotMobileDao.getCustomer(applicationId);

		if(customer==null){
			throw new EOTException(5011);
		}
		
		if( ! oldPin.equals(customer.getLoginPin()) ){
			throw new EOTException(ErrorConstants.INVALID_USER_PIN);
		}
		
		if(appMaster.getStatus() == Constants.APP_STATUS_ACTIVATION_SC_DEBITED){
			appMaster.setStatus(Constants.APP_STATUS_ACTIVATED);
			eotMobileDao.update(appMaster);
		}

		System.out.println("oldPin = " + oldPin);
		System.out.println("newPin = " + newPin);

		customer.setActive(Constants.CHANGE_PIN_REQ);
		customer.setLoginPin(newPin);
		eotMobileDao.update(customer);

		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}
		
		CustomerAccount account = accountList.get(0) ;

//		accountDto.setAccountNO(account.getAccountNumber());
//		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
//		accountDto.setBankCode(account.getBank().getBankId().toString());
//		accountDto.setBranchCode(account.getBranch().getBranchId().toString());
//		
//		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();
//
//		serviceChargeDebitDTO.setCustomerAccount(accountDto);
//		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
//		serviceChargeDebitDTO.setReferenceType(referenceType);
//		serviceChargeDebitDTO.setRequestID(requestID.toString());
//		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
//		serviceChargeDebitDTO.setTransactionType(transactionType.toString());
//		serviceChargeDebitDTO.setAmount(0L);
//
//		try {
//			
//			utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
//			
//			Transaction txn = new Transaction();
//			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
//			mobileRequest.setTransaction(txn);
//			
//		} catch (EOTCoreException e) {
//			e.printStackTrace();
//			throw new EOTException(Integer.parseInt(e.getMessageKey()));
//		}

		return packResponse(customer.getDefaultLanguage()); 

	}

	/**
	 * Pack response.
	 * 
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 */
	public byte[][] packResponse(String defaultLang) {

		String message = messageSource.getMessage("CHANGE_TXN_SUCCESS", null, new Locale(defaultLang));
		
		return new byte[][] {message.getBytes()};
	}

}
