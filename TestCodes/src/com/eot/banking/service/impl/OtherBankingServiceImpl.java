/* Copyright � EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: OtherBankingServiceImpl.java,v 1.0
 *
 * Date Author Changes
 * 3 Nov, 2015, 1:24:06 PM Sambit Created
 */
package com.eot.banking.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.eot.banking.common.CoreUrls;
import com.eot.banking.common.EOTConstants;
import com.eot.banking.common.TitleEnum;
import com.eot.banking.common.UrlConstants;
import com.eot.banking.controller.CustomerModel;
import com.eot.banking.daos.ServiceChargeDao;
import com.eot.banking.dto.AccountMaintenanceDTO;
import com.eot.banking.dto.CardMaintenanceDTO;
import com.eot.banking.dto.ChangePINDTO;
import com.eot.banking.dto.CityDTO;
import com.eot.banking.dto.ConfirmPinDTO;
import com.eot.banking.dto.CountryDTO;
import com.eot.banking.dto.CurrencyConverterDTO;
import com.eot.banking.dto.CustomerProfileDTO;
import com.eot.banking.dto.ExchangeRateDTO;
import com.eot.banking.dto.KycTypeDTO;
import com.eot.banking.dto.LocateUsDTO;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.MasterDataSelfOnboard;
import com.eot.banking.dto.MobileMenuMasterDataDTO;
import com.eot.banking.dto.NumericCodeDTO;
import com.eot.banking.dto.OTPDTO;
import com.eot.banking.dto.PayeeDTO;
import com.eot.banking.dto.QuarterDTO;
import com.eot.banking.dto.RecentRecipeintsDTO;
import com.eot.banking.dto.ReportsModel;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.dto.SCRuleDTO;
import com.eot.banking.dto.SaleDTO;
import com.eot.banking.dto.SecurityQuestionDTO;
import com.eot.banking.dto.ServiceChargeDTO;
import com.eot.banking.dto.SmsResponseDTO;
import com.eot.banking.dto.TitleDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.UploadCustomerDocument;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.server.data.OtpDTO;
import com.eot.banking.service.MobileDynamicMenuService;
import com.eot.banking.service.OtherBankingService;
import com.eot.banking.utils.AppConfigurations;
import com.eot.banking.utils.DateUtil;
import com.eot.banking.utils.EOTUtil;
import com.eot.banking.utils.FileUtil;
import com.eot.banking.utils.HashUtil;
import com.eot.banking.utils.JSONAdaptor;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.sms.AppLinkAlertDTO;
import com.eot.dtos.sms.InitialTxnPinLoginPinAlertDTO;
import com.eot.dtos.sms.NewLoginPinAlertDTO;
import com.eot.dtos.sms.NewTxnPinAlertDTO;
import com.eot.dtos.sms.SmsHeader;
import com.eot.dtos.sms.WebOTPAlertDTO;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.Account;
import com.eot.entity.AppMaster;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.BillerTypes;
import com.eot.entity.Branch;
import com.eot.entity.BusinessPartner;
import com.eot.entity.City;
import com.eot.entity.Country;
import com.eot.entity.CountryNames;
import com.eot.entity.Currency;
import com.eot.entity.CurrencyConverter;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.CustomerDocument;
import com.eot.entity.CustomerProfiles;
import com.eot.entity.ExchangeRate;
import com.eot.entity.HelpDesk;
import com.eot.entity.IncorrectKycDetails;
import com.eot.entity.KycType;
import com.eot.entity.Language;
import com.eot.entity.LocateUS;
import com.eot.entity.LocationType;
import com.eot.entity.MobileRequest;
import com.eot.entity.NetworkType;
import com.eot.entity.Operator;
import com.eot.entity.OperatorDenomination;
import com.eot.entity.Otp;
import com.eot.entity.Payee;
import com.eot.entity.Quarter;
import com.eot.entity.SecurityQuestion;
import com.eot.entity.Transaction;
import com.eot.entity.TransactionType;
import com.security.kms.security.KMSSecurityException;
import com.sun.jndi.cosnaming.IiopUrl.Address;
import com.thinkways.util.HexString;

import oracle.core.lmx.CoreException;

/**
 * The Class OtherBankingServiceImpl.
 */
@Service
public class OtherBankingServiceImpl implements OtherBankingService{

	/** The base service impl. */
	@Autowired
	private BaseServiceImpl baseServiceImpl;

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;

	/** The sms service client stub. */
//	@Autowired
//	private SmsServiceClientStub smsServiceClientStub ;

	/** The otp for sale enabled. */
	private boolean otpForSaleEnabled = false ;

	/** The app configurations. */
	@Autowired
	private AppConfigurations appConfigurations;
	
	@Autowired
	private MobileDynamicMenuService mobileDynamicMenuService;
	
	@Autowired
	private ServiceChargeDao serviceChargeDao;
	
	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Sets the otp for sale enabled.
	 *
	 * @param otpForSaleEnabled the new otp for sale enabled
	 */
	public void setOtpForSaleEnabled(boolean otpForSaleEnabled) {
		this.otpForSaleEnabled = otpForSaleEnabled;
	}

	/**
	 * Process change pin request.
	 *
	 * @param changePINDTO the change pindto
	 * @return the change pindto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processChangePINRequest(com.eot.banking.dto.ChangePINDTO)
	 */
	public ChangePINDTO processChangePINRequest(ChangePINDTO changePINDTO) throws EOTException {
		System.out.println("***************** ChangePinHandler *************");
		baseServiceImpl.handleRequest(changePINDTO);
		String oldPin = changePINDTO.getActivationPIN();
		String newPin = changePINDTO.getNewPIN();
		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(changePINDTO.getApplicationId());
		if(customer==null){
			throw new EOTException(5011);
		}
		if(  !oldPin.equals(customer.getLoginPin()) ){
			throw new EOTException(ErrorConstants.INVALID_USER_PIN);
		}
		if(oldPin.equals(newPin) ){
			throw new EOTException("New pin cannot be same as old pin");
		}
		/*if(baseServiceImpl.appMaster.getStatus() == Constants.APP_STATUS_ACTIVATION_SC_DEBITED){
			baseServiceImpl.appMaster.setStatus(Constants.APP_STATUS_ACTIVATED);
			baseServiceImpl.eotMobileDao.update(baseServiceImpl.appMaster);
		}*/
		if(baseServiceImpl.appMaster.getStatus() == Constants.APP_STATUS_ACTIVATION_SC_DEBITED_temp){
			baseServiceImpl.appMaster.setStatus(Constants.APP_STATUS_ACTIVATION_SC_DEBITED_temp);
			baseServiceImpl.eotMobileDao.update(baseServiceImpl.appMaster);
		}
		customer.setActive(Constants.CHANGE_PIN_REQ);
		customer.setLoginPin(newPin);
		baseServiceImpl.eotMobileDao.update(customer);
		changePINDTO.setSuccessResponse("Success");
		return changePINDTO;
	}

