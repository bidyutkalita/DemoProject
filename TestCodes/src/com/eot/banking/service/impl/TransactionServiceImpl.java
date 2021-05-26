
/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: TransactionServiceImpl.java,v 1.0
 *
 * Date Author Changes
 * 21 Oct, 2015, 3:02:20 PM Sambit Created
 */
package com.eot.banking.service.impl;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.cbs.ws.common.CBSException;
import com.cbs.ws.dto.ChequeStatusDTO;
import com.cbs.ws.handler.CBSBankingStubProxy;
import com.eot.banking.common.CoreUrls;
import com.eot.banking.common.EOTConstants;
import com.eot.banking.common.OtpStatusEnum;
import com.eot.banking.common.UrlConstants;
import com.eot.banking.dto.BalanceEnquiryDTO;
import com.eot.banking.dto.BankTransactionDTO;
import com.eot.banking.dto.BillPaymentDTO;
import com.eot.banking.dto.ChequeEnquiryDTO;
import com.eot.banking.dto.CurrentBalance;
import com.eot.banking.dto.FundTransferDTO;
import com.eot.banking.dto.LastTxnReceiptDTO;
import com.eot.banking.dto.LastTxnReceiptDTO.TransactionRecipt;
import com.eot.banking.dto.MinistatementDTO;
import com.eot.banking.dto.MinistatementDTO.TransactionDTO;
import com.eot.banking.dto.PendingTransactionDTO;
import com.eot.banking.dto.RemittanceDTO;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.dto.SMSCashDTO;
import com.eot.banking.dto.SaleDTO;
import com.eot.banking.dto.ServiceChargeDTO;
import com.eot.banking.dto.SmsResponseDTO;
import com.eot.banking.dto.TopUpDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.server.data.OtpDTO;
import com.eot.banking.service.OtherBankingService;
import com.eot.banking.service.TransactionService;
import com.eot.banking.utils.DateUtil;
import com.eot.banking.utils.HashUtil;
import com.eot.coreclient.webservice.BankingServiceClientStub;
import com.eot.coreclient.webservice.BasicBankingServiceClientStub;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.banking.AdjustmentTransactionDTO;
import com.eot.dtos.banking.DepositDTO;
import com.eot.dtos.banking.TransferDirectDTO;
import com.eot.dtos.banking.WithdrawalDTO;
import com.eot.dtos.basic.MiniStatementDTO;
import com.eot.dtos.sms.SmsHeader;
import com.eot.dtos.sms.WebOTPAlertDTO;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.dtos.utilities.VoucherTopupDTO;
import com.eot.entity.Account;
import com.eot.entity.AppMaster;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.Branch;
import com.eot.entity.ClearingHousePoolMember;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.ExternalTransaction;
import com.eot.entity.MobileRequest;
import com.eot.entity.Operator;
import com.eot.entity.Otp;
import com.eot.entity.PendingTransaction;
import com.eot.entity.SenelecBills;
import com.eot.entity.SmsLog;
import com.eot.entity.Transaction;
import com.eot.entity.TransactionJournal;
import com.eot.entity.TransactionType;
import com.security.kms.security.KMSSecurityException;
import com.thinkways.util.HexString;

/**
 * The Class TransactionServiceImpl.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	/** The base service impl. */
	@Autowired
	private BaseServiceImpl baseServiceImpl;

	/** The other banking service. */
	@Autowired
	private OtherBankingService otherBankingService;

	/** The basic banking service client stub. */
	@Autowired
	private BasicBankingServiceClientStub basicBankingServiceClientStub;

	/** The banking service client stub. */
	@Autowired
	private BankingServiceClientStub bankingServiceClientStub;

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;
	
	@Autowired
	private RestTemplate restTemplate;

	/** The otp for sale enabled. */
	private boolean otpForSaleEnabled= true;
	
	/*
	 * @Autowired private SmsServiceClientStub smsServiceClientStub ;
	 */

	/**
	 * Sets the otp for sale enabled.
	 * 
	 * @param otpForSaleEnabled
	 *            the new otp for sale enabled
	 */
	/*public void setOtpForSaleEnabled(boolean otpForSaleEnabled) {
		this.otpForSaleEnabled = otpForSaleEnabled;
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processBalanceEnquiry(com.eot.
	 * banking.dto.TransactionBaseDTO)
	 */
	public BalanceEnquiryDTO processBalanceEnquiryRequest(BalanceEnquiryDTO balanceEnquiryDTO) throws EOTException {

		System.out.println("******** BalanceEnquiryHandler *************");

		baseServiceImpl.handleRequest(balanceEnquiryDTO);
		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		if (Constants.ALIAS_TYPE_CARD_ACC == balanceEnquiryDTO.getAliasType()) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), balanceEnquiryDTO.getAccountAlias());
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}
			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == balanceEnquiryDTO.getAliasType()) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), balanceEnquiryDTO.getAccountAlias());
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		} else {

			CustomerBankAccount account = baseServiceImpl.eotMobileDao.getBankAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), balanceEnquiryDTO.getAccountAlias());
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getBankAccountNumber());
	//		accountDto.setBankCode(account.getBank().getBankId().toString());

		}

		accountDto.setAccountAlias(balanceEnquiryDTO.getAccountAlias());
		accountDto.setAccountType(balanceEnquiryDTO.getAliasType().toString());

		com.eot.dtos.basic.BalanceEnquiryDTO coreBalanceEnquiryDTO = new com.eot.dtos.basic.BalanceEnquiryDTO();

		coreBalanceEnquiryDTO.setTransactionType(balanceEnquiryDTO.getTransactionType().toString()); // Should be moved to Constant
		coreBalanceEnquiryDTO.setCustomerAccount(accountDto);
		coreBalanceEnquiryDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		coreBalanceEnquiryDTO.setReferenceType(baseServiceImpl.referenceType);
		coreBalanceEnquiryDTO.setRequestID(baseServiceImpl.requestID.toString());
		coreBalanceEnquiryDTO.setChannelType(Constants.EOT_CHANNEL);
		coreBalanceEnquiryDTO.setAmount(0D);

		try {
			// old core call to core wallet by bidyut
			// coreBalanceEnquiryDTO =
			// basicBankingServiceClientStub.balanceEnquiry(coreBalanceEnquiryDTO);

			// rest call updated by bidyut
			coreBalanceEnquiryDTO = processRequest(CoreUrls.BALANCE_ENQ_WALLET, coreBalanceEnquiryDTO, com.eot.dtos.basic.BalanceEnquiryDTO.class);
			if (coreBalanceEnquiryDTO.getErrorCode() != 0) {
				throw new EOTException(coreBalanceEnquiryDTO.getErrorCode());
			}
			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(coreBalanceEnquiryDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

		} /*
			 * catch (EOTCoreException Exception e) { e.printStackTrace(); throw new
			 * EOTException(ErrorConstants.SERVICE_ERROR); throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); }
			 */
		finally {

		}

		//balanceEnquiryDTO.setAvailableBalance(Math.ceil(coreBalanceEnquiryDTO.getBalance().doubleValue()));
		balanceEnquiryDTO.setAvailableBalance(null !=coreBalanceEnquiryDTO.getBalance() ? new Double(new DecimalFormat("#0.00").format(coreBalanceEnquiryDTO.getBalance().doubleValue())):0.00);
		// balanceEnquiryDTO.setAvailableBalance(coreBalanceEnquiryDTO.getBalance());
		// balanceEnquiryDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("BAL_ENQ_SUCCESS",
		// new String[] { new
		// DecimalFormat("0").format(coreBalanceEnquiryDTO.getBalance()) }, new
		// Locale(baseServiceImpl.customer.getDefaultLanguage())));
		//balanceEnquiryDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("BAL_ENQ_SUCCESS", new String[] { String.format("%.00f", Math.ceil(coreBalanceEnquiryDTO.getBalance().doubleValue())) }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		balanceEnquiryDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("BAL_ENQ_SUCCESS", new String[] {null != coreBalanceEnquiryDTO.getBalance() ? new DecimalFormat("#0.00").format(coreBalanceEnquiryDTO.getBalance().doubleValue()):"0.00" }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		return balanceEnquiryDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processMinistatementRequest(com.
	 * eot.banking.dto.MinistatementDTO)
	 */
	public MinistatementDTO processMinistatementRequest(MinistatementDTO ministatementDTO) throws EOTException {

		baseServiceImpl.handleRequest(ministatementDTO);
		String accountAlias = ministatementDTO.getAccountAlias();
		Integer aliasType = ministatementDTO.getAliasType();

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		if (Constants.ALIAS_TYPE_CARD_ACC == aliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), accountAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == aliasType) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), accountAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		} else if (Constants.ALIAS_TYPE_BANK_ACC == aliasType) {

			CustomerBankAccount account = baseServiceImpl.eotMobileDao.getBankAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), accountAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getBankAccountNumber());
	//		accountDto.setBankCode(account.getBank().getBankId().toString());

		}

		accountDto.setAccountAlias(accountAlias);
		accountDto.setAccountType(aliasType.toString());

		MiniStatementDTO miniStatementDTO = new MiniStatementDTO();

		miniStatementDTO.setTransactionType(ministatementDTO.getTransactionType().toString());
		miniStatementDTO.setCustomerAccount(accountDto);
		miniStatementDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		miniStatementDTO.setReferenceType(baseServiceImpl.referenceType);
		miniStatementDTO.setRequestID(baseServiceImpl.requestID.toString());
		miniStatementDTO.setChannelType(Constants.EOT_CHANNEL);
		miniStatementDTO.setAmount(0D);

		try {

			// miniStatementDTO =
			// basicBankingServiceClientStub.miniStatement(miniStatementDTO);

			// rest call updated by bidyut
			miniStatementDTO = processRequest(CoreUrls.MINI_STATEMENT, miniStatementDTO, com.eot.dtos.basic.MiniStatementDTO.class);
			if (miniStatementDTO.getErrorCode() != 0) {
				throw new EOTException(miniStatementDTO.getErrorCode());
			}

			com.eot.entity.Transaction txn = new com.eot.entity.Transaction();
			txn.setTransactionId(new Long(miniStatementDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

			if (miniStatementDTO.getTransactionList() == null) {
				throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);
			}

			List<com.eot.dtos.basic.Transaction> list = Arrays.asList(miniStatementDTO.getTransactionList());

			ArrayList<TransactionDTO> listOfTxn = new ArrayList<MinistatementDTO.TransactionDTO>();
			StringBuffer buff = new StringBuffer();
			for (com.eot.dtos.basic.Transaction transaction : list) {

				TransactionDTO transactionDTO = ministatementDTO.new TransactionDTO();
				transactionDTO.setAmount(transaction.getAmount());
				transactionDTO.setTransDate(transaction.getTransDate().getTimeInMillis());
				transactionDTO.setTransDesc(transaction.getTransDesc());
				transactionDTO.setTransType(transaction.getTransType());

				String dt = DateUtil.formatDateToStr(transaction.getTransDate().getTime());
				buff.append(dt + "|");
				buff.append(transaction.getTransDesc() + " - [" + transaction.getTransType() + "] " + transaction.getAmount() + "\n");

				listOfTxn.add(transactionDTO);
			}

			ministatementDTO.setSuccessResponse(buff.toString());
			ministatementDTO.setListOfTxn(listOfTxn);
			ministatementDTO.setCurrentBalance(miniStatementDTO.getCurrentBalance());
		//	ministatementDTO.setCurrentBalance(null != miniStatementDTO.getCurrentBalance() ? new DecimalFormat("#0.00").format(miniStatementDTO.getCurrentBalance()):"0");
			
			return ministatementDTO;

		} /*
			 * catch (Exception e) { e.printStackTrace(); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); throw new
			 * EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#validateTransferRequest(com.eot.
	 * banking.dto.FundTransferDTO)
	 */
	public FundTransferDTO validateTransferRequest(FundTransferDTO fundTransferDTO) throws EOTException {

		baseServiceImpl.handleRequest(fundTransferDTO);
	//	String fromAlias = fundTransferDTO.getAccountAlias();
		String toMobileNo = fundTransferDTO.getPayeeMobileNumber();
		Double amount = fundTransferDTO.getAmount();

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(toMobileNo)) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		CustomerAccount otherAccount = baseServiceImpl.eotMobileDao.getPayeeAccountFromMobileNo(toMobileNo);

		if (otherAccount == null) {
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}

	/*	if (otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}*/
		
		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(otherAccount.getCustomer().getAppId());
			
				if( otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){  // validate status of merchant/customer
					throw new EOTException(ErrorConstants.AGENT_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_ACC_BLOCKED);
				}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_PENDING && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_KYC_PENDING);
				}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_KYC_REJECTED);
				}
				
				if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER) {
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_BLOCKED);
				}else if( otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
				}
