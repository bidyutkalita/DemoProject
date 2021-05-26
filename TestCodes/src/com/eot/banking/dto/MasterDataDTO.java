/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: MasterDataDTO.java,v 1.0
*
* Date Author Changes
* 27 Oct, 2015, 3:30:13 PM Sambit Created
*/
package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eot.entity.CustomerBankAccount;
import com.eot.entity.HelpDesk;

// TODO: Auto-generated Javadoc
/**
 * The Class MasterDataDTO.
 */
public class MasterDataDTO extends TransactionBaseDTO {

	/** The Constant serialVersionUID. */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 8746686350408138193L;

	/** The customer country name. */
	private String customerCountryName;
	
	/** The customer country id. */
	private Integer customerCountryId;
	
	/** The customer country isd code. */
	private Integer customerCountryIsdCode;
	
	private Map<String, String> dynamicThemeColorCode = new HashMap<String, String>();
	
	private List<MobileDynamicTab> listOfDynamicTabs = new ArrayList<MobileDynamicTab>();
	
	private Long stampFeeDeposit;

	private String walletCustomerId;

	private String entityCode;

	private String cbsId;
	
	private String bankCode;
	
	private List<TitleDTO> titleList;
	
	private int userType;
	
	private String defaultbankId;
	private String agentCode;
	
	/** The list of bank. */
	private ArrayList<Bank> listOfBank = new ArrayList<MasterDataDTO.Bank>();

	/** The list of operator. */
	private ArrayList<Operator> listOfOperator = new ArrayList<MasterDataDTO.Operator>();

	/** The list of biller type. */
	private ArrayList<BillerType> listOfBillerType = new ArrayList<MasterDataDTO.BillerType>();

	/** The list of biller. */
	private ArrayList<Biller> listOfBiller = new ArrayList<MasterDataDTO.Biller>();

	/** The list of branch. */
	private ArrayList<Branch> listOfBranch = new ArrayList<MasterDataDTO.Branch>();

	/** The list of account. */
	private ArrayList<Account> listOfAccount = new ArrayList<MasterDataDTO.Account>();
	
	private String benificiaryAccount;
	
	private String benificiaryName;
	
	/** The list of payee. */
	private ArrayList<PayeeDTO> listOfPayee = new ArrayList<PayeeDTO>();

	/** The list of country. */
	private ArrayList<Country> listOfCountry = new ArrayList<MasterDataDTO.Country>();
	
	/** The list of currency. */
	private ArrayList<Currency> listOfCurrency = new ArrayList<MasterDataDTO.Currency>();
	
	/** The list of locate us dto. */
	private ArrayList<LocateUsDTO> listOfLocateUsDTO = new ArrayList<LocateUsDTO>();
	
	/** The list of network type. */
	private ArrayList<NetworkType> listOfNetworkType = new ArrayList<MasterDataDTO.NetworkType>();
	
	/** The list of SC rules. */
	private ArrayList<SCRuleDTO> listOfSCRules = new ArrayList<SCRuleDTO>();
	
	/** The list of languages. */
	private ArrayList<LanguageData> listOfLanguages = new ArrayList<MasterDataDTO.LanguageData>();
	
	/** The list of securityQuestions. */
	private List<SecurityQuestionDTO> securityQuestions = new ArrayList<SecurityQuestionDTO>();
	private List<CustomerBankAccount> customerBankAccountList = new ArrayList<CustomerBankAccount>();
	
	/** The list of helpDesk. */
	private List<HelpDesk> helpDeskList = new ArrayList<HelpDesk>();
	
	private List<String> idTypes = new ArrayList<String>();
	
	Account recevierAccount ;
	
	private String cbsBankCode;
	private Long branchId;
	private Integer bankId;
	private Integer profileId;
	private String cbsBranchCode;
	private int applicationStatus;
	private Integer customerStatus;

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCbsBranchCode() {
		return cbsBranchCode;
	}