	/**
	 * Process change txn pin request.
	 *
	 * @param changePINDTO the change pindto
	 * @return the change pindto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processChangeTxnPINRequest(com.eot.banking.dto.ChangePINDTO)
	 */
	public ChangePINDTO processChangeTxnPINRequest(ChangePINDTO changePINDTO) throws EOTException {

		System.out.println("******** ChangePinHandler *************");

		baseServiceImpl.handleRequest(changePINDTO);
		String oldTxnPin = changePINDTO.getTransactionPIN();
		String newTxnPin = changePINDTO.getNewTxnPIN();
		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(changePINDTO.getApplicationId());
		/*if( ! oldTxnPin.equals( baseServiceImpl.customer.getTransactionPin()) ){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}*/
		if( ! oldTxnPin.equals( customer.getTransactionPin()) ){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		if(baseServiceImpl.appMaster.getStatus() == Constants.APP_STATUS_ACTIVATION_SC_DEBITED){
			baseServiceImpl.appMaster.setStatus(Constants.APP_STATUS_ACTIVATED);
			baseServiceImpl.eotMobileDao.update(baseServiceImpl.appMaster);
		}

	//	List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(baseServiceImpl.customer.getCustomerId());
		List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		CustomerAccount account = accountList.get(0) ;

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
	//	serviceChargeDebitDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(baseServiceImpl.referenceType);
		serviceChargeDebitDTO.setRequestID(baseServiceImpl.requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(changePINDTO.getTransactionType().toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			//utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
			serviceChargeDebitDTO=processRequest(CoreUrls.SERVICE_CHARGE_DEBIT_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
			txn.setRequestChannel(EOTConstants.REQUEST_CHANNEL_MOBILE);
			baseServiceImpl.mobileRequest.setTransaction(txn);

		}finally {
			
		}/* catch (EOTCoreException e) {
			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}*/

		/*baseServiceImpl.customer.setActive(Constants.CHANGE_TXN_PIN_REQ);
		baseServiceImpl.customer.setTransactionPin(newTxnPin);*/
		customer.setActive(Constants.CHANGE_TXN_PIN_REQ);
		customer.setTransactionPin(newTxnPin);
		baseServiceImpl.eotMobileDao.update(customer);
		
		if (baseServiceImpl.appMaster.getStatus() == Constants.APP_STATUS_DOWNLOADED) {
			baseServiceImpl.appMaster.setStatus(Constants.APP_STATUS_ACTIVATION_SC_DEBITED);
			baseServiceImpl.eotMobileDao.update(baseServiceImpl.appMaster);
		}

		changePINDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CHANGE_TXN_PIN_SUCCESS", null, new Locale(baseServiceImpl.defaultLocale.toString())));

		return changePINDTO;

	}

	/**
	 * Process add card request.
	 *
	 * @param cardMaintenanceDTO the card maintenance dto
	 * @return the card maintenance dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processAddCardRequest(com.eot.banking.dto.CardMaintenanceDTO)
	 */
	public CardMaintenanceDTO processAddCardRequest( CardMaintenanceDTO cardMaintenanceDTO ) throws EOTException {

		System.out.println("******** AddCardHandler *************");

		baseServiceImpl.handleRequest(cardMaintenanceDTO);
		String cardAlias = cardMaintenanceDTO.getCardAlias();
		String cardNo = cardMaintenanceDTO.getCardNumber();
		String cvv = cardMaintenanceDTO.getCvv().toString();
		String dateOfExpiry = cardMaintenanceDTO.getExpiryDate();

		CustomerCard card=null;
		try {
			card = baseServiceImpl.eotMobileDao.getCustomerCardByCardNumber(HexString.bufferToHex(baseServiceImpl.kmsHandler.externalDbDesOperation(cardNo.getBytes(),true) ));
		} catch (KMSSecurityException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

		if(card != null){
			throw new EOTException(ErrorConstants.MOBILE_ID_EXIST);
		}

		card = new CustomerCard();

		if(dateOfExpiry.length()==4){

			dateOfExpiry = dateOfExpiry.substring(2, 4)+dateOfExpiry.substring(0, 2);

		}

		card.setAlias(cardAlias);
		try {
			card.setCardNumber(HexString.bufferToHex(baseServiceImpl.kmsHandler.externalDbDesOperation(cardNo.getBytes(),true) ));
			card.setCvv(HexString.bufferToHex(baseServiceImpl.kmsHandler.externalDbDesOperation(cvv.getBytes(),true) ));
			card.setCardExpiry(HexString.bufferToHex(baseServiceImpl.kmsHandler.externalDbDesOperation(dateOfExpiry.getBytes(),true) ));
		} catch (KMSSecurityException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
		card.setReferenceId(baseServiceImpl.customer.getCustomerId()+"");
		card.setReferenceType(0);
		card.setStatus(Constants.CARD_STATUS_NOT_CONFIRMED);
		CustomerAccount customerAccount = (CustomerAccount) baseServiceImpl.customer.getCustomerAccounts().iterator().next();
		card.setBank(customerAccount.getBank());

		Integer confirmCode = EOTUtil.generateConfirmationCode();
		card.setNameOnCard(confirmCode+"");

		baseServiceImpl.eotMobileDao.save(card);

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(card.getCardNumber());
		accountDto.setBankCode(card.getBank().getBankId().toString());
		accountDto.setAccountType(Constants.ALIAS_TYPE_CARD_ACC+"");

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(baseServiceImpl.referenceType);
		serviceChargeDebitDTO.setRequestID(baseServiceImpl.requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(cardMaintenanceDTO.getTransactionType().toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			//serviceChargeDebitDTO =	utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);

			serviceChargeDebitDTO=processRequest(CoreUrls.SERVICE_CHARGE_DEBIT_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
		} /*catch (EOTCoreException e) {
			e.printStackTrace();
			baseServiceImpl.eotMobileDao.delete(card);
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}*/finally {
			
		}

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(cardMaintenanceDTO.getTransactionType());
		baseServiceImpl.mobileRequest.setTransactionType(transactionType);

		//		try {
		//			AddCardAlertDTO dto = new AddCardAlertDTO() ;
		//			dto.setCardNumber(cardNo);
		//			dto.setConfirmationCode(confirmCode+"");
		//			dto.setLocale(baseServiceImpl.customer.getDefaultLanguage());
		//			dto.setMobileNo(serviceChargeDebitDTO.getChannelType());
		//			dto.setScheduleDate(Calendar.getInstance());
		//
		//			smsServiceClientStub.addCardAlert(dto);
		//
		//		} catch (Exception e) {
		//			throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
		//		}

		cardMaintenanceDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("ADD_CARD_SUCCESS", null, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return cardMaintenanceDTO;
	}

	/**
	 * Process confirm card request.
	 *
	 * @param cardMaintenanceDTO the card maintenance dto
	 * @return the card maintenance dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processConfirmCardRequest(com.eot.banking.dto.CardMaintenanceDTO)
	 */
	public CardMaintenanceDTO processConfirmCardRequest( CardMaintenanceDTO cardMaintenanceDTO ) throws EOTException {

		baseServiceImpl.handleRequest(cardMaintenanceDTO);
		String cardAlias = cardMaintenanceDTO.getCardAlias();
		String confirmationCode = cardMaintenanceDTO.getConfirmationCode();

		CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardForConfirmation(baseServiceImpl.customer.getCustomerId(), cardAlias);

		if(card == null){
			throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
		}

		if(!card.getNameOnCard().equalsIgnoreCase(confirmationCode)){
			throw new EOTException(ErrorConstants.INVALID_CONFIMATION_CODE);
		}

		card.setStatus(Constants.CARD_STATUS_CONFIRMED);

		baseServiceImpl.eotMobileDao.update(card);

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(cardMaintenanceDTO.getTransactionType());
		baseServiceImpl.mobileRequest.setTransactionType(transactionType);

		cardMaintenanceDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CONFIRM_CARD_SUCCESS", null, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return cardMaintenanceDTO;
	}

	/**
	 * Process delete card request.
	 *
	 * @param cardMaintenanceDTO the card maintenance dto
	 * @return the card maintenance dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processDeleteCardRequest(com.eot.banking.dto.CardMaintenanceDTO)
	 */
	public CardMaintenanceDTO processDeleteCardRequest( CardMaintenanceDTO cardMaintenanceDTO ) throws EOTException {

		baseServiceImpl.handleRequest(cardMaintenanceDTO);
		String cardAlias = cardMaintenanceDTO.getCardAlias();

		CustomerCard card = baseServiceImpl.eotMobileDao.getCustomerCardFromAlias(baseServiceImpl.customer.getCustomerId(), cardAlias);

		if(card == null){
			throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
		}

		card.setStatus(Constants.CARD_STATUS_DELETED);

		baseServiceImpl.eotMobileDao.update(card);

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(cardMaintenanceDTO.getTransactionType());
		baseServiceImpl.mobileRequest.setTransactionType(transactionType);

		cardMaintenanceDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("DELETE_CARD_SUCCESS", null, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return cardMaintenanceDTO;
	}

	/**
	 * Process add bank account request.
	 *
	 * @param accountMaintenanceDTO the account maintenance dto
	 * @return the account maintenance dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processAddBankAccountRequest(com.eot.banking.dto.AccountMaintenanceDTO)
	 */
	public AccountMaintenanceDTO processAddBankAccountRequest( AccountMaintenanceDTO accountMaintenanceDTO ) throws EOTException {

		System.out.println("******** Add Bank Account Service *************");

		baseServiceImpl.handleRequest(accountMaintenanceDTO);
		String accountAlias = accountMaintenanceDTO.getBankAccountAlias();
		String accountNumber = accountMaintenanceDTO.getBankAccountNumber();

		if(baseServiceImpl.customer.getBankCustomerId()== null){
			throw new EOTException(ErrorConstants.INVALID_BANK_CUSTOMER_ID);
		}

		CustomerBankAccount customerBankAccount = baseServiceImpl.eotMobileDao.getCustomerBankAccountFromAccountNumber(accountNumber);

		if(customerBankAccount != null){
			throw new EOTException(ErrorConstants.ACCOUNT_NUMBER_EXIST);
		}

		customerBankAccount = new CustomerBankAccount();
		customerBankAccount.setAlias(accountAlias);
		customerBankAccount.setReferenceId(baseServiceImpl.customer.getCustomerId()+"");
		customerBankAccount.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		customerBankAccount.setAccountHolderName(baseServiceImpl.customer.getFirstName());
		customerBankAccount.setStatus(Constants.ACTIVE);
		customerBankAccount.setBankAccountNumber(accountNumber);
		CustomerAccount customerAccount = (CustomerAccount) baseServiceImpl.customer.getCustomerAccounts().iterator().next();
	//	customerBankAccount.setBank(customerAccount.getBank());
		customerBankAccount.setCreatedDate(new Date());
		customerBankAccount.setUpdatedDate(new Date());

		baseServiceImpl.eotMobileDao.save(customerBankAccount);

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(customerAccount.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(customerAccount.getBank().getBankId().toString());
		accountDto.setBranchCode(customerAccount.getBranch().getBranchId().toString());

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(baseServiceImpl.referenceType);
		serviceChargeDebitDTO.setRequestID(baseServiceImpl.requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(accountMaintenanceDTO.getTransactionType().toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			///utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
			
			serviceChargeDebitDTO=processRequest(CoreUrls.SERVICE_CHARGE_DEBIT_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));

			baseServiceImpl.mobileRequest.setTransaction(txn);

		} finally {
			
		}/*catch (EOTCoreException e) {
			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}*/

		accountMaintenanceDTO.setSuccessResponse(
				baseServiceImpl.messageSource.getMessage("ADD_BANK_ACCOUNT_SUCCESS", null, 
						new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return accountMaintenanceDTO;
	}

	/**
	 * Process delete bank account request.
	 *
	 * @param accountMaintenanceDTO the account maintenance dto
	 * @return the account maintenance dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processDeleteBankAccountRequest(com.eot.banking.dto.AccountMaintenanceDTO)
	 */
	public AccountMaintenanceDTO processDeleteBankAccountRequest( AccountMaintenanceDTO accountMaintenanceDTO ) throws EOTException {

		baseServiceImpl.handleRequest(accountMaintenanceDTO);
		CustomerBankAccount customerBankAccount = baseServiceImpl.eotMobileDao.getBankAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), accountMaintenanceDTO.getBankAccountAlias());

		if(customerBankAccount == null){
			throw new EOTException(ErrorConstants.ACCOUNT_NUMBER_EXIST);
		}
		customerBankAccount.setStatus(Constants.INACTIVE);
		baseServiceImpl.eotMobileDao.update(customerBankAccount);

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(accountMaintenanceDTO.getTransactionType());
		baseServiceImpl.mobileRequest.setTransactionType(transactionType);

		accountMaintenanceDTO.setSuccessResponse(
				baseServiceImpl.messageSource.getMessage("DELETE_BANK_ACCOUNT_SUCCESS", null, 
						new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return accountMaintenanceDTO;

	}

	/**
	 * Process update profile request.
	 *
	 * @param masterDataDTO the master data dto
	 * @return the master data dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processUpdateProfileRequest(com.eot.banking.dto.MasterDataDTO)
	 */
	public MasterDataDTO processUpdateProfileRequest(MasterDataDTO masterDataDTO) throws EOTException {

		System.out.println("******** ProfileUpdateHandler *************");

		baseServiceImpl.handleRequest(masterDataDTO);
		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(masterDataDTO.getApplicationId());
	/*	List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(baseServiceImpl.customer.getCustomerId());
		List<Payee> payeeList = baseServiceImpl.eotMobileDao.getPayeeList(baseServiceImpl.customer.getCustomerId());
		List<CustomerCard> cardList = baseServiceImpl.eotMobileDao.getCardDetails(baseServiceImpl.customer.getCustomerId());*/
		
		List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(customer.getCustomerId());
		List<Payee> payeeList = baseServiceImpl.eotMobileDao.getPayeeList(customer.getCustomerId());
		List<CustomerCard> cardList = baseServiceImpl.eotMobileDao.getCardDetails(customer.getCustomerId());
		
		List<Bank> bankList = baseServiceImpl.eotMobileDao.getBanks();
		List<Branch> branchList = baseServiceImpl.eotMobileDao.getBranchs();
//		List<Operator> operatorList = baseServiceImpl.eotMobileDao.getOperatorList(baseServiceImpl.customer.getCountry().getCountryId());
		//List<Operator> operatorList = baseServiceImpl.eotMobileDao.getOperatorList(customer.getCountry().getCountryId());
		
		String bankCode=accountList.get(0).getBank().getBankCode();
		List<Operator> operatorList=baseServiceImpl.eotMobileDao.getOperatorListByBankCode(bankCode);
		
		
		List<Country> countryList = baseServiceImpl.eotMobileDao.getAllCountry();
		List<Currency> currencyList = baseServiceImpl.eotMobileDao.getAllCurrency();
		List<BillerTypes> billerTypeList = baseServiceImpl.eotMobileDao.getBillerTypeList();
//		List<Biller> billerList = baseServiceImpl.eotMobileDao.getBillerList(baseServiceImpl.customer.getCountry().getCountryId());
//		List<CustomerBankAccount> customerBankAccountList = baseServiceImpl.eotMobileDao.getBankAccountDetails(baseServiceImpl.customer.getCustomerId());
		
		List<Biller> billerList = baseServiceImpl.eotMobileDao.getBillerList(customer.getCountry().getCountryId());
		List<CustomerBankAccount> customerBankAccountList = baseServiceImpl.eotMobileDao.getBankAccountDetails(customer.getCustomerId());
		List<NetworkType> networkTypes = baseServiceImpl.eotMobileDao.getAllNetWorkType();
	/*	List<LocationType> locationTypeList = baseServiceImpl.eotMobileDao.getAllActiveLocationType(baseServiceImpl.customer.getDefaultLanguage().substring(0, 2), Constants.ACTIVE_STATUS);*/
		List<LocationType> locationTypeList = baseServiceImpl.eotMobileDao.getAllActiveLocationType(customer.getDefaultLanguage().substring(0, 2), Constants.ACTIVE_STATUS);
		MobileMenuMasterDataDTO mobileMenuMasterDto=mobileDynamicMenuService.loadMobileMenu(masterDataDTO);
		Map theamConfigMap=mobileDynamicMenuService.loadTheamColorConfig(masterDataDTO);
		List<Language> languages = baseServiceImpl.eotMobileDao.getAllLanguageData();
		List<SecurityQuestion> securityQuestionModelList=baseServiceImpl.eotMobileDao.getQuestions(StringUtils.isNotEmpty(masterDataDTO.getDefaultLocale()) ? masterDataDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);
	//	ArrayList<HelpDesk> helpdesks = baseServiceImpl.eotMobileDao.getAllHelpDeskList();
		List<HelpDesk> helpdesks = getHelpDeskDetails();
		List<KycType> kycTypeList=baseServiceImpl.eotMobileDao.getKycTypeForCustomerType(customer.getType());
		if(!bankList.isEmpty())
		{
			masterDataDTO.setDefaultbankId(bankList.get(0).getBankCode());
		}
//		return packResponse( masterDataDTO, baseServiceImpl.customer, accountList, payeeList, cardList, bankList, branchList, operatorList, countryList, networkTypes, locationTypeList, currencyList, billerTypeList, billerList, customerBankAccountList,mobileMenuMasterDto,theamConfigMap);
		return packResponse( masterDataDTO, customer, accountList, payeeList, cardList, bankList, branchList, operatorList, countryList, networkTypes, locationTypeList, currencyList, billerTypeList, billerList, customerBankAccountList,mobileMenuMasterDto,theamConfigMap,languages,securityQuestionModelList,helpdesks,kycTypeList);
	}

	/**
	 * Process reset login pin request.
	 *
	 * @param transactionBaseDTO the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processResetLoginPINRequest(com.eot.banking.dto.TransactionBaseDTO)
	 */
	public TransactionBaseDTO processResetLoginPINRequest( TransactionBaseDTO transactionBaseDTO ) throws EOTException {

		System.out.println("******** Reset Pin Service *************");

		String applicationId = transactionBaseDTO.getApplicationId();
		Integer transactionType = transactionBaseDTO.getTransactionType();

		baseServiceImpl.appMaster = baseServiceImpl.eotMobileDao.getApplicationType(applicationId);

		if (baseServiceImpl.appMaster == null){
			throw new EOTException(ErrorConstants.INVALID_APPLICATION);
		}

		baseServiceImpl.referenceType  = Integer.valueOf(baseServiceImpl.appMaster.getReferenceType());

		if(baseServiceImpl.appMaster.getReferenceType()== Constants.REF_TYPE_CUSTOMER || 
				baseServiceImpl.appMaster.getReferenceType()== Constants.REF_TYPE_MERCHANT ){  

			baseServiceImpl.customer = baseServiceImpl.eotMobileDao.getCustomer(applicationId);

			if(baseServiceImpl.customer == null){
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
			}
			if( baseServiceImpl.customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED  ){
				throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
			}

			List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(baseServiceImpl.customer.getCustomerId());

			if(accountList.size() == 0 ){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			if(accountList.get(0).getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
				throw new EOTException(ErrorConstants.INACTIVE_BANK);
			}

		}

		baseServiceImpl.mobileRequest = new MobileRequest();  

		TransactionType txnType = new TransactionType();
		txnType.setTransactionType(transactionType);
		baseServiceImpl.mobileRequest.setTransactionType(txnType);
		baseServiceImpl.mobileRequest.setTransactionTime(new Date());
		baseServiceImpl.mobileRequest.setAppMaster(baseServiceImpl.appMaster);
		baseServiceImpl.mobileRequest.setReferenceId(baseServiceImpl.appMaster.getReferenceId());
		baseServiceImpl.mobileRequest.setReferenceType(baseServiceImpl.appMaster.getReferenceType());
		baseServiceImpl.mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_LOGGED);

		baseServiceImpl.eotMobileDao.save(baseServiceImpl.mobileRequest);  // Log Mobile Request

		baseServiceImpl.requestID = baseServiceImpl.mobileRequest.getRequestId();

		if( Constants.APP_STATUS_RESET_PIN_VERIFIED != baseServiceImpl.appMaster.getStatus() && Constants.APP_STATUS_RESET_PIN_SC_DEBITED != baseServiceImpl.appMaster.getStatus()){
			throw new EOTException(ErrorConstants.VERIFY_CUSTOMER_RESET_TXN_PIN);
		}

		List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(baseServiceImpl.customer.getCustomerId());

		if(accountList.size() == 0 ){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		CustomerAccount account = accountList.get(0) ;

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		if( Constants.APP_STATUS_RESET_PIN_SC_DEBITED != baseServiceImpl.appMaster.getStatus() ){
			ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

			serviceChargeDebitDTO.setCustomerAccount(accountDto);
			serviceChargeDebitDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
			serviceChargeDebitDTO.setReferenceType(baseServiceImpl.referenceType);
			serviceChargeDebitDTO.setRequestID(baseServiceImpl.requestID.toString());
			serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
			serviceChargeDebitDTO.setTransactionType(transactionType.toString());
			serviceChargeDebitDTO.setAmount(0D);

			try {
				//utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
				
				serviceChargeDebitDTO=processRequest(CoreUrls.SERVICE_CHARGE_DEBIT_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);

				Transaction txn = new Transaction();
				txn.setRequestChannel(EOTConstants.REQUEST_CHANNEL_MOBILE);
				txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));

				baseServiceImpl.mobileRequest.setTransaction(txn);

				baseServiceImpl.appMaster.setStatus(Constants.APP_STATUS_RESET_PIN_SC_DEBITED);
				baseServiceImpl.eotMobileDao.update(baseServiceImpl.appMaster);
			} /*catch (EOTCoreException e) {
				e.printStackTrace();
				throw new EOTException(Integer.parseInt(e.getMessageKey()));
			}*/
			finally {
				
			}

		}

		try {

			baseServiceImpl.kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);

			Integer loginPin = EOTUtil.generateLoginPin() ;

			baseServiceImpl.customer.setLoginPin(HashUtil.generateHash(loginPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
			baseServiceImpl.eotMobileDao.update(baseServiceImpl.customer);

			NewLoginPinAlertDTO pinDto = new NewLoginPinAlertDTO();
			pinDto.setLocale(baseServiceImpl.customer.getDefaultLanguage());
			pinDto.setLoginPIN(loginPin.toString());
			pinDto.setMobileNo(baseServiceImpl.customer.getCountry().getIsdCode()+baseServiceImpl.customer.getMobileNumber());
			pinDto.setScheduleDate(Calendar.getInstance());

//			smsServiceClientStub.newLoginPinAlert(pinDto);
			
			sendSMS(UrlConstants.NEW_LOGIN_PIN_ALERT, pinDto);

			baseServiceImpl.appMaster.setStatus(Constants.APP_STATUS_NEW_PIN_SENT);
			baseServiceImpl.eotMobileDao.update(baseServiceImpl.appMaster);

			transactionBaseDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("RESET_PIN_SUCCESS", null, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

			return transactionBaseDTO;

		} catch (Exception e1) {
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

	}

	/**
	 * Process reset txn pin request.
	 *
	 * @param transactionBaseDTO the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processResetTxnPINRequest(com.eot.banking.dto.TransactionBaseDTO)
	 */
	public TransactionBaseDTO processResetTxnPINRequest( TransactionBaseDTO transactionBaseDTO ) throws EOTException {

		System.out.println("******** Reset Pin Service *************");

		baseServiceImpl.handleRequest(transactionBaseDTO);
		List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(baseServiceImpl.customer.getCustomerId());

		if(accountList.size() == 0){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		CustomerAccount account = accountList.get(0) ;

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(account.getAccountNumber());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
		accountDto.setBankCode(account.getBank().getBankId().toString());
		accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(baseServiceImpl.referenceType);
		serviceChargeDebitDTO.setRequestID(baseServiceImpl.requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(transactionBaseDTO.getTransactionType().toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			//utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);

			serviceChargeDebitDTO=processRequest(CoreUrls.SERVICE_CHARGE_DEBIT_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
		//	txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
			txn.setRequestChannel(EOTConstants.REQUEST_CHANNEL_MOBILE);
			baseServiceImpl.mobileRequest.setTransaction(txn);

		} /*catch (EOTCoreException e) {
			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}*/
		finally {
			
		}

		Integer transactionPin = EOTUtil.generateTransactionPin();

		try{

			baseServiceImpl.customer.setTransactionPin(HashUtil.generateHash(transactionPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
			baseServiceImpl.eotMobileDao.update(baseServiceImpl.customer);

			NewTxnPinAlertDTO pinDto = new NewTxnPinAlertDTO();
			pinDto.setLocale(baseServiceImpl.customer.getDefaultLanguage());
			pinDto.setTxnPIN(transactionPin.toString());
			pinDto.setMobileNo(baseServiceImpl.customer.getCountry().getIsdCode()+baseServiceImpl.customer.getMobileNumber());
			pinDto.setScheduleDate(Calendar.getInstance());

//			smsServiceClientStub.newTxnPinAlert(pinDto);
			sendSMS(UrlConstants.NEW_TXN_PIN_ALERT, pinDto);

		}catch (Exception e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

		transactionBaseDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("RESET_TXN_PIN_SUCCESS", null, new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return transactionBaseDTO;
	}

	/**
	 * Process set default account.
	 *
	 * @param transactionBaseDTO the transaction base dto
	 * @return the transaction base dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processSetDefaultAccount(com.eot.banking.dto.TransactionBaseDTO)
	 */
	public TransactionBaseDTO processSetDefaultAccount( TransactionBaseDTO transactionBaseDTO ) throws EOTException {
		System.out.println("******** processSetDefaultAccount *************");

		baseServiceImpl.handleRequest(transactionBaseDTO);
		transactionBaseDTO.setSuccessResponse( baseServiceImpl.messageSource.getMessage
				("SET_DEFAULT_ACC_SUCCESS", null , new Locale(baseServiceImpl.customer.getDefaultLanguage())));

		return transactionBaseDTO;
	}

	/**
	 * Process get baseServiceImpl.customer account details.
	 *
	 * @param saleDTO the sale dto
	 * @return the master data dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processGetCustomerAccountDetails(com.eot.banking.dto.TransactionBaseDTO)
	 */
	public MasterDataDTO processGetCustomerAccountDetails( SaleDTO saleDTO ) throws EOTException {
		System.out.println("******** processGetCustomerAccountDetails *************");

		Customer customer = baseServiceImpl.eotMobileDao.getCustomerFromMobileNo(saleDTO.getCustomerMobileNumber());

		if(customer==null){
			throw new EOTException(ErrorConstants.INVALID_MOBILE_NUMBER);
		}
		if(Constants.APP_TYPE_INTER_OPERABILITY==saleDTO.getAppType())
		{
			if(customer.getTransactionPin().equals(saleDTO.getTransactionPIN()))
				throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		
		
		List<CustomerAccount> beneficiaryAccount=null;
		Customer benificiary =null;
		if(null!=saleDTO.getBeneficiaryMobileNumber())
		{
			benificiary = baseServiceImpl.eotMobileDao.getCustomerFromMobileNo(saleDTO.getBeneficiaryMobileNumber());
			
			if(benificiary==null)
			{
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
			}
			
			
			beneficiaryAccount = baseServiceImpl.eotMobileDao.getCustomerAccounts(benificiary.getCustomerId());
			if(beneficiaryAccount.size() == 0 ){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

		}

		List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0 ){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		if(accountList.get(0).getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
			throw new EOTException(ErrorConstants.INACTIVE_BANK);
		}

		List<CustomerCard> cardList = baseServiceImpl.eotMobileDao.getCardDetails(customer.getCustomerId());

		/*if(otpForSaleEnabled){

			sendOtp();

		}*/
		
		
		MasterDataDTO masterDataDTO=packAccountDetails( accountList, cardList);
		com.eot.banking.dto.MasterDataDTO.Account account =  masterDataDTO.new Account();
		if (beneficiaryAccount!=null && beneficiaryAccount.size() != 0) {
			
			CustomerAccount custAcc = beneficiaryAccount.get(0);
			// For interoperability FI customers not allowed
			if (custAcc.getAccount().getAccountType() == 4) {
				throw new EOTException(ErrorConstants.INVALID_FI_CUSTOMER);
			}
			
			masterDataDTO.setBenificiaryAccount(custAcc.getAccountNumber());
			masterDataDTO.setBenificiaryName(benificiary.getFirstName()+" "+benificiary.getLastName());
			
			account.setAccountNumber(custAcc.getAccountNumber());
			account.setAccountAlias(custAcc.getAccount().getAlias());
			account.setAccountAliasType(Constants.ALIAS_TYPE_MOBILE_ACC);
			account.setStatus(Constants.ACTIVE);
			account.setBank(custAcc.getBank().getBankId());
			account.setBranch(custAcc.getBranch().getBranchId());
		}
		
		if(saleDTO.getCustomerOTP()!= null && !saleDTO.getCustomerOTP().isEmpty())
		{

			OtpDTO otpDto = new OtpDTO();
			otpDto.setOtphash(saleDTO.getCustomerOTP());
			otpDto.setReferenceId(customer.getCustomerId() + "");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			//otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_WITHDRAWA);
			otpDto.setAmount(saleDTO.getAmount());
			//Otp otp = baseServiceImpl.eotMobileDao.verifyOTPWithAmount(otpDto);
			Otp otp = baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
			System.out.println("baseServiceImpl.customerPin - " + saleDTO.getCustomerOTP());
			System.out.println("db - " + otpDto.getOtphash());
			if (otp == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}
		
		}
		masterDataDTO.setCbsBankCode(accountList.get(0).getBank().getBankCode());
		masterDataDTO.setRecevierAccount(account);
		masterDataDTO.setWalletCustomerId(customer.getCustomerId()+"");
		masterDataDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
		masterDataDTO.setTransactionPIN(customer.getTransactionPin());
		return masterDataDTO;
	}

	/**
	 * Pack response.
	 *
	 * @param masterDataDTO the master data dto
	 * @param customer the customer
	 * @param eotAccounts the eot accounts
	 * @param payeeList the payee list
	 * @param cards the cards
	 * @param banks the banks
	 * @param branchs the branchs
	 * @param operators the operators
	 * @param countries the countries
	 * @param networkTypes the network types
	 * @param locationTypeList the location type list
	 * @param currencies the currencies
	 * @param billerTypeList the biller type list
	 * @param billers the billers
	 * @param bankAccountDetails the bank account details
	 * @return the master data dto
	 */
	@SuppressWarnings("unchecked")
	private MasterDataDTO packResponse( MasterDataDTO masterDataDTO,Customer customer, List<CustomerAccount> eotAccounts,List<Payee> payeeList, List<CustomerCard> cards, List<Bank> banks,List<Branch> branchs, List<Operator> operators,List<Country> countries, List<NetworkType> networkTypes, List<LocationType> locationTypeList, List<Currency> currencies, List<BillerTypes> billerTypeList,List<Biller> billers, List<CustomerBankAccount> bankAccountDetails,MobileMenuMasterDataDTO mobileMenuMasterDataDTO,Map theamConfigMap,List<Language> languages,List<SecurityQuestion> securityQuestionModelList,List<HelpDesk> helpdesks,List<KycType> kycTypeList){

		ArrayList<com.eot.banking.dto.MasterDataDTO.Bank> listOfBank = new ArrayList<MasterDataDTO.Bank>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Operator> listOfOperator = new ArrayList<MasterDataDTO.Operator>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.BillerType> listOfBillerType = new ArrayList<MasterDataDTO.BillerType>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Biller> listOfBiller = new ArrayList<MasterDataDTO.Biller>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Branch> listOfBranch = new ArrayList<MasterDataDTO.Branch>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Account> listOfAccount = new ArrayList<MasterDataDTO.Account>();
		
		List<CustomerBankAccount> customerBankAccountList= new ArrayList<CustomerBankAccount>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Country> listOfCountry = new ArrayList<MasterDataDTO.Country>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Currency> listOfCurrency = new ArrayList<MasterDataDTO.Currency>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.NetworkType> listOfNetworkType = new ArrayList<MasterDataDTO.NetworkType>();

		ArrayList<PayeeDTO> listOfPayee = new ArrayList<PayeeDTO>();

		ArrayList<LocateUsDTO> listOfLocateUsDTO = new ArrayList<LocateUsDTO>();
		
		ArrayList<com.eot.banking.dto.MasterDataDTO.LanguageData> listOfLanguages = new ArrayList<MasterDataDTO.LanguageData>();
		
		List<SecurityQuestionDTO> securityQuestions = new ArrayList<SecurityQuestionDTO>();
		
		/*String firstName = baseServiceImpl.customer.getFirstName();
		String middleName = baseServiceImpl.customer.getMiddleName();
		String lastName = baseServiceImpl.customer.getLastName();*/
		
		String firstName = customer.getFirstName();
		String middleName = customer.getMiddleName();
		String lastName = customer.getLastName();
		String customerName = firstName;
		if(middleName != null && middleName.trim().length()>0)
			customerName = customerName + " " + middleName;
		if(lastName != null && lastName.trim().length()>0)
			customerName = customerName + " " + lastName;
	/*	masterDataDTO.setCustomerName(customerName);
		masterDataDTO.setCustomerCountryId(baseServiceImpl.customer.getCountry().getCountryId());
		masterDataDTO.setCustomerCountryName(baseServiceImpl.customer.getCountry().getCountry());
		masterDataDTO.setCustomerCountryIsdCode(baseServiceImpl.customer.getCountry().getIsdCode());
		masterDataDTO.setMobileNumber(baseServiceImpl.customer.getMobileNumber());*/
		
		masterDataDTO.setCustomerName(customerName);
		masterDataDTO.setProfileId(customer.getCustomerProfiles().getProfileId());
		masterDataDTO.setBankId(customer.getCustomerProfiles().getBank().getBankId());
		masterDataDTO.setCustomerCountryId(customer.getCountry().getCountryId());
		masterDataDTO.setCustomerCountryName(customer.getCountry().getCountry());
		masterDataDTO.setCustomerCountryIsdCode(customer.getCountry().getIsdCode());
		masterDataDTO.setMobileNumber(customer.getMobileNumber());

		for (CustomerAccount eotAccount : eotAccounts) {

			com.eot.banking.dto.MasterDataDTO.Account account = masterDataDTO.new Account();

			account.setAccountAlias(eotAccount.getAccount().getAlias());
			account.setAccountAliasType(Constants.ALIAS_TYPE_MOBILE_ACC);
			account.setStatus(Constants.ACTIVE);
			listOfAccount.add(account);
		}
		for (Payee payee : payeeList) {

			PayeeDTO payeeDTO = new PayeeDTO();

			payeeDTO.setPayeeAlias(payee.getAlias());
			payeeDTO.setPayeeAccountNumber(payee.getAccountNumber());
			payeeDTO.setPayeeName(payee.getAccountHolderName());
			payeeDTO.setPayeeType(Constants.ALIAS_TYPE_MOBILE_ACC);
			payeeDTO.setStatus(Constants.ACTIVE);
			listOfPayee.add(payeeDTO);
		}
		for (CustomerCard card : cards) {
			com.eot.banking.dto.MasterDataDTO.Account account = masterDataDTO.new Account();
			account.setAccountAlias(card.getAlias());
			account.setAccountAliasType(Constants.ALIAS_TYPE_CARD_ACC);
			account.setStatus(card.getStatus());
			listOfAccount.add(account);
		}
		for (CustomerBankAccount customerBankAccount : bankAccountDetails) {
			com.eot.banking.dto.MasterDataDTO.Account account = masterDataDTO.new Account();
			account.setAccountAlias(customerBankAccount.getAlias());
			account.setAccountAliasType(Constants.ALIAS_TYPE_BANK_ACC);
			account.setStatus(customerBankAccount.getStatus());
			account.setAccountNumber(customerBankAccount.getBankAccountNumber());
			listOfAccount.add(account);
		}
		for (Bank bank : banks) {
			com.eot.banking.dto.MasterDataDTO.Bank bankDetails = masterDataDTO.new Bank();
			bankDetails.setBankCode(bank.getBankCode());
			bankDetails.setBankName(bank.getBankName());
			bankDetails.setBankShortName(bank.getBankShortName());
			listOfBank.add(bankDetails);
		}
		for (Branch branch : branchs) {

			com.eot.banking.dto.MasterDataDTO.Branch branchDetails = masterDataDTO.new Branch();
			branchDetails.setBranchCode(branch.getBranchCode());
			branchDetails.setBankCode(branch.getBank().getBankCode());
			branchDetails.setBranchName(branch.getLocation());
			listOfBranch.add(branchDetails);
		}
		for (Language locale : languages) {
			com.eot.banking.dto.MasterDataDTO.LanguageData languageDetails = masterDataDTO.new LanguageData();
			languageDetails.setLanguageCode(locale.getLanguageCode());
			languageDetails.setDescription(locale.getDescription());
			languageDetails.setDownloadUrl(baseServiceImpl.appConfig.getLocaleFileDownloadURL().concat(locale.getLanguageCode()+".zip"));
			listOfLanguages.add(languageDetails);
		}
		
		for (SecurityQuestion securityQuestion : securityQuestionModelList) {
			
			SecurityQuestionDTO k = new SecurityQuestionDTO();
			k.setQuestionId(securityQuestion.getQuestionId());
			k.setQuestion(securityQuestion.getQuestion());
			k.setActive(securityQuestion.getActive());
			k.setLocale(securityQuestion.getLocale());
			securityQuestions.add(k);
		}		
		
		for (Operator operator : operators) {
			com.eot.banking.dto.MasterDataDTO.Operator operatorDetails = masterDataDTO.new Operator();
			operatorDetails.setCountryCode(operator.getCountry().getCountryCodeNumeric().toString());
			operatorDetails.setOperatorId(operator.getOperatorId());
			operatorDetails.setOperatorName(operator.getOperatorName());
			operatorDetails.setDenominationList(getOperatorDenominations(operator.getOperatorDenominations()));
			listOfOperator.add(operatorDetails);
		}
		for (Country country : countries) {
			com.eot.banking.dto.MasterDataDTO.Country countryDetails = masterDataDTO.new Country();
			countryDetails.setCountryCodeNumeric(country.getCountryCodeNumeric());
			/*countryDetails.setCountryName(getCountryByLanguage(country.getCountryNames(),baseServiceImpl.customer.getDefaultLanguage()));*/
			countryDetails.setCountryName(getCountryByLanguage(country.getCountryNames(),customer.getDefaultLanguage()));
			countryDetails.setIsdCode(country.getIsdCode());
			countryDetails.setMobileNumberLength(country.getMobileNumberLength()+"");
			Set<City> cities = country.getCities();
			ArrayList<com.eot.banking.dto.MasterDataDTO.City> listOfCity = new ArrayList<com.eot.banking.dto.MasterDataDTO.City>();
			List<City> cityList = new ArrayList<City>(cities);	
			Collections.sort(cityList, this.new SortByAlphaCity());
			for(City city : cityList){
				com.eot.banking.dto.MasterDataDTO.City cityDetails = masterDataDTO.new City();
				cityDetails.setCityId(city.getCityId());
				cityDetails.setCityName(city.getCity());
				listOfCity.add(cityDetails);

				Set<Quarter> quarters = city.getQuarters();
				ArrayList<com.eot.banking.dto.MasterDataDTO.Quarter> listOfQuarter = new ArrayList<com.eot.banking.dto.MasterDataDTO.Quarter>();
				List<Quarter> quarterList = new ArrayList<Quarter>(quarters);	
				Collections.sort(quarterList, this.new SortByAlphaQuarter());
				for(Quarter quarter : quarterList){
					com.eot.banking.dto.MasterDataDTO.Quarter quarterDetails = masterDataDTO.new Quarter();
					quarterDetails.setQuarterId(quarter.getQuarterId());
					quarterDetails.setQuarterName(quarter.getQuarter());
					listOfQuarter.add(quarterDetails);
				}
				cityDetails.setListOfQuarter(listOfQuarter);
			}
			countryDetails.setListOfCity(listOfCity);
			listOfCountry.add(countryDetails);
		}

		for(NetworkType networkType : networkTypes){
			com.eot.banking.dto.MasterDataDTO.NetworkType networkTypeDetails = masterDataDTO.new NetworkType();
			networkTypeDetails.setNetworkTypeId(networkType.getNetworkTypeId());
			networkTypeDetails.setNetworkType(networkType.getNetworkType());
			listOfNetworkType.add(networkTypeDetails);
		}

		for(LocationType locationType : locationTypeList){
			LocateUsDTO locateUsDTO = new LocateUsDTO();
			locateUsDTO.setLocationTypeId(locationType.getLocationTypeId());
			locateUsDTO.setLocationTypeName(locationType.getLocationType());
			listOfLocateUsDTO.add(locateUsDTO);
		}
		for (Currency currency : currencies){
			com.eot.banking.dto.MasterDataDTO.Currency currencyDetails = masterDataDTO.new Currency();
			currencyDetails.setCurrencyId(currency.getCurrencyId());
			currencyDetails.setCurrencyName(currency.getCurrencyName());
			listOfCurrency.add(currencyDetails);

		}
		for (BillerTypes billerTypes : billerTypeList) {
			com.eot.banking.dto.MasterDataDTO.BillerType billerType = masterDataDTO.new BillerType();
			billerType.setBillerId(billerTypes.getBillerTypeId());
			billerType.setBillerType(billerTypes.getBillerType());
			listOfBillerType.add(billerType);
		}
		for (Biller biller : billers) {
			com.eot.banking.dto.MasterDataDTO.Biller billerDetails = masterDataDTO.new Biller();
			billerDetails.setCountryCode(biller.getCountry().getCountryCodeNumeric()+"");
			billerDetails.setBillerId(biller.getBillerId());
			billerDetails.setBillerType(biller.getBillerType().getBillerTypeId()+"");
			billerDetails.setBillerName(biller.getBillerName());
			listOfBiller.add(billerDetails);
		}
		List<String> idTypes = new ArrayList<String>();
		for (KycType kycType : kycTypeList) {
			idTypes.add(kycType.getKycDescription());
		}


		masterDataDTO.setIdTypes(idTypes);
		masterDataDTO.setListOfAccount(listOfAccount);
		masterDataDTO.setListOfPayee(listOfPayee);
		masterDataDTO.setListOfBank(listOfBank);
		masterDataDTO.setListOfBillerType(listOfBillerType);
		masterDataDTO.setListOfBiller(listOfBiller);
		masterDataDTO.setListOfBranch(listOfBranch);
		masterDataDTO.setListOfCountry(listOfCountry);
		masterDataDTO.setListOfNetworkType(listOfNetworkType);
		masterDataDTO.setListOfLocateUsDTO(listOfLocateUsDTO);
		masterDataDTO.setListOfOperator(listOfOperator);
		masterDataDTO.setListOfCurrency(listOfCurrency);
		masterDataDTO.setListOfLanguages(listOfLanguages);
		masterDataDTO.setSecurityQuestions(securityQuestions);
		masterDataDTO.setTitleList(setTitleList());
		masterDataDTO.setListOfDynamicTabs(mobileMenuMasterDataDTO.getListOfDynamicTabs());
		masterDataDTO.setDynamicThemeColorCode(theamConfigMap);
		masterDataDTO.setHelpDeskList(helpdesks);
		masterDataDTO.setCustomerBankAccountList(bankAccountDetails);

		return masterDataDTO;
	}
	/**
	 * Gets the operator denominations.
	 * 
	 * @param denominations
	 *            the denominations
	 * @return the operator denominations
	 */
	private ArrayList<Long> getOperatorDenominations( Set<OperatorDenomination> denominations) {

		ArrayList<Long> denominationList = new ArrayList<Long>();

		for (OperatorDenomination denomination : denominations) {
			if(denomination.getActive()==1){
				denominationList.add(denomination.getDenomination());
			}
		}
		return denominationList;
	}

	/**
	 * Gets the country by language.
	 * 
	 * @param names
	 *            the names
	 * @param language
	 *            the language
	 * @return the country by language
	 */
	private String getCountryByLanguage( Set<CountryNames> names, String language ) {

		String name = null;
		for (CountryNames countryNames : names) {
			if(countryNames.getComp_id().getLanguageCode().equalsIgnoreCase(language)){
				name = countryNames.getCountryName();
			}
		}
		return name;
	}

	/**
	 * Pack account details.
	 *
	 * @param accountList the account list
	 * @param cardList the card list
	 * @return the master data dto
	 */
	private MasterDataDTO packAccountDetails( List<CustomerAccount> accountList,List<CustomerCard> cardList ) {

		MasterDataDTO masterDataDTO = new MasterDataDTO();
		ArrayList<com.eot.banking.dto.MasterDataDTO.Account> listOfAccount = new ArrayList<MasterDataDTO.Account>();

		for (CustomerAccount eotAccount : accountList) {

			com.eot.banking.dto.MasterDataDTO.Account account = masterDataDTO.new Account();

			account.setAccountAlias(eotAccount.getAccount().getAlias());
			account.setAccountAliasType(Constants.ALIAS_TYPE_MOBILE_ACC);
			account.setStatus(Constants.ACTIVE);
			account.setAccountNumber(eotAccount.getAccountNumber());
			account.setBranch(eotAccount.getBranch().getBranchId());
			account.setBank(eotAccount.getBank().getBankId());
			account.setBankCode(eotAccount.getBank().getBankCode());
			
			listOfAccount.add(account);
		}

		for (CustomerCard card : cardList) {
			com.eot.banking.dto.MasterDataDTO.Account account = masterDataDTO.new Account();
			account.setAccountAlias(card.getAlias());
			account.setAccountAliasType(Constants.ALIAS_TYPE_CARD_ACC);
			account.setStatus(card.getStatus());
			listOfAccount.add(account);
		}
		masterDataDTO.setListOfAccount(listOfAccount);

		return masterDataDTO;
	}

	/**
	 * Process send otp.
	 *
	 * @param otpdto the otpdto
	 * @return the otpdto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processSendOTP(com.eot.banking.dto.OTPDTO)
	 */
	@Override
	public OTPDTO processSendOTP(OTPDTO otpdto) throws EOTException {

		this.baseServiceImpl.handleRequest(otpdto);

		sendOtp();

		return otpdto;
	}

	private void sendOtp() throws EOTException {
		WebOTPAlertDTO dto = new WebOTPAlertDTO();
		dto.setLocale(baseServiceImpl.customer.getDefaultLanguage());
		dto.setMobileNo(baseServiceImpl.customer.getMobileNumber());
		dto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
		dto.setReferenceId(baseServiceImpl.customer.getCustomerId().toString());
		dto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		dto.setScheduleDate(Calendar.getInstance());
		SmsResponseDTO responseDTO=sendSMS(UrlConstants.NEW_LOGIN_PIN_ALERT, dto);
		if(responseDTO.getStatus().equalsIgnoreCase("0"))
			throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);

		/*
		 * try { smsServiceClientStub.webOTPAlert(dto); } catch (SmsServiceException e)
		 * { throw new EOTException(ErrorConstants.SMS_ALERT_FAILED); }
		 */
	}

	/**
	 * Process verify otp.
	 *
	 * @param otpdto the otpdto
	 * @return the otpdto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processVerifyOTP(com.eot.banking.dto.OTPDTO)
	 */
	@Override
	public OTPDTO processVerifyOTP(OTPDTO otpdto) throws EOTException {

		this.baseServiceImpl.handleRequest(otpdto);

		OtpDTO otpDto=new OtpDTO();
		otpDto.setOtphash(otpdto.getOtp());
		otpDto.setReferenceId(baseServiceImpl.customer.getCustomerId()+"");
		otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
		Otp otp =	baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
		if(otp== null){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
		}
		return otpdto;
	}

	/**
	 * Fetch payee info.
	 *
	 * @param payeeDTO the payee dto
	 * @return the payee dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#fetchPayeeInfo(com.eot.banking.dto.PayeeDTO)
	 */
	@Override
	public PayeeDTO fetchPayeeInfo(PayeeDTO payeeDTO) throws EOTException {
		this.baseServiceImpl.handleRequest(payeeDTO);
		Customer payee = baseServiceImpl.eotMobileDao.getCustomerFromMobileNo(payeeDTO.getPayeeAccountNumber());
		if(payee == null){
			throw new EOTException(ErrorConstants.PAYEE_DOES_NOT_EXIST);
		}
		String firstName = payee.getFirstName();
		String middleName = payee.getMiddleName();
		String lastName = payee.getLastName();
		String payeeName = firstName;
		if(middleName != null && middleName.trim().length()>0)
			payeeName = payeeName + " " + middleName;
		if(lastName != null && lastName.trim().length()>0)
			payeeName = payeeName + " " + lastName;
		payeeDTO.setPayeeName(payeeName);

		return payeeDTO;
	}

	/**
	 * Process add payee.
	 *
	 * @param payeeDTO the payee dto
	 * @return the payee dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processAddPayee(com.eot.banking.dto.PayeeDTO)
	 */
	@Override
	public PayeeDTO processAddPayee(PayeeDTO payeeDTO) throws EOTException {

		this.baseServiceImpl.handleRequest(payeeDTO);
		Customer newPayee = null;
		Bank bank = null;
		Branch branch = null;

		if( ! payeeDTO.getTransactionPIN().equals(baseServiceImpl.customer.getTransactionPin()) ){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}
		if( payeeDTO.getPayeeAlias().length()>Constants.PAYEE_NAME_LEGNTH ){
			throw new EOTException(ErrorConstants.PAYEE_NAME_TO_LONG);
		}

		Payee payee = baseServiceImpl.eotMobileDao.getPayeeFromAlias(baseServiceImpl.customer.getCustomerId(), payeeDTO.getPayeeAlias());
		if(payee != null && payee.getStatus().intValue() != Constants.INACTIVE){
			throw new EOTException(ErrorConstants.PAYEE_EXIST);
		}
		if(payeeDTO.getPayeeType().equals(Constants.WALLET_PAYEE)){
			newPayee = baseServiceImpl.eotMobileDao.getCustomerFromMobileNo(payeeDTO.getPayeeAccountNumber());
		}else{
			bank = baseServiceImpl.eotMobileDao.getBankFromBankId(payeeDTO.getBankId());
			branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(payeeDTO.getBranchId());
		}
		payee = new Payee();
		payee.setAccountHolderName(payeeDTO.getPayeeName());
		payee.setAccountNumber(payeeDTO.getPayeeAccountNumber());
		payee.setAlias(payeeDTO.getPayeeAlias());
		if(payeeDTO.getPayeeType().equals(Constants.WALLET_PAYEE)){

			CustomerAccount payeeAccount = (CustomerAccount) newPayee.getCustomerAccounts().iterator().next();
			payee.setBankCode(payeeAccount.getBank().getBankCode());
			payee.setBranchCode(payeeAccount.getBranch().getBranchCode());
		}else{
			payee.setBankCode(bank.getBankCode());
			payee.setBranchCode(branch.getBranchCode());
		}
		payee.setPayeeType(payeeDTO.getPayeeType());
		payee.setStatus(Constants.ACTIVE);
		payee.setReferenceType(baseServiceImpl.customer.getType());
		payee.setReferenceId(baseServiceImpl.customer.getCustomerId().toString());
		payee.setCreatedDate(new Date());
		payee.setUpdatedDate(new Date());

		baseServiceImpl.eotMobileDao.save(payee);

		List<CustomerAccount> accountList = baseServiceImpl.eotMobileDao.getCustomerAccounts(baseServiceImpl.customer.getCustomerId());

		if(accountList.size() == 0){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(accountList.get(0).getAccountNumber());
		accountDto.setBankCode(accountList.get(0).getBank().getBankId().toString());
		accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(baseServiceImpl.customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(baseServiceImpl.referenceType);
		serviceChargeDebitDTO.setRequestID(baseServiceImpl.requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(payeeDTO.getTransactionType().toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			//serviceChargeDebitDTO =	utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
			
			serviceChargeDebitDTO=processRequest(CoreUrls.SERVICE_CHARGE_DEBIT_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));

		} finally {
			
		}/*catch (EOTCoreException e) {
			e.printStackTrace();
			baseServiceImpl.eotMobileDao.delete(payee);
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}*/

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(payeeDTO.getTransactionType());
		baseServiceImpl.mobileRequest.setTransactionType(transactionType);

		payeeDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("ADD_PAYEE_SUCCESS", new String[]{payeeDTO.getPayeeAlias()}, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		return payeeDTO;
	}

	/**
	 * Process delete payee.
	 *
	 * @param payeeDTO the payee dto
	 * @return the payee dto
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#processDeletePayee(com.eot.banking.dto.PayeeDTO)
	 */
	@Override
	public PayeeDTO processDeletePayee(PayeeDTO payeeDTO) throws EOTException {
		this.baseServiceImpl.handleRequest(payeeDTO);
		String payeeAlias = payeeDTO.getPayeeAlias();

		Payee payee = baseServiceImpl.eotMobileDao.getPayeeFromAlias(baseServiceImpl.customer.getCustomerId(), payeeAlias);

		if(payee == null){
			throw new EOTException(ErrorConstants.PAYEE_NOT_FOUND);
		}

		payee.setStatus(Constants.INACTIVE);

		baseServiceImpl.eotMobileDao.update(payee);

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(payeeDTO.getTransactionType());
		baseServiceImpl.mobileRequest.setTransactionType(transactionType);

		payeeDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("DELETE_PAYEE_SUCCESS", null, new Locale(baseServiceImpl.customer.getDefaultLanguage())));
		return payeeDTO;
	}

	/**
	 * Creates the cutomer profile.
	 *
	 * @param customerDTO the baseServiceImpl.customer dto
	 * @return the baseServiceImpl.customer profile dto
	 * @throws Exception the exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#createCutomerProfile(com.eot.banking.dto.CustomerProfileDTO)
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
	public CustomerProfileDTO createCutomerProfile(CustomerProfileDTO customerDTO) throws Exception {

		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(customerDTO.getApplicationId());
		
		if(appMaster.getStatus()==Constants.APP_STATUS_BLOCKED ||appMaster.getStatus()==Constants.APP_STATUS_DEACTIVATED){
			throw new EOTException(ErrorConstants.UNVAILABLE_REGISTRATION);
		}
		
		Country country = null; 
		CustomerProfiles customerProfile= null;
		
		if (null == customerDTO.getFirstName()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "First name");
		}
		
		if (null == customerDTO.getMobileNumber()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Mobile number");
		}
		
		if (null == customerDTO.getDob()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Date of birth");
		}		
		
		if (null == customerDTO.getCityId()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "City");
		}
		
		if (null == customerDTO.getQaurterId()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Quarter");
		}
		if (null == customerDTO.getAddress()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Address");
		}
		
		/*if (null == customerDTO.getIdProof()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Id Proof");
		}
		
		if (null == customerDTO.getKycTypeId()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "KYC Type");
		}*/
		// Question,Answer and Language are required for this API so adding by vineeth, on 31-10-2018
			/*	 if (null == customerDTO.getQuestionId()) {
					
					throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Question");
				}
				
				if (null == customerDTO.getAnswer()) {
					
					throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Answer");
				}*/
				/*if (null == customerDTO.getLanguageCode()) {
					
					throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Language");
				}*/
		// changes end
		if(customerDTO.getCountryId() != null){
		//	country = baseServiceImpl.eotMobileDao.getCountryFromCountryCode(customerDTO.getCountryId());
			country = baseServiceImpl.eotMobileDao.getCountry(customerDTO.getCountryId());			
		}else{
			country = baseServiceImpl.eotMobileDao.getCountry(Constants.DEFAULT_COUNTRY_ID);
		}
		customerDTO.setDefaultLocale(StringUtils.isNotEmpty(customerDTO.getDefaultLocale()) ? customerDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);
		customerDTO.setVersionNumber(StringUtils.isNotEmpty(customerDTO.getVersionNumber()) ? customerDTO.getVersionNumber() : Constants.DEFAULT_APP_VERSIOn);
		
		String mobileNumber=country.getIsdCode().toString().concat(customerDTO.getMobileNumber());
		Customer cust = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(mobileNumber);
		if( cust != null ){
			throw new EOTException(ErrorConstants.MOBILE_NUMBER_REGISTERED_ALREADY);
		}
		
		// Validating OTP
		validateOtp(customerDTO, mobileNumber);
		
		String onboardedBy = null;
		BusinessPartner businessPartner = null;
		Customer merchant = null;
		CustomerAccount merchantAccountDetails =null;		
		if (StringUtils.isNotEmpty(customerDTO.getApplicationId())) {
			merchant = baseServiceImpl.eotMobileDao.getCustomer(customerDTO.getApplicationId());
			if (merchant == null) {
				throw new EOTException(ErrorConstants.INVALID_MERCHANT);
			}
			if(merchant.getActive()==Constants.APP_STATUS_BLOCKED)
			{
				throw new EOTException(ErrorConstants.APPLICATION_BLOCKED);
				
			}
			// customer registered for same country where the merchant
			// bellongs:bidyut
			country = merchant.getCountry();

			merchantAccountDetails = baseServiceImpl.eotMobileDao
					.getAccountFromCustomerId(merchant.getCustomerId());

			if (merchantAccountDetails == null) {
				throw new EOTException(ErrorConstants.INVALID_MERCHANT_ACCOUNT);
			}
			onboardedBy = merchant.getAgentCode();
			businessPartner = merchant.getBusinessPartner();
			customerProfile=baseServiceImpl.eotMobileDao.getCustomerProfile(merchantAccountDetails.getBank().getBankId(),customerDTO.getType());
		}else
		{
			customerProfile=baseServiceImpl.eotMobileDao.getSelfRegisterProfile();
		}
		
		cust = new Customer();

		Integer loginPin = EOTUtil.generateLoginPin() ;
		Integer txnPin = EOTUtil.generateTransactionPin();
		String appID = EOTUtil.generateAppID();
		String uuid = EOTUtil.generateUUID();

		AppMaster app = new AppMaster();
		app.setAppId(appID);
		app.setReferenceId(cust.getCustomerId()+"");
		app.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		app.setStatus(Constants.APP_STATUS_NEW);
		app.setUuid(uuid);
		app.setAppVersion(customerDTO.getVersionNumber());

		Calendar cal = Calendar.getInstance();
		app.setCreatedDate(cal.getTime());
		Integer dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
		app.setExpiryDate(cal.getTime());

		baseServiceImpl.eotMobileDao.save(app);

		City city = baseServiceImpl.eotMobileDao.getCityFromCityId(customerDTO.getCityId());//new City();
		Quarter quarter = baseServiceImpl.eotMobileDao.getQuarterFromQuarterId(customerDTO.getQaurterId()==null ? 1L: customerDTO.getQaurterId());//new Quarter();
		String customerMobileNumber=customerDTO.getMobileNumber();
		if(customerMobileNumber.startsWith("0"))
			customerMobileNumber=customerMobileNumber.substring(1);
		cust.setMobileNumber(customerMobileNumber);
		cust.setEmailAddress(customerDTO.getEmailId());
		cust.setFirstName(customerDTO.getFirstName());
		cust.setMiddleName(customerDTO.getMiddleName());
		cust.setLastName(customerDTO.getLastName());
		cust.setDob(new Date(customerDTO.getDob()));
		cust.setKycStatus(0);

		cust.setAddress(customerDTO.getAddress());
		cust.setCountry(country);
		city.setCityId(customerDTO.getCityId());
		quarter.setQuarterId(customerDTO.getQaurterId()==null ? 1L: customerDTO.getQaurterId());
		cust.setCity(city);
		cust.setQuarter(quarter);
		cust.setType(Constants.REF_TYPE_CUSTOMER);
		cust.setActive(Constants.CUSTOMER_STATUS_NEW);

		cust.setTitle(customerDTO.getTitle());
		cust.setGender(customerDTO.getGender());
		cust.setDefaultLanguage(customerDTO.getLanguageCode() != null ? customerDTO.getLanguageCode() : Constants.DEFAULT_LANGUAGE );
	//	cust.setDefaultLanguage(Constants.DEFAULT_LANGUAGE);

		cust.setLoginPin(HashUtil.generateHash(loginPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
		//cust.setTransactionPin(HashUtil.generateHash(txnPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
		cust.setTransactionPin(HashUtil.generateHash(loginPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
		cust.setAppId(appID);
		cust.setOnbordedBy(onboardedBy !=null ? onboardedBy : "Self");
		cust.setBusinessPartner(businessPartner);
		cust.setCreatedDate(new Date());
		cust.setChanel("Mobile");
		CustomerProfiles custProfile=new CustomerProfiles();
		custProfile.setProfileId(customerDTO.getProfileId() != null ? customerDTO.getProfileId() : Constants.DEFAULT_CUSTOMER_PROFILE);
		if(customerProfile!=null)
			custProfile.setProfileId(customerProfile.getProfileId());
		cust.setCustomerProfiles(custProfile);

		baseServiceImpl.eotMobileDao.save(cust);
		app.setReferenceId(cust.getCustomerId().toString());
		baseServiceImpl.eotMobileDao.update(app);
		
		CustomerDocument customerDocument = new CustomerDocument();
		if ( null != customerDTO.getIdProof() &&  !"".equals(customerDTO.getIdProof() )) 
		{
			
			if (customerDTO.getAddressProof()!= null) 
				customerDocument.setSignaturePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getSignature())));
			if (customerDTO.getIdProof()!= null) 
				customerDocument.setIdproofPhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getIdProof())));
			if(customerDTO.getCustomerPhoto()!=null)
				customerDocument.setProfilePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getCustomerPhoto())));
			if(customerDTO.getAddressProof()!=null)
				customerDocument.setAddressProof(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getAddressProof())));
			customerDocument.setCustomerId(cust.getCustomerId());
			customerDocument.setCustomer(cust);
			baseServiceImpl.eotMobileDao.save(customerDocument);
		}else {
			customerDocument.setCustomerId(cust.getCustomerId());
			customerDocument.setCustomer(cust);
			customerDocument.setSignaturePhoto(Hibernate.createBlob("".getBytes()));
			customerDocument.setIdproofPhoto(Hibernate.createBlob("".getBytes()));
			customerDocument.setProfilePhoto(Hibernate.createBlob("".getBytes()));
			customerDocument.setAddressProof(Hibernate.createBlob("".getBytes()));
			baseServiceImpl.eotMobileDao.save(customerDocument);
		}
		/*CustomerSecurityAnswer answer = new CustomerSecurityAnswer();
		answer.setAnswer(customerDTO.getAnswer());
		answer.setCustomer(cust);
		SecurityQuestion securityQuestion = new SecurityQuestion();
		securityQuestion.setQuestionId(customerDTO.getQuestionId());
		answer.setSecurityQuestion(securityQuestion);*/
		
		//below bank id code is mobified by bidyut
		//perpous is to achive the corret bank registration for FiCustomer insted of default bank
		
		
		int bankId=Constants.DEFAULT_BANK;
		long branchId=Constants.DEFAULT_BRANCH;
		
		Bank custBank = null;
		if(StringUtils.isNotEmpty(customerDTO.getBankCode())){
			custBank = baseServiceImpl.eotMobileDao.getBankIdFromBankCode(customerDTO.getBankCode());
			bankId = null != custBank ? custBank.getBankId() : bankId;
		}
		
		
		if(null!=customerDTO.getBankId())
		{
			bankId=customerDTO.getBankId();
		}
		if(null!=customerDTO.getCbsBranchCode())
		{
			branchId=baseServiceImpl.eotMobileDao.getBranchId(bankId, customerDTO.getCbsBranchCode());
		}
		
	//	baseServiceImpl.eotMobileDao.save(answer);
		Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(null != merchantAccountDetails && null != merchantAccountDetails.getBank() && null != merchantAccountDetails.getBank().getBankId() ? merchantAccountDetails.getBank().getBankId(): bankId);
	//	Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(customerDTO.getBankId() != null ? customerDTO.getBankId() : Constants.DEFAULT_BANK);
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(null != merchantAccountDetails && null != merchantAccountDetails.getBranch() && null != merchantAccountDetails.getBranch().getBranchId() ? merchantAccountDetails.getBranch().getBranchId() : branchId);
	//	Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(customerDTO.getBranchId() != null ? customerDTO.getBranchId() : Constants.DEFAULT_BRANCH);

		Account account = new Account();
		account.setAccountNumber(EOTUtil.generateAccountNumber(baseServiceImpl.eotMobileDao.getNextAccountNumberSequence()));
		account.setAccountType(customerDTO.getAliasType() != null ? customerDTO.getAliasType() : Constants.ACCOUNT_TYPE_PERSONAL);
		account.setActive(Constants.APP_STATUS_ACTIVATED);
		String alias = Constants.ACCOUNT_ALIAS_CUSTOMER + " - " + bank.getBankName() ;
		account.setAlias(alias);
		account.setCurrentBalance(0.0);
		account.setCurrentBalanceType(Constants.ACCOUNT_BALANCE_TYPE_CREDIT);
		account.setReferenceId(cust.getCustomerId().toString());
		account.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		account.setAliasType(Constants.ALIAS_TYPE_WALLET_ACCOUNT);

		try{
		baseServiceImpl.eotMobileDao.save(account);
		}catch (Exception e) {
//			e.printStackTrace();
			// TODO: handle exception
		}

		CustomerAccount customerAccount = new CustomerAccount();
		customerAccount.setAccount(account);
		customerAccount.setAccountNumber(account.getAccountNumber());
		customerAccount.setCustomer(cust);
		customerAccount.setBank(bank);
		customerAccount.setBranch(branch);

		baseServiceImpl.eotMobileDao.save(customerAccount);

		customerDTO.setAccountNumber(account.getAccountNumber());
		customerDTO.setCustomerId(cust.getCustomerId());

		String appType = cust.getType().equals(EOTConstants.REFERENCE_TYPE_CUSTOMER) ?Constants.APP_TYPE_CUSTOMER
				: cust.getType().equals(EOTConstants.REFERENCE_TYPE_AGENT) ? Constants.APP_TYPE_AGENT
				: cust.getType().equals(EOTConstants.REFERENCE_TYPE_MERCHANT) ? Constants.APP_TYPE_MERCHANT:"";
		  AppLinkAlertDTO appAlertDto = new AppLinkAlertDTO();
		  appAlertDto.setApplicationName("m-GURUSH");
		  appAlertDto.setDownloadLink(appConfigurations.getAppDownloadURL()+appType);
		  
		  appAlertDto.setLocale(cust.getDefaultLanguage());
		  appAlertDto.setMobileNo(country.getIsdCode()+cust.getMobileNumber());
		  appAlertDto.setScheduleDate(Calendar.getInstance());
//		  smsServiceClientStub.appLinkAlert(appAlertDto);
		  sendSMS(UrlConstants.APP_LINGK_ALERT, appAlertDto);
		  
		 
		 InitialTxnPinLoginPinAlertDTO pinDto = new InitialTxnPinLoginPinAlertDTO();
		 pinDto.setLocale(cust.getDefaultLanguage());
		 pinDto.setLoginPIN(loginPin.toString());
		 pinDto.setMobileNo(country.getIsdCode()+cust.getMobileNumber());
		 pinDto.setTxnPIN(loginPin.toString());
		 pinDto.setScheduleDate(Calendar.getInstance());
//		 smsServiceClientStub.initialTxnPinLoginPinAlert(pinDto);
		 sendSMS(UrlConstants.INITIAL_LOGIN_AND_TXN_PIN_ALERT, pinDto);
		
		customerDTO.setApplicationId(appID);
		customerDTO.setStatus(0);
		customerDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTMER_REGISTRATION_SUCCESS", null, new Locale(cust.getDefaultLanguage())));
		customerDTO.setMessageDescription(baseServiceImpl.messageSource.getMessage("CUSTMER_REGISTRATION_SUCCESS", null, new Locale(cust.getDefaultLanguage())));
		return customerDTO;

	}

	/**
	 * Gets the exchange rate.
	 *
	 * @param exchangeRateDTO the exchange rate dto
	 * @return the exchange rate
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#getExchangeRate(com.eot.banking.dto.ExchangeRateDTO)
	 */
	@Override
	public void getExchangeRate(ExchangeRateDTO exchangeRateDTO) throws EOTException {

		this.baseServiceImpl.handleRequest(exchangeRateDTO);
		CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), exchangeRateDTO.getAccountAlias());
		if(account == null){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		ExchangeRate exchangeRate = baseServiceImpl.eotMobileDao.getExchangeRateFromCurrecyIdAndBankId(exchangeRateDTO.getCurrencyId(),account.getBank().getBankId());

		if (exchangeRate == null) {
			throw new EOTException(ErrorConstants.EXCHANGE_RATE_NOT_AVAILABLE);
		}
		exchangeRateDTO.setBuyingRate(exchangeRate.getBuyingRate());
		exchangeRateDTO.setSellingRate(exchangeRate.getSellingRate());

	}

	/**
	 * Gets the location.
	 *
	 * @param locateUsDTO the locate us dto
	 * @return the location
	 * @throws EOTException the EOT exception
	 */
	@SuppressWarnings("unused")
	@Override
	public void getLocation(LocateUsDTO locateUsDTO) throws EOTException {
		this.baseServiceImpl.handleRequest(locateUsDTO);

		List<String> address = new ArrayList<String>();
		String businessName ="";
		String data = "";
		/*commented by bidyut
		 * Location should be fetch from locateUs table not from branch table
		 * 
		 * 
		 * if(locateUsDTO.getLocationTypeId().equals(Constants.LOCATION_TYPE_BRANCH)){
			List<Branch> branchs = baseServiceImpl.eotMobileDao.getBranchList(locateUsDTO);
			if(null == branchs){
				throw new EOTException(ErrorConstants.LOCATION_DETAILS_NOT_AVAILABLE);
			}
			for (Branch branch : branchs) {
				address.add(branch.getAddress());
			}
		}else{

			List<LocateUS> locateUSList = baseServiceImpl.eotMobileDao.getServiceLocation(locateUsDTO);
			if(null == locateUSList){
				throw new EOTException(ErrorConstants.LOCATION_DETAILS_NOT_AVAILABLE);
			}
			for (LocateUS locateUS : locateUSList) {
				address.add(locateUS.getAddress());
				locateUsDTO.setLatitude(locateUS.getLatitude());
				locateUsDTO.setLongitude(locateUS.getLongitude());
			}
		}*/
		
		
		List<LocateUS> locateUSList = baseServiceImpl.eotMobileDao.getServiceLocation(locateUsDTO);
		/*if(null == locateUSList){
			throw new EOTException(ErrorConstants.LOCATION_NOT_FOUND);
		}*/
		if(locateUSList!=null){
			for (LocateUS locateUS : locateUSList) {
			address.add(locateUS.getAddress());
			locateUsDTO.setLatitude(locateUS.getLatitude());
			locateUsDTO.setLongitude(locateUS.getLongitude());
		}
		}
		List<Customer> Agents = baseServiceImpl.eotMobileDao.getAllAgents(locateUsDTO);
		//List<String> agentLocation= baseServiceImpl.eotMobileDao.getGeographyLocationOfAgents(locateUsDTO);
		
		if(CollectionUtils.isEmpty(Agents)){
			throw new EOTException(ErrorConstants.LOCATION_NOT_FOUND);
		}
		
		List<LocateUsDTO.Address> addresses = new ArrayList<>();
		for(Customer customer : Agents){
			LocateUsDTO.Address address2 = new LocateUsDTO().new Address();
			address2.setAddress(customer.getAddress());
			address2.setMobile(customer.getMobileNumber());
			address2.setName(customer.getBusinessName());
			addresses.add(address2);
			locateUsDTO.setAddressList(addresses);
		}
		
	/*	if(locateUSList==null && agentLocation==null){
			throw new EOTException(ErrorConstants.LOCATION_DETAILS_NOT_AVAILABLE);
		}*/
		/*if(CollectionUtils.isNotEmpty(agentLocation)){
			address.addAll(agentLocation);
		}*/
		locateUsDTO.setAddress(address);
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.service.OtherBankingService#convertCurrency(com.eot.banking.dto.CurrencyConverterDTO)
	 */
	@Override
	public void convertCurrency(CurrencyConverterDTO currencyConverterDTO) throws EOTException {
		this.baseServiceImpl.handleRequest(currencyConverterDTO);
		CustomerAccount account = baseServiceImpl.eotMobileDao.getAccountFromAccountAlias(baseServiceImpl.customer.getCustomerId(), currencyConverterDTO.getAccountAlias());
		if(account == null){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		CurrencyConverter currencyConverter = baseServiceImpl.eotMobileDao.getConversionRateFromCurrecyIdsAndBankId(
				currencyConverterDTO.getBaseCurrencyId(),currencyConverterDTO.getCounterCurrencyId(),
				account.getBank().getBankId());

		if(currencyConverter == null){
			throw new EOTException(ErrorConstants.CONVERSION_RATE_NOT_AVAILABLE);
		}

		currencyConverterDTO.setConversionRate(currencyConverter.getConversionRate()*currencyConverterDTO.getDemonination());
	}


	@Override
	public NumericCodeDTO getCountryCurrencyNumericCode(String bankCode) throws Exception {
		com.eot.entity.Bank bank = baseServiceImpl.eotMobileDao.getBankIdFromBankCode(bankCode);
		com.eot.entity.Country country = null;
		com.eot.entity.Currency currency = null;
		
		if (bank != null){
			
			 country = bank.getCountry();
			 currency = bank.getCurrency();
		}
		
		if (country == null)
			throw new EOTException(ErrorConstants.INVALID_BANK_ID);
		
		NumericCodeDTO countryDTO = new NumericCodeDTO(); 
		countryDTO.setCountryNumericCode(country.getCountryCodeNumeric().toString());;
		countryDTO.setCurrencyNumericCode(currency.getCurrencyCodeNumeric().toString());
		
		return countryDTO;
	}

	
	@Override
	public NumericCodeDTO getCountryByCountryId(Integer countryId) throws Exception {
		com.eot.entity.Country country = baseServiceImpl.eotMobileDao.getCountryFromCountryCode(countryId);
		
		
		
		if (country == null)
			throw new EOTException(ErrorConstants.INVALID_BANK_ID);
		
		NumericCodeDTO countryDTO = new NumericCodeDTO(); 
		countryDTO.setCountryName(country.getCountry());
		
		return countryDTO;
	}
	
	@Override
	public NumericCodeDTO getCityByCityId(Integer cityId) throws Exception {
		com.eot.entity.City city = baseServiceImpl.eotMobileDao.getCityFromCityId(cityId);
		
		
		
		if (city == null)
			throw new EOTException(ErrorConstants.INVALID_BANK_ID);
		NumericCodeDTO countryDTO = new NumericCodeDTO(); 
		countryDTO.setCityName(city.getCity());
		
		
		return countryDTO;
	}
	
	@Override
	public NumericCodeDTO getQuarterByQuarterId(Long quarterId) throws Exception {
		com.eot.entity.Quarter quarter  = baseServiceImpl.eotMobileDao.getQuarterFromQuarterId(quarterId);
		
		
		
		if (quarter == null)
			throw new EOTException(ErrorConstants.INVALID_BANK_ID);
		
		NumericCodeDTO countryDTO = new NumericCodeDTO(); 
		countryDTO.setQuarterName(quarter.getQuarter());
		
		return countryDTO;
	}

	@Override
	public NumericCodeDTO getCountryByBankId(Integer bankId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean validateBranch(MasterDataDTO masterDataDTO) throws Exception {
		com.eot.entity.Bank bank = baseServiceImpl.eotMobileDao.getBankIdFromBankCode(masterDataDTO.getBankCode());
		Long branchId = baseServiceImpl.eotMobileDao.getBranchId(bank.getBankId(),masterDataDTO.getCbsBranchCode());
		return branchId!=null?true:false;
	}
	@Override
	public Integer getBankIdByBankCode(MasterDataDTO masterDataDTO) throws Exception {
		if(StringUtils.isNotEmpty(masterDataDTO.getCbsBankCode()))
			masterDataDTO.setBankCode(masterDataDTO.getCbsBankCode());
		com.eot.entity.Bank bank = baseServiceImpl.eotMobileDao.getBankIdFromBankCode(masterDataDTO.getBankCode());
		return bank!=null?bank.getBankId():null;
	}

	@Override
	public ServiceChargeDTO getServiceCharge(ServiceChargeDTO serviceChargeDTO) throws EOTException {/*
		List<Object[]> scRules =  baseServiceImpl.eotMobileDao.getSCRules(masterDataDTO.getAppType());
		ArrayList<SCRuleDTO> listOfSCRules = new ArrayList<SCRuleDTO>();
		for(Object[] obj : scRules){

			SCRuleDTO dto = new SCRuleDTO();
			dto.setRuleType((Integer)obj[0]);
			dto.setServiceChargeRuleId((Long)obj[1]);
			dto.setServiceChargePct((Float)obj[2]);
			dto.setServiceChargeFxd((Long)obj[3]);
			dto.setDiscountLimit((Long)obj[4]);
			dto.setMinServiceCharge((Long)obj[5]);
			dto.setMaxServiceCharge((Long)obj[6]);
			dto.setMinTxnValue((Long)obj[7]);
			dto.setMaxTxnValue((Long)obj[8]);
			dto.setTimeZoneRule((Integer)obj[9]);
			dto.setImposedOn((Integer)obj[10]);
			dto.setApplicableFrom(((Date)obj[11]).getTime());
			dto.setApplicableTo(((Date)obj[12]).getTime());
			dto.setApplicableDay((Integer)obj[13]);
			dto.setApplciableFromHH((Integer)obj[14]);
			dto.setApplicableToHH((Integer)obj[15]);
			dto.setTransactionType((Integer)obj[16]);
			listOfSCRules.add(dto);
		}
		
		//Set master DTO
		masterDataDTO.setListOfSCRules(listOfSCRules);*/
		


		Double serviceCharge = null;
		/*Double stampFee=0D;
		Integer  serviceChargeImposedOn = EOTConstants.SERVICE_CHARGE_IMPOSED_ON_CUSTOMER;
		ServiceChargeSplit[] serviceChargeSplitList = new ServiceChargeSplit[0];*/

		Integer transactionType = new Integer( serviceChargeDTO.getTxnTypeId() );
		/*if(transactionType.intValue()==Constants.TXN_TYPE_SMS_CASH) {
			transactionType=Constants.TXN_TYPE_SMS_CASH_RECEIVE;
		}
			*/
		 if(serviceChargeDTO.getTransactionType()==Constants.TXN_TYPE_FLOAT_MANAGEMENT) {
			transactionType=Constants.TXN_TYPE_FLOAT_MANAGEMENT;
		}
		//TransactionType txnType = transactionRuleDao.getTransactionType(transactionType);

		//initializeAllAmtsToZero( header );

		/*if(txnType.getHasServiceCharges()==1){
			if(header.getTxnMode() == null || header.getTxnMode() != CoreConstants.OFFLINE_TXN){*/
				//Integer bankGroupId = null;
				//Integer customerProfileId = null;

				//String customerId = header.getTransactionType().equals(CoreConstants.TXN_TYPE_SALE)?((SaleDTO) header).getCustomerID() : header.getReferenceID();

				//CustomerAccount customer = accountDao.getCustomerAccountByCustomerId(customerId);

				//bankGroupId = null != customer && null != customer.getBank() && customer.getBank().getBankGroup() != null ? customer.getBank().getBankGroup().getBankGroupId() : null ;
				//customerProfileId = null != customer && null != customer.getCustomer() && null != customer.getCustomer().getCustomerProfiles() ? customer.getCustomer().getCustomerProfiles().getProfileId(): null;

				Map<Integer, Map<Long, List<SCRuleDTO>>> rulesList = null;

				if(EOTConstants.TXN_TYPE_INTRA == serviceChargeDTO.getTxnBankingType()){
					rulesList = serviceChargeDao.getIntraBankServiceChargeRules(transactionType, serviceChargeDTO.getApplicationType() , null, null,serviceChargeDTO.getTxnAmount()  );
				}else{
					rulesList = serviceChargeDao.getInterBankServiceChargeRules(transactionType, serviceChargeDTO.getApplicationType(), serviceChargeDTO.getTxnAmount()) ;
				}

				List<SCRuleDTO> applicableRules;
				try {
					applicableRules = getApplicableRules(rulesList, serviceChargeDTO.getTxnAmount());
				
				if( applicableRules.size() == 0 ){
					throw new EOTException( EOTConstants.SERVICE_CHARGE_NOT_DEFINED );
				}

				for(SCRuleDTO ruleDTO : applicableRules) {

					Double tempServiceCharge = calculateServiceCharge(null != serviceChargeDTO.getTxnAmount() ? serviceChargeDTO.getTxnAmount():0, null != ruleDTO.getServiceChargePct()? ruleDTO.getServiceChargePct():0, null != ruleDTO.getServiceChargeFxd() ? ruleDTO.getServiceChargeFxd():0,
							null != ruleDTO.getDiscountLimit()?ruleDTO.getDiscountLimit():0, null != ruleDTO.getMinServiceCharge() ? ruleDTO.getMinServiceCharge() :0, null!= ruleDTO.getMaxServiceCharge()? ruleDTO.getMaxServiceCharge() :0);

					if( serviceCharge == null || tempServiceCharge < serviceCharge ){
						serviceCharge = tempServiceCharge;
						//serviceChargeImposedOn = ruleDTO.getImposedOn();
					}
				}
				serviceChargeDTO.setServiceCharge(serviceCharge);
				serviceChargeDTO.setServiceChargeAmt(serviceCharge);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			/*} else {
				//serviceCharge = header.getServiceChargeAmt();
			}*/

			/*Integer bankId = new Integer( header.getCustomerAccount().getBankCode() );

			List<com.eot.entity.ServiceChargeSplit> amountPercentList = 
					Util.getIntraOrInter(header) == CoreConstants.TXN_TYPE_INTRA ?
							serviceChargeDao.getAmountPercentListIntra(transactionType, bankId) : 
								serviceChargeDao.getAmountPercentListInter(transactionType);

							if(header.getTransactionType().equalsIgnoreCase(CoreConstants.TXN_TYPE_DEPOSIT)){
								stampFee = serviceChargeDao.getStampFeeFromServiceChargeSplit(transactionType, new Integer(header.getOtherAccount().getBankCode())).get(0).getServiceChargePct().doubleValue();
							}

							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_STAMP_FEE, stampFee );
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_TXN_AMT, header.getAmount());
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_SERVICE_CHARGE, serviceCharge);
							float percent = serviceChargeDao.getTaxFromServiceChargeSplit(transactionType, bankId).get(0).getServiceChargePct();
							Double taxAmount = (double) Math.round(((serviceCharge * percent) / ( percent + 100.0 )));
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_TAX_AMT, taxAmount );
							Double bankRevenue = serviceCharge - taxAmount;
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_BANK_REVENUE, bankRevenue);
							percent = getAndDeleteFromPercentList( CoreConstants.AMT_TYPE_EOT_SC_SHARE, amountPercentList);
							Double scShare = (double) (bankRevenue * (double)percent / 100);
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_EOT_SC_SHARE, scShare);
							for ( com.eot.entity.ServiceChargeSplit amountPercent : amountPercentList ) {

								if( CoreConstants.AMT_TYPE_INTER_BANK_FEE.equals( amountPercent.getAmountType() )) {

									Double interBankFee = bankRevenue * (double)amountPercent.getServiceChargePct() / 100;
									Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_INTER_BANK_FEE, interBankFee);
								}
							}
							
							 * Logic for calculating Stakeholder Service Charge split. 
							 * Commented out and maintained for future implementation.
							 
							BankDTO bankDTO = bankDao.getBankById( bankId );
				if( bankDTO.getAgreementModel() == CoreConstants.BANK_AGRREMENT_MODEL_ONE_TIME ) {
					serviceChargeSplitList = new ServiceChargeSplit[]{ new ServiceChargeSplit( bankDTO.getAccountNo(), serviceCharge) };
				}
				else {
					List<com.eot.entity.ServiceChargeSplit> serviceChargeSplit = 
						serviceChargeDao.getServiceChargeSplit( transactionType, bankId);
					serviceChargeSplitList = calculateServiceChargeSplit( serviceCharge, serviceChargeSplit);
					long sc = 0 ;
					for(ServiceChargeSplit split : serviceChargeSplitList){

						sc += split.getAmount() == null ? 0 : split.getAmount() ;
					}
					serviceCharge = sc ;
				}
		}

		if(header.getTransactionType().equals(CoreConstants.TXN_TYPE_ADJUSTMENT)){

			AdjustmentTransactionDTO adjustmentTransactionDTO = (AdjustmentTransactionDTO)header;

			transactionType = new Integer(adjustmentTransactionDTO.getTransactionTypeRef());

			Integer bankId = new Integer( header.getCustomerAccount().getBankCode() );

			serviceCharge = Double.parseDouble(adjustmentTransactionDTO.getFee());
			List<com.eot.entity.ServiceChargeSplit> amountPercentList = 
					Util.getIntraOrInter(header) == CoreConstants.TXN_TYPE_INTRA ?
							serviceChargeDao.getAmountPercentListIntra(transactionType, bankId) : 
								serviceChargeDao.getAmountPercentListInter(transactionType);

							if(header.getTransactionType().equalsIgnoreCase(CoreConstants.TXN_TYPE_DEPOSIT)){
								stampFee = serviceChargeDao.getStampFeeFromServiceChargeSplit(transactionType, new Integer(header.getOtherAccount().getBankCode())).get(0).getServiceChargePct().doubleValue();
							}

							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_STAMP_FEE, stampFee );
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_TXN_AMT, header.getAmount());
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_SERVICE_CHARGE, serviceCharge);
							float percent = serviceChargeDao.getTaxFromServiceChargeSplit(transactionType, bankId).get(0).getServiceChargePct();
							Double taxAmount = (double) Math.round(((serviceCharge * percent) / ( percent + 100.0 )));
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_TAX_AMT, taxAmount );
							Double bankRevenue = serviceCharge - taxAmount;
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_BANK_REVENUE, bankRevenue);
							percent = getAndDeleteFromPercentList( CoreConstants.AMT_TYPE_EOT_SC_SHARE, amountPercentList);
							Double scShare = (double) Math.round((bankRevenue * (double)percent / 100));
							Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_EOT_SC_SHARE, scShare);
							for ( com.eot.entity.ServiceChargeSplit amountPercent : amountPercentList ) {

								if( CoreConstants.AMT_TYPE_INTER_BANK_FEE.equals( amountPercent.getAmountType() )) {

									Double interBankFee = bankRevenue * (double)amountPercent.getServiceChargePct() / 100;
									Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_INTER_BANK_FEE, interBankFee);
								}
							}
		}

		if(header.getTransactionType().equalsIgnoreCase(CoreConstants.TXN_TYPE_DEPOSIT)){
			serviceCharge = serviceCharge+stampFee;
		}

		header.setServiceChargeImposedOn( serviceChargeImposedOn );
		header.setServiceChargeAmt( serviceCharge == null ? 0 : serviceCharge );
		Util.setAmountInHeader(header, CoreConstants.AMT_TYPE_SERVICE_CHARGE, 
				serviceCharge == null ? 0 : serviceCharge);
		header.setServiceChargeSplitList( serviceChargeSplitList );*/
	
		
		return serviceChargeDTO;
	}

	@Override
	public MasterDataDTO getServiceChargeCalculater(MasterDataDTO masterDataDTO) throws EOTException {
		
		return null;
	}
	private List<SCRuleDTO> getApplicableRules( Map<Integer, Map<Long, List<SCRuleDTO>>> ruleTypeMap, double transactionAmount ) throws CoreException {

		List<SCRuleDTO> applicableRules = new ArrayList<SCRuleDTO>();

		for (int j = ruleTypeMap.size(); j >= 1 ; j--) {

			Map<Long, List<SCRuleDTO>> serviceRuleIdMap = ruleTypeMap.get( j );
			if( serviceRuleIdMap.size() == 0 ) {
				continue;
			}
			for ( List<SCRuleDTO> scrRuleList: serviceRuleIdMap.values() ) {
				for (SCRuleDTO scRuleDTO : scrRuleList) {
					if( transactionAmount >= scRuleDTO.getMinTxnValue() && 
							transactionAmount <= scRuleDTO.getMaxTxnValue() ){
						applicableRules.add( scRuleDTO );
					}
				}
			}
			break;
		}

		return applicableRules;
	}

	private Double calculateServiceCharge(Double amount, Float serviceChargePct ,Long serviceChargeFxd, 
			Long discountLimit , Long minServiceCharge, Long maxServiceCharge){

		Double serviceChargeAmount = (( amount-discountLimit ) * serviceChargePct ) / 100F + serviceChargeFxd;

		if(serviceChargeAmount < minServiceCharge) {

			return (double)minServiceCharge;
		}
		else if(serviceChargeAmount > maxServiceCharge) {

			return(double)maxServiceCharge;
		}
		else 
		//	return (double)Math.ceil( serviceChargeAmount );
			return null !=serviceChargeAmount ? new Double(new DecimalFormat("#0.00").format(serviceChargeAmount.doubleValue())):0;

	}

	@Override
	public CustomerProfileDTO customerSelfOnboarding(CustomerProfileDTO customerDTO) throws Exception {

		Country country = null; 
		CustomerProfiles customerProfile= null;
		
		if(customerDTO.getCountryId() != null){
			country = baseServiceImpl.eotMobileDao.getCountryFromCountryCode(customerDTO.getCountryId());
		}else{
			//Default country for m-GURUSH is South Sudan
			country = baseServiceImpl.eotMobileDao.getCountry(Constants.DEFAULT_COUNTRY_ID);
		}
		
		customerDTO.setDefaultLocale(StringUtils.isNotEmpty(customerDTO.getDefaultLocale()) ? customerDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);
		Customer cust = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(customerDTO.getMobileNumber());
		if( cust != null ){
			throw new EOTException(ErrorConstants.MOBILE_NUMBER_REGISTERED_ALREADY);
		}
		
		/*Customer merchant = null;
		CustomerAccount merchantAccountDetails =null;
		if (StringUtils.isNotEmpty(customerDTO.getApplicationId())) {
			merchant = baseServiceImpl.eotMobileDao.getCustomer(customerDTO.getApplicationId());
			if (merchant == null) {
				throw new EOTException(ErrorConstants.INVALID_MERCHANT);
			}
			// customer registered for same country where the merchant
			// bellongs:bidyut
			country = merchant.getCountry();

			merchantAccountDetails = baseServiceImpl.eotMobileDao
					.getAccountFromCustomerId(merchant.getCustomerId());

			if (merchantAccountDetails == null) {
				throw new EOTException(ErrorConstants.INVALID_MERCHANT_ACCOUNT);
			}
			
			customerProfile=baseServiceImpl.eotMobileDao.getCustomerProfile(merchantAccountDetails.getBank().getBankId());
		}*/
		cust = new Customer();

		Integer loginPin = EOTUtil.generateLoginPin() ;
		Integer txnPin = EOTUtil.generateTransactionPin();
		String appID = EOTUtil.generateAppID();
		String uuid = EOTUtil.generateUUID();

		AppMaster app = new AppMaster();
		app.setAppId(appID);
		app.setReferenceId(cust.getCustomerId()+"");
		app.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		app.setStatus(Constants.APP_STATUS_NEW);
		app.setUuid(uuid);
		app.setAppVersion("1.0");

		Calendar cal = Calendar.getInstance();
		app.setCreatedDate(cal.getTime());
		Integer dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth+1);
		app.setExpiryDate(cal.getTime());

		baseServiceImpl.eotMobileDao.save(app);

		City city = new City();
		Quarter quarter = new Quarter();
		cust.setMobileNumber(customerDTO.getMobileNumber());
		cust.setEmailAddress(customerDTO.getEmailId());
		cust.setFirstName(customerDTO.getFirstName());
		cust.setMiddleName(customerDTO.getMiddleName());
		cust.setLastName(customerDTO.getLastName());
		cust.setDob(new Date(customerDTO.getDob()));

		cust.setAddress(customerDTO.getAddress());
		cust.setCountry(country);
		city.setCityId(customerDTO.getCityId());
		quarter.setQuarterId(customerDTO.getQaurterId()==null ? 1L: customerDTO.getQaurterId());
		cust.setCity(city);
		cust.setQuarter(quarter);
		cust.setType(Constants.REF_TYPE_CUSTOMER);
		cust.setActive(Constants.CUSTOMER_STATUS_NEW);

		cust.setTitle(customerDTO.getTitle());
		cust.setGender(customerDTO.getGender());
		cust.setDefaultLanguage(Constants.DEFAULT_LANGUAGE);

		cust.setLoginPin(HashUtil.generateHash(loginPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
		cust.setTransactionPin(HashUtil.generateHash(txnPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
		
		cust.setAppId(appID);
		cust.setCreatedDate(new Date());

		CustomerProfiles custProfile=new CustomerProfiles();
		custProfile.setProfileId(customerDTO.getProfileId() != null ? customerDTO.getProfileId() : Constants.DEFAULT_CUSTOMER_PROFILE);
		if(customerProfile!=null && customerDTO.getProfileId()==null)
			custProfile.setProfileId(customerProfile.getProfileId());
		cust.setCustomerProfiles(custProfile);

		baseServiceImpl.eotMobileDao.save(cust);
		app.setReferenceId(cust.getCustomerId().toString());
		baseServiceImpl.eotMobileDao.update(app);

		if(null !=customerDTO.getIdProof()) {
			
			CustomerDocument customerDocument = new CustomerDocument();
			if(customerDTO.getAddressProof()!=null)
			customerDocument.setSignaturePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getAddressProof())));
			
			customerDocument.setIdproofPhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getIdProof())));
			if(customerDTO.getCustomerPhoto()!=null)
				customerDocument.setProfilePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getCustomerPhoto())));
			if(customerDTO.getAddressProof()!=null)
				customerDocument.setAddressProof(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getAddressProof())));
			customerDocument.setCustomerId(cust.getCustomerId());
			customerDocument.setCustomer(cust);
	
			baseServiceImpl.eotMobileDao.save(customerDocument);
		}
		/* Security question and answer not needed
		 * CustomerSecurityAnswer answer = new CustomerSecurityAnswer();
		answer.setAnswer(customerDTO.getAnswer());
		answer.setCustomer(cust);
		SecurityQuestion securityQuestion = new SecurityQuestion();
		securityQuestion.setQuestionId(customerDTO.getQuestionId());
		answer.setSecurityQuestion(securityQuestion);*/
		
		//below bank id code is mobified by bidyut
		//perpous is to achive the corret bank registration for FiCustomer insted of default bank
		int bankId=Constants.DEFAULT_BANK;
		long branchId=Constants.DEFAULT_BRANCH;
		
		Bank custBank = null;
		/*if(StringUtils.isNotEmpty(customerDTO.getBankCode())){
			custBank = baseServiceImpl.eotMobileDao.getBankIdFromBankCode(customerDTO.getBankCode());
			bankId = null != custBank ? custBank.getBankId() : bankId;
		}
		
		if(null!=customerDTO.getBankId())
		{
			bankId=customerDTO.getBankId();
		}
		if(null!=customerDTO.getCbsBranchCode())
		{
			branchId=baseServiceImpl.eotMobileDao.getBranchId(bankId, customerDTO.getCbsBranchCode());
		}*/

		//baseServiceImpl.eotMobileDao.save(answer);
		Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(bankId);
	//	Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(customerDTO.getBankId() != null ? customerDTO.getBankId() : Constants.DEFAULT_BANK);
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(branchId);
	//	Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(customerDTO.getBranchId() != null ? customerDTO.getBranchId() : Constants.DEFAULT_BRANCH);

		Account account = new Account();
		account.setAccountNumber(EOTUtil.generateAccountNumber(baseServiceImpl.eotMobileDao.getNextAccountNumberSequence()));
		account.setAccountType(customerDTO.getAliasType() != null ? customerDTO.getAliasType() : Constants.ACCOUNT_TYPE_PERSONAL);
		account.setActive(Constants.APP_STATUS_ACTIVATED);
		String alias = Constants.ACCOUNT_ALIAS_CUSTOMER + " - " + bank.getBankName() ;
		account.setAlias(alias);
		account.setCurrentBalance(0.0);
		account.setCurrentBalanceType(Constants.ACCOUNT_BALANCE_TYPE_CREDIT);
		account.setReferenceId(cust.getCustomerId().toString());
		account.setReferenceType(Constants.REF_TYPE_CUSTOMER);

		try{
		baseServiceImpl.eotMobileDao.save(account);
		}catch (Exception e) {
//			e.printStackTrace();
			// TODO: handle exception
		}

		CustomerAccount customerAccount = new CustomerAccount();
		customerAccount.setAccount(account);
		customerAccount.setAccountNumber(account.getAccountNumber());
		customerAccount.setCustomer(cust);
		customerAccount.setBank(bank);
		customerAccount.setBranch(branch);

		baseServiceImpl.eotMobileDao.save(customerAccount);

		customerDTO.setAccountNumber(account.getAccountNumber());
		customerDTO.setCustomerId(cust.getCustomerId());

		AppLinkAlertDTO appAlertDto = new AppLinkAlertDTO();
		appAlertDto.setApplicationName("m-GURUSH");
		String appType = cust.getType().equals(Constants.REFERENCE_TYPE_CUSTOMER) ? Constants.APP_TYPE_CUSTOMER
				: cust.getType().equals(Constants.REFERENCE_TYPE_AGENT) ? Constants.APP_TYPE_AGENT
				: cust.getType().equals(Constants.REFERENCE_TYPE_MERCHANT) ? Constants.APP_TYPE_MERCHANT:"";

		appAlertDto.setDownloadLink(appConfigurations.getAppDownloadURL() + appType);
		
		
		appAlertDto.setLocale(cust.getDefaultLanguage());
		appAlertDto.setMobileNo(country.getIsdCode()+cust.getMobileNumber());
		appAlertDto.setScheduleDate(Calendar.getInstance());
		//smsServiceClientStub.appLinkAlert(appAlertDto);

		InitialTxnPinLoginPinAlertDTO pinDto = new InitialTxnPinLoginPinAlertDTO();
		pinDto.setLocale(cust.getDefaultLanguage());
		pinDto.setLoginPIN(loginPin.toString());
		pinDto.setMobileNo(country.getIsdCode()+cust.getMobileNumber());
		pinDto.setTxnPIN(txnPin.toString());
		pinDto.setScheduleDate(Calendar.getInstance());
		//smsServiceClientStub.initialTxnPinLoginPinAlert(pinDto);
		
		customerDTO.setStatus(0);
		customerDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTMER_REGISTRATION_SUCCESS", null, new Locale(cust.getDefaultLanguage())));
		return customerDTO;

	}

	@Override
	public MasterDataSelfOnboard fetchSelfOnboardMasterData() throws EOTException {
		
		MasterDataSelfOnboard  masterData = new MasterDataSelfOnboard();
		List<Country> countryModelList = baseServiceImpl.eotMobileDao.getCountries();
		List<KycType> kycTypeModelList = baseServiceImpl.eotMobileDao.getKycType();
		List<CountryDTO> countryDTOList=new ArrayList<CountryDTO>();
		List<KycTypeDTO> kycDTOList=new ArrayList<KycTypeDTO>();
		
		masterData.setDefaultLocale(StringUtils.isNotEmpty(masterData.getDefaultLocale()) ? masterData.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);
		
		for (Country country : countryModelList) {
			setCountries(masterData, country, countryDTOList);
		}
		
		for (KycType kycType : kycTypeModelList) {
			
			KycTypeDTO k = new KycTypeDTO();
			k.setKycTypeId(kycType.getKycTypeId());
			k.setKycTypeName(kycType.getKycDescription());
			kycDTOList.add(k);
		}
		
		masterData.setCountryList(countryDTOList);
		masterData.setKycTypeList(kycDTOList);
		masterData.setTitleList(setTitleList());
		return masterData;
	}

	private void setCountries(MasterDataSelfOnboard  masterData, Country country, List<CountryDTO> countryDTOList) {
		
			CountryDTO countryDTO = new CountryDTO(); 

			countryDTO.setCountryCodeAlpha2(country.getCountryCodeAlpha2());
			countryDTO.setCountryCodeAlpha3(country.getCountryCodeAlpha3());
			countryDTO.setCountryCodeNumeric(country.getCountryCodeNumeric());
			countryDTO.setCountryId(country.getCountryId());
			countryDTO.setCountryName(getCountryName(country, masterData.getDefaultLocale(), country.getCountryId()));
			countryDTO.setIsdCode(country.getIsdCode());
			countryDTO.setMobileNumberLength(country.getMobileNumberLength());
			countryDTO.setCityList(setCities(country.getCities()));
			countryDTOList.add(countryDTO);
			
	}
	
	private String getCountryName( Country country, String languageCode, int countryId) {
		
		Set<CountryNames> countryNames = country.getCountryNames();
		/*CountryNamesPK compKey=new CountryNamesPK();
		compKey.setCountryId(countryId);
		compKey.setLanguageCode(languageCode);*/
		
		for (CountryNames countryName : countryNames) {
			if (countryName.getComp_id().getCountryId().equals(countryId)
					&& countryName.getComp_id().getLanguageCode().equals(languageCode)) {
				return countryName.getCountryName();
			}
		}
		return null;
	}
	
	private List<CityDTO> setCities(Set<City> cityList1){
		
		List<CityDTO> cityDTOList=new ArrayList<CityDTO>();
		List<City> cityList = new ArrayList<City>(cityList1);	
		Collections.sort(cityList, this.new SortByAlphaCity());
		for (City city : cityList) {
			
			CityDTO c = new CityDTO();
			c.setCityId(city.getCityId());
			c.setCityName(city.getCity());
			c.setQuarterList(setQuarters(city.getQuarters()));
			cityDTOList.add(c);
		}
		return cityDTOList;
	}
	
	
	private List<QuarterDTO> setQuarters(Set<Quarter> quarterList1){
			
			List<QuarterDTO> quarterDTOList=new ArrayList<QuarterDTO>();
			List<Quarter> quarterList = new ArrayList<Quarter>(quarterList1);	
			Collections.sort(quarterList, this.new SortByAlphaQuarter());
			for (Quarter quarter : quarterList) {
				
				QuarterDTO q = new QuarterDTO();
				q.setQuarterId(quarter.getQuarterId());
				q.setQuarterName(quarter.getQuarter());
				quarterDTOList.add(q);
			}
			
			return quarterDTOList;
		}
	
	
	/*private float getAndDeleteFromPercentList( String amounType, 
			List<com.eot.entity.ServiceChargeSplit> amountPercentList) throws CoreException {

		for (com.eot.entity.ServiceChargeSplit amountPercent : amountPercentList) {

			if( amounType.equals(amountPercent.getAmountType()) ) {

				amountPercentList.remove( amountPercent );
				return amountPercent.getServiceChargePct();
			}
		}
		throw new EOTException( EOTConstants.INVALID_PARAMETERS_ERROR,
				"AmountType " + amounType + " not found in amountPercentList");
	}*/
	
	/**
	 * @author bidyut
	 * @param url
	 * @param obj
	 * @param type
	 * @return
	 */
	public <T extends com.eot.dtos.common.Header> T processRequest(String url, T obj,Class<T> type) 
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate= new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		obj.setRequestChannel(EOTConstants.REQUEST_CHANNEL_MOBILE);
		obj=restTemplate.postForObject(url, obj,  type);
		return obj;
		
	}
	private List<TitleDTO> setTitleList() {
		List<TitleDTO> titleList =new ArrayList<TitleDTO>();
		Map<Integer, String> titleMap = TitleEnum.getTitleMap();
		
		for (Map.Entry<Integer, String> entry : titleMap.entrySet()) {
			TitleDTO title=new TitleDTO();
			title.setTitleId(entry.getKey());
			title.setTitleName(entry.getValue());
			titleList.add(title);
		}
		
		return titleList;
	}
	class SortByAlphaCity implements Comparator<City> 
	{ 
	    // Used for sorting in ascending order of 
	    public int compare(City city1, City city2) 
	    { 
	        return city1.getCity().compareToIgnoreCase(city2.getCity()); 
	    } 
	} 
	class SortByAlphaQuarter implements Comparator<Quarter> 
	{ 
		@Override
		public int compare(Quarter o1, Quarter o2) {
			// TODO Auto-generated method stub
			return o1.getQuarter().compareToIgnoreCase(o2.getQuarter());
		} 
	}
	@Override
	public List<RecentRecipeintsDTO> getRecentReciepnts(RecentRecipeintsDTO recentRecipeintsDTO){
		
		Customer customer=baseServiceImpl.eotMobileDao.getCustomer(recentRecipeintsDTO.getApplicationId());
		
		List<Object[]> customers= baseServiceImpl.eotMobileDao.getRecentRecipeints(customer.getCustomerId());
		List<RecentRecipeintsDTO> recentReciepents= new ArrayList<>();
		RecentRecipeintsDTO dto=null;
		
		for(Object[] obj : customers){

			dto = new RecentRecipeintsDTO();
			dto = new RecentRecipeintsDTO();
			dto.setCustomerName((String)obj[3]);
			dto.setMobileNumber("0"+(String)obj[1]);
			recentReciepents.add(dto);

		}
		/*for (Customer customer2 : customers) {
			dto = new RecentRecipeintsDTO();
			dto.setCustomerName(customer2.getFirstName()+" "+customer2.getLastName());
			dto.setMobileNumber(customer2.getMobileNumber());
			
			recentReciepents.add(dto);
		}*/
		return recentReciepents;
	}
	
	
	
	
	
	
	
	
	@Override
	public CustomerProfileDTO updateKYC(CustomerProfileDTO customerDTO) throws Exception {

		Country country = null; 
		CustomerProfiles customerProfile= null;
		
		
		
		
		Customer cusotmer = baseServiceImpl.eotMobileDao.getCustomerByMobile(customerDTO.getMobileNumber());
		if( cusotmer == null ){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		if(cusotmer.getActive().intValue()==Constants.CUSTOMER_STATUS_DEACTIVATED)
			throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
		Customer agent;
		if (StringUtils.isNotEmpty(customerDTO.getApplicationId())) {
			agent = baseServiceImpl.eotMobileDao.getCustomer(customerDTO.getApplicationId());
			if (agent == null) {
				throw new EOTException(ErrorConstants.INVALID_MERCHANT);
			}
			
		}/*else
		{
			customerProfile=baseServiceImpl.eotMobileDao.getSelfRegisterProfile();
		}*/
		cusotmer.setTitle(customerDTO.getTitle());
		cusotmer.setAddress(customerDTO.getAddress());
		cusotmer.setGender(customerDTO.getGender());
		baseServiceImpl.eotMobileDao.update(cusotmer);
		
		CustomerDocument customerDocument= cusotmer.getCustomerDocument();
		/*
		 * if(!cusotmer.getOnbordedBy().equalsIgnoreCase("self")) { throw new
		 * EOTException(ErrorConstants.CUSTOMR_DOCUMENT_ALREADY_EXIST); }
		
		if(customerDocument.getSignaturePhoto()!=null)
		{
			throw new EOTException(ErrorConstants.CUSTOMR_DOCUMENT_ALREADY_EXIST);
		} */
		if ( null != customerDTO.getIdProof() &&  !"".equals(customerDTO.getIdProof() )) 
		{
			//CustomerDocument customerDocument = new CustomerDocument();
			if (customerDTO.getAddressProof()!= null) 
				customerDocument.setSignaturePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getAddressProof())));
			customerDocument.setIdproofPhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getIdProof())));
			if(customerDTO.getCustomerPhoto()!=null)
				customerDocument.setProfilePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getCustomerPhoto())));
			if(customerDTO.getAddressProof()!=null)
				customerDocument.setAddressProof(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getAddressProof())));
			customerDocument.setCustomerId(cusotmer.getCustomerId());
			customerDocument.setCustomer(cusotmer);
			baseServiceImpl.eotMobileDao.save(customerDocument);
		}
		
		
		customerDTO.setStatus(0);
		customerDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("KYC_UPDATE_SUCCESS", null, new Locale(cusotmer.getDefaultLanguage())));
		customerDTO.setMessageDescription(baseServiceImpl.messageSource.getMessage("KYC_UPDATE_SUCCESS", null, new Locale(cusotmer.getDefaultLanguage())));
		return customerDTO;

	}
	
	@Override
	public CustomerProfileDTO fetchCustomer(CustomerProfileDTO customerDTO) throws Exception {

		
		Customer cusotmer = baseServiceImpl.eotMobileDao.getCustomerByMobile(customerDTO.getMobileNumber());
		if( cusotmer == null ){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		if(cusotmer.getActive().intValue()==Constants.CUSTOMER_STATUS_DEACTIVATED)
			throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
		
		customerDTO.setStatus(0);
		customerDTO.setCustomerName(cusotmer.getFirstName()+" "+cusotmer.getLastName());
		return customerDTO;

	}
	

@SuppressWarnings("unused")
@Override
public TransactionBaseDTO processForgetPin(TransactionBaseDTO transactionBaseDTO) throws EOTException,Exception {
	
	String mobileNumber = transactionBaseDTO.getMobileNumber();	
	if(mobileNumber != null) {		
		Country	country = baseServiceImpl.eotMobileDao.getCountry(Constants.DEFAULT_COUNTRY_ID);		
		String cellNumber = country.getIsdCode().toString().concat(mobileNumber);
		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(cellNumber);		
		if( customer != null ){
			CustomerAccount customerAccountDetails = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(customer.getCustomerId());					
			if (customerAccountDetails == null) {
				
				if ( customer.getType().equals(Constants.REF_TYPE_CUSTOMER))
						throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
				else if ( customer.getType().equals(Constants.REF_TYPE_AGENT))
						throw new EOTException(ErrorConstants.INVALID_AGENT_ACCOUNT);
				else if ( customer.getType().equals(Constants.REF_TYPE_MERCHANT))
						throw new EOTException(ErrorConstants.INVALID_MERCHANT_ACCOUNT);
			}else {	
					int referenceType = customer.getType();
					WebOTPAlertDTO dto=new WebOTPAlertDTO();
					dto.setLocale(customer.getDefaultLanguage());
					dto.setMobileNo(cellNumber);
					dto.setOtpType(Constants.OTP_TYPE_FORGOT_PIN);
					dto.setReferenceId(customer.getCustomerId().toString());
				//	dto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
					dto.setReferenceType(referenceType);
					dto.setScheduleDate(Calendar.getInstance());
					
					SmsResponseDTO responseDTO=sendSMS(UrlConstants.WEB_OTP_ALERT, dto);
					if(responseDTO.getStatus().equalsIgnoreCase("0"))
						throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
					/*
					 * try { smsServiceClientStub.webOTPAlert(dto); }catch (SmsServiceException e) {
					 * throw new EOTException(ErrorConstants.SMS_ALERT_FAILED); }
					 */
			}
		}else
			throw new EOTException(ErrorConstants.USER_NOT_EXISTS);
		transactionBaseDTO.setStatus(0);
		transactionBaseDTO.setMessageDescription("OTP sent to Mobile Number "+mobileNumber);
	}else
		throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Mobile Number");
	return transactionBaseDTO;
}

@Override
public ConfirmPinDTO processConfirmPin(ConfirmPinDTO confirmPinDTO) throws EOTException, Exception {
	
	 String newPin = confirmPinDTO.getNewPin();	
	 String otp = confirmPinDTO.getOtp();
	 String mobileNumber = confirmPinDTO.getMobileNumber();
	 
	if (null == newPin || newPin.equals("")) 			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "New Pin");
	else if (null == otp || otp.equals(""))			
		throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "OTP");
	else if (null == mobileNumber || mobileNumber.equals(""))		
		throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Mobile Number");
	
	Country	country = baseServiceImpl.eotMobileDao.getCountry(Constants.DEFAULT_COUNTRY_ID);
	mobileNumber = country.getIsdCode().toString().concat(mobileNumber);
	Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(mobileNumber);
	if( customer != null ){
		CustomerAccount customerAccountDetails = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(customer.getCustomerId());					
		if (customerAccountDetails == null) {
			
			if ( customer.getType().equals(Constants.REF_TYPE_CUSTOMER))
					throw new EOTException(ErrorConstants.INVALID_CUSTOMER_ACCOUNT);
			else if ( customer.getType().equals(Constants.REF_TYPE_AGENT))
					throw new EOTException(ErrorConstants.INVALID_AGENT_ACCOUNT);
			else if ( customer.getType().equals(Constants.REF_TYPE_MERCHANT))
					throw new EOTException(ErrorConstants.INVALID_MERCHANT_ACCOUNT);
		}
		String referenceId = customer.getCustomerId().toString();
		Otp otp1 = baseServiceImpl.eotMobileDao.getOtp(referenceId);
		String actualOtp = otp1 != null ? otp1.getOtpHash() :"";
		if(actualOtp.equals(otp)) {
			customer.setLoginPin(newPin);
			baseServiceImpl.eotMobileDao.update(customer);
		}else
			throw new EOTException(ErrorConstants.INVALID_OTP);
	}else 
		throw new EOTException(ErrorConstants.USER_NOT_EXISTS);
	confirmPinDTO.setStatus(0);
