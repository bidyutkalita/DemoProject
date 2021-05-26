/* Copyright © EasOfTech 2015. All rights reserved.

 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: TransactionController.java,v 1.0
 *
 * Date Author Changes
 * 21 Oct, 2015, 2:57:58 PM Sambit Created
 */
package com.eot.banking.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.dto.BalanceEnquiryDTO;
import com.eot.banking.dto.BankTransactionDTO;
import com.eot.banking.dto.BillPaymentDTO;
import com.eot.banking.dto.ChequeEnquiryDTO;
import com.eot.banking.dto.CurrentBalance;
import com.eot.banking.dto.FundTransferDTO;
import com.eot.banking.dto.LastTxnReceiptDTO;
import com.eot.banking.dto.MinistatementDTO;
import com.eot.banking.dto.PendingTransactionDTO;
import com.eot.banking.dto.RemittanceDTO;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.dto.SMSCashDTO;
import com.eot.banking.dto.SaleDTO;
import com.eot.banking.dto.TopUpDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.OtherBankingService;
import com.eot.banking.service.TransactionService;
import com.eot.banking.utils.JSONAdaptor;
import com.eot.entity.Customer;

/**
 * The Class TransactionController.
 */
@Controller
@RequestMapping("/rest/txn/")
public class TransactionController {

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/** The transaction service. */
	@Autowired
	private TransactionService transactionService;
	/** The other banking service. */


	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

