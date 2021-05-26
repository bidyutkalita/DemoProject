package com.eot.banking.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class DstvPackageDetails {
	
	 private String currency ;
	 @JsonProperty("package")
     private String pack;
     private long price;
     
     
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
     
     

}
