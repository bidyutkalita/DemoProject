/* Copyright � EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: EOTMobileDao.java,v 1.0
*
* Date Author Changes
* 17 Nov, 2015, 1:17:52 PM Sambit Created
*/
package com.eot.banking.daos;

import java.util.ArrayList;
import java.util.List;

import com.eot.banking.controller.CustomerModel;
import com.eot.banking.daos.base.BaseDao;
import com.eot.banking.dto.CurrentBalance;
import com.eot.banking.dto.FAQsModelDTO.FAQ;
import com.eot.banking.dto.LocateUsDTO;
import com.eot.banking.dto.MobileMenuMasterDataDTO;
import com.eot.banking.dto.PendingTransactionDTO;
import com.eot.banking.dto.ReportsModel;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.server.data.OtpDTO;
import com.eot.entity.Account;
import com.eot.entity.AppMaster;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.BillerTypes;
import com.eot.entity.Branch;
import com.eot.entity.BusinessPartner;
import com.eot.entity.BusinessPartnerUser;
import com.eot.entity.City;
import com.eot.entity.ClearingHousePool;
import com.eot.entity.ClearingHousePoolMember;
import com.eot.entity.Country;
import com.eot.entity.Currency;
import com.eot.entity.CurrencyConverter;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.CustomerDocument;
import com.eot.entity.CustomerProfiles;
import com.eot.entity.ElectricityBill;
import com.eot.entity.ExchangeRate;
import com.eot.entity.ExternalTransaction;
import com.eot.entity.IncorrectKycDetails;
import com.eot.entity.KycType;
import com.eot.entity.Language;
import com.eot.entity.LocateUS;
import com.eot.entity.LocationType;
import com.eot.entity.MobileMenuConfiguration;
import com.eot.entity.MobileMenuLanguage;
import com.eot.entity.MobileRequest;
import com.eot.entity.Mobiletheamcolorconfig;
import com.eot.entity.NetworkType;
import com.eot.entity.Operator;
import com.eot.entity.Otp;
import com.eot.entity.Payee;
import com.eot.entity.PendingTransaction;
import com.eot.entity.Quarter;
import com.eot.entity.RemittanceCompany;
import com.eot.entity.RemittanceTransaction;
import com.eot.entity.SecurityQuestion;
import com.eot.entity.SenelecBills;
import com.eot.entity.Transaction;
import com.eot.entity.TransactionType;
import com.eot.entity.VersionDetails;
import com.eot.entity.WebUser;

// TODO: Auto-generated Javadoc
/**
 * The Interface EOTMobileDao.
 */
public interface EOTMobileDao extends BaseDao {

	/**
	 * Gets the banks.
	 * 
	 * @return the banks
	 */
	List<Bank> getBanks();

	/**
	 * Gets the customer.
	 * 
	 * @param applicationId
	 *            the application id
	 * @return the customer
	 */
	Customer getCustomer(String applicationId);

	/**
	 * Gets the customer from mobile no.
	 * 
	 * @param mobileNumber
	 *            the mobile number
	 * @return the customer from mobile no
	 */
	Customer getCustomerFromMobileNo(String mobileNumber);

	/**
	 * Gets the customer card for confirmation.
	 * 
	 * @param customerId
	 *            the customer id
	 * @param cardAlias
	 *            the card alias
	 * @return the customer card for confirmation
	 */
	CustomerCard getCustomerCardForConfirmation(Long customerId, String cardAlias);

	/**
	 * Gets the customer card from alias.
	 * 
	 * @param customerId
	 *            the customer id
	 * @param cardAlias
	 *            the card alias
	 * @return the customer card from alias
	 */
	CustomerCard getCustomerCardFromAlias(Long customerId, String cardAlias);

	/**
	 * Gets the customer accounts.
	 * 
	 * @param customerId
	 *            the customer id
	 * @return the customer accounts
	 */
	List<CustomerAccount> getCustomerAccounts(Long customerId);

