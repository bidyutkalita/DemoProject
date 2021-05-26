/* Copyright � EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: OtherBankingService.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:47 PM Sambit Created
*/
package com.eot.banking.service;

import java.util.List;

import com.eot.banking.controller.CustomerModel;
import com.eot.banking.dto.AccountMaintenanceDTO;
import com.eot.banking.dto.CardMaintenanceDTO;
import com.eot.banking.dto.ChangePINDTO;
import com.eot.banking.dto.ConfirmPinDTO;
import com.eot.banking.dto.CurrencyConverterDTO;
import com.eot.banking.dto.CustomerProfileDTO;
import com.eot.banking.dto.ExchangeRateDTO;
import com.eot.banking.dto.LocateUsDTO;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.MasterDataSelfOnboard;
import com.eot.banking.dto.NumericCodeDTO;
import com.eot.banking.dto.OTPDTO;
import com.eot.banking.dto.PayeeDTO;
import com.eot.banking.dto.RecentRecipeintsDTO;
import com.eot.banking.dto.ReportsModel;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.dto.SaleDTO;
import com.eot.banking.dto.ServiceChargeDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.UploadCustomerDocument;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.exception.EOTException;
import com.eot.entity.HelpDesk;

/**
 * The Interface OtherBankingService.
 */
public interface OtherBankingService {

	/**
	 * Process change pin request.
	 * 
	 * @param changePINDTO
	 *            the change pindto
	 * @return the change pindto
	 * @throws EOTException
	 *             the eOT exception
	 */
	ChangePINDTO processChangePINRequest(ChangePINDTO changePINDTO) throws EOTException;

	/**
	 * Process change txn pin request.
	 * 
	 * @param changePINDTO
	 *            the change pindto
	 * @return the change pindto
	 * @throws EOTException
	 *             the eOT exception
	 */
	ChangePINDTO processChangeTxnPINRequest(ChangePINDTO changePINDTO) throws EOTException;

	/**
	 * Process add card request.
	 * 
	 * @param cardMaintenanceDTO
	 *            the card maintenance dto
	 * @return the card maintenance dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	CardMaintenanceDTO processAddCardRequest(CardMaintenanceDTO cardMaintenanceDTO) throws EOTException;

	/**
	 * Process confirm card request.
	 * 
	 * @param cardMaintenanceDTO
	 *            the card maintenance dto
	 * @return the card maintenance dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	CardMaintenanceDTO processConfirmCardRequest(CardMaintenanceDTO cardMaintenanceDTO) throws EOTException;

	/**
	 * Process delete card request.
	 * 
	 * @param cardMaintenanceDTO
	 *            the card maintenance dto
	 * @return the card maintenance dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	CardMaintenanceDTO processDeleteCardRequest(CardMaintenanceDTO cardMaintenanceDTO) throws EOTException;

	/**
	 * Process add bank account request.
	 * 
	 * @param accountMaintenanceDTO
	 *            the account maintenance dto
	 * @return the account maintenance dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	AccountMaintenanceDTO processAddBankAccountRequest(AccountMaintenanceDTO accountMaintenanceDTO) throws EOTException;

	/**
	 * Process delete bank account request.
	 * 
	 * @param accountMaintenanceDTO
	 *            the account maintenance dto
	 * @return the account maintenance dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	AccountMaintenanceDTO processDeleteBankAccountRequest(AccountMaintenanceDTO accountMaintenanceDTO)
			throws EOTException;

	/**
	 * Process update profile request.
	 * 
	 * @param masterDataDTO
	 *            the master data dto
	 * @return the master data dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	MasterDataDTO processUpdateProfileRequest(MasterDataDTO masterDataDTO) throws EOTException;

	/**
	 * Process reset login pin request.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	TransactionBaseDTO processResetLoginPINRequest(TransactionBaseDTO transactionBaseDTO) throws EOTException;

	/**
	 * Process reset txn pin request.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	TransactionBaseDTO processResetTxnPINRequest(TransactionBaseDTO transactionBaseDTO) throws EOTException;

	/**
	 * Process set default account.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	TransactionBaseDTO processSetDefaultAccount(TransactionBaseDTO transactionBaseDTO) throws EOTException;

	/**
	 * Process get customer account details.
	 *
	 * @param saleDTO
	 *            the sale dto
	 * @return the master data dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	MasterDataDTO processGetCustomerAccountDetails(SaleDTO saleDTO) throws EOTException;

	/**
	 * Process send otp.
	 *
	 * @param otpdto
	 *            the otpdto
	 * @return the otpdto
	 * @throws EOTException
	 *             the eOT exception
	 */
	OTPDTO processSendOTP(OTPDTO otpdto) throws EOTException;

