package com.eot.banking.dto;

import java.util.List;

public class MasterDataSelfOnboard extends TransactionBaseDTO{
	
	private List<KycTypeDTO> kycTypeList;
	
	private List<CountryDTO> countryList;
	private List<TitleDTO> titleList;

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



	
}
