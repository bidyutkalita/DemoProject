/* Copyright © EasOfTech 2016. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: IntegrationServiceImpl.java
*
* Date Author Changes
* 23 Jan, 2017 Swadhin Created
*/
package com.eot.banking.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eot.banking.common.TitleEnum;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.SCRuleDTO;
import com.eot.banking.dto.SecurityQuestionDTO;
import com.eot.banking.dto.TitleDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.server.Constants;
import com.eot.banking.service.IntegrationService;
import com.eot.banking.service.impl.OtherBankingServiceImpl.SortByAlphaCity;
import com.eot.banking.service.impl.OtherBankingServiceImpl.SortByAlphaQuarter;
import com.eot.dtos.common.ServiceChargeSplit;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.BillerTypes;
import com.eot.entity.Branch;
import com.eot.entity.City;
import com.eot.entity.Country;
import com.eot.entity.CountryNames;
import com.eot.entity.Currency;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.HelpDesk;
import com.eot.entity.Language;
import com.eot.entity.Operator;
import com.eot.entity.OperatorDenomination;
import com.eot.entity.Quarter;
import com.eot.entity.SecurityQuestion;

// TODO: Auto-generated Javadoc
/**
 * The Class IntegrationServiceImpl.
 */
@Service
public class IntegrationServiceImpl implements IntegrationService {

	/** The base service impl. */
	@Autowired
	private BaseServiceImpl baseServiceImpl;
	
	/* (non-Javadoc)
	 * @see com.eot.banking.service.IntegrationService#getMasterData(com.eot.banking.dto.MasterDataDTO)
	 */
	
	@Override
	@SuppressWarnings("all")
	public void getMasterData(MasterDataDTO masterDataDTO) throws EOTException {

		List<Bank> bankList = baseServiceImpl.eotMobileDao.getBanks();
		List<Branch> branchList = baseServiceImpl.eotMobileDao.getBranchs();
		List<Operator> operatorList = baseServiceImpl.eotMobileDao.getOperatorListByBankCode(masterDataDTO.getCbsBankCode());
		List<Country> countryList = baseServiceImpl.eotMobileDao.getAllCountry();
		List<Currency> currencyList = baseServiceImpl.eotMobileDao.getAllCurrency();
		List<BillerTypes> billerTypeList = baseServiceImpl.eotMobileDao.getBillerTypeList();
		//List<Biller> billerList = baseServiceImpl.eotMobileDao.getBillerList();
		List<Object[]> scRules =  baseServiceImpl.eotMobileDao.getSCRules(Constants.ALIAS_TYPE_FI_ACC);
		List<Language> languages = baseServiceImpl.eotMobileDao.getAllLanguageData();
		List<SecurityQuestion> securityQuestionModelList=baseServiceImpl.eotMobileDao.getQuestions(StringUtils.isNotEmpty(masterDataDTO.getDefaultLocale()) ? masterDataDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE);
		List<HelpDesk> helpDeskList = baseServiceImpl.eotMobileDao.getAllHelpDeskList();
		
		Long stampFee = 0L;		
		//@start, Exception handled by sudhanshu, dated : 03-07-2018
		List<Biller> billerList = null;
		if(StringUtils.isNotEmpty(masterDataDTO.getCbsId())){
			
			// We are getting cbsBankCode instead of cbsId.
			Integer bankId=baseServiceImpl.eotMobileDao.getBankIdFromBankCode(masterDataDTO.getCbsId()).getBankId();
			stampFee=baseServiceImpl.eotMobileDao.getStampFeeDebitTransaction(bankId);
			Bank bank = baseServiceImpl.eotMobileDao.getBankFromBankId(bankId);
			Country country = null !=bank? bank.getCountry():null;
			billerList = baseServiceImpl.eotMobileDao.getBillerList(null != country ? country.getCountryId() : null);
		}
		//@End, Exception handled by sudhanshu, dated : 03-07-2018

		packResponse( masterDataDTO, bankList, branchList, operatorList, countryList,  currencyList, billerTypeList, billerList , scRules,languages,stampFee,securityQuestionModelList, helpDeskList);
	}
	
