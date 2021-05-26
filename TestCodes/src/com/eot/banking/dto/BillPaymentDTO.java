/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BillPaymentDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:36 PM Sambit Created
*/
package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class BillPaymentDTO.
 */
public class BillPaymentDTO extends TransactionBaseDTO {
	
	/** The customer id. */
	private String customerId;
	
	/** The consumer name. */
	private String consumerName;
	
	/** The biller id. */
	private String billerId;
	
	/** The biller name. */
	private String billerName;
	
	/** The bill amount. */
	private Double billAmount;
	
	/** The payment due date. */
	private Long paymentDueDate;
	
	/** The payment extension date. */
	private Long paymentExtensionDate;
	
	/** The payment type. */
	private Integer paymentType;
	
	/** The bill types as string. */
	private String billTypesAsString;
	
	/** The customer type. */
	private String customerType;
	
	/** The int customer id. */
	private String intCustomerId;
	
	/** The total amount. */
	private String totalAmount;
	
	/** The uuid. */
	private String uuid;
	
	private String policyNumber;
	
	/** The bills. */
	private List<Bill> bills = new ArrayList<Bill>();

	/**
	 * Gets the bill types as string.
	 *
	 * @return the bill types as string
	 * The billTypesAsString
	 */
	public String getBillTypesAsString() {
		return billTypesAsString;
	}

	/**
	 * Sets the bill types as string.
	 *
	 * @param billTypesAsString The billTypesAsString
	 */
	public void setBillTypesAsString(String billTypesAsString) {
		this.billTypesAsString = billTypesAsString;
	}

	/**
	 * Gets the bills.
	 *
	 * @return the bills
	 * The bills
	 */
	public List<Bill> getBills() {
		return bills;
	}

	/**
	 * Sets the bills.
	 *
	 * @param bills The bills
	 */
	public void setBills(List<Bill> bills) {
		this.bills = bills;
	}

	/**
	 * Gets the customer type.
	 *
	 * @return the customer type
	 * The customerType
	 */
	public String getCustomerType() {
		return customerType;
	}

	/**
	 * Sets the customer type.
	 *
	 * @param customerType The customerType
	 */
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	/**
	 * Gets the int customer id.
	 *
	 * @return the int customer id
	 * The intCustomerId
	 */
	public String getIntCustomerId() {
		return intCustomerId;
	}

	/**
	 * Sets the int customer id.
	 *
	 * @param intCustomerId The intCustomerId
	 */
	public void setIntCustomerId(String intCustomerId) {
		this.intCustomerId = intCustomerId;
	}

	/**
	 * Gets the total amount.
	 *
	 * @return the total amount
	 * The totalAmount
	 */
	public String getTotalAmount() {
		return totalAmount;
	}

	/**
	 * Sets the total amount.
	 *
	 * @param totalAmount The totalAmount
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 * The uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Sets the uuid.
	 *
	 * @param uuid The uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Gets the customer id.
	 * 
	 * @return the customer id
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * Sets the customer id.
	 * 
	 * @param customerId
	 *            the new customer id
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * Gets the biller id.
	 * 
	 * @return the biller id
	 */
	public String getBillerId() {
		return billerId;
	}

