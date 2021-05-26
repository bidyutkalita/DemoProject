/* Copyright © EasOfTech 2016. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SCRuleDTO.java
*
* Date Author Changes
* 24 Jan, 2017 Swadhin Created
*/
package com.eot.banking.dto;

/**
 * The Class SCRuleDTO.
 */
public class SCRuleDTO {
	
	/** The rule type. */
	Integer ruleType;
	
	/** The service charge rule id. */
	Long serviceChargeRuleId;
	
	/** The applicable from. */
	Long applicableFrom;
	
	/** The applicable to. */
	Long applicableTo;
	
	/** The applicable day. */
	Integer applicableDay;
	
	/** The applciable from HH. */
	Integer applciableFromHH;
	
	/** The applicable to HH. */
	Integer applicableToHH;
	
	/** The service charge rule value id. */
	Integer serviceChargeRuleValueId;
	
	/** The service charge pct. */
	Float serviceChargePct;
	
	/** The service charge fxd. */
	Long serviceChargeFxd;
	
	/** The discount limit. */
	Long discountLimit;
	
	/** The min service charge. */
	Long minServiceCharge;
	
	/** The max service charge. */
	Long maxServiceCharge;
	
	/** The min txn value. */
	Long minTxnValue;
	
	/** The max txn value. */
	Long maxTxnValue;
	
	/** The time zone rule. */
	Integer timeZoneRule;
	
	/** The imposed on. */
	Integer imposedOn;
	
	/** The transaction type. */
	Integer transactionType;
	
	/**
	 * Gets the rule type.
	 *
	 * @return the rule type
	 */
	public Integer getRuleType() {
		return ruleType;
	}
	
	/**
	 * Sets the rule type.
	 *
	 * @param ruleType the new rule type
	 */
	public void setRuleType(Integer ruleType) {
		this.ruleType = ruleType;
	}
	
	/**
	 * Gets the service charge rule id.
	 *
	 * @return the service charge rule id
	 */
	public Long getServiceChargeRuleId() {
		return serviceChargeRuleId;
	}
	
	/**
	 * Sets the service charge rule id.
	 *
	 * @param serviceChargeRuleId the new service charge rule id
	 */
	public void setServiceChargeRuleId(Long serviceChargeRuleId) {
		this.serviceChargeRuleId = serviceChargeRuleId;
	}
	
	/**
	 * Gets the service charge rule value id.
	 *
	 * @return the service charge rule value id
	 */
	public Integer getServiceChargeRuleValueId() {
		return serviceChargeRuleValueId;
	}
	
	/**
	 * Sets the service charge rule value id.
	 *
	 * @param serviceChargeRuleValueId the new service charge rule value id
	 */
	public void setServiceChargeRuleValueId(Integer serviceChargeRuleValueId) {
		this.serviceChargeRuleValueId = serviceChargeRuleValueId;
	}
	
	/**
	 * Gets the applicable from.
	 *
	 * @return the applicable from
	 */
	public Long getApplicableFrom() {
		return applicableFrom;
	}
	
	/**
	 * Sets the applicable from.
	 *
	 * @param applicableFrom the new applicable from
	 */
	public void setApplicableFrom(Long applicableFrom) {
		this.applicableFrom = applicableFrom;
	}
	
	/**
	 * Gets the applicable to.
	 *
	 * @return the applicable to
	 */
	public Long getApplicableTo() {
		return applicableTo;
	}
	
	/**
	 * Sets the applicable to.
	 *
	 * @param applicableTo the new applicable to
	 */
	public void setApplicableTo(Long applicableTo) {
		this.applicableTo = applicableTo;
	}
	
	/**
	 * Gets the applicable day.
	 *
	 * @return the applicable day
	 */
	public Integer getApplicableDay() {
		return applicableDay;
	}	
	/**
	 * Sets the applicable day.
	 *
	 * @param applicableDay the new applicable day
	 */
	public void setApplicableDay(Integer applicableDay) {
		this.applicableDay = applicableDay;
	}
	
	/**
	 * Gets the applciable from HH.
	 *
	 * @return the applciable from HH
	 */
	public Integer getApplciableFromHH() {
		return applciableFromHH;
	}
	
	/**
	 * Sets the applciable from HH.
	 *
	 * @param applciableFromHH the new applciable from HH
	 */
	public void setApplciableFromHH(Integer applciableFromHH) {
		this.applciableFromHH = applciableFromHH;
	}
	
	/**
	 * Gets the applicable to HH.
	 *
	 * @return the applicable to HH
	 */
	public Integer getApplicableToHH() {
		return applicableToHH;
	}
	
	/**
	 * Sets the applicable to HH.
	 *
	 * @param applicableToHH the new applicable to HH
	 */
	public void setApplicableToHH(Integer applicableToHH) {
		this.applicableToHH = applicableToHH;	}
	
