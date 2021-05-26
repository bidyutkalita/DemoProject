package com.eot.banking.dto;

import java.util.List;

import com.eot.entity.Transaction;

public class WithdrawalTransactionsDTO extends TransactionBaseDTO {
	private String imgUrl;
	private String agentCode;
	private String name;
	private String merchantCode;
	private Integer isApproved;
	private String businessName;
	
	private List<ReversalTransactionDTO> withdrawTransactions;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the withdrawTransactions
	 */
	public List<ReversalTransactionDTO> getWithdrawTransactions() {
		return withdrawTransactions;
	}

	
	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	/**
	 * @param withdrawTransactions the withdrawTransactions to set
	 */
	public void setWithdrawTransactions(List<ReversalTransactionDTO> withdrawTransactions) {
		this.withdrawTransactions = withdrawTransactions;
	}

	public Integer getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Integer isApproved) {
		this.isApproved = isApproved;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
}
