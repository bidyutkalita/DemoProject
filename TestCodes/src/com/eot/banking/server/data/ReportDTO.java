/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ReportDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:36 PM Sambit Created
*/
package com.eot.banking.server.data;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class ReportDTO.
 */
public class ReportDTO {
	
	/** The country id. */
	private Long countryId;
	
	/** The bank id. */
	private Long bankId;
	
	/** The from date. */
	private Date fromDate;
	
	/** The to date. */
	private Date toDate;
	
	/**
	 * Gets the country id.
	 * 
	 * @return the country id
	 */
	public Long getCountryId() {
		return countryId;
	}
	
	/**
	 * Sets the country id.
	 * 
	 * @param countryId
	 *            the new country id
	 */
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	
	/**
	 * Gets the bank id.
	 * 
	 * @return the bank id
	 */
	public Long getBankId() {
		return bankId;
	}
	
	/**
	 * Sets the bank id.
	 * 
	 * @param bankId
	 *            the new bank id
	 */
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	
	/**
	 * Gets the from date.
	 * 
	 * @return the from date
	 */
	public Date getFromDate() {
		return fromDate;
	}
	
	/**
	 * Sets the from date.
	 * 
	 * @param fromDate
	 *            the new from date
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	/**
	 * Gets the to date.
	 * 
	 * @return the to date
	 */
	public Date getToDate() {
		return toDate;
	}
	
	/**
	 * Sets the to date.
	 * 
	 * @param toDate
	 *            the new to date
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
}
