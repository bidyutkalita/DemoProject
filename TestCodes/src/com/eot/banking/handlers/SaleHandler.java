/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SaleHandler.java,v 1.0
*
* Date Author Changes
* 3 Nov, 2015, 1:21:51 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.server.data.OtpDTO;
import com.eot.banking.utils.DateUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.BankingServiceClientStub;
import com.eot.dtos.banking.SaleDTO;
import com.eot.entity.ClearingHousePoolMember;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.Otp;
import com.eot.entity.Transaction;
import com.security.kms.security.KMSSecurityException;
import com.thinkways.util.HexString;

// TODO: Auto-generated Javadoc
/**
 * The Class SaleHandler.
 */
public class SaleHandler extends BaseHandler {

	/** The banking service client stub. */
	@Autowired
	private BankingServiceClientStub bankingServiceClientStub;
	
	/** The otp for sale enabled. */
	@Autowired
	private boolean otpForSaleEnabled ;

	/**
	 * Sets the banking service client stub.
	 * 
	 * @param bankingServiceClientStub
	 *            the new banking service client stub
	 */
	public void setBankingServiceClientStub(BankingServiceClientStub bankingServiceClientStub) {
		this.bankingServiceClientStub = bankingServiceClientStub;
	}

	/**
	 * Sets the otp for sale enabled.
	 * 
	 * @param otpForSaleEnabled
	 *            the new otp for sale enabled
	 */
	public void setOtpForSaleEnabled(boolean otpForSaleEnabled) {
		this.otpForSaleEnabled = otpForSaleEnabled;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public  byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		String toAlias = new String(plainData[dataOffset++]);
		Integer toAliasType = Integer.parseInt(new String(plainData[dataOffset++]));
		String fromAlias = new String(plainData[dataOffset++]);
		Integer fromAliasType = Integer.parseInt(new String(plainData[dataOffset++]));
		String customerMobileNumer = new String(plainData[dataOffset++]);
		Long amount = Long.parseLong(new String(plainData[dataOffset++]));
		String customerPin = new String(plainData[dataOffset++]);
		String merchantPin = new String(plainData[dataOffset++]);
		System.out.println("*********** SaleHandler *************");

		Customer saleCust= eotMobileDao.getCustomerFromMobileNo(customerMobileNumer);
		if(saleCust==null){
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}

		if((customer.getCountry().getIsdCode()+customer.getMobileNumber()).equalsIgnoreCase(saleCust.getMobileNumber())){
			throw new EOTException(ErrorConstants.SAME_MOBILE_NO);
		}

		if(!merchantPin.equals(customer.getTransactionPin().toString())){
			throw new EOTException(ErrorConstants.INVALID_TXN_PIN);
		}

		if(otpForSaleEnabled){
			OtpDTO otpDto=new OtpDTO();
			otpDto.setOtphash(customerPin);
			otpDto.setReferenceId(saleCust.getCustomerId()+"");
			otpDto.setReferenceType(Constants.REF_TYPE_CUSTOMER);
			otpDto.setOtpType(Constants.OTP_TYPE_CUSTOMER);
			Otp otp =	eotMobileDao.verifyOTP(otpDto);
			System.out.println("customerPin - " + customerPin );
			System.out.println("db - " + otpDto.getOtphash());
			if(otp== null){
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_OTP);
			}
		}else{

			if(!customerPin.equals(saleCust.getTransactionPin().toString())){
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_TXN_PIN);
			}

		}

		com.eot.dtos.common.Account customerAccount = new com.eot.dtos.common.Account();

		customerAccount.setAccountAlias(fromAlias);
		customerAccount.setAccountType(fromAliasType.toString());

		if( Constants.ALIAS_TYPE_CARD_ACC == fromAliasType ){

			CustomerCard card = eotMobileDao.getCustomerCardFromAlias(saleCust.getCustomerId(), fromAlias);
			if(card == null){
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			try {
				customerAccount.setAccountNO(new String( kmsHandler.externalDbDesOperation( HexString.hexToBuffer(card.getCardNumber()), false)));
			} catch (NumberFormatException e) {
//				e.printStackTrace();
			} catch (KMSSecurityException e) {
//				e.printStackTrace();
			}
			customerAccount.setBankCode(card.getBank().getBankId().toString());

		}else if( Constants.ALIAS_TYPE_MOBILE_ACC == fromAliasType ){

			CustomerAccount walletAccount = eotMobileDao.getAccountFromAccountAlias(saleCust.getCustomerId(), fromAlias);

			if(walletAccount == null){
				throw new EOTException(ErrorConstants.MERCHANT_ACCOUNT_NOT_FOUND);
			}

			customerAccount.setAccountNO(walletAccount.getAccountNumber());
			customerAccount.setBankCode(walletAccount.getBank().getBankId().toString());
			customerAccount.setBranchCode(walletAccount.getBranch().getBranchId().toString());

		}

		com.eot.dtos.common.Account merchantAccount = new com.eot.dtos.common.Account();

		merchantAccount.setAccountAlias(toAlias);
		merchantAccount.setAccountType(toAliasType.toString());

		if( Constants.ALIAS_TYPE_CARD_ACC == toAliasType ){

			CustomerCard card = eotMobileDao.getCustomerCardFromAlias(customer.getCustomerId(), toAlias);
			if(card == null){
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			merchantAccount.setAccountNO(card.getCardNumber());
			merchantAccount.setBankCode(card.getBank().getBankId().toString());

		}else if( Constants.ALIAS_TYPE_MOBILE_ACC == toAliasType ){

			CustomerAccount walletAccount = eotMobileDao.getAccountFromAccountAlias(customer.getCustomerId(), toAlias);

			if(walletAccount == null){
				throw new EOTException(ErrorConstants.CUSTOMER_ACCOUNT_NOT_FOUND);
			}

			merchantAccount.setAccountNO(walletAccount.getAccountNumber());
			merchantAccount.setBankCode(walletAccount.getBank().getBankId().toString());
			merchantAccount.setBranchCode(walletAccount.getBranch().getBranchId().toString());

		}

		List<ClearingHousePoolMember> chPoolList = eotMobileDao.getClearingHouse(new Integer(customerAccount.getBankCode()), new Integer(merchantAccount.getBankCode()));

		if(chPoolList==null){
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}

		/*ClearingHousePool clearingHousePool = eotMobileDao.getClearingHouse(chPoolList.get(0).getClearingPoolId());

		if(clearingHousePool==null){
			throw new EOTException(ErrorConstants.INVALID_CH_POOL);
		}*/

		SaleDTO saleDTO = new SaleDTO();

		saleDTO.setTransactionType(transactionType.toString());
		saleDTO.setAmount(amount.doubleValue());
		saleDTO.setCustomerAccount(customerAccount);
		saleDTO.setOtherAccount(merchantAccount);
		saleDTO.setCustomerID(saleCust.getCustomerId().toString());
		saleDTO.setRequestID(requestID.toString());
		saleDTO.setReferenceID(customer.getCustomerId().toString());
		saleDTO.setReferenceType(referenceType);
		saleDTO.setTransDesc("Sale");
		saleDTO.setChannelType(Constants.EOT_CHANNEL);

		try {

			saleDTO = bankingServiceClientStub.sale(saleDTO);

			Transaction txn = new Transaction();
			txn.setTransactionId(new Long(saleDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

			//	return packResponse(toAlias, fromAlias, amount, customer.getDefaultLanguage());
			return packResponse(customer.getCustomerId().toString(),customer.getFirstName(),customer.getCity().getCity(),saleCust.getFirstName(),saleCust.getMobileNumber(),
					fromAlias, amount,saleDTO.getTransactionNO(), customer.getDefaultLanguage());
		} catch (EOTCoreException e) {
//			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

	}

	/*public byte[][] packResponse(String toAlias,String fromAlias,Long amount,String defaultLang) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("SALE_SUCCESS", new String[]{fromAlias,toAlias,amount.toString()}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}*/

	/**
	 * Pack response.
	 * 
	 * @param merchantId
	 *            the merchant id
	 * @param merchantName
	 *            the merchant name
	 * @param merchantCity
	 *            the merchant city
	 * @param customerName
	 *            the customer name
	 * @param CustomerMobileNo
	 *            the customer mobile no
	 * @param fromAlias
	 *            the from alias
	 * @param amount
	 *            the amount
	 * @param txnAuthCode
	 *            the txn auth code
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(String merchantId,String merchantName,String merchantCity,String customerName,String CustomerMobileNo,String fromAlias,
			Long amount,String txnAuthCode,String defaultLang) throws UnsupportedEncodingException {

		String message = messageSource.getMessage("SALE_SUCCESS", new String[]{DateUtil.formattedDateAndTime(new Date()),merchantId,merchantName,merchantCity,customerName,CustomerMobileNo,
				fromAlias,amount.toString(),txnAuthCode}, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};
	}
}