	/**
	 * Gets the card details.
	 * 
	 * @param customerId
	 *            the customer id
	 * @return the card details
	 */
	List<CustomerCard> getCardDetails(Long customerId);

	/**
	 * Gets the payee account from mobile no.
	 * 
	 * @param mobileNo
	 *            the mobile no
	 * @return the payee account from mobile no
	 */
	CustomerAccount getPayeeAccountFromMobileNo(String mobileNo);

	/**
	 * Gets the account from account alias.
	 * 
	 * @param customerId
	 *            the customer id
	 * @param accountAlias
	 *            the account alias
	 * @return the account from account alias
	 */
	CustomerAccount getAccountFromAccountAlias(Long customerId, String accountAlias);

	/**
	 * Gets the cutomer account from mobile no.
	 * 
	 * @param mobileNo
	 *            the mobile no
	 * @param accountAlias
	 *            the account alias
	 * @return the cutomer account from mobile no
	 */
	CustomerAccount getCutomerAccountFromMobileNo(String mobileNo, String accountAlias);

	/**
	 * Gets the request.
	 * 
	 * @param applicationId
	 *            the application id
	 * @param stan
	 *            the stan
	 * @param rrn
	 *            the rrn
	 * @param transmissionTime
	 *            the transmission time
	 * @return the request
	 */
	MobileRequest getRequest(String applicationId, Long stan, Long rrn, String transmissionTime);

	/**
	 * Gets the application type.
	 * 
	 * @param applicationId
	 *            the application id
	 * @return the application type
	 */
	AppMaster getApplicationType(String applicationId);

	/**
	 * Gets the operator list.
	 * 
	 * @param countryId
	 *            the country id
	 * @return the operator list
	 */
	List<Operator> getOperatorList(Integer countryId);

	/**
	 * Gets the clearing house.
	 * 
	 * @param customerBank
	 *            the customer bank
	 * @param otherPartyBank
	 *            the other party bank
	 * @return the clearing house
	 */
	List<ClearingHousePoolMember> getClearingHouse(Integer customerBank, Integer otherPartyBank);

	/**
	 * Gets the clearing house.
	 * 
	 * @param clearingHouseId
	 *            the clearing house id
	 * @return the clearing house
	 */
	ClearingHousePool getClearingHouse(Integer clearingHouseId);

	/**
	 * Verify otp.
	 * 
	 * @param otpDTO
	 *            the otp dto
	 * @return the otp
	 */
	Otp verifyOTP(OtpDTO otpDTO);

	/**
	 * Find operator.
	 * 
	 * @param operatorId
	 *            the operator id
	 * @return the operator
	 */
	Operator findOperator(Long operatorId);

	/**
	 * Gets the electricity bill.
	 * 
	 * @param billerCustomerId
	 *            the biller customer id
	 * @return the electricity bill
	 */
	ElectricityBill getElectricityBill(String billerCustomerId);

	/**
	 * Gets the electricity bill from policy id.
	 * 
	 * @param policyNumber
	 *            the policy number
	 * @return the electricity bill from policy id
	 */
	SenelecBills getElectricityBillFromPolicyId(String policyNumber);

	/**
	 * Gets the customer card by card number.
	 * 
	 * @param cardNumber
	 *            the card number
	 * @return the customer card by card number
	 */
	CustomerCard getCustomerCardByCardNumber(String cardNumber);

	/**
	 * Gets the biller.
	 * 
	 * @param billerId
	 *            the biller id
	 * @return the biller
	 */
	Biller getBiller(Integer billerId);

	/**
	 * Gets the all country.
	 * 
	 * @return the all country
	 */
	List<Country> getAllCountry();

	/**
	 * Gets the biller list.
	 * 
	 * @param countryId
	 *            the country id
	 * @return the biller list
	 */
	List<Biller> getBillerList(Integer countryId);