	public void setCbsBranchCode(String cbsBranchCode) {
		this.cbsBranchCode = cbsBranchCode;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getWalletCustomerId() {
		return walletCustomerId;
	}

	public void setWalletCustomerId(String walletCustomerId) {
		this.walletCustomerId = walletCustomerId;
	}

	/**
	 * Gets the customer country name.
	 *
	 * @return the customer country name
	 */
	public String getCustomerCountryName() {
		return customerCountryName;
	}

	/**
	 * Sets the customer country name.
	 *
	 * @param customerCountryName the new customer country name
	 */
	public void setCustomerCountryName(String customerCountryName) {
		this.customerCountryName = customerCountryName;
	}

	/**
	 * Gets the customer country id.
	 *
	 * @return the customer country id
	 */
	public Integer getCustomerCountryId() {
		return customerCountryId;
	}

	/**
	 * Sets the customer country id.
	 *
	 * @param customerCountryId the new customer country id
	 */
	public void setCustomerCountryId(Integer customerCountryId) {
		this.customerCountryId = customerCountryId;
	}

	/**
	 * Gets the customer country isd code.
	 *
	 * @return the customer country isd code
	 */
	public Integer getCustomerCountryIsdCode() {
		return customerCountryIsdCode;
	}

	/**
	 * Sets the customer country isd code.
	 *
	 * @param customerCountryIsdCode the new customer country isd code
	 */
	public void setCustomerCountryIsdCode(Integer customerCountryIsdCode) {
		this.customerCountryIsdCode = customerCountryIsdCode;
	}

	/**
	 * Gets the list of bank.
	 * 
	 * @return the list of bank
	 */
	public ArrayList<Bank> getListOfBank() {
		return listOfBank;
	}

	/**
	 * Sets the list of bank.
	 * 
	 * @param listOfBank
	 *            the new list of bank
	 */
	public void setListOfBank(ArrayList<Bank> listOfBank) {
		this.listOfBank = listOfBank;
	}

	/**
	 * Gets the list of operator.
	 * 
	 * @return the list of operator
	 */
	public ArrayList<Operator> getListOfOperator() {
		return listOfOperator;
	}

	/**
	 * Sets the list of operator.
	 * 
	 * @param listOfOperator
	 *            the new list of operator
	 */
	public void setListOfOperator(ArrayList<Operator> listOfOperator) {
		this.listOfOperator = listOfOperator;
	}

	/**
	 * Gets the list of biller.
	 * 
	 * @return the list of biller
	 */
	public ArrayList<Biller> getListOfBiller() {
		return listOfBiller;
	}

	/**
	 * Sets the list of biller.
	 * 
	 * @param listOfBiller
	 *            the new list of biller
	 */
	public void setListOfBiller(ArrayList<Biller> listOfBiller) {
		this.listOfBiller = listOfBiller;
	}

	/**
	 * Gets the list of branch.
	 * 
	 * @return the list of branch
	 */
	public ArrayList<Branch> getListOfBranch() {
		return listOfBranch;
	}

	/**
	 * Sets the list of branch.
	 * 
	 * @param listOfBranch
	 *            the new list of branch
	 */
	public void setListOfBranch(ArrayList<Branch> listOfBranch) {
		this.listOfBranch = listOfBranch;
	}

	/**
	 * Gets the list of account.
	 * 
	 * @return the list of account
	 */
	public ArrayList<Account> getListOfAccount() {
		return listOfAccount;
	}

	/**
	 * Sets the list of account.
	 * 
	 * @param listOfAccount
	 *            the new list of account
	 */
	public void setListOfAccount(ArrayList<Account> listOfAccount) {
		this.listOfAccount = listOfAccount;
	}

	/**
	 * Gets the list of country.
	 * 
	 * @return the list of country
	 */
	public ArrayList<Country> getListOfCountry() {
		return listOfCountry;
	}

	/**
	 * Sets the list of country.
	 * 
	 * @param listOfCountry
	 *            the new list of country
	 */
	public void setListOfCountry(ArrayList<Country> listOfCountry) {
		this.listOfCountry = listOfCountry;
	}

	/**
	 * Gets the list of biller type.
	 * 
	 * @return the list of biller type
	 */
	public ArrayList<BillerType> getListOfBillerType() {
		return listOfBillerType;
	}

	/**
	 * Sets the list of biller type.
	 * 
	 * @param listOfBillerType
	 *            the new list of biller type
	 */
	public void setListOfBillerType(ArrayList<BillerType> listOfBillerType) {
		this.listOfBillerType = listOfBillerType;
	}

	/**
	 * Gets the list of payee.
	 *
	 * @return the list of payee
	 */
	public ArrayList<PayeeDTO> getListOfPayee() {
		return listOfPayee;
	}

	/**
	 * Sets the list of payee.
	 *
	 * @param listOfPayee the new list of payee
	 */
	public void setListOfPayee(ArrayList<PayeeDTO> listOfPayee) {
		this.listOfPayee = listOfPayee;
	}

	/**
	 * Gets the list of currency.
	 *
	 * @return the list of currency
	 */
	public ArrayList<Currency> getListOfCurrency() {
		return listOfCurrency;
	}

	/**
	 * Sets the list of currency.
	 *
	 * @param listOfCurrency the new list of currency
	 */
	public void setListOfCurrency(ArrayList<Currency> listOfCurrency) {
		this.listOfCurrency = listOfCurrency;
	}

	/**
	 * Gets the list of locate us dto.
	 *
	 * @return the list of locate us dto
	 */
	public ArrayList<LocateUsDTO> getListOfLocateUsDTO() {
		return listOfLocateUsDTO;
	}

	/**
	 * Sets the list of locate us dto.
	 *
	 * @param listOfLocateUsDTO the new list of locate us dto
	 */
	public void setListOfLocateUsDTO(ArrayList<LocateUsDTO> listOfLocateUsDTO) {
		this.listOfLocateUsDTO = listOfLocateUsDTO;
	}

	/**
	 * Gets the list of network type.
	 *
	 * @return the list of network type
	 */
	public ArrayList<NetworkType> getListOfNetworkType() {
		return listOfNetworkType;
	}

	/**
	 * Sets the list of network type.
	 *
	 * @param listOfNetworkType the new list of network type
	 */
	public void setListOfNetworkType(ArrayList<NetworkType> listOfNetworkType) {
		this.listOfNetworkType = listOfNetworkType;
	}

	/**
	 * Gets the list of SC rules.
	 *
	 * @return the list of SC rules
	 */
	public ArrayList<SCRuleDTO> getListOfSCRules() {
		return listOfSCRules;
	}

	/**
	 * Sets the list of SC rules.
	 *
	 * @param listOfSCRules the new list of SC rules
	 */
	public void setListOfSCRules(ArrayList<SCRuleDTO> listOfSCRules) {
		this.listOfSCRules = listOfSCRules;
	}

	/**
	 * Gets the list of languages.
	 *
	 * @return the list of languages
	 */
	public ArrayList<LanguageData> getListOfLanguages() {
		return listOfLanguages;
	}

	/**
	 * Sets the list of languages.
	 *
	 * @param listOfLanguages the new list of languages
	 */
	public void setListOfLanguages(ArrayList<LanguageData> listOfLanguages) {
		this.listOfLanguages = listOfLanguages;
	}

	/**
	 * The Class Bank.
	 */
	public class Bank {

		/** The bank name. */
		private String bankName;

		/** The bank short name. */
		private String bankShortName;

		/** The bank code. */
		private String bankCode;

		/**
		 * Gets the bank name.
		 * 
		 * @return the bank name
		 */
		public String getBankName() {
			return bankName;
		}

		/**
		 * Sets the bank name.
		 * 
		 * @param bankName
		 *            the new bank name
		 */
		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		/**
		 * Gets the bank short name.
		 * 
		 * @return the bank short name
		 */
		public String getBankShortName() {
			return bankShortName;
		}

		/**
		 * Sets the bank short name.
		 * 
		 * @param bankShortName
		 *            the new bank short name
		 */
		public void setBankShortName(String bankShortName) {
			this.bankShortName = bankShortName;
		}

		/**
		 * Gets the bank code.
		 * 
		 * @return the bank code
		 */
		public String getBankCode() {
			return bankCode;
		}

		/**
		 * Sets the bank code.
		 * 
		 * @param bankCode
		 *            the new bank code
		 */
		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}
	}

