package com.eot.kcb.dto;

public class AccountDeLinkRequestDTO extends BaseDTO {
	
	private String txnId;
	private String bankName;
	private String branchCode;
	private String accountNumber;
	private String accountTitle;
	private String referenceID;
	private String timestamp;
	private String countryCode;
	private String authorizedMobileNumber;
	private String iMSI;
	
	
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountTitle() {
		return accountTitle;
	}
	public void setAccountTitle(String accountTitle) {
		this.accountTitle = accountTitle;
	}
	public String getReferenceID() {
		return referenceID;
	}
	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getAuthorizedMobileNumber() {
		return authorizedMobileNumber;
	}
	public void setAuthorizedMobileNumber(String authorizedMobileNumber) {
		this.authorizedMobileNumber = authorizedMobileNumber;
	}
	public String getiMSI() {
		return iMSI;
	}
	public void setiMSI(String iMSI) {
		this.iMSI = iMSI;
	}
	@Override
	public String toString() {
		return "AccountDeLinkRequestDTO [txnId=" + txnId + ", bankName=" + bankName + ", branchCode=" + branchCode
				+ ", accountNumber=" + accountNumber + ", accountTitle=" + accountTitle + ", referenceID=" + referenceID
				+ ", timestamp=" + timestamp + ", countryCode=" + countryCode + ", authorizedMobileNumber="
				+ authorizedMobileNumber + ", iMSI=" + iMSI + "]";
	}

}
