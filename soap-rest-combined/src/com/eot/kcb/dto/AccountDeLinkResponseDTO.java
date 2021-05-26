package com.eot.kcb.dto;

public class AccountDeLinkResponseDTO  extends BaseDTO {
	

	private String statusCode;
	private String txnId;
	private String IMSI;
	private String reference;
	private String Timestamp;
	private String statusDescription;
	
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getIMSI() {
		return IMSI;
	}
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getTimestamp() {
		return Timestamp;
	}
	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	@Override
	public String toString() {
		return "AccountDeLinkResponseDTO [statusCode=" + statusCode + ", txnId=" + txnId + ", IMSI=" + IMSI
				+ ", reference=" + reference + ", Timestamp=" + Timestamp + ", statusDescription=" + statusDescription
				+ "]";
	}
	
	

}
