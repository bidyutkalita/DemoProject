package com.eot.banking.dto;

public class UploadCustomerDocument extends TransactionBaseDTO{
	
	private String dataName;
	private String data;
	private boolean customerExist;
	
	private boolean dataUploaded;
	
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public boolean isDataUploaded() {
		return dataUploaded;
	}
	public void setDataUploaded(boolean dataUploaded) {
		this.dataUploaded = dataUploaded;
	}
	public boolean isCustomerExist() {
		return customerExist;
	}
	public void setCustomerExist(boolean customerExist) {
		this.customerExist = customerExist;
	}


	
	
	

}