	/**
	 * Last successful transaction.
	 * 
	 * @param applicationId
	 *            the application id
	 * @param txnTypeOfLastTransaction
	 *            the txn type of last transaction
	 * @return the list
	 */
	List<MobileRequest> lastSuccessfulTransaction(String applicationId, Integer txnTypeOfLastTransaction);

	/**
	 * Gets the virtual cardfor bank.
	 * 
	 * @param bankId
	 *            the bank id
	 * @return the virtual cardfor bank
	 */
	CustomerCard getVirtualCardforBank(String bankId);

	/**
	 * Gets the application version.
	 * 
	 * @param versionNumber
	 *            the version number
	 * @return the application version
	 */
	VersionDetails getApplicationVersion(String versionNumber);

	/**
	 * Gets the bank account from account alias.
	 * 
	 * @param customerId
	 *            the customer id
	 * @param accountAlias
	 *            the account alias
	 * @return the bank account from account alias
	 */
	CustomerBankAccount getBankAccountFromAccountAlias(Long customerId, String accountAlias);

	/**
	 * Gets the customer bank account from account number.
	 * 
	 * @param accountNumber
	 *            the account number
	 * @return the customer bank account from account number
	 */
	CustomerBankAccount getCustomerBankAccountFromAccountNumber(String accountNumber);

	/**
	 * Gets the branchs.
	 * 
	 * @return the branchs
	 */
	List<Branch> getBranchs();

	/**
	 * Gets the bank account details.
	 * 
	 * @param customerId
	 *            the customer id
	 * @return the bank account details
	 */
	List<CustomerBankAccount> getBankAccountDetails(Long customerId);

	/**
	 * Gets the bank from bank id.
	 * 
	 * @param bankId
	 *            the bank id
	 * @return the bank from bank id
	 */
	Bank getBankFromBankId(Integer bankId);

	/**
	 * Gets the branch from branch id.
	 * 
	 * @param branchId
	 *            the branch id
	 * @return the branch from branch id
	 */
	Branch getBranchFromBranchId(Long branchId);

	/**
	 * Gets the mobile request.
	 * 
	 * @param requestID
	 *            the request id
	 * @return the mobile request
	 */
	MobileRequest getMobileRequest(Long requestID);

	/**
	 * Gets the biller type list.
	 * 
	 * @return the biller type list
	 */
	List<BillerTypes> getBillerTypeList();

	/**
	 * Gets the payee from alias.
	 *
	 * @param customerId
	 *            the customer id
	 * @param payeeAlias
	 *            the payee alias
	 * @return the payee from alias
	 */
	Payee getPayeeFromAlias(Long customerId, String payeeAlias);

	/**
	 * Gets the customer by mobile number.
	 *
	 * @param mobileNumber
	 *            the mobile number
	 * @return the customer by mobile number
	 */
	Customer getCustomerByMobileNumber(String mobileNumber);

	/**
	 * Gets the country.
	 *
	 * @param countryId
	 *            the country id
	 * @return the country
	 */
	com.eot.entity.Country getCountry(Integer countryId);

	/**
	 * Gets the next account number sequence.
	 *
	 * @return the next account number sequence
	 */
	Long getNextAccountNumberSequence();

	/**
	 * Gets the customer account from alias.
	 *
	 * @param customerId
	 *            the customer id
	 * @param accountNumber
	 *            the account number
	 * @return the customer account from alias
	 */
	CustomerAccount getCustomerAccountFromAlias(Long customerId, String accountNumber);

	/**
	 * Gets the payee list.
	 *
	 * @param customerId
	 *            the customer id
	 * @return the payee list
	 */
	List<Payee> getPayeeList(Long customerId);

	/**
	 * Gets the all currency.
	 *
	 * @return the all currency
	 */
	List<Currency> getAllCurrency();

	/**
	 * Gets the exchange rate from currecy id and bank id.
	 *
	 * @param currencyId
	 *            the currency id
	 * @param bankId
	 *            the bank id
	 * @return the exchange rate from currecy id and bank id
	 */
	ExchangeRate getExchangeRateFromCurrecyIdAndBankId(Integer currencyId, Integer bankId);

