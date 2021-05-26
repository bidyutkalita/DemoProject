/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: AddCardHandler.java,v 1.0
*
* Date Author Changes
* 28 Oct, 2015, 3:46:51 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.EOTUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.dtos.sms.AddCardAlertDTO;
import com.eot.dtos.utilities.ServiceChargeDebitDTO;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.TransactionType;
import com.eot.smsclient.webservice.SmsServiceClientStub;
import com.security.kms.security.KMSSecurityException;
import com.thinkways.util.HexString;

// TODO: Auto-generated Javadoc
/**
 * The Class AddCardHandler.
 */
public class AddCardHandler extends BaseHandler {

	/** The sms service client stub. */
	@Autowired
	private SmsServiceClientStub smsServiceClientStub ;

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

	/**
	 * Sets the sms service client stub.
	 * 
	 * @param smsServiceClientStub
	 *            the new sms service client stub
	 */
	public void setSmsServiceClientStub(SmsServiceClientStub smsServiceClientStub) {
		this.smsServiceClientStub = smsServiceClientStub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer requestType, byte[][] plainData) throws EOTException {

		System.out.println("******** AddCardHandler *************");

		String cardAlias = new String(plainData[dataOffset++]);
		String cardNo = new String(plainData[dataOffset++]);
		String cvv = new String(plainData[dataOffset++]);
		Long dt = new Long(new String(plainData[dataOffset++]));
		String dateOfExpiry = new String(new SimpleDateFormat("MMyy").format(new Date(dt)));

		Customer customer = eotMobileDao.getCustomer(applicationId);

		if(customer==null){
			throw new EOTException(5011);
		}

		CustomerCard card=null;
		try {
			card = eotMobileDao.getCustomerCardByCardNumber(HexString.bufferToHex(kmsHandler.externalDbDesOperation(cardNo.getBytes(),true) ));
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
			card.setCardNumber(HexString.bufferToHex(kmsHandler.externalDbDesOperation(cardNo.getBytes(),true) ));
			card.setCvv(HexString.bufferToHex(kmsHandler.externalDbDesOperation(cvv.getBytes(),true) ));
			card.setCardExpiry(HexString.bufferToHex(kmsHandler.externalDbDesOperation(dateOfExpiry.getBytes(),true) ));
		} catch (KMSSecurityException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}
		card.setReferenceId(customer.getCustomerId()+"");
		card.setReferenceType(0);
		card.setStatus(Constants.CARD_STATUS_NOT_CONFIRMED);
		CustomerAccount customerAccount = (CustomerAccount) customer.getCustomerAccounts().iterator().next();
		card.setBank(customerAccount.getBank());

		Integer confirmCode = EOTUtil.generateConfirmationCode();
		card.setNameOnCard(confirmCode+"");

		eotMobileDao.save(card);

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();
		accountDto.setAccountNO(card.getCardNumber());
		accountDto.setBankCode(card.getBank().getBankId().toString());
		accountDto.setAccountType(Constants.ALIAS_TYPE_CARD_ACC+"");

		ServiceChargeDebitDTO serviceChargeDebitDTO = new ServiceChargeDebitDTO();

		serviceChargeDebitDTO.setCustomerAccount(accountDto);
		serviceChargeDebitDTO.setReferenceID(customer.getCustomerId().toString());
		serviceChargeDebitDTO.setReferenceType(referenceType);
		serviceChargeDebitDTO.setRequestID(requestID.toString());
		serviceChargeDebitDTO.setChannelType(Constants.EOT_CHANNEL);
		serviceChargeDebitDTO.setTransactionType(requestType.toString());
		serviceChargeDebitDTO.setAmount(0D);

		try {
			serviceChargeDebitDTO =	utilityServicesCleintSub.serviceChargeDebit(serviceChargeDebitDTO);

		} catch (EOTCoreException e) {
//			e.printStackTrace();
			eotMobileDao.delete(card);
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		}

		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(requestType);
		mobileRequest.setTransactionType(transactionType);

		try {
			AddCardAlertDTO dto = new AddCardAlertDTO() ;
			dto.setCardNumber(cardNo);
			dto.setConfirmationCode(confirmCode+"");
			dto.setLocale(customer.getDefaultLanguage());
			dto.setMobileNo(serviceChargeDebitDTO.getChannelType());
			dto.setScheduleDate(Calendar.getInstance());

			smsServiceClientStub.addCardAlert(dto);

		} catch (Exception e) {
			throw new EOTException(ErrorConstants.SMS_ALERT_FAILED);
		}

		return packResponse(customer.getDefaultLanguage());
	}

	/**
	 * Pack response.
	 * 
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 */
	public byte[][] packResponse(String defaultLang) {

		String message = messageSource.getMessage("ADD_CARD_SUCCESS", null, new Locale(defaultLang));

		return new byte[][] {message.getBytes()};

	}
}
