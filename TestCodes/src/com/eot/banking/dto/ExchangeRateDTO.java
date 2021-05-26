/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ExchangeRateDTO.java
*
* Date Author Changes
* 19 May, 2016 Swadhin Created
*/
package com.eot.banking.dto;

/**
 * The Class ExchangeRateDTO.
 */
public class ExchangeRateDTO extends TransactionBaseDTO{
	
	/** The currency id. */
	private Integer currencyId;
	
	/** The selling rate. */
	private Double sellingRate;
	
	/** The buying rate. */
	private Double buyingRate;
	
	/**
	 * Gets the currency id.
	 *
	 * @return the currency id
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}

	/**
	 * Sets the currency id.
	 *
	 * @param currencyId the new currency id
	 */
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	/**
	 * Gets the selling rate.
	 *
	 * @return the selling rate
	 */
	public Double getSellingRate() {
		return sellingRate;
	}

	/**
	 * Sets the selling rate.
	 *
	 * @param sellingRate the new selling rate
	 */
	public void setSellingRate(Double sellingRate) {
		this.sellingRate = sellingRate;
	}

	/**
	 * Gets the buying rate.
	 *
	 * @return the buying rate
	 */
	public Double getBuyingRate() {
		return buyingRate;
	}

	/**
	 * Sets the buying rate.
	 *
	 * @param buyingRate the new buying rate
	 */
	public void setBuyingRate(Double buyingRate) {
		this.buyingRate = buyingRate;
	}

}
