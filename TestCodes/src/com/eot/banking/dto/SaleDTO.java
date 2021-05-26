/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SaleDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:03 PM Sambit Created
*/
package com.eot.banking.dto;

import com.eot.banking.dto.MasterDataDTO.Account;

// TODO: Auto-generated Javadoc
/**
 * The Class SaleDTO.
 */
public class SaleDTO extends TransactionBaseDTO {
	
	/** The customer account alias. */
	private String customerAccountAlias;
	
	/** The customer account alias type. */
	private Integer customerAccountAliasType;
	
	/** The customer mobile number. */
	private String customerMobileNumber;
	
	/** The merchant name. */
	private String merchantName;
	
	/** The merchant location. */
	private String merchantLocation;
	
	/** The merchant code. */
	private String merchantCode;
	
	/** The txn auth code. */
	private String txnAuthCode;
	
	private String beneficiaryMobileNumber;

	/**
	 * Gets the customer account alias.
	 * 
	 * @return the customer account alias
	 */
	public String getCustomerAccountAlias() {
		return customerAccountAlias;
	}

	/**
	 * Sets the customer account alias.
	 * 
	 * @param customerAccountAlias
	 *            the new customer account alias
	 */
	public void setCustomerAccountAlias(String customerAccountAlias) {
		this.customerAccountAlias = customerAccountAlias;
	}

	/**
	 * Gets the customer account alias type.
	 * 
	 * @return the customer account alias type
	 */
	public Integer getCustomerAccountAliasType() {
		return customerAccountAliasType;
	}

	/**
	 * Sets the customer account alias type.
	 * 
	 * @param customerAccountAliasType
	 *            the new customer account alias type
	 */
	public void setCustomerAccountAliasType(Integer customerAccountAliasType) {
		this.customerAccountAliasType = customerAccountAliasType;
	}

	/**
	 * Gets the customer mobile number.
	 * 
	 * @return the customer mobile number
	 */
	public String getCustomerMobileNumber() {
		return customerMobileNumber;
	}

	/**
	 * Sets the customer mobile number.
	 * 
	 * @param customerMobileNumber
	 *            the new customer mobile number
	 */
	public void setCustomerMobileNumber(String customerMobileNumber) {
		this.customerMobileNumber = customerMobileNumber;
	}

	/**
	 * Gets the merchant name.
	 * 
	 * @return the merchant name
	 */
	public String getMerchantName() {
		return merchantName;
	}

	/**
	 * Sets the merchant name.
	 * 
	 * @param merchantName
	 *            the new merchant name
	 */
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	/**
	 * Gets the merchant location.
	 * 
	 * @return the merchant location
	 */
	public String getMerchantLocation() {
		return merchantLocation;
	}

	/**
	 * Sets the merchant location.
	 * 
	 * @param merchantLocation
	 *            the new merchant location
	 */
	public void setMerchantLocation(String merchantLocation) {
		this.merchantLocation = merchantLocation;
	}

	/**
	 * Gets the merchant code.
	 * 
	 * @return the merchant code
	 */
	public String getMerchantCode() {
		return merchantCode;
	}

	/**
	 * Sets the merchant code.
	 * 
	 * @param merchantCode
	 *            the new merchant code
	 */
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	/**
	 * Gets the txn auth code.
	 * 
	 * @return the txn auth code
	 */
	public String getTxnAuthCode() {
		return txnAuthCode;
	}

	/**
	 * Sets the txn auth code.
	 * 
	 * @param txnAuthCode
	 *            the new txn auth code
	 */
	public void setTxnAuthCode(String txnAuthCode) {
		this.txnAuthCode = txnAuthCode;
	}

	public String getBeneficiaryMobileNumber() {
		return beneficiaryMobileNumber;
	}

	public void setBeneficiaryMobileNumber(String beneficiaryMobileNumber) {
		this.beneficiaryMobileNumber = beneficiaryMobileNumber;
	}
	
}
