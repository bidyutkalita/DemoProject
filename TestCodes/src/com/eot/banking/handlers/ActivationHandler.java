/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: ActivationHandler.java,v 1.0
 *
 * Date Author Changes
 * 3 Nov, 2015, 1:18:56 PM Sambit Created
 */
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.DateUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.Branch;
import com.eot.entity.City;
import com.eot.entity.Country;
import com.eot.entity.CountryNames;
import com.eot.entity.Currency;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.LocationType;
import com.eot.entity.MobileRequest;
import com.eot.entity.Operator;
import com.eot.entity.OperatorDenomination;
import com.eot.entity.Payee;
import com.eot.entity.Quarter;
import com.eot.entity.RemittanceCompaniesTransferType;
import com.eot.entity.RemittanceCompany;
import com.eot.entity.Transaction;
import com.eot.entity.TransactionType;
import com.security.kms.security.KMSSecurityException;
import com.thinkways.util.TLVUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ActivationHandler.
 */
public class ActivationHandler extends BaseHandler {

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;

	/**
	 * Sets the utility services cleint sub.
	 * 
	 * @param utilityServicesCleintSub
	 *            the new utility services cleint sub
	 */
	public void setUtilityServicesCleintSub(UtilityServicesCleintSub utilityServicesCleintSub) {
		this.utilityServicesCleintSub = utilityServicesCleintSub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#handleRequest(byte[][], boolean, java.lang.String)
	 */
	@Override
	public byte[][] handleRequest( byte[][] requestData, boolean reinitiatedTxn ,String userName ) throws Exception{

		String applicationId = null ;
		boolean keyStored = false;
		int requestStatus = Constants.MOBREQUEST_STATUS_LOGGED ;
		byte[] responseData = null ;

		try{

			Integer transactionType = Integer.parseInt(new String(requestData[0]));
			applicationId = new String(requestData[1]);
			byte[] encryptedData = requestData[2];

			try{

				kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);

			}catch(Exception ex){

			}

			kmsHandler.unWrapAndStoreRmkWrappedKey(requestData[3], applicationId,
					Constants.KEY_VERSION, Constants.KEY_OWNER, new Date(), new Date());

			keyStored = true ;

			byte[][] plainData = TLVUtil.getAllDataFromLVArray( kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, 
					Constants.KEY_OWNER, encryptedData, false), 0 );

			Date transmissionTime = new Date(Long.parseLong(new String(plainData[0])));
			Date transactionTime = new Date(Long.parseLong(new String(plainData[1])));
			Long stan = new Long(new String(plainData[2]));
			Long rrn = new Long(new String(plainData[3]));

			System.out.println("eotMobileDao : " + eotMobileDao);
			appMaster = eotMobileDao.getApplicationType(applicationId);

			if (appMaster == null){
				throw new EOTException(ErrorConstants.INVALID_APPLICATION);
			}

			referenceType  = Integer.valueOf(appMaster.getReferenceType());

			if(appMaster.getReferenceType()== Constants.REF_TYPE_CUSTOMER || 
					appMaster.getReferenceType()== Constants.REF_TYPE_MERCHANT ){  

				customer = eotMobileDao.getCustomer(applicationId);

				if(customer == null){
					throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
				}
				if( customer.getActive() == Constants.CUSTOMER_STATUS_DEACTIVATED  ){ 
					throw new EOTException(ErrorConstants.CUSTOMER_DEACTIVATED);
				}

				List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

				if(accountList.size() == 0 ){
					throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
				}

				if(accountList.get(0).getBank().getStatus() == Constants.INACTIVE_BANK_STATUS){
					throw new EOTException(ErrorConstants.INACTIVE_BANK);
				}

			}

			if( appMaster.getStatus() != Constants.APP_STATUS_DOWNLOADED && 
					appMaster.getStatus() != Constants.APP_STATUS_ACTIVATION_SC_DEBITED 
					&& appMaster.getStatus() != Constants.APP_STATUS_NEW_PIN_SENT){  
				throw new EOTException(ErrorConstants.INVALID_APPLICATION_STATE);
			}

			if(!(DateUtil.formatDate(transmissionTime).equals(DateUtil.formatDate(new Date())) 
					&& DateUtil.formatDate(transactionTime).equals(DateUtil.formatDate(new Date())))){  
				throw new EOTException(ErrorConstants.INVALID_DATE);
			}

			mobileRequest = eotMobileDao.getRequest(applicationId ,stan ,rrn ,DateUtil.formatDate(new Date())); // Get the request from MobileRequest log table.

			if ( mobileRequest != null ){
				if( transmissionTime.getTime() <= mobileRequest.getTransmissionTime().getTime() ){  
					throw new EOTException(ErrorConstants.INVALID_TIME);
				}else{
					Blob reqBlob = mobileRequest.getResponseString() ;
					int responseStatus =  mobileRequest.getStatus() == Constants.MOBREQUEST_STATUS_SUCCESS ? Constants.MOB_RESP_STATUS_SUCCESS : Constants.MOB_RESP_STATUS_FAILURE ;
					responseData = reqBlob.getBytes(1, (int)reqBlob.length());
					return new byte[][]{(responseStatus + "").getBytes(), responseData};
				}
			}

			mobileRequest = new MobileRequest();  

			TransactionType txnType = new TransactionType();
			txnType.setTransactionType(transactionType);
			mobileRequest.setTransactionType(txnType);
			mobileRequest.setRequestString(Hibernate.createBlob(encryptedData));
			mobileRequest.setRrn(rrn);
			mobileRequest.setStan(stan);
			mobileRequest.setTransactionTime(transactionTime);
			mobileRequest.setTransmissionTime(transmissionTime);
			mobileRequest.setAppMaster(appMaster);
			mobileRequest.setReferenceId(appMaster.getReferenceId());
			mobileRequest.setReferenceType(appMaster.getReferenceType());
			mobileRequest.setStatus(Constants.MOBREQUEST_STATUS_LOGGED);

			eotMobileDao.save(mobileRequest);  // Log Mobile Request

			requestID = mobileRequest.getRequestId();

			byte[][] response = processRequest( applicationId, transactionType, plainData);

			byte[][] ret = new byte[response.length + 4][];
			ret[0] = (transmissionTime.getTime() + "").getBytes();
			ret[1] = (transactionTime.getTime() + "").getBytes();
			ret[2] = (stan + "").getBytes();
			ret[3] = (rrn + "").getBytes();
			for(int i = 0; i < response.length; i++) {
				ret[i + 4] = response[i];
			}

			byte[] plainResponseData = TLVUtil.getLVArrayFromMultipleData( ret );

			responseData =  kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, plainResponseData, true);
			requestStatus = Constants.MOBREQUEST_STATUS_SUCCESS ;

		} catch(Exception ex){
			ex.printStackTrace();
			if( keyStored ) {
				try {
					kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, 
							Constants.KEY_OWNER);
				} catch (KMSSecurityException e) {
//					e.printStackTrace();
				}
			}

			if(ex instanceof EOTException){
				requestStatus = ((EOTException)ex).getErrorCode() ;
				String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
				responseData = messageSource.getMessage("ERROR_"+requestStatus, null, new Locale(locale)).getBytes();
			}else{
				requestStatus = ErrorConstants.SERVICE_ERROR ;
				String locale = customer == null ? defaultLocale : customer.getDefaultLanguage() ;
				responseData = messageSource.getMessage("ERROR_"+requestStatus, null, new Locale(locale)).getBytes();
			}
		} finally {

			if( mobileRequest != null ){

				mobileRequest.setStatus(requestStatus) ;
				mobileRequest.setResponseString(Hibernate.createBlob(responseData));

				eotMobileDao.update(mobileRequest);

			}

		}

		int responseStatus = requestStatus == Constants.MOBREQUEST_STATUS_SUCCESS ? Constants.MOB_RESP_STATUS_SUCCESS : Constants.MOB_RESP_STATUS_FAILURE ;
		return new byte[][] { (responseStatus+"").getBytes() , responseData };

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		String userPinHash = new String(plainData[dataOffset]);

		System.out.println("userPinHash : " + userPinHash );
		System.out.println("customerPinHash : " + customer.getLoginPin() );

		if( ! userPinHash.equalsIgnoreCase(customer.getLoginPin())){
			throw new EOTException(ErrorConstants.INVALID_USER_PIN);
		}

		List<CustomerAccount> accountList = eotMobileDao.getCustomerAccounts(customer.getCustomerId());

		if(accountList.size() == 0 ){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}

		CustomerAccount account = accountList.get(0) ;

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
		serviceChargeDebitDTO.setTransactionType(transactionType.toString());
		serviceChargeDebitDTO.setAmount(0D);
		serviceChargeDebitDTO.setOtherAccount(bankAccountDto);

		try {
			utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(serviceChargeDebitDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

			appMaster.setStatus(Constants.APP_STATUS_ACTIVATION_SC_DEBITED);
			eotMobileDao.update(appMaster);
		} catch (EOTCoreException e) {
//			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}


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
