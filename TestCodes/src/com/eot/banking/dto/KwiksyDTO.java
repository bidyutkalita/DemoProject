package com.eot.banking.dto;

import java.io.Serializable;
import java.util.Date;

public class KwiksyDTO extends TransactionBaseDTO implements Serializable {
	
	private  KwiksyProvider[] providerList=null;
	private String country;
	
	
	private Long remittanceId;
    private String clientId;
    private Integer referenceType;
    private String referenceId;
    private String sender;
    private String receiver;
    private String receiverCode;
    private Double commission;
    private Integer countryCode;
    private Integer providerId;
    private String providerName;
    private String refTxnNumber;
    private Long transactionId;
    private Date remittanceDate;
    private String bankCode;
    private KwiksyExchangeRateDTO exangeRate;
    
    private String commissioncurrency;
    private Integer commissionamount;
    
 
    

	public KwiksyProvider[] getProviderList() {
		return providerList;
	}

	public void setProviderList(KwiksyProvider[] providerList) {
		this.providerList = providerList;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getRemittanceId() {
		return remittanceId;
	}

	public void setRemittanceId(Long remittanceId) {
		this.remittanceId = remittanceId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Integer getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(Integer referenceType) {
		this.referenceType = referenceType;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiverCode() {
		return receiverCode;
	}

	public void setReceiverCode(String receiverCode) {
		this.receiverCode = receiverCode;
	}


	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Integer getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getProviderId() {
		return providerId;
	}

	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getRefTxnNumber() {
		return refTxnNumber;
	}

	public void setRefTxnNumber(String refTxnNumber) {
		this.refTxnNumber = refTxnNumber;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}


	public Date getRemittanceDate() {
		return remittanceDate;
	}

	public void setRemittanceDate(Date remittanceDate) {
		this.remittanceDate = remittanceDate;
	}

	public KwiksyExchangeRateDTO getExangeRate() {
		return exangeRate;
	}

	public void setExangeRate(KwiksyExchangeRateDTO exangeRate) {
		this.exangeRate = exangeRate;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCommissioncurrency() {
		return commissioncurrency;
	}

	public void setCommissioncurrency(String commissioncurrency) {
		this.commissioncurrency = commissioncurrency;
	}

	public Integer getCommissionamount() {
		return commissionamount;
	}

	public void setCommissionamount(Integer commissionamount) {
		this.commissionamount = commissionamount;
	}



	
}