	/**
	 * Pack response.
	 *
	 * @param masterDataDTO the master data DTO
	 * @param banks the banks
	 * @param branchs the branchs
	 * @param operators the operators
	 * @param countries the countries
	 * @param currencies the currencies
	 * @param billerTypeList the biller type list
	 * @param billers the billers
	 * @param scRules the sc rules
	 * @param languages the languages
	 * @return the master data DTO
	 */
	@SuppressWarnings("unchecked")
	private MasterDataDTO packResponse( MasterDataDTO masterDataDTO, List<Bank> banks,List<Branch> branchs, List<Operator> operators,List<Country> countries, List<Currency> currencies, List<BillerTypes> billerTypeList,List<Biller> billers, List<Object[]> scRules,List<Language> languages,Long stampFee,List<SecurityQuestion> securityQuestionModelList,List<HelpDesk> helpDeskList){

		ArrayList<com.eot.banking.dto.MasterDataDTO.Bank> listOfBank = new ArrayList<MasterDataDTO.Bank>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Operator> listOfOperator = new ArrayList<MasterDataDTO.Operator>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.BillerType> listOfBillerType = new ArrayList<MasterDataDTO.BillerType>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Biller> listOfBiller = new ArrayList<MasterDataDTO.Biller>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Branch> listOfBranch = new ArrayList<MasterDataDTO.Branch>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Country> listOfCountry = new ArrayList<MasterDataDTO.Country>();

		ArrayList<com.eot.banking.dto.MasterDataDTO.Currency> listOfCurrency = new ArrayList<MasterDataDTO.Currency>();
		
		ArrayList<SCRuleDTO> listOfSCRules = new ArrayList<SCRuleDTO>();
		
		ArrayList<com.eot.banking.dto.MasterDataDTO.LanguageData> listOfLanguages = new ArrayList<MasterDataDTO.LanguageData>();
		
		List<SecurityQuestionDTO> securityQuestions = new ArrayList<SecurityQuestionDTO>();
		
		
		for (Bank bank : banks) {
			com.eot.banking.dto.MasterDataDTO.Bank bankDetails = masterDataDTO.new Bank();
			bankDetails.setBankCode(bank.getBankCode());
			bankDetails.setBankName(bank.getBankName());
			bankDetails.setBankShortName(bank.getBankShortName());
			listOfBank.add(bankDetails);
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
		
		for (Branch branch : branchs) {

			com.eot.banking.dto.MasterDataDTO.Branch branchDetails = masterDataDTO.new Branch();
			branchDetails.setBranchCode(branch.getBranchCode());
			branchDetails.setBankCode(branch.getBank().getBankCode());
			branchDetails.setBranchName(branch.getLocation());
			listOfBranch.add(branchDetails);
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
			countryDetails.setCountryName(getCountryByLanguage(country.getCountryNames(),"en_US"));
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
		//Exception handled by sudhanshu, dated : 03-07-2018
		if (CollectionUtils.isNotEmpty(billers)) {
			for (Biller biller : billers) {
				com.eot.banking.dto.MasterDataDTO.Biller billerDetails = masterDataDTO.new Biller();
				billerDetails.setCountryCode(biller.getCountry().getCountryCodeNumeric() + "");
				billerDetails.setBillerId(biller.getBillerId());
				billerDetails.setBillerType(biller.getBillerType().getBillerTypeId() + "");
				billerDetails.setBillerName(biller.getBillerName());
				listOfBiller.add(billerDetails);
			}
		}

		
		//code for returning service charge map
		
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
		masterDataDTO.setListOfSCRules(listOfSCRules);
		masterDataDTO.setListOfBank(listOfBank);
		masterDataDTO.setListOfBillerType(listOfBillerType);
		masterDataDTO.setListOfBiller(listOfBiller);
		masterDataDTO.setListOfBranch(listOfBranch);
		masterDataDTO.setListOfCountry(listOfCountry);
		masterDataDTO.setListOfOperator(listOfOperator);
		masterDataDTO.setListOfCurrency(listOfCurrency);
		masterDataDTO.setListOfLanguages(listOfLanguages);
		masterDataDTO.setSecurityQuestions(securityQuestions);
		masterDataDTO.setTitleList(setTitleList());
		masterDataDTO.setStampFeeDeposit(stampFee);
		masterDataDTO.setHelpDeskList(helpDeskList);
		masterDataDTO.setStatus(0);
		
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
