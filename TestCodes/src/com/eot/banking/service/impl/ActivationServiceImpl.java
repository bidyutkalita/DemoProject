/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: ActivationServiceImpl.java,v 1.0
 *
 * Date Author Changes
 * 3 Nov, 2015, 1:23:08 PM Sambit Created
 */
package com.eot.banking.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eot.banking.common.CoreUrls;
import com.eot.banking.common.EOTConstants;
import com.eot.banking.common.TitleEnum;
import com.eot.banking.dto.LocateUsDTO;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.entity.Currency;
import com.eot.entity.Customer;
import com.eot.banking.dto.PayeeDTO;
import com.eot.banking.dto.SecurityQuestionDTO;
import com.eot.banking.dto.TitleDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.MasterDataDTO.LanguageData;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.ActivationService;
import com.eot.banking.service.impl.OtherBankingServiceImpl.SortByAlphaCity;
import com.eot.banking.service.impl.OtherBankingServiceImpl.SortByAlphaQuarter;
import com.eot.banking.utils.DateUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.BillerTypes;
import com.eot.entity.Branch;
import com.eot.entity.City;
import com.eot.entity.Country;
import com.eot.entity.CountryNames;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.HelpDesk;
import com.eot.entity.KycType;
import com.eot.entity.Language;
import com.eot.entity.LocationType;
import com.eot.entity.MobileRequest;
import com.eot.entity.NetworkType;
import com.eot.entity.Operator;
import com.eot.entity.OperatorDenomination;
import com.eot.entity.Payee;
import com.eot.entity.Quarter;
import com.eot.entity.SecurityQuestion;
import com.eot.entity.Transaction;
import com.eot.entity.TransactionType;

// TODO: Auto-generated Javadoc
/**
 * The Class ActivationServiceImpl.
 */
@Service
public class ActivationServiceImpl extends BaseServiceImpl implements ActivationService{

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;
	
	/** The base service impl. */
	@Autowired
	private BaseServiceImpl baseServiceImpl;

	/* (non-Javadoc)
	 * @see com.eot.service.ActivationService#processActivation(com.eot.dto.TransactionRequestBaseDTO)
	 */

	@Override
	public void handleRequest(TransactionBaseDTO transactionBaseDTO) throws EOTException {

		transactionBaseDTO.setDefaultLocale(defaultLocale);
		Integer transactionType = transactionBaseDTO.getTransactionType();
		String applicationId = new String(transactionBaseDTO.getApplicationId());

		Date transmissionTime = new Date(transactionBaseDTO.getTransmissionTime());
		Date transactionTime = new Date(transactionBaseDTO.getTransactionTime());
		Long stan = new Long(transactionBaseDTO.getStan());
		Long rrn = new Long(transactionBaseDTO.getRrn());

		appMaster = eotMobileDao.getApplicationType(applicationId); // Check in appmaster table for appid

		if (appMaster == null){
			throw new EOTException(ErrorConstants.INVALID_APPLICATION);
		}

		referenceType  = Integer.valueOf(appMaster.getReferenceType());

		if(appMaster.getReferenceType()== Constants.REF_TYPE_CUSTOMER || 
				appMaster.getReferenceType()== Constants.REF_TYPE_MERCHANT ||
				appMaster.getReferenceType()== Constants.REF_TYPE_AGENT){  // Check in customer/merchant table with appid

			customer = eotMobileDao.getCustomer(applicationId);

			if(customer == null){
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
			}
		/*	if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED ){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
			}*/
			
			if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED ){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.Y_CUSTOMER_ACC_DEACTIVATED);
			}else if( customer.getActive() == Constants.CUSTOMER_STATUS_SUSPENDED && customer.getType()==EOTConstants.REFERENCE_TYPE_CUSTOMER){  // validate status of merchant/customer
				throw new EOTException(ErrorConstants.CUSTOMER_ACC_SUSPENDED);
			}
			if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
				throw new EOTException(ErrorConstants.AGENT_KYC_PENDING);
			}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_AGENT){
				throw new EOTException(ErrorConstants.AGENT_KYC_REJECTED);
			}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_PENDING && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT){
				throw new EOTException(ErrorConstants.MERCHANT_KYC_PENDING);
			}else if(customer.getKycStatus() == EOTConstants.KYC_STATUS_REGEJETED && customer.getType()==EOTConstants.REFERENCE_TYPE_MERCHANT)
				throw new EOTException(ErrorConstants.MERCHANT_KYC_REJECTED);
			

			List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

			if(accountList.size() == 0 ){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			if(accountList.get(0).getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
				throw new EOTException(ErrorConstants.INACTIVE_BANK);
			}

		}

		/* Now there will be playstore no appstore
		 * 
		 * if( appMaster.getStatus() != Constants.APP_STATUS_DOWNLOADED && 
				appMaster.getStatus() != Constants.APP_STATUS_ACTIVATION_SC_DEBITED ){  
			throw new EOTException(ErrorConstants.INVALID_APPLICATION_STATE);
		}
*/
		if(!(DateUtil.formatDate(transmissionTime).equals(DateUtil.formatDate(new Date())) 
				&& DateUtil.formatDate(transactionTime).equals(DateUtil.formatDate(new Date())))){  
			throw new EOTException(ErrorConstants.INVALID_DATE);
		}

		mobileRequest = eotMobileDao.getRequest(applicationId ,stan ,rrn ,DateUtil.formatDate(new Date())); // Get the request from MobileRequest log table.

		if ( mobileRequest != null ){
			if( transmissionTime.getTime() <= mobileRequest.getTransmissionTime().getTime() ){  
				throw new EOTException(ErrorConstants.INVALID_TIME);

			}
		}else {

			mobileRequest = new MobileRequest();

			TransactionType txnType = new TransactionType();
			txnType.setTransactionType(transactionType);
			mobileRequest.setTransactionType(txnType);

			mobileRequest.setRequestString(Hibernate.createBlob(transactionBaseDTO.getEncPayload()));
			mobileRequest.setRrn(rrn);
			mobileRequest.setStan(stan);
			mobileRequest.setTransactionTime(new Date());
			mobileRequest.setTransmissionTime(transmissionTime);
			mobileRequest.setAppMaster(appMaster);
			mobileRequest.setReferenceId(appMaster.getReferenceId());
			mobileRequest.setReferenceType(appMaster.getReferenceType());
			mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_LOGGED);

			eotMobileDao.save(mobileRequest);

			transactionBaseDTO.setRequestID(mobileRequest.getRequestId());
			transactionBaseDTO.setEncPayload(null);

		}

		requestID = mobileRequest.getRequestId();

	}

	/**
	 * Process activation request.
	 *
	 * @param masterDataDTO the master data DTO
	 * @return the master data DTO
	 * @throws EOTException the EOT exception
	 */
	/* (non-Javadoc)
	 * @see com.eot.service.ActivationService#processActivationRequest(com.eot.dto.ActivationDTO)
	 */
	public MasterDataDTO processActivationRequest( MasterDataDTO masterDataDTO ) throws EOTException {

		this.handleRequest(masterDataDTO);
		String userPinHash = masterDataDTO.getActivationPIN();
		customer = eotMobileDao.getCustomer(masterDataDTO.getApplicationId());

		if( ! userPinHash.equalsIgnoreCase(customer.getLoginPin())){
			throw new EOTException(ErrorConstants.INVALID_USER_PIN);
		}

		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0 ){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}
		String bankCode=accountList.get(0).getBank().getBankCode();
		masterDataDTO.setDefaultbankId(bankCode);
		
		masterDataDTO.setApplicationStatus(appMaster.getStatus());
		masterDataDTO.setCustomerStatus(customer.getActive());
		
		if( appMaster.getStatus() == Constants.APP_STATUS_DOWNLOADED ) {

			
			CustomerAccount account = accountList.get(0) ;
			masterDataDTO.setDefaultbankId(account.getBank().getBankCode());
			com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setAccountType(Constants.ALIAS_TYPE_MOBILE_ACC+"");
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

			com.eot.dtos.common.Account bankAccountDto = new  com.eot.dtos.common.Account();

			com.eot.entity.Account bankAccount = account.getBank().getAccount() ;

			bankAccountDto.setAccountAlias(bankAccount.getAlias());
			bankAccountDto.setAccountNO(bankAccount.getAccountNumber());
			bankAccountDto.setAccountType(Constants.ALIAS_TYPE_OTHER+"");
			bankAccountDto.setBankCode(account.getBank().getBankId().toString());
			bankAccountDto.setBranchCode(account.getBranch().getBranchId().toString());

			ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

			serviceChargeDebitDTO.setCustomerAccount(accountDto);
			serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
			serviceChargeDebitDTO.setReferenceType(referenceType);
			serviceChargeDebitDTO.setRequestID(requestID.toString());
			serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
			serviceChargeDebitDTO.setTransactionType(masterDataDTO.getTransactionType().toString());
			serviceChargeDebitDTO.setAmount(0D);
			serviceChargeDebitDTO.setOtherAccount(bankAccountDto);

			try {
				//utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
				
				serviceChargeDebitDTO=processRequest(CoreUrls.SERVICE_CHARGE_DEBIT_URL, serviceChargeDebitDTO, com.eot.dtos.utilities.ServiceChargeDebitDTO.class);
				if(serviceChargeDebitDTO.getErrorCode()!=0)
				{
					throw new EOTException(serviceChargeDebitDTO.getErrorCode());
				}

				Transaction txn = new Transaction();
				txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
				mobileRequest.setTransaction(txn);

				//appMaster.setStatus(Constants.APP_STATUS_ACTIVATION_SC_DEBITED);
				appMaster.setStatus(Constants.APP_STATUS_ACTIVATION_SC_DEBITED_temp);
				eotMobileDao.update(appMaster);
			} /*catch (EOTCoreException e) {
				e.printStackTrace();
				throw new EOTException(Integer.parseInt(e.getMessageKey()));
			}*/finally {
				
			}

		}else if(appMaster.getStatus() == Constants.APP_STATUS_NEW)
		{
			appMaster.setStatus(Constants.APP_STATUS_ACTIVATION_SC_DEBITED_temp);
			eotMobileDao.update(appMaster);
		}

		List<CustomerCard> cardList = eotMobileDao.getCardDetails(customer.getCustomerId());
		List<Payee> payeeList = eotMobileDao.getPayeeList(customer.getCustomerId());
		List<Bank> bankList = eotMobileDao.getBanks();
		List<Branch> branchList = eotMobileDao.getBranchs();
		List<Operator> operatorList = eotMobileDao.getOperatorListByBankCode(bankCode);
		List<Country> countryList = eotMobileDao.getAllCountry();
		List<Currency> currencyList = eotMobileDao.getAllCurrency();
		List<BillerTypes> billerTypeList = eotMobileDao.getBillerTypeList();
		List<Biller> billerList = eotMobileDao.getBillerList(customer.getCountry().getCountryId());
		List<CustomerBankAccount> customerBankAccountList = eotMobileDao.getBankAccountDetails(customer.getCustomerId());
		List<NetworkType> networkTypes = eotMobileDao.getAllNetWorkType();
		List<LocationType> locationTypeList = eotMobileDao.getAllActiveLocationType(customer.getDefaultLanguage().substring(0, 2), Constants.ACTIVE_STATUS);
		List<Language> languages = eotMobileDao.getAllLanguageData();
		List<SecurityQuestion> securityQuestionModelList=baseServiceImpl.eotMobileDao.getQuestions(StringUtils.isNotEmpty(masterDataDTO.getDefaultLocale()) ? masterDataDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);
		ArrayList<HelpDesk> helpdesks = baseServiceImpl.eotMobileDao.getAllHelpDeskList();
		List<KycType> kycTypeList=baseServiceImpl.eotMobileDao.getKycTypeForCustomerType(customer.getType());
		
		return packResponse( masterDataDTO, customer, accountList, payeeList, cardList, bankList, branchList, operatorList, countryList, networkTypes, locationTypeList, currencyList, billerTypeList, billerList, customerBankAccountList, languages, securityQuestionModelList,helpdesks,kycTypeList);
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
	private MasterDataDTO packResponse( MasterDataDTO masterDataDTO,Customer customer, List<CustomerAccount> eotAccounts,List<Payee> payeeList, List<CustomerCard> cards, List<Bank> banks,List<Branch> branchs, List<Operator> operators,List<Country> countries, List<NetworkType> networkTypes, List<LocationType> locationTypeList, List<Currency> currencies, List<BillerTypes> billerTypeList,List<Biller> billers, List<CustomerBankAccount> bankAccountDetails,List<Language> languages, List<SecurityQuestion> securityQuestionModelList,ArrayList<HelpDesk> helpdesks,List<KycType> kycTypeList){

		ArrayList<com.eot.banking.dto.MasterDataDTO.Bank> listOfBank = new ArrayList<MasterDataDTO.Bank>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Operator> listOfOperator = new ArrayList<MasterDataDTO.Operator>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.BillerType> listOfBillerType = new ArrayList<MasterDataDTO.BillerType>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Biller> listOfBiller = new ArrayList<MasterDataDTO.Biller>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Branch> listOfBranch = new ArrayList<MasterDataDTO.Branch>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Account> listOfAccount = new ArrayList<MasterDataDTO.Account>();
		
		ArrayList<com.eot.banking.dto.MasterDataDTO.Account> customerBankAccountList = new ArrayList<MasterDataDTO.Account>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Country> listOfCountry = new ArrayList<MasterDataDTO.Country>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Currency> listOfCurrency = new ArrayList<MasterDataDTO.Currency>();
		
		ArrayList<com.eot.banking.dto.MasterDataDTO.NetworkType> listOfNetworkType = new ArrayList<MasterDataDTO.NetworkType>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.LanguageData> listOfLanguages = new ArrayList<MasterDataDTO.LanguageData>();
		
		List<SecurityQuestionDTO> securityQuestions = new ArrayList<SecurityQuestionDTO>();
		
		ArrayList<PayeeDTO> listOfPayee = new ArrayList<PayeeDTO>();

		ArrayList<LocateUsDTO> listOfLocateUsDTO = new ArrayList<LocateUsDTO>();
		
		String firstName = customer.getFirstName();
		String middleName = customer.getMiddleName();
		String lastName = customer.getLastName();
		String customerName = firstName;
		if(middleName != null && middleName.trim().length()>0)
			customerName = customerName + " " + middleName;
		if(lastName != null && lastName.trim().length()>0)
			customerName = customerName + " " + lastName;
		masterDataDTO.setCustomerName(customerName);
		masterDataDTO.setProfileId(customer.getCustomerProfiles().getProfileId());
		masterDataDTO.setBankId(customer.getCustomerProfiles().getBank().getBankId());
		masterDataDTO.setCustomerCountryId(customer.getCountry().getCountryId());
		masterDataDTO.setCustomerCountryName(customer.getCountry().getCountry());
		masterDataDTO.setCustomerCountryIsdCode(customer.getCountry().getIsdCode());

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
			customerBankAccountList.add(account);
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
		masterDataDTO.setTitleList(setTitleList());
		masterDataDTO.setSecurityQuestions(securityQuestions);
		masterDataDTO.setMobileNumber(customer.getMobileNumber());
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
}