//				else if((otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_PENDING || otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_APPROVE_PENDING) && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
//					throw new EOTException(ErrorConstants.CUSTOMER_KYC_PENDING);
//				}
				
				if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
					throw new EOTException(ErrorConstants.MERCHANT_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_ACC_BLOCKED);
				}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_PENDING && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_KYC_PENDING);
				}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_KYC_REJECTED);
				}
				
				if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_SUSPENDED) {
					throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
				}

		if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}

		ServiceChargeDTO serviceChargeDTO = new ServiceChargeDTO();

		serviceChargeDTO.setApplicationType(fundTransferDTO.getApplicationType());
		serviceChargeDTO.setTxnAmount(fundTransferDTO.getAmount() != null ? fundTransferDTO.getAmount().doubleValue() : null);
		if (fundTransferDTO.getTxnBankingType() != null)
			serviceChargeDTO.setTxnBankingType(fundTransferDTO.getTxnBankingType());
		else
			serviceChargeDTO.setTxnBankingType(EOTConstants.TXN_TYPE_INTRA);

		serviceChargeDTO.setTransactionType(fundTransferDTO.getTransactionType());
		serviceChargeDTO.setTxnTypeId(Constants.TRANSFER_DIRECT_REQ);
		if (fundTransferDTO.getApplicationType() == null)
			serviceChargeDTO.setApplicationType(1 + "");

		serviceChargeDTO = otherBankingService.getServiceCharge(serviceChargeDTO);
		String payeeName = otherAccount.getCustomer().getFirstName()!= null ? otherAccount.getCustomer().getFirstName() : "".concat(" ").concat(otherAccount.getCustomer().getFirstName());
		String payeeLastName = otherAccount.getCustomer().getLastName()!= null ? otherAccount.getCustomer().getLastName() : "";
		//String payeeName = otherAccount.getCustomer().getFirstName().concat(" ").concat(otherAccount.getCustomer().getMiddleName() != null ? otherAccount.getCustomer().getMiddleName() : "").concat(" ").concat(otherAccount.getCustomer().getLastName());
		// fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_DIR_CONFIRM",
		// new String[] { fromAlias, toMobileNo, payeeName, amount.toString(),
		// serviceChargeDTO.getServiceCharge() != null ?
		// serviceChargeDTO.getServiceCharge().toString() : "" }, new
		// Locale(baseServiceImpl.customer.getDefaultLanguage())));
		
		
		fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_DIR_CONFIRM", new String[] { toMobileNo, payeeName+" "+payeeLastName, null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()):"0.00", serviceChargeDTO.getServiceCharge() != null ? new DecimalFormat("#0.00").format(serviceChargeDTO.getServiceCharge().doubleValue()).toString() : "0.00",fundTransferDTO.getRemarks() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		fundTransferDTO.setServiceChargeAmt(serviceChargeDTO.getServiceCharge());
		fundTransferDTO.setServiceCharge(serviceChargeDTO.getServiceCharge());
		return fundTransferDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processTransferRequest(com.eot.
	 * banking.dto.FundTransferDTO)
	 */
	public FundTransferDTO processTransferRequest(FundTransferDTO fundTransferDTO) throws EOTException {

		baseServiceImpl.handleRequest(fundTransferDTO);
		Customer agent = null;
		String toMobileNo = fundTransferDTO.getPayeeMobileNumber();
		if (fundTransferDTO.getTransactionType() == Constants.TXN_TYPE_PAY) {
			agent = baseServiceImpl.eotMobileDao.getAgentByAgentCode(fundTransferDTO.getAgentCode());
			if(agent.getType()!=Constants.REF_TYPE_MERCHANT)
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			toMobileNo = agent.getCountry().getIsdCode() + agent.getMobileNumber();
			
		}
		String fromAlias = fundTransferDTO.getAccountAlias();
		Integer fromAliasType = fundTransferDTO.getAliasType();

		Double amount = fundTransferDTO.getAmount();
		String txnPin = fundTransferDTO.getTransactionPIN();

		if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(toMobileNo)) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		accountDto.setAccountAlias(fromAlias);
		accountDto.setAccountType(fromAliasType.toString());

		if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		}

		CustomerAccount otherAccount = baseServiceImpl.eotMobileDao.getPayeeAccountFromMobileNo(toMobileNo);
		// validation send money customer to customer
		if (fundTransferDTO.getTransactionType() == Constants.TRANSFER_DIRECT_REQ) {
			if (baseServiceImpl.customer.getType() !=  Constants.REF_TYPE_CUSTOMER || otherAccount.getCustomer().getType()!=Constants.REF_TYPE_CUSTOMER)
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}

		if (otherAccount == null) {
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}

		if (otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}
		
		if (otherAccount.getCustomer().getActive() == EOTConstants.CUSTOMER_STATUS_SUSPENDED) { // validate status of Payee
			throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
		}

		if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}
		
		// validation send money agent to agent
		if (fundTransferDTO.getTransactionType() == Constants.FLOAT_MANAGEMENT_REQ) {
			if (baseServiceImpl.customer.getType() !=  Constants.REF_TYPE_AGENT || otherAccount.getCustomer().getType()!=Constants.REF_TYPE_AGENT)
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		
		List<ClearingHousePoolMember> chPoolList = baseServiceImpl.eotMobileDao.getClearingHouse(Integer.parseInt(accountDto.getBankCode()), otherAccount.getBank().getBankId());

		if (chPoolList == null) {
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}

		com.eot.dtos.common.Account otherAccountDto = new com.eot.dtos.common.Account();
		otherAccountDto.setAccountAlias(toMobileNo);
		otherAccountDto.setAccountNO(otherAccount.getAccountNumber());
		otherAccountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAccountDto.setBankCode(otherAccount.getBank().getBankId().toString());
		otherAccountDto.setBranchCode(otherAccount.getBranch().getBranchId().toString());

		TransferDirectDTO transferDirectDTO = new TransferDirectDTO();

		transferDirectDTO.setCustomerAccount(accountDto);
		transferDirectDTO.setOtherAccount(otherAccountDto);
		transferDirectDTO.setAmount(amount.doubleValue());
		transferDirectDTO.setChannelType(Constants.EOT_CHANNEL);
		transferDirectDTO.setRequestID(baseServiceImpl.requestID.toString());
		transferDirectDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		transferDirectDTO.setReferenceType(baseServiceImpl.referenceType);
		transferDirectDTO.setTransactionType(fundTransferDTO.getTransactionType().toString());
		transferDirectDTO.setRemarks(fundTransferDTO.getRemarks());
		transferDirectDTO.setPayeeName(otherAccount.getCustomer().getFirstName());
		
		try {

			// rest call updated by bidyut
			transferDirectDTO = processRequest(CoreUrls.TRANSFER_DIRECT_TXN_URL, transferDirectDTO, com.eot.dtos.banking.TransferDirectDTO.class);
			if (transferDirectDTO.getErrorCode() != 0) {
				throw new EOTException(transferDirectDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(transferDirectDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

			String middleName = otherAccount.getCustomer().getMiddleName()!= null ? otherAccount.getCustomer().getMiddleName() : "".concat(" ").concat(otherAccount.getCustomer().getMiddleName());
			if(middleName==null)
				middleName="";
			else middleName=middleName +" ";
		
			if (fundTransferDTO.getTransactionType() == Constants.TXN_TYPE_PAY) {
				fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_PAY_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00",  agent.getAgentCode(), StringUtils.isNotEmpty(agent.getBusinessName()) ? agent.getBusinessName():getName(agent.getFirstName(),agent.getLastName()), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "m-GURUSH Pay", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			} else if (fundTransferDTO.getTransactionType() == Constants.TXN_TYPE_FLOAT_MANAGEMENT) {
				fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("FLOAT_MGMT_PAY_SUCCESS",  new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00", null != transferDirectDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(transferDirectDTO.getServiceChargeAmt().doubleValue()) : "0.00", fundTransferDTO.getPayeeMobileNumber(), otherAccount.getCustomer().getFirstName()+" ".concat(otherAccount.getCustomer().getLastName()!=null?otherAccount.getCustomer().getLastName():""), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "Transfer Float", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			}
			else {

				fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_DIR_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00", null != transferDirectDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(transferDirectDTO.getServiceChargeAmt().doubleValue()) : "0.00", toMobileNo, getName(otherAccount.getCustomer().getFirstName(),otherAccount.getCustomer().getLastName()), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "NA", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			}
			return fundTransferDTO;

		} /*
			 * catch (EOTCoreException Exception e) { e.printStackTrace();
			 * //System.out.println(e.getMessageKey()); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey()));
			 * 
			 * throw new EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processBillPresentmentRequest(com.
	 * eot.banking.dto.BillPaymentDTO)
	 */
	public BillPaymentDTO processBillPresentmentRequest(BillPaymentDTO billPaymentDTO) throws EOTException {

		System.out.println("******** BillPresentmentHandler *************");

		if (null != billPaymentDTO.getApplicationId()) {
			baseServiceImpl.handleRequest(billPaymentDTO);
		}
		String policyNumber = billPaymentDTO.getCustomerId();
		String billerId = billPaymentDTO.getBillerId();

		Biller biller = baseServiceImpl.eotMobileDao.getBiller(Integer.parseInt(billerId));

		if (biller == null) {
			throw new EOTException(ErrorConstants.INVALID_BILLER);
		}

		// fetchKifiyaBill(billPaymentDTO);

		SenelecBills senelecBill = baseServiceImpl.eotMobileDao.getElectricityBillFromPolicyId(policyNumber);

		if (senelecBill == null) {
			throw new EOTException(ErrorConstants.NO_BILLS_FOUND);
		}

		String successResponse = baseServiceImpl.messageSource.getMessage("VIEW_BILL", new String[] { senelecBill.getSenelecCustomers().getCustomerName().trim(), senelecBill.getSenelecCustomers().getPolicyNumber(), DateUtil.formatDate(senelecBill.getPaymentDate()), DateUtil.formatDate(senelecBill.getExtensionDate()),  null != senelecBill.getRecordAmount() ? new DecimalFormat("#0.00").format(senelecBill.getRecordAmount().doubleValue()) : "0.00" + "", biller.getPartialPayments() + "" }, new Locale(baseServiceImpl.customer != null ? baseServiceImpl.customer.getDefaultLanguage() : baseServiceImpl.defaultLocale));

		billPaymentDTO.setSuccessResponse(successResponse);
		billPaymentDTO.setConsumerName(senelecBill.getSenelecCustomers().getCustomerName());
		billPaymentDTO.setBillAmount(senelecBill.getRecordAmount());
		billPaymentDTO.setPaymentDueDate(senelecBill.getPaymentDate().getTime());
		billPaymentDTO.setPaymentExtensionDate(senelecBill.getExtensionDate().getTime());
		billPaymentDTO.setPaymentType(biller.getPartialPayments());
		billPaymentDTO.setStatus(0);

		return billPaymentDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processBillPaymentRequest(com.eot.
	 * banking.dto.BillPaymentDTO)
	 */
	public BillPaymentDTO processBillPaymentRequest(BillPaymentDTO dto) throws EOTException {

		String fromAlias = dto.getAccountAlias();
		Integer fromAliasType = dto.getAliasType();
		String policyNumber = dto.getCustomerId();
		if (null != dto.getPolicyNumber())
			policyNumber = dto.getPolicyNumber();
		Double amount = dto.getAmount();
		String txnPin = dto.getTransactionPIN();
		String billerId = dto.getBillerId();
		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		com.eot.dtos.common.Account billerAcountDto = new com.eot.dtos.common.Account();
		if (null != dto.getApplicationId()) {
			baseServiceImpl.handleRequest(dto);
			if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
			}

		} else {
			baseServiceImpl.customer = baseServiceImpl.eotMobileDao.getCustomerAccountFromAccountNumber(dto.getAccountNumber()).getCustomer();
		}

		accountDto.setAccountAlias(fromAlias);
		accountDto.setAccountType(fromAliasType.toString());

		SenelecBills bill = baseServiceImpl.eotMobileDao.getElectricityBillFromPolicyId(policyNumber);

		if (amount < bill.getRecordAmount()) {
			throw new EOTException(ErrorConstants.NO_PARTIAL_PAYMENTS);
		} else if (amount > bill.getRecordAmount()) {
			throw new EOTException(ErrorConstants.INVALID_BILL_AMOUNT);
		}

		if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());
			// accountDto.setBankCode(card.getBank().getBankCode());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			// accountDto.setBankCode(account.getBank().getBankCode());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		} else if (Constants.ALIAS_TYPE_FI_ACC == fromAliasType) {
			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(baseServiceImpl.customer.getCustomerId());
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			// accountDto.setBankCode(account.getBank().getBankCode());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());
		}

		Biller biller = baseServiceImpl.eotMobileDao.getBiller(Integer.parseInt(billerId));

		if (biller == null) {
			throw new EOTException(ErrorConstants.INVALID_BILLER);
		}

		billerAcountDto.setAccountNO(biller.getAccount().getAccountNumber());
		billerAcountDto.setAccountType(Constants.ALIAS_TYPE_OTHER + "");
		billerAcountDto.setBankCode(biller.getBank().getBankId().toString());
		// billerAcountDto.setBankCode(biller.getBank().getBankCode());

		com.eot.dtos.utilities.BillPaymentDTO billPaymentDTO = new com.eot.dtos.utilities.BillPaymentDTO();

		billPaymentDTO.setCustomerAccount(accountDto);
		billPaymentDTO.setOtherAccount(billerAcountDto);
		billPaymentDTO.setAmount(amount);
		billPaymentDTO.setChannelType(Constants.EOT_CHANNEL);
		// billPaymentDTO.setRequestID(baseServiceImpl.requestID.toString());
		billPaymentDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		billPaymentDTO.setReferenceType(baseServiceImpl.customer.getType());
		billPaymentDTO.setTransactionType(dto.getTransactionType().toString());
		billPaymentDTO.setBillerID(billerId);
		billPaymentDTO.setRemarks(dto.getRemarks());
		billPaymentDTO.setCustomerPolicyId(policyNumber);
		
		try {
			// spring webservice call to core: commented by bidyut
			// billPaymentDTO = utilityServicesCleintSub.billPayment(billPaymentDTO);

			// rest call to core : modified by bidyut
			billPaymentDTO = processRequest(CoreUrls.BILL_PAYMENT_URL, billPaymentDTO, com.eot.dtos.utilities.BillPaymentDTO.class);
			if (billPaymentDTO.getErrorCode() != 0) {
				throw new EOTException(billPaymentDTO.getErrorCode());
			}
			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(billPaymentDTO.getTransactionNO()));
			// baseServiceImpl.mobileRequest.setTransaction(txn);

			bill.setAmountPaid(amount);
			bill.setMobileNumber(baseServiceImpl.customer.getMobileNumber());
			if (bill.getRecordAmount() <= amount) {
				bill.setStatus(1);
			} else {
				bill.setStatus(2);
			}

			bill.setTransaction(txn);

			baseServiceImpl.eotMobileDao.update(bill);
		} /*
			 * catch (Exception e) { e.printStackTrace();
			 * //System.out.println(e.getMessageKey()); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); throw new
			 * EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}

		dto.setBillerName(biller.getBillerName());
		dto.setAccountBalance(billPaymentDTO.getCustomerAccount().getAccountBalance());
		dto.setServiceChargeAmt(billPaymentDTO.getServiceChargeAmt());
		if(dto.getRemarks()==null || dto.getRemarks().equals("")){
			dto.setRemarks("Bill Payment");
		}
		
		/*
		 * dto.setSuccessResponse(baseServiceImpl.messageSource.getMessage(
		 * "BILL_PAY_SUCCESS", new String[]{fromAlias,
		 * biller.getBillerName(),amount.toString()}, new
		 * Locale(baseServiceImpl.customer.getDefaultLanguage())));
		 */

		/*
		 * @ Author name <vinod joshi>, Date<7/17/2018>, purpose of change <Recipt
		 * Modification > ,
		 */
		/* @ Start */
		dto.setSuccessResponse(baseServiceImpl.messageSource.getMessage("BILL_PAY_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != billPaymentDTO.getAmount() ? new DecimalFormat("#0.00").format( billPaymentDTO.getAmount().doubleValue()):"0.00", null != billPaymentDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(billPaymentDTO.getServiceChargeAmt().doubleValue()):"0.00", biller.getBillerName(), policyNumber, dto.getRemarks(), billPaymentDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		/* @ End */
		return dto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eot.banking.service.TransactionService#processTopUpRequest(com.eot.
	 * banking.dto.TopUpDTO)
	 */
	public TopUpDTO processTopUpRequest(TopUpDTO topUpDTO) throws EOTException {

		System.out.println("******** MobileTopupHandler *************");

		String accountAlias = topUpDTO.getAccountAlias();
		Integer aliasType = topUpDTO.getAliasType();
		Long operatorId = topUpDTO.getOperatorId();
		Double amount = topUpDTO.getAmount();
		String txnPin = topUpDTO.getTransactionPIN();
		if (accountAlias == null) {
			accountAlias = topUpDTO.getCustomerName();
		}

		if (null != topUpDTO.getApplicationId()) {
			baseServiceImpl.handleRequest(topUpDTO);
			if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
			}
		} else {
			baseServiceImpl.customer = baseServiceImpl.eotMobileDao.getCustomerAccountFromAccountNumber(topUpDTO.getAccountNumber()).getCustomer();
		}

		Operator operator = baseServiceImpl.eotMobileDao.findOperator(operatorId);

		if (operator == null) {
			throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		com.eot.dtos.common.Account operatorAccountDto = new com.eot.dtos.common.Account();

		operatorAccountDto.setAccountNO(operator.getAccount().getAccountNumber());
		operatorAccountDto.setAccountType(Constants.ALIAS_TYPE_OTHER + "");
		operatorAccountDto.setBankCode(operator.getBank().getBankId() + "");
		// operatorAccountDto.setBankCode(operator.getBank().getBankCode());

		if (Constants.ALIAS_TYPE_CARD_ACC == aliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), accountAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}
			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());
			// accountDto.setBankCode(card.getBank().getBankCode());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == aliasType) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), accountAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			// accountDto.setBankCode(account.getBank().getBankCode());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		} else if (Constants.ALIAS_TYPE_FI_ACC == aliasType) {
			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(baseServiceImpl.customer.getCustomerId());
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			// accountDto.setBankCode(account.getBank().getBankCode());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());
		}

		accountDto.setAccountAlias(accountAlias);
		accountDto.setAccountType(aliasType.toString());

		VoucherTopupDTO voucherTopupDTO = new VoucherTopupDTO();

		voucherTopupDTO.setTransactionType(topUpDTO.getTransactionType().toString());
		voucherTopupDTO.setCustomerAccount(accountDto);
		voucherTopupDTO.setOtherAccount(operatorAccountDto);
		voucherTopupDTO.setAmount(amount);
		voucherTopupDTO.setChannelType(Constants.EOT_CHANNEL);
		voucherTopupDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		voucherTopupDTO.setReferenceType(baseServiceImpl.customer.getType());
		voucherTopupDTO.setCustomerAccount(accountDto);
		voucherTopupDTO.setOperatorID(operatorId + "");

		try {
			// spring web servicer call : commented by bidyut
			// voucherTopupDTO = utilityServicesCleintSub.voucherTopup(voucherTopupDTO);

			// rest call to core : modified by bidyut
			voucherTopupDTO = processRequest(CoreUrls.TOPUP_URL, voucherTopupDTO, com.eot.dtos.utilities.VoucherTopupDTO.class);
			if (voucherTopupDTO.getErrorCode() != 0) {
				throw new EOTException(voucherTopupDTO.getErrorCode());
			}
		} /*
			 * catch (Exception e) { e.printStackTrace(); throw new
			 * EOTException(ErrorConstants.SERVICE_ERROR);
			 * 
			 * //throw new EOTException(Integer.parseInt(e.getMessageKey())); }
			 */
		finally {

		}
	//	String date = DateUtil.formatDateToStr(new Date());
		/*
		 * topUpDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage(
		 * "TOPUP_SUCCESS", new
		 * String[]{operator.getOperatorName(),voucherTopupDTO.getVoucherNo(),date,
		 * accountAlias,amount.toString()}, new
		 * Locale(baseServiceImpl.customer.getDefaultLanguage())));
		 * topUpDTO.setOperatorName(operator.getOperatorName());
		 * topUpDTO.setVoucherNumber(voucherTopupDTO.getVoucherNo());
		 * topUpDTO.setAccountBalance(voucherTopupDTO.getCustomerAccount().
		 * getAccountBalance());
		 * topUpDTO.setServiceChargeAmt(voucherTopupDTO.getServiceChargeAmt());
		 */

		/*
		 * @ Author name <vinod joshi>, Date<7/17/2018>, purpose of change <Recipt
		 * Modification > ,
		 */
		/* @ Start */
		// baseServiceImpl.customer.getCountry().getIsdCode().toString() +
		// baseServiceImpl.customer.getMobileNumber()
		topUpDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TOPUP_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != topUpDTO.getAmount() ? new DecimalFormat("#0.00").format( topUpDTO.getAmount().doubleValue()) : "0.00", null != voucherTopupDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(voucherTopupDTO.getServiceChargeAmt().doubleValue()) : "0.00", operator.getOperatorName(), voucherTopupDTO.getVoucherNo(), topUpDTO.getRemarks() == null || topUpDTO.getRemarks().equals("")?  " Airtime" :topUpDTO.getRemarks(), voucherTopupDTO.getTransactionNO() + "" },

				new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		topUpDTO.setOperatorName(operator.getOperatorName());
		topUpDTO.setVoucherNumber(voucherTopupDTO.getVoucherNo());
		topUpDTO.setAccountBalance(voucherTopupDTO.getCustomerAccount().getAccountBalance());
		topUpDTO.setServiceChargeAmt(voucherTopupDTO.getServiceChargeAmt());
		topUpDTO.setTransactionNO(voucherTopupDTO.getTransactionNO());
		/* @ End */
		return topUpDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eot.banking.service.TransactionService#processChequeEnquiry(com.eot.
	 * banking.dto.ChequeEnquiryDTO)
	 */
	public ChequeEnquiryDTO processChequeEnquiry(ChequeEnquiryDTO chequeEnquiryDTO) throws EOTException {

		baseServiceImpl.handleRequest(chequeEnquiryDTO);
		String accountAlias = chequeEnquiryDTO.getAccountAlias();
		String chequeNumber = chequeEnquiryDTO.getChequeNumber();

		ChequeStatusDTO chequeStatusDTO = new ChequeStatusDTO();
		chequeStatusDTO.setChequeNumber(chequeNumber);

		try {
			CBSBankingStubProxy bankingStubProxy = new CBSBankingStubProxy();
			chequeStatusDTO = bankingStubProxy.handleCBSChequeStatus(chequeStatusDTO);

		} catch (CBSException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.INVALID_CHEQUE_NUMBER);
		} catch (RemoteException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

		if (baseServiceImpl.customer.getBankCustomerId() == null) {
			throw new EOTException(ErrorConstants.INVALID_BANK_CUSTOMER_ID_FOR_CHEQUE);
		}

		CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), accountAlias);

		if (customerAccount == null) {
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}
		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_BANK_ACC + "");
		accountDto.setBankCode(customerAccount.getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getBranch().getBranchId().toString());

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(baseServiceImpl.referenceType);
		serviceChargeDebitDTO.setRequestID(baseServiceImpl.requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(chequeEnquiryDTO.getTransactionType().toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			// spring web service call : commented by bidyut
			// utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);

			// rest call to core : modified by bidyut
			serviceChargeDebitDTO = processRequest(CoreUrls.TOPUP_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);
			if (serviceChargeDebitDTO.getErrorCode() != 0) {
				throw new EOTException(serviceChargeDebitDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

		} /*
			 * catch (Exception e) { e.printStackTrace(); throw new
			 * EOTException(ErrorConstants.SERVICE_ERROR); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); }
			 */
		finally {

		}

		chequeEnquiryDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CHEQUE_" + chequeStatusDTO.getStatus(), new String[] { chequeEnquiryDTO.getChequeNumber() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		chequeEnquiryDTO.setChequeStatus(chequeStatusDTO.getStatus());

		return chequeEnquiryDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processSaleRequest(com.eot.banking
	 * .dto.SaleDTO)
	 */
	public SaleDTO processSaleRequest(SaleDTO dto) throws EOTException {

		baseServiceImpl.handleRequest(dto);
		String toAlias = dto.getAccountAlias();
		Integer toAliasType = dto.getAliasType();
		String fromAlias = dto.getCustomerAccountAlias();
		Integer fromAliasType = dto.getCustomerAccountAliasType();
		String customerMobileNumer = dto.getCustomerMobileNumber();
		Double amount = dto.getAmount();
		String customerPin = dto.getCustomerOTP();
		String merchantPin = dto.getTransactionPIN();
		System.out.println("*********** SaleHandler *************");

		Customer saleCust = baseServiceImpl.eotMobileDao.getCustomerFromMobileNo(customerMobileNumer);
		if (saleCust == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(saleCust.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		if (!merchantPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		// Validation for customer type
		if (saleCust.getType() != Constants.REF_TYPE_CUSTOMER) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		if (baseServiceImpl.customer.getType() == Constants.REF_TYPE_AGENT) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		if (!dto.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		if (otpForSaleEnabled) {
			OtpDTO otpDto = new OtpDTO();
			otpDto.setOtphash(customerPin);
			otpDto.setReferenceId(saleCust.getCustomerId() + "");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_WITHDRAWA);
			otpDto.setAmount(dto.getAmount());
			//Otp otp = baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
			Otp otp = baseServiceImpl.eotMobileDao.verifyOTPWithAmount(otpDto);

			System.out.println("customerPin - " + customerPin);
			System.out.println("db - " + otpDto.getOtphash());
			if (otp == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}
			otp.setStatus(OtpStatusEnum.USED.getCode());
			baseServiceImpl.eotMobileDao.update(otp);
		}
			/*if (!customerPin.equals(saleCust.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_TXN_PIN);
			}*/
		/*
		 * if
		 * (!dto.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin()
		 * .toString())) { throw new EOTException(ErrorConstants.INVALID_TXN_PIN); }
		 */
			 

		com.eot.dtos.common.Account customerAccount = new com.eot.dtos.common.Account();

		customerAccount.setAccountAlias(fromAlias);
		customerAccount.setAccountType(fromAliasType.toString());

		CustomerAccount custWalletAccount = null;
		CustomerAccount merchantWalletAccount = null;

		if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(saleCust.getCustomerId(), fromAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			try {
				customerAccount.setAccountNO(new String(baseServiceImpl.kmsHandler.externalDbDesOperation(HexString.hexToBuffer(card.getCardNumber()), false)));
			} catch (NumberFormatException e) {
//				e.printStackTrace();
			} catch (KMSSecurityException e) {
//				e.printStackTrace();
			}
			customerAccount.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

			custWalletAccount = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(saleCust.getCustomerId(), fromAlias);

			if (custWalletAccount == null) {
				throw new EOTException(ErrorConstants.MERCHANT_ACCOUNT_NOT_FOUND);
			}

			customerAccount.setAccountNO(custWalletAccount.getAccountNumber());
			customerAccount.setBankCode(custWalletAccount.getBank().getBankId().toString());
			customerAccount.setBranchCode(custWalletAccount.getBranch().getBranchId().toString());

		}

		com.eot.dtos.common.Account merchantAccount = new com.eot.dtos.common.Account();

		merchantAccount.setAccountAlias(toAlias);
		merchantAccount.setAccountType(toAliasType.toString());

		if (Constants.ALIAS_TYPE_CARD_ACC == toAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), toAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			merchantAccount.setAccountNO(card.getCardNumber());
			merchantAccount.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == toAliasType) {

			merchantWalletAccount = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), toAlias);

			if (merchantWalletAccount == null) {
				throw new EOTException(ErrorConstants.CUSTOMER_ACCOUNT_NOT_FOUND);
			}

			merchantAccount.setAccountNO(merchantWalletAccount.getAccountNumber());
			merchantAccount.setBankCode(merchantWalletAccount.getBank().getBankId().toString());
			merchantAccount.setBranchCode(merchantWalletAccount.getBranch().getBranchId().toString());

		}

		List<ClearingHousePoolMember> chPoolList = baseServiceImpl.eotMobileDao.getClearingHouse(new Integer(customerAccount.getBankCode()), new Integer(merchantAccount.getBankCode()));

		if (chPoolList == null) {
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}

		com.eot.dtos.banking.SaleDTO saleDTO = new com.eot.dtos.banking.SaleDTO();

		saleDTO.setTransactionType(dto.getTransactionType().toString());
		saleDTO.setAmount(amount);
		saleDTO.setCustomerAccount(customerAccount);
		saleDTO.setOtherAccount(merchantAccount);
		saleDTO.setCustomerID(saleCust.getCustomerId().toString());
		saleDTO.setRequestID(baseServiceImpl.requestID.toString());
		saleDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		saleDTO.setReferenceType(baseServiceImpl.referenceType);
		saleDTO.setTransDesc("Sale");
		saleDTO.setChannelType(Constants.EOT_CHANNEL);
		saleDTO.setRemarks("Sale");

		try {

			// saleDTO = bankingServiceClientStub.sale(saleDTO);

			// rest call to core : modified by bidyut
			
			saleDTO = processRequest(CoreUrls.SALE_URL, saleDTO, com.eot.dtos.banking.SaleDTO.class);
			if (saleDTO.getErrorCode() != 0) {
				throw new EOTException(saleDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(saleDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);
			 

			// =================Sale Request Save to Pending transaction==================
		/*	TransactionType transactionType = new TransactionType();
			transactionType.setTransactionType(Constants.TXN_ID_MERCHANT_SALE);

			PendingTransaction pendingTransaction = new PendingTransaction();
			pendingTransaction.setAmount(dto.getAmount());
			pendingTransaction.setCustomer(saleCust);
			pendingTransaction.setReferenceType(saleCust.getType());
			pendingTransaction.setCustomerAccount(customerAccount.getAccountNO());
			pendingTransaction.setBank(merchantWalletAccount.getBank());
			pendingTransaction.setCustomerAccountType(Constants.ALIAS_TYPE_MOBILE_ACC);
			pendingTransaction.setInitiatedBy(baseServiceImpl.customer.getCustomerId() + "");
			pendingTransaction.setOtherAccount(merchantWalletAccount.getAccountNumber());
			pendingTransaction.setOtherAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC);
			pendingTransaction.setStatus(Constants.TRANSACTION_INITIATEDBY_MERCHANT);
			pendingTransaction.setTransactionType(transactionType);
			pendingTransaction.setTransactionDate(new Date());

			baseServiceImpl.eotMobileDao.save(pendingTransaction);*/
			// ===================================================================================

		} /*
			 * catch (Exception e) { e.printStackTrace(); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); throw new
			 * EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}

		/*
		 * String message = baseServiceImpl.messageSource.getMessage("SALE_SUCCESS", new
		 * String[]{DateUtil.formattedDateAndTime(new
		 * Date()),baseServiceImpl.customer.getCustomerId().toString(),
		 * baseServiceImpl.customer.getFirstName(),baseServiceImpl.customer.getCity().
		 * getCity(),saleCust.getFirstName(),saleCust.getMobileNumber(),
		 * fromAlias,amount.toString(),saleDTO.getTransactionNO()}, new
		 * Locale(baseServiceImpl.customer.getDefaultLanguage()));
		 */

		/*
		 * @ Author name <vinod joshi>, Date<7/17/2018>, purpose of change <Recipt
		 * Modification > ,
		 */
		/* @ Start */
	//	String message = baseServiceImpl.messageSource.getMessage("SALE_REQUEST", new String[] { DateUtil.formattedDateAndTime(new Date()), null != saleDTO.getAmount() ? new DecimalFormat("#0.00").format(saleDTO.getAmount().doubleValue()):"0.00", saleCust.getMobileNumber(), saleCust.getFirstName(), dto.getRemarks() != null ? dto.getRemarks() : "Sale"}, new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		String message = baseServiceImpl.messageSource.getMessage("SALE_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != saleDTO.getAmount() ? new DecimalFormat("#0.00").format(saleDTO.getAmount().doubleValue()):"0.00",  null != saleDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(saleDTO.getServiceChargeAmt().doubleValue()):"0.00", dto.getCustomerMobileNumber(), saleCust.getFirstName(), dto.getRemarks() != null ? dto.getRemarks() : "Sale", saleDTO.getTransactionNO()}, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

		/* @ End */
		dto.setSuccessResponse(message);
		dto.setCustomerName(saleCust.getFirstName());
		dto.setMerchantCode(baseServiceImpl.customer.getCustomerId().toString());
		dto.setMerchantLocation(baseServiceImpl.customer.getCity().getCity());
		dto.setMerchantName(baseServiceImpl.customer.getFirstName());
		dto.setTxnAuthCode(saleDTO.getTransactionNO());
		dto.setRemarks("Sale");

		return dto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processSMSCashRequest(com.eot.
	 * banking.dto.SMSCashDTO)
	 */
	public SMSCashDTO processSMSCashRequest(SMSCashDTO dto) throws EOTException, Exception {

		String fromAlias = dto.getAccountAlias();
		Integer fromAliasType = dto.getAliasType();
		String toMobileNo = dto.getPayeeMobileNumber();
		Double amount = dto.getAmount();
		String txnPin = dto.getTransactionPIN();

		if (null != dto.getApplicationId()) {
			baseServiceImpl.handleRequest(dto);
			if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
			}
		} else {
			baseServiceImpl.customer = baseServiceImpl.eotMobileDao.getCustomerAccountFromAccountNumber(dto.getAccountNumber()).getCustomer();
		}
		String authMode = "";
		if (dto.getPinGenerationMode().intValue() == Constants.PIN_TO_BENEFICIARY.intValue()) {
			authMode = "PIN To Receiver";
		} else if (dto.getPinGenerationMode().intValue() == Constants.PIN_TO_REMITTER.intValue()) {
			authMode = "Remitter";
		} else if (dto.getPinGenerationMode().intValue() == Constants.PIN_TO_BOTH.intValue()) {
			authMode = "PIN to Both";
		}
		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		accountDto.setAccountAlias(fromAlias);

		accountDto.setAccountType(fromAliasType.toString());

		ExternalTransaction externalTransaction = new ExternalTransaction();

		if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());
			externalTransaction.setExternalEntityId(card.getBank().getBankId());
			externalTransaction.setEntityName(card.getBank().getBankName());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			/*
			 * CustomerCard virtualCard =
			 * baseServiceImpl.eotMobileDao.getVirtualCardforBank(account.getBank().
			 * getBankId().toString());
			 * 
			 * if (virtualCard == null) { throw new
			 * EOTException(ErrorConstants.BANK_CARD_NOT_FOUND); }
			 */

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());
			accountDto.setAccountBalance(account.getAccount().getCurrentBalance());
			externalTransaction.setExternalEntityId(account.getBank().getBankId());
			externalTransaction.setEntityName(account.getBank().getBankName());
		} else if (Constants.ALIAS_TYPE_FI_ACC == fromAliasType) {
			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(baseServiceImpl.customer.getCustomerId());
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());
			accountDto.setAccountBalance(account.getAccount().getCurrentBalance());
			externalTransaction.setExternalEntityId(account.getBank().getBankId());
			externalTransaction.setEntityName(account.getBank().getBankName());
		}

		externalTransaction.setAmount(dto.getAmount());
		externalTransaction.setBeneficiaryMobileNumber(toMobileNo);
		externalTransaction.setCustomerName(baseServiceImpl.customer.getFirstName());
		externalTransaction.setMobileNumber(baseServiceImpl.customer.getMobileNumber());
		externalTransaction.setTransactionDate(new Date());
		externalTransaction.setStan(dto.getStan());
		externalTransaction.setTransactionType(dto.getTransactionType());

		com.eot.dtos.banking.SMSCashDTO smsCashDTO = new com.eot.dtos.banking.SMSCashDTO();

		smsCashDTO.setCustomerAccount(accountDto);
		smsCashDTO.setAmount(amount);
		smsCashDTO.setChannelType(Constants.EOT_CHANNEL);
		// smsCashDTO.setRequestID(baseServiceImpl.requestID.toString());
		smsCashDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		smsCashDTO.setReferenceType(baseServiceImpl.customer.getType());
		smsCashDTO.setTransactionType(dto.getTransactionType().toString());
		smsCashDTO.setPayeeMobileNumber(toMobileNo);
		smsCashDTO.setWithdrawalMode(dto.getWithdrawalMode());
		smsCashDTO.setPinGenerationMode(dto.getPinGenerationMode());
		// smsCashDTO.setPinGenerationMode(1);
		smsCashDTO.setWithdrawalMode(Constants.MERCHANT_NETWORK);

		try {
			// spring service call to core :commented by bidyut
			// smsCashDTO = bankingServiceClientStub.smsCash(smsCashDTO);

			// rest call to core : modified by bidyut
			smsCashDTO = processRequest(CoreUrls.SMS_CASH_URL, smsCashDTO, com.eot.dtos.banking.SMSCashDTO.class);
			if (smsCashDTO.getErrorCode() != 0) {
				throw new EOTException(smsCashDTO.getErrorCode());
			}

			dto.setAccountBalance(smsCashDTO.getCustomerAccount().getAccountBalance());
			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(smsCashDTO.getTransactionNO()));
			if (null != dto.getApplicationId()) {
				baseServiceImpl.mobileRequest.setTransaction(txn);
			}
			externalTransaction.setServiceChargeAmount(smsCashDTO.getServiceChargeAmt());
			externalTransaction.setTransactionId(new Long(smsCashDTO.getTransactionNO()));
			externalTransaction.setStatus(Constants.TXN_STATUS_SMS_CASH_INITIATED);
			externalTransaction.setTxnPin(HashUtil.generateHash(smsCashDTO.getSmsCashPin().getBytes(), Constants.PIN_HASH_ALGORITHM));
			baseServiceImpl.eotMobileDao.save(externalTransaction);
			if(dto.getRemarks().equals("") || dto.getRemarks()==null){
				dto.setRemarks("Non Registered User");
			}
			externalTransaction.setStatus(Constants.TXN_STATUS_SMS_CASH_INITIATED);


		} /*
			 * catch (EOTCoreException e) { e.printStackTrace();
			 * System.out.println(e.getMessageKey()); throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); }
			 *//*
				 * catch (Exception e) { e.printStackTrace(); throw new
				 * EOTException(ErrorConstants.SERVICE_ERROR); }
				 */
		finally {

		}
		dto.setSuccessResponse(baseServiceImpl.messageSource.getMessage("SMS_CASH_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()):"0.00", null != smsCashDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(smsCashDTO.getServiceChargeAmt().doubleValue()):"0.00", toMobileNo, authMode, dto.getRemarks() != null ? dto.getRemarks() : "Non Registered User", smsCashDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		dto.setSmsCashPIN(smsCashDTO.getSmsCashPin());
		dto.setServiceChargeAmt(smsCashDTO.getServiceChargeAmt());

		return dto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processLastTransactionReceipt(com.
	 * eot.banking.dto.LastTxnReceiptDTO)
	 */
	public LastTxnReceiptDTO processLastTransactionReceipt(LastTxnReceiptDTO lastTxnReceiptDTO) throws EOTException {

		System.out.println("******** processLastTransactionReceipt *************");

		baseServiceImpl.handleRequest(lastTxnReceiptDTO);
		Integer txnTypeOfLastTransaction = lastTxnReceiptDTO.getTypeOfTxnReceipt();

		List<MobileRequest> listOfMobileRequest = baseServiceImpl.eotMobileDao.lastSuccessfulTransaction(lastTxnReceiptDTO.getApplicationId(), txnTypeOfLastTransaction);

		if (listOfMobileRequest == null) {
			throw new EOTException(ErrorConstants.LAST_TXN_NOT_AVAILABLE);
		}

		ArrayList<TransactionRecipt> listOfLastTxnRecipt = new ArrayList<LastTxnReceiptDTO.TransactionRecipt>();

		for (MobileRequest mobileRequest : listOfMobileRequest) {

			TransactionRecipt transactionRecipt = lastTxnReceiptDTO.new TransactionRecipt();

			try {
				transactionRecipt.setTransactionTime(mobileRequest.getTransactionTime().getTime());
				byte[] blobData = mobileRequest.getResponseString().getBytes(1, (int) mobileRequest.getResponseString().length());
				//transactionRecipt.setTxnResponse(new String(blobData));
			} catch (Exception e) {
//				e.printStackTrace();
				throw new EOTException(ErrorConstants.SERVICE_ERROR);
			}

			listOfLastTxnRecipt.add(transactionRecipt);
		}
		lastTxnReceiptDTO.setListOfLastTxnRecipt(listOfLastTxnRecipt);

		return lastTxnReceiptDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processDeposit(com.eot.banking.dto
	 * .TransactionBaseDTO)
	 */
	@Override
	public TransactionBaseDTO processDeposit(TransactionBaseDTO transactionBaseDTO) throws EOTException {

		baseServiceImpl.handleRequest(transactionBaseDTO);
		if (!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(transactionBaseDTO.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}
		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(transactionBaseDTO.getMobileNumber());
		if (customer == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountFromAlias(customer.getCustomerId(), transactionBaseDTO.getAccountAlias());
		if (account == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
		}
		if (customer.getType() != Constants.REF_TYPE_CUSTOMER) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		
		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(customer.getAppId());
		
		if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){  // validate status of merchant/customer
					throw new EOTException(ErrorConstants.AGENT_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_ACC_BLOCKED);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_KYC_PENDING);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_KYC_REJECTED);
				}
				
				if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER) {
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_BLOCKED);
				}else if( customer.getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
				}
				
				if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
					throw new EOTException(ErrorConstants.MERCHANT_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_ACC_BLOCKED);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_KYC_PENDING);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_KYC_REJECTED);
				}
				
				if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_SUSPENDED) {
					throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
				}


		Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(Constants.DEFAULT_BANK);
		Account bankAccount = bank.getAccount();
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(Constants.DEFAULT_BRANCH);

		Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		// load Merchant account
		CustomerAccount merchantAccount = baseServiceImpl.eotMobileDao.getAgentPoolAccountFromAgentId(merchantDetails.getCustomerId());

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(transactionBaseDTO.getAccountAlias());
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		/*
		 * this is not required for agent deposite transaction
		 * com.eot.dtos.common.Account bankAccountDto = new
		 * com.eot.dtos.common.Account();
		 * 
		 * bankAccountDto.setAccountAlias(bankAccount.getAlias());
		 * bankAccountDto.setAccountNO(merchantAccount.getAccountNumber());
		 * bankAccountDto.setAccountType(Constants.ALIAS_TYPE_OTHER+"");
		 * bankAccountDto.setBankCode((merchantAccount.getBank().getBankId().toString())
		 * ); bankAccountDto.setBranchCode(merchantAccount.getBranch().getBranchId().
		 * toString());
		 */

		com.eot.dtos.common.Account merchantAccountDTO = new com.eot.dtos.common.Account();
		merchantAccountDTO.setAccountNO(merchantAccount.getAccountNumber());
		merchantAccountDTO.setAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC + "");
		merchantAccountDTO.setBankCode(merchantAccount.getBank().getBankId().toString());
		merchantAccountDTO.setBranchCode(merchantAccount.getBranch().getBranchId().toString());

		DepositDTO depositDTO = new DepositDTO();

		depositDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		depositDTO.setAmount(transactionBaseDTO.getAmount());
		depositDTO.setChannelType(Constants.EOT_CHANNEL);
		depositDTO.setCustomerAccount(accountDto);
		depositDTO.setOtherAccount(merchantAccountDTO);
		depositDTO.setTransactionType(Constants.TXN_TYPE_MERCHANT_DEPOSIT + "");
		// depositDTO.setCommissionToBePaidId(merchantDetails.getCustomerId()+"");

		try {
			// spring web service call to core: commented by bidyut
			// depositDTO = bankingServiceClientStub.deposit(depositDTO);

			// rest call to core : modified by bidyut
			depositDTO = processRequest(CoreUrls.DEPOSITE_TXN_URL, depositDTO, com.eot.dtos.banking.DepositDTO.class);
			if (depositDTO.getErrorCode() != 0) {
				throw new EOTException(depositDTO.getErrorCode());
			}
			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(depositDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

		} /*
			 * catch (Exception e) { e.printStackTrace();
			 * //System.out.println(e.getMessageKey()); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); throw new
			 * EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}

		/*
		 * String message = baseServiceImpl.messageSource.getMessage("DEPOSIT_SUCCESS",
		 * new String[]{DateUtil.formattedDateAndTime(new
		 * Date()),transactionBaseDTO.getMobileNumber(),
		 * transactionBaseDTO.getAccountAlias(),baseServiceImpl.customer.getFirstName(),
		 * baseServiceImpl.customer.getCity().getCity(),
		 * transactionBaseDTO.getAmount().toString(),depositDTO.getTransactionNO()}, new
		 * Locale(baseServiceImpl.customer.getDefaultLanguage()));
		 */

		/*
		 * Author name <vinod joshi>, Date<6/22/2018>, purpose of change <Recipt
		 * Modification > ,
		 */
		/* Start */
	//	String message = baseServiceImpl.messageSource.getMessage("DEPOSIT_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != transactionBaseDTO.getAmount() ? new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00", null != depositDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format( depositDTO.getServiceChargeAmt().doubleValue() ):"0.00", transactionBaseDTO.getMobileNumber(), customer.getFirstName(), transactionBaseDTO.getRemarks() != null ? transactionBaseDTO.getRemarks() : "Cash Deposit", depositDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		String message = baseServiceImpl.messageSource.getMessage("DEPOSIT_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != transactionBaseDTO.getAmount() ? new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00", transactionBaseDTO.getMobileNumber(), getName(customer.getFirstName(), customer.getLastName()), transactionBaseDTO.getRemarks() != null ? transactionBaseDTO.getRemarks() : "Cash Deposit", depositDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));
			/* End */
		transactionBaseDTO.setSuccessResponse(message);

		return transactionBaseDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processWithdrawal(com.eot.banking.
	 * dto.TransactionBaseDTO)
	 */
	@Override
	public WithdrawalTransactionsDTO processWithdrawal(WithdrawalTransactionsDTO transactionBaseDTO) throws EOTException {

		baseServiceImpl.handleRequest(transactionBaseDTO);
		/*
		 * if(!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.
		 * getTransactionPin().toString())){ throw new
		 * EOTException(ErrorConstants.INVALID_TXN_PIN); }
		 */

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(transactionBaseDTO.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobile(transactionBaseDTO.getMobileNumber());
		if (customer == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED) {
			throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
		}
		if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_SUSPENDED) {
			throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
		}
		// Validation for customer type
		if(transactionBaseDTO.getTransactionType().intValue()==EOTConstants.TXN_TYPE_MERCHANT_PAYOUT)
		{
			if (customer.getType() != Constants.REF_TYPE_MERCHANT) {
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			}
			if (baseServiceImpl.customer.getType() != Constants.REF_TYPE_AGENT) {
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			}
		}else if (customer.getType() != Constants.REF_TYPE_CUSTOMER) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		if (!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		if (otpForSaleEnabled) {
			OtpDTO otpDto = new OtpDTO();
			otpDto.setOtphash(transactionBaseDTO.getCustomerOTP());
			otpDto.setReferenceId(customer.getCustomerId() + "");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			//otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_WITHDRAWA);
			otpDto.setAmount(transactionBaseDTO.getAmount());
			Otp otp = baseServiceImpl.eotMobileDao.verifyOTPWithAmount(otpDto);
			//Otp otp = baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
			System.out.println("baseServiceImpl.customerPin - " + transactionBaseDTO.getCustomerOTP());
			System.out.println("db - " + otpDto.getOtphash());
			if (otp == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}
			
			otp.setStatus(OtpStatusEnum.USED.getCode());
			baseServiceImpl.eotMobileDao.update(otp);

		} 
			//
			// if
			// (!transactionBaseDTO.getCustomerOTP().equals(customer.getTransactionPin().toString()))
			// {
			// throw new EOTException(ErrorConstants.INVALID_CUSTOMER_TXN_PIN);
			// }



		CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountFromAlias(customer.getCustomerId(), transactionBaseDTO.getAccountAlias());
		if (account == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
		}

		Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(Constants.DEFAULT_BANK);
		Account bankAccount = bank.getAccount();
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(Constants.DEFAULT_BRANCH);

		Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		// load Merchant account
		CustomerAccount merchantAccount = baseServiceImpl.eotMobileDao.getAgentPoolAccountFromAgentId(merchantDetails.getCustomerId());

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(transactionBaseDTO.getAccountAlias());
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		/*
		 * com.eot.dtos.common.Account bankAccountDto = new
		 * com.eot.dtos.common.Account();
		 * 
		 * bankAccountDto.setAccountAlias(bankAccount.getAlias());
		 * bankAccountDto.setAccountNO(merchantAccount.getAccountNumber());
		 * bankAccountDto.setAccountType(Constants.ALIAS_TYPE_OTHER+"");
		 * //bankAccountDto.setBankCode(bank.getBankId().toString());
		 * //bankAccountDto.setBranchCode(branch.getBranchId().toString());
		 * bankAccountDto.setBankCode(merchantAccount.getBank().getBankId().toString());
		 * bankAccountDto.setBranchCode(merchantAccount.getBranch().getBranchId().
		 * toString());
		 */

		com.eot.dtos.common.Account customerAccountDTO = new com.eot.dtos.common.Account();

		// merchant account details
		// bankAccountDto.setAccountAlias(bankAccount.getAlias());
		customerAccountDTO.setAccountNO(merchantAccount.getAccountNumber());
		customerAccountDTO.setAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC + "");
		customerAccountDTO.setBankCode(merchantAccount.getBank().getBankId().toString());
		customerAccountDTO.setBranchCode(merchantAccount.getBranch().getBranchId().toString());

		WithdrawalDTO withdrawalDTO = new WithdrawalDTO();

		withdrawalDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		withdrawalDTO.setAmount(transactionBaseDTO.getAmount());
		withdrawalDTO.setChannelType(Constants.EOT_CHANNEL);
		withdrawalDTO.setCustomerAccount(accountDto);
		withdrawalDTO.setOtherAccount(customerAccountDTO);
		withdrawalDTO.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL + "");
		// withdrawalDTO.setCommissionToBePaidId(merchantDetails.getCustomerId()+"");

		try {
			//===========================================================================================
			// spring web service call to core: commentec by bidyut
			// withdrawalDTO = bankingServiceClientStub.withdrawal(withdrawalDTO);

			// rest call to core : modified by bidyut
			if (transactionBaseDTO.getTransactionType().intValue() == EOTConstants.TXN_TYPE_MERCHANT_PAYOUT) {
				withdrawalDTO.setTransactionType(Constants.TXN_ID_MERCHANT_MERCHANT_PAYOUT + "");
				withdrawalDTO = processRequest(CoreUrls.WITHDRAWAL_TXN_URL, withdrawalDTO, com.eot.dtos.banking.WithdrawalDTO.class);
				if (withdrawalDTO.getErrorCode() != 0) {
					throw new EOTException(withdrawalDTO.getErrorCode());
				}
			} else {
				withdrawalDTO = processRequest(CoreUrls.WITHDRAWAL_TXN_URL, withdrawalDTO, com.eot.dtos.banking.WithdrawalDTO.class);
				if (withdrawalDTO.getErrorCode() != 0) {
					throw new EOTException(withdrawalDTO.getErrorCode());
				}
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(withdrawalDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);
			
			String customerType = "";
			if (merchantDetails.getType() == Constants.REF_TYPE_AGENT) {
				customerType = "Agent";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_MERCHANT) {
				customerType = "Merchant";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_AGENT_SOLE_MERCHANT) {
				customerType = "ASM";
			}

			String message = baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()),null != transactionBaseDTO.getAmount() ?  new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00", null != withdrawalDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(withdrawalDTO.getServiceChargeAmt().doubleValue()):"0.00", transactionBaseDTO.getMobileNumber(), customer.getFirstName() /*+ " " + merchantDetails.getLastName()*/, withdrawalDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

			
			if(transactionBaseDTO.getTransactionType().intValue()==EOTConstants.TXN_TYPE_MERCHANT_PAYOUT)
			{
				message = baseServiceImpl.messageSource.getMessage("MERCHANT_PAY_OUT", new String[] { DateUtil.formattedDateAndTime(new Date()),null != transactionBaseDTO.getAmount() ?  new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00", null != withdrawalDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(withdrawalDTO.getServiceChargeAmt().doubleValue()):"0.00", transactionBaseDTO.getMobileNumber(), getName(customer.getFirstName(),customer.getLastName()) /*+ " " + merchantDetails.getLastName()*/, withdrawalDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

				
			}
			transactionBaseDTO.setTransactionType(transactionBaseDTO.getTransactionType());
			transactionBaseDTO.setSuccessResponse(message);
			//===========================================================================================

			// =================Withdrawal Request Save to Pending transaction==================
			/*TransactionType transactionType = new TransactionType();
			transactionType.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL);

			PendingTransaction pendingTransaction = new PendingTransaction();
			pendingTransaction.setAmount(transactionBaseDTO.getAmount());
			pendingTransaction.setCustomer(customer);
			pendingTransaction.setReferenceType(customer.getType());
			pendingTransaction.setCustomerAccount(account.getAccountNumber());
			pendingTransaction.setBank(account.getBank());
			pendingTransaction.setCustomerAccountType(Constants.ALIAS_TYPE_MOBILE_ACC);
			pendingTransaction.setInitiatedBy(merchantDetails.getCustomerId() + "");
			pendingTransaction.setOtherAccount(merchantAccount.getAccountNumber());
			pendingTransaction.setOtherAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC);
			pendingTransaction.setStatus(Constants.TRANSACTION_INITIATEDBY_MERCHANT);
			pendingTransaction.setTransactionType(transactionType);
			pendingTransaction.setTransactionDate(new Date());

			baseServiceImpl.eotMobileDao.save(pendingTransaction);
			
			String message = baseServiceImpl.messageSource.getMessage("WITHDRAWAL_REQUEST", new String[] { DateUtil.formattedDateAndTime(new Date()), new DecimalFormat("#.00").format(transactionBaseDTO.getAmount()) , customer.getMobileNumber(), customer.getFirstName(), transactionBaseDTO.getRemarks() != null ? transactionBaseDTO.getRemarks() : "Cash Withdrawal", "Pending"/* ,withdrawalDTO.getTransactionNO()  }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

			transactionBaseDTO.setSuccessResponse(message);
			*/
			// ===================================================================================
			
			

		} 
		/*catch (EOTCoreException e) {
			e.printStackTrace();
			System.out.println(e.getMessageKey());
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}*/
			 
		/*
		 * catch (Exception e) { throw new EOTException(ErrorConstants.SERVICE_ERROR); }
		 */
		finally {

		}
		/*
		 * String message =
		 * baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new
		 * String[]{DateUtil.formattedDateAndTime(new
		 * Date()),transactionBaseDTO.getMobileNumber(),
		 * transactionBaseDTO.getAccountAlias(),baseServiceImpl.customer.getFirstName(),
		 * baseServiceImpl.customer.getCity().getCity(),
		 * transactionBaseDTO.getAmount().toString(),withdrawalDTO.getTransactionNO()},
		 * new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		 */
		/*
		 * @ Author name <vinod joshi>, Date<7/17/2018>, purpose of change <Recipt
		 * Modification > ,
		 */
		/* @ Start */
		/*
		 * String message =
		 * baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new
		 * String[]{DateUtil.formattedDateAndTime(new
		 * Date()),transactionBaseDTO.getAmount().toString(),
		 * withdrawalDTO.getServiceChargeAmt()+"",transactionBaseDTO.getAccountAlias(),
		 * customer.getFirstName(),transactionBaseDTO.getMobileNumber(),baseServiceImpl.
		 * customer.getFirstName()
		 * ,baseServiceImpl.customer.getMobileNumber(),baseServiceImpl.customer.getCity(
		 * ).getCity(), withdrawalDTO.getTransactionNO()}, new
		 * Locale(baseServiceImpl.customer.getDefaultLanguage()));
		 */
		/* End */
		transactionBaseDTO.setUsername(merchantDetails.getFirstName() + merchantDetails.getLastName());
		return transactionBaseDTO;
	}
	
	
	
	@Override
	public TransactionBaseDTO requestWithdrawal(TransactionBaseDTO transactionBaseDTO) throws EOTException {

		Integer actualOtpTransactionType = transactionBaseDTO.getTransactionType();
		transactionBaseDTO.setTransactionType(EOTConstants.TXN_TYPE_Transaction_OTP);
		
		baseServiceImpl.handleRequest(transactionBaseDTO);
		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(transactionBaseDTO.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}
		String mobileNumber="";
		if(!transactionBaseDTO.getMobileNumber().startsWith("211"))
			mobileNumber="211"+transactionBaseDTO.getMobileNumber();
			

//		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(transactionBaseDTO.getMobileNumber());
		
		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(mobileNumber);
		if (customer == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		if(actualOtpTransactionType.intValue()==Constants.TXN_ID_REMITTANCE_OUTWARD_AGENT)
		{
			if(customer.getType().intValue()!=EOTConstants.REFERENCE_TYPE_CUSTOMER)
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		
		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(customer.getAppId());
				
				if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){  // validate status of merchant/customer
					throw new EOTException(ErrorConstants.AGENT_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_ACC_BLOCKED);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_KYC_PENDING);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
					throw new EOTException(ErrorConstants.AGENT_KYC_REJECTED);
				}
				
				if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER) {
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_BLOCKED);
				}else if( customer.getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
					throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
				}
				
				if (customer.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
					throw new EOTException(ErrorConstants.MERCHANT_ACC_DEACTIVATED);
				}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_ACC_BLOCKED);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_KYC_PENDING);
				}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
					throw new EOTException(ErrorConstants.MERCHANT_KYC_REJECTED);
				}
				
		
		
		
