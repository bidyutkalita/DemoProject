/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ServerException.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:13 PM Sambit Created
*/
package com.eot.banking.server;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerException.
 */
public class ServerException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6068138909328688258L;
	
	/** The code. */
	private int code;
	
	/**
	 * Instantiates a new server exception.
	 * 
	 * @param code
	 *            the code
	 */
	public ServerException(int code) {
		this.code = code;
	}
	
	/**
	 * Instantiates a new server exception.
	 * 
	 * @param message
	 *            the message
	 */
	public ServerException(String message) {
		super(message);
	}
	
	/**
	 * Gets the code.
	 * 
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
}