	/**
	 * The Class Branch.
	 */
	public class Branch {

		/** The branch code. */
		private String branchCode;

		/** The bank code. */
		private String bankCode;

		/** The branch name. */
		private String branchName;

		/**
		 * Gets the branch code.
		 * 
		 * @return the branch code
		 */
		public String getBranchCode() {
			return branchCode;
		}

		/**
		 * Sets the branch code.
		 * 
		 * @param branchCode
		 *            the new branch code
		 */
		public void setBranchCode(String branchCode) {
			this.branchCode = branchCode;
		}

		/**
		 * Gets the bank code.
		 * 
		 * @return the bank code
		 */
		public String getBankCode() {
			return bankCode;
		}

		/**
		 * Sets the bank code.
		 * 
		 * @param bankCode
		 *            the new bank code
		 */
		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}

		/**
		 * Gets the branch name.
		 * 
		 * @return the branch name
		 */
		public String getBranchName() {
			return branchName;
		}

		/**
		 * Sets the branch name.
		 * 
		 * @param branchName
		 *            the new branch name
		 */
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}

	}

	/**
	 * The Class Operator.
	 */
	public class Operator {

		/** The country code. */
		private String countryCode;

		/** The operator id. */
		private Long operatorId;

		/** The operator name. */
		private String operatorName;

		/** The denomination list. */
		private ArrayList<Long> denominationList = new ArrayList<Long>();

		/**
		 * Gets the country code.
		 * 
		 * @return the country code
		 */
		public String getCountryCode() {
			return countryCode;
		}

		/**
		 * Sets the country code.
		 * 
		 * @param countryCode
		 *            the new country code
		 */
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		/**
		 * Gets the operator id.
		 * 
		 * @return the operator id
		 */
		public Long getOperatorId() {
			return operatorId;
		}

		
		/**
		 * Sets the operator id.
		 * 
		 * @param operatorId
		 *            the new operator id
		 */
		public void setOperatorId(Long operatorId) {
			this.operatorId = operatorId;
		}

		/**
		 * Gets the operator name.
		 * 
		 * @return the operator name
		 */
		public String getOperatorName() {
			return operatorName;
		}

		/**
		 * Sets the operator name.
		 * 
		 * @param operatorName
		 *            the new operator name
		 */
		public void setOperatorName(String operatorName) {
			this.operatorName = operatorName;
		}

		/**
		 * Gets the denomination list.
		 * 
		 * @return the denomination list
		 */
		public ArrayList<Long> getDenominationList() {
			return denominationList;
		}

		/**
		 * Sets the denomination list.
		 * 
		 * @param denominationList
		 *            the new denomination list
		 */
		public void setDenominationList(ArrayList<Long> denominationList) {
			this.denominationList = denominationList;
		}
	}

	/**
	 * The Class BillerType.
	 */
	public class BillerType {

		/** The biller id. */
		private Integer billerId;

		/** The biller type. */
		private String billerType;

		/**
		 * Gets the biller id.
		 * 
		 * @return the biller id
		 */
		public Integer getBillerId() {
			return billerId;
		}

		/**
		 * Sets the biller id.
		 * 
		 * @param billerId
		 *            the new biller id
		 */
		public void setBillerId(Integer billerId) {
			this.billerId = billerId;
		}

		/**
		 * Gets the biller type.
		 * 
		 * @return the biller type
		 */
		public String getBillerType() {
			return billerType;
		}

		/**
		 * Sets the biller type.
		 * 
		 * @param billerType
		 *            the new biller type
		 */
		public void setBillerType(String billerType) {
			this.billerType = billerType;
		}


	}

	/**
	 * The Class Biller.
	 */
	public class Biller {

		/** The country code. */
		private String countryCode;

		/** The biller type. */
		private String billerType;

		/** The biller id. */
		private Integer billerId;

		/** The biller name. */
		private String billerName;

		/**
		 * Gets the country code.
		 * 
		 * @return the country code
		 */
		public String getCountryCode() {
			return countryCode;
		}

		/**
		 * Sets the country code.
		 * 
		 * @param countryCode
		 *            the new country code
		 */
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		/**
		 * Gets the biller type.
		 * 
		 * @return the biller type
		 */
		public String getBillerType() {
			return billerType;
		}

		/**
		 * Sets the biller type.
		 * 
		 * @param billerType
		 *            the new biller type
		 */
		public void setBillerType(String billerType) {
			this.billerType = billerType;
		}

		/**
		 * Gets the biller id.
		 * 
		 * @return the biller id
		 */
		public Integer getBillerId() {
			return billerId;
		}

		/**
		 * Sets the biller id.
		 * 
		 * @param billerId
		 *            the new biller id
		 */
		public void setBillerId(Integer billerId) {
			this.billerId = billerId;
		}

		/**
		 * Gets the biller name.
		 * 
		 * @return the biller name
		 */
		public String getBillerName() {
			return billerName;
		}

		/**
		 * Sets the biller name.
		 * 
		 * @param billerName
		 *            the new biller name
		 */
		public void setBillerName(String billerName) {
			this.billerName = billerName;
		}

	}

	/**
	 * The Class Account.
	 */
	public class Account {

		/** The account alias. */
		public String accountAlias;

		/** The account alias type. */
		public Integer accountAliasType;

		/** The status. */
		public Integer status;
		
		public String accountNumber;
		
		public Long branch;
		public Integer bank;
		public String bankCode;

		/**
		 * Gets the account alias.
		 * 
		 * @return the account alias
		 */
		public String getAccountAlias() {
			return accountAlias;
		}

		/**
		 * Sets the account alias.
		 * 
		 * @param accountAlias
		 *            the new account alias
		 */
		public void setAccountAlias(String accountAlias) {
			this.accountAlias = accountAlias;
		}

		/**
		 * Gets the account alias type.
		 * 
		 * @return the account alias type
		 */
		public Integer getAccountAliasType() {
			return accountAliasType;
		}

		/**
		 * Sets the account alias type.
		 * 
		 * @param accountAliasType
		 *            the new account alias type
		 */
		public void setAccountAliasType(Integer accountAliasType) {
			this.accountAliasType = accountAliasType;
		}

		/**
		 * Gets the status.
		 * 
		 * @return the status
		 */
		public Integer getStatus() {
			return status;
		}

		/**
		 * Sets the status.
		 * 
		 * @param status
		 *            the new status
		 */
		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		public Long getBranch() {
			return branch;
		}

		public void setBranch(Long branch) {
			this.branch = branch;
		}

		public Integer getBank() {
			return bank;
		}

		public void setBank(Integer bank) {
			this.bank = bank;
		}
		public String getBankCode() {
			return bankCode;
		}

		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}
		
		
	}

	/**
	 * The Class Country.
	 */
	public class Country {

		/** The country code numeric. */
		private Integer countryCodeNumeric;

		/** The country name. */
		private String countryName;

		/** The isd code. */
		private Integer isdCode;

		/** The mobile number length. */
		private String mobileNumberLength;
		
		/** The list of city. */
		private ArrayList<City> listOfCity = new ArrayList<City>();
		
		/**
		 * Gets the country code numeric.
		 * 
		 * @return the country code numeric
		 */
		public Integer getCountryCodeNumeric() {
			return countryCodeNumeric;
		}

		/**
		 * Sets the country code numeric.
		 * 
		 * @param countryCodeNumeric
		 *            the new country code numeric
		 */
		public void setCountryCodeNumeric(Integer countryCodeNumeric) {
			this.countryCodeNumeric = countryCodeNumeric;
		}

		/**
		 * Gets the country name.
		 * 
		 * @return the country name
		 */
		public String getCountryName() {
			return countryName;
		}

		/**
		 * Sets the country name.
		 * 
		 * @param countryName
		 *            the new country name
		 */
		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}

		/**
		 * Gets the isd code.
		 * 
		 * @return the isd code
		 */
		public Integer getIsdCode() {
			return isdCode;
		}

		/**
		 * Sets the isd code.
		 * 
		 * @param isdCode
		 *            the new isd code
		 */
		public void setIsdCode(Integer isdCode) {
			this.isdCode = isdCode;
		}

		/**
		 * Gets the mobile number length.
		 * 
		 * @return the mobile number length
		 */
		public String getMobileNumberLength() {
			return mobileNumberLength;
		}

		/**
		 * Sets the mobile number length.
		 * 
		 * @param mobileNumberLength
		 *            the new mobile number length
		 */
		public void setMobileNumberLength(String mobileNumberLength) {
			this.mobileNumberLength = mobileNumberLength;
		}

		/**
		 * Gets the list of city.
		 *
		 * @return the list of city
		 */
		public ArrayList<City> getListOfCity() {
			return listOfCity;
		}

		/**
		 * Sets the list of city.
		 *
		 * @param listOfCity the new list of city
		 */
		public void setListOfCity(ArrayList<City> listOfCity) {
			this.listOfCity = listOfCity;
		}

	}
	
	/**
	 * The Class Currency.
	 */
	public class Currency {
		
		/** The currency id. */
		private Integer currencyId;
		
		/** The currency name. */
		private String currencyName;

		/**
		 * Gets the currency id.
		 *
		 * @return the currency id
		 */
		public Integer getCurrencyId() {
			return currencyId;
		}

		/**
		 * Sets the currency id.
		 *
		 * @param currencyId the new currency id
		 */
		public void setCurrencyId(Integer currencyId) {
			this.currencyId = currencyId;
		}

		/**
		 * Gets the currency name.
		 *
		 * @return the currency name
		 */
		public String getCurrencyName() {
			return currencyName;
		}

		/**
		 * Sets the currency name.
		 *
		 * @param currencyName the new currency name
		 */
		public void setCurrencyName(String currencyName) {
			this.currencyName = currencyName;
		}
	}

	/**
	 * The Class City.
	 */
	public class City {
		
	    /** The city id. */
    	private Integer cityId;

	    /** The city name. */
    	private String cityName;

    	/** The list ofquarter. */
		private ArrayList<Quarter> listOfQuarter = new ArrayList<Quarter>();
		
		/**
		 * Gets the city id.
		 *
		 * @return the city id
		 */
		public Integer getCityId() {
			return cityId;
		}

		/**
		 * Sets the city id.
		 *
		 * @param cityId the new city id
		 */
		public void setCityId(Integer cityId) {
			this.cityId = cityId;
		}

		/**
		 * Gets the city name.
		 *
		 * @return the city name
		 */
		public String getCityName() {
			return cityName;
		}

		/**
		 * Sets the city name.
		 *
		 * @param cityName the new city name
		 */
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}

		/**
		 * Gets the list of quarter.
		 *
		 * @return the list of quarter
		 */
		public ArrayList<Quarter> getListOfQuarter() {
			return listOfQuarter;
		}

		/**
		 * Sets the list of quarter.
		 *
		 * @param listOfQuarter the new list of quarter
		 */
		public void setListOfQuarter(ArrayList<Quarter> listOfQuarter) {
			this.listOfQuarter = listOfQuarter;
		}
		
	}
	
	/**
	 * The Class Quarter.
	 */
	public class Quarter {

	    /** The quarter id. */
    	private Long quarterId;

	    /** The quarter. */
    	private String quarterName;

		/**
		 * Gets the quarter id.
		 *
		 * @return the quarter id
		 */
		public Long getQuarterId() {
			return quarterId;
		}

		/**
		 * Sets the quarter id.
		 *
		 * @param quarterId the new quarter id
		 */
		public void setQuarterId(Long quarterId) {
			this.quarterId = quarterId;
		}

		/**
		 * Gets the quarter name.
		 *
		 * @return the quarter name
		 */
		public String getQuarterName() {
			return quarterName;
		}

		/**
		 * Sets the quarter name.
		 *
		 * @param quarterName the new quarter name
		 */
		public void setQuarterName(String quarterName) {
			this.quarterName = quarterName;
		}

	}
	
	/**
	 * The Class NetworkType.
	 */
	public class NetworkType {
		
		/** The network type id. */
		private Integer networkTypeId;
		
		/** The network type. */
		private String networkType;

		/**
		 * Gets the network type id.
		 *
		 * @return the network type id
		 */
		public Integer getNetworkTypeId() {
			return networkTypeId;
		}

		/**
		 * Sets the network type id.
		 *
		 * @param networkTypeId the new network type id
		 */
		public void setNetworkTypeId(Integer networkTypeId) {
			this.networkTypeId = networkTypeId;
		}

		/**
		 * Gets the network type.
		 *
		 * @return the network type
		 */
		public String getNetworkType() {
			return networkType;
		}

		/**
		 * Sets the network type.
		 *
		 * @param networkType the new network type
		 */
		public void setNetworkType(String networkType) {
			this.networkType = networkType;
		}
		
	}
	
	/**
	 * The Class LanguageData.
	 */
	public class LanguageData{

		/** The language code. */
		private String languageCode;
		
		/** The description. */
		private String description;
		
		/** The download url. */
		private String downloadUrl;
		
		/**
		 * Gets the language code.
		 *
		 * @return the language code
		 */
		public String getLanguageCode() {
			return languageCode;
		}
		
		/**
		 * Sets the language code.
		 *
		 * @param languageCode the new language code
		 */
		public void setLanguageCode(String languageCode) {
			this.languageCode = languageCode;
		}
		
		/**
		 * Gets the description.
		 *
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * Sets the description.
		 *
		 * @param description the new description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * Gets the download url.
		 *
		 * @return the download url
		 */
		public String getDownloadUrl() {
			return downloadUrl;
		}

		/**
		 * Sets the download url.
		 *
		 * @param downloadUrl the new download url
		 */
		public void setDownloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
		}
		
		
	}
	public List<MobileDynamicTab> getListOfDynamicTabs() {
		return listOfDynamicTabs;
	}

	public void setListOfDynamicTabs(List<MobileDynamicTab> listOfDynamicTabs) {
		this.listOfDynamicTabs = listOfDynamicTabs;
	}

	public Map<String, String> getDynamicThemeColorCode() {
		return dynamicThemeColorCode;
	}

	public void setDynamicThemeColorCode(Map<String, String> dynamicThemeColorCode) {
		this.dynamicThemeColorCode = dynamicThemeColorCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getBenificiaryAccount() {
		return benificiaryAccount;
	}

	public void setBenificiaryAccount(String benificiaryAccount) {
		this.benificiaryAccount = benificiaryAccount;
	}

	public String getBenificiaryName() {
		return benificiaryName;
	}

	public void setBenificiaryName(String benificiaryName) {
		this.benificiaryName = benificiaryName;
	}

	public Account getRecevierAccount() {
		return recevierAccount;
	}

	public void setRecevierAccount(Account recevierAccount) {
		this.recevierAccount = recevierAccount;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public Long getStampFeeDeposit() {
		return stampFeeDeposit;
	}

	public void setStampFeeDeposit(Long stampFeeDeposit) {
		this.stampFeeDeposit = stampFeeDeposit;
	}

	public String getCbsId() {
		return cbsId;
	}

	public void setCbsId(String cbsId) {
		this.cbsId = cbsId;
	}

	
	public String getCbsBankCode() {
		return cbsBankCode;
	}

	public void setCbsBankCode(String cbsBankCode) {
		this.cbsBankCode = cbsBankCode;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public List<SecurityQuestionDTO> getSecurityQuestions() {
		return securityQuestions;
	}

	public void setSecurityQuestions(List<SecurityQuestionDTO> securityQuestions) {
		this.securityQuestions = securityQuestions;
	}

	public Integer getBankId() {
		return bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public List<TitleDTO> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<TitleDTO> titleList) {
		this.titleList = titleList;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public int getApplicationStatus() {
		return applicationStatus;
	}

	public void setApplicationStatus(int applicationStatus) {
		this.applicationStatus = applicationStatus;
	}



	public String getDefaultbankId() {
		return defaultbankId;
	}

	public void setDefaultbankId(String defaultbankId) {
		this.defaultbankId = defaultbankId;
	}

	public List<HelpDesk> getHelpDeskList() {
		return helpDeskList;
	}

	public void setHelpDeskList(List<HelpDesk> helpDeskList) {
		this.helpDeskList = helpDeskList;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public Integer getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(Integer customerStatus) {
		this.customerStatus = customerStatus;
	}

	public List<String> getIdTypes() {
		return idTypes;
	}

	public void setIdTypes(List<String> idTypes) {
		this.idTypes = idTypes;
	}

	public List<CustomerBankAccount> getCustomerBankAccountList() {
		return customerBankAccountList;
	}

	public void setCustomerBankAccountList(List<CustomerBankAccount> customerBankAccountList) {
		this.customerBankAccountList = customerBankAccountList;
	}
	
	
	
}