/*		if(transactionBaseDTO.getTransactionType().intValue()==Constants.TXN_ID_MERCHANT_MERCHANT_PAYOUT)
		{
			if (customer.getType() != Constants.REF_TYPE_MERCHANT) {// Validation for customer type
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			}
		}else if (customer.getType() != Constants.REF_TYPE_CUSTOMER) {// Validation for customer type
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}*/
	/*	CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountFromAlias(customer.getCustomerId(), transactionBaseDTO.getAccountAlias());
		if (account == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
		}*/
	
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(Constants.DEFAULT_BRANCH);
		Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		// load Merchant account
		CustomerAccount merchantAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(merchantDetails.getCustomerId());

		
			
		try {
			
			WebOTPAlertDTO dto = new WebOTPAlertDTO();
			dto.setLocale(customer.getDefaultLanguage());
			dto.setMobileNo(customer.getCountry().getIsdCode()+ customer.getMobileNumber());//transactionBaseDTO.getMobileNumber()
			dto.setOtpType(Constants.OTP_TYPE_WITHDRAWA);
			dto.setReferenceId(customer.getCustomerId().toString());
			dto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			dto.setScheduleDate(Calendar.getInstance());
			if(null!=transactionBaseDTO.getAmount())
				dto.setAmount(transactionBaseDTO.getAmount());
			if(actualOtpTransactionType==132)
				dto.setTransactionType(EOTConstants.TXN_TYPE_MERCHANT_PAYOUT.toString());
			else 
				dto.setTransactionType(actualOtpTransactionType.toString());
			
			if(actualOtpTransactionType==Constants.TXN_ID_REMITTANCE_OUTWARD_AGENT)
			{
				dto.setTransactionType(Constants.TXN_ID_REMITTANCE_OUTWARD_AGENT+"");
				dto.setOtpType(Constants.OTP_TYPE_REMITTANCE_OUTWARD);
			}
			
//			smsServiceClientStub.webOTPAlert(dto);
			SmsResponseDTO responseDTO=sendSMS(UrlConstants.WEB_OTP_ALERT, dto);
			if(responseDTO.getStatus().equalsIgnoreCase("0"))
				throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
			
			
			/*
			 * 
			String otp = generatePin();
			ExternalTransaction externalTransaction = new ExternalTransaction();
			externalTransaction.setAmount(transactionBaseDTO.getAmount());
			externalTransaction.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL);
			externalTransaction.setTxnPin(HashUtil.generateHash(otp.getBytes(), Constants.PIN_HASH_ALGORITHM));
			externalTransaction.setBeneficiaryMobileNumber(customer.getCountry().getIsdCode()+customer.getMobileNumber());
			externalTransaction.setCustomerName(customer.getFirstName());
			externalTransaction.setMobileNumber(baseServiceImpl.customer.getMobileNumber());
			externalTransaction.setTransactionDate(new Date());
			externalTransaction.setStan(transactionBaseDTO.getStan());
			baseServiceImpl.eotMobileDao.save(externalTransaction);
			
			CustomMsgAlertDTO dto = new CustomMsgAlertDTO();
			dto.setMessage("otp for withdrawal Transaction is : "+otp);
			dto.setMobileNo(customer.getCountry().getIsdCode()+customer.getMobileNumber());
			dto.setScheduleDate(Calendar.getInstance());
			dto.setLocale(baseServiceImpl.customer.getDefaultLanguage());
			smsServiceClientStub.customMsgAlert(dto);
			*/
		
			
		} catch (/*SmsServiceException e*/EOTException ex) {
			throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
		}
		/*catch (NoSuchAlgorithmException | UnsupportedEncodingException e ) {
			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		} */
		finally {

		}
		transactionBaseDTO.setUsername(merchantDetails.getFirstName() + merchantDetails.getLastName());
		String message = baseServiceImpl.messageSource.getMessage("OTP_GENERATED_SUCCESS", new String[] { "0" + customer.getMobileNumber()  }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));
