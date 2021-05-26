package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.eot.banking.dto.MinistatementDTO.TransactionDTO;
import com.eot.entity.TransactionType;

@SuppressWarnings("all")
public class PendingTransactionDTO extends TransactionBaseDTO{
	
	/** The list of txn. */
	
	private Long pendingTxnRecordId;
	private  ArrayList<TransactionDTO> listOfPendingTxn = new ArrayList<PendingTransactionDTO.TransactionDTO>();
	
	private List<PendingTransactionDTO> pendingTransactionsList;
	
	private String agentCode;
	
	private String agentType;
	
	
	private String agentName;
	
	private Integer isApproved;

	private String phone;
	
	
	List<PendingTransactionDTO> list;
	
	public ArrayList<TransactionDTO> getListOfPendingTxn() {
		return listOfPendingTxn;
	}




	public void setListOfPendingTxn(ArrayList<TransactionDTO> listOfPendingTxn) {
		this.listOfPendingTxn = listOfPendingTxn;
	}



	public class TransactionDTO{
		
	private Long transactionId;
	private Date transactionDate;
	private String referenceId;
	private Integer referenceType;
	private String customerAccount;
	private Integer customerAccountType;
	private String otherAccount;
	private Integer otherAccountType;
	private double amount;
	private Integer transactionStatus;
	private Integer txnType;
	private String approvedBy;
	private String initiatedBy;
	private String name;
	private String phone;
	private String serviceCharge;
	
//	private String remarks;
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public Integer getReferenceType() {
		return referenceType;
	}
	public void setReferenceType(Integer referenceType) {
		this.referenceType = referenceType;
	}
	public String getCustomerAccount() {
		return customerAccount;
	}
	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}
	public Integer getCustomerAccountType() {
		return customerAccountType;
	}
	public void setCustomerAccountType(Integer customerAccountType) {
		this.customerAccountType = customerAccountType;
	}
	public String getOtherAccount() {
		return otherAccount;
	}
	public void setOtherAccount(String otherAccount) {
		this.otherAccount = otherAccount;
	}
	public Integer getOtherAccountType() {
		return otherAccountType;
	}
	public void setOtherAccountType(Integer otherAccountType) {
		this.otherAccountType = otherAccountType;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
		
	public Integer getTxnType() {
		return txnType;
	}
	public void setTxnType(Integer txnType) {
		this.txnType = txnType;
	}

	public Integer getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(Integer transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public String getInitiatedBy() {
		return initiatedBy;
	}
	public void setInitiatedBy(String initiatedBy) {
		this.initiatedBy = initiatedBy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	}



	public Long getPendingTxnRecordId() {
		return pendingTxnRecordId;
	}




	public void setPendingTxnRecordId(Long pendingTxnRecordId) {
		this.pendingTxnRecordId = pendingTxnRecordId;
	}




	public String getAgentCode() {
		return agentCode;
	}




	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}




	public String getAgentName() {
		return agentName;
	}




	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public List<PendingTransactionDTO> getList() {
		return list;
	}

	public void setList(List<PendingTransactionDTO> list) {
		this.list = list;
	}




	public List<PendingTransactionDTO> getPendingTransactionsList() {
		return pendingTransactionsList;
	}




	public void setPendingTransactionsList(List<PendingTransactionDTO> pendingTransactionsList) {
		this.pendingTransactionsList = pendingTransactionsList;
	}




	public Integer getIsApproved() {
		return isApproved;
	}




	public void setIsApproved(Integer isApproved) {
		this.isApproved = isApproved;
	}




	public String getPhone() {
		return phone;
	}




	public void setPhone(String phone) {
		this.phone = phone;
	}

}
