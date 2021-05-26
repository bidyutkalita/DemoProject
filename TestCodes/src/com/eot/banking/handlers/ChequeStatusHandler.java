/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ChequeStatusHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:49 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.rmi.RemoteException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.cbs.ws.common.CBSException;
import com.cbs.ws.dto.ChequeStatusDTO;
import com.cbs.ws.handler.CBSBankingStubProxy;
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
 * The Class ChequeStatusHandler.
 */
public class ChequeStatusHandler extends BaseHandler {

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
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData) throws EOTException {

		String accountAlias = new String(plainData[dataOffset++]);
		String chequeNumber = new String(plainData[dataOffset++]);

		ChequeStatusDTO chequeStatusDTO = new ChequeStatusDTO();
		chequeStatusDTO.setChequeNumber(chequeNumber);

		try {
			CBSBankingStubProxy bankingStubProxy = new CBSBankingStubProxy();
			chequeStatusDTO = bankingStubProxy.handleCBSChequeStatus(chequeStatusDTO);

		} catch (CBSException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.INVALID_CHEQUE_NUMBER);
		} catch (RemoteException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

		if(customer.getBankCustomerId()== null){
			throw new EOTException(ErrorConstants.INVALID_BANK_CUSTOMER_ID_FOR_CHEQUE);
		}

		CustomerAccount customerAccount = eotMobileDao.getAccountFromAccountAlias(customer.getCustomerId(), accountAlias);

		if(customerAccount == null){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}
		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_BANK_ACC+"");
		accountDto.setBankCode(customerAccount.getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getBranch().getBranchId().toString());

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(referenceType);
		serviceChargeDebitDTO.setRequestID(requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(requestType.toString());
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

		return packResponse(customer.getDefaultLanguage(), chequeStatusDTO.getStatus(), chequeNumber);
	}

	/**
	 * Pack response.
	 * 
	 * @param defaultLang
	 *            the default lang
	 * @param chequeStatus
	 *            the cheque status
	 * @param chequeNumber
	 *            the cheque number
	 * @return the byte[][]
	 */
	public byte[][] packResponse(String defaultLang, String chequeStatus, String chequeNumber) {

		String message = messageSource.getMessage("CHEQUE_"+chequeStatus, new String[]{chequeNumber}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};

	}
}
