package com.eot.banking.dto;

import java.util.List;

public class CityDTO {
	
	private Integer cityId;
	
	private  String cityName;
	
	private List<QuarterDTO> quarterList;

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public List<QuarterDTO> getQuarterList() {
		return quarterList;
	}

	public void setQuarterList(List<QuarterDTO> quarterList) {
		this.quarterList = quarterList;
	}
	
}
