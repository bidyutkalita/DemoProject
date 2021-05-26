package com.eot.banking.dto;

public class ConfirmPinDTO extends TransactionBaseDTO{
	
	private String newPin;
	
	private String otp;

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}	
}
