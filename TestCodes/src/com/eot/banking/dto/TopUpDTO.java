/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: TopUpDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:08 PM Sambit Created
*/
package com.eot.banking.dto;

// TODO: Auto-generated Javadoc
/**
 * The Class TopUpDTO.
 */
public class TopUpDTO extends TransactionBaseDTO {
	
	/** The operator id. */
	private Long operatorId;
	
	/** The operator name. */
	private String operatorName;
	
	/** The voucher number. */
	private String voucherNumber;
	
	private String transactionNO;
	/**
	 * Gets the operator id.
	 * 
	 * @return the operator id
	 */
	
	
	public Long getOperatorId() {
		return operatorId;
	}

	public String getTransactionNO() {
		return transactionNO;
	}

	public void setTransactionNO(String transactionNO) {
		this.transactionNO = transactionNO;
	}

	/**
	 * Sets the operator id.
	 * 
	 * @param operatorId
	 *            the new operator id
	 */
	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	/**
	 * Gets the operator name.
	 * 
	 * @return the operator name
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * Sets the operator name.
	 * 
	 * @param operatorName
	 *            the new operator name
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * Gets the voucher number.
	 * 
	 * @return the voucher number
	 */
	public String getVoucherNumber() {
		return voucherNumber;
	}

	/**
	 * Sets the voucher number.
	 * 
	 * @param voucherNumber
	 *            the new voucher number
	 */
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

}
