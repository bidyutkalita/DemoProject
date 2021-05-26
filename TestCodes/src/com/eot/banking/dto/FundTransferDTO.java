/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: FundTransferDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:48 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class FundTransferDTO.
 */
public class FundTransferDTO extends TransactionBaseDTO {
	
	/** The payee mobile number. */
	private String payeeMobileNumber;
	
	 
 	/** The app type. */
 	private String applicationType;
 	
 	/** The txn banking type. */
 	private Integer txnBankingType;
 	
 	private String agentCode;
 	private String agentName;
 	
 	private String bankAccountNumber;
 	
 	
	
	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	/**
	 * Gets the payee mobile number.
	 * 
	 * @return the payee mobile number
	 */
	public String getPayeeMobileNumber() {
		return payeeMobileNumber;
	}

	/**
	 * Sets the payee mobile number.
	 * 
	 * @param payeeMobileNumber
	 *            the new payee mobile number
	 */
	public void setPayeeMobileNumber(String payeeMobileNumber) {
		this.payeeMobileNumber = payeeMobileNumber;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public Integer getTxnBankingType() {
		return txnBankingType;
	}

	public void setTxnBankingType(Integer txnBankingType) {
		this.txnBankingType = txnBankingType;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

}
