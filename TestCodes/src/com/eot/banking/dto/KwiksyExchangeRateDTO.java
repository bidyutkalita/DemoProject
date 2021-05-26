package com.eot.banking.dto;


public class KwiksyExchangeRateDTO {

	private String sender_currency;
	private String receiver_currency;
	private String rate;
	private String responseStatus;
	private String responseMessage;
	private String responseCode;
	
	
	public String getSender_currency() {
		return sender_currency;
	}
	public void setSender_currency(String sender_currency) {
		this.sender_currency = sender_currency;
	}
	public String getReceiver_currency() {
		return receiver_currency;
	}
	public void setReceiver_currency(String receiver_currency) {
		this.receiver_currency = receiver_currency;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	
	
}