	/**
	 * Sets the biller id.
	 * 
	 * @param billerId
	 *            the new biller id
	 */
	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}

	/**
	 * Gets the bill amount.
	 * 
	 * @return the bill amount
	 */
	public Double getBillAmount() {
		return billAmount;
	}

	/**
	 * Sets the bill amount.
	 * 
	 * @param billAmount
	 *            the new bill amount
	 */
	public void setBillAmount(Double billAmount) {
		this.billAmount = billAmount;
	}

	/**
	 * Gets the payment due date.
	 * 
	 * @return the payment due date
	 */
	public Long getPaymentDueDate() {
		return paymentDueDate;
	}

	/**
	 * Sets the payment due date.
	 * 
	 * @param paymentDueDate
	 *            the new payment due date
	 */
	public void setPaymentDueDate(Long paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	/**
	 * Gets the payment extension date.
	 * 
	 * @return the payment extension date
	 */
	public Long getPaymentExtensionDate() {
		return paymentExtensionDate;
	}

	/**
	 * Sets the payment extension date.
	 * 
	 * @param paymentExtensionDate
	 *            the new payment extension date
	 */
	public void setPaymentExtensionDate(Long paymentExtensionDate) {
		this.paymentExtensionDate = paymentExtensionDate;
	}

	/**
	 * Gets the payment type.
	 * 
	 * @return the payment type
	 */
	public Integer getPaymentType() {
		return paymentType;
	}

	/**
	 * Sets the payment type.
	 * 
	 * @param paymentType
	 *            the new payment type
	 */
	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * Gets the consumer name.
	 * 
	 * @return the consumer name
	 */
	public String getConsumerName() {
		return consumerName;
	}

	/**
	 * Sets the consumer name.
	 * 
	 * @param consumerName
	 *            the new consumer name
	 */
	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	/**
	 * Gets the biller name.
	 * 
	 * @return the biller name
	 */
	public String getBillerName() {
		return billerName;
	}

	/**
	 * Sets the biller name.
	 * 
	 * @param billerName
	 *            the new biller name
	 */
	public void setBillerName(String billerName) {
		this.billerName = billerName;
	}
	
	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	/**
	 * The Class Bill.
	 */
	public class Bill {

		/** The amount. */
		private String amount;

		/** The bill month. */
		private String billMonth;

		/** The bill number. */
		private String billNumber;

		/** The int cust id. */
		private String intCustId;

		/** The is active. */
		private String isActive;

		/** The utility id. */
		private String utilityId;

		/**
		 * Gets the amount.
		 *
		 * @return the amount
		 * The amount
		 */
		public String getAmount() {
			return amount;
		}

		/**
		 * Sets the amount.
		 *
		 * @param amount The amount
		 */
		public void setAmount(String amount) {
			this.amount = amount;
		}

		/**
		 * Gets the bill month.
		 *
		 * @return the bill month
		 * The billMonth
		 */
		public String getBillMonth() {
			return billMonth;
		}

		/**
		 * Sets the bill month.
		 *
		 * @param billMonth The billMonth
		 */
		public void setBillMonth(String billMonth) {
			this.billMonth = billMonth;
		}

		/**
		 * Gets the bill number.
		 *
		 * @return the bill number
		 * The billNumber
		 */
		public String getBillNumber() {
			return billNumber;
		}

		/**
		 * Sets the bill number.
		 *
		 * @param billNumber The billNumber
		 */
		public void setBillNumber(String billNumber) {
			this.billNumber = billNumber;
		}

		/**
		 * Gets the int cust id.
		 *
		 * @return the int cust id
		 * The intCustId
		 */
		public String getIntCustId() {
			return intCustId;
		}

		/**
		 * Sets the int cust id.
		 *
		 * @param intCustId The intCustId
		 */
		public void setIntCustId(String intCustId) {
			this.intCustId = intCustId;
		}

		/**
		 * Gets the checks if is active.
		 *
		 * @return the checks if is active
		 * The isActive
		 */
		public String getIsActive() {
			return isActive;
		}

		/**
		 * Sets the checks if is active.
		 *
		 * @param isActive The isActive
		 */
		public void setIsActive(String isActive) {
			this.isActive = isActive;
		}

		/**
		 * Gets the utility id.
		 *
		 * @return the utility id
		 * The utilityId
		 */
		public String getUtilityId() {
			return utilityId;
		}

		/**
		 * Sets the utility id.
		 *
		 * @param utilityId The utilityId
		 */
		public void setUtilityId(String utilityId) {
			this.utilityId = utilityId;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Bill [amount=" + amount + ", billMonth=" + billMonth
					+ ", billNumber=" + billNumber + ", intCustId=" + intCustId
					+ ", isActive=" + isActive + ", utilityId=" + utilityId
					+ ", getAmount()=" + getAmount() + ", getBillMonth()="
					+ getBillMonth() + ", getBillNumber()=" + getBillNumber()
					+ ", getIntCustId()=" + getIntCustId() + ", getIsActive()="
					+ getIsActive() + ", getUtilityId()=" + getUtilityId()
					+ "]";
		}
		
	}

}
