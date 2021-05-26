/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ChangePINDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:42 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class ChangePINDTO.
 */
public class ChangePINDTO extends TransactionBaseDTO{
	
	/** The new pin. */
	private String newPIN;
	
	/** The new txn pin. */
	private String newTxnPIN;

	/**
	 * Gets the new pin.
	 * 
	 * @return the new pin
	 */
	public String getNewPIN() {
		return newPIN;
	}

	/**
	 * Sets the new pin.
	 * 
	 * @param newPIN
	 *            the new new pin
	 */
	public void setNewPIN(String newPIN) {
		this.newPIN = newPIN;
	}

	/**
	 * Gets the new txn pin.
	 * 
	 * @return the new txn pin
	 */
	public String getNewTxnPIN() {
		return newTxnPIN;
	}

	/**
	 * Sets the new txn pin.
	 * 
	 * @param newTxnPIN
	 *            the new new txn pin
	 */
	public void setNewTxnPIN(String newTxnPIN) {
		this.newTxnPIN = newTxnPIN;
	}

}
