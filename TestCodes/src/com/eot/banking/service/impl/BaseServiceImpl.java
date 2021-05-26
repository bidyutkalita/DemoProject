/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BaseServiceImpl.java,v 1.0
*
* Date Author Changes
* 3 Nov, 2015, 1:09:43 PM Sambit Created
*/
package com.eot.banking.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.daos.EOTMobileDao;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.BaseService;
import com.eot.banking.utils.AppConfigurations;
import com.eot.banking.utils.DateUtil;
import com.eot.entity.AppMaster;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.MobileRequest;
import com.eot.entity.TransactionType;
import com.eot.entity.VersionDetails;
import com.security.kms.KMS;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseServiceImpl.
 */
@Service
public class BaseServiceImpl implements BaseService{

	/** The message source. */
	@Autowired
	protected MessageSource messageSource ;

	/** The eot mobile dao. */
	@Autowired
	protected EOTMobileDao eotMobileDao;

	/** The app config. */
	@Autowired
	protected AppConfigurations appConfig;

	/** The kms handler. */
	protected KMS kmsHandler;

	/** The mobile request. */
	protected MobileRequest mobileRequest = null;

	/** The app master. */
	protected AppMaster appMaster = null ;

	/** The customer. */
	protected Customer customer = null;

	/** The request id. */
	protected Long requestID = null;

	/** The reference type. */
	protected Integer referenceType = null;

	/** The default locale. */
	protected String defaultLocale = "en_US";

	/**
	 * Sets the kms handler.
	 * 
	 * @param kmsHandler
	 *            the new kms handler
	 */
	public void setKmsHandler(KMS kmsHandler) {
		this.kmsHandler = kmsHandler;
	}

	

	/**
	 * Sets the eot mobile dao.
	 * 
	 * @param eotMobileDao
	 *            the new eot mobile dao
	 */
	public void setEotMobileDao(EOTMobileDao eotMobileDao) {
		this.eotMobileDao = eotMobileDao;
	}



	/* (non-Javadoc)
	 * @see com.eot.banking.service.BaseService#handleRequest(com.eot.banking.dto.TransactionBaseDTO)
	 */
	public void handleRequest(TransactionBaseDTO transactionBaseDTO) throws EOTException{

		transactionBaseDTO.setDefaultLocale(defaultLocale);
		Integer transactionType = transactionBaseDTO.getTransactionType();
		String applicationId = new String(transactionBaseDTO.getApplicationId());

		Date transmissionTime = new Date(transactionBaseDTO.getTransmissionTime());
		Date transactionTime = new Date(transactionBaseDTO.getTransactionTime());
		Long stan = new Long(transactionBaseDTO.getStan());
		Long rrn = new Long(transactionBaseDTO.getRrn());
		String versionNumber = new String(transactionBaseDTO.getVersionNumber());

		appMaster = eotMobileDao.getApplicationType(applicationId); // Check in appmaster table for appid

		if (appMaster == null){
			throw new EOTException(ErrorConstants.INVALID_APPLICATION);
		}

		referenceType  = Integer.valueOf(appMaster.getReferenceType());

		if(appMaster.getReferenceType()== Constants.REF_TYPE_CUSTOMER || 
				appMaster.getReferenceType()== Constants.REF_TYPE_MERCHANT ||
				appMaster.getReferenceType() == Constants.REF_TYPE_AGENT||appMaster.getReferenceType() == Constants.REF_TYPE_AGENT_SOLE_MERCHANT){  // Check in customer/merchant table with appid

			customer = eotMobileDao.getCustomer(applicationId);

			if(customer == null){
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
			}
			
			if( appMaster.getStatus() == Constants.APP_STATUS_BLOCKED ){
				throw new EOTException(ErrorConstants.Y_ACCOUNT_BLOCKED);
			}
			
			if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.Y_AGENT_ACC_DEACTIVATED);
			}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
				throw new EOTException(ErrorConstants.AGENT_KYC_PENDING);
			}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
				throw new EOTException(ErrorConstants.AGENT_KYC_REJECTED);
			}
			
			if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER) {
				throw new EOTException(ErrorConstants.Y_CUSTOMER_ACC_DEACTIVATED);
			}else if( customer.getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
			}
			
			if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
				throw new EOTException(ErrorConstants.Y_MERCHANT_ACC_DEACTIVATED);
			}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
				throw new EOTException(ErrorConstants.MERCHANT_KYC_PENDING);
			}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
				throw new EOTException(ErrorConstants.MERCHANT_KYC_REJECTED);
			}
			
			if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_SUSPENDED) {
				throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
			}

			transactionBaseDTO.setDefaultLocale(customer == null ? defaultLocale : customer.getDefaultLanguage());

			VersionDetails version = eotMobileDao.getApplicationVersion(versionNumber);

			if(version!=null){
				Calendar cal = Calendar.getInstance();
				Integer dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
				appMaster.setStatus(Constants.APP_STATUS_NEW);
				appMaster.setExpiryDate(cal.getTime());
				eotMobileDao.update(appMaster);
				transactionBaseDTO.setAppUpdateURL(appConfig.getAppDownloadURL().concat(appMaster.getUuid()));
				throw new EOTException(ErrorConstants.APPLICATION_UPDATE_AVAILABLE);
			}

			List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

			if(accountList.size() == 0 ){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			if(accountList.get(0).getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
				throw new EOTException(ErrorConstants.INACTIVE_BANK);
			}

		}

		if( appMaster.getStatus() == Constants.APP_STATUS_BLOCKED  ){  
			throw new EOTException(ErrorConstants.APPLICATION_BLOCKED);
		}else if( appMaster.getStatus() == Constants.APP_STATUS_RESET_PIN_VERIFIED || 
				appMaster.getStatus() == Constants.APP_STATUS_RESET_PIN_SC_DEBITED || 
				appMaster.getStatus() == Constants.APP_STATUS_NEW_PIN_SENT ){  
			throw new EOTException(ErrorConstants.LOGIN_PIN_RESET);
		} else if(appMaster.getStatus() == Constants.APP_STATUS_DEACTIVATED){
			throw new EOTException(ErrorConstants.APPLICATION_DEACTIVATED);
		}

		if(!(DateUtil.formatDate(transmissionTime).equals(DateUtil.formatDate(new Date())) 
				&& DateUtil.formatDate(transactionTime).equals(DateUtil.formatDate(new Date())))){  
			throw new EOTException(ErrorConstants.INVALID_DATE);
		}

		mobileRequest = eotMobileDao.getRequest(applicationId ,stan ,rrn ,DateUtil.formatDate(new Date())); // Get the request from MobileRequest log table.
		TransactionType txnType = new TransactionType();
		txnType.setTransactionType(transactionType);
		if ( mobileRequest != null ){
			mobileRequest.setTransactionType(txnType);
			if( transmissionTime.getTime() <= mobileRequest.getTransmissionTime().getTime() ){  
				throw new EOTException(ErrorConstants.INVALID_TIME);
			} 
		}else {
			mobileRequest = new MobileRequest();
			mobileRequest.setTransactionType(txnType);
			mobileRequest.setRequestString(Hibernate.createBlob(transactionBaseDTO.getEncPayload()));
			mobileRequest.setRrn(rrn);
			mobileRequest.setStan(stan);
			mobileRequest.setTransactionTime(new Date());
			mobileRequest.setTransmissionTime(transmissionTime);
			mobileRequest.setAppMaster(appMaster);
			mobileRequest.setReferenceId(appMaster.getReferenceId());
			mobileRequest.setReferenceType(appMaster.getReferenceType());
			mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_LOGGED);

			eotMobileDao.save(mobileRequest);
			transactionBaseDTO.setRequestID(mobileRequest.getRequestId());
			transactionBaseDTO.setEncPayload(null);

		}

		requestID = mobileRequest.getRequestId();
		
		
		/*
		 * appMaster.setStatus(20); eotMobileDao.update(appMaster);
		 */

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.service.BaseService#updateMobileRequest(com.eot.banking.dto.TransactionBaseDTO)
	 */
	public void updateMobileRequest( TransactionBaseDTO transactionBaseDTO ) {
		if(transactionBaseDTO.getRequestID() != null){
			MobileRequest mobileRequest = eotMobileDao.getMobileRequest(transactionBaseDTO.getRequestID());
			mobileRequest.setResponseString(Hibernate.createBlob(transactionBaseDTO.getEncPayload()));
			mobileRequest.setTransactionType(eotMobileDao.getLoadTransactionType(transactionBaseDTO.getTransactionType()));
			if(transactionBaseDTO.getStatus() == null){
				mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_SUCCESS);
			}else{
				mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_FAILURE);
			}
			transactionBaseDTO.setEncPayload(null);
			eotMobileDao.update(mobileRequest);

		}
	}

}