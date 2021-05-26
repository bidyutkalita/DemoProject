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
public class CurrencyConverterDTO extends TransactionBaseDTO{
	
	/** The base currency id. */
	private Integer baseCurrencyId;
	
	/** The counter currency id. */
	private Integer counterCurrencyId;
	
	/** The conversion rate. */
	private Double conversionRate;
	
	/** The demonination. */
	private Double demonination;

	/**
	 * Gets the base currency id.
	 *
	 * @return the base currency id
	 */
	public Integer getBaseCurrencyId() {
		return baseCurrencyId;
	}

	/**
	 * Sets the base currency id.
	 *
	 * @param baseCurrencyId the new base currency id
	 */
	public void setBaseCurrencyId(Integer baseCurrencyId) {
		this.baseCurrencyId = baseCurrencyId;
	}

	/**
	 * Gets the counter currency id.
	 *
	 * @return the counter currency id
	 */
	public Integer getCounterCurrencyId() {
		return counterCurrencyId;
	}

	/**
	 * Sets the counter currency id.
	 *
	 * @param counterCurrencyId the new counter currency id
	 */
	public void setCounterCurrencyId(Integer counterCurrencyId) {
		this.counterCurrencyId = counterCurrencyId;
	}

	/**
	 * Gets the conversion rate.
	 *
	 * @return the conversion rate
	 */
	public Double getConversionRate() {
		return conversionRate;
	}

	/**
	 * Sets the conversion rate.
	 *
	 * @param conversionRate the new conversion rate
	 */
	public void setConversionRate(Double conversionRate) {
		this.conversionRate = conversionRate;
	}

	/**
	 * Gets the demonination.
	 *
	 * @return the demonination
	 */
	public Double getDemonination() {
		return demonination;
	}

	/**
	 * Sets the demonination.
	 *
	 * @param demonination the new demonination
	 */
	public void setDemonination(Double demonination) {
		this.demonination = demonination;
	}
	
}
