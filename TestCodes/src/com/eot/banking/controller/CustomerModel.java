package com.eot.banking.controller;

import java.util.ArrayList;
import java.util.List;

import com.eot.banking.dto.TransactionBaseDTO;


public class CustomerModel extends TransactionBaseDTO {

	private Long fromDate;

	private Long toDate;
	
	private int pageNo;
	
	private List<Customers> transactions =new ArrayList<Customers>();

	public Long getFromDate() {
		return fromDate;
	}
	public void setFromDate(Long fromDate) {
		this.fromDate = fromDate;
	}
	public Long getToDate() {
		return toDate;
	}
	public void setToDate(Long toDate) {
		this.toDate = toDate;
	}
	
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	



	public List<Customers> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Customers> transactions) {
		this.transactions = transactions;
	}





	public class Customers{
		
		private String firstName;
		
		private String mobileNumber;
		
		private Long createdDate;
		
		private Integer customerKycStatus;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public Long getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Long createdDate) {
			this.createdDate = createdDate;
		}

		public Integer getCustomerKycStatus() {
			return customerKycStatus;
		}

		public void setCustomerKycStatus(Integer customerKycStatus) {
			this.customerKycStatus = customerKycStatus;
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}	
		
		
		
	}
	
}
