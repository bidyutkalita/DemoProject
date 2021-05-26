package com.eot.banking.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.management.loading.PrivateClassLoader;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.common.FieldExecutiveEnum;
import com.eot.banking.common.TitleEnum;
import com.eot.banking.common.UrlConstants;
import com.eot.banking.dto.BusinessPartnerLoginDTO;
import com.eot.banking.dto.BusinessPartnerMasterDataDTO;
import com.eot.banking.dto.ChangePasswordDTO;
import com.eot.banking.dto.CityDTO;
import com.eot.banking.dto.ConfirmPinDTO;
import com.eot.banking.dto.CountryDTO;
import com.eot.banking.dto.CustomerProfileDTO;
import com.eot.banking.dto.CustomerTypeDTO;
import com.eot.banking.dto.FAQsModelDTO;
import com.eot.banking.dto.FAQsModelDTO.FAQ;
import com.eot.banking.dto.HelpDeskModelDTO;
import com.eot.banking.dto.KycTypeDTO;
import com.eot.banking.dto.LanguageDTO;
import com.eot.banking.dto.QuarterDTO;
import com.eot.banking.dto.SecurityQuestionDTO;
import com.eot.banking.dto.SmsResponseDTO;
import com.eot.banking.dto.TitleDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.BusinessPartnerService;
import com.eot.banking.utils.AppConfigurations;
import com.eot.banking.utils.EOTUtil;
import com.eot.banking.utils.FileUtil;
import com.eot.banking.utils.HashUtil;
import com.eot.dtos.sms.AppLinkAlertDTO;
import com.eot.dtos.sms.InitialTxnPinLoginPinAlertDTO;
import com.eot.dtos.sms.SmsHeader;
import com.eot.dtos.sms.WebOTPAlertDTO;
import com.eot.entity.Account;
import com.eot.entity.AppMaster;
import com.eot.entity.Bank;
import com.eot.entity.Branch;
import com.eot.entity.BusinessPartner;
import com.eot.entity.BusinessPartnerUser;
import com.eot.entity.City;
import com.eot.entity.Country;
import com.eot.entity.CountryNames;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerDocument;
import com.eot.entity.CustomerProfiles;
import com.eot.entity.HelpDesk;
import com.eot.entity.KycType;
import com.eot.entity.Language;
import com.eot.entity.Otp;
import com.eot.entity.Quarter;
import com.eot.entity.SecurityQuestion;
import com.eot.entity.WebUser;
import com.eot.smsclient.SmsServiceException;
import com.eot.smsclient.webservice.SmsServiceClientStub;

@Service
public class BusinessPartnerServiceImpl implements BusinessPartnerService {

	/*
	 * @Autowired private WebUserDao webUserDao;
	 */

	@Autowired
	private BaseServiceImpl baseServiceImpl;

	@Autowired
	private AppConfigurations appConfigurations;

	/** The sms service client stub. */
//	@Autowired
//	private SmsServiceClientStub smsServiceClientStub;
	
	@Autowired
	private RestTemplate restTemplate;



	@Override
	public BusinessPartnerLoginDTO processLoginEnquiryRequest(BusinessPartnerLoginDTO businessPartnerLoginDTO) throws EOTException {
		String userName = businessPartnerLoginDTO.getUserName();
		// String password = businessPartnerLoginDTO.getPassword();
		// Integer status;
		if (null == businessPartnerLoginDTO.getPassword()) {
			businessPartnerLoginDTO.setMessageDescription(EOTConstants.FIELD_NON_EMPTY_PASSWORD);
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Password");
		}

		BusinessPartnerUser businessPartnerUser = baseServiceImpl.eotMobileDao.getUser(userName);

		if (businessPartnerUser != null && businessPartnerUser.getLoginPin() != null) {
			if (businessPartnerUser.getLoginPin().equals(businessPartnerLoginDTO.getPassword())) {
				businessPartnerLoginDTO.setUserStatus(businessPartnerUser.getStatus());
				businessPartnerLoginDTO.setStatus(0);

			} else {
				// businessPartnerLoginDTO.setStatus(1);
				businessPartnerLoginDTO.setMessageDescription(EOTConstants.INVALID_USER_PIN);
				throw new EOTException(ErrorConstants.INVALID_USER_PIN);
			}

		} else {
			businessPartnerLoginDTO.setMessageDescription(EOTConstants.INVALID_BUSINESS_PARTNER);
			throw new EOTException(ErrorConstants.INVALID_BUSINESS_PARTNER);
		}
		businessPartnerLoginDTO.setBusinessPartnerName(businessPartnerUser.getBusinessPartner().getName());
		/*
		 * if(businessPartnerUser.getUserName() != null) {
		 * if(businessPartnerUser.getLoginPin().equals(password)) { } }else { }
		 */
		/*
		 * @Override public BusinessPartnerUser getUser(String name) { return
		 * getHibernateTemplate().get(BusinessPartnerUser.class,name); }
		 */

		// businessPartnerLoginDTO.setSuccessResponse(message);
		return businessPartnerLoginDTO;
	}