	/**
	 * Gets the balance.
	 * 
	 * @param balanceEnquiryDTO
	 *            the balance enquiry dto
	 * @return the balance
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "balanceEnquiry", method = RequestMethod.POST)
	public @ResponseBody BalanceEnquiryDTO getBalance(@RequestBody BalanceEnquiryDTO balanceEnquiryDTO) throws Exception {
		logger.info("************************ Inside getBalance *********************");
		try {
			balanceEnquiryDTO = transactionService.processBalanceEnquiryRequest(balanceEnquiryDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(balanceEnquiryDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(balanceEnquiryDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return Balance ***********************");
		return balanceEnquiryDTO;
	}

	/**
	 * Gets the ministatement.
	 * 
	 * @param ministatementDTO
	 *            the ministatement dto
	 * @return the ministatement
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "ministatment", method = RequestMethod.POST)
	public @ResponseBody MinistatementDTO getMinistatement(@RequestBody MinistatementDTO ministatementDTO) throws Exception {
		logger.info("************************ Inside getMinistatement *********************");
		try {
			ministatementDTO = transactionService.processMinistatementRequest(ministatementDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(ministatementDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(ministatementDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return getMinistatement ***********************");
		return ministatementDTO;
	}

	/**
	 * Do deposit.
	 *
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "deposit", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO doDeposit(@RequestBody TransactionBaseDTO transactionBaseDTO) throws Exception {
		logger.info("************************ Inside doDeposit *********************");
		try {

			transactionBaseDTO = transactionService.processDeposit(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doDeposit ***********************");
		return transactionBaseDTO;
	}

	/**
	 * Do withdrawal.
	 *
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "withdrawal", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO doWithdrawal(@RequestBody WithdrawalTransactionsDTO transactionBaseDTO) throws Exception {
		
		logger.info("************************ Inside doWithdrawal *********************");
		try {
			transactionBaseDTO = transactionService.processWithdrawal(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doWithdrawal ***********************");
		return transactionBaseDTO;
	}
	
	@RequestMapping(value = "customerwithdrawal", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO doCustomerWithdrawal(@RequestBody WithdrawalTransactionsDTO transactionBaseDTO) throws Exception {
		
		logger.info("************************ Inside customerwithdrawal *********************");
		try {
			transactionBaseDTO = transactionService.processWithdrawalCustomerInitiated(transactionBaseDTO);
			
			transactionBaseDTO.setStatus(0);
		} catch (EOTException e) {
			transactionBaseDTO.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
			transactionBaseDTO.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return customerwithdrawal ***********************");
		return transactionBaseDTO;
	}
	
	@RequestMapping(value = "transactionOtp", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO requestWithdrawal(@RequestBody TransactionBaseDTO transactionBaseDTO) throws Exception {
		logger.info("************************ Inside otp generation  *********************");
		try {
			transactionBaseDTO = transactionService.requestWithdrawal(transactionBaseDTO);
			transactionBaseDTO.setStatus(0);
		} catch (EOTException e) {
			transactionBaseDTO.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
			transactionBaseDTO.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return generation  ***********************");
		return transactionBaseDTO;
	}

	/**
	 * Validate fund transfer.
	 * 
	 * @param fundTransferDTO
	 *            the fund transfer dto
	 * @return the fund transfer dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "validateFundTransfer", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO validateFundTransfer(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		logger.info("************************ Inside validateFundTransfer **********************");
		try {
			fundTransferDTO = transactionService.validateTransferRequest(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return validateFundTransfer ***********************");
		return fundTransferDTO;
	}

	/**
	 * Do fund transfer.
	 * 
	 * @param fundTransferDTO
	 *            the fund transfer dto
	 * @return the fund transfer dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "fundTransfer", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO doFundTransfer(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		logger.info("************************ Inside doFundTransfer *********************");
		try {
			fundTransferDTO = transactionService.processTransferRequest(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doFundTransfer ***********************");
		return fundTransferDTO;
	}

	/**
	 * Show bill details.
	 * 
	 * @param billPaymentDTO
	 *            the bill payment dto
	 * @return the bill payment dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "billPresentment", method = RequestMethod.POST)
	public @ResponseBody BillPaymentDTO showBillDetails(@RequestBody BillPaymentDTO billPaymentDTO) throws Exception {
		logger.info("************************ Inside showBillDetails **********************");
		try {
			billPaymentDTO = transactionService.processBillPresentmentRequest(billPaymentDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(billPaymentDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(billPaymentDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return showBillDetails ***********************");
		return billPaymentDTO;
	}

	/**
	 * Do bill payment.
	 * 
	 * @param billPaymentDTO
	 *            the bill payment dto
	 * @return the bill payment dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "billPayment", method = RequestMethod.POST)
	public @ResponseBody BillPaymentDTO doBillPayment(@RequestBody BillPaymentDTO billPaymentDTO) throws Exception {
		logger.info("************************ Inside doBillPayment **********************");
		try {
			billPaymentDTO.setStatus(null);
			billPaymentDTO = transactionService.processBillPaymentRequest(billPaymentDTO);
		} catch (EOTException e) {
			billPaymentDTO.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(billPaymentDTO, e.getErrorCode());
		} catch (Exception ex) {
			billPaymentDTO.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(billPaymentDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doBillPayment ***********************");
		return billPaymentDTO;
	}

	/**
	 * Do top up.
	 * 
	 * @param topUpDTO
	 *            the top up dto
	 * @return the top up dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "topUp", method = RequestMethod.POST)
	public @ResponseBody TopUpDTO doTopUp(@RequestBody TopUpDTO topUpDTO) throws Exception {
		logger.info("************************ Inside doTopUp **********************");
		try {
			topUpDTO = transactionService.processTopUpRequest(topUpDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			topUpDTO.setErrorCode(e.getErrorCode() + "");
			getErrorResponse(topUpDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(topUpDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doTopUp ***********************");
		return topUpDTO;
	}

	/**
	 * Do sale.
	 * 
	 * @param saleDTO
	 *            the sale dto
	 * @return the sale dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "sale", method = RequestMethod.POST)
	public @ResponseBody SaleDTO doSale(@RequestBody SaleDTO saleDTO) throws Exception {
		logger.info("************************ Inside doSale **********************");
		try {
			saleDTO = transactionService.processSaleRequest(saleDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(saleDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(saleDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doSale ***********************");
		return saleDTO;
	}

	/**
	 * Do encash.
	 *
	 * @param saleDTO
	 *            the sale dto
	 * @return the sale dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "encash", method = RequestMethod.POST)
	public @ResponseBody SaleDTO doEncash(@RequestBody SaleDTO saleDTO) throws Exception {
		logger.info("************************ Inside doEncash **********************");
		try {
			saleDTO = transactionService.processEncashRequest(saleDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(saleDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(saleDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doEncash ***********************");
		return saleDTO;
	}

	/**
	 * Do sms cash.
	 * 
	 * @param smsCashDTO
	 *            the sms cash dto
	 * @return the sMS cash dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "smsCash", method = RequestMethod.POST)
	public @ResponseBody SMSCashDTO doSMSCash(@RequestBody SMSCashDTO smsCashDTO) throws Exception {
		logger.info("************************ Inside doSMSCash **********************");
		try {
			if(smsCashDTO.getPayeeMobileNumber()!=null){
				Customer baseDTO= transactionService.getSmsCashByNumber(smsCashDTO.getPayeeMobileNumber());
				 if(baseDTO==null){
					 smsCashDTO = transactionService.processSMSCashRequest(smsCashDTO);
				 }else{
					 smsCashDTO.setIsRegistered(EOTConstants.CUSTOMER_REGISTERED_SMS_CASH);
					 throw new EOTException(ErrorConstants.UNVALID_SMS_CASH);
				 }
			}
			
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(smsCashDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(smsCashDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doSMSCash ***********************");
		return smsCashDTO;
	}

	/**
	 * Show SMS cash details.
	 *
	 * @param smsCashDTO
	 *            the sms cash DTO
	 * @return the SMS cash DTO
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "showSMSCashDetails", method = RequestMethod.POST)
	public @ResponseBody SMSCashDTO showSMSCashDetails(@RequestBody SMSCashDTO smsCashDTO) throws Exception {
		logger.info("************************ Inside showSMSCashDetails **********************");
		try {
			smsCashDTO = transactionService.showSMSCashDetails(smsCashDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(smsCashDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(smsCashDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return showSMSCashDetails ***********************");
		return smsCashDTO;
	}

	/**
	 * Receive SMS cash.
	 *
	 * @param smsCashDTO
	 *            the sms cash DTO
	 * @return the SMS cash DTO
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "receiveSMSCash", method = RequestMethod.POST)
	public @ResponseBody SMSCashDTO receiveSMSCash(@RequestBody SMSCashDTO smsCashDTO) throws Exception {
		logger.info("************************ Inside receiveSMSCash **********************");
		try {
			smsCashDTO = transactionService.receiveSMSCash(smsCashDTO);
			System.out.println(smsCashDTO.getStatus());
			smsCashDTO.setStatus(null);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(smsCashDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(smsCashDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return receiveSMSCash ***********************");
		return smsCashDTO;
	}

	/**
	 * Gets the last txn receipt.
	 * 
	 * @param lastTxnReceiptDTO
	 *            the last txn receipt dto
	 * @return the last txn receipt
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "lastTxnReceipt", method = RequestMethod.POST)
	public @ResponseBody LastTxnReceiptDTO getLastTxnReceipt(@RequestBody LastTxnReceiptDTO lastTxnReceiptDTO) throws Exception {
		logger.info("************************ Inside getLastTxnReceipt **********************");
		try {
			lastTxnReceiptDTO = transactionService.processLastTransactionReceipt(lastTxnReceiptDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(lastTxnReceiptDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(lastTxnReceiptDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return getLastTxnReceipt ***********************");
		return lastTxnReceiptDTO;
	}

	/**
	 * Gets the cheque status.
	 * 
	 * @param chequeEnquiryDTO
	 *            the cheque enquiry dto
	 * @return the cheque status
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "chequeStatus", method = RequestMethod.POST)
	public @ResponseBody ChequeEnquiryDTO getChequeStatus(@RequestBody ChequeEnquiryDTO chequeEnquiryDTO) throws Exception {
		logger.info("************************ Inside getChequeStatus **********************");
		try {
			chequeEnquiryDTO = transactionService.processChequeEnquiry(chequeEnquiryDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(chequeEnquiryDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(chequeEnquiryDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return getChequeStatus ***********************");
		return chequeEnquiryDTO;
	}

	/**
	 * Send money.
	 *
	 * @param remittanceDTO
	 *            the remittance dto
	 * @return the remittance dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "sendMoney", method = RequestMethod.POST)
	public @ResponseBody RemittanceDTO sendMoney(@RequestBody RemittanceDTO remittanceDTO) throws Exception {
		logger.info("************************ Inside sendMoney **********************");
		try {
			remittanceDTO = transactionService.processSendMoney(remittanceDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(remittanceDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(remittanceDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return sendMoney ***********************");
		return remittanceDTO;
	}

	/**
	 * Receive money.
	 *
	 * @param remittanceDTO
	 *            the remittance dto
	 * @return the remittance dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "receiveMoney", method = RequestMethod.POST)
	public @ResponseBody RemittanceDTO receiveMoney(@RequestBody RemittanceDTO remittanceDTO) throws Exception {
		logger.info("************************ Inside receiveMoney **********************");
		try {
			remittanceDTO = transactionService.processReceiveMoney(remittanceDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(remittanceDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(remittanceDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return receiveMoney ***********************");
		return remittanceDTO;
	}

	/**
	 * Gets the error response.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @param errorCode
	 *            the error code
	 * @return the error response
	 */
	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO, Integer errorCode) {

		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_" + errorCode, null, new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));
		return transactionBaseDTO;
	}

//	@RequestMapping(value = "pendingTransactions", method = RequestMethod.POST)
//	public @ResponseBody PendingTransactionDTO getPendingTransactions(@RequestBody TransactionBaseDTO transactionBaseDTO) throws Exception {
//		logger.info("************************ Inside pendingTransactions **********************");
//		PendingTransactionDTO pendingTransactionDTO = new PendingTransactionDTO();
//		try {
//			// transactionBaseDTO =
//			// transactionService.getPendingTransactions(transactionBaseDTO);
//
//			/*
//			 * String payload = new JSONAdaptor().toJSON(transactionBaseDTO);
//			 * transactionBaseDTO = new JSONAdaptor().fromJSON(new
//			 * String(payload),TransactionBaseDTO.class); //
//			 * transactionBaseDTO.setApplicationId(applicationId); //
//			 * transactionBaseDTO.setEncPayload(payload);
//			 */ pendingTransactionDTO = transactionService.getPendingTransactions(transactionBaseDTO);
//		} catch (EOTException e) {
//			pendingTransactionDTO.setStatus(1);
//			e.printStackTrace();
//			// getErrorResponse(transactionBaseDTO, e.getErrorCode());
//		} catch (Exception ex) {
//			pendingTransactionDTO.setStatus(1);
//
//			ex.printStackTrace();
//			// getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
//		}
//
//		logger.info("************************* Return pendingTransactions ***********************");
//		// pendingTransactionDTO.setMessageDescription("List of Pending Transactions");
//		pendingTransactionDTO.setTransactionType(transactionBaseDTO.getTransactionType());
//		pendingTransactionDTO.setSuccessResponse("Success Response for Pending Transactions");
//		return pendingTransactionDTO;
//	}a

	
	@RequestMapping(value = "loadTxnForApprove", method = RequestMethod.POST)
	public @ResponseBody PendingTransactionDTO loadWithdrawalTransaction(@RequestBody PendingTransactionDTO withdrawalTransactions) throws Exception {
		logger.info("************************ sync Pending Transaction *********************");
		// WithdrawalTransactionsDTO withdrawalTransactions=
		try {
			List<PendingTransactionDTO> transactions = transactionService.loadPenndingTransaction(withdrawalTransactions);
			//withdrawalTransactions.setList(transactions);
			withdrawalTransactions.setList(transactions);
			withdrawalTransactions.setStatus(0);
		} catch (EOTException e) {
//			e.printStackTrace();
			loadErrorResponse(withdrawalTransactions, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			loadErrorResponse(withdrawalTransactions, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* sync Pending Transaction Response ***********************");
		withdrawalTransactions.setCustomer(null);
		return withdrawalTransactions;
	}

	
	@RequestMapping(value = "approveWithdrawalTransaction", method = RequestMethod.POST)
	public @ResponseBody PendingTransactionDTO approveWithdrawalTransaction(@RequestBody PendingTransactionDTO transactionBaseDTO) throws Exception {

		if (transactionBaseDTO.getIsApproved().equals(Constants.REQUEST_FOR_APPROVE)) {
			logger.info("************************ Approve pending Transaction *********************");
			try {
				transactionBaseDTO = transactionService.approvePendingTransaction(transactionBaseDTO);
				transactionBaseDTO.setStatus(0);
			} catch (EOTException e) {
//				e.printStackTrace();
				loadErrorResponse(transactionBaseDTO, e.getErrorCode());
			} catch (Exception ex) {
//				ex.printStackTrace();
				loadErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
			}
			logger.info("************************* Approve pending Response ***********************");
		} else {
			logger.info("************************ Reject pending Transaction *********************");
			try {
				transactionBaseDTO = transactionService.rejectPendingTransaction(transactionBaseDTO);
				transactionBaseDTO.setStatus(0);
			} catch (EOTException e) {
//				e.printStackTrace();
				loadErrorResponse(transactionBaseDTO, e.getErrorCode());
			} catch (Exception ex) {
//				ex.printStackTrace();
				loadErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
			}
			logger.info("************************* Reject Withdrawal Response ***********************");
		}
		return transactionBaseDTO;
	}
	
	
	@RequestMapping(value = "validatePay", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO validatePay(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		logger.info("************************ Inside validateFundTransfer **********************");
		try {
			fundTransferDTO = transactionService.validatePayRequest(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return validateFundTransfer ***********************");
		return fundTransferDTO;
	}
	
	@RequestMapping(value = "validateWithdrawal", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO validateWithdrawal(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		logger.info("************************ Inside validateWithdrawal **********************");
		int transactionType=fundTransferDTO.getTransactionType();
		System.out.println("transaction type====>>"+transactionType);
		try {
			
			fundTransferDTO = transactionService.validateWithdrawal(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return validateWithdrawal ***********************");
		fundTransferDTO.setTransactionType(transactionType);
		return fundTransferDTO;
	}

	/**
	 * Do fund transfer.
	 * 
	 * @param fundTransferDTO
	 *            the fund transfer dto
	 * @return the fund transfer dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "pay", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO doPay(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		logger.info("************************ Inside doFundTransfer *********************");
		try {
			fundTransferDTO = transactionService.processTransferRequest(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doFundTransfer ***********************");
		return fundTransferDTO;
	}



	private TransactionBaseDTO loadErrorResponse(TransactionBaseDTO transactionBaseDTO, Integer errorCode) {

		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_" + errorCode, null, new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));
		return transactionBaseDTO;
	}
	
	@RequestMapping(value = "loadTxnReversalForApprove", method = RequestMethod.POST)
	public @ResponseBody ReversalTransactionDTO loadTransactionReversalForApprove(@RequestBody ReversalTransactionDTO reversalTransactionDTO) throws Exception {
		logger.info("**##TransactionController##******************** loadTransactionReversalForApprove *********************");
		try {
			List<ReversalTransactionDTO> reversalTransactions = transactionService.loadTransactionReversalForApprove(reversalTransactionDTO);
			reversalTransactionDTO.setReversalTxnList(reversalTransactions);
			reversalTransactionDTO.setStatus(0);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(reversalTransactionDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(reversalTransactionDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("**##TransactionController##******************** loadTransactionReversalForApprove ***********************");
		return reversalTransactionDTO;
	}
	
	@RequestMapping(value = "approveReversalTxn", method = RequestMethod.POST)
	public @ResponseBody ReversalTransactionDTO approveReversalTxn(@RequestBody ReversalTransactionDTO reversalTransactionDTO) throws Exception {
		logger.info("**##TransactionController##******************** approveReversalTxn *********************");
		try {
			 transactionService.processApprovedReversalTxn(reversalTransactionDTO);
			 reversalTransactionDTO.setStatus(0);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(reversalTransactionDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(reversalTransactionDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("**##TransactionController##******************** approveReversalTxn ***********************");
		return reversalTransactionDTO;
	}
	

	
	@RequestMapping(value = "agentMoneyTransfer", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO performAgentMoneyTransfer(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		try {
			
			JSONAdaptor adaptor = new JSONAdaptor() ;
			String payload = adaptor.toJSON(fundTransferDTO);
			System.out.println(payload);
			fundTransferDTO = transactionService.processTransferRequest(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		return fundTransferDTO;
	}
	
	@RequestMapping(value = "quickBalance", method = RequestMethod.POST)
	public @ResponseBody CurrentBalance fetchBalance(@RequestBody CurrentBalance currentBalance) throws Exception {
		try {
			
			currentBalance = transactionService.fetchBalance(currentBalance);
			currentBalance.setStatus(0);
		} catch (EOTException e) {
			currentBalance.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(currentBalance, e.getErrorCode());
		} catch (Exception ex) {
			currentBalance.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(currentBalance, ErrorConstants.SERVICE_ERROR);
		}
		return currentBalance;
	}
	
	@RequestMapping(value = "ownTransfer", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO doOwnTransfer(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		logger.info("************************ Inside doFundTransfer *********************");
		try {
			fundTransferDTO = transactionService.ownTransferRequest(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doFundTransfer ***********************");
		return fundTransferDTO;
	}
	
	@RequestMapping(value = "doMarchentFloat", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO doMarchentFloat(@RequestBody FundTransferDTO fundTransferDTO) throws Exception {
		logger.info("************************ Inside doFundTransfer *********************");
		try {
			fundTransferDTO = transactionService.doMarchentFloat(fundTransferDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(fundTransferDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(fundTransferDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return doFundTransfer ***********************");
		return fundTransferDTO;
	}
	
	@RequestMapping(value = "bankToWallet", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO bankToWallet(@RequestBody FundTransferDTO transactionDto) throws Exception {
		logger.info("************************ Inside bankToWallet *********************");
		try {
			
			transactionDto=transactionService.processBankToWallet(transactionDto);
		} catch (EOTException e) {
			e.printStackTrace();
			getErrorResponse(transactionDto, e.getErrorCode());
		} catch (Exception ex) {
			ex.printStackTrace();
			getErrorResponse(transactionDto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return bankToWallet ***********************");
		return transactionDto;
	}
	
	@RequestMapping(value = "walletToBank", method = RequestMethod.POST)
	public @ResponseBody FundTransferDTO walletToBank(@RequestBody FundTransferDTO transactionDto) throws Exception {
		logger.info("************************ Inside walletToBank *********************");
		try {
			transactionDto=transactionService.processWalletToBank(transactionDto);
		} catch (EOTException e) {
			e.printStackTrace();
			getErrorResponse(transactionDto, e.getErrorCode());
		} catch (Exception ex) {
			ex.printStackTrace();
			getErrorResponse(transactionDto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return walletToBank ***********************");
		return transactionDto;
	}
	
	@RequestMapping(value = "agentCashOut", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO doAgentCashOut(@RequestBody WithdrawalTransactionsDTO transactionBaseDTO) throws Exception {
		
		logger.info("************************ Inside agent cashOut *********************");
		try {
			transactionBaseDTO = transactionService.processAgentCashOut(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return agent cashOut ***********************");
		return transactionBaseDTO;
	}
	
	@RequestMapping(value = "merchantCashOut", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO doMerchantCashOut(@RequestBody WithdrawalTransactionsDTO transactionBaseDTO) throws Exception {
		
		logger.info("************************ Inside agent cashOut *********************");
		int transactionType= transactionBaseDTO.getTransactionType();
		transactionBaseDTO.setTransactionType(140);//140 merchant payout txn type
		try {
			transactionBaseDTO = transactionService.processAgentCashOut(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return agent cashOut ***********************");
		transactionBaseDTO.setTransactionType(transactionType);
		return transactionBaseDTO;
	}
	

	@RequestMapping(value = "loadPayOutTxn", method = RequestMethod.POST)
	public @ResponseBody WithdrawalTransactionsDTO loadPayoutTxn(@RequestBody WithdrawalTransactionsDTO transactionBaseDTO) throws Exception {
		logger.info("************************ load pay out  *********************");
		try {
			transactionBaseDTO = transactionService.loadPendingTxnForMerchant(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return pay out  ***********************");
		return transactionBaseDTO;
	}
	@RequestMapping(value = "doReversal", method=RequestMethod.POST)
	public @ResponseBody ReversalTransactionDTO doReversalTransaction(@RequestBody ReversalTransactionDTO reversalTransactionDTO){

		
		try{
			reversalTransactionDTO = transactionService.processReversalTransaction(reversalTransactionDTO);
			
			
			
		}catch(EOTException e){
//			e.printStackTrace();
			getErrorResponse(reversalTransactionDTO, e.getErrorCode());
		}catch(Exception e){
//			e.printStackTrace();
			getErrorResponse(reversalTransactionDTO, ErrorConstants.SERVICE_ERROR);
		}	   
		return reversalTransactionDTO;
	}
	
	@RequestMapping(value = "merchantPayout", method = RequestMethod.POST)
	public @ResponseBody WithdrawalTransactionsDTO merchantPayout(@RequestBody WithdrawalTransactionsDTO transactionBaseDTO) throws Exception {
		
		int transactionType= transactionBaseDTO.getTransactionType();
		transactionBaseDTO.setTransactionType(140);//140 merchant payout txn type
		if (transactionBaseDTO.getIsApproved().equals(Constants.REQUEST_FOR_APPROVE)) {
		logger.info("************************ Inside merchantPayout *********************");
		try {
			transactionBaseDTO = transactionService.processMerchantPayout(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return merchantPayout ***********************");
		
	}
	else
	{

		logger.info("************************ Reject pending Transaction *********************");
		try {
			transactionBaseDTO = transactionService.rejectMerchantPayout(transactionBaseDTO);
			transactionBaseDTO.setStatus(0);
		} catch (EOTException e) {
//			e.printStackTrace();
			loadErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			loadErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Reject Withdrawal Response ***********************");
	
		
	}
		transactionBaseDTO.setTransactionType(transactionType);
		return transactionBaseDTO;
	}
	
}

