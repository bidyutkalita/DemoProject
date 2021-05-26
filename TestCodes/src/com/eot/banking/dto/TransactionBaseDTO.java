/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: TransactionBaseDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:11 PM Sambit Created
*/
package com.eot.banking.dto;

import java.util.Date;
import java.util.List;

import com.eot.entity.AppMaster;
import com.eot.entity.Customer;
import com.eot.entity.MobileRequest;
import com.eot.entity.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class TransactionBaseDTO.
 */
/**
 * @author K Vineeth
 *
 */
public class TransactionBaseDTO extends BaseDTO {

	/** The account alias. */
	private String accountAlias;

	/** The mobile number. */
	private String mobileNumber;

	/** The account number. */
	private String accountNumber;

	/** The account balance. */
	private Double accountBalance;

	/** The customer name. */
	private String customerName;

	/** The alias type. */
	private Integer aliasType;

	/** The transmission time. */
	private Long transmissionTime;

	/** The transaction time. */
	private Long transactionTime;

	/** The stan. */
	private Long stan;

	/** The rrn. */
	private Long rrn;

	/** The activation pin. */
	private String activationPIN;

	/** The transaction pin. */
	private String transactionPIN;

	/** The customer otp. */
	private String customerOTP;
	
	/** The service charge amt. */
	private Double serviceChargeAmt;

	/** The amount. */
	private Double amount;

	/** The remarks. */
	private String remarks;

	/** The app type. */
	private Integer appType;

	

	private String username;

	private Customer customer;






	

	

	
	
	private MobileRequest mobileRequest;

	private AppMaster appMaster;





	private Double serviceCharge;

	public Double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(Double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	

	/**
	 * Gets the transmission time.
	 *
	 * @return the transmission time
	 */
	public Long getTransmissionTime() {
		return transmissionTime;
	}

	/**
	 * Sets the transmission time.
	 *
	 * @param transmissionTime
	 *            the new transmission time
	 */
	public void setTransmissionTime(Long transmissionTime) {
		this.transmissionTime = transmissionTime;
	}

	/**
	 * Gets the transaction time.
	 *
	 * @return the transaction time
	 */
	public Long getTransactionTime() {
		return transactionTime;
	}

	/**
	 * Sets the transaction time.
	 *
	 * @param transactionTime
	 *            the new transaction time
	 */
	public void setTransactionTime(Long transactionTime) {
		this.transactionTime = transactionTime;
	}

	/**
	 * Gets the stan.
	 *
	 * @return the stan
	 */
	public Long getStan() {
		return stan;
	}

	/**
	 * Sets the stan.
	 *
	 * @param stan
	 *            the new stan
	 */
	public void setStan(Long stan) {
		this.stan = stan;
	}

	/**
	 * Gets the rrn.
	 *
	 * @return the rrn
	 */
	public Long getRrn() {
		return rrn;
	}

	/**
	 * Sets the rrn.
	 *
	 * @param rrn
	 *            the new rrn
	 */
	public void setRrn(Long rrn) {
		this.rrn = rrn;
	}

	/**
	 * Gets the account alias.
	 *
	 * @return the account alias
	 */
	public String getAccountAlias() {
		return accountAlias;
	}

	/**
	 * Sets the account alias.
	 *
	 * @param accountAlias
	 *            the new account alias
	 */
	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	/**
	 * Gets the alias type.
	 *
	 * @return the alias type
	 */
	public Integer getAliasType() {
		return aliasType;
	}

	/**
	 * Sets the alias type.
	 *
	 * @param aliasType
	 *            the new alias type
	 */
	public void setAliasType(Integer aliasType) {
		this.aliasType = aliasType;
	}

	/**
	 * Gets the transaction pin.
	 *
	 * @return the transaction pin
	 */
	public String getTransactionPIN() {
		return transactionPIN;
	}

	/**
	 * Sets the transaction pin.
	 *
	 * @param transactionPIN
	 *            the new transaction pin
	 */
	public void setTransactionPIN(String transactionPIN) {
		this.transactionPIN = transactionPIN;
	}

	/**
	 * Gets the activation pin.
	 *
	 * @return the activation pin
	 */
	public String getActivationPIN() {
		return activationPIN;
	}

	/**
	 * Sets the activation pin.
	 *
	 * @param activationPIN
	 *            the new activation pin
	 */
	public void setActivationPIN(String activationPIN) {
		this.activationPIN = activationPIN;
	}

	/**
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 *
	 * @param amount
	 *            the new amount
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * Gets the mobile number.
	 *
	 * @return the mobile number
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * Sets the mobile number.
	 *
	 * @param mobileNumber
	 *            the new mobile number
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * Gets the customer otp.
	 *
	 * @return the customer otp
	 */
	public String getCustomerOTP() {
		return customerOTP;
	}

	/**
	 * Sets the customer otp.
	 *
	 * @param customerOTP
	 *            the new customer otp
	 */
	public void setCustomerOTP(String customerOTP) {
		this.customerOTP = customerOTP;
	}

	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Sets the customer name.
	 *
	 * @param customerName
	 *            the new customer name
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * Gets the account number.
	 *
	 * @return the account number
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * Sets the account number.
	 *
	 * @param accountNumber
	 *            the new account number
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Gets the account balance.
	 *
	 * @return the account balance
	 */
	public Double getAccountBalance() {
		return accountBalance;
	}

	/**
	 * Sets the account balance.
	 *
	 * @param accountBalance
	 *            the new account balance
	 */
	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}

	/**
	 * Gets the service charge amt.
	 *
	 * @return the service charge amt
	 */
	public Double getServiceChargeAmt() {
		return serviceChargeAmt;
	}

	/**
	 * Sets the service charge amt.
	 *
	 * @param serviceChargeAmt
	 *            the new service charge amt
	 */
	public void setServiceChargeAmt(Double serviceChargeAmt) {
		this.serviceChargeAmt = serviceChargeAmt;
	}

	/**
	 * Gets the app type.
	 *
	 * @return the app type
	 */
	public Integer getAppType() {
		return appType;
	}

	/**
	 * Sets the app type.
	 *
	 * @param appType
	 *            the new app type
	 */
	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public MobileRequest getMobileRequest() {
		return mobileRequest;
	}

	public void setMobileRequest(MobileRequest mobileRequest) {
		this.mobileRequest = mobileRequest;
	}

	public AppMaster getAppMaster() {
		return appMaster;
	}

	public void setAppMaster(AppMaster appMaster) {
		this.appMaster = appMaster;
	}









}
