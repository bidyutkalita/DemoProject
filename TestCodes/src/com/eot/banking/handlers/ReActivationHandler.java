/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ReActivationHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:44 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.DateUtil;
import com.eot.entity.CustomerAccount;
import com.eot.entity.MobileRequest;
import com.eot.entity.TransactionType;
import com.security.kms.security.KMSSecurityException;
import com.thinkways.util.TLVUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ReActivationHandler.
 */
public class ReActivationHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#handleRequest(byte[][], boolean, java.lang.String)
	 */
	@Override
	public byte[][] handleRequest( byte[][] requestData, boolean reinitiatedTxn ,String userName ) throws Exception {

		String applicationId = null ;
		boolean keyStored = false;
		int requestStatus = Constants.MOBREQUEST_STATUS_LOGGED ;
		byte[] responseData = null ;

		try{

			Integer transactionType = Integer.parseInt(new String(requestData[0]));
			applicationId = new String(requestData[1]);
			byte[] encryptedData = requestData[2];

			kmsHandler.unWrapAndStoreRmkWrappedKey(requestData[3], applicationId,
					Constants.KEY_VERSION, Constants.KEY_OWNER, new Date(), new Date());

			keyStored = true ;

			byte[][] plainData = TLVUtil.getAllDataFromLVArray( kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, 
					Constants.KEY_OWNER, encryptedData, false), 0 );

			Date transmissionTime = new Date(Long.parseLong(new String(plainData[0])));
			Date transactionTime = new Date(Long.parseLong(new String(plainData[1])));
			Long stan = new Long(new String(plainData[2]));
			Long rrn = new Long(new String(plainData[3]));

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

			if( appMaster.getStatus() != Constants.APP_STATUS_NEW_PIN_SENT ){  
				throw new EOTException(ErrorConstants.INVALID_APPLICATION_STATE);
			}

			if(!(DateUtil.formatDate(transmissionTime).equals(DateUtil.formatDate(new Date())) 
					&& DateUtil.formatDate(transactionTime).equals(DateUtil.formatDate(new Date())))){  
				throw new EOTException(ErrorConstants.INVALID_DATE);
			}

			mobileRequest = eotMobileDao.getRequest(applicationId ,stan ,rrn ,DateUtil.formatDate(new Date())); // Get the request from MobileRequest log table.

			if ( mobileRequest != null ){
				if( transmissionTime.getTime() <= mobileRequest.getTransmissionTime().getTime() ){  
					throw new EOTException(ErrorConstants.INVALID_TIME);
				}else{
					Blob reqBlob = mobileRequest.getResponseString() ;
					int responseStatus =  mobileRequest.getStatus() == Constants.MOBREQUEST_STATUS_SUCCESS ? Constants.MOB_RESP_STATUS_SUCCESS : Constants.MOB_RESP_STATUS_FAILURE ;
					responseData = reqBlob.getBytes(1, (int)reqBlob.length());
					return new byte[][]{(responseStatus + "").getBytes(), responseData};
				}
			}

			mobileRequest = new MobileRequest();  

			TransactionType txnType = new TransactionType();
			txnType.setTransactionType(transactionType);
			mobileRequest.setTransactionType(txnType);
			mobileRequest.setRequestString(Hibernate.createBlob(encryptedData));
			mobileRequest.setRrn(rrn);
			mobileRequest.setStan(stan);
			mobileRequest.setTransactionTime(transactionTime);
			mobileRequest.setTransmissionTime(transmissionTime);
			mobileRequest.setAppMaster(appMaster);
			mobileRequest.setReferenceId(appMaster.getReferenceId());
			mobileRequest.setReferenceType(appMaster.getReferenceType());
			mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_LOGGED);

			eotMobileDao.save(mobileRequest);  // Log Mobile Request

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

			byte[] plainResponseData = TLVUtil.getLVArrayFromMultipleData( ret );

			responseData =  kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, plainResponseData, true);
			requestStatus = Constants.MOBREQUEST_STATUS_SUCCESS ;

			appMaster.setStatus(Constants.APP_STATUS_ACTIVATED);
			eotMobileDao.update(appMaster); 

		}catch(Exception ex){
			ex.printStackTrace();
			if( keyStored ) {
				try {
					kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, 
							Constants.KEY_OWNER);
				} catch (KMSSecurityException e) {
//					e.printStackTrace();
				}
			}

			if(ex instanceof EOTException){
				requestStatus = ((EOTException)ex).getErrorCode() ;
				String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
				responseData = messageSource.getMessage("ERROR_"+requestStatus, null, new Locale(locale)).getBytes("UTF-8");
			}else{
				requestStatus = ErrorConstants.SERVICE_ERROR ;
				String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
				responseData = messageSource.getMessage("ERROR_"+requestStatus, null, new Locale(locale)).getBytes("UTF-8");
			}
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
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		String userPinHash = new String(plainData[dataOffset]);

		System.out.println("userPinHash : " + userPinHash );
		System.out.println("customerPinHash : " + customer.getLoginPin() );

		if( ! userPinHash.equals(customer.getLoginPin()) ){
			throw new EOTException(ErrorConstants.INVALID_USER_PIN);
		}

		try {
			return packResponse(customer.getDefaultLanguage());
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
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
		String message = messageSource.getMessage("RE_ACTIVATION_SUCCESS", null, new Locale(defaultLang));
		return new byte[][] {message.getBytes("UTF-8")};

	}

}