	/**
	 * Gets the service location.
	 *
	 * @param locateUsDTO
	 *            the locate us dto
	 * @return the service location
	 */
	List<LocateUS> getServiceLocation(LocateUsDTO locateUsDTO);

	/**
	 * Gets the all active location type.
	 *
	 * @param locale
	 *            the locale
	 * @param status
	 *            the status
	 * @return the all active location type
	 */
	List<LocationType> getAllActiveLocationType(String locale, Integer status);

	/**
	 * Gets the all city.
	 *
	 * @return the all city
	 */
	List<City> getAllCity();

	/**
	 * Gets the all quarter.
	 *
	 * @return the all quarter
	 */
	List<Quarter> getAllQuarter();

	/**
	 * Gets the all net work type.
	 *
	 * @return the all net work type
	 */
	List<NetworkType> getAllNetWorkType();

	/**
	 * Gets the branch list.
	 *
	 * @param locateUsDTO
	 *            the locate us DTO
	 * @return the branch list
	 */
	List<com.eot.entity.Branch> getBranchList(LocateUsDTO locateUsDTO);

	/**
	 * Gets the conversion rate from currecy ids and bank id.
	 *
	 * @param baseCurrencyId
	 *            the base currency id
	 * @param counterCurrencyId
	 *            the counter currency id
	 * @param bankId
	 *            the bank id
	 * @return the conversion rate from currecy ids and bank id
	 */
	CurrencyConverter getConversionRateFromCurrecyIdsAndBankId(Integer baseCurrencyId, Integer counterCurrencyId,
			Integer bankId);

	/**
	 * Gets the all remittance company.
	 *
	 * @return the all remittance company
	 */
	List<RemittanceCompany> getAllRemittanceCompany();

	/**
	 * Gets the customer account from account number.
	 *
	 * @param accountNumber
	 *            the account number
	 * @return the customer account from account number
	 */
	CustomerAccount getCustomerAccountFromAccountNumber(String accountNumber);

	/**
	 * Gets the SC rules.
	 *
	 * @return the SC rules
	 */
	List<Object[]> getSCRules(Integer sourceType);

	/**
	 * Gets the all language data.
	 *
	 * @return the all language data
	 */
	List<Language> getAllLanguageData();

	/**
	 * Gets the country from country code.
	 *
	 * @param countryCode
	 *            the country code
	 * @return the country from country code
	 */
	com.eot.entity.Country getCountryFromCountryCode(Integer countryCode);

	/**
	 * Gets the account from customer id.
	 *
	 * @param customerId
	 *            the customer id
	 * @return the account from customer id
	 */
	CustomerAccount getAccountFromCustomerId(Long customerId);

	/**
	 * Gets the external txn from beneficiary mobile number.
	 *
	 * @param payeeMobileNumber
	 *            the payee mobile number
	 * @return the external txn from beneficiary mobile number
	 */
	List<ExternalTransaction> getExternalTxnsFromBeneficiaryMobileNumber(String payeeMobileNumber);

	/**
	 * Gets the external txns from id.
	 *
	 * @param smsCashId
	 *            the sms cash id
	 * @return the external txns from id
	 */
	ExternalTransaction getExternalTxnsFromId(Long smsCashId);

	List<MobileMenuConfiguration> loadMobileMenus(MobileMenuMasterDataDTO mobileMenuDTO);

	List<MobileMenuLanguage> loadMenuLanguagesByFCNCode(List<MobileMenuConfiguration> mobileMenuConf);

	Mobiletheamcolorconfig loadMobileTheameColors(MobileMenuMasterDataDTO mobileMenuDTO);

	Long getStampFeeDebitTransaction(Integer bankId);

	Customer getCustomerByCustomerId(String customerId);

	City getCityFromCityId(Integer cityId);

	Quarter getQuarterFromQuarterId(Long quarterId);

	Long getBranchId(Integer bankId, String branchId);
	
	List<Biller> getBillerList();
	
