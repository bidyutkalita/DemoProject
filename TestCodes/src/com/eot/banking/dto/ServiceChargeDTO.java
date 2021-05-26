package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class ServiceChargeDTO.
 */
public class ServiceChargeDTO extends TransactionBaseDTO{
	 
 	/** The txn type id. */
 	private Integer txnTypeId;
	 
 	/** The app type. */
 	private String applicationType;
	 
 	/** The txn banking type. */
 	private Integer txnBankingType;
	 
 	/** The amount. */
 	private Double txnAmount;
 	
 	/** The service charge. */
	 
 	/**
 	 * Gets the txn type id.
 	 *
 	 * @return the txn type id
 	 */
 	public Integer getTxnTypeId() {
		return txnTypeId;
	}
	
	/**
	 * Sets the txn type id.
	 *
	 * @param txnTypeId the new txn type id
	 */
	public void setTxnTypeId(Integer txnTypeId) {
		this.txnTypeId = txnTypeId;
	}
	
	
	/**
	 * Gets the txn banking type.
	 *
	 * @return the txn banking type
	 */
	public Integer getTxnBankingType() {
		return txnBankingType;
	}
	
	/**
	 * Sets the txn banking type.
	 *
	 * @param txnBankingType the new txn banking type
	 */
	public void setTxnBankingType(Integer txnBankingType) {
		this.txnBankingType = txnBankingType;
	}
	
	
	/**
	 * Gets the service charge.
	 *
	 * @return the service charge
	 */

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public Double getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(Double txnAmount) {
		this.txnAmount = txnAmount;
	}
	
	

	
}
