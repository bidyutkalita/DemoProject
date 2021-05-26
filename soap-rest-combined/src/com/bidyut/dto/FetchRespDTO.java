package com.bidyut.dto;

public class FetchRespDTO {
	
	private String balance ;
	private String Code;
	private String createdOn;
	private String currencyCode;
	private String name;
	private String mobileNumber;
	private String address;
	private String type;
	private String responseCode;
	private String remarks;
	private String message;
	
	
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "FetchRespDTO [balance=" + balance + ", Code=" + Code + ", createdOn=" + createdOn + ", currencyCode="
				+ currencyCode + ", name=" + name + ", mobileNumber=" + mobileNumber + ", address=" + address
				+ ", type=" + type + ", responseCode=" + responseCode + ", remarks=" + remarks + ", message=" + message
				+ "]";
	}
	
	

	
}