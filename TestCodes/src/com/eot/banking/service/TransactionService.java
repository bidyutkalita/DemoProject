/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: TransactionService.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:53 PM Sambit Created
*/
package com.eot.banking.service;

import java.util.List;

import com.eot.banking.dto.BalanceEnquiryDTO;
import com.eot.banking.dto.BankTransactionDTO;
import com.eot.banking.dto.BillPaymentDTO;
import com.eot.banking.dto.ChequeEnquiryDTO;
import com.eot.banking.dto.CurrentBalance;
import com.eot.banking.dto.FundTransferDTO;
import com.eot.banking.dto.LastTxnReceiptDTO;
import com.eot.banking.dto.MinistatementDTO;
import com.eot.banking.dto.RemittanceDTO;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.dto.SMSCashDTO;
import com.eot.banking.dto.SaleDTO;
import com.eot.banking.dto.TopUpDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.dto.PendingTransactionDTO;
import com.eot.banking.exception.EOTException;
import com.eot.entity.Customer;

// TODO: Auto-generated Javadoc
/**
 * The Interface TransactionService.
 */
public interface TransactionService {

	/**
	 * Process balance enquiry request.
	 * 
	 * @param balanceEnquiryDTO
	 *            the balance enquiry dto
	 * @return the balance enquiry dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	BalanceEnquiryDTO processBalanceEnquiryRequest( BalanceEnquiryDTO balanceEnquiryDTO ) throws EOTException;
	
	/**
	 * Process ministatement request.
	 * 
	 * @param ministatementDTO
	 *            the ministatement dto
	 * @return the ministatement dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	MinistatementDTO processMinistatementRequest( MinistatementDTO ministatementDTO ) throws EOTException;
	
	/**
	 * Validate transfer request.
	 * 
	 * @param fundTransferDTO
	 *            the fund transfer dto
	 * @return the fund transfer dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	FundTransferDTO validateTransferRequest( FundTransferDTO fundTransferDTO ) throws EOTException;
	
	/**
	 * Process transfer request.
	 * 
	 * @param fundTransferDTO
	 *            the fund transfer dto
	 * @return the fund transfer dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	FundTransferDTO processTransferRequest( FundTransferDTO fundTransferDTO ) throws EOTException;
	
	/**
	 * Process bill presentment request.
	 * 
	 * @param billPaymentDTO
	 *            the bill payment dto
	 * @return the bill payment dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	BillPaymentDTO processBillPresentmentRequest(BillPaymentDTO billPaymentDTO) throws EOTException;
	
	/**
	 * Process bill payment request.
	 * 
	 * @param billPaymentDTO
	 *            the bill payment dto
	 * @return the bill payment dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	BillPaymentDTO processBillPaymentRequest(BillPaymentDTO billPaymentDTO) throws EOTException;
	
	/**
	 * Process top up request.
	 * 
	 * @param topUpDTO
	 *            the top up dto
	 * @return the top up dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	TopUpDTO processTopUpRequest(TopUpDTO topUpDTO) throws EOTException;
	
	/**
	 * Process cheque enquiry.
	 * 
	 * @param chequeEnquiryDTO
	 *            the cheque enquiry dto
	 * @return the cheque enquiry dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	ChequeEnquiryDTO processChequeEnquiry(ChequeEnquiryDTO chequeEnquiryDTO) throws EOTException;
	
	/**
	 * Process sale request.
	 * 
	 * @param saleDTO
	 *            the sale dto
	 * @return the sale dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	SaleDTO processSaleRequest(SaleDTO saleDTO) throws EOTException;
	
	/**
	 * Process sms cash request.
	 * 
	 * @param smsCashDTO
	 *            the sms cash dto
	 * @return the sMS cash dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	SMSCashDTO processSMSCashRequest(SMSCashDTO smsCashDTO) throws EOTException,Exception;
	
	/**
	 * Process last transaction receipt.
	 * 
	 * @param lastTxnReceiptDTO
	 *            the last txn receipt dto
	 * @return the last txn receipt dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	LastTxnReceiptDTO processLastTransactionReceipt(LastTxnReceiptDTO lastTxnReceiptDTO) throws EOTException;

	/**
	 * Process deposit.
	 *
	 * @param transactionBaseDTO the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException the eOT exception
	 */
	TransactionBaseDTO processDeposit(TransactionBaseDTO transactionBaseDTO) throws EOTException;