	public Bank getBankIdFromBankCode(String bankCode);

	List<Operator> getOperatorListByBankCode(String cbsBankCode);

	CustomerProfiles getCustomerProfile(Integer bankId,Integer customerType);
	
	List<Country> getCountries();
	
	List<KycType> getKycType();

	BusinessPartnerUser getUser(String name);
	WebUser getWebUser(String name);
	
	Customer getCustomerByMobile(String mobile);
	
	Customer getCustomerByAppId(String appId);

	CustomerAccount getCustomerAccountWithCustomerId(Long customerId);

	ArrayList<PendingTransactionDTO.TransactionDTO> getPendingTransactions(TransactionBaseDTO transactionBaseDTO, String accountNumber);
	List<PendingTransaction> loadPendingTransactions(PendingTransactionDTO transaction);

	PendingTransaction loadPendingTransaction(PendingTransactionDTO transaction);

	Customer getCustomerFromCustomerId(Long customerId);

	TransactionType getLoadTransactionType(Integer transactionType);

	List<SecurityQuestion> getQuestions(String locale);


	PendingTransaction loadSaleTransaction(PendingTransactionDTO transaction);

	CustomerProfiles getSelfRegisterProfile();

	List<Object[]> getRecentRecipeints(Long customerId);

	CustomerAccount getPayeeAccountFromAgentCode(String agentCode);

	Customer getAgentByAgentCode(String agentCode);
	
	public String getAgentCode(Integer type);

	Otp getOtp(String referenceId);

	List<ReversalTransactionDTO> loadTransactionReversalForApprove(ReversalTransactionDTO reversalTransactionDTO);

	Transaction getTransactionByTxnId(Integer transactionId);

	Account getAccount(String accountNumber);

	ArrayList<com.eot.entity.HelpDesk> getAllHelpDeskList();

	/**
	 * Gets the business partner user.
	 *
	 * @param userName the user name
	 * @return the business partner user
	 */
	BusinessPartnerUser getBusinessPartnerUser(String userName);
	
	WebUser getUserByMobileNumber(String mobileNumber);

	Otp verifyOTPWithAmount(OtpDTO otpDTO);
	
	List<Object[]> getTransactions(Integer bankId,ReportsModel baseDTO);

	List<CustomerAccount> getAccountListFromCustomerId(Long customerId);

	CustomerAccount getAgentPoolAccountFromAgentId(Long customerId);

	CustomerDocument loadCustomerDocumentByCustomerId(Long customerId);

	CurrentBalance getCount(CurrentBalance currentBalance,String onBoardedBy);
	
	List<Customer> getCustomerReports(Integer type,CustomerModel customerModel,String onBoardedBy);

	ArrayList<FAQ> getFAQsList();
	
	List<Object[]> getTransactionsReports(String accountNumber,Integer bankId,ReportsModel baseDTO,Integer type);

	List<KycType> getKycTypeForCustomerType(int customerType);

	IncorrectKycDetails getIncorrectKycByMobileNumber(String mobileNumber);

	List<String> getGeographyLocationOfAgents(LocateUsDTO locateUsDTO);

	PendingTransaction loadPendingTxn(Customer customer, int transactionTypeID);

	CustomerAccount getCommissionAccountFromAgentId(Long customerId);

	
	List<ReversalTransactionDTO> getTransactionforReversal(WithdrawalTransactionsDTO withdrawalTransactionsDTO);


	PendingTransaction loadPendingRecord(String parameter, int transactionTypeID);

	RemittanceTransaction getRemitanceTxnByRefTxnId(String refTxnNumber);

	ExternalTransaction getExternalTransaction(String payeeMobileNumber);

	BusinessPartner getBusinessPartnerByPartnerType(int partnerType);

	CustomerBankAccount getCustomerBankAccountByID(String customrId);
	
	List<Customer> getAllAgents(LocateUsDTO locateUsDTO);
	
	/*SMSCashDTO getSmsCashByNumber(String payeenumber);*/

}
