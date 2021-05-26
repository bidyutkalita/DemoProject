/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SMSCashDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:05 PM Sambit Created
*/
package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class SMSCashDTO.
 */
public class SMSCashDTO extends TransactionBaseDTO {

	/** The sms cash id. */
	private Long smsCashId;
	
	/** The payee mobile number. */
	private String payeeMobileNumber;
	
	/** The sms cash pin. */
	private String smsCashPIN;
	
	/** The withdrawal mode. */
	private Integer withdrawalMode;
	
	/** The status flag. */
	private Integer statusFlag;
	
	/** The beneficiary amount. */
	private Double beneficiaryAmount;
	
	private Integer pinGenerationMode ; 
	
	private String smsCashDate;
	
	private Integer isRegistered = 0;
	
	/** The list of SMS cash details. */
	private List<SMSCashDTO> listOfSMSCashDetails = new ArrayList<SMSCashDTO>();

	/**
	 * Gets the payee mobile number.
	 * 
	 * @return the payee mobile number
	 */
	public String getPayeeMobileNumber() {
		return payeeMobileNumber;
	}

	/**
	 * Sets the payee mobile number.
	 * 
	 * @param payeeMobileNumber
	 *            the new payee mobile number
	 */
	public void setPayeeMobileNumber(String payeeMobileNumber) {
		this.payeeMobileNumber = payeeMobileNumber;
	}

	/**
	 * Gets the sms cash pin.
	 * 
	 * @return the sms cash pin
	 */
	public String getSmsCashPIN() {
		return smsCashPIN;
	}

	/**
	 * Sets the sms cash pin.
	 * 
	 * @param smsCashPIN
	 *            the new sms cash pin
	 */
	public void setSmsCashPIN(String smsCashPIN) {
		this.smsCashPIN = smsCashPIN;
	}

	/**
	 * Gets the list of SMS cash details.
	 *
	 * @return the list of SMS cash details
	 */
	public List<SMSCashDTO> getListOfSMSCashDetails() {
		return listOfSMSCashDetails;
	}

	/**
	 * Sets the list of SMS cash details.
	 *
	 * @param listOfSMSCashDetails the new list of SMS cash details
	 */
	public void setListOfSMSCashDetails(List<SMSCashDTO> listOfSMSCashDetails) {
		this.listOfSMSCashDetails = listOfSMSCashDetails;
	}

	/**
	 * Gets the sms cash id.
	 *
	 * @return the sms cash id
	 */
	public Long getSmsCashId() {
		return smsCashId;
	}

	/**
	 * Sets the sms cash id.
	 *
	 * @param smsCashId the new sms cash id
	 */
	public void setSmsCashId(Long smsCashId) {
		this.smsCashId = smsCashId;
	}

	/**
	 * Gets the withdrawal mode.
	 *
	 * @return the withdrawal mode
	 */
	public Integer getWithdrawalMode() {
		return withdrawalMode;
	}

	/**
	 * Sets the withdrawal mode.
	 *
	 * @param withdrawalMode the new withdrawal mode
	 */
	public void setWithdrawalMode(Integer withdrawalMode) {
		this.withdrawalMode = withdrawalMode;
	}

	/**
	 * Gets the status flag.
	 *
	 * @return the status flag
	 */
	public Integer getStatusFlag() {
		return statusFlag;
	}

	/**
	 * Sets the status flag.
	 *
	 * @param statusFlag the new status flag
	 */
	public void setStatusFlag(Integer statusFlag) {
		this.statusFlag = statusFlag;
	}

	/**
	 * Gets the beneficiary amount.
	 *
	 * @return the beneficiary amount
	 */
	public Double getBeneficiaryAmount() {
		return beneficiaryAmount;
	}

	/**
	 * Sets the beneficiary amount.
	 *
	 * @param beneficiaryAmount the new beneficiary amount
	 */
	public void setBeneficiaryAmount(Double beneficiaryAmount) {
		this.beneficiaryAmount = beneficiaryAmount;
	}

	public Integer getPinGenerationMode() {
		return pinGenerationMode;
	}

	public void setPinGenerationMode(Integer pinGenerationMode) {
		this.pinGenerationMode = pinGenerationMode;
	}

	public String getSmsCashDate() {
		return smsCashDate;
	}

	public void setSmsCashDate(String smsCashDate) {
		this.smsCashDate = smsCashDate;
	}

	public Integer getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(Integer isRegistered) {
		this.isRegistered = isRegistered;
	}


}
