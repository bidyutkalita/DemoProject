/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: EOTException.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:14 PM Sambit Created
*/
package com.eot.banking.exception;



// TODO: Auto-generated Javadoc
/**
 * The Class EOTException.
 */
public class EOTException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The error code. */
	private Integer errorCode;
	
	/** The error message. */
	private String errorMessage;
	
	
	private String fieldName;

    /**
	 * Instantiates a new eOT exception.
	 * 
	 * @param errorCode
	 *            the error code
	 */
    public EOTException(Integer errorCode) {
        this.errorCode = errorCode;
    }
    
    public EOTException(Integer errorCode, String fieldName) {
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }
    
    /**
	 * Instantiates a new eOT exception.
	 * 
	 * @param errorMessage
	 *            the error message
	 */
    public EOTException(String errorMessage) {
    	this.errorMessage = errorMessage;
    }
    
	/**
	 * Gets the error code.
	 * 
	 * @return the error code
	 */
	public Integer getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public String getFieldName() {
		return fieldName;
	}
	

}
