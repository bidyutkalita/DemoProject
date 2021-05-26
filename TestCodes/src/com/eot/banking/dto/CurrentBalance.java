package com.eot.banking.dto;


public class CurrentBalance   extends TransactionBaseDTO {
	
	private String currentBalance;
	private String commission;
	private String organizationName;
	private Integer todayCount;
	private Integer totalCount;
	private Integer pendingCount;
	
	public Integer getPendingCount() {
		return pendingCount;
	}
	public void setPendingCount(Integer pendingCount) {
		this.pendingCount = pendingCount;
	}
	public Integer getTodayCount() {
		return todayCount;
	}
	public void setTodayCount(Integer todayCount) {
		this.todayCount = todayCount;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	
	
	public String getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	
	
	
	
	

}
