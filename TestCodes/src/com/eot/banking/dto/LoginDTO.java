package com.eot.banking.dto;

public class LoginDTO extends TransactionBaseDTO {
	
	private String password;
	
	private int userType;
	
	private int applicationStatus;
	
	private int customerStatus;
	
	private boolean is_logged;
	
	private String agentCode;

	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public int getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(int applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public int getCustomerStatus() {
		return customerStatus;
	}
	public void setCustomerStatus(int customerStatus) {
		this.customerStatus = customerStatus;
	}
	public boolean isIs_logged() {
		return is_logged;
	}
	public void setIs_logged(boolean is_logged) {
		this.is_logged = is_logged;
	}
	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
}
