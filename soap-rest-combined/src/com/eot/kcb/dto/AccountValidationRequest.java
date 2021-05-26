package com.eot.kcb.dto;

public class AccountValidationRequest extends BaseDTO {
	private String txnUserId;
	private String txnPassword;
	private String txnBankName;
	private String txnAccountNumbar;
	private String txnRefId;
	private String txnDate;
	
	
	public String getTxnUserId() {
		return txnUserId;
	}
	public void setTxnUserId(String txnUserId) {
		this.txnUserId = txnUserId;
	}
	public String getTxnPassword() {
		return txnPassword;
	}
	public void setTxnPassword(String txnPassword) {
		this.txnPassword = txnPassword;
	}
	public String getTxnBankName() {
		return txnBankName;
	}
	public void setTxnBankName(String txnBankName) {
		this.txnBankName = txnBankName;
	}
	public String getTxnAccountNumbar() {
		return txnAccountNumbar;
	}
	public void setTxnAccountNumbar(String txnAccountNumbar) {
		this.txnAccountNumbar = txnAccountNumbar;
	}
	public String getTxnRefId() {
		return txnRefId;
	}
	public void setTxnRefId(String txnRefId) {
		this.txnRefId = txnRefId;
	}
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	@Override
	public String toString() {
		return "AccountValidationRequest [txnUserId=" + txnUserId + ", txnPassword=" + txnPassword + ", txnBankName="
				+ txnBankName + ", txnAccountNumbar=" + txnAccountNumbar + ", txnRefId=" + txnRefId + ", txnDate="
				+ txnDate + "]";
	}

	
	
}
