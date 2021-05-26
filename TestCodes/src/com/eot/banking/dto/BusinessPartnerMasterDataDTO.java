package com.eot.banking.dto;

import java.util.List;

import com.eot.entity.SecurityQuestion;

public class BusinessPartnerMasterDataDTO extends TransactionBaseDTO{
	
	private List<KycTypeDTO> kycTypeList;
	
	private List<CountryDTO> countryList;
	
	private List<TitleDTO> titleList;
	
	private List<CustomerTypeDTO> customerTypeList;
	
	private List<LanguageDTO> languages;
	
	private List<SecurityQuestionDTO> securityQuestions;
	
	public List<KycTypeDTO> getKycTypeList() {
		return kycTypeList;
	}

	public void setKycTypeList(List<KycTypeDTO> kycTypeList) {
		this.kycTypeList = kycTypeList;
	}

	public List<CountryDTO> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<CountryDTO> countryList) {
		this.countryList = countryList;
	}

	public List<TitleDTO> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<TitleDTO> titleList) {
		this.titleList = titleList;
	}

	public List<CustomerTypeDTO> getCustomerTypeList() {
		return customerTypeList;
	}

	public void setCustomerTypeList(List<CustomerTypeDTO> customerTypeList) {
		this.customerTypeList = customerTypeList;
	}

	public List<LanguageDTO> getLanguages() {
		return languages;
	}

	public void setLanguages(List<LanguageDTO> languages) {
		this.languages = languages;
	}

	public List<SecurityQuestionDTO> getSecurityQuestions() {
		return securityQuestions;
	}

	public void setSecurityQuestions(List<SecurityQuestionDTO> securityQuestions) {
		this.securityQuestions = securityQuestions;
	}

	
	
}
