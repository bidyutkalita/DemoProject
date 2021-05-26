/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BaseDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:32 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseDTO.
 */
public class BaseDTO {

	/** The application id. */
	private String applicationId;
	
	/** The default locale. */
	private String defaultLocale;
	
	/** The transaction type. */
	private Integer transactionType;
	
	/** The status. */
	private Integer status;
	
	/** The message description. */
	private String messageDescription;
	
	/** The version number. */
	private String versionNumber;
	
	/** The success response. */
	private String successResponse;
	
	/** The enc payload. */
	private byte[] encPayload;
	
	/** The request id. */
	private Long requestID;
	
	/** The app update url. */
	private String appUpdateURL;
	
	private boolean serverPKWrapKeyRequired = false;
	
	private long requestStan = 0;

	/**
	 * 
	 */
	private int requestType;
	
	private String errorCode;
	
	private String masterKey;


	private String checkSumString;
	/**
	 * Gets the application id.
	 * 
	 * @return the application id
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * Sets the application id.
	 * 
	 * @param applicationId
	 *            the new application id
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * Gets the transaction type.
	 * 
	 * @return the transaction type
	 */
	public Integer getTransactionType() {
		return transactionType;
	}

	/**
	 * Sets the transaction type.
	 * 
	 * @param transactionType
	 *            the new transaction type
	 */
	public void setTransactionType(Integer transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * Gets the message description.
	 * 
	 * @return the message description
	 */
	public String getMessageDescription() {
		return messageDescription;
	}

	/**
	 * Sets the message description.
	 * 
	 * @param messageDescription
	 *            the new message description
	 */
	public void setMessageDescription(String messageDescription) {
		this.messageDescription = messageDescription;
	}

	/**
	 * Gets the default locale.
	 * 
	 * @return the default locale
	 */
	public String getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * Sets the default locale.
	 * 
	 * @param defaultLocale
	 *            the new default locale
	 */
	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	
	/**
	 * Gets the success response.
	 * 
	 * @return the success response
	 */
	public String getSuccessResponse() {
		return successResponse;
	}

	/**
	 * Sets the success response.
	 * 
	 * @param successResponse
	 *            the new success response
	 */
	public void setSuccessResponse(String successResponse) {
		this.successResponse = successResponse;
	}
	
	/**
	 * Gets the version number.
	 * 
	 * @return the version number
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * Sets the version number.
	 * 
	 * @param versionNumber
	 *            the new version number
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * Gets the enc payload.
	 * 
	 * @return the enc payload
	 */
	public byte[] getEncPayload() {
		return encPayload;
	}

	/**
	 * Sets the enc payload.
	 * 
	 * @param encPayload
	 *            the new enc payload
	 */
	public void setEncPayload(byte[] encPayload) {
		this.encPayload = encPayload;
	}

	/**
	 * Gets the request id.
	 * 
	 * @return the request id
	 */
	public Long getRequestID() {
		return requestID;
	}

	/**
	 * Sets the request id.
	 * 
	 * @param requestID
	 *            the new request id
	 */
	public void setRequestID(Long requestID) {
		this.requestID = requestID;
	}

	/**
	 * Gets the app update url.
	 *
	 * @return the app update url
	 */
	public String getAppUpdateURL() {
		return appUpdateURL;
	}

	/**
	 * Sets the app update url.
	 *
	 * @param appUpdateURL the new app update url
	 */
	public void setAppUpdateURL(String appUpdateURL) {
		this.appUpdateURL = appUpdateURL;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public boolean isServerPKWrapKeyRequired() {
		return serverPKWrapKeyRequired;
	}

	public void setServerPKWrapKeyRequired(boolean serverPKWrapKeyRequired) {
		this.serverPKWrapKeyRequired = serverPKWrapKeyRequired;
	}

	public long getRequestStan() {
		return requestStan;
	}

	public void setRequestStan(long requestStan) {
		this.requestStan = requestStan;
	}

	public String getMasterKey() {
		return masterKey;
	}

	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	public String getCheckSumString() {
		return checkSumString;
	}

	public void setCheckSumString(String checkSumString) {
		this.checkSumString = checkSumString;
	}
	
	
	
}
