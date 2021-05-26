package com.eot.kcb.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bank_account_mapping")
public class BankAccountMapping {
	@Id
	@GeneratedValue
	private Integer  id;
	private String accountNumber;
	private String mobileNumber;
	private String accountType;
	private String linkedStatus;
	private String accountStatus;
	private String iMSI;
	private String iDType;
	private String iDNumber;
	private String nationality;
	private String bankName;
	private String branchCode;
	private String countryCode;
	private String referenceID;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getLinkedStatus() {
		return linkedStatus;
	}
	public void setLinkedStatus(String linkedStatus) {
		this.linkedStatus = linkedStatus;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public String getiMSI() {
		return iMSI;
	}
	public void setiMSI(String iMSI) {
		this.iMSI = iMSI;
	}
	public String getiDType() {
		return iDType;
	}
	public void setiDType(String iDType) {
		this.iDType = iDType;
	}
	public String getiDNumber() {
		return iDNumber;
	}
	public void setiDNumber(String iDNumber) {
		this.iDNumber = iDNumber;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getReferenceID() {
		return referenceID;
	}
	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}
	
	
	
}
