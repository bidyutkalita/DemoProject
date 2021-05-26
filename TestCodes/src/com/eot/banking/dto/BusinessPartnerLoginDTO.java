package com.eot.banking.dto;

public class BusinessPartnerLoginDTO extends TransactionBaseDTO {

	private String userName;
	private String password;
	private String businessPartnerName;
	private Integer userStatus;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getBusinessPartnerName() {
		return businessPartnerName;
	}
	public void setBusinessPartnerName(String businessPartnerName) {
		this.businessPartnerName = businessPartnerName;
	}
	public Integer getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}
}
