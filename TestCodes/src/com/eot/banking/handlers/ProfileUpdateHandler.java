/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ProfileUpdateHandler.java,v 1.0
*
* Date Author Changes
* 3 Nov, 2015, 1:21:03 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.Branch;
import com.eot.entity.City;
import com.eot.entity.Country;
import com.eot.entity.CountryNames;
import com.eot.entity.Currency;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.LocationType;
import com.eot.entity.Operator;
import com.eot.entity.OperatorDenomination;
import com.eot.entity.Payee;
import com.eot.entity.Quarter;
import com.eot.entity.RemittanceCompaniesTransferType;
import com.eot.entity.RemittanceCompany;

// TODO: Auto-generated Javadoc
/**
 * The Class ProfileUpdateHandler.
 */
public class ProfileUpdateHandler extends BaseHandler {

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;

	/**
	 * Sets the utility services cleint sub.
	 * 
	 * @param utilityServicesCleintSub
	 *            the new utility services cleint sub
	 */
	public void setUtilityServicesCleintSub( UtilityServicesCleintSub utilityServicesCleintSub) {
		this.utilityServicesCleintSub = utilityServicesCleintSub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		System.out.println("******** ProfileUpdateHandler *************");

		String appVersion = new String(plainData[dataOffset++]);

		Customer customer = eotMobileDao.getCustomer(applicationId);

		if(customer==null){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}

		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		//		accountDto.setAccountNO(accountList.get(0).getAccountNumber());
		//		accountDto.setBankCode(accountList.get(0).getBank().getBankId().toString());
		//		accountDto.setBranchCode(accountList.get(0).getBranch().getBranchId().toString());
		//
		//		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();
		//
		//		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		//		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
		//		serviceChargeDebitDTO.setReferenceType(referenceType);
		//		serviceChargeDebitDTO.setRequestID(requestID.toString());
		//		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		//		serviceChargeDebitDTO.setTransactionType(transactionType.toString());
		//		serviceChargeDebitDTO.setAmount(0L);
		//
		//		try {
		//			utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);
		//
		//			Transaction txn = new Transaction();
		//			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
		//			mobileRequest.setTransaction(txn);
		//
		//		} catch (EOTCoreException e) {
		//			e.printStackTrace();
		//			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		//		}

		List<CustomerCard> cardList = eotMobileDao.getCardDetails(customer.getCustomerId());
		List<Payee> payeeList = eotMobileDao.getPayeeList(customer.getCustomerId());
		List<Bank> bankList = eotMobileDao.getBanks();
		List<Branch> branchList = eotMobileDao.getBranchs();
		List<Operator> operatorList = eotMobileDao.getOperatorList(customer.getCountry().getCountryId());
		List<Country> countryList = eotMobileDao.getAllCountry();
		List<City> cityList = eotMobileDao.getAllCity();
		List<Quarter> quarterList = eotMobileDao.getAllQuarter();
		List<Biller> billerList = eotMobileDao.getBillerList(customer.getCountry().getCountryId());
		List<CustomerBankAccount> customerBankAccountList = eotMobileDao.getBankAccountDetails(customer.getCustomerId());
		List<Currency> currencyList = eotMobileDao.getAllCurrency();
		List<LocationType> locationTypeList = eotMobileDao.getAllActiveLocationType(customer.getDefaultLanguage().substring(0, 2), Constants.ACTIVE_STATUS);
		List<RemittanceCompany> remittanceCompanies = eotMobileDao.getAllRemittanceCompany();

		try {
			return packResponse( accountList, payeeList, cardList, bankList, branchList, operatorList, countryList, billerList, customerBankAccountList,currencyList,locationTypeList,cityList,quarterList,remittanceCompanies);
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
	}

	/**
	 * Pack response.
	 *
	 * @param eotAccounts the eot accounts
	 * @param payeeList the payee list
	 * @param cards            the cards
	 * @param banks            the banks
	 * @param branchs            the branchs
	 * @param operators            the operators
	 * @param countries            the countries
	 * @param billers            the billers
	 * @param bankAccountDetails            the bank account details
	 * @param currencyList the currency list
	 * @param locationTypeList the location type list
	 * @param cityList the city list
	 * @param quarterList the quarter list
	 * @param remittanceCompanies the remittance companies
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@SuppressWarnings("unchecked")
	private byte[][] packResponse( List<CustomerAccount> eotAccounts, List<Payee> payeeList, List<CustomerCard> cards, List<Bank> banks,List<Branch> branchs, List<Operator> operators,List<Country> countries,List<Biller> billers, List<CustomerBankAccount> bankAccountDetails, List<Currency> currencyList, List<LocationType> locationTypeList, List<City> cityList, List<Quarter> quarterList,List<RemittanceCompany> remittanceCompanies) throws UnsupportedEncodingException {

		ArrayList<String> data = new ArrayList<String>();
		data.add(eotAccounts.get(0).getAccount().getAlias() + "");
		data.add(eotAccounts.size() + "");
		for (CustomerAccount eotAccount : eotAccounts) {
			data.add(eotAccount.getAccount().getAlias());
			data.add(eotAccount.getBranch().getBranchId() + "");
		}
		data.add(payeeList.size() + "");
		for (Payee payee : payeeList) {
			data.add(payee.getAlias());
			data.add(payee.getAccountNumber());
			data.add(payee.getAccountHolderName());
			data.add(Constants.ALIAS_TYPE_MOBILE_ACC+"");
			data.add(Constants.ACTIVE+"");
		}
		data.add(cards.size() + "");
		for (CustomerCard card : cards) {
			data.add(card.getAlias());
			data.add((card.getStatus()) + "");	

		}
		data.add(banks.size() + "");
		for (Bank bank : banks) {
			data.add(bank.getBankId() + "");
			data.add(bank.getBankName());
			data.add(bank.getBankName());
		}
		data.add(branchs.size() + "");
		for (Branch branch : branchs) {
			data.add(branch.getBranchId() + "");
			data.add(branch.getBank().getBankId() + "");
			data.add(branch.getLocation());
		}
		data.add(operators.size() + "");
		for (Operator operator : operators) {
			data.add(operator.getCountry().getCountryId() + "");
			data.add(operator.getOperatorId() + "");
			data.add(operator.getOperatorName());
			data.add(operator.getOperatorName());
			data.add(join(operator.getOperatorDenominations(), "|"));
		}
		data.add(countries.size() + "");
		for (Country country : countries) {
			data.add(country.getCountryId()+"");
			data.add(getCountryByLanguage(country.getCountryNames(),customer.getDefaultLanguage()));
			data.add(country.getIsdCode()+"");
			data.add(country.getMobileNumberLength()+"");
		}
		data.add(billers.size() + "");
		for (Biller biller : billers) {
			data.add(biller.getBillerType().getBillerTypeId()+"");
			data.add(biller.getBillerType().getBillerType());
		}
		data.add(billers.size() + "");
		for (Biller biller : billers) {
			data.add(biller.getCountry().getCountryId()+"");
			data.add(biller.getBillerId()+"");
			data.add(biller.getBillerName());
			data.add(biller.getBillerType().getBillerTypeId()+"");
		}
		data.add(bankAccountDetails.size() + "");
		for (CustomerBankAccount customerBankAccount : bankAccountDetails) {
			data.add(customerBankAccount.getAlias());
			data.add((customerBankAccount.getStatus()) + "");	

		}
		data.add(locationTypeList.size() + "");
		for(LocationType locationType : locationTypeList){
			data.add(locationType.getLocationTypeId()+"");
			data.add(locationType.getLocationType());
		}
		data.add(currencyList.size() + "");
		for (Currency currency : currencyList){
			data.add(currency.getCurrencyId()+"");
			data.add(currency.getCurrencyName());

		}

		data.add(cityList.size() + "");
		for(City city : cityList){
			data.add(city.getCityId()+"");
			data.add(city.getCity());
			data.add(city.getCountry().getCountryId()+"");
		}
		data.add(quarterList.size() + "");
		for(Quarter quarter : quarterList){
			data.add(quarter.getQuarterId()+"");
			data.add(quarter.getQuarter());
			data.add(quarter.getCity().getCityId()+"");
			data.add(quarter.getCity().getCountry().getCountryId()+"");

		}
		data.add(remittanceCompanies.size() + "");
		for(RemittanceCompany remittanceCompany : remittanceCompanies){
			data.add(remittanceCompany.getRemittanceCompanyId()+"");
			data.add(remittanceCompany.getRemittanceCompanyName());
			data.add(joinTrfType(remittanceCompany.getRemittanceCompaniesTransferTypes(), "|"));
		}

		System.out.println(data);
		byte[][] response = new byte[data.size()][];
		for(int i = 0; i < response.length; i++) {
			response[i] = data.get(i).getBytes("UTF8");
		}

		return response;
	}
	
	/**
	 * Join.
	 * 
	 * @param denominations
	 *            the denominations
	 * @param delimiter
	 *            the delimiter
	 * @return the string
	 */
	private String join( Set<OperatorDenomination> denominations, String delimiter ) {

		StringBuffer ret = new StringBuffer();
		for (OperatorDenomination denomination : denominations) {
			if(denomination.getActive()==1){
				ret.append(denomination.getDenomination() + "");
				ret.append(delimiter);
			}
		}
		if( ret.length() != 0 ) {

			return ret.substring(0,ret.length()-1);
		}
		else {

			return ret.toString();
		}
	}
	
	/**
	 * Join trf type.
	 *
	 * @param transferTypes the transfer types
	 * @param delimiter the delimiter
	 * @return the string
	 */
	private String joinTrfType( Set<RemittanceCompaniesTransferType> transferTypes, String delimiter ) {

		StringBuffer ret = new StringBuffer();
		for (RemittanceCompaniesTransferType transferType : transferTypes) {
			ret.append(transferType.getComp_id().getTransferTypeId() + "");
			ret.append(delimiter);
		}
		if( ret.length() != 0 ) {

			return ret.substring(0,ret.length()-1);
		}
		else {

			return ret.toString();
		}
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
}