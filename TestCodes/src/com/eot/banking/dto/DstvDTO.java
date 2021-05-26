package com.eot.banking.dto;

import java.util.List;


public class DstvDTO  extends TransactionBaseDTO
{
	private List<DstvPackageDetails> data;
	
	
	private String cardSerialNo;
	
	private String packageName;
	

	public String getCardSerialNo() {
		return cardSerialNo;
	}
	public void setCardSerialNo(String cardSerialNo) {
		this.cardSerialNo = cardSerialNo;
	}
	public List<DstvPackageDetails> getData() {
		return data;
	}
	public void setData(List<DstvPackageDetails> data) {
		this.data = data;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	

	
	
	
}
