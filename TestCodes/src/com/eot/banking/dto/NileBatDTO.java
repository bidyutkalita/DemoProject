package com.eot.banking.dto;

public class NileBatDTO extends TransactionBaseDTO {
	
	private Long nileBatUnit;
	
	private String beneficiaryMobileNumber;

	public Long getNileBatUnit() {
		return nileBatUnit;
	}

	public void setNileBatUnit(Long nileBatUnit) {
		this.nileBatUnit = nileBatUnit;
	}

	public String getBeneficiaryMobileNumber() {
		return beneficiaryMobileNumber;
	}

	public void setBeneficiaryMobileNumber(String beneficiaryMobileNumber) {
		this.beneficiaryMobileNumber = beneficiaryMobileNumber;
	}
	
	
	
	

}