//		String message="otp generated";
		transactionBaseDTO.setSuccessResponse(message);
		transactionBaseDTO.setMessageDescription(message);
		return transactionBaseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eot.banking.service.TransactionService#processEncashRequest(com.eot.
	 * banking.dto.SaleDTO)
	 */
	public SaleDTO processEncashRequest(SaleDTO dto) throws EOTException {

		System.out.println("*********** EnCashHandler *************");
		baseServiceImpl.handleRequest(dto);
		String toAlias = dto.getAccountAlias();
		Integer toAliasType = dto.getAliasType();
		String fromAlias = dto.getCustomerAccountAlias();
		Integer fromAliasType = dto.getCustomerAccountAliasType();
		String customerMobileNumer = dto.getCustomerMobileNumber();
		Double amount = dto.getAmount();
		String customerPin = dto.getCustomerOTP();
		String merchantPin = dto.getTransactionPIN();

		Customer encashCustomer = baseServiceImpl.eotMobileDao.getCustomerFromMobileNo(customerMobileNumer);
		if (encashCustomer == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(encashCustomer.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		if (!merchantPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		// Validation for customer type
		if (encashCustomer.getType() != Constants.REF_TYPE_CUSTOMER) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}

		if (otpForSaleEnabled) {
			OtpDTO otpDto = new OtpDTO();
			otpDto.setOtphash(customerPin);
			otpDto.setReferenceId(encashCustomer.getCustomerId() + "");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
			Otp otp = baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
			System.out.println("customerPin - " + customerPin);
			System.out.println("db - " + otpDto.getOtphash());
			if (otp == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}
		} else {

			if (!customerPin.equals(encashCustomer.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_TXN_PIN);
			}

		}

		com.eot.dtos.common.Account customerAccount = new com.eot.dtos.common.Account();

		customerAccount.setAccountAlias(fromAlias);
		customerAccount.setAccountType(fromAliasType.toString());

		if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(encashCustomer.getCustomerId(), fromAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			try {
				customerAccount.setAccountNO(new String(baseServiceImpl.kmsHandler.externalDbDesOperation(HexString.hexToBuffer(card.getCardNumber()), false)));
			} catch (NumberFormatException e) {
//				e.printStackTrace();
			} catch (KMSSecurityException e) {
//				e.printStackTrace();
			}
			customerAccount.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

			CustomerAccount walletAccount = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(encashCustomer.getCustomerId(), fromAlias);

			if (walletAccount == null) {
				throw new EOTException(ErrorConstants.MERCHANT_ACCOUNT_NOT_FOUND);
			}

			customerAccount.setAccountNO(walletAccount.getAccountNumber());
			customerAccount.setBankCode(walletAccount.getBank().getBankId().toString());
			customerAccount.setBranchCode(walletAccount.getBranch().getBranchId().toString());

		}

		com.eot.dtos.common.Account merchantAccount = new com.eot.dtos.common.Account();

		merchantAccount.setAccountAlias(toAlias);
		merchantAccount.setAccountType(toAliasType.toString());

		if (Constants.ALIAS_TYPE_CARD_ACC == toAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), toAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			merchantAccount.setAccountNO(card.getCardNumber());
			merchantAccount.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == toAliasType) {

			CustomerAccount walletAccount = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), toAlias);

			if (walletAccount == null) {
				throw new EOTException(ErrorConstants.CUSTOMER_ACCOUNT_NOT_FOUND);
			}

			merchantAccount.setAccountNO(walletAccount.getAccountNumber());
			merchantAccount.setBankCode(walletAccount.getBank().getBankId().toString());
			merchantAccount.setBranchCode(walletAccount.getBranch().getBranchId().toString());

		}

		List<ClearingHousePoolMember> chPoolList = baseServiceImpl.eotMobileDao.getClearingHouse(new Integer(customerAccount.getBankCode()), new Integer(merchantAccount.getBankCode()));

		if (chPoolList == null) {
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}

		com.eot.dtos.banking.SaleDTO saleDTO = new com.eot.dtos.banking.SaleDTO();

		saleDTO.setTransactionType(dto.getTransactionType().toString());
		saleDTO.setAmount(amount);
		saleDTO.setCustomerAccount(customerAccount);
		saleDTO.setOtherAccount(merchantAccount);
		saleDTO.setCustomerID(encashCustomer.getCustomerId().toString());
		saleDTO.setRequestID(baseServiceImpl.requestID.toString());
		saleDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		saleDTO.setReferenceType(baseServiceImpl.referenceType);
		saleDTO.setTransDesc("Encash");
		saleDTO.setChannelType(Constants.EOT_CHANNEL);

		try {
			// spring web service call to core: commented by bidyut
			// saleDTO = bankingServiceClientStub.sale(saleDTO);

			// rest call to core : modified by bidyut
			saleDTO = processRequest(CoreUrls.SALE_URL, saleDTO, com.eot.dtos.banking.SaleDTO.class);
			if (saleDTO.getErrorCode() != 0) {
				throw new EOTException(saleDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(saleDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

		} /*
			 * catch (EOTCoreException e) { e.printStackTrace(); throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); }
			 */
		/*
		 * catch (Exception e) { throw new EOTException(ErrorConstants.SERVICE_ERROR); }
		 */

		finally {

		}

		String message = baseServiceImpl.messageSource.getMessage("ENCASH_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), encashCustomer.getCustomerId().toString(), encashCustomer.getFirstName(), encashCustomer.getMobileNumber(), fromAlias, null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()):"0.00", saleDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

		dto.setSuccessResponse(message);
		dto.setCustomerName(encashCustomer.getFirstName());
		dto.setMerchantCode(baseServiceImpl.customer.getCustomerId().toString());
		dto.setMerchantLocation(baseServiceImpl.customer.getCity().getCity());
		dto.setMerchantName(baseServiceImpl.customer.getFirstName());
		dto.setTxnAuthCode(saleDTO.getTransactionNO());

		return dto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#processSendMoney(com.eot.banking.
	 * dto.RemittanceDTO)
	 */
	@Override
	public RemittanceDTO processSendMoney(RemittanceDTO remittanceDTO) throws EOTException {
		baseServiceImpl.handleRequest(remittanceDTO);
		String fromAlias = remittanceDTO.getAccountAlias();
		Integer fromAliasType = remittanceDTO.getAliasType();
		String senderNo = remittanceDTO.getSenderMobileNumber();
		String receiverNo = remittanceDTO.getReceiverMobileNumber();
		Double amount = remittanceDTO.getAmount();
		String txnPin = remittanceDTO.getTransactionPIN();

		if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		accountDto.setAccountAlias(fromAlias);
		accountDto.setAccountType(fromAliasType.toString());

		if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		}

		CustomerAccount otherAccount = baseServiceImpl.eotMobileDao.getPayeeAccountFromMobileNo(receiverNo);

		if (otherAccount == null) {
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}

		if (otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}

		if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}

		List<ClearingHousePoolMember> chPoolList = baseServiceImpl.eotMobileDao.getClearingHouse(Integer.parseInt(accountDto.getBankCode()), otherAccount.getBank().getBankId());

		if (chPoolList == null) {
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}

		com.eot.dtos.common.Account otherAccountDto = new com.eot.dtos.common.Account();
		otherAccountDto.setAccountAlias(receiverNo);
		otherAccountDto.setAccountNO(otherAccount.getAccountNumber());
		otherAccountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAccountDto.setBankCode(otherAccount.getBank().getBankId().toString());
		otherAccountDto.setBranchCode(otherAccount.getBranch().getBranchId().toString());

		TransferDirectDTO transferDirectDTO = new TransferDirectDTO();

		transferDirectDTO.setCustomerAccount(accountDto);
		transferDirectDTO.setOtherAccount(otherAccountDto);
		transferDirectDTO.setAmount(amount);
		transferDirectDTO.setChannelType(Constants.EOT_CHANNEL);
		transferDirectDTO.setRequestID(baseServiceImpl.requestID.toString());
		transferDirectDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		transferDirectDTO.setReferenceType(baseServiceImpl.referenceType);
		transferDirectDTO.setTransactionType(remittanceDTO.getTransactionType().toString());

		try {
			// spring web service call to core : commented by bidyut
			// transferDirectDTO =
			// bankingServiceClientStub.transferDirect(transferDirectDTO);

			// rest call to core : modified by bidyut
			transferDirectDTO = processRequest(CoreUrls.TRANSFER_DIRECT_TXN_URL, transferDirectDTO, com.eot.dtos.banking.TransferDirectDTO.class);
			if (transferDirectDTO.getErrorCode() != 0) {
				throw new EOTException(transferDirectDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(transferDirectDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

			remittanceDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_DIR_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), fromAlias, receiverNo, null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()):"0.00", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

			return remittanceDTO;

		} /*
			 * catch (EOTCoreException e) { e.printStackTrace();
			 * System.out.println(e.getMessageKey()); throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); }
			 */
		finally {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eot.banking.service.TransactionService#processReceiveMoney(com.eot.
	 * banking.dto.RemittanceDTO)
	 */
	@Override
	public RemittanceDTO processReceiveMoney(RemittanceDTO remittanceDTO) throws EOTException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#showSMSCashDetails(com.eot.banking
	 * .dto.SMSCashDTO)
	 */
	@Override
	public SMSCashDTO showSMSCashDetails(SMSCashDTO smsCashDTO) throws EOTException {
		smsCashDTO.setPayeeMobileNumber(smsCashDTO.getMobileNumber());
		List<ExternalTransaction> externalTxnList = baseServiceImpl.eotMobileDao.getExternalTxnsFromBeneficiaryMobileNumber(smsCashDTO.getPayeeMobileNumber());

		if (CollectionUtils.isEmpty(externalTxnList)) {
			throw new EOTException(ErrorConstants.TXN_DETAILS_NOT_AVAILABLE);
		}
		List<SMSCashDTO> listOfSMSCashDetails = new ArrayList<SMSCashDTO>();
		for (ExternalTransaction externalTransaction : externalTxnList) {
			SMSCashDTO dto = new SMSCashDTO();
			dto.setSmsCashId(externalTransaction.getExternalTxnId());
			dto.setCustomerName(externalTransaction.getCustomerName());
			dto.setPayeeMobileNumber((externalTransaction.getBeneficiaryMobileNumber()));
			dto.setBeneficiaryAmount(externalTransaction.getAmount());
			
			ServiceChargeDTO serviceChargeDTO = new ServiceChargeDTO();
			serviceChargeDTO.setTransactionType(externalTransaction.getTransactionType());
			serviceChargeDTO.setTxnTypeId(externalTransaction.getTransactionType());
			serviceChargeDTO.setTxnBankingType(EOTConstants.TXN_TYPE_INTRA);
			serviceChargeDTO.setTxnAmount(externalTransaction.getAmount());
			serviceChargeDTO.setApplicationType(EOTConstants.DEFAULT_APPLICATION_TYPE.toString());
			serviceChargeDTO = otherBankingService.getServiceCharge(serviceChargeDTO);			
			
			dto.setServiceCharge(serviceChargeDTO.getServiceChargeAmt());
			Date date = externalTransaction.getTransactionDate();
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy hh:mm:ss");
			dto.setSmsCashDate(format.format(date));

			listOfSMSCashDetails.add(dto);
		}
		smsCashDTO.setListOfSMSCashDetails(listOfSMSCashDetails);
		return smsCashDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eot.banking.service.TransactionService#receiveSMSCash(com.eot.banking.dto
	 * .SMSCashDTO)
	 */
	@Override
	public SMSCashDTO receiveSMSCash(SMSCashDTO smsCashDTO) throws EOTException {
		baseServiceImpl.handleRequest(smsCashDTO);

		ExternalTransaction externalTransaction = baseServiceImpl.eotMobileDao.getExternalTxnsFromId(smsCashDTO.getSmsCashId());

		if (externalTransaction == null) {
			throw new EOTException(ErrorConstants.TXN_DETAILS_NOT_AVAILABLE);
		}

		if (!smsCashDTO.getSmsCashPIN().equalsIgnoreCase(externalTransaction.getTxnPin())) {
			throw new EOTException(ErrorConstants.INVALID_SMS_CASH_PIN);
		}

		String fromAlias = smsCashDTO.getAccountAlias();
		Integer fromAliasType = smsCashDTO.getAliasType();
		// String toMobileNo = smsCashDTO.getPayeeMobileNumber();
		String toMobileNo = externalTransaction.getBeneficiaryMobileNumber();
		Double dobuleAmount = externalTransaction.getAmount();
	//	Long amount = dobuleAmount.longValue();

		Customer agent = baseServiceImpl.eotMobileDao.getCustomer(smsCashDTO.getApplicationId());
		if (!agent.getTransactionPin().equals(smsCashDTO.getTransactionPIN()))
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);

		smsCashDTO.setBeneficiaryAmount(externalTransaction.getAmount());
		smsCashDTO.setStatus(0);
		if (externalTransaction.getCustomerName() != null)
			smsCashDTO.setCustomerName(externalTransaction.getCustomerName());
		smsCashDTO.setPayeeMobileNumber(externalTransaction.getBeneficiaryMobileNumber());

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(fromAlias);
		accountDto.setAccountType(fromAliasType.toString());
		CustomerAccount agentAccount = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(agent.getCustomerId(), fromAlias);
		if (agentAccount == null) {
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		accountDto.setAccountNO(agentAccount.getAccountNumber());
		accountDto.setBankCode(agentAccount.getBank().getBankId().toString());
		accountDto.setBranchCode(agentAccount.getBranch().getBranchId().toString());
		externalTransaction.setExternalEntityId(agentAccount.getBank().getBankId());
		externalTransaction.setEntityName(agentAccount.getBank().getBankName());

		com.eot.dtos.banking.SMSCashDTO smsCashRCVDTO = new com.eot.dtos.banking.SMSCashDTO();

		smsCashRCVDTO.setCustomerAccount(accountDto);
		smsCashRCVDTO.setAmount(dobuleAmount);
		smsCashRCVDTO.setChannelType(Constants.EOT_CHANNEL);
		// smsCashDTO.setRequestID(baseServiceImpl.requestID.toString());
		smsCashRCVDTO.setReferenceID(agent.getCustomerId().toString());
		smsCashRCVDTO.setReferenceType(agent.getType());
		smsCashRCVDTO.setTransactionType(smsCashDTO.getTransactionType().toString());
		smsCashRCVDTO.setPayeeMobileNumber(toMobileNo);
		if(smsCashDTO.getRemarks()== null || smsCashDTO.getRemarks().equals("")){
			smsCashRCVDTO.setRemarks("SMS Cash Withdraw");
		}else{
			smsCashRCVDTO.setRemarks(smsCashDTO.getRemarks());
		}

		try {
			// spring service call to core :commented by bidyut
			// smsCashDTO = bankingServiceClientStub.smsCash(smsCashDTO);

			// rest call to core : modified by bidyut
			smsCashRCVDTO = processRequest(CoreUrls.SMS_CASH_URL, smsCashRCVDTO, com.eot.dtos.banking.SMSCashDTO.class);
			if (smsCashRCVDTO.getErrorCode() != 0) {
				externalTransaction.setStatus(Constants.TXN_STATUS_RECONSILE);
				//baseServiceImpl.eotMobileDao.update(externalTransaction);
				throw new EOTException(smsCashRCVDTO.getErrorCode());

			}else if (smsCashRCVDTO.getErrorCode() == 0) {
				externalTransaction.setStatus(Constants.TXN_STATUS_SUCESSFUL);
				externalTransaction.setReferencedId(Long.parseLong(smsCashRCVDTO.getTransactionNO()));
				baseServiceImpl.eotMobileDao.update(externalTransaction);
			}

			
			smsCashDTO.setAccountBalance(smsCashRCVDTO.getCustomerAccount().getAccountBalance());
			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(smsCashRCVDTO.getTransactionNO()));
			/*
			 * if (null != smsCashDTO.getApplicationId()) {
			 * baseServiceImpl.mobileRequest.setTransaction(txn); }
			 */

		} /*
			 * catch (EOTCoreException e) { e.printStackTrace();
			 * System.out.println(e.getMessageKey()); throw new
			 * EOTException(Integer.parseInt(e.getMessageKey())); }
			 *//*
				 * catch (Exception e) { e.printStackTrace(); throw new
				 * EOTException(ErrorConstants.SERVICE_ERROR); }
				 */
		finally {

		}

		smsCashDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("SMS_CASH_RECEIV_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != dobuleAmount ? new DecimalFormat("#0.00").format(dobuleAmount.doubleValue()):"0.00", externalTransaction.getBeneficiaryMobileNumber(),null !=smsCashRCVDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(smsCashRCVDTO.getServiceChargeAmt().doubleValue()):"0.00",smsCashRCVDTO.getRemarks() ,smsCashRCVDTO.getTransactionNO() }, new Locale(agent.getDefaultLanguage())));

		return smsCashDTO;
	}

	/**
	 * @author bidyut
	 * @param url
	 * @param obj
	 * @param type
	 * @return
	 */
	public <T extends com.eot.dtos.common.Header> T processRequest(String url, T obj, Class<T> type) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		obj.setRequestChannel(EOTConstants.REQUEST_CHANNEL_MOBILE);
		obj = restTemplate.postForObject(url, obj, type);
		return obj;
	}

	@Override
	public PendingTransactionDTO getPendingTransactions(TransactionBaseDTO transactionBaseDTO) throws EOTException {

		Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		if (merchantDetails == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		// CustomerAccount account =
		// baseServiceImpl.eotMobileDao.getCustomerAccountFromAlias(customer.getCustomerId(),transactionBaseDTO.getAccountAlias());
		CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountWithCustomerId(merchantDetails.getCustomerId());
		if (account == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
		}
		PendingTransactionDTO pendingTransactionDTO = new PendingTransactionDTO();
		ArrayList<PendingTransactionDTO.TransactionDTO> pendingTransactionsLists = baseServiceImpl.eotMobileDao.getPendingTransactions(transactionBaseDTO, account.getAccountNumber());
		if (pendingTransactionsLists.size() < 0) {
			pendingTransactionDTO.setMessageDescription("No pending Transactions");
		} else {
			pendingTransactionDTO.setListOfPendingTxn(pendingTransactionsLists);
			pendingTransactionDTO.setMessageDescription("List of Pending Transactions");
		}

		// pendingTransactionDTO.setListOfPendingTxn(pendingTransactionsLists);
		/*
		 * for(PendingTransactionDTO.TransactionDTO tx:pendingTransactionsLists) {
		 * pendingTransactionDTO.setListOfPendingTxn(tx); }
		 */
		pendingTransactionDTO.setRemarks(transactionBaseDTO.getRemarks() != null ? transactionBaseDTO.getRemarks() : "NA");
		pendingTransactionDTO.setUsername(transactionBaseDTO.getUsername());
		pendingTransactionDTO.setStatus(0);
		return pendingTransactionDTO;
	}

	/**
	 * @author Bidyut: withdrawal new flow
	 */

	@Override
	public List<PendingTransactionDTO> loadPenndingTransaction(PendingTransactionDTO transaction) throws EOTException {

		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(transaction.getApplicationId());

		if (customer == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);

		CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(customer.getCustomerId());

		if (customerAccount == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);

		transaction.setCustomer(customer);
		transaction.setAccountNumber(customerAccount.getAccountNumber());

		List<PendingTransaction> list = baseServiceImpl.eotMobileDao.loadPendingTransactions(transaction);

		if (list.size() == 0)
			throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);

		List<PendingTransactionDTO> transactionBaseDTOs = new ArrayList<PendingTransactionDTO>();
		PendingTransactionDTO transactionDTO = null;
		PendingTransaction pt = null;
		Customer merchent = null;
		for (int i = 0; i < list.size(); i++) {
			pt = list.get(i);
			transactionDTO = new PendingTransactionDTO();
			transactionDTO.setAmount(pt.getAmount());
			transactionDTO.setTransactionTime(new Long(pt.getTransactionDate().getTime()));
			transactionDTO.setPendingTxnRecordId(pt.getTransactionId());
			transactionDTO.setTransactionType(pt.getTransactionType().getTransactionType());
			transactionDTO.setServiceCharge(0.0);
			merchent = baseServiceImpl.eotMobileDao.getCustomerFromCustomerId(Long.parseLong(pt.getInitiatedBy()));
			if (merchent != null) {
				Integer type = merchent.getType();
				transactionDTO.setAgentType(type == 0 ? "Customer" : type == 1 ? "Agent" : type == 2 ? "Merchant" : "Agent Sole Merchant");
				transactionDTO.setAgentName(merchent.getFirstName()  + " " + merchent.getLastName());
				transactionDTO.setPhone(merchent.getMobileNumber());
				transactionDTO.setAgentCode(merchent.getAgentCode());
			}
			transactionBaseDTOs.add(transactionDTO);

		}

		return transactionBaseDTOs;
	}

	@Override
	public PendingTransactionDTO approvePendingTransaction(PendingTransactionDTO transactionBaseDTO) throws EOTException {

		baseServiceImpl.handleRequest(transactionBaseDTO);
		if (!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		if (customer == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);

		CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(customer.getCustomerId());

		if (customerAccount == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);

		transactionBaseDTO.setAccountNumber(customerAccount.getAccountNumber());

		PendingTransaction pendingTransaction = baseServiceImpl.eotMobileDao.loadPendingTransaction(transactionBaseDTO);

		if (pendingTransaction == null)
			throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);

		if (pendingTransaction.getTransactionType().getTransactionType() == Constants.TXN_ID_MERCHANT_WITHDRAWAL) {
			CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountFromAccountNumber(pendingTransaction.getCustomerAccount());
			if (account == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
			}
			// get marchant details
			Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomerFromCustomerId(Long.parseLong(pendingTransaction.getInitiatedBy()));
			// load Merchant account
			CustomerAccount merchantAccount = baseServiceImpl.eotMobileDao.getCustomerAccountFromAccountNumber(pendingTransaction.getOtherAccount());

			// customer account details
			com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
			accountDto.setAccountAlias(transactionBaseDTO.getAccountAlias());
			accountDto.setAccountNO(pendingTransaction.getCustomerAccount());
			accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

			// merchant account details
			com.eot.dtos.common.Account merchantAccountDTO = new com.eot.dtos.common.Account();
			merchantAccountDTO.setAccountNO(merchantAccount.getAccountNumber());
			merchantAccountDTO.setAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC + "");
			merchantAccountDTO.setBankCode(merchantAccount.getBank().getBankId().toString());
			merchantAccountDTO.setBranchCode(merchantAccount.getBranch().getBranchId().toString());

			WithdrawalDTO withdrawalDTO = new WithdrawalDTO();

			withdrawalDTO.setReferenceID(pendingTransaction.getInitiatedBy());
			// withdrawalDTO.setAmount(transactionBaseDTO.getAmount().doubleValue());
			withdrawalDTO.setAmount(transactionBaseDTO.getAmount());
			withdrawalDTO.setChannelType(Constants.EOT_CHANNEL);
			withdrawalDTO.setCustomerAccount(accountDto);
			withdrawalDTO.setOtherAccount(merchantAccountDTO);
			withdrawalDTO.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL + "");
			withdrawalDTO.setCommissionToBePaidId(pendingTransaction.getInitiatedBy());

			try {
				// rest call to core : modified by bidyut
				withdrawalDTO = processRequest(CoreUrls.WITHDRAWAL_TXN_URL, withdrawalDTO, com.eot.dtos.banking.WithdrawalDTO.class);
				if (withdrawalDTO.getErrorCode() != 0) {
					throw new EOTException(withdrawalDTO.getErrorCode());
				}

				Transaction txn = new Transaction();
				txn.setTransactionId(new Long(withdrawalDTO.getTransactionNO()));
				baseServiceImpl.mobileRequest.setTransaction(txn);
			} finally {

			}

			// =================Updating Pending transaction for success==================
			pendingTransaction.setStatus(Constants.TRANSACTION_STATUS_SUCCESS);
			pendingTransaction.setApprovedBy(customer.getCustomerId().toString());
			pendingTransaction.setTransactionNo(withdrawalDTO.getTransactionNO());
			baseServiceImpl.eotMobileDao.update(pendingTransaction);

			// String message =
			// baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new String[] {
			// DateUtil.formattedDateAndTime(new Date()), customer.getMobileNumber(),
			// transactionBaseDTO.getAccountAlias(), customer.getFirstName(),
			// customer.getCity().getCity(), transactionBaseDTO.getAmount().toString(),
			// withdrawalDTO.getTransactionNO(), merchantDetails.getFirstName() + " " +
			// merchantDetails.getLastName() }, new
			// Locale(baseServiceImpl.customer.getDefaultLanguage()));

			String customerType = "";
			if (merchantDetails.getType() == Constants.REF_TYPE_AGENT) {
				customerType = "Agent";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_MERCHANT) {
				customerType = "SM";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_AGENT_SOLE_MERCHANT) {
				customerType = "ASM";
			}

			String message = baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()),null != transactionBaseDTO.getAmount() ?  new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00",null !=transactionBaseDTO.getServiceCharge() ? new DecimalFormat("#0.00").format(transactionBaseDTO.getServiceCharge().doubleValue()):"0.00", merchantDetails.getMobileNumber(), merchantDetails.getFirstName() /*+ " " + merchantDetails.getLastName()*/, withdrawalDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

			transactionBaseDTO.setTransactionType(transactionBaseDTO.getTransactionType());
			transactionBaseDTO.setSuccessResponse(message);
			return transactionBaseDTO;

		} else if (pendingTransaction.getTransactionType().getTransactionType() == Constants.TXN_ID_MERCHANT_SALE) {
			CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountFromAccountNumber(pendingTransaction.getCustomerAccount());
			if (account == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
			}

			// get marchant details
			Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomerFromCustomerId(Long.parseLong(pendingTransaction.getInitiatedBy()));

			// load Merchant account
			CustomerAccount merchantAccount = baseServiceImpl.eotMobileDao.getCustomerAccountFromAccountNumber(pendingTransaction.getOtherAccount());

			// customer account details
			com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
			accountDto.setAccountAlias(transactionBaseDTO.getAccountAlias());
			accountDto.setAccountNO(pendingTransaction.getCustomerAccount());
			accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

			// merchant account details
			// bankAccountDto.setAccountAlias(bankAccount.getAlias());
			com.eot.dtos.common.Account merchantAccountDTO = new com.eot.dtos.common.Account();
			merchantAccountDTO.setAccountNO(merchantAccount.getAccountNumber());
			merchantAccountDTO.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
			merchantAccountDTO.setBankCode(merchantAccount.getBank().getBankId().toString());
			merchantAccountDTO.setBranchCode(merchantAccount.getBranch().getBranchId().toString());

			com.eot.dtos.banking.SaleDTO saleDTO = new com.eot.dtos.banking.SaleDTO();

			saleDTO.setTransactionType(Constants.TXN_ID_MERCHANT_SALE + "");
			saleDTO.setAmount(transactionBaseDTO.getAmount());
			saleDTO.setCustomerAccount(accountDto);
			saleDTO.setOtherAccount(merchantAccountDTO);
			saleDTO.setCustomerID(pendingTransaction.getCustomer().getCustomerId() + "");
			saleDTO.setRequestID(baseServiceImpl.requestID.toString());
			saleDTO.setReferenceID(pendingTransaction.getInitiatedBy());
			saleDTO.setReferenceType(baseServiceImpl.referenceType);
			saleDTO.setTransDesc("Sale");
			saleDTO.setChannelType(Constants.EOT_CHANNEL);

			try {
				saleDTO = processRequest(CoreUrls.SALE_URL, saleDTO, com.eot.dtos.banking.SaleDTO.class);
				if (saleDTO.getErrorCode() != 0) {
					throw new EOTException(saleDTO.getErrorCode());
				}

				Transaction txn = new Transaction();
				txn.setTransactionId(new Long(saleDTO.getTransactionNO()));
				baseServiceImpl.mobileRequest.setTransaction(txn);
			} /*
				 * catch (EOTException e) { e.printStackTrace(); //=================Updating
				 * Pending transaction for fail==================
				 * //pendingTransaction.setStatus(Constants.TRANSACTION_WITHDRAWAL_FAIL);
				 * //pendingTransaction.setApprovedBy(customer.getCustomerId().toString());
				 * pendingTransaction.setComment(e.getMessage());
				 * baseServiceImpl.eotMobileDao.update(pendingTransaction);
				 * 
				 * throw new EOTException(Integer.parseInt(e.getMessageKey())); }catch
				 * (Exception e) {
				 * pendingTransaction.setStatus(Constants.TRANSACTION_WITHDRAWAL_FAIL);
				 * pendingTransaction.setApprovedBy(customer.getCustomerId().toString());
				 * baseServiceImpl.eotMobileDao.update(pendingTransaction); throw new
				 * EOTException(ErrorConstants.SERVICE_ERROR); }
				 */
			finally {

			}

			// =================Updating Pending transaction for success==================
			pendingTransaction.setStatus(Constants.TRANSACTION_STATUS_SUCCESS);
			pendingTransaction.setApprovedBy(customer.getCustomerId().toString());
			pendingTransaction.setTransactionNo(saleDTO.getTransactionNO());
			baseServiceImpl.eotMobileDao.update(pendingTransaction);

			String customerType = "";
			if (merchantDetails.getType() == Constants.REF_TYPE_AGENT) {
				customerType = "Agent";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_MERCHANT) {
				customerType = "SM";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_AGENT_SOLE_MERCHANT) {
				customerType = "ASM";
			}

			String message = baseServiceImpl.messageSource.getMessage("SALE_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != transactionBaseDTO.getAmount() ? new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00" + "", null !=transactionBaseDTO.getServiceCharge() ? new DecimalFormat("#0.00").format( transactionBaseDTO.getServiceCharge().doubleValue()):"0.00", merchantDetails.getMobileNumber(), merchantDetails.getFirstName() /*+ " " + merchantDetails.getLastName()*/, saleDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));
			transactionBaseDTO.setSuccessResponse(message);
			return transactionBaseDTO;
		}
		return transactionBaseDTO;
	}

	@Override
	public PendingTransactionDTO rejectPendingTransaction(PendingTransactionDTO transactionBaseDTO) throws EOTException {
		baseServiceImpl.handleRequest(transactionBaseDTO);

		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		if (customer == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);

		CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(customer.getCustomerId());

		if (customerAccount == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);

		transactionBaseDTO.setAccountNumber(customerAccount.getAccountNumber());

		PendingTransaction pendingTransaction = baseServiceImpl.eotMobileDao.loadPendingTransaction(transactionBaseDTO);

		if (pendingTransaction == null)
			throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);

		// =================Updating Pending transaction for success==================
		pendingTransaction.setStatus(Constants.TRANSACTION_STATUS_REJECTED);
		pendingTransaction.setApprovedBy(customer.getCustomerId().toString());
		baseServiceImpl.eotMobileDao.update(pendingTransaction);

		// String message =
		// baseServiceImpl.messageSource.getMessage("WITHDRAWAL_REJECTED", new String[]
		// { transactionBaseDTO.getPendingTxnRecordId() + "" }, new
		// Locale(baseServiceImpl.customer.getDefaultLanguage()));
		String message = baseServiceImpl.messageSource.getMessage("WITHDRAWAL_REJECTED", new String[] { "" }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		transactionBaseDTO.setSuccessResponse(message);
		return transactionBaseDTO;
	}

	@Override
	public FundTransferDTO validatePayRequest(FundTransferDTO fundTransferDTO) throws EOTException {

		baseServiceImpl.handleRequest(fundTransferDTO);
	//	String fromAlias = fundTransferDTO.getAccountAlias();
		String agentCode = fundTransferDTO.getAgentCode();
		Double amount = fundTransferDTO.getAmount();

		Customer merchant = baseServiceImpl.eotMobileDao.getAgentByAgentCode(agentCode);
		if (merchant == null)
			throw new EOTException(ErrorConstants.MERCHANT_CODE_NOT_FOUND);
		
		
		  if(merchant.getType()!=Constants.REF_TYPE_MERCHANT) throw new
		  EOTException(ErrorConstants.INVALID_MERCHANT_CODE);
		 

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(merchant.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		/*if ((baseServiceImpl.customer.getType() != Constants.REF_TYPE_CUSTOMER)) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}*/

		CustomerAccount otherAccount = baseServiceImpl.eotMobileDao.getPayeeAccountFromAgentCode(fundTransferDTO.getAgentCode());

		if (otherAccount == null) {
			throw new EOTException(ErrorConstants.AGENT_CODE_NOT_FOUND);
		}

		/*if (otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}*/

		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(otherAccount.getCustomer().getAppId());
		
		if( otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){  // validate status of merchant/customer
			throw new EOTException(ErrorConstants.AGENT_ACC_DEACTIVATED);
		}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
			throw new EOTException(ErrorConstants.AGENT_ACC_BLOCKED);
		}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_PENDING && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
			throw new EOTException(ErrorConstants.AGENT_KYC_PENDING);
		}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
			throw new EOTException(ErrorConstants.AGENT_KYC_REJECTED);
		}
		
		if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER) {
			throw new EOTException(ErrorConstants.CUSTOMER_ACC_DEACTIVATED);
		}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){
			throw new EOTException(ErrorConstants.CUSTOMER_ACC_BLOCKED);
		}else if( otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
			throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
		}
		
		if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
			throw new EOTException(ErrorConstants.MERCHANT_ACC_DEACTIVATED);
		}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
			throw new EOTException(ErrorConstants.MERCHANT_ACC_BLOCKED);
		}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_PENDING && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
			throw new EOTException(ErrorConstants.MERCHANT_KYC_PENDING);
		}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
			throw new EOTException(ErrorConstants.MERCHANT_KYC_REJECTED);
		}	
		
		if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_SUSPENDED) {
			throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
		}
		
		if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}

		ServiceChargeDTO serviceChargeDTO = new ServiceChargeDTO();

		serviceChargeDTO.setApplicationType(fundTransferDTO.getApplicationType());
		serviceChargeDTO.setTxnAmount(fundTransferDTO.getAmount() != null ? fundTransferDTO.getAmount() : null);
		if (fundTransferDTO.getTxnBankingType() != null)
			serviceChargeDTO.setTxnBankingType(fundTransferDTO.getTxnBankingType());
		else
			serviceChargeDTO.setTxnBankingType(EOTConstants.TXN_TYPE_INTRA);

		serviceChargeDTO.setTransactionType(fundTransferDTO.getTransactionType());
		if(fundTransferDTO.getTransactionType().equals(Constants.VALIDATE_MERCHANT_PAY)){
			serviceChargeDTO.setTxnTypeId(Constants.EXEC_PAY_MERCHANT);}
		else{serviceChargeDTO.setTxnTypeId(Constants.TXN_TYPE_PAY);}
		
		if (fundTransferDTO.getApplicationType() == null)
			serviceChargeDTO.setApplicationType(1 + "");

		serviceChargeDTO = otherBankingService.getServiceCharge(serviceChargeDTO);
		String payeeName = otherAccount.getCustomer().getFirstName().concat(" ").concat(otherAccount.getCustomer().getLastName());
		String businessName = otherAccount.getCustomer().getBusinessName();
		fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_DIR_CONFIRM", new String[] { agentCode, StringUtils.isNotEmpty(businessName) ?businessName:payeeName, null != amount ? new DecimalFormat("#0.00").format( amount.doubleValue()):"0.00", serviceChargeDTO.getServiceCharge() != null ? new DecimalFormat("#0.00").format( serviceChargeDTO.getServiceCharge().doubleValue()):"0.00" , fundTransferDTO.getRemarks()}, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		fundTransferDTO.setServiceChargeAmt(serviceChargeDTO.getServiceCharge());
		fundTransferDTO.setServiceCharge(serviceChargeDTO.getServiceCharge());
		fundTransferDTO.setCustomerName(getName(merchant.getFirstName(), merchant.getLastName()));
		fundTransferDTO.setAgentCode(merchant.getAgentCode());
		fundTransferDTO.setAgentName( StringUtils.isNotEmpty(merchant.getBusinessName())  ? merchant.getBusinessName():getName(merchant.getFirstName(),merchant.getLastName()));
		if(fundTransferDTO.getRemarks()=="" ||fundTransferDTO.getRemarks().equals("")){
			fundTransferDTO.setRemarks("m-GURUSH Pay");
//			fundTransferDTO.setRemarks("");
		}
		return fundTransferDTO;

	}

	private String getName(String firstName, String lastName) {
		String name = firstName;
		if(StringUtils.isNotEmpty(lastName)) {
			name = name + " " + lastName;
		}
		return name;
	}

	@Override
	public List<ReversalTransactionDTO> loadTransactionReversalForApprove(ReversalTransactionDTO reversalTransactionDTO)
			throws EOTException {
		baseServiceImpl.handleRequest(reversalTransactionDTO);
		List<ReversalTransactionDTO> reversalTransactions = baseServiceImpl.eotMobileDao.loadTransactionReversalForApprove(reversalTransactionDTO);
		return reversalTransactions;
	}

	@Override
	public ReversalTransactionDTO processApprovedReversalTxn(ReversalTransactionDTO reversalTransactionDTO) throws EOTException {
		baseServiceImpl.handleRequest(reversalTransactionDTO);
		com.eot.entity.Transaction transaction = baseServiceImpl.eotMobileDao.getTransactionByTxnId(Integer.parseInt(reversalTransactionDTO.getTransactionId()));
		AdjustmentTransactionDTO adjustmentTransactionDTO = new AdjustmentTransactionDTO();

		Account agentAccount = baseServiceImpl.eotMobileDao.getAccount(transaction.getCustomerAccount());
		Account customerAccount = baseServiceImpl.eotMobileDao.getAccount(transaction.getOtherAccount());
		
		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(customerAccount.getAlias());
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(customerAccount.getCustomerAccount().getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getCustomerAccount().getBranch().getBranchId().toString());

		com.eot.dtos.common.Account bankAccountDto = new  com.eot.dtos.common.Account();
		bankAccountDto.setAccountType(Constants.ALIAS_TYPE_BANK_ACC+"");

		bankAccountDto.setAccountAlias(agentAccount.getAlias());
		bankAccountDto.setAccountNO(agentAccount.getAccountNumber());
		bankAccountDto.setAccountType(Constants.ALIAS_TYPE_BANK_ACC+"");
		bankAccountDto.setBankCode(agentAccount.getCustomerAccount().getBank().getBankId().toString());
		bankAccountDto.setBranchCode(agentAccount.getCustomerAccount().getBranch().getBranchId().toString());

		adjustmentTransactionDTO.setTransactionTypeRef(transaction.getTransactionType().getTransactionType().toString());
		adjustmentTransactionDTO.setTransactionId(transaction.getTransactionId().toString());
		adjustmentTransactionDTO.setAmount(reversalTransactionDTO.getAmount());
		adjustmentTransactionDTO.setFee("0");
		adjustmentTransactionDTO.setDescription("");
		adjustmentTransactionDTO.setReferenceID(customerAccount.getReferenceId());
		adjustmentTransactionDTO.setChannelType(Constants.EOT_CHANNEL);
		adjustmentTransactionDTO.setTransactionType("61");
		adjustmentTransactionDTO.setCustomerAccount(accountDto);
		adjustmentTransactionDTO.setOtherAccount(bankAccountDto);
		adjustmentTransactionDTO.setClearingPoolId(((TransactionJournal)transaction.getTransactionJournals().iterator().next()).getClearingHousePool().getClearingPoolId());
		
		adjustmentTransactionDTO=processRequest(CoreUrls.ADJUSTMENT_TXN_URL, adjustmentTransactionDTO, com.eot.dtos.banking.AdjustmentTransactionDTO.class);
		if(adjustmentTransactionDTO.getErrorCode()!=0)
		{
			throw new EOTException("ERROR_"+adjustmentTransactionDTO.getErrorCode());
		}
		
		reversalTransactionDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TXN_REVERSAL_CONFIRM", new String[] { reversalTransactionDTO.getMobileNumber(), reversalTransactionDTO.getAgentName(), null != reversalTransactionDTO.getAmount() ? new DecimalFormat("#0.00").format(reversalTransactionDTO.getAmount().doubleValue()):"0.00",reversalTransactionDTO.getTransactionId()}, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		return reversalTransactionDTO;
	}
	
	
	
	@Override
	public TransactionBaseDTO processMerchantPayout(TransactionBaseDTO transactionBaseDTO) throws EOTException {

		baseServiceImpl.handleRequest(transactionBaseDTO);
		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(transactionBaseDTO.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(transactionBaseDTO.getMobileNumber());
		if (customer == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		// Validation for customer type
		if (customer.getType() != Constants.REF_TYPE_AGENT) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		if (otpForSaleEnabled) {
			OtpDTO otpDto = new OtpDTO();
			otpDto.setOtphash(transactionBaseDTO.getCustomerOTP());
			otpDto.setReferenceId(customer.getCustomerId() + "");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			//otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_WITHDRAWA);
			otpDto.setAmount(transactionBaseDTO.getAmount());
			Otp otp = baseServiceImpl.eotMobileDao.verifyOTPWithAmount(otpDto);
			//Otp otp = baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
			System.out.println("baseServiceImpl.customerPin - " + transactionBaseDTO.getCustomerOTP());
			System.out.println("db - " + otpDto.getOtphash());
			if (otp == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}
			
			otp.setStatus(OtpStatusEnum.USED.getCode());
			baseServiceImpl.eotMobileDao.update(otp);
			
		} else {
			//
			// if
			// (!transactionBaseDTO.getCustomerOTP().equals(customer.getTransactionPin().toString()))
			// {
			// throw new EOTException(ErrorConstants.INVALID_CUSTOMER_TXN_PIN);
			// }
			if (!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
			}

		}
		CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountFromAlias(customer.getCustomerId(), transactionBaseDTO.getAccountAlias());
		if (account == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
		}

		Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(Constants.DEFAULT_BANK);
		Account bankAccount = bank.getAccount();
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(Constants.DEFAULT_BRANCH);

		Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		// load Merchant account
		CustomerAccount merchantAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(merchantDetails.getCustomerId());

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(transactionBaseDTO.getAccountAlias());
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		/*
		 * com.eot.dtos.common.Account bankAccountDto = new
		 * com.eot.dtos.common.Account();
		 * 
		 * bankAccountDto.setAccountAlias(bankAccount.getAlias());
		 * bankAccountDto.setAccountNO(merchantAccount.getAccountNumber());
		 * bankAccountDto.setAccountType(Constants.ALIAS_TYPE_OTHER+"");
		 * //bankAccountDto.setBankCode(bank.getBankId().toString());
		 * //bankAccountDto.setBranchCode(branch.getBranchId().toString());
		 * bankAccountDto.setBankCode(merchantAccount.getBank().getBankId().toString());
		 * bankAccountDto.setBranchCode(merchantAccount.getBranch().getBranchId().
		 * toString());
		 */

		com.eot.dtos.common.Account customerAccountDTO = new com.eot.dtos.common.Account();

		// merchant account details
		// bankAccountDto.setAccountAlias(bankAccount.getAlias());
		customerAccountDTO.setAccountNO(merchantAccount.getAccountNumber());
		customerAccountDTO.setAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC + "");
		customerAccountDTO.setBankCode(merchantAccount.getBank().getBankId().toString());
		customerAccountDTO.setBranchCode(merchantAccount.getBranch().getBranchId().toString());

		WithdrawalDTO withdrawalDTO = new WithdrawalDTO();

		withdrawalDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		withdrawalDTO.setAmount(transactionBaseDTO.getAmount());
		withdrawalDTO.setChannelType(Constants.EOT_CHANNEL);
		withdrawalDTO.setCustomerAccount(accountDto);
		withdrawalDTO.setOtherAccount(customerAccountDTO);
		withdrawalDTO.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL + "");
		// withdrawalDTO.setCommissionToBePaidId(merchantDetails.getCustomerId()+"");

		try {
			//===========================================================================================
			// spring web service call to core: commentec by bidyut
			// withdrawalDTO = bankingServiceClientStub.withdrawal(withdrawalDTO);

			// rest call to core : modified by bidyut
			withdrawalDTO = processRequest(CoreUrls.WITHDRAWAL_TXN_URL, withdrawalDTO, com.eot.dtos.banking.WithdrawalDTO.class);
			if (withdrawalDTO.getErrorCode() != 0) {
				throw new EOTException(withdrawalDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(withdrawalDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);
			
			String customerType = "";
			if (merchantDetails.getType() == Constants.REF_TYPE_AGENT) {
				customerType = "Agent";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_MERCHANT) {
				customerType = "SM";
			} else if (merchantDetails.getType() == Constants.REF_TYPE_AGENT_SOLE_MERCHANT) {
				customerType = "ASM";
			}

			String message = baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()),null != transactionBaseDTO.getAmount() ?  new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00", null != transactionBaseDTO.getServiceCharge() ? new DecimalFormat("#0.00").format(transactionBaseDTO.getServiceCharge().doubleValue()):"0.00", merchantDetails.getMobileNumber(), merchantDetails.getFirstName() /*+ " " + merchantDetails.getLastName()*/, withdrawalDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

			transactionBaseDTO.setTransactionType(transactionBaseDTO.getTransactionType());
			transactionBaseDTO.setSuccessResponse(message);
			//===========================================================================================

			// =================Withdrawal Request Save to Pending transaction==================
			/*TransactionType transactionType = new TransactionType();
			transactionType.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL);

			PendingTransaction pendingTransaction = new PendingTransaction();
			pendingTransaction.setAmount(transactionBaseDTO.getAmount());
			pendingTransaction.setCustomer(customer);
			pendingTransaction.setReferenceType(customer.getType());
			pendingTransaction.setCustomerAccount(account.getAccountNumber());
			pendingTransaction.setBank(account.getBank());
			pendingTransaction.setCustomerAccountType(Constants.ALIAS_TYPE_MOBILE_ACC);
			pendingTransaction.setInitiatedBy(merchantDetails.getCustomerId() + "");
			pendingTransaction.setOtherAccount(merchantAccount.getAccountNumber());
			pendingTransaction.setOtherAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC);
			pendingTransaction.setStatus(Constants.TRANSACTION_INITIATEDBY_MERCHANT);
			pendingTransaction.setTransactionType(transactionType);
			pendingTransaction.setTransactionDate(new Date());

			baseServiceImpl.eotMobileDao.save(pendingTransaction);
			
			String message = baseServiceImpl.messageSource.getMessage("WITHDRAWAL_REQUEST", new String[] { DateUtil.formattedDateAndTime(new Date()), new DecimalFormat("#.00").format(transactionBaseDTO.getAmount()) , customer.getMobileNumber(), customer.getFirstName(), transactionBaseDTO.getRemarks() != null ? transactionBaseDTO.getRemarks() : "Cash Withdrawal", "Pending"/* ,withdrawalDTO.getTransactionNO()  }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

			transactionBaseDTO.setSuccessResponse(message);
			*/
			// ===================================================================================
			
			

		} 
		/*catch (EOTCoreException e) {
			e.printStackTrace();
			System.out.println(e.getMessageKey());
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}*/
			 
		/*
		 * catch (Exception e) { throw new EOTException(ErrorConstants.SERVICE_ERROR); }
		 */
		finally {

		}
		/*
		 * String message =
		 * baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new
		 * String[]{DateUtil.formattedDateAndTime(new
		 * Date()),transactionBaseDTO.getMobileNumber(),
		 * transactionBaseDTO.getAccountAlias(),baseServiceImpl.customer.getFirstName(),
		 * baseServiceImpl.customer.getCity().getCity(),
		 * transactionBaseDTO.getAmount().toString(),withdrawalDTO.getTransactionNO()},
		 * new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		 */
		/*
		 * @ Author name <vinod joshi>, Date<7/17/2018>, purpose of change <Recipt
		 * Modification > ,
		 */
		/* @ Start */
		/*
		 * String message =
		 * baseServiceImpl.messageSource.getMessage("WITHDRAWAL_SUCCESS", new
		 * String[]{DateUtil.formattedDateAndTime(new
		 * Date()),transactionBaseDTO.getAmount().toString(),
		 * withdrawalDTO.getServiceChargeAmt()+"",transactionBaseDTO.getAccountAlias(),
		 * customer.getFirstName(),transactionBaseDTO.getMobileNumber(),baseServiceImpl.
		 * customer.getFirstName()
		 * ,baseServiceImpl.customer.getMobileNumber(),baseServiceImpl.customer.getCity(
		 * ).getCity(), withdrawalDTO.getTransactionNO()}, new
		 * Locale(baseServiceImpl.customer.getDefaultLanguage()));
		 */
		/* End */
		transactionBaseDTO.setUsername(merchantDetails.getFirstName() + merchantDetails.getLastName());
		return transactionBaseDTO;
	}
	
	@Override
	public CurrentBalance fetchBalance(CurrentBalance currentBalance) throws EOTException {
		
		
		Customer agent = baseServiceImpl.eotMobileDao.getCustomer(currentBalance.getApplicationId());
		
		List<CustomerAccount> accountList=baseServiceImpl.eotMobileDao.getAccountListFromCustomerId(agent.getCustomerId());
		
		if(accountList.isEmpty())
		{
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
		
		for(CustomerAccount  custAcc:accountList)
		{
			//Account acc = baseServiceImpl.eotMobileDao.getAccount(custAcc.getAccountNumber());
			//System.out.println(acc.getAliasType());
			currentBalance.setOrganizationName(custAcc.getBank().getBankName());
			if(custAcc.getAccount().getAliasType()==Constants.ALIAS_TYPE_WALLET_ACCOUNT)
				currentBalance.setCurrentBalance(new DecimalFormat("#0.00").format((custAcc.getAccount().getCurrentBalance())));
			if(custAcc.getAccount().getAliasType()==Constants.ALIAS_TYPE_COMMISSION_ACCOUNT)
				currentBalance.setCommission(new DecimalFormat("#0.00").format((custAcc.getAccount().getCurrentBalance())));
				
		}
		currentBalance = baseServiceImpl.eotMobileDao.getCount(currentBalance,agent.getAgentCode());
		
		return currentBalance;

	}
	
	@Override
	public Customer getSmsCashByNumber(String payeemobile) {
		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(payeemobile);
		return customer;
	}
	
	
	public static String generatePin(){
		Random generator = new Random();
		generator.setSeed(System.currentTimeMillis());

		int num = generator.nextInt(99999) + 99999;
		if (num < 100000 || num > 999999) {
			num = generator.nextInt(99999) + 99999;
			if (num < 100000 || num > 999999) {
			}
		}
		return num+"";
	}
	
	@Override
	public FundTransferDTO ownTransferRequest(FundTransferDTO fundTransferDTO) throws EOTException {

		baseServiceImpl.handleRequest(fundTransferDTO);
		Customer agent = null;
		String toMobileNo = fundTransferDTO.getPayeeMobileNumber();
		String fromAlias = fundTransferDTO.getAccountAlias();
		Integer fromAliasType = fundTransferDTO.getAliasType();

		Double amount = fundTransferDTO.getAmount();
		String txnPin = fundTransferDTO.getTransactionPIN();

		if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(toMobileNo)) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		accountDto.setAccountAlias(fromAlias);
		accountDto.setAccountType(fromAliasType.toString());

		if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

			CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (card == null) {
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

			CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
			if (account == null) {
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		}

		CustomerAccount otherAccount = baseServiceImpl.eotMobileDao.getPayeeAccountFromMobileNo(toMobileNo);
		// validation send money customer to customer
		if (fundTransferDTO.getTransactionType() == Constants.TRANSFER_DIRECT_REQ) {
			if (baseServiceImpl.customer.getType() !=  Constants.REF_TYPE_CUSTOMER || otherAccount.getCustomer().getType()!=Constants.REF_TYPE_CUSTOMER)
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}

		if (otherAccount == null) {
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}

		if (otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}

		if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}
		
		// validation send money agent to agent
		if (fundTransferDTO.getTransactionType() == Constants.FLOAT_MANAGEMENT_REQ) {
			if (baseServiceImpl.customer.getType() !=  Constants.REF_TYPE_AGENT || otherAccount.getCustomer().getType()!=Constants.REF_TYPE_AGENT)
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		
		List<ClearingHousePoolMember> chPoolList = baseServiceImpl.eotMobileDao.getClearingHouse(Integer.parseInt(accountDto.getBankCode()), otherAccount.getBank().getBankId());

		if (chPoolList == null) {
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}

		com.eot.dtos.common.Account otherAccountDto = new com.eot.dtos.common.Account();
		otherAccountDto.setAccountAlias(toMobileNo);
		otherAccountDto.setAccountNO(otherAccount.getAccountNumber());
		otherAccountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		otherAccountDto.setBankCode(otherAccount.getBank().getBankId().toString());
		otherAccountDto.setBranchCode(otherAccount.getBranch().getBranchId().toString());

		TransferDirectDTO transferDirectDTO = new TransferDirectDTO();

		transferDirectDTO.setCustomerAccount(accountDto);
		transferDirectDTO.setOtherAccount(otherAccountDto);
		transferDirectDTO.setAmount(amount.doubleValue());
		transferDirectDTO.setChannelType(Constants.EOT_CHANNEL);
		transferDirectDTO.setRequestID(baseServiceImpl.requestID.toString());
		transferDirectDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		transferDirectDTO.setReferenceType(baseServiceImpl.referenceType);
		transferDirectDTO.setTransactionType(fundTransferDTO.getTransactionType().toString());
		transferDirectDTO.setRemarks(fundTransferDTO.getRemarks());
		transferDirectDTO.setPayeeName(otherAccount.getCustomer().getFirstName());
		
		try {

			// rest call updated by bidyut
			transferDirectDTO = processRequest(CoreUrls.TRANSFER_DIRECT_TXN_URL, transferDirectDTO, com.eot.dtos.banking.TransferDirectDTO.class);
			if (transferDirectDTO.getErrorCode() != 0) {
				throw new EOTException(transferDirectDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(transferDirectDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

			if (fundTransferDTO.getTransactionType() == Constants.TXN_TYPE_PAY) {
				fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_PAY_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00",  agent.getAgentCode(), agent.getFirstName()+" ".concat(agent.getLastName()!=null?otherAccount.getCustomer().getLastName():""), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "m-GURUSH Pay", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			} else if (fundTransferDTO.getTransactionType() == Constants.TXN_TYPE_FLOAT_MANAGEMENT) {
				fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("FLOAT_MGMT_PAY_SUCCESS",  new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00", null != transferDirectDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(transferDirectDTO.getServiceChargeAmt().doubleValue()) : "0.00", fundTransferDTO.getPayeeMobileNumber(), otherAccount.getCustomer().getFirstName()+" ".concat(otherAccount.getCustomer().getLastName()!=null?otherAccount.getCustomer().getLastName():""), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "Transfer Float", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			}
			else {

				fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("TRF_DIR_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00", null != transferDirectDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(transferDirectDTO.getServiceChargeAmt().doubleValue()) : "0.00", toMobileNo, otherAccount.getCustomer().getFirstName()+" ".concat(otherAccount.getCustomer().getLastName()!=null?otherAccount.getCustomer().getLastName():""), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "NA", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			}
			return fundTransferDTO;

		} /*
			 * catch (EOTCoreException Exception e) { e.printStackTrace();
			 * //System.out.println(e.getMessageKey()); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey()));
			 * 
			 * throw new EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}
	}
		@Override
		public WithdrawalTransactionsDTO processWithdrawalCustomerInitiated(WithdrawalTransactionsDTO transactionBaseDTO) throws EOTException {

			baseServiceImpl.handleRequest(transactionBaseDTO);
			Customer agent=baseServiceImpl.eotMobileDao.getAgentByAgentCode(transactionBaseDTO.getAgentCode());
			if(agent==null) {
				throw new EOTException(ErrorConstants.AGENT_CODE_NOT_FOUND);
			}

			if (agent.getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED) {
				throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
			}
			// Validation for customer type
			if (agent.getType() != Constants.REF_TYPE_AGENT) {
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			}
			if (!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
			}

			CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getCustomerAccountFromAlias(baseServiceImpl.customer.getCustomerId(), transactionBaseDTO.getAccountAlias());
			if (customerAccount == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
			}

			Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(Constants.DEFAULT_BANK);
			Account bankAccount = bank.getAccount();
			Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(Constants.DEFAULT_BRANCH);


			// load agent account
			CustomerAccount agentAccount = baseServiceImpl.eotMobileDao.getAgentPoolAccountFromAgentId(agent.getCustomerId());

			com.eot.dtos.common.Account customerAccountDTO = new com.eot.dtos.common.Account();
			customerAccountDTO.setAccountAlias(transactionBaseDTO.getAccountAlias());
			customerAccountDTO.setAccountNO(customerAccount.getAccountNumber());
			customerAccountDTO.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
			customerAccountDTO.setBankCode(customerAccount.getBank().getBankId().toString());
			customerAccountDTO.setBranchCode(customerAccount.getBranch().getBranchId().toString());


			com.eot.dtos.common.Account agentAccountDTO = new com.eot.dtos.common.Account();

			// merchant account details
			// bankAccountDto.setAccountAlias(bankAccount.getAlias());
			agentAccountDTO.setAccountNO(agentAccount.getAccountNumber());
			agentAccountDTO.setAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC + "");
			agentAccountDTO.setBankCode(agentAccount.getBank().getBankId().toString());
			agentAccountDTO.setBranchCode(agentAccount.getBranch().getBranchId().toString());

			WithdrawalDTO withdrawalDTO = new WithdrawalDTO();

			withdrawalDTO.setReferenceID(agent.getCustomerId().toString());
			withdrawalDTO.setAmount(transactionBaseDTO.getAmount());
			withdrawalDTO.setChannelType(Constants.EOT_CHANNEL);
			withdrawalDTO.setCustomerAccount(customerAccountDTO);
			withdrawalDTO.setOtherAccount(agentAccountDTO);
			withdrawalDTO.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL + "");
			// withdrawalDTO.setCommissionToBePaidId(merchantDetails.getCustomerId()+"");

			try {
					withdrawalDTO = processRequest(CoreUrls.WITHDRAWAL_TXN_URL, withdrawalDTO, com.eot.dtos.banking.WithdrawalDTO.class);
					if (withdrawalDTO.getErrorCode() != 0) {
						throw new EOTException(withdrawalDTO.getErrorCode());
				}

				Transaction txn = new Transaction();
				txn.setTransactionId(new Long(withdrawalDTO.getTransactionNO()));
				baseServiceImpl.mobileRequest.setTransaction(txn);
				
				String customerType = "";
				if (agent.getType() == Constants.REF_TYPE_AGENT) {
					customerType = "Agent";
				} else if (agent.getType() == Constants.REF_TYPE_MERCHANT) {
					customerType = "Merchant";
				} else if (agent.getType() == Constants.REF_TYPE_AGENT_SOLE_MERCHANT) {
					customerType = "ASM";
				}

				String message = baseServiceImpl.messageSource.getMessage("CUSTOMER_WITHDRAWAL_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()),null != transactionBaseDTO.getAmount() ?  new DecimalFormat("#0.00").format(transactionBaseDTO.getAmount().doubleValue()):"0.00", null != withdrawalDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(withdrawalDTO.getServiceChargeAmt().doubleValue()):"0.00", agent.getAgentCode(), StringUtils.isNotEmpty(agent.getBusinessName())  ? agent.getBusinessName():getName(agent.getFirstName(),agent.getLastName()) /*+ " " + merchantDetails.getLastName()*/, withdrawalDTO.getTransactionNO(),transactionBaseDTO.getRemarks() }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));

				
				transactionBaseDTO.setTransactionType(transactionBaseDTO.getTransactionType());
				transactionBaseDTO.setSuccessResponse(message);

			} 
			finally {

			}
			transactionBaseDTO.setUsername(agent.getFirstName() + agent.getLastName());
			transactionBaseDTO.setAgentCode(agent.getAgentCode());
			return transactionBaseDTO;
		}
		
		
		
		@Override
		public FundTransferDTO validateWithdrawal(FundTransferDTO fundTransferDTO) throws EOTException {

			baseServiceImpl.handleRequest(fundTransferDTO);
		//	String fromAlias = fundTransferDTO.getAccountAlias();
			String agentCode = fundTransferDTO.getAgentCode();
			Double amount = fundTransferDTO.getAmount();

			Customer merchant = baseServiceImpl.eotMobileDao.getAgentByAgentCode(agentCode);
			if (merchant == null)
				throw new EOTException(ErrorConstants.AGENT_CODE_NOT_FOUND);
			
			
			  if(merchant.getType()!=Constants.REF_TYPE_AGENT) throw new
			  EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			 

			if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(merchant.getMobileNumber())) {
				throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
			}

			/*if ((baseServiceImpl.customer.getType() != Constants.REF_TYPE_CUSTOMER)) {
				throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			}*/

			CustomerAccount otherAccount = baseServiceImpl.eotMobileDao.getPayeeAccountFromAgentCode(fundTransferDTO.getAgentCode());

			if (otherAccount == null) {
				throw new EOTException(ErrorConstants.AGENT_CODE_NOT_FOUND);
			}

			/*if (otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
				throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
			}*/
			
			AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(otherAccount.getCustomer().getAppId());
			
			if( otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.AGENT_ACC_DEACTIVATED);
			}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
				throw new EOTException(ErrorConstants.AGENT_ACC_BLOCKED);
			}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_PENDING && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
				throw new EOTException(ErrorConstants.AGENT_KYC_PENDING);
			}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_AGENT){
				throw new EOTException(ErrorConstants.AGENT_KYC_REJECTED);
			}
			
			if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER) {
				throw new EOTException(ErrorConstants.CUSTOMER_ACC_DEACTIVATED);
			}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){
				throw new EOTException(ErrorConstants.CUSTOMER_ACC_BLOCKED);
			}else if( otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
			}
			
			if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
				throw new EOTException(ErrorConstants.MERCHANT_ACC_DEACTIVATED);
			}else if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
				throw new EOTException(ErrorConstants.MERCHANT_ACC_BLOCKED);
			}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_PENDING && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
				throw new EOTException(ErrorConstants.MERCHANT_KYC_PENDING);
			}else if(otherAccount.getCustomer().getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
				throw new EOTException(ErrorConstants.MERCHANT_KYC_REJECTED);
			}
			
			if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_SUSPENDED) {
				throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
			}

			if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
				throw new EOTException(ErrorConstants.INACTIVE_BANK);
			}

			ServiceChargeDTO serviceChargeDTO = new ServiceChargeDTO();

			serviceChargeDTO.setApplicationType(fundTransferDTO.getApplicationType());
			serviceChargeDTO.setTxnAmount(fundTransferDTO.getAmount() != null ? fundTransferDTO.getAmount() : null);
			if (fundTransferDTO.getTxnBankingType() != null)
				serviceChargeDTO.setTxnBankingType(fundTransferDTO.getTxnBankingType());
			else
				serviceChargeDTO.setTxnBankingType(EOTConstants.TXN_TYPE_INTRA);

			serviceChargeDTO.setTransactionType(fundTransferDTO.getTransactionType());
			serviceChargeDTO.setTxnTypeId(Constants.TXN_ID_MERCHANT_WITHDRAWAL);
			if (fundTransferDTO.getApplicationType() == null)
				serviceChargeDTO.setApplicationType(1 + "");

			serviceChargeDTO = otherBankingService.getServiceCharge(serviceChargeDTO);
			String payeeName = otherAccount.getCustomer().getFirstName().concat(" ").concat(otherAccount.getCustomer().getLastName());
			fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("WITHDRAWAL_CONFIRM", new String[] { agentCode, payeeName, null != amount ? new DecimalFormat("#0.00").format( amount.doubleValue()):"0.00", serviceChargeDTO.getServiceCharge() != null ? new DecimalFormat("#0.00").format( serviceChargeDTO.getServiceCharge().doubleValue()):"0.00" , fundTransferDTO.getRemarks()}, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

			fundTransferDTO.setServiceChargeAmt(serviceChargeDTO.getServiceCharge());
			fundTransferDTO.setServiceCharge(serviceChargeDTO.getServiceCharge());
			fundTransferDTO.setCustomerName(merchant.getFirstName());
			fundTransferDTO.setAgentCode(merchant.getAgentCode());
			fundTransferDTO.setAgentName(StringUtils.isNotEmpty(merchant.getBusinessName()) ? merchant.getBusinessName():getName(merchant.getFirstName(),merchant.getLastName()));
			if(fundTransferDTO.getRemarks()=="" ||fundTransferDTO.getRemarks().equals("")){
//				fundTransferDTO.setRemarks("m-GURUSH Pay");
				fundTransferDTO.setRemarks("Withdrawal");
			}
			return fundTransferDTO;

		}

		@Override
		public FundTransferDTO doMarchentFloat(FundTransferDTO fundTransferDTO) throws EOTException {


			baseServiceImpl.handleRequest(fundTransferDTO);
			Customer agent = null;
			String toMobileNo = fundTransferDTO.getPayeeMobileNumber();
			if (fundTransferDTO.getTransactionType() == Constants.EXEC_PAY_MERCHANT) {
				agent = baseServiceImpl.eotMobileDao.getAgentByAgentCode(fundTransferDTO.getAgentCode());
				if(agent.getType()!=Constants.REF_TYPE_MERCHANT)
					throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
				toMobileNo = agent.getCountry().getIsdCode() + agent.getMobileNumber();
				
			}
			String fromAlias = fundTransferDTO.getAccountAlias();
			Integer fromAliasType = fundTransferDTO.getAliasType();

			Double amount = fundTransferDTO.getAmount();
			String txnPin = fundTransferDTO.getTransactionPIN();

			if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
				throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
			}

			if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber()).equalsIgnoreCase(toMobileNo)) {
				throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
			}

			com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

			accountDto.setAccountAlias(fromAlias);
			accountDto.setAccountType(fromAliasType.toString());

			if (Constants.ALIAS_TYPE_CARD_ACC == fromAliasType) {

				CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
				if (card == null) {
					throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
				}

				accountDto.setAccountNO(card.getCardNumber());
				accountDto.setBankCode(card.getBank().getBankId().toString());

			} else if (Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType) {

				CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), fromAlias);
				if (account == null) {
					throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
				}

				accountDto.setAccountNO(account.getAccountNumber());
				accountDto.setBankCode(account.getBank().getBankId().toString());
				accountDto.setBranchCode(account.getBranch().getBranchId().toString());

			}

			CustomerAccount otherAccount = baseServiceImpl.eotMobileDao.getPayeeAccountFromMobileNo(toMobileNo);
			
			if (otherAccount == null) {
				throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
			}

			if (otherAccount.getCustomer().getActive()==EOTConstants.CUSTOMER_STATUS_DEACTIVATED && otherAccount.getCustomer().getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
				throw new EOTException(ErrorConstants.MERCHANT_ACC_DEACTIVATED);
			}

			if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
				throw new EOTException(ErrorConstants.INACTIVE_BANK);
			}
			
			// validation send money merchnat to merchnat
			if (fundTransferDTO.getTransactionType() == Constants.EXEC_PAY_MERCHANT) {
				if (baseServiceImpl.customer.getType() !=  Constants.REF_TYPE_MERCHANT || otherAccount.getCustomer().getType()!=Constants.REF_TYPE_MERCHANT)
					throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
			}
			
			List<ClearingHousePoolMember> chPoolList = baseServiceImpl.eotMobileDao.getClearingHouse(Integer.parseInt(accountDto.getBankCode()), otherAccount.getBank().getBankId());

			if (chPoolList == null) {
				throw new EOTException(ErrorConstants.INVALID_CH_POOL);
			}

			com.eot.dtos.common.Account otherAccountDto = new com.eot.dtos.common.Account();
			otherAccountDto.setAccountAlias(toMobileNo);
			otherAccountDto.setAccountNO(otherAccount.getAccountNumber());
			otherAccountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
			otherAccountDto.setBankCode(otherAccount.getBank().getBankId().toString());
			otherAccountDto.setBranchCode(otherAccount.getBranch().getBranchId().toString());

			TransferDirectDTO transferDirectDTO = new TransferDirectDTO();

			transferDirectDTO.setCustomerAccount(accountDto);
			transferDirectDTO.setOtherAccount(otherAccountDto);
			transferDirectDTO.setAmount(amount.doubleValue());
			transferDirectDTO.setChannelType(Constants.EOT_CHANNEL);
			transferDirectDTO.setRequestID(baseServiceImpl.requestID.toString());
			transferDirectDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
			transferDirectDTO.setReferenceType(baseServiceImpl.referenceType);
			transferDirectDTO.setTransactionType(fundTransferDTO.getTransactionType().toString());
			transferDirectDTO.setRemarks(fundTransferDTO.getRemarks());
			transferDirectDTO.setPayeeName(otherAccount.getCustomer().getFirstName());
			
			try {

				// rest call updated by bidyut
				transferDirectDTO = processRequest(CoreUrls.TRANSFER_DIRECT_TXN_URL, transferDirectDTO, com.eot.dtos.banking.TransferDirectDTO.class);
				if (transferDirectDTO.getErrorCode() != 0) {
					throw new EOTException(transferDirectDTO.getErrorCode());
				}

				Transaction txn = new Transaction();
				txn.setTransactionId(new Long(transferDirectDTO.getTransactionNO()));
				baseServiceImpl.mobileRequest.setTransaction(txn);

				if (fundTransferDTO.getTransactionType() == Constants.EXEC_PAY_MERCHANT) {
					fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("MERCHANT_FLOAT_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00",  null != transferDirectDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(transferDirectDTO.getServiceChargeAmt().doubleValue()) : "0.00", agent.getAgentCode(), StringUtils.isNotEmpty(agent.getBusinessName()) ? agent.getBusinessName():getName(agent.getFirstName(),agent.getLastName()), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "Pay Merchant", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
				}
				
				return fundTransferDTO;

			} /*
				 * catch (EOTCoreException Exception e) { e.printStackTrace();
				 * //System.out.println(e.getMessageKey()); //throw new
				 * EOTException(Integer.parseInt(e.getMessageKey()));
				 * 
				 * throw new EOTException(ErrorConstants.SERVICE_ERROR); }
				 */
			finally {

			}
		
		}
		
		
		
		@Override
		public BankTransactionDTO walletToBankTransfer(BankTransactionDTO transactionDTO) throws EOTException {
			
			CustomerAccount customerAccount= null;
			baseServiceImpl.handleRequest(transactionDTO);
			CustomerBankAccount  customerBankAccount=baseServiceImpl.eotMobileDao.getCustomerBankAccountFromAccountNumber(transactionDTO.getAccountNumber());

			if(customerBankAccount==null )
				throw new EOTException(ErrorConstants.INVALID_BANK_ACCOUNT);
			
			if(baseServiceImpl.customer.getType()==Constants.REF_TYPE_AGENT)
				customerAccount= baseServiceImpl.eotMobileDao.getAgentPoolAccountFromAgentId(baseServiceImpl.customer.getCustomerId());
			else
				customerAccount= baseServiceImpl.eotMobileDao.getCustomerAccountWithCustomerId(baseServiceImpl.customer.getCustomerId());
			
			
			com.eot.dtos.common.Account customerAccountDTO = new com.eot.dtos.common.Account();
			customerAccountDTO.setAccountAlias(transactionDTO.getAccountAlias());
			customerAccountDTO.setAccountType(transactionDTO.getAliasType().toString());
			customerAccountDTO.setAccountNO(customerAccount.getAccountNumber());
			customerAccountDTO.setBankCode(customerAccount.getBank().getBankId().toString());
			customerAccountDTO.setBranchCode(customerAccount.getBranch().getBranchId().toString());
			
			//to do: load bank pool account number
			com.eot.dtos.common.Account bankPoolAccountNo = new com.eot.dtos.common.Account();
		/*
		 * bankPoolAccountNo.setAccountAlias(toMobileNo);
		 * bankPoolAccountNo.setAccountNO(otherAccount.getAccountNumber());
		 * bankPoolAccountNo.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		 * bankPoolAccountNo.setBankCode(otherAccount.getBank().getBankId().toString());
		 * bankPoolAccountNo.setBranchCode(otherAccount.getBranch().getBranchId().
		 * toString());
		 */
			
			
			
			return transactionDTO;	
		}
		public SmsResponseDTO sendSMS(String url,SmsHeader smsHeader)
		{
			SmsResponseDTO dto= new SmsResponseDTO();
			try {
				dto= 	restTemplate.postForObject(url, smsHeader, SmsResponseDTO.class);
			}catch (Exception e) {
				dto.setStatus("0");
//				e.printStackTrace();
			}
			return dto;
		}
		
		
	@Override
	public WithdrawalTransactionsDTO processAgentCashOut(WithdrawalTransactionsDTO transactionBaseDTO)
			throws EOTException {

		baseServiceImpl.handleRequest(transactionBaseDTO);
		if (!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		List<CustomerAccount> accountList=baseServiceImpl.eotMobileDao.getAccountListFromCustomerId(baseServiceImpl.customer.getCustomerId());
		
		if(accountList.isEmpty())
		{
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
		
		for(CustomerAccount  custAcc:accountList)
		{
			//Account acc = baseServiceImpl.eotMobileDao.getAccount(custAcc.getAccountNumber());
			//System.out.println(acc.getAliasType());
			if(custAcc.getAccount().getAliasType()==Constants.ALIAS_TYPE_WALLET_ACCOUNT) {
//				currentBalance.setCurrentBalance(new DecimalFormat("#0.00").format((custAcc.getAccount().getCurrentBalance())));
				custAcc.getAccount().getCurrentBalance();
				if(baseServiceImpl.customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT) {
					if(custAcc.getAccount().getCurrentBalance()<transactionBaseDTO.getAmount())
						throw new EOTException(ErrorConstants.INSUFFIENT_BALANCE);
				}
			}
			if(custAcc.getAccount().getAliasType()==Constants.ALIAS_TYPE_COMMISSION_ACCOUNT) {
//				currentBalance.setCommission(new DecimalFormat("#0.00").format((custAcc.getAccount().getCurrentBalance())));
				if(custAcc.getAccount().getCurrentBalance()<transactionBaseDTO.getAmount())
					throw new EOTException(ErrorConstants.INSUFFIENT_BALANCE);
			}
				
		}
		Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(Constants.DEFAULT_BANK);
		Account bankAccount = bank.getAccount();
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(Constants.DEFAULT_BRANCH);

		Customer merchantDetails = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());
//		CustomerAccount merchantAccount = baseServiceImpl.eotMobileDao.getAgentPoolAccountFromAgentId(merchantDetails.getCustomerId());
		CustomerAccount merchantAccount=null;
		if(baseServiceImpl.customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT)
			merchantAccount =baseServiceImpl.eotMobileDao.getCommissionAccountFromAgentId(merchantDetails.getCustomerId());
		else
			merchantAccount = baseServiceImpl.eotMobileDao.getCustomerAccountWithCustomerId(merchantDetails.getCustomerId());

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(transactionBaseDTO.getTransactionType());
		
		PendingTransaction pendingTransaction=baseServiceImpl.eotMobileDao.loadPendingTxn(merchantDetails,transactionBaseDTO.getTransactionType());
		if(pendingTransaction!=null)
			throw new EOTException(ErrorConstants.TXN_ALREADY_EXIST);
		
		pendingTransaction = new PendingTransaction();
		pendingTransaction.setAmount(transactionBaseDTO.getAmount());
		pendingTransaction.setCustomer(merchantDetails);
		pendingTransaction.setReferenceType(merchantDetails.getType());
		pendingTransaction.setCustomerAccount(merchantAccount.getAccountNumber());
		pendingTransaction.setBank(merchantAccount.getBank());
//		pendingTransaction.setCustomerAccountType(Constants.ALIAS_TYPE_MOBILE_ACC);
		pendingTransaction.setInitiatedBy(merchantDetails.getCustomerId() + "");
		pendingTransaction.setOtherAccount(merchantAccount.getAccountNumber());
//		pendingTransaction.setOtherAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC);
		pendingTransaction.setStatus(Constants.TRANSACTION_INITIATED_FOR_APPROVAL);
		pendingTransaction.setTransactionType(transactionType);
		pendingTransaction.setTransactionDate(new Date());
		pendingTransaction.setName(merchantDetails.getFirstName()+" "+merchantDetails.getLastName());
		pendingTransaction.setCustomerCode(merchantDetails.getAgentCode());
		pendingTransaction.setCustomerMobileNo(merchantDetails.getMobileNumber());

		baseServiceImpl.eotMobileDao.save(pendingTransaction);

		String message = null;
		if(transactionBaseDTO.getTransactionType()==Constants.TXN_ID_MERCHANT_MERCHANT_PAYOUT) {
		message = baseServiceImpl.messageSource.getMessage("MERCHANT_PAY_COUT",
				new String[] { DateUtil.formattedDateAndTime(new Date()),
						new DecimalFormat("#.00").format(transactionBaseDTO.getAmount()),
						merchantDetails.getMobileNumber(), merchantDetails.getFirstName(),
						transactionBaseDTO.getRemarks() != null ? transactionBaseDTO.getRemarks() : "Cash Out" },
				new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		}
		else {
			message = baseServiceImpl.messageSource.getMessage("AGENT_CASH_COUT",
					new String[] { DateUtil.formattedDateAndTime(new Date()),
							new DecimalFormat("#.00").format(transactionBaseDTO.getAmount()),
							merchantDetails.getMobileNumber(), merchantDetails.getFirstName(),
							transactionBaseDTO.getRemarks() != null ? transactionBaseDTO.getRemarks() : "Cash Out" },
					new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		}
		transactionBaseDTO.setSuccessResponse(message);
		return transactionBaseDTO;
	}
	
	
	@Override
	public WithdrawalTransactionsDTO loadPendingTxnForMerchant(WithdrawalTransactionsDTO transaction) throws EOTException {
		baseServiceImpl.handleRequest(transaction);
		Customer agent = baseServiceImpl.customer;
		if (agent == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		PendingTransaction pt = baseServiceImpl.eotMobileDao.loadPendingRecord(transaction.getMobileNumber(),EOTConstants.TXN_TYPE_MERCHANT_PAYOUT);
		if (pt == null)
			throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);
		
		Customer merchant = baseServiceImpl.eotMobileDao.getCustomerByMobile(pt.getCustomerMobileNo());
		if (merchant == null)
			throw new EOTException(ErrorConstants.INVALID_MERCHANT);
		
		CustomerAccount agentAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(agent.getCustomerId());
		if (agentAccount == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);

		
//		transaction.setCustomer(agent);
		transaction.setAccountNumber(agentAccount.getAccountNumber());
		Customer merchent = null;
		transaction.setAmount(pt.getAmount());
		transaction.setTransactionTime(new Long(pt.getTransactionDate().getTime()));
		transaction.setTransactionType(pt.getTransactionType().getTransactionType());
		transaction.setServiceCharge(0.0);
		transaction.setName(merchant.getFirstName()+" "+merchant.getLastName());
		transaction.setMerchantCode(merchant.getAgentCode());
		transaction.setBusinessName(merchant.getBusinessName());
		transaction.setMobileNumber(merchant.getMobileNumber());
		try
		{
			ServiceChargeDTO serviceChargeDTO = new ServiceChargeDTO();
			serviceChargeDTO.setTransactionType(pt.getTransactionType().getTransactionType());
			serviceChargeDTO.setApplicationType(1+"");//
			serviceChargeDTO.setTxnTypeId(pt.getTransactionType().getTransactionType());
			serviceChargeDTO.setTxnAmount(pt.getAmount());
			serviceChargeDTO.setTxnBankingType(1);// 1 is for interbank transaction
			serviceChargeDTO=otherBankingService.getServiceCharge(serviceChargeDTO);
			transaction.setServiceCharge(serviceChargeDTO.getServiceCharge());
			transaction.setServiceChargeAmt(serviceChargeDTO.getServiceChargeAmt());
		}catch (Exception e) {
//			e.printStackTrace();
		}

		return transaction;
	}
	
	@Override
	@Transactional
	public ReversalTransactionDTO processReversalTransaction(ReversalTransactionDTO reversalTransactionDTO)
			throws EOTException {
		baseServiceImpl.handleRequest(reversalTransactionDTO);
		Transaction transaction = baseServiceImpl.eotMobileDao.getTransactionByTxnId(Integer.parseInt(reversalTransactionDTO.getTransactionId()));

		AdjustmentTransactionDTO adjustmentTransactionDTO = new AdjustmentTransactionDTO();

		Account customerAccount = baseServiceImpl.eotMobileDao.getAccount(transaction.getCustomerAccount());
		
		String applicationId = new String(customerAccount.getCustomerAccount().getCustomer().getAppId());
		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(applicationId);
		
		if(appMaster.getStatus() == Constants.APP_STATUS_BLOCKED && customerAccount.getCustomerAccount().getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){
			throw new EOTException(ErrorConstants.CUSTOMER_ACC_BLOCKED);
		}else if( customerAccount.getCustomerAccount().getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED && customerAccount.getCustomerAccount().getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.CUSTOMER_ACC_DEACTIVATED);
		}else if( customerAccount.getCustomerAccount().getCustomer().getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && customerAccount.getCustomerAccount().getCustomer().getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
			throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
		}

		//MobileRequest mobileRequest = baseServiceImpl.eotMobileDao.getMobileRequest(Long.parseLong(reversalTransactionDTO.getTransactionId()));
		
		Account bankAccount = baseServiceImpl.eotMobileDao.getAccount(transaction.getOtherAccount());// Agent account
		
		/*MobileRequest request = new MobileRequest();
		request.setReferenceId(transaction.getReferenceId());
		request.setReferenceType(EOTConstants.REFERENCE_TYPE_AGENT);
		request.setStatus(EOTConstants.MOBEQUEST_STATUS_FAILURE);
		TransactionType txnType = new TransactionType();
		txnType.setTransactionType(EOTConstants.TXN_ID_TYPE_REVERSAL);
		request.setTransactionType(txnType);
		request.setTransactionTime(new Date());

		baseServiceImpl.eotMobileDao.save(request);*/
		
		if (!reversalTransactionDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(customerAccount.getAlias());
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setAccountType(EOTConstants.ALIAS_TYPE_WALLET_ACCOUNT + "");
		accountDto.setBankCode(customerAccount.getCustomerAccount().getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getCustomerAccount().getBranch().getBranchId().toString());

		com.eot.dtos.common.Account bankAccountDto = new com.eot.dtos.common.Account();
		bankAccountDto.setAccountType(EOTConstants.ALIAS_TYPE_WALLET_ACCOUNT + "");// Agent Account
		bankAccountDto.setAccountAlias(bankAccount.getAlias());
		bankAccountDto.setAccountNO(bankAccount.getAccountNumber());
		bankAccountDto.setBankCode(bankAccount.getCustomerAccount().getBank().getBankId().toString());
		bankAccountDto.setBranchCode(bankAccount.getCustomerAccount().getBranch().getBranchId().toString());

		adjustmentTransactionDTO.setTransactionTypeRef(transaction.getTransactionType().getTransactionType().toString());
		adjustmentTransactionDTO.setTransactionId(reversalTransactionDTO.getTransactionId());
		adjustmentTransactionDTO.setAmount(transaction.getAmount());
		adjustmentTransactionDTO.setFee(reversalTransactionDTO.getFee());
		adjustmentTransactionDTO.setDescription(reversalTransactionDTO.getDescription());
		adjustmentTransactionDTO.setReferenceID(customerAccount.getReferenceId());
		adjustmentTransactionDTO.setChannelType(EOTConstants.EOT_CHANNEL);
		adjustmentTransactionDTO.setTransactionType("61");
		adjustmentTransactionDTO.setCustomerAccount(accountDto);
		adjustmentTransactionDTO.setOtherAccount(bankAccountDto);
		adjustmentTransactionDTO.setRequestID(transaction.getCustomerAccount());
		adjustmentTransactionDTO.setMobileNumber(reversalTransactionDTO.getMobileNumber());

		try {

			adjustmentTransactionDTO = processRequest(CoreUrls.ADJUSTMENT_TXN_URL, adjustmentTransactionDTO, com.eot.dtos.banking.AdjustmentTransactionDTO.class);
			if (adjustmentTransactionDTO.getErrorCode() != 0) {
				throw new EOTException(adjustmentTransactionDTO.getErrorCode());
			}
			String message = baseServiceImpl.messageSource.getMessage("REVERSAL_SUCCESS",
					new String[] { DateUtil.formattedDateAndTime(new Date()),
							new DecimalFormat("#.00").format(adjustmentTransactionDTO.getAmount()), adjustmentTransactionDTO.getTransactionNO(),
							reversalTransactionDTO.getMobileNumber(), reversalTransactionDTO.getCustomerName(),
							reversalTransactionDTO.getRemarks() != null ? reversalTransactionDTO.getRemarks() : "Transaction Reverted" },
					new Locale(baseServiceImpl.customer.getDefaultLanguage()));
			reversalTransactionDTO.setSuccessResponse(message);
			reversalTransactionDTO.setStatus(adjustmentTransactionDTO.getErrorCode());
		} catch (EOTException e) {
//			e.printStackTrace();
			throw new EOTException(e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			throw new EOTException(ErrorConstants.CORE_CONNECTION_ERROR);
		}
	
		return reversalTransactionDTO;
	}
	
	@Override
	public WithdrawalTransactionsDTO processMerchantPayout(WithdrawalTransactionsDTO transactionBaseDTO)
			throws EOTException {

		baseServiceImpl.handleRequest(transactionBaseDTO);
		/*
		 * if(!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.
		 * getTransactionPin().toString())){ throw new
		 * EOTException(ErrorConstants.INVALID_TXN_PIN); }
		 */

		if ((baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber())
				.equalsIgnoreCase(transactionBaseDTO.getMobileNumber())) {
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		Customer merchant = baseServiceImpl.eotMobileDao
				.getCustomerByMobile(transactionBaseDTO.getMobileNumber());
		if (merchant == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		if (merchant.getActive() == EOTConstants.CUSTOMER_STATUS_DEACTIVATED) {
			throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
		}
		if (merchant.getActive() == EOTConstants.CUSTOMER_STATUS_SUSPENDED) {
			throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
		}

		PendingTransaction pt = baseServiceImpl.eotMobileDao.loadPendingTxn(merchant,
				EOTConstants.TXN_TYPE_MERCHANT_PAYOUT);
		if (pt == null) {
			throw new EOTException(ErrorConstants.TXN_DETAILS_NOT_AVAILABLE);
		}
		// Validation for customer type
		if (merchant.getType() != Constants.REF_TYPE_MERCHANT) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		if (baseServiceImpl.customer.getType() != Constants.REF_TYPE_AGENT) {
			throw new EOTException(ErrorConstants.TXN_NOT_ALLOWED);
		}
		if (!transactionBaseDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		if (otpForSaleEnabled) {
			OtpDTO otpDto = new OtpDTO();
			otpDto.setOtphash(transactionBaseDTO.getCustomerOTP());
			otpDto.setReferenceId(merchant.getCustomerId() + "");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			// otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_WITHDRAWA);
			otpDto.setAmount(transactionBaseDTO.getAmount());
			Otp otp = baseServiceImpl.eotMobileDao.verifyOTPWithAmount(otpDto);
			// Otp otp = baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
			System.out.println("baseServiceImpl.customerPin - " + transactionBaseDTO.getCustomerOTP());
			System.out.println("db - " + otpDto.getOtphash());
			if (otp == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}

			otp.setStatus(OtpStatusEnum.USED.getCode());
			baseServiceImpl.eotMobileDao.update(otp);

		}

//		CustomerAccount account = baseServiceImpl.eotMobileDao.getCustomerAccountFromAlias(merchant.getCustomerId(),transactionBaseDTO.getAccountAlias());
		CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(merchant.getCustomerId());
		if (account == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
		}

		Customer agentDetails = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		// load Agent account
		CustomerAccount agentAccount = baseServiceImpl.eotMobileDao.getAgentPoolAccountFromAgentId(agentDetails.getCustomerId());

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(account.getAccount().getAlias());
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC + "");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		com.eot.dtos.common.Account customerAccountDTO = new com.eot.dtos.common.Account();

		// merchant account details
		// bankAccountDto.setAccountAlias(bankAccount.getAlias());
		customerAccountDTO.setAccountNO(agentAccount.getAccountNumber());
		customerAccountDTO.setAccountType(Constants.ALIAS_TYPE_MERCHANT_ACC + "");
		customerAccountDTO.setBankCode(agentAccount.getBank().getBankId().toString());
		customerAccountDTO.setBranchCode(agentAccount.getBranch().getBranchId().toString());

		WithdrawalDTO withdrawalDTO = new WithdrawalDTO();

		withdrawalDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		withdrawalDTO.setAmount(transactionBaseDTO.getAmount());
		withdrawalDTO.setChannelType(Constants.EOT_CHANNEL);
		withdrawalDTO.setCustomerAccount(accountDto);
		withdrawalDTO.setOtherAccount(customerAccountDTO);
		withdrawalDTO.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL + "");

		try {
			// ===========================================================================================
			// spring web service call to core: commentec by bidyut
			// withdrawalDTO = bankingServiceClientStub.withdrawal(withdrawalDTO);

			// rest call to core : modified by bidyut
			withdrawalDTO.setTransactionType(Constants.TXN_ID_MERCHANT_MERCHANT_PAYOUT + "");
			withdrawalDTO = processRequest(CoreUrls.WITHDRAWAL_TXN_URL, withdrawalDTO,
					com.eot.dtos.banking.WithdrawalDTO.class);
			if (withdrawalDTO.getErrorCode() != 0) {
				throw new EOTException(withdrawalDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(withdrawalDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);
			String message = baseServiceImpl.messageSource
					.getMessage("MERCHANT_PAY_OUT",
							new String[] { DateUtil.formattedDateAndTime(new Date()),
									null != transactionBaseDTO.getAmount() ? new DecimalFormat("#0.00")
											.format(transactionBaseDTO.getAmount().doubleValue()) : "0.00",
									null != withdrawalDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00")
											.format(withdrawalDTO.getServiceChargeAmt().doubleValue()) : "0.00",
									transactionBaseDTO.getMobileNumber(),
									getName(merchant.getFirstName(),
											merchant.getLastName()) /* + " " + merchantDetails.getLastName() */,
									withdrawalDTO.getTransactionNO() },
							new Locale(baseServiceImpl.customer.getDefaultLanguage()));

			transactionBaseDTO.setTransactionType(transactionBaseDTO.getTransactionType());
			transactionBaseDTO.setSuccessResponse(message);

			// =================Withdrawal Request Save to Pending transaction==================

			pt.setStatus(Constants.TRANSACTION_STATUS_SUCCESS);
			pt.setApprovedBy(agentDetails.getAgentCode());
			baseServiceImpl.eotMobileDao.update(pt);

			transactionBaseDTO.setSuccessResponse(message);

		} finally {

		}
		transactionBaseDTO.setUsername(agentDetails.getFirstName() + agentDetails.getLastName());
		return transactionBaseDTO;
	}
	@Override
	public WithdrawalTransactionsDTO rejectMerchantPayout(WithdrawalTransactionsDTO transactionBaseDTO) throws EOTException {
		baseServiceImpl.handleRequest(transactionBaseDTO);

		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(transactionBaseDTO.getApplicationId());

		if (customer == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);

		CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(customer.getCustomerId());

		if (customerAccount == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);

		transactionBaseDTO.setAccountNumber(customerAccount.getAccountNumber());
		
		Customer merchant = baseServiceImpl.eotMobileDao.getCustomerByMobile(transactionBaseDTO.getMobileNumber());

		if (merchant == null)
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);

		PendingTransaction pendingTransaction = baseServiceImpl.eotMobileDao.loadPendingTxn(merchant, EOTConstants.TXN_TYPE_MERCHANT_PAYOUT);

		if (pendingTransaction == null)
			throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);

		// =================Updating Pending transaction for success==================
		pendingTransaction.setStatus(Constants.TRANSACTION_STATUS_REJECTED);
		pendingTransaction.setApprovedBy(customer.getCustomerId().toString());
		baseServiceImpl.eotMobileDao.update(pendingTransaction);

		// String message =
		// baseServiceImpl.messageSource.getMessage("WITHDRAWAL_REJECTED", new String[]
		// { transactionBaseDTO.getPendingTxnRecordId() + "" }, new
		// Locale(baseServiceImpl.customer.getDefaultLanguage()));
		String message = baseServiceImpl.messageSource.getMessage("WITHDRAWAL_REJECTED", new String[] { "" }, new Locale(baseServiceImpl.customer.getDefaultLanguage()));
		transactionBaseDTO.setSuccessResponse(message);
		transactionBaseDTO.setMessageDescription(message);
		return transactionBaseDTO;
	}
	
	@Override
	public FundTransferDTO processWalletToBank(FundTransferDTO fundTransferDTO) throws EOTException {

		baseServiceImpl.handleRequest(fundTransferDTO);
		String fromAlias = fundTransferDTO.getAccountAlias();
		Integer fromAliasType = fundTransferDTO.getAliasType();

		Double amount = fundTransferDTO.getAmount();
		String txnPin = fundTransferDTO.getTransactionPIN();

		if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(baseServiceImpl.customer.getCustomerId());

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(customerAccount.getAccount().getAlias());
		accountDto.setAccountType(customerAccount.getAccount().getAccountType()+"");
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setBankCode(customerAccount.getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getBranch().getBranchId().toString());


//		CustomerBankAccount customerBankAccount = baseServiceImpl.eotMobileDao.getCustomerBankAccountFromAccountNumber(fundTransferDTO.getAccountNumber());
		Account customerBankAccount = baseServiceImpl.eotMobileDao.getAccount("1000000000207");
		// validation send money customer to customer

		if (customerBankAccount == null) {
			throw new EOTException(ErrorConstants.INVALID_BANK_ACCOUNT);
		}

		/*if (customerBankAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}*/
		

		/*if (customerBankAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}	*/	
		
		// validation send money agent to agent
		


		com.eot.dtos.common.Account bankAccountDTO = new com.eot.dtos.common.Account();
//		otherAccountDto.setAccountAlias(toMobileNo);
		bankAccountDTO.setAccountNO(customerBankAccount.getAccountNumber());
		bankAccountDTO.setAccountType(Constants.ALIAS_TYPE_BANK_ACC+ "");
		bankAccountDTO.setBankCode(customerAccount.getBank().getBankId().toString());
		bankAccountDTO.setBranchCode(customerAccount.getBranch().getBranchId().toString());

		TransferDirectDTO transferDirectDTO = new TransferDirectDTO();

		transferDirectDTO.setCustomerAccount(accountDto);
		transferDirectDTO.setOtherAccount(bankAccountDTO);
		transferDirectDTO.setAmount(amount.doubleValue());
		transferDirectDTO.setChannelType(Constants.EOT_CHANNEL);
		transferDirectDTO.setRequestID(baseServiceImpl.requestID.toString());
		transferDirectDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		transferDirectDTO.setReferenceType(baseServiceImpl.referenceType);
		transferDirectDTO.setTransactionType(fundTransferDTO.getTransactionType().toString());
		transferDirectDTO.setRemarks(fundTransferDTO.getRemarks());
		
		try {

			// rest call updated by bidyut
			transferDirectDTO = processRequest(CoreUrls.TRANSFER_DIRECT_TXN_URL, transferDirectDTO, com.eot.dtos.banking.TransferDirectDTO.class);
			if (transferDirectDTO.getErrorCode() != 0) {
				throw new EOTException(transferDirectDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(transferDirectDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);
			
			fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("WALLET_BANK_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00", null != transferDirectDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(transferDirectDTO.getServiceChargeAmt().doubleValue()) : "0.00", fundTransferDTO.getAccountNumber(), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "NA", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			
			String cBankAccountNumber = baseServiceImpl.eotMobileDao.getCustomerBankAccountByID(baseServiceImpl.customer.getCustomerId().toString()).getBankAccountNumber();

			SmsLog smsLog = new SmsLog();
			smsLog.setMobileNumber(baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber());
			smsLog.setMessageType(Constants.MESSAGE_TYPE_WALLET_TO_BANK);
			smsLog.setEncoding(1);
			smsLog.setCreatedDate(new Date());
			smsLog.setMessage(transferDirectDTO.getTransactionNO() + " Confirmed. SSP "+ ((null != amount) ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00") +" sent to Bank Account: "+ cBankAccountNumber +" on "+new SimpleDateFormat("dd/MM/yyyy 'at' h:mm aa").format(Calendar.getInstance().getTime())+". New m-GURUSH balance is SSP "+new DecimalFormat("#0.00").format(transferDirectDTO.getCustomerAccount().getAccountBalance().doubleValue()));
			smsLog.setScheduledDate(new Date());
			smsLog.setStatus(0);
			baseServiceImpl.eotMobileDao.save(smsLog);
						
			return fundTransferDTO;

		} /*
			 * catch (EOTCoreException Exception e) { e.printStackTrace();
			 * //System.out.println(e.getMessageKey()); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey()));
			 * 
			 * throw new EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}
	}
	
	@Override
	public FundTransferDTO processBankToWallet(FundTransferDTO fundTransferDTO) throws EOTException {

		baseServiceImpl.handleRequest(fundTransferDTO);

		Double amount = fundTransferDTO.getAmount();
		String txnPin = fundTransferDTO.getTransactionPIN();

		if (!txnPin.equals(baseServiceImpl.customer.getTransactionPin().toString())) {
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		
		CustomerAccount customerAccount = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(baseServiceImpl.customer.getCustomerId());

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountAlias(customerAccount.getAccount().getAlias());
		accountDto.setAccountType(Constants.ALIAS_TYPE_BANK_ACC + "");
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setBankCode(customerAccount.getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getBranch().getBranchId().toString());


//		CustomerBankAccount customerBankAccount = baseServiceImpl.eotMobileDao.getCustomerBankAccountFromAccountNumber(fundTransferDTO.getAccountNumber());
		Account customerBankAccount = baseServiceImpl.eotMobileDao.getBankFromBankId(customerAccount.getBank().getBankId()).getAccount();
		

		if (customerBankAccount == null) {
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}

		/*if (otherAccount.getCustomer().getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED) { // validate status of Payee
			throw new EOTException(ErrorConstants.INACTIVE_PAYEE);
		}
		
		if (otherAccount.getCustomer().getActive() == EOTConstants.CUSTOMER_STATUS_SUSPENDED) { // validate status of Payee
			throw new EOTException(ErrorConstants.Y_ACCOUNT_SUSPENDED);
		}

		if (otherAccount.getBank().getStatus() == Constants.INACTIVE_BANK_STATUS) {
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}*/
		
		// validation send money agent to agent

		com.eot.dtos.common.Account bankAccountDTO = new com.eot.dtos.common.Account();
//		otherAccountDto.setAccountAlias(toMobileNo);
		bankAccountDTO.setAccountNO(customerBankAccount.getAccountNumber());
		bankAccountDTO.setAccountType(Constants.ALIAS_TYPE_WALLET_ACCOUNT + "");
		bankAccountDTO.setBankCode(customerAccount.getBank().getBankId().toString());
		bankAccountDTO.setBranchCode(customerAccount.getBranch().getBranchId().toString());

		TransferDirectDTO transferDirectDTO = new TransferDirectDTO();

		transferDirectDTO.setCustomerAccount(accountDto);
		transferDirectDTO.setOtherAccount(bankAccountDTO );
		transferDirectDTO.setAmount(amount.doubleValue());
		transferDirectDTO.setChannelType(Constants.EOT_CHANNEL);
		transferDirectDTO.setRequestID(baseServiceImpl.requestID.toString());
		transferDirectDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		transferDirectDTO.setReferenceType(baseServiceImpl.referenceType);
		transferDirectDTO.setTransactionType(fundTransferDTO.getTransactionType().toString());
		transferDirectDTO.setRemarks(fundTransferDTO.getRemarks());
//		transferDirectDTO.setPayeeName(otherAccount.getCustomer().getFirstName());
		
		try {

			// rest call updated by bidyut
			transferDirectDTO = processRequest(CoreUrls.TRANSFER_DIRECT_TXN_URL, transferDirectDTO, com.eot.dtos.banking.TransferDirectDTO.class);
			if (transferDirectDTO.getErrorCode() != 0) {
				throw new EOTException(transferDirectDTO.getErrorCode());
			}

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(transferDirectDTO.getTransactionNO()));
			baseServiceImpl.mobileRequest.setTransaction(txn);

			
			fundTransferDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("BANK_TO_WALLET_SUCCESS", new String[] { DateUtil.formattedDateAndTime(new Date()), null != amount ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00", null != transferDirectDTO.getServiceChargeAmt() ? new DecimalFormat("#0.00").format(transferDirectDTO.getServiceChargeAmt().doubleValue()) : "0.00", fundTransferDTO.getAccountNumber(), transferDirectDTO.getRemarks() != null ? transferDirectDTO.getRemarks() : "NA", transferDirectDTO.getTransactionNO() }, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
			
			String cBankAccountNumber = baseServiceImpl.eotMobileDao.getCustomerBankAccountByID(baseServiceImpl.customer.getCustomerId().toString()).getBankAccountNumber();

			SmsLog smsLog = new SmsLog();
			smsLog.setMobileNumber(baseServiceImpl.customer.getCountry().getIsdCode() + baseServiceImpl.customer.getMobileNumber());
			smsLog.setMessageType(Constants.MESSAGE_TYPE_BANK_TO_WALLET);
			smsLog.setEncoding(1);
			smsLog.setCreatedDate(new Date());
			smsLog.setMessage(transferDirectDTO.getTransactionNO() + " Confirmed. You have received SSP "+ ((null != amount) ? new DecimalFormat("#0.00").format(amount.doubleValue()) : "0.00") +" from Bank Account: "+ cBankAccountNumber +" on "+new SimpleDateFormat("dd/MM/yyyy 'at' h:mm aa").format(Calendar.getInstance().getTime())+". New m-GURUSH balance is SSP "+new DecimalFormat("#0.00").format(transferDirectDTO.getCustomerAccount().getAccountBalance().doubleValue()));
			smsLog.setScheduledDate(new Date());
			smsLog.setStatus(0);
			baseServiceImpl.eotMobileDao.save(smsLog);			
			
			return fundTransferDTO;

		} /*
			 * catch (EOTCoreException Exception e) { e.printStackTrace();
			 * //System.out.println(e.getMessageKey()); //throw new
			 * EOTException(Integer.parseInt(e.getMessageKey()));
			 * 
			 * throw new EOTException(ErrorConstants.SERVICE_ERROR); }
			 */
		finally {

		}
	}
	}
