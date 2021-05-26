package com.eot.kcb.dto;

public class AccountValidationResponse {
	
	private String statusDescription;
	private String statusCode;
	private String accountNumber;
	private String accountTitle;
	private String accountStatus;
	private String accountTypes;
	private String transactionID;
	private String linkedStatus;
	private String transactionTimeStamp;
	private int status;
	
	
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
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
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public String getAccountTypes() {
		return accountTypes;
	}
	public void setAccountTypes(String accountTypes) {
		this.accountTypes = accountTypes;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getLinkedStatus() {
		return linkedStatus;
	}
	public void setLinkedStatus(String linkedStatus) {
		this.linkedStatus = linkedStatus;
	}
	public String getTransactionTimeStamp() {
		return transactionTimeStamp;
	}
	public void setTransactionTimeStamp(String transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "AccountValidationResponse [statusDescription=" + statusDescription + ", statusCode=" + statusCode
				+ ", accountNumber=" + accountNumber + ", accountTitle=" + accountTitle + ", accountStatus="
				+ accountStatus + ", accountTypes=" + accountTypes + ", transactionID=" + transactionID
				+ ", linkedStatus=" + linkedStatus + ", transactionTimeStamp=" + transactionTimeStamp + ", status="
				+ status + "]";
	}
	
	

}