//	String message = customer.getType() == 0 ? "Login pin changed Successfully" : "Pin changed Successfully";
	String message = "Pin changed successfully";
	confirmPinDTO.setMessageDescription(message);
	return confirmPinDTO;
}

	@Override
	public OTPDTO sendOtpForRegistration(OTPDTO otpDTO) throws EOTException, Exception {
		
		
		String mobileNumber = otpDTO.getMobileNumber();
		
		if (!StringUtils.isNotEmpty(mobileNumber)) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Mobile Number");
		}
		
		//WebUser webUser = baseServiceImpl.eotMobileDao.getUserByMobileNumber(mobileNumber);
		
		/*
		 * if (webUser !=null) { throw new
		 * EOTException(ErrorConstants.MOBILE_NUMBER_REGISTERED_ALREADY); }
		 */
		
		Country	country = baseServiceImpl.eotMobileDao.getCountry(Constants.DEFAULT_COUNTRY_ID);
		mobileNumber = country.getIsdCode().toString().concat(mobileNumber);
		
		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(mobileNumber);
		if (customer !=null) {
			throw new EOTException(ErrorConstants.MOBILE_NUMBER_REGISTERED_ALREADY);
		}
		

		WebOTPAlertDTO dto = new WebOTPAlertDTO();
		dto.setLocale(baseServiceImpl.defaultLocale);
		dto.setMobileNo(mobileNumber);
		dto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
		dto.setReferenceId(mobileNumber);
		dto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		dto.setScheduleDate(Calendar.getInstance());
		SmsResponseDTO responseDTO=sendSMS(UrlConstants.WEB_OTP_ALERT, dto);
		if(responseDTO.getStatus().equalsIgnoreCase("0"))
			throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);

		/*
		 * try { smsServiceClientStub.webOTPAlert(dto); } catch (SmsServiceException e)
		 * { throw new EOTException(ErrorConstants.SMS_ALERT_FAILED); }
		 */
	
		
		otpDTO.setStatus(0);
		otpDTO.setMessageDescription("OTP sent to Mobile Number "+mobileNumber);
		
		return otpDTO;
	}
	
	private void validateOtp(CustomerProfileDTO customerDTO, String mobileWithISD)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, EOTException {
		OtpDTO otpDto=new OtpDTO();
		
		otpDto.setOtphash(customerDTO.getCustomerOTP());
		otpDto.setReferenceId(mobileWithISD);
		otpDto.setReferenceType( Constants.REF_TYPE_CUSTOMER);
		otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
		Otp otp =	baseServiceImpl.eotMobileDao.verifyOTP(otpDto);
		if(otp== null){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
		}
	}

	@Override
	public ReportsModel txnReports(ReportsModel transactionBaseDTO) throws EOTException, Exception {
		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByAppId(transactionBaseDTO.getApplicationId());
		Integer bankId = null;
		if( customer == null ){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		transactionBaseDTO.setMobileNumber(customer.getMobileNumber());
		CustomerAccount customerAccountDetails = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(customer.getCustomerId());
		bankId = customerAccountDetails.getBank().getBankId();
		ReportsModel returnModel = new ReportsModel();
		List<Object[]> transactionList =   baseServiceImpl.eotMobileDao.getTransactionsReports(customerAccountDetails.getAccountNumber(),bankId, transactionBaseDTO,customer.getType());
		if(transactionList.size()==0){
			throw new EOTException(ErrorConstants.DATA_NOT_AVAILABLE);
		}
		TransactionBaseDTO txnBaseDto = new TransactionBaseDTO(); 
		Iterator<Object[]> iterator = transactionList.iterator(); 
		/*SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");*/
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		List<com.eot.banking.dto.ReportsModel.Transaction> transactions  = new ArrayList<com.eot.banking.dto.ReportsModel.Transaction>();
		com.eot.banking.dto.ReportsModel.Transaction transaction=null;
		ReportsModel returnModel1 = new ReportsModel();

		for(Object[] obj : transactionList) {
			
			     String fullname = (String)obj[1];
			     String[] labels = fullname.split("/");
			     String txnLabel = labels.length>0?labels[0].trim():"";
			     String mobile = "",txnId="",res="";
			     if(!txnLabel.equals("Reversal")){
			     mobile = labels.length>1?labels[1].trim():"";
			     txnId = labels.length>2?labels[2].trim():"";
			}else{
			     res = labels.length>1?labels[1].trim():"";
				 txnLabel = txnLabel+"/"+res;
				 mobile   = labels.length>2?labels[2].trim():"";
				 txnId    = labels.length>3?labels[3].trim():"";
			}
			String dcName = (String)obj[2];
			if(dcName.startsWith("D")){dcName = "Debit";}else{dcName = "Credit";}
			
			
			transaction = returnModel1. new Transaction();
			Calendar calendarObj = Calendar.getInstance();
			calendarObj.setTime( (Date)obj[0] );
			String dt = (DateUtil.dateAndTime((Date)obj[0]));
			Date date = formatter.parse(dt);
			long millis = date.getTime();
			transaction.setTxnDate(millis);
			
			
			transaction.setTransactionLabels(txnLabel);
			transaction.setMobileNumber(mobile);
			transaction.setTxnId(txnId);
			transaction.setDebitCredit(dcName);
			transaction.setAmount((Double)obj[3]);
			/*transaction.setFromAccountBalalnce((Double)obj[4]);
			transaction.setToAccountBalalnce((Double)obj[5]);*/

			transactions.add(transaction);
		}
		
		returnModel.setTransactions(transactions);
		returnModel.setStatus(0);
		returnModel.setTransactionType(EOTConstants.TRANSACTION_REPORT);
		String message = "";
		returnModel.setMessageDescription(message);
		return returnModel;
	}
	
	public static String  SetTransactiontypeLabel(Integer txnType){
		String label = "";
		switch (txnType) {
		case 30 :
			return "Balance Enquiry";
		case 35 :
			return "Mini Statement";
		case 55 :
			return "Send Money";
		case 82 :
			return "Bill Payment";
		case 80 :
			return "Buy Airtime";
		case 83 :
			return "SMS Cash";
		case 128 :
			return "m-GURUSH Pay";
		case 90 :
			return "Sale";
		case 126 :
			return "SMS Cash Receive";
		case 115 :
			return "Cash Deposit";
		case 116 :
			return "Cash Withdrawal";
		case 133 :
			return "Transfer Float";
		case 140 :
			return "Merchant Payout";
		case 120 :
			return "Commission Share";
		
		default:
		return "No Label Define";
			
		}
	}
	
	@Override
	public UploadCustomerDocument updoadDocument(UploadCustomerDocument uploadCustomerDocument) throws EOTException {

		boolean finalStatus=false;
		uploadCustomerDocument.setCustomerExist(true);
		uploadCustomerDocument.setDataUploaded(finalStatus);
	//	baseServiceImpl.handleRequest(uploadCustomerDocument);
		Customer agent = baseServiceImpl.eotMobileDao.getCustomer(uploadCustomerDocument.getApplicationId());
		Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(agent.getCountry().getIsdCode()+uploadCustomerDocument.getMobileNumber());
		if(customer==null) {
			uploadCustomerDocument.setDataUploaded(finalStatus);
			
			IncorrectKycDetails incorrectKycDetails = baseServiceImpl.eotMobileDao.getIncorrectKycByMobileNumber(uploadCustomerDocument.getMobileNumber());
			if(incorrectKycDetails==null)
			{
				incorrectKycDetails= new IncorrectKycDetails();
				incorrectKycDetails.setMobileNumber(uploadCustomerDocument.getMobileNumber());
				baseServiceImpl.eotMobileDao.save(incorrectKycDetails);
			}
				
			try {
				switch (uploadCustomerDocument.getDataName()) {
				case EOTConstants.PROFILE_PIC:
					incorrectKycDetails = baseServiceImpl.eotMobileDao.getIncorrectKycByMobileNumber(uploadCustomerDocument.getMobileNumber());
					incorrectKycDetails.setProfilePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
					baseServiceImpl.eotMobileDao.update(incorrectKycDetails);
					uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
					finalStatus=true;
					uploadCustomerDocument.setDataUploaded(finalStatus);
					uploadCustomerDocument.setStatus(0);
					break;
				case EOTConstants.SIGNATURE_PIC:
					incorrectKycDetails = baseServiceImpl.eotMobileDao.getIncorrectKycByMobileNumber(uploadCustomerDocument.getMobileNumber());
					incorrectKycDetails.setSignaturePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
					uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
					baseServiceImpl.eotMobileDao.update(incorrectKycDetails);
					finalStatus=true;
					uploadCustomerDocument.setDataUploaded(finalStatus);
					uploadCustomerDocument.setStatus(0);
					break;
				case EOTConstants.ADDRESS_PROOF_PIC:
					incorrectKycDetails = baseServiceImpl.eotMobileDao.getIncorrectKycByMobileNumber(uploadCustomerDocument.getMobileNumber());
					incorrectKycDetails.setAddressProofPhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
					baseServiceImpl.eotMobileDao.update(incorrectKycDetails);
					uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
					finalStatus=true;
					uploadCustomerDocument.setDataUploaded(finalStatus);
					uploadCustomerDocument.setStatus(0);
					break;
				case EOTConstants.ID_PROOF_PIC:
					incorrectKycDetails = baseServiceImpl.eotMobileDao.getIncorrectKycByMobileNumber(uploadCustomerDocument.getMobileNumber());
					incorrectKycDetails.setIdProofPhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
					baseServiceImpl.eotMobileDao.update(incorrectKycDetails);
					uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
					uploadCustomerDocument.setStatus(0);
					finalStatus=true;
					uploadCustomerDocument.setDataUploaded(finalStatus);
					break;

				case EOTConstants.COLUMN_FORM_DATA:
					incorrectKycDetails = baseServiceImpl.eotMobileDao.getIncorrectKycByMobileNumber(uploadCustomerDocument.getMobileNumber());
					CustomerProfileDTO customerData=(CustomerProfileDTO) new JSONAdaptor().fromJSON(new String(uploadCustomerDocument.getData()), CustomerProfileDTO.class);
					incorrectKycDetails.setTitle(customerData.getTitle());
					incorrectKycDetails.setGender(customerData.getGender());
					incorrectKycDetails.setAddress(customerData.getAddress());
					
					City city = new City();
					city.setCityId(customerData.getCityId());
					incorrectKycDetails.setCity(city);
					
					Quarter quarter = new Quarter();
					quarter.setQuarterId(customerData.getQaurterId());
					incorrectKycDetails.setQuarter(quarter);
					uploadCustomerDocument.setStatus(0);
					
					baseServiceImpl.eotMobileDao.update(incorrectKycDetails);
					finalStatus=true;
					uploadCustomerDocument.setDataUploaded(finalStatus);

					break;
				}
			
			
			uploadCustomerDocument.setCustomerExist(true);
			return uploadCustomerDocument;
		}catch (Exception e) {
//			e.printStackTrace();
		}
		}
		if(customer.getType()!=0) {
			uploadCustomerDocument.setCustomerExist(false);
			finalStatus=true;
			uploadCustomerDocument.setStatus(0);
			uploadCustomerDocument.setDataUploaded(finalStatus);
			return uploadCustomerDocument;
		}
		if(customer.getKycStatus()==11) {
			uploadCustomerDocument.setCustomerExist(false);
			finalStatus=true;
			uploadCustomerDocument.setStatus(0);
			uploadCustomerDocument.setDataUploaded(finalStatus);
			return uploadCustomerDocument;
		}

		
		CustomerDocument customerDocument = baseServiceImpl.eotMobileDao.loadCustomerDocumentByCustomerId(customer.getCustomerId());
		if(customerDocument==null)
		{
			customerDocument= new CustomerDocument();
			customerDocument.setCustomerId(customer.getCustomerId());
			customerDocument.setCustomer(customer);
			customerDocument.setSignaturePhoto(Hibernate.createBlob("".getBytes()));
			customerDocument.setIdproofPhoto(Hibernate.createBlob("".getBytes()));
			customerDocument.setProfilePhoto(Hibernate.createBlob("".getBytes()));
			customerDocument.setAddressProof(Hibernate.createBlob("".getBytes()));
			baseServiceImpl.eotMobileDao.save(customerDocument);
		}
		/*try {
			if(customerDocument.getProfilePhoto().getBinaryStream().toString().length()!=37 && customerDocument.getAddressProof().getBinaryStream().toString().length()!=37 && customerDocument.getIdproofPhoto().getBinaryStream().toString().length()!=37 &&customerDocument.getSignaturePhoto().getBinaryStream().toString().length()!=37)
				finalStatus=true;
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}*/
			/*if(customerDocument.getAddressProof()!=null)
				finalStatus=true;
			if(customerDocument.getIdproofPhoto()!=null)
				finalStatus=true;
			if(customerDocument.getSignaturePhoto()!=null)
				finalStatus=true;*/
			
		uploadCustomerDocument.setDataUploaded(finalStatus);
		try {
			switch (uploadCustomerDocument.getDataName()) {
			case EOTConstants.PROFILE_PIC:
				customerDocument.setProfilePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
				baseServiceImpl.eotMobileDao.update(customerDocument);
				uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
				finalStatus=true;
				uploadCustomerDocument.setDataUploaded(finalStatus);
				uploadCustomerDocument.setStatus(0);
				break;
			case EOTConstants.SIGNATURE_PIC:
				customerDocument.setSignaturePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
				uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
				baseServiceImpl.eotMobileDao.update(customerDocument);
				finalStatus=true;
				uploadCustomerDocument.setDataUploaded(finalStatus);
				uploadCustomerDocument.setStatus(0);
				break;
			case EOTConstants.ADDRESS_PROOF_PIC:
				customerDocument.setAddressProof(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
				baseServiceImpl.eotMobileDao.update(customerDocument);
				uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
				finalStatus=true;
				uploadCustomerDocument.setDataUploaded(finalStatus);
				uploadCustomerDocument.setStatus(0);
				break;
			case EOTConstants.ID_PROOF_PIC:
				customerDocument.setIdproofPhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(uploadCustomerDocument.getData())));
				baseServiceImpl.eotMobileDao.update(customerDocument);
				uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_SUCCESS", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
				uploadCustomerDocument.setStatus(0);
				finalStatus=true;
				uploadCustomerDocument.setDataUploaded(finalStatus);
				break;

			case EOTConstants.COLUMN_FORM_DATA:
				CustomerProfileDTO customerData=(CustomerProfileDTO) new JSONAdaptor().fromJSON(new String(uploadCustomerDocument.getData()), CustomerProfileDTO.class);
				customer.setTitle(customerData.getTitle());
				customer.setGender(customerData.getGender());
				customer.setAddress(customerData.getAddress());
				City city = new City();
				city.setCityId(customerData.getCityId());
				customer.setCity(city);
				
				System.out.println("tittle : "+ customerData.getTitle());
				System.out.println("Gender: "+ customerData.getGender());
				Quarter quarter = new Quarter();
				quarter.setQuarterId(customerData.getQaurterId());
				customer.setQuarter(quarter);
				uploadCustomerDocument.setStatus(0);
				
				baseServiceImpl.eotMobileDao.update(customer);
				
				customerDocument.setIdType(customerData.getIdType());
				baseServiceImpl.eotMobileDao.update(customerDocument);
				
				break;
			}
			if(customerDocument.getIdproofPhoto().length() != 0 && customerDocument.getProfilePhoto().length() != 0) {
				customer.setKycStatus(EOTConstants.KYC_STATUS_APPROVE_PENDING);
				baseServiceImpl.eotMobileDao.update(customer);
			}
		}catch (Exception e) {
			uploadCustomerDocument.setStatus(1);
			uploadCustomerDocument.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CUSTOMER_DOCUMENT_UPLOAD_FAIL", new String[]{uploadCustomerDocument.getDataName()}, new Locale(baseServiceImpl.defaultLocale.toString())));
		}

		return uploadCustomerDocument;
	}

	@Override
	public CustomerModel CustomerReports(CustomerModel customerReportDTO) throws EOTException, Exception {
		Customer customer = null;
		Integer type = 0;
		String onBoardedBy = null;
		
		if(customerReportDTO.getApplicationId()!=null && customerReportDTO.getApplicationId().equals("")==false){
		   customer = baseServiceImpl.eotMobileDao.getCustomerByAppId(customerReportDTO.getApplicationId());
		   onBoardedBy = customer.getAgentCode();
		}
		
		if(customerReportDTO.getMobileNumber()!=null && customerReportDTO.getMobileNumber().equals("")==false){
		   customer = baseServiceImpl.eotMobileDao.getCustomerByMobile(customerReportDTO.getMobileNumber());
		   if( customer == null ){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		   if(customer.getType().equals(0)==false){
			   throw new EOTException(ErrorConstants.NOT_CUSTOMER);
		   }
		}
		CustomerModel customerModel = new CustomerModel();
		List<Customer> customersList =   baseServiceImpl.eotMobileDao.getCustomerReports(type, customerReportDTO,onBoardedBy);
		if(customersList.size()==0){
			throw new EOTException(ErrorConstants.DATA_NOT_AVAILABLE);
		}
		List<CustomerModel.Customers> customerReportList  = new ArrayList<CustomerModel.Customers>();	
		 for(Customer customerData : customersList){
			
			CustomerModel.Customers customers = new CustomerModel().new Customers(); 
			customers.setFirstName(customerData.getFirstName()+" "+customerData.getLastName());
			customers.setMobileNumber(customerData.getMobileNumber());
			customers.setCustomerKycStatus(customerData.getKycStatus());
			customers.setCreatedDate(customerData.getCreatedDate().getTime());
			
			customerReportList.add(customers);
			
		 }
		 	customerModel.setTransactions(customerReportList);
		 	customerModel.setStatus(0);
		 	customerModel.setTransactionType(EOTConstants.CUSTOMER_REPORT);
			String message = "Data successfully loaded.";
			customerModel.setMessageDescription(message);
			return customerModel;
	}
	public SmsResponseDTO sendSMS(String url,SmsHeader smsHeader)
	{
		SmsResponseDTO dto= new SmsResponseDTO();
		try {
			dto= 	restTemplate.postForObject(url, smsHeader, SmsResponseDTO.class);
		}catch (Exception e) {
			dto.setStatus("0");
//			e.printStackTrace();
		}
		return dto;
	}
	
	public List<HelpDesk> getHelpDeskDetails() {
		return baseServiceImpl.eotMobileDao.getAllHelpDeskList();
	}

	@Override
	public List<ReversalTransactionDTO> getTransactionforReversal(WithdrawalTransactionsDTO withdrawalTransactionsDTO)
			throws EOTException {
		List<ReversalTransactionDTO> withdrawalTransactions = baseServiceImpl.eotMobileDao.getTransactionforReversal(withdrawalTransactionsDTO);
		if(CollectionUtils.isEmpty(withdrawalTransactions)){
			 throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);
		}
		return withdrawalTransactions;
	}
	
}