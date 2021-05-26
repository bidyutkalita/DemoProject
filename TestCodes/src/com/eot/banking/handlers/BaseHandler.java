/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BaseHandler.java,v 1.0
*
* Date Author Changes
* 3 Nov, 2015, 1:17:17 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.sql.Blob;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.eot.banking.daos.EOTMobileDao;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.AppConfigurations;
import com.eot.banking.utils.DateUtil;
import com.eot.entity.AppMaster;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.MobileRequest;
import com.eot.entity.MobileRequestArchive;
import com.eot.entity.TransactionType;
import com.eot.entity.VersionDetails;
import com.eot.entity.WebUser;
import com.security.kms.KMS;
import com.thinkways.util.TLVUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseHandler.
 */
public abstract class BaseHandler{

	/** The data offset. */
	protected int dataOffset = 5 ;

	/** The message source. */
	@Autowired
	protected ReloadableResourceBundleMessageSource messageSource ;
	
	/** The eot mobile dao. */
	@Autowired
	protected EOTMobileDao eotMobileDao;
	
	/** The kms handler. */
	@Autowired
	protected KMS kmsHandler;
	
	/** The app config. */
	@Autowired
	private AppConfigurations appConfig;

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
	 * Sets the message source.
	 * 
	 * @param messageSource
	 *            the new message source
	 */
	public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	/**
	 * Sets the app config.
	 * 
	 * @param appConfig
	 *            the new app config
	 */
	public void setAppConfig(AppConfigurations appConfig) {
		this.appConfig = appConfig;
	}
	
	/**
	 * Sets the kms handler.
	 *
	 * @param kmsHandler the new kms handler
	 */
	public void setKmsHandler(KMS kmsHandler) {
		this.kmsHandler = kmsHandler;
	}

	/**
	 * Sets the eot mobile dao.
	 *
	 * @param eotMobileDao the new eot mobile dao
	 */
	public void setEotMobileDao(EOTMobileDao eotMobileDao) {
		this.eotMobileDao = eotMobileDao;
	}


