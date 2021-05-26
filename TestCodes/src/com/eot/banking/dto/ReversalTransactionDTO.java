/* Copyright © EOT 2018. All rights reserved.
*
* This software is the confidential and proprietary information
* of EOT. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EOT.
*
* Id: ReversalTransactionDTO.java
*
* Date Author Changes
* Jan 23, 2019 Sudhanshu Created
*/
package com.eot.banking.dto;

import java.util.Date;
import java.util.List;

import com.eot.dtos.common.Account;

/**
 * The Class ReversalTransactionDTO.
 */
public class ReversalTransactionDTO extends TransactionBaseDTO {
	
	/** The transaction type ref. */
	private String transactionTypeRef;
	
	private String agentName;
	
	
	/** The transaction id. */
	private String transactionId;
	
	/** The fee. */
	private String fee;
	
	/** The app id. */
	private String appId;
	
	/** The description. */
	private String description;
	
	/** The reference id. */
	private String referenceId;
	
	/** The reference type. */
	private int referenceType;
	
	/** The customer account. */
	private Account customerAccount;
	
	/** The other account. */
	private Account otherAccount;
	
	/** The channel type. */
	private String channelType;
	
	private String custAccount;
	
	private String agentAccount;
	
	private Date transactionDate;
	
	/**
	 * @return the transactionDate
	 */
	public Date getTransactionDate() {
		return transactionDate;
	}

	/**
	 * @param transactionDate the transactionDate to set
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	/** The reversal txn list. */
	private List<ReversalTransactionDTO> reversalTxnList;
	
	/**
	 * Gets the transaction type ref.
	 *
	 * @return the transaction type ref
	 */
	public String getTransactionTypeRef() {
		return transactionTypeRef;
	}
	
	/**
	 * Sets the transaction type ref.
	 *
	 * @param transactionTypeRef the new transaction type ref
	 */
	public void setTransactionTypeRef(String transactionTypeRef) {
		this.transactionTypeRef = transactionTypeRef;
	}
	
	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 */
	public String getTransactionId() {
		return transactionId;
	}
	
	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the new transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * Gets the fee.
	 *
	 * @return the fee
	 */
	public String getFee() {
		return fee;
	}
	
	/**
	 * Sets the fee.
	 *
	 * @param fee the new fee
	 */
	public void setFee(String fee) {
		this.fee = fee;
	}
	
	/**
	 * Gets the app id.
	 *
	 * @return the app id
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * Sets the app id.
	 *
	 * @param appId the new app id
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the reference id.
	 *
	 * @return the reference id
	 */
	public String getReferenceId() {
		return referenceId;
	}
	
	/**
	 * Sets the reference id.
	 *
	 * @param referenceId the new reference id
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
	/**
	 * Gets the reference type.
	 *
	 * @return the reference type
	 */
	public int getReferenceType() {
		return referenceType;
	}
	
	/**
	 * Sets the reference type.
	 *
	 * @param referenceType the new reference type
	 */
	public void setReferenceType(int referenceType) {
		this.referenceType = referenceType;
	}
	
	/**
	 * Gets the customer account.
	 *
	 * @return the customer account
	 */
	public Account getCustomerAccount() {
		return customerAccount;
	}
	
	/**
	 * Sets the customer account.
	 *
	 * @param customerAccount the new customer account
	 */
	public void setCustomerAccount(Account customerAccount) {
		this.customerAccount = customerAccount;
	}
	
	/**
	 * Gets the other account.
	 *
	 * @return the other account
	 */
	public Account getOtherAccount() {
		return otherAccount;
	}
	
	/**
	 * Sets the other account.
	 *
	 * @param otherAccount the new other account
	 */
	public void setOtherAccount(Account otherAccount) {
		this.otherAccount = otherAccount;
	}
	
	/**
	 * Gets the channel type.
	 *
	 * @return the channel type
	 */
	public String getChannelType() {
		return channelType;
	}
	
	/**
	 * Sets the channel type.
	 *
	 * @param channelType the new channel type
	 */
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	/**
	 * Gets the reversal txn list.
	 *
	 * @return the reversal txn list
	 */
	public List<ReversalTransactionDTO> getReversalTxnList() {
		return reversalTxnList;
	}

	
	/**
	 * @return the custAccount
	 */
	public String getCustAccount() {
		return custAccount;
	}

	/**
	 * @param custAccount the custAccount to set
	 */
	public void setCustAccount(String custAccount) {
		this.custAccount = custAccount;
	}

	/**
	 * @return the agentAccount
	 */
	public String getAgentAccount() {
		return agentAccount;
	}

	/**
	 * @param agentAccount the agentAccount to set
	 */
	public void setAgentAccount(String agentAccount) {
		this.agentAccount = agentAccount;
	}

	/**
	 * Sets the reversal txn list.
	 *
	 * @param reversalTxnList the reversal txn list
	 */
	public void setReversalTxnList(List<ReversalTransactionDTO> reversalTxnList) {
		this.reversalTxnList = reversalTxnList;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
}
