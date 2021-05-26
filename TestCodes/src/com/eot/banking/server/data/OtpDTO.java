/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: OtpDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:33 PM Sambit Created
*/
package com.eot.banking.server.data;

// TODO: Auto-generated Javadoc
/**
 * The Class OtpDTO.
 */
public class OtpDTO {
	
	/** The reference type. */
	private int  referenceType;
	
	/** The reference id. */
	private String referenceId;
	
	/** The otphash. */
	private String otphash;
	
	/** The otp type. */
	private int otpType;
	
	private double amount;
	
	/**
	 * Gets the reference type.
	 * 
	 * @return the reference type
	 */
	public int getReferenceType() {
		return referenceType;
	}
	
	/**
	 * Sets the reference type.
	 * 
	 * @param referenceType
	 *            the new reference type
	 */
	public void setReferenceType(int referenceType) {
		this.referenceType = referenceType;
	}
	
	/**
	 * Gets the reference id.
	 * 
	 * @return the reference id
	 */
	public String getReferenceId() {
		return referenceId;
	}
	
	/**
	 * Sets the reference id.
	 * 
	 * @param referenceId
	 *            the new reference id
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
	/**
	 * Gets the otphash.
	 * 
	 * @return the otphash
	 */
	public String getOtphash() {
		return otphash;
	}
	
	/**
	 * Sets the otphash.
	 * 
	 * @param otphash
	 *            the new otphash
	 */
	public void setOtphash(String otphash) {
		this.otphash = otphash;
	}
	
	/**
	 * Gets the otp type.
	 * 
	 * @return the otp type
	 */
	public int getOtpType() {
		return otpType;
	}
	
	/**
	 * Sets the otp type.
	 * 
	 * @param otpType
	 *            the new otp type
	 */
	public void setOtpType(int otpType) {
		this.otpType = otpType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	

}
