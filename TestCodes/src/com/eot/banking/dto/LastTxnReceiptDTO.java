/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: LastTxnReceiptDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:51 PM Sambit Created
*/
package com.eot.banking.dto;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class LastTxnReceiptDTO.
 */
public class LastTxnReceiptDTO extends TransactionBaseDTO{
	
	/** The type of txn receipt. */
	private Integer typeOfTxnReceipt; 
	
	/** The list of last txn recipt. */
	private ArrayList<TransactionRecipt> listOfLastTxnRecipt = new ArrayList<LastTxnReceiptDTO.TransactionRecipt>();
 
	/**
	 * Gets the list of last txn recipt.
	 * 
	 * @return the list of last txn recipt
	 */
	public ArrayList<TransactionRecipt> getListOfLastTxnRecipt() {
		return listOfLastTxnRecipt;
	}

	/**
	 * Sets the list of last txn recipt.
	 * 
	 * @param listOfLastTxnRecipt
	 *            the new list of last txn recipt
	 */
	public void setListOfLastTxnRecipt(ArrayList<TransactionRecipt> listOfLastTxnRecipt) {
		this.listOfLastTxnRecipt = listOfLastTxnRecipt;
	}
	
	/**
	 * Gets the type of txn receipt.
	 * 
	 * @return the type of txn receipt
	 */
	public Integer getTypeOfTxnReceipt() {
		return typeOfTxnReceipt;
	}

	/**
	 * Sets the type of txn receipt.
	 * 
	 * @param typeOfTxnReceipt
	 *            the new type of txn receipt
	 */
	public void setTypeOfTxnReceipt(Integer typeOfTxnReceipt) {
		this.typeOfTxnReceipt = typeOfTxnReceipt;
	}

	/**
	 * The Class TransactionRecipt.
	 */
	public class TransactionRecipt {
		
		/** The transaction time. */
		private Long transactionTime;
		
		/** The txn response. */
		private String txnResponse;

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
		 * Gets the txn response.
		 * 
		 * @return the txn response
		 */
		public String getTxnResponse() {
			return txnResponse;
		}

		/**
		 * Sets the txn response.
		 * 
		 * @param txnResponse
		 *            the new txn response
		 */
		public void setTxnResponse(String txnResponse) {
			this.txnResponse = txnResponse;
		}
		
		
	}

}