	/**
	 * Gets the service charge pct.
	 *
	 * @return the service charge pct
	 */
	public Float getServiceChargePct() {
		return serviceChargePct;
	}
	
	/**
	 * Sets the service charge pct.
	 *
	 * @param serviceChargePct the new service charge pct
	 */
	public void setServiceChargePct(Float serviceChargePct) {
		this.serviceChargePct = serviceChargePct;
	}
	
	/**
	 * Gets the service charge fxd.
	 *
	 * @return the service charge fxd
	 */
	public Long getServiceChargeFxd() {
		return serviceChargeFxd;
	}
	
	/**
	 * Sets the service charge fxd.
	 *
	 * @param serviceChargeFxd the new service charge fxd
	 */
	public void setServiceChargeFxd(Long serviceChargeFxd) {
		this.serviceChargeFxd = serviceChargeFxd;
	}
	
	/**
	 * Gets the discount limit.
	 *
	 * @return the discount limit
	 */
	public Long getDiscountLimit() {
		return discountLimit;
	}
	
	/**
	 * Sets the discount limit.
	 *
	 * @param discountLimit the new discount limit
	 */
	public void setDiscountLimit(Long discountLimit) {
		this.discountLimit = discountLimit;
	}
	
	/**
	 * Gets the min service charge.
	 *
	 * @return the min service charge
	 */
	public Long getMinServiceCharge() {
		return minServiceCharge;
	}
	
	/**
	 * Gets the transaction type.
	 *
	 * @return the transaction type
	 */
	public Integer getTransactionType() {
		return transactionType;
	}
	
	/**
	 * Sets the transaction type.
	 *
	 * @param transactionType the new transaction type
	 */
	public void setTransactionType(Integer transactionType) {
		this.transactionType = transactionType;
	}
	
	/**
	 * Sets the min service charge.
	 *
	 * @param minServiceCharge the new min service charge
	 */
	public void setMinServiceCharge(Long minServiceCharge) {
		this.minServiceCharge = minServiceCharge;
	}
	
	/**
	 * Gets the max service charge.
	 *
	 * @return the max service charge
	 */
	public Long getMaxServiceCharge() {
		return maxServiceCharge;
	}
	
	/**
	 * Sets the max service charge.
	 *
	 * @param maxServiceCharge the new max service charge
	 */
	public void setMaxServiceCharge(Long maxServiceCharge) {
		this.maxServiceCharge = maxServiceCharge;
	}
	
	/**
	 * Gets the min txn value.
	 *
	 * @return the min txn value
	 */
	public Long getMinTxnValue() {
		return minTxnValue;
	}
	
	/**
	 * Sets the min txn value.
	 *
	 * @param minTxnValue the new min txn value
	 */
	public void setMinTxnValue(Long minTxnValue) {
		this.minTxnValue = minTxnValue;
	}
	
	/**
	 * Gets the max txn value.
	 *
	 * @return the max txn value
	 */
	public Long getMaxTxnValue() {
		return maxTxnValue;
	}
	
	/**
	 * Sets the max txn value.
	 *
	 * @param maxTxnValue the new max txn value
	 */
	public void setMaxTxnValue(Long maxTxnValue) {
		this.maxTxnValue = maxTxnValue;
	}
	
	/**
	 * Gets the time zone rule.
	 *
	 * @return the time zone rule
	 */
	public Integer getTimeZoneRule() {
		return timeZoneRule;
	}
	
	/**
	 * Sets the time zone rule.
	 *
	 * @param timeZoneRule the new time zone rule
	 */
	public void setTimeZoneRule(Integer timeZoneRule) {
		this.timeZoneRule = timeZoneRule;
	}
	
	/**
	 * Gets the imposed on.
	 *
	 * @return the imposed on
	 */
	public Integer getImposedOn() {
		return imposedOn;
	}
	
	/**
	 * Sets the imposed on.
	 *
	 * @param imposedOn the new imposed on
	 */
	public void setImposedOn(Integer imposedOn) {
		this.imposedOn = imposedOn;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer buff = new StringBuffer();
		buff.append(ruleType+" ");
		buff.append(serviceChargeRuleId+" ");
		buff.append(applicableFrom+" ");
		buff.append(applicableTo+" ");
		buff.append(applicableDay+" ");
		buff.append(serviceChargeRuleValueId+" ");
		buff.append(applciableFromHH+" ");
	    buff.append(applicableToHH+" ");
		buff.append(serviceChargePct+" ");
		buff.append(serviceChargeFxd+" ");
		buff.append(discountLimit+" ");
		buff.append(minServiceCharge+" ");
		buff.append(maxServiceCharge+" ");
		buff.append(minTxnValue+" ");
		buff.append(maxTxnValue+" ");
		buff.append(timeZoneRule+" ");
		buff.append(imposedOn);
		
		return buff.toString();
	}
	
}
