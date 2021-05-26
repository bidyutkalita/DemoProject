/* Copyright © EasOfTech 2016. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: RemittanceDTO.java
*
* Date Author Changes
* 14 Jun, 2016 Swadhin Created
*/
package com.eot.banking.dto;

/**
 * The Class FundTransferDTO.
 */
public class RemittanceDTO extends TransactionBaseDTO {

	/** The sender mobile number. */
	private String senderMobileNumber;

	/** The receiver mobile number. */
	private String receiverMobileNumber;

	/** The authorization code. */
	private String authorizationCode;

	/**
	 * Gets the sender mobile number.
	 *
	 * @return the sender mobile number
	 */
	public String getSenderMobileNumber() {
		return senderMobileNumber;
	}

	/**
	 * Sets the sender mobile number.
	 *
	 * @param senderMobileNumber the new sender mobile number
	 */
	public void setSenderMobileNumber(String senderMobileNumber) {
		this.senderMobileNumber = senderMobileNumber;
	}

	/**
	 * Gets the receiver mobile number.
	 *
	 * @return the receiver mobile number
	 */
	public String getReceiverMobileNumber() {
		return receiverMobileNumber;
	}

	/**
	 * Sets the receiver mobile number.
	 *
	 * @param receiverMobileNumber the new receiver mobile number
	 */
	public void setReceiverMobileNumber(String receiverMobileNumber) {
		this.receiverMobileNumber = receiverMobileNumber;
	}

	/**
	 * Gets the authorization code.
	 *
	 * @return the authorization code
	 */
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	/**
	 * Sets the authorization code.
	 *
	 * @param authorizationCode the new authorization code
	 */
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

}
