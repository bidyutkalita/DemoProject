/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: MinistatementDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:00 PM Sambit Created
*/
package com.eot.banking.dto;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class MinistatementDTO.
 */
public class MinistatementDTO extends TransactionBaseDTO {
	
	private String currentBalance;
	/** The list of txn. */
	private ArrayList<TransactionDTO> listOfTxn = null;//new ArrayList<MinistatementDTO.TransactionDTO>();

	/**
	 * Gets the list of txn.
	 * 
	 * @return the list of txn
	 */
	public ArrayList<TransactionDTO> getListOfTxn() {
		return listOfTxn;
	}

	/**
	 * Sets the list of txn.
	 * 
	 * @param listOfTxn
	 *            the new list of txn
	 */
	public void setListOfTxn(ArrayList<TransactionDTO> listOfTxn) {
		this.listOfTxn = listOfTxn;
	}
	
	/**
	 * The Class TransactionDTO.
	 */
	public class TransactionDTO{
		
		/** The amount. */
		private Double amount;

		/** The trans date. */
		private Long transDate;
		
		/** The trans desc. */
		private String transDesc;
		
		/** The trans type. */
		private String transType;
		
		/**
		 * Gets the trans date.
		 * 
		 * @return the trans date
		 */
		public Long getTransDate() {
			return transDate;
		}

		/**
		 * Sets the trans date.
		 * 
		 * @param transDate
		 *            the new trans date
		 */
		public void setTransDate(Long transDate) {
			this.transDate = transDate;
		}

		/**
		 * Gets the trans desc.
		 * 
		 * @return the trans desc
		 */
		public String getTransDesc() {
			return transDesc;
		}

		/**
		 * Sets the trans desc.
		 * 
		 * @param transDesc
		 *            the new trans desc
		 */
		public void setTransDesc(String transDesc) {
			this.transDesc = transDesc;
		}

		/**
		 * Gets the trans type.
		 * 
		 * @return the trans type
		 */
		public String getTransType() {
			return transType;
		}

		/**
		 * Sets the trans type.
		 * 
		 * @param transType
		 *            the new trans type
		 */
		public void setTransType(String transType) {
			this.transType = transType;
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
	}

	public String getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}

}