	@Override
	@Transactional
	public CustomerProfileDTO createFieldExecutive(CustomerProfileDTO customerDTO) throws Exception {

		Country country = null;
		CustomerProfiles customerProfile = null;

		if (null == customerDTO.getTitle()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Title");
		}

		if (null == customerDTO.getFirstName()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "First name");
		}

		if (null == customerDTO.getMobileNumber()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Mobile number");
		}

		if (null == customerDTO.getDob()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Date of birth");
		}

		if (null == customerDTO.getCityId()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "City");
		}

		if (null == customerDTO.getQaurterId()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Quarter");
		}

		if (null == customerDTO.getGender()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Gender");
		}

		// Question,Answer and Language are required for this API so adding by vineeth,
		// on 31-10-2018
		/*
		 * if (null == customerDTO.getQuestionId()) {
		 * 
		 * throw new EOTException(ErrorConstants.FIELD_NON_EMPTY , "Question"); }
		 * 
		 * if (null == customerDTO.getAnswer()) {
		 * 
		 * throw new EOTException(ErrorConstants.FIELD_NON_EMPTY , "Answer"); }
		 */
		if (null == customerDTO.getLanguageCode()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Language");
		}
		// changes end
		if (null == customerDTO.getIdProof()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Id Proof");
		}

		if (null == customerDTO.getAddress()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Address");
		}

		if (null == customerDTO.getCustomerPhoto()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Profile Pic");
		}

		if (null == customerDTO.getAddressProof()) {

			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Address Proof");
		}

		BusinessPartnerUser businessPartnerUser = baseServiceImpl.eotMobileDao.getUser(customerDTO.getUsername());

		if (null == businessPartnerUser) {

			throw new EOTException(ErrorConstants.INVALID_BUSINESS_PARTNER_USER);
		}
		WebUser webUser = baseServiceImpl.eotMobileDao.getWebUser(customerDTO.getUsername());
		if (!webUser.getCredentialsExpired().equalsIgnoreCase("N")) {
			throw new EOTException(ErrorConstants.INACTIVE_BUSINESS_PARTNER);
		}
		if (customerDTO.getCountryId() != null) {
			country = baseServiceImpl.eotMobileDao.getCountryFromCountryCode(customerDTO.getCountryId());
		} else {
			country = baseServiceImpl.eotMobileDao.getCountry(Constants.DEFAULT_COUNTRY_ID);
		}
		customerDTO.setDefaultLocale(StringUtils.isNotEmpty(customerDTO.getDefaultLocale()) ? customerDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);
		customerDTO.setVersionNumber(StringUtils.isNotEmpty(customerDTO.getVersionNumber()) ? customerDTO.getVersionNumber() : Constants.DEFAULT_APP_VERSIOn);

		// String
		// mobileNumber=country.getIsdCode().toString().concat(customerDTO.getMobileNumber());
		Customer cust = baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(country.getIsdCode().toString().concat(customerDTO.getMobileNumber()));
		if (cust != null) {
			throw new EOTException(ErrorConstants.MOBILE_NUMBER_REGISTERED_ALREADY);
		}
		Customer merchant = null;
		CustomerAccount merchantAccountDetails = null;
		if (StringUtils.isNotEmpty(customerDTO.getApplicationId()) && (customerDTO.getType() == 0 || customerDTO.getType() == 1 || customerDTO.getType() == 2 || customerDTO.getType() == 3)) {
			merchant = baseServiceImpl.eotMobileDao.getCustomer(customerDTO.getApplicationId());
			if (merchant == null) {
				throw new EOTException(ErrorConstants.INVALID_MERCHANT);
			}
			// customer registered for same country where the merchant
			// bellongs:bidyut
			country = merchant.getCountry();

			merchantAccountDetails = baseServiceImpl.eotMobileDao.getAccountFromCustomerId(merchant.getCustomerId());

			if (merchantAccountDetails == null) {
				throw new EOTException(ErrorConstants.INVALID_MERCHANT_ACCOUNT);
			}

			customerProfile = baseServiceImpl.eotMobileDao.getCustomerProfile(merchantAccountDetails.getBank().getBankId(),customerDTO.getType());
		}
		cust = new Customer();

		Integer loginPin = EOTUtil.generateLoginPin();
		Integer txnPin = EOTUtil.generateTransactionPin();
		String appID = EOTUtil.generateAppID();
		String uuid = EOTUtil.generateUUID();

		AppMaster app = new AppMaster();
		app.setAppId(appID);
		app.setReferenceId(cust.getCustomerId() + "");
		app.setReferenceType(Constants.REF_TYPE_CUSTOMER);
		app.setStatus(Constants.APP_STATUS_NEW);
		app.setUuid(uuid);
		app.setAppVersion(customerDTO.getVersionNumber());

		Calendar cal = Calendar.getInstance();
		app.setCreatedDate(cal.getTime());
		Integer dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth + 1);
		app.setExpiryDate(cal.getTime());

		baseServiceImpl.eotMobileDao.save(app);

		City city = baseServiceImpl.eotMobileDao.getCityFromCityId(customerDTO.getCityId());// new City();
		Quarter quarter = baseServiceImpl.eotMobileDao.getQuarterFromQuarterId(customerDTO.getQaurterId() == null ? 1L : customerDTO.getQaurterId());// new Quarter();
		cust.setMobileNumber(customerDTO.getMobileNumber());
		cust.setEmailAddress(customerDTO.getEmailId());
		cust.setFirstName(customerDTO.getFirstName());
		cust.setMiddleName(customerDTO.getMiddleName());
		cust.setLastName(customerDTO.getLastName());
		cust.setDob(new Date(customerDTO.getDob()));
		cust.setLoginAttempts(0);
		cust.setAddress(customerDTO.getAddress());
		cust.setCountry(country);
		city.setCityId(customerDTO.getCityId());
		cust.setChanel("Mobile");
		quarter.setQuarterId(customerDTO.getQaurterId() == null ? 1L : customerDTO.getQaurterId());
		cust.setCity(city);
		cust.setQuarter(quarter);
		// cust.setType(Constants.REF_TYPE_CUSTOMER);
		cust.setActive(Constants.CUSTOMER_STATUS_NEW);

		cust.setTitle(customerDTO.getTitle());
		cust.setGender(customerDTO.getGender());
		cust.setDefaultLanguage(customerDTO.getLanguageCode() != null ? customerDTO.getLanguageCode() : Constants.DEFAULT_LANGUAGE);
		// cust.setDefaultLanguage(Constants.DEFAULT_LANGUAGE);

		cust.setLoginPin(HashUtil.generateHash(loginPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
		cust.setTransactionPin(HashUtil.generateHash(txnPin.toString().getBytes(), Constants.PIN_HASH_ALGORITHM));
		cust.setAppId(appID);

		cust.setCommission(customerDTO.getCommission());
		cust.setBusinessPartner(businessPartnerUser.getBusinessPartner());
		cust.setOnbordedBy(businessPartnerUser.getUserName());
		cust.setType(customerDTO.getType());
		cust.setKycStatus(0);//kyc Pending
		if (customerDTO.getType() != 0) {
			String agentCode = baseServiceImpl.eotMobileDao.getAgentCode(customerDTO.getType());
			Integer numericCode = 0;
			if (customerDTO.getType() == (1)) {
				numericCode = agentCode.length() >= 6 ? Integer.parseInt(agentCode.substring(1)) + 100000 : Integer.parseInt(agentCode) + 100000;
			} else if (customerDTO.getType() == (2)) {
				numericCode = agentCode.length() >= 6 ? Integer.parseInt(agentCode.substring(1)) + 700000 : Integer.parseInt(agentCode) + 700000;
			}
			agentCode = numericCode.toString();
			agentCode = ISOUtil.zeropad(agentCode, 6);
			cust.setAgentCode(agentCode);
		}

		cust.setCreatedDate(new Date());

		CustomerProfiles custProfile = new CustomerProfiles();
		custProfile.setProfileId(customerDTO.getProfileId() != null ? customerDTO.getProfileId() : Constants.DEFAULT_CUSTOMER_PROFILE);
		if (customerProfile != null && customerDTO.getProfileId() == null)
			custProfile.setProfileId(customerProfile.getProfileId());
		cust.setCustomerProfiles(custProfile);

		baseServiceImpl.eotMobileDao.save(cust);
		app.setReferenceId(cust.getCustomerId().toString());
		baseServiceImpl.eotMobileDao.update(app);

		CustomerDocument customerDocument = new CustomerDocument();
		if (customerDTO.getSignature() != null)
			customerDocument.setSignaturePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getSignature())));
		customerDocument.setIdproofPhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getIdProof())));
		if (customerDTO.getCustomerPhoto() != null)
			customerDocument.setProfilePhoto(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getCustomerPhoto())));
		if (customerDTO.getAddressProof() != null)
			customerDocument.setAddressProof(Hibernate.createBlob(FileUtil.decodeBase64BinaryStringToBytes(customerDTO.getAddressProof())));
		customerDocument.setCustomerId(cust.getCustomerId());
		customerDocument.setCustomer(cust);
		baseServiceImpl.eotMobileDao.save(customerDocument);

		// Questions not required in this API

		/*
		 * CustomerSecurityAnswer answer = new CustomerSecurityAnswer();
		 * answer.setAnswer(customerDTO.getAnswer()); answer.setCustomer(cust);
		 * SecurityQuestion securityQuestion = new SecurityQuestion();
		 * securityQuestion.setQuestionId(customerDTO.getQuestionId());
		 * answer.setSecurityQuestion(securityQuestion);
		 * baseServiceImpl.eotMobileDao.save(answer);
		 */

		// below bank id code is mobified by bidyut
		// perpous is to achive the corret bank registration for FiCustomer insted of
		// default bank
		int bankId = Constants.DEFAULT_BANK;
		long branchId = Constants.DEFAULT_BRANCH;

		Bank custBank = null;
		if (StringUtils.isNotEmpty(customerDTO.getBankCode())) {
			custBank = baseServiceImpl.eotMobileDao.getBankIdFromBankCode(customerDTO.getBankCode());
			bankId = null != custBank ? custBank.getBankId() : bankId;
		}

		// code for bank and branch for wallet application, vineeth changes, on
		// 31-10-2018
		custBank = businessPartnerUser.getBusinessPartner().getBank();
		if (custBank != null) {
			bankId = custBank.getBankId();
			List<Branch> branches = new ArrayList<Branch>(custBank.getBranches());
			branchId = branches.get(0).getBranchId();
		}
		// changes end
		if (null != customerDTO.getBankId()) {
			bankId = customerDTO.getBankId();
		}
		if (null != customerDTO.getCbsBranchCode()) {
			branchId = baseServiceImpl.eotMobileDao.getBranchId(bankId, customerDTO.getCbsBranchCode());
		}

		// baseServiceImpl.eotMobileDao.save(answer);
		Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(null != merchantAccountDetails && null != merchantAccountDetails.getBank() && null != merchantAccountDetails.getBank().getBankId() ? merchantAccountDetails.getBank().getBankId() : bankId);
		// Bank bank =
		// baseServiceImpl.eotMobileDao.getBankFromBankId(customerDTO.getBankId() !=
		// null ? customerDTO.getBankId() : Constants.DEFAULT_BANK);
		Branch branch = baseServiceImpl.eotMobileDao.getBranchFromBranchId(null != merchantAccountDetails && null != merchantAccountDetails.getBranch() && null != merchantAccountDetails.getBranch().getBranchId() ? merchantAccountDetails.getBranch().getBranchId() : branchId);
		// Branch branch =
		// baseServiceImpl.eotMobileDao.getBranchFromBranchId(customerDTO.getBranchId()
		// != null ? customerDTO.getBranchId() : Constants.DEFAULT_BRANCH);

		long accountSeq = baseServiceImpl.eotMobileDao.getNextAccountNumberSequence();
		Account account = new Account();
		account.setAccountNumber(EOTUtil.generateAccountNumber(accountSeq));
		account.setAccountType(customerDTO.getAliasType() != null ? customerDTO.getAliasType() : Constants.ACCOUNT_TYPE_PERSONAL);
		account.setActive(Constants.APP_STATUS_ACTIVATED);
		String alias = Constants.ACCOUNT_ALIAS_CUSTOMER + " - " + bank.getBankName();
		account.setAlias(alias);
		account.setAliasType(Constants.ALIAS_TYPE_WALLET_ACCOUNT);
		account.setCurrentBalance(0.0);
		account.setCurrentBalanceType(Constants.ACCOUNT_BALANCE_TYPE_CREDIT);
		account.setReferenceId(cust.getCustomerId().toString());
		account.setReferenceType(customerDTO.getType());

		try {
			baseServiceImpl.eotMobileDao.save(account);
		} catch (Exception e) {
//			e.printStackTrace();
			// TODO: handle exception
		}

		// bellow code is for commissio account only for agent
		if (customerDTO.getType() == Constants.REF_TYPE_AGENT) {
			Account agentCommissionAccount = new Account();

			agentCommissionAccount.setAccountNumber(EOTUtil.generateAccountNumber(accountSeq + 1));
			agentCommissionAccount.setAccountType(Constants.ACCOUNT_TYPE_PERSONAL);
			agentCommissionAccount.setActive(Constants.ACCOUNT_STATUS_ACTIVE);

			String commissionAccountAlias = Constants.ACCOUNT_ALIAS_CUSTOMER + " - " + bank.getBankName();
			agentCommissionAccount.setAlias(commissionAccountAlias);
			agentCommissionAccount.setCurrentBalance(0.0);
			agentCommissionAccount.setAliasType(Constants.ALIAS_TYPE_COMMISSION_ACCOUNT);
			agentCommissionAccount.setCurrentBalanceType(Constants.ACCOUNT_BALANCE_TYPE_CREDIT);
			agentCommissionAccount.setReferenceId(cust.getCustomerId().toString());
			agentCommissionAccount.setReferenceType(customerDTO.getType());

			baseServiceImpl.eotMobileDao.save(agentCommissionAccount);

			CustomerAccount commissionAccount = new CustomerAccount();
			commissionAccount.setAccount(agentCommissionAccount);
			commissionAccount.setAccountNumber(agentCommissionAccount.getAccountNumber());
			commissionAccount.setCustomer(cust);
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

	//	appAlertDto.setDownloadLink(appConfigurations.getAppDownloadURL() + uuid);
		appAlertDto.setDownloadLink(appConfigurations.getAppDownloadURL() + appType);
		appAlertDto.setLocale(cust.getDefaultLanguage());
		appAlertDto.setMobileNo(country.getIsdCode() + cust.getMobileNumber());
		appAlertDto.setScheduleDate(Calendar.getInstance());
		// smsServiceClientStub.appLinkAlert(appAlertDto);

		InitialTxnPinLoginPinAlertDTO pinDto = new InitialTxnPinLoginPinAlertDTO();
		pinDto.setLocale(cust.getDefaultLanguage());
		pinDto.setLoginPIN(loginPin.toString());
		pinDto.setMobileNo(country.getIsdCode() + cust.getMobileNumber());
		pinDto.setTxnPIN(txnPin.toString());
		pinDto.setScheduleDate(Calendar.getInstance());
		// smsServiceClientStub.initialTxnPinLoginPinAlert(pinDto);

		customerDTO.setApplicationId(appID);
		customerDTO.setStatus(0);
		customerDTO.setAddressProof(null);
		customerDTO.setSignature(null);
		customerDTO.setIdProof(null);
		customerDTO.setCustomerPhoto(null);
		customerDTO.setEncPayload(null);
		String typeMessage = customerDTO.getType() == 0 ? "CUSTMER_REGISTRATION_SUCCESS" : customerDTO.getType() == 1 ? "AGENT_REGISTRATION_SUCCESS" : customerDTO.getType() == 2 ? "SOLE_MERCHANT_REGISTRATION_SUCCESS" : "AGENT_SOLE_MERCHANT_REGISTRATION_SUCCESS";

		customerDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage(typeMessage, null, new Locale(cust.getDefaultLanguage())));
		customerDTO.setMessageDescription(baseServiceImpl.messageSource.getMessage(typeMessage, null, new Locale(cust.getDefaultLanguage())));
		return customerDTO;

	}

	@Override
	public BusinessPartnerMasterDataDTO fetchBusinessPartnerMasterData() throws EOTException {

		BusinessPartnerMasterDataDTO masterData = new BusinessPartnerMasterDataDTO();
		List<Country> countryModelList = baseServiceImpl.eotMobileDao.getCountries();
		List<KycType> kycTypeModelList = baseServiceImpl.eotMobileDao.getKycType();
		List<Language> languagesModelList = baseServiceImpl.eotMobileDao.getAllLanguageData();
		List<SecurityQuestion> securityQuestionModelList = baseServiceImpl.eotMobileDao.getQuestions(StringUtils.isNotEmpty(masterData.getDefaultLocale()) ? masterData.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);

		List<CountryDTO> countryDTOList = new ArrayList<CountryDTO>();
		List<KycTypeDTO> kycDTOList = new ArrayList<KycTypeDTO>();
		List<LanguageDTO> LanguageDTOList = new ArrayList<LanguageDTO>();
		List<SecurityQuestionDTO> securityQuestionDTOList = new ArrayList<SecurityQuestionDTO>();

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
		for (Language language : languagesModelList) {

			LanguageDTO k = new LanguageDTO();
			k.setLanguageId(language.getLanguageId());
			k.setDescription(language.getDescription());
			k.setLanguageCode(language.getLanguageCode());
			LanguageDTOList.add(k);
		}
		for (SecurityQuestion securityQuestion : securityQuestionModelList) {

			SecurityQuestionDTO k = new SecurityQuestionDTO();
			k.setQuestionId(securityQuestion.getQuestionId());
			k.setQuestion(securityQuestion.getQuestion());
			k.setActive(securityQuestion.getActive());
			k.setLocale(securityQuestion.getLocale());
			securityQuestionDTOList.add(k);
		}

		masterData.setSecurityQuestions(securityQuestionDTOList);
		masterData.setLanguages(LanguageDTOList);
		masterData.setCountryList(countryDTOList);
		masterData.setKycTypeList(kycDTOList);
		masterData.setCustomerTypeList(setCustomerTypeList());
		masterData.setTitleList(setTitleList());

		return masterData;
	}

	private void setCountries(BusinessPartnerMasterDataDTO masterData, Country country, List<CountryDTO> countryDTOList) {

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

	private String getCountryName(Country country, String languageCode, int countryId) {

		Set<CountryNames> countryNames = country.getCountryNames();
		/*
		 * CountryNamesPK compKey=new CountryNamesPK(); compKey.setCountryId(countryId);
		 * compKey.setLanguageCode(languageCode);
		 */

		for (CountryNames countryName : countryNames) {
			if (countryName.getComp_id().getCountryId().equals(countryId) && countryName.getComp_id().getLanguageCode().equals(languageCode)) {
				return countryName.getCountryName();
			}
		}
		return null;
	}

	private List<CityDTO> setCities(Set<City> cityList1) {

		List<CityDTO> cityDTOList = new ArrayList<CityDTO>();
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

	private List<QuarterDTO> setQuarters(Set<Quarter> quarterList1) {

		List<QuarterDTO> quarterDTOList = new ArrayList<QuarterDTO>();
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

	private List<CustomerTypeDTO> setCustomerTypeList() {

		List<CustomerTypeDTO> customerTypeList = new ArrayList<CustomerTypeDTO>();
		Map<Integer, String> fieldExecutiveMap = FieldExecutiveEnum.getFieldExecutiveMap();
		fieldExecutiveMap.remove(3);
		for (Map.Entry<Integer, String> entry : fieldExecutiveMap.entrySet()) {
			if (entry.getKey().intValue() != 0) {
				CustomerTypeDTO custTypeDTO = new CustomerTypeDTO();
				custTypeDTO.setCustomerTypeId(entry.getKey());
				custTypeDTO.setCustomerTypeName(entry.getValue());
				customerTypeList.add(custTypeDTO);
			}
		}

		return customerTypeList;
	}

	private List<TitleDTO> setTitleList() {
		List<TitleDTO> titleList = new ArrayList<TitleDTO>();
		Map<Integer, String> titleMap = TitleEnum.getTitleMap();

		for (Map.Entry<Integer, String> entry : titleMap.entrySet()) {
			TitleDTO title = new TitleDTO();
			title.setTitleId(entry.getKey());
			title.setTitleName(entry.getValue());
			titleList.add(title);
		}

		return titleList;
	}

	@Override
	public ChangePasswordDTO changeLoginPassword(ChangePasswordDTO changePasswordDTO) throws EOTException {

		BusinessPartnerUser businessPartnerUser = baseServiceImpl.eotMobileDao.getUser(changePasswordDTO.getUsername());
		validate(changePasswordDTO, businessPartnerUser);
		businessPartnerUser.setLoginPin(changePasswordDTO.getNewPassword());
		businessPartnerUser.setStatus(20);
		baseServiceImpl.eotMobileDao.update(businessPartnerUser);
		changePasswordDTO.setStatus(0);
		changePasswordDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("CHANGE_TXN_SUCCESS", null, new Locale(changePasswordDTO.getDefaultLocale() != null ? changePasswordDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));
		changePasswordDTO.setMessageDescription(baseServiceImpl.messageSource.getMessage("CHANGE_TXN_SUCCESS", null, new Locale(changePasswordDTO.getDefaultLocale() != null ? changePasswordDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));
		return changePasswordDTO;
	}

	private void validate(ChangePasswordDTO changePasswordDTO, BusinessPartnerUser businessPartnerUser) throws EOTException {

		if (businessPartnerUser == null) {
			changePasswordDTO.setStatus(1);
			throw new EOTException(ErrorConstants.INVALID_BUSINESS_PARTNER);
		}

		if (!businessPartnerUser.getLoginPin().equals(changePasswordDTO.getOldPassword())) {
			changePasswordDTO.setStatus(1);
			throw new EOTException(ErrorConstants.INVALID_USER_PIN);
		}

		/*
		 * if (businessPartnerUser.getStatus().intValue() !=10) {
		 * changePasswordDTO.setStatus(1); throw new
		 * EOTException(ErrorConstants.USER_NOT_ACTIVE); }
		 */
	}

	class SortByAlphaCity implements Comparator<City> {
		// Used for sorting in ascending order of
		public int compare(City city1, City city2) {
			return city1.getCity().compareToIgnoreCase(city2.getCity());
		}
	}

	class SortByAlphaQuarter implements Comparator<Quarter> {
		@Override
		public int compare(Quarter o1, Quarter o2) {
			// TODO Auto-generated method stub
			return o1.getQuarter().compareToIgnoreCase(o2.getQuarter());
		}
	}

	@Override
	public FAQsModelDTO fetchFaqs() throws EOTException {

		List<String> quesions = new ArrayList<String>();
		List<String> answers = new ArrayList<String>();
		ArrayList<FAQ> faqsList = new ArrayList<FAQ>();

		quesions.add("New Registration");
		quesions.add("Partial Registration");
		quesions.add("Change PIN");
		quesions.add("Reports");

		answers.add("This option helps you to register a new Agent Sole Merchant / Agent / Merchant to the application. Basic & KYC Information are required to onboard an Agent Sole Merchant / Agent / Merchant Successfully.");
		answers.add("This option helps you to complete or delete the registration process of a partial registration.");
		answers.add("This option helps you to change your current login credentials.");
		answers.add("This option helps you to view the number of New & Partial Registrations performed. Cumulative Count will give the information of all registrations added from the day of activation and Today\'s Count will display the record added on current date.This option helps you to view the number of New & Partial Registrations performed. Cumulative Count will give the information of all registrations added from the day of activation and Today\'s Count will display the record added on current date.");

		FAQsModelDTO faqsModelDTO = new FAQsModelDTO();

		for (String question : quesions) {
			FAQ faq = faqsModelDTO.new FAQ();
			faq.setQuestion(question);
			faq.setAnswer(answers.get(quesions.indexOf(question)));
			faqsList.add(faq);
		}
		faqsModelDTO.setFAQsList(faqsList);

		return faqsModelDTO;
	}

	@Override
	public FAQsModelDTO fetchFaqsCustomer() {

		ArrayList<FAQ> faqs = baseServiceImpl.eotMobileDao.getFAQsList();
		FAQsModelDTO faqsList = new FAQsModelDTO();
		faqsList.setFAQsList(faqs);
		return faqsList;
	}
	
	@Override
	public HelpDeskModelDTO getAllHelpDeskList() {

		ArrayList<HelpDesk> helpdesks = baseServiceImpl.eotMobileDao.getAllHelpDeskList();
		HelpDeskModelDTO helpdeskList = new HelpDeskModelDTO();
		helpdeskList.setHelpDeskList(helpdesks);
		return helpdeskList;
	}

	@SuppressWarnings("unused")
	@Override
	public TransactionBaseDTO processForgetPin(TransactionBaseDTO transactionBaseDTO) throws EOTException, Exception {

		String mobileNumber = transactionBaseDTO.getMobileNumber();

		if (mobileNumber != null) {
			WebUser webUser = baseServiceImpl.eotMobileDao.getUserByMobileNumber(mobileNumber);

			if (webUser == null) {
				throw new EOTException(ErrorConstants.INVALID_NUMBER);
			}

			if (webUser != null) {
				BusinessPartnerUser businessPartnerUser = baseServiceImpl.eotMobileDao.getBusinessPartnerUser(webUser.getUserName());

				Country country = baseServiceImpl.eotMobileDao.getCountry(Constants.DEFAULT_COUNTRY_ID);
				String cellNumber = country.getIsdCode().toString().concat(mobileNumber);
				// Customer customer =
				// baseServiceImpl.eotMobileDao.getCustomerByMobileNumber(cellNumber);

				// int referenceType = customer.getType();
				WebOTPAlertDTO dto = new WebOTPAlertDTO();
				dto.setLocale(Constants.DEFAULT_LANGUAGE);
				dto.setMobileNo(cellNumber);
				dto.setOtpType(Constants.OTP_TYPE_FORGOT_PIN);
				dto.setReferenceId(webUser.getUserName());
				dto.setReferenceType(Constants.REF_TYPE_BUSINESS_PARTNER_USER);
				dto.setScheduleDate(Calendar.getInstance());
				try {
//					smsServiceClientStub.webOTPAlert(dto);
					
					SmsResponseDTO responseDTO=sendSMS(UrlConstants.WEB_OTP_ALERT, dto);
					if(responseDTO.getStatus().equalsIgnoreCase("0"))
						throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
				} catch (EOTException e) {
					throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
				}

				transactionBaseDTO.setStatus(0);
				transactionBaseDTO.setMessageDescription("OTP sent to Mobile Number " + mobileNumber);
			}
		} else
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Mobile Number");
		return transactionBaseDTO;
	}

	@Override
	public ConfirmPinDTO processConfirmPin(ConfirmPinDTO confirmPinDTO) throws EOTException, Exception {

		String newPin = confirmPinDTO.getNewPin();
		String otp = confirmPinDTO.getOtp();
		String mobileNumber = confirmPinDTO.getMobileNumber();

		if (null == newPin || newPin.equals(""))
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "New Pin");
		else if (null == otp || otp.equals(""))
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "OTP");
		else if (null == mobileNumber || mobileNumber.equals(""))
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY, "Mobile Number");

		WebUser webUser = baseServiceImpl.eotMobileDao.getUserByMobileNumber(mobileNumber);
		if (webUser != null) {
			BusinessPartnerUser businessPartnerUser = baseServiceImpl.eotMobileDao.getBusinessPartnerUser(webUser.getUserName());

			String referenceId = webUser.getUserName();
			Otp otp1 = baseServiceImpl.eotMobileDao.getOtp(referenceId);
			String actualOtp = otp1 != null ? otp1.getOtpHash() : "";
			if (actualOtp.equals(otp)) {
				businessPartnerUser.setLoginPin(newPin);
				baseServiceImpl.eotMobileDao.update(businessPartnerUser);
			} else
				throw new EOTException(ErrorConstants.INVALID_OTP);

			confirmPinDTO.setStatus(0);
			String message = "Pin changed Successfully";
			confirmPinDTO.setMessageDescription(message);

		}
		return confirmPinDTO;
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
}
