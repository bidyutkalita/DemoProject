package com.eot.dto;

public class TransferRespDTO {
	
	private String responseCode;
	private String message;
	
	
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "TransferRespDTO [responseCode=" + responseCode + ", message=" + message + "]";
	}
	
	
	

}