	/**
	 * Process withdrawal.
	 *
	 * @param transactionBaseDTO the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException the eOT exception
	 */
	WithdrawalTransactionsDTO processWithdrawal(WithdrawalTransactionsDTO transactionBaseDTO) throws EOTException;
	
	/**
	 * Process encash request.
	 *
	 * @param dto the dto
	 * @return the sale dto
	 * @throws EOTException the eOT exception
	 */
	SaleDTO processEncashRequest(SaleDTO dto) throws EOTException;

	/**
	 * Process send money.
	 *
	 * @param remittanceDTO the remittance dto
	 * @return the remittance dto
	 * @throws EOTException the EOT exception
	 */
	RemittanceDTO processSendMoney(RemittanceDTO remittanceDTO)throws EOTException;

	/**
	 * Process receive money.
	 *
	 * @param remittanceDTO the remittance dto
	 * @return the remittance dto
	 * @throws EOTException the EOT exception
	 */
	RemittanceDTO processReceiveMoney(RemittanceDTO remittanceDTO)throws EOTException;
	
	/**
	 * Show SMS cash details.
	 *
	 * @param smsCashDTO the sms cash DTO
	 * @return the SMS cash DTO
	 * @throws EOTException the EOT exception
	 */
	SMSCashDTO showSMSCashDetails(SMSCashDTO smsCashDTO) throws EOTException;
	
	/**
	 * Receive SMS cash.
	 *
	 * @param smsCashDTO the sms cash DTO
	 * @return the SMS cash DTO
	 * @throws EOTException the EOT exception
	 */
	SMSCashDTO receiveSMSCash(SMSCashDTO smsCashDTO) throws EOTException;

	PendingTransactionDTO getPendingTransactions(TransactionBaseDTO transactionBaseDTO) throws EOTException;

	PendingTransactionDTO approvePendingTransaction(PendingTransactionDTO pendingTransactionDTO) throws EOTException;

	PendingTransactionDTO rejectPendingTransaction(PendingTransactionDTO transactionBaseDTO) throws EOTException;

	List<PendingTransactionDTO> loadPenndingTransaction(PendingTransactionDTO transaction) throws EOTException;

	FundTransferDTO validatePayRequest(FundTransferDTO fundTransferDTO) throws EOTException;

	List<ReversalTransactionDTO> loadTransactionReversalForApprove(ReversalTransactionDTO reversalTransactionDTO) throws EOTException;

	ReversalTransactionDTO processApprovedReversalTxn(ReversalTransactionDTO reversalTransactionDTO) throws EOTException;
	
	Customer getSmsCashByNumber(String payeemobile);

	TransactionBaseDTO requestWithdrawal(TransactionBaseDTO transactionBaseDTO) throws EOTException;

	TransactionBaseDTO processMerchantPayout(TransactionBaseDTO transactionBaseDTO) throws EOTException;

	CurrentBalance fetchBalance(CurrentBalance currentBalance) throws EOTException;

	FundTransferDTO ownTransferRequest(FundTransferDTO fundTransferDTO) throws EOTException;

	WithdrawalTransactionsDTO processWithdrawalCustomerInitiated(WithdrawalTransactionsDTO transactionBaseDTO)
			throws EOTException;

	FundTransferDTO validateWithdrawal(FundTransferDTO fundTransferDTO) throws EOTException;
	
	FundTransferDTO doMarchentFloat(FundTransferDTO fundTransferDTO) throws EOTException;

	BankTransactionDTO walletToBankTransfer(BankTransactionDTO transactionDTO) throws EOTException;

	WithdrawalTransactionsDTO processAgentCashOut(WithdrawalTransactionsDTO transactionBaseDTO) throws EOTException;

	WithdrawalTransactionsDTO loadPendingTxnForMerchant(WithdrawalTransactionsDTO transaction) throws EOTException;

	ReversalTransactionDTO processReversalTransaction(ReversalTransactionDTO reversalTransactionDTO) throws EOTException;

	WithdrawalTransactionsDTO processMerchantPayout(WithdrawalTransactionsDTO transactionBaseDTO) throws EOTException;

	WithdrawalTransactionsDTO rejectMerchantPayout(WithdrawalTransactionsDTO transactionBaseDTO) throws EOTException;

	FundTransferDTO processWalletToBank(FundTransferDTO fundTransferDTO) throws EOTException;

	FundTransferDTO processBankToWallet(FundTransferDTO fundTransferDTO) throws EOTException;
	
}
