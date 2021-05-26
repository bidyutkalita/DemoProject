/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ChequeEnquiryDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:45 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class ChequeEnquiryDTO.
 */
public class ChequeEnquiryDTO extends TransactionBaseDTO {
	
	/** The cheque number. */
	private String chequeNumber;
	
	/** The cheque status. */
	private String chequeStatus;

	/**
	 * Gets the cheque number.
	 * 
	 * @return the cheque number
	 */
	public String getChequeNumber() {
		return chequeNumber;
	}

	/**
	 * Sets the cheque number.
	 * 
	 * @param chequeNumber
	 *            the new cheque number
	 */
	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	/**
	 * Gets the cheque status.
	 * 
	 * @return the cheque status
	 */
	public String getChequeStatus() {
		return chequeStatus;
	}

	/**
	 * Sets the cheque status.
	 * 
	 * @param chequeStatus
	 *            the new cheque status
	 */
	public void setChequeStatus(String chequeStatus) {
		this.chequeStatus = chequeStatus;
	}

}
