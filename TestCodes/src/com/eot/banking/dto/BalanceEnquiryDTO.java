/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BalanceEnquiryDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:30 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class BalanceEnquiryDTO.
 */
public class BalanceEnquiryDTO extends TransactionBaseDTO{
	
	/** The available balance. */
	private Double availableBalance;
	
	/** The unclear balance. */
	private Double unclearBalance;
	
	/** The total balance. */
	private Double totalBalance;

	/**
	 * Gets the available balance.
	 * 
	 * @return the available balance
	 */
	public Double getAvailableBalance() {
		return availableBalance;
	}

	/**
	 * Sets the available balance.
	 * 
	 * @param availableBalance
	 *            the new available balance
	 */
	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	/**
	 * Gets the unclear balance.
	 * 
	 * @return the unclear balance
	 */
	public Double getUnclearBalance() {
		return unclearBalance;
	}

	/**
	 * Sets the unclear balance.
	 * 
	 * @param unclearBalance
	 *            the new unclear balance
	 */
	public void setUnclearBalance(Double unclearBalance) {
		this.unclearBalance = unclearBalance;
	}

	/**
	 * Gets the total balance.
	 * 
	 * @return the total balance
	 */
	public Double getTotalBalance() {
		return totalBalance;
	}

	/**
	 * Sets the total balance.
	 * 
	 * @param totalBalance
	 *            the new total balance
	 */
	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}

}
