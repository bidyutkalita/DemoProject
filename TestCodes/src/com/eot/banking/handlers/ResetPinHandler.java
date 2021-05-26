/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ResetPinHandler.java,v 1.0
*
* Date Author Changes
* 28 Oct, 2015, 3:47:05 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.EOTUtil;
import com.eot.banking.utils.HashUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.sms.NewLoginPinAlertDTO;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.CustomerAccount;
import com.eot.entity.MobileRequest;
import com.eot.entity.Transaction;
import com.eot.entity.TransactionType;
import com.eot.smsclient.webservice.SmsServiceClientStub;
import com.thinkways.util.TLVUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ResetPinHandler.
 */
public class ResetPinHandler extends BaseHandler {

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;

	/** The sms service client stub. */
	@Autowired
	private SmsServiceClientStub smsServiceClientStub ; 

	/**
	 * Sets the utility services cleint sub.
	 * 
	 * @param utilityServicesCleintSub
	 *            the new utility services cleint sub
	 */
	public void setUtilityServicesCleintSub(
			UtilityServicesCleintSub utilityServicesCleintSub) {
		this.utilityServicesCleintSub = utilityServicesCleintSub;
	}

	/**
	 * Sets the sms service client stub.
	 * 
	 * @param smsServiceClientStub
	 *            the new sms service client stub
	 */
	public void setSmsServiceClientStub(SmsServiceClientStub smsServiceClientStub) {
		this.smsServiceClientStub = smsServiceClientStub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#handleRequest(byte[][], boolean, java.lang.String)
	 */
	@Override
	public byte[][] handleRequest( byte[][] requestData, boolean reinitiatedTxn ,String userName ) throws Exception{

		String applicationId = null ;
		int requestStatus = Constants.MOBREQUEST_STATUS_LOGGED ;
		byte[] responseData = null ;

		try{

			Integer transactionType = Integer.parseInt(new String(requestData[0]));
			applicationId = new String(requestData[1]);

			appMaster = eotMobileDao.getApplicationType(applicationId);

			if (appMaster == null){
				throw new EOTException(ErrorConstants.INVALID_APPLICATION);
			}

			referenceType  = Integer.valueOf(appMaster.getReferenceType());

			if(appMaster.getReferenceType()== Constants.REF_TYPE_CUSTOMER || 
					appMaster.getReferenceType()== Constants.REF_TYPE_MERCHANT ){  

				customer = eotMobileDao.getCustomer(applicationId);

				if(customer == null){
					throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
				}
				if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED  ){ 
					throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
				}
				
				List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

				if(accountList.size() == 0 ){
					throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
				}

				if(accountList.get(0).getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
					throw new EOTException(ErrorConstants.INACTIVE_BANK);
				}

			}

			mobileRequest = new MobileRequest();  

			TransactionType txnType = new TransactionType();
			txnType.setTransactionType(transactionType);
			mobileRequest.setTransactionType(txnType);
			mobileRequest.setTransactionTime(new Date());
			mobileRequest.setAppMaster(appMaster);
			mobileRequest.setReferenceId(appMaster.getReferenceId());
			mobileRequest.setReferenceType(appMaster.getReferenceType());
			mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_LOGGED);

			eotMobileDao.save(mobileRequest);  // Log Mobile Request

			requestID = mobileRequest.getRequestId();

			byte[][] response = processRequest( applicationId, transactionType, requestData);

			responseData = TLVUtil.getLVArrayFromMultipleData(response);
			requestStatus = Constants.MOBREQUEST_STATUS_SUCCESS;

		}catch (EOTException e) {
			requestStatus = e.getErrorCode() ;
			String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
			responseData = messageSource.getMessage("ERROR_"+requestStatus, null, new Locale(locale)).getBytes();
		}catch (Exception e) {
			requestStatus = ErrorConstants.SERVICE_ERROR ;
			String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
			responseData = messageSource.getMessage("ERROR_"+requestStatus, null, new Locale(locale)).getBytes();
		}finally {

			if( mobileRequest != null ){

				mobileRequest.setStatus(requestStatus) ;
				mobileRequest.setResponseString(Hibernate.createBlob(responseData));

				eotMobileDao.update(mobileRequest);

			}

		}

		int responseStatus = requestStatus == Constants.MOBREQUEST_STATUS_SUCCESS ? Constants.MOB_RESP_STATUS_SUCCESS : Constants.MOB_RESP_STATUS_FAILURE ;
		return new byte[][] { (responseStatus+"").getBytes() , responseData };

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData) throws EOTException {

		System.out.println("******** Reset Pin Handler *************");

		if( Constants.APP_STATUS_RESET_PIN_VERIFIED != appMaster.getStatus() && Constants.APP_STATUS_RESET_PIN_SC_DEBITED != appMaster.getStatus()){
			throw new EOTException(ErrorConstants.VERIFY_CUSTOMER_RESET_TXN_PIN);
		}

		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0 ){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		CustomerAccount account = accountList.get(0) ;

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		if( Constants.APP_STATUS_RESET_PIN_SC_DEBITED != appMaster.getStatus() ){
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

				appMaster.setStatus(Constants.APP_STATUS_RESET_PIN_SC_DEBITED);
				eotMobileDao.update(appMaster);
			} catch (EOTCoreException e) {
//				e.printStackTrace();
				throw new EOTException(Integer.parseInt(e.getMessageKey()));
			}

		}

		try {

			kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);

			Integer loginPin = EOTUtil.generateLoginPin() ;

			customer.setLoginPin(HashUtil.generateHash(loginPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
			eotMobileDao.update(customer);

			NewLoginPinAlertDTO pinDto = new NewLoginPinAlertDTO();
			pinDto.setLocale(customer.getDefaultLanguage());
			pinDto.setLoginPIN(loginPin.toString());
			pinDto.setMobileNo(customer.getCountry().getIsdCode()+customer.getMobileNumber());
			pinDto.setScheduleDate(Calendar.getInstance());

			smsServiceClientStub.newLoginPinAlert(pinDto);

			appMaster.setStatus(Constants.APP_STATUS_NEW_PIN_SENT);
			eotMobileDao.update(appMaster);

			return packResponse(customer.getDefaultLanguage());
		} catch (Exception e1) {
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

		String message = messageSource.getMessage("RESET_PIN_SUCCESS", null, new Locale(defaultLang));
		return new byte[][] {message.getBytes("UTF-8")};

	}

}