	/**
	 * Handle request.
	 * 
	 * @param requestData
	 *            the request data
	 * @param reinitiatedTxn
	 *            the reinitiated txn
	 * @param userName
	 *            the user name
	 * @return the byte[][]
	 * @throws Exception
	 *             the exception
	 */
	public byte[][] handleRequest( byte[][] requestData, boolean reinitiatedTxn ,String userName ) throws Exception {

		int requestStatus = Constants.MOBREQUEST_STATUS_LOGGED ;
		byte[] responseData = null ;
		byte[] dataToBeLogged = null ;

		try{

			Integer transactionType = Integer.parseInt(new String(requestData[0]));
			String applicationId = new String(requestData[1]);
			byte[] encryptedData = requestData[2];

			byte[][] plainData = TLVUtil.getAllDataFromLVArray( kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, encryptedData, false), 0 );

			Date transmissionTime = new Date(Long.parseLong(new String(plainData[0])));
			Date transactionTime = new Date(Long.parseLong(new String(plainData[1])));
			Long stan = new Long(new String(plainData[2]));
			Long rrn = new Long(new String(plainData[3]));
			String versionNumber = new String(plainData[4]);

			appMaster = eotMobileDao.getApplicationType(applicationId); // Check in appmaster table for appid

			if (appMaster == null){
				throw new EOTException(ErrorConstants.INVALID_APPLICATION);
			}

			referenceType  = Integer.valueOf(appMaster.getReferenceType());

			if(appMaster.getReferenceType()== Constants.REF_TYPE_CUSTOMER || 
					appMaster.getReferenceType()== Constants.REF_TYPE_MERCHANT ){  // Check in customer/merchant table with appid

				customer = eotMobileDao.getCustomer(applicationId);

				if(customer == null){
					throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
				}
				if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED ){  // validate status of merchant/customer
					throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
				}

				VersionDetails version = eotMobileDao.getApplicationVersion(versionNumber);

				if(version!=null){
					Calendar cal = Calendar.getInstance();
					Integer dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
					appMaster.setStatus(Constants.APP_STATUS_NEW);
					appMaster.setExpiryDate(cal.getTime());
					eotMobileDao.update(appMaster);
					throw new EOTException(appConfig.getAppDownloadURL().concat(appMaster.getUuid()));
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

			if ( mobileRequest != null && ! reinitiatedTxn ){
				if( transmissionTime.getTime() <= mobileRequest.getTransmissionTime().getTime() ){  
					throw new EOTException(ErrorConstants.INVALID_TIME);
				}else{
					Blob reqBlob = mobileRequest.getResponseString() ;
					int responseStatus =  mobileRequest.getStatus() == Constants.MOBREQUEST_STATUS_SUCCESS ? Constants.MOB_RESP_STATUS_SUCCESS : Constants.MOB_RESP_STATUS_FAILURE ;
					responseData = reqBlob.getBytes(1, (int)reqBlob.length());
					return new byte[][]{(responseStatus + "").getBytes(), responseData};
				}
			} else if ( mobileRequest != null && reinitiatedTxn ){

				if(mobileRequest.getStatus() != Constants.MOBREQUEST_STATUS_SUCCESS && 
						mobileRequest.getTransactionType().getTransactionType() == Constants.TRANSFER_DIRECT_REQ  ){

					MobileRequestArchive orgRequest = new MobileRequestArchive();

					orgRequest.setOriginalRequest(mobileRequest);
					orgRequest.setStatus(mobileRequest.getStatus());
					orgRequest.setTransaction(mobileRequest.getTransaction());
					orgRequest.setTransactionTime(mobileRequest.getTransactionTime());
					WebUser user = new WebUser();
					user.setUserName(userName);
					orgRequest.setWebUser(user);

					eotMobileDao.save(orgRequest);

				}else{
					throw new EOTException(ErrorConstants.INVALID_REINITIATION_REQUEST);
				}


			} else {

				mobileRequest = new MobileRequest();

				TransactionType txnType = new TransactionType();
				txnType.setTransactionType(transactionType);
				mobileRequest.setTransactionType(txnType);

				mobileRequest.setRequestString(Hibernate.createBlob(encryptedData));
				mobileRequest.setRrn(rrn);
				mobileRequest.setStan(stan);
				mobileRequest.setTransactionTime(new Date());
				mobileRequest.setTransmissionTime(transmissionTime);
				mobileRequest.setAppMaster(appMaster);
				mobileRequest.setReferenceId(appMaster.getReferenceId());
				mobileRequest.setReferenceType(appMaster.getReferenceType());
				mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_LOGGED);

				eotMobileDao.save(mobileRequest);

			}

			requestID = mobileRequest.getRequestId();

			byte[][] response = processRequest( applicationId, transactionType, plainData);

			byte[][] ret = new byte[response.length + 4][];
			ret[0] = (transmissionTime.getTime() + "").getBytes();
			ret[1] = (transactionTime.getTime() + "").getBytes();
			ret[2] = (stan + "").getBytes();
			ret[3] = (rrn + "").getBytes();
			for(int i = 0; i < response.length; i++) {
				ret[i + 4] = response[i];
			}
			byte[] resp = TLVUtil.getLVArrayFromMultipleData(ret);

			dataToBeLogged =  kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, resp, true);

			requestStatus = Constants.MOBREQUEST_STATUS_SUCCESS ;

			if(reinitiatedTxn){
				responseData = ret[4] ;
			}else{
				responseData = dataToBeLogged ;
			}

		} catch (EOTException e){
//			e.printStackTrace();
			if(e.getErrorCode()!=null){
				requestStatus = e.getErrorCode();
			}else{
				throw(e);
			}

			String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
			dataToBeLogged = responseData = messageSource.getMessage("ERROR_"+e.getErrorCode(), null, new Locale(locale)).getBytes();
			System.out.println("--"+new String(responseData) +"--");
		} catch(Exception e){
//			e.printStackTrace();
			requestStatus = ErrorConstants.SERVICE_ERROR ;
			String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
			dataToBeLogged = responseData = messageSource.getMessage("ERROR_"+ErrorConstants.SERVICE_ERROR, null, new Locale(locale)).getBytes();
		} finally {

			if( mobileRequest != null ){

				mobileRequest.setStatus(requestStatus) ;
				mobileRequest.setResponseString(Hibernate.createBlob(dataToBeLogged));

				eotMobileDao.update(mobileRequest);

			}

		}

		int responseStatus = (requestStatus == Constants.MOBREQUEST_STATUS_SUCCESS) ? Constants.MOB_RESP_STATUS_SUCCESS : Constants.MOB_RESP_STATUS_FAILURE ;

		return new byte[][] { (responseStatus+"").getBytes() , responseData };

	}

	/**
	 * Process request.
	 * 
	 * @param applicationId
	 *            the application id
	 * @param requestType
	 *            the request type
	 * @param plainData
	 *            the plain data
	 * @return the byte[][]
	 * @throws EOTException
	 *             the eOT exception
	 */
	protected abstract byte[][] processRequest(String applicationId, Integer requestType, byte[][] plainData) throws EOTException;

}