	/**
	 * Process verify otp.
	 *
	 * @param otpdto
	 *            the otpdto
	 * @return the otpdto
	 * @throws EOTException
	 *             the eOT exception
	 */
	OTPDTO processVerifyOTP(OTPDTO otpdto) throws EOTException;

	/**
	 * Fetch payee info.
	 *
	 * @param payeeDTO
	 *            the payee dto
	 * @return the payee dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	PayeeDTO fetchPayeeInfo(PayeeDTO payeeDTO) throws EOTException;

	/**
	 * Process add payee.
	 *
	 * @param payeeDTO
	 *            the payee dto
	 * @return the payee dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	PayeeDTO processAddPayee(PayeeDTO payeeDTO) throws EOTException;

	/**
	 * Process delete payee.
	 *
	 * @param payeeDTO
	 *            the payee dto
	 * @return the payee dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	PayeeDTO processDeletePayee(PayeeDTO payeeDTO) throws EOTException;

	/**
	 * Creates the cutomer profile.
	 *
	 * @param customerProfileDTO
	 *            the customer profile dto
	 * @return the customer profile dto
	 * @throws Exception
	 *             the exception
	 */
	CustomerProfileDTO createCutomerProfile(CustomerProfileDTO customerProfileDTO) throws Exception;

	/**
	 * Gets the exchange rate.
	 *
	 * @param exchangeRateDTO
	 *            the exchange rate dto
	 * @return the exchange rate
	 * @throws EOTException
	 *             the eOT exception
	 */
	void getExchangeRate(ExchangeRateDTO exchangeRateDTO) throws EOTException;

	/**
	 * Gets the location.
	 *
	 * @param locateUsDTO
	 *            the locate us dto
	 * @return the location
	 * @throws EOTException
	 *             the eOT exception
	 */
	void getLocation(LocateUsDTO locateUsDTO) throws EOTException;

	/**
	 * Convert currency.
	 *
	 * @param currencyConverterDTO
	 *            the currency converter DTO
	 * @throws EOTException
	 *             the EOT exception
	 */

	void convertCurrency(CurrencyConverterDTO currencyConverterDTO) throws EOTException;

	public NumericCodeDTO getCountryByBankId(Integer bankId) throws Exception;

	public NumericCodeDTO getCountryByCountryId(Integer countryId) throws Exception;

	public NumericCodeDTO getCityByCityId(Integer cityId) throws Exception;

	public NumericCodeDTO getQuarterByQuarterId(Long quarterId) throws Exception;
	
	public NumericCodeDTO getCountryCurrencyNumericCode(String bankCode) throws Exception;

	boolean validateBranch(MasterDataDTO masterDataDTO) throws Exception;
	
	public ServiceChargeDTO getServiceCharge(ServiceChargeDTO masterDataDTO) throws EOTException;
	
	public MasterDataDTO getServiceChargeCalculater(MasterDataDTO masterDataDTO) throws EOTException;

	Integer getBankIdByBankCode(MasterDataDTO masterDataDTO) throws Exception;
	
	CustomerProfileDTO customerSelfOnboarding(CustomerProfileDTO customerDTO) throws Exception;
	
	MasterDataSelfOnboard fetchSelfOnboardMasterData() throws EOTException;

	List<RecentRecipeintsDTO> getRecentReciepnts(RecentRecipeintsDTO recentRecipeintsDTO);

	CustomerProfileDTO updateKYC(CustomerProfileDTO customerDTO) throws Exception;

	CustomerProfileDTO fetchCustomer(CustomerProfileDTO customerDTO) throws Exception;

	TransactionBaseDTO processForgetPin(TransactionBaseDTO transactionBaseDTO) throws EOTException,Exception;

	ConfirmPinDTO processConfirmPin(ConfirmPinDTO confirmPinDTO) throws EOTException,Exception;
	
	 OTPDTO sendOtpForRegistration( OTPDTO otpDTO) throws EOTException,Exception;
	 
	 ReportsModel txnReports(ReportsModel transactionBaseDTO) throws EOTException,Exception;

	UploadCustomerDocument updoadDocument(UploadCustomerDocument uploadCustomerDocument) throws EOTException;
	
	CustomerModel CustomerReports(CustomerModel customerReportDTO)  throws EOTException,Exception;

	List<HelpDesk> getHelpDeskDetails();

	List<ReversalTransactionDTO> getTransactionforReversal(WithdrawalTransactionsDTO withdrawalTransactionsDTO) throws EOTException;

}
