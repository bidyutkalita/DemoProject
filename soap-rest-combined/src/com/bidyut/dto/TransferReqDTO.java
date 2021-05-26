package com.bidyut.dto;

public class TransferReqDTO {
	
	private String username;
	private String password;
	private String code;
	private double amount;
	private String transactionID;
	private String paymentMode;
	private String transactionDate;
	private String poolAccountnumber;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getPoolAccountnumber() {
		return poolAccountnumber;
	}
	public void setPoolAccountnumber(String poolAccountnumber) {
		this.poolAccountnumber = poolAccountnumber;
	}
	@Override
	public String toString() {
		return "TransferReqDTO [username=" + username + ", password=" + password + ", code=" + code + ", amount="
				+ amount + ", transactionID=" + transactionID + ", paymentMode=" + paymentMode + ", transactionDate="
				+ transactionDate + ", poolAccountnumber=" + poolAccountnumber + "]";
	}
	
	
	
	
	


}
