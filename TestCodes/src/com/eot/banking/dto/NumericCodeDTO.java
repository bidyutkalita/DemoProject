package com.eot.banking.dto;

public class NumericCodeDTO extends TransactionBaseDTO{
	
	private String cbsId;
	
	private String currencyNumericCode;
	
	private String countryNumericCode;

	private String countryName;
	
	private String cityName;
	
	private String quarterName;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getQuarterName() {
		return quarterName;
	}

	public void setQuarterName(String quarterName) {
		this.quarterName = quarterName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryNumericCode() {
		return countryNumericCode;
	}

	public void setCountryNumericCode(String countryNumericCode) {
		this.countryNumericCode = countryNumericCode;
	}

	public String getCbsId() {
		return cbsId;
	}

	public void setCbsId(String cbsId) {
		this.cbsId = cbsId;
	}

	public String getCurrencyNumericCode() {
		return currencyNumericCode;
	}

	public void setCurrencyNumericCode(String currencyNumericCode) {
		this.currencyNumericCode = currencyNumericCode;
	}

}
