package com.eot.banking.dto;

import java.util.List;

public class CountryDTO {

	private Integer countryId;
	
	private String countryName;
	
	private String countryCodeAlpha2;
	
	private String countryCodeAlpha3;
	
	private Integer countryCodeNumeric;
	
	private Integer isdCode; 
	
	private Integer mobileNumberLength;
	
	private List<CityDTO> cityList;

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public List<CityDTO> getCityList() {
		return cityList;
	}

	public void setCityList(List<CityDTO> cityList) {
		this.cityList = cityList;
	}

	public String getCountryCodeAlpha2() {
		return countryCodeAlpha2;
	}

	public void setCountryCodeAlpha2(String countryCodeAlpha2) {
		this.countryCodeAlpha2 = countryCodeAlpha2;
	}

	public String getCountryCodeAlpha3() {
		return countryCodeAlpha3;
	}

	public void setCountryCodeAlpha3(String countryCodeAlpha3) {
		this.countryCodeAlpha3 = countryCodeAlpha3;
	}

	public Integer getCountryCodeNumeric() {
		return countryCodeNumeric;
	}

	public void setCountryCodeNumeric(Integer countryCodeNumeric) {
		this.countryCodeNumeric = countryCodeNumeric;
	}

	public Integer getIsdCode() {
		return isdCode;
	}

	public void setIsdCode(Integer isdCode) {
		this.isdCode = isdCode;
	}

	public Integer getMobileNumberLength() {
		return mobileNumberLength;
	}

	public void setMobileNumberLength(Integer mobileNumberLength) {
		this.mobileNumberLength = mobileNumberLength;
	}
	
}
