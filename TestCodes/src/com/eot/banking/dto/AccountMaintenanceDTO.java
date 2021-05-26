/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: AccountMaintenanceDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:27 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class AccountMaintenanceDTO.
 */
public class AccountMaintenanceDTO extends TransactionBaseDTO {
	
	/** The bank account number. */
	private String bankAccountNumber;
	
	/** The bank account alias. */
	private String bankAccountAlias;
	
	/** The bank code. */
	private String bankCode;
	
	/** The branch code. */
	private String branchCode;

	/**
	 * Gets the bank account number.
	 * 
	 * @return the bank account number
	 */
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	/**
	 * Sets the bank account number.
	 * 
	 * @param bankAccountNumber
	 *            the new bank account number
	 */
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	/**
	 * Gets the bank code.
	 * 
	 * @return the bank code
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * Sets the bank code.
	 * 
	 * @param bankCode
	 *            the new bank code
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * Gets the branch code.
	 * 
	 * @return the branch code
	 */
	public String getBranchCode() {
		return branchCode;
	}

	/**
	 * Sets the branch code.
	 * 
	 * @param branchCode
	 *            the new branch code
	 */
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	/**
	 * Gets the bank account alias.
	 * 
	 * @return the bank account alias
	 */
	public String getBankAccountAlias() {
		return bankAccountAlias;
	}

	/**
	 * Sets the bank account alias.
	 * 
	 * @param bankAccountAlias
	 *            the new bank account alias
	 */
	public void setBankAccountAlias(String bankAccountAlias) {
		this.bankAccountAlias = bankAccountAlias;
	}

}
