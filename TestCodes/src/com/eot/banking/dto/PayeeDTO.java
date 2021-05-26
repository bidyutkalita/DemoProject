/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: PayeeDTO.java
*
* Date Author Changes
* 4 Feb, 2016 Swadhin Created
*/
package com.eot.banking.dto;

/**
 * The Class PayeeDTO.
 */
public class PayeeDTO extends TransactionBaseDTO {
	
	/** The payee account number. */
	private String payeeAccountNumber;
	
	/** The payee type. */
	private Integer payeeType;
	
	/** The payee name. */
	private String payeeName;
	
	/** The payee alias. */
	private String payeeAlias;
	
	/** The bank id. */
	private Integer bankId;
	
	/** The branch id. */
	private Long branchId;
	
	/**
	 * Gets the payee account number.
	 *
	 * @return the payee account number
	 */
	public String getPayeeAccountNumber() {
		return payeeAccountNumber;
	}

	/**
	 * Sets the payee account number.
	 *
	 * @param payeeAccountNumber the new payee account number
	 */
	public void setPayeeAccountNumber(String payeeAccountNumber) {
		this.payeeAccountNumber = payeeAccountNumber;
	}

	/**
	 * Gets the payee type.
	 *
	 * @return the payee type
	 */
	public Integer getPayeeType() {
		return payeeType;
	}

	/**
	 * Sets the payee type.
	 *
	 * @param payeeType the new payee type
	 */
	public void setPayeeType(Integer payeeType) {
		this.payeeType = payeeType;
	}

	/**
	 * Gets the payee name.
	 *
	 * @return the payee name
	 */
	public String getPayeeName() {
		return payeeName;
	}

	/**
	 * Sets the payee name.
	 *
	 * @param payeeName the new payee name
	 */
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	/**
	 * Gets the payee alias.
	 *
	 * @return the payee alias
	 */
	public String getPayeeAlias() {
		return payeeAlias;
	}

	/**
	 * Sets the payee alias.
	 *
	 * @param payeeAlias the new payee alias
	 */
	public void setPayeeAlias(String payeeAlias) {
		this.payeeAlias = payeeAlias;
	}

	/**
	 * Gets the bank id.
	 *
	 * @return the bank id
	 */
	public Integer getBankId() {
		return bankId;
	}

	/**
	 * Sets the bank id.
	 *
	 * @param bankId the new bank id
	 */
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	/**
	 * Gets the branch id.
	 *
	 * @return the branch id
	 */
	public Long getBranchId() {
		return branchId;
	}

	/**
	 * Sets the branch id.
	 *
	 * @param branchId the new branch id
	 */
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	
	

}
