/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: CardMaintenanceDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:39 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class CardMaintenanceDTO.
 */
public class CardMaintenanceDTO extends TransactionBaseDTO {
	
	/** The card number. */
	private String cardNumber;
	
	/** The card alias. */
	private String cardAlias;
	
	/** The cvv. */
	private Integer cvv;
	
	/** The expiry date. */
	private String expiryDate;
	
	/** The confirmation code. */
	private String confirmationCode;

	/**
	 * Gets the card number.
	 * 
	 * @return the card number
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * Sets the card number.
	 * 
	 * @param cardNumber
	 *            the new card number
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * Gets the card alias.
	 * 
	 * @return the card alias
	 */
	public String getCardAlias() {
		return cardAlias;
	}

	/**
	 * Sets the card alias.
	 * 
	 * @param cardAlias
	 *            the new card alias
	 */
	public void setCardAlias(String cardAlias) {
		this.cardAlias = cardAlias;
	}

	/**
	 * Gets the cvv.
	 * 
	 * @return the cvv
	 */
	public Integer getCvv() {
		return cvv;
	}

	/**
	 * Sets the cvv.
	 * 
	 * @param cvv
	 *            the new cvv
	 */
	public void setCvv(Integer cvv) {
		this.cvv = cvv;
	}

	/**
	 * Gets the expiry date.
	 * 
	 * @return the expiry date
	 */
	public String getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Sets the expiry date.
	 * 
	 * @param expiryDate
	 *            the new expiry date
	 */
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * Gets the confirmation code.
	 * 
	 * @return the confirmation code
	 */
	public String getConfirmationCode() {
		return confirmationCode;
	}

	/**
	 * Sets the confirmation code.
	 * 
	 * @param confirmationCode
	 *            the new confirmation code
	 */
	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

}
