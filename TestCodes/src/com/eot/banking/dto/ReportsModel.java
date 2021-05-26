package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportsModel extends TransactionBaseDTO {

	private List<Transaction> transactions =new ArrayList<Transaction>();
	
	private Long fromDate;

	private Long toDate;
	
	private String agentCode;
	
	private int pageNo;

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
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}



	public class Transaction extends TransactionBaseDTO{
		
		private String name;
		
		private Long transactionDate;
		
	 	private String requestChannel;
	 	
	 	private Long txnDate;
	 	
	 	private Integer transactionStatus;
	 	
	 	private Double txnAmount;
	 	
	 	private String agentCode;
	 	
	 	private String transactionLabels ;
	 	
	 	private String debitCredit;
	 	
	 	private String txnId;

		public String getTransactionLabels() {
			return transactionLabels;
		}

		public void setTransactionLabels(String transactionLabels) {
			this.transactionLabels = transactionLabels;
		}

		public Double getTxnAmount() {
			return txnAmount;
		}

		public void setTxnAmount(Double txnAmount) {
			this.txnAmount = txnAmount;
		}

		public Long getTxnDate() {
			return txnDate;
		}

		public void setTxnDate(Long txnDate) {
			this.txnDate = txnDate;
		}

		

		public Integer getTransactionStatus() {
			return transactionStatus;
		}

		public void setTransactionStatus(Integer transactionStatus) {
			this.transactionStatus = transactionStatus;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getTransactionDate() {
			return transactionDate;
		}

		public void setTransactionDate(Long transactionDate) {
			this.transactionDate = transactionDate;
		}

		public String getRequestChannel() {
			return requestChannel;
		}

		public void setRequestChannel(String requestChannel) {
			this.requestChannel = requestChannel;
		}

		public String getAgentCode() {
			return agentCode;
		}

		public void setAgentCode(String agentCode) {
			this.agentCode = agentCode;
		}

		public String getDebitCredit() {
			return debitCredit;
		}

		public void setDebitCredit(String debitCredit) {
			this.debitCredit = debitCredit;
		}

		public String getTxnId() {
			return txnId;
		}

		public void setTxnId(String txnId) {
			this.txnId = txnId;
		}
	 	
	}

	public String getAgentCode() {
		return agentCode;
	}
	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	
 	
 	
 	
	
}
