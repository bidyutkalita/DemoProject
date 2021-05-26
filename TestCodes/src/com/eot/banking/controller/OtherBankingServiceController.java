/* Copyright � EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: OtherBankingServiceController.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:57:46 PM Sambit Created
*/
package com.eot.banking.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eot.banking.dto.AccountMaintenanceDTO;
import com.eot.banking.dto.CardMaintenanceDTO;
import com.eot.banking.dto.ChangePINDTO;
import com.eot.banking.dto.ConfirmPinDTO;
import com.eot.banking.dto.CurrencyConverterDTO;
import com.eot.banking.dto.CustomerProfileDTO;
import com.eot.banking.dto.ExchangeRateDTO;
import com.eot.banking.dto.HelpDeskModelDTO;
import com.eot.banking.dto.LocateUsDTO;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.MasterDataSelfOnboard;
import com.eot.banking.dto.MobileMenuMasterDataDTO;
import com.eot.banking.dto.NumericCodeDTO;
import com.eot.banking.dto.OTPDTO;
import com.eot.banking.dto.PayeeDTO;
import com.eot.banking.dto.RecentRecipeintsDTO;
import com.eot.banking.dto.SaleDTO;
import com.eot.banking.dto.ServiceChargeDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.UploadCustomerDocument;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.dto.ReportsModel;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.ActivationService;
import com.eot.banking.service.MobileDynamicMenuService;
import com.eot.banking.service.OtherBankingService;
import com.eot.banking.utils.JSONAdaptor;
import com.eot.crypto.CheckSumUtil;
import com.eot.dtos.banking.WithdrawalDTO;
import com.eot.entity.HelpDesk;
import com.eot.entity.Transaction;
import com.security.kms.KMS;

/**
 * The Class OtherBankingServiceController.
 */
@Controller
@RequestMapping("/rest/service")
public class OtherBankingServiceController {

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/** The other banking service. */
	@Autowired
	private OtherBankingService otherBankingService;
	
	@Autowired
	private MobileDynamicMenuService mobileDynamicMenuService;
	
	@Autowired
	private ActivationService activationService;
	
	private KMS kmsHandler;
	
	public void setKmsHandler(KMS kmsHandler) {
		this.kmsHandler = kmsHandler;
	}
	
	/** The is encrypted. */

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(OtherBankingServiceController.class);

	/**
	 * Register customer.
	 *
	 * @param servletRequest the servlet request
	 * @param servletResponse the servlet response
	 * @return the customer profile dto
	 * @throws EOTException the eOT exceptiond
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@RequestMapping(value = "registerCustomer", method = RequestMethod.POST)
	public CustomerProfileDTO registerCustomer(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws EOTException, IOException {

		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream());
		String applicationId = null;
		String jsonString = null;
		CustomerProfileDTO customerProfileDTO = new JSONAdaptor().fromJSON(new String(payload), CustomerProfileDTO.class);
		try {
			applicationId = customerProfileDTO.getApplicationId();
			Integer transactionType = customerProfileDTO.getTransactionType();
			customerProfileDTO = otherBankingService.createCutomerProfile(customerProfileDTO);
			customerProfileDTO.setTransactionType(transactionType);

		} catch (EOTException e) {

			// CustomerProfileDTO error=new CustomerProfileDTO();
//			e.printStackTrace();
//			getErrorResponse(customerProfileDTO, e.getErrorCode(), e.getFieldName());
			getErrorResponse(customerProfileDTO, e.getErrorCode());
			// return error;
		} catch (Exception ex) {
			// CustomerProfileDTO error=new CustomerProfileDTO();
//			ex.printStackTrace();
			getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
			// return error;
		}
		customerProfileDTO.setApplicationId(applicationId);

		/*if (isSecured) {

			try {

				encPayload = kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, jsonString.getBytes(), true);

				payload = Base64.encode(encPayload);

			} catch (KMSSecurityException e) {
				getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
			}
		} else {
			payload = jsonString.getBytes() ;
		}*/

	
		try {

			if (true) {
				// transactionBaseDTO.setMasterKey("7sdfdiet43535j3");
				TransactionBaseDTO transactionBaseDTO = (TransactionBaseDTO) customerProfileDTO;
				try {
					String masterKey = generateCheckSumKey();
					transactionBaseDTO.setEncPayload(null);
					transactionBaseDTO.setCheckSumString(null);
					customerProfileDTO.setMasterKey(masterKey);

					TransactionBaseDTO baseDTO2 = new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO), TransactionBaseDTO.class);
					String checkSumString = CheckSumUtil.getCheckSumUtil().genrateCheckSum(masterKey, baseDTO2);
					customerProfileDTO.setCheckSumString(checkSumString);
					jsonString = new JSONAdaptor().toJSON(customerProfileDTO);

				} catch (Exception e) {
//					e.printStackTrace();
					throw new HttpMessageNotReadableException("checkSum fail ", e);
				}
			}
			jsonString = new JSONAdaptor().toJSON(customerProfileDTO);

			System.out.println(jsonString);

			byte[] encPayload = null;

			payload = jsonString.getBytes();
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();
		} catch (Exception e) {
			customerProfileDTO.setStatus(1);
			getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
		}

		customerProfileDTO.setEncPayload(payload);
		activationService.updateMobileRequest(customerProfileDTO);
		return customerProfileDTO;

	}
	/**
	 * Do login pin change.
	 * 
	 * @param changePINDTO
	 *            the change pindto
	 * @return the change pindto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "changeLoginPIN", method = RequestMethod.POST)
	public @ResponseBody ChangePINDTO doLoginPINChange( @RequestBody ChangePINDTO changePINDTO ) throws Exception {
		logger.info("************************ Inside doLoginPINChange *********************");
		try {
			changePINDTO = otherBankingService.processChangePINRequest(changePINDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(changePINDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(changePINDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return doLoginPINChange ***********************");
		return changePINDTO;
	}

	/**
	 * Do txn pin change.
	 * 
	 * @param changePINDTO
	 *            the change pindto
	 * @return the change pindto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "changeTxnPIN", method = RequestMethod.POST)
	public @ResponseBody ChangePINDTO doTxnPINChange( @RequestBody ChangePINDTO changePINDTO ) throws Exception {
		logger.info("************************ Inside doTxnPINChange *********************");
		try {
			changePINDTO.setTransactionType(20);
			changePINDTO = otherBankingService.processChangeTxnPINRequest(changePINDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(changePINDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(changePINDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return doTxnPINChange ***********************");
		return changePINDTO;
	}

	/**
	 * Reset login pin.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "resetLoginPIN", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO resetLoginPIN( @RequestBody TransactionBaseDTO transactionBaseDTO ) throws Exception {
		logger.info("************************ Inside resetLoginPIN *********************");
		try {
			transactionBaseDTO = otherBankingService.processResetLoginPINRequest(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return resetLoginPIN ***********************");
		return transactionBaseDTO;
	}

	/**
	 * Do txn pin change.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "resetTxnPIN", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO doTxnPINChange( @RequestBody TransactionBaseDTO transactionBaseDTO ) throws Exception {
		logger.info("************************ Inside resetTxnPIN *********************");
		try {
			transactionBaseDTO = otherBankingService.processResetTxnPINRequest(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return resetTxnPIN ***********************");
		return transactionBaseDTO;
	}
	
	/**
	 * Sets the default account.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @return the transaction base dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "setDefaultAccount", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO setDefaultAccount( @RequestBody TransactionBaseDTO transactionBaseDTO ) throws Exception {
		logger.info("************************ Inside setDefaultAccount *********************");
		try {
			transactionBaseDTO = otherBankingService.processSetDefaultAccount(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(transactionBaseDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return setDefaultAccount ***********************");
		return transactionBaseDTO;
	}
	
	/**
	 * Fetch payee info.
	 *
	 * @param payeeDTO the payee dto
	 * @return the payee dto
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "fetchPayeeInfo", method = RequestMethod.POST)
	public @ResponseBody PayeeDTO fetchPayeeInfo( @RequestBody PayeeDTO payeeDTO ) throws Exception {
		logger.info("************************ Inside fetchPayeeInfo *********************");
		try {
			payeeDTO = otherBankingService.fetchPayeeInfo(payeeDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(payeeDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(payeeDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return fetchPayeeInfo ***********************");
		return payeeDTO;
	}
	
	/**
	 * Adds the payee.
	 *
	 * @param payeeDTO the payee dto
	 * @return the payee dto
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "addPayee", method = RequestMethod.POST)
	public @ResponseBody PayeeDTO addPayee( @RequestBody PayeeDTO payeeDTO ) throws Exception {
		logger.info("************************ Inside addPayee *********************");
		try {
			payeeDTO = otherBankingService.processAddPayee(payeeDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(payeeDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(payeeDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return addPayee ***********************");
		return payeeDTO;
	}
	
	/**
	 * Delete payee.
	 *
	 * @param payeeDTO the payee dto
	 * @return the payee dto
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "deletePayee", method = RequestMethod.POST)
	public @ResponseBody PayeeDTO deletePayee( @RequestBody PayeeDTO payeeDTO ) throws Exception {
		logger.info("************************ Inside deletePayee *********************");
		try {
			payeeDTO = otherBankingService.processDeletePayee(payeeDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(payeeDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(payeeDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return deletePayee ***********************");
		return payeeDTO;
	}

	/**
	 * Adds the card.
	 * 
	 * @param cardMaintenanceDTO
	 *            the card maintenance dto
	 * @return the card maintenance dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "addCard", method = RequestMethod.POST)
	public @ResponseBody CardMaintenanceDTO addCard( @RequestBody CardMaintenanceDTO cardMaintenanceDTO ) throws Exception {
		logger.info("************************ Inside addCard *********************");
		try {
			cardMaintenanceDTO = otherBankingService.processAddCardRequest(cardMaintenanceDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(cardMaintenanceDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(cardMaintenanceDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return addCard ***********************");
		return cardMaintenanceDTO;
	}

	/**
	 * Confirm card.
	 * 
	 * @param cardMaintenanceDTO
	 *            the card maintenance dto
	 * @return the card maintenance dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "confirmCard", method = RequestMethod.POST)
	public @ResponseBody CardMaintenanceDTO confirmCard( @RequestBody CardMaintenanceDTO cardMaintenanceDTO ) throws Exception {
		logger.info("************************ Inside confirmCard *********************");
		try {
			cardMaintenanceDTO = otherBankingService.processConfirmCardRequest(cardMaintenanceDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(cardMaintenanceDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(cardMaintenanceDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return confirmCard ***********************");
		return cardMaintenanceDTO;
	}

	/**
	 * Delete card.
	 * 
	 * @param cardMaintenanceDTO
	 *            the card maintenance dto
	 * @return the card maintenance dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "deleteCard", method = RequestMethod.POST)
	public @ResponseBody CardMaintenanceDTO deleteCard( @RequestBody CardMaintenanceDTO cardMaintenanceDTO ) throws Exception {
		logger.info("************************ Inside deleteCard *********************");
		try {
			cardMaintenanceDTO = otherBankingService.processDeleteCardRequest(cardMaintenanceDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(cardMaintenanceDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(cardMaintenanceDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return deleteCard ***********************");
		return cardMaintenanceDTO;
	}

	/**
	 * Adds the bank account.
	 * 
	 * @param accountMaintenanceDTO
	 *            the account maintenance dto
	 * @return the account maintenance dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "addBankAccount", method = RequestMethod.POST)
	public @ResponseBody AccountMaintenanceDTO addBankAccount( @RequestBody AccountMaintenanceDTO accountMaintenanceDTO ) throws Exception {
		logger.info("************************ Inside addBankAccount *********************");
		try {
			accountMaintenanceDTO = otherBankingService.processAddBankAccountRequest(accountMaintenanceDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(accountMaintenanceDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(accountMaintenanceDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return addBankAccount ***********************");
		return accountMaintenanceDTO;
	}

	/**
	 * Delete bank account.
	 * 
	 * @param accountMaintenanceDTO
	 *            the account maintenance dto
	 * @return the account maintenance dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "deleteBankAccount", method = RequestMethod.POST)
	public @ResponseBody AccountMaintenanceDTO deleteBankAccount( @RequestBody AccountMaintenanceDTO accountMaintenanceDTO ) throws Exception {
		logger.info("************************ Inside deleteBankAccount *********************");
		try {
			accountMaintenanceDTO = otherBankingService.processDeleteBankAccountRequest(accountMaintenanceDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(accountMaintenanceDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(accountMaintenanceDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return deleteBankAccount ***********************");
		return accountMaintenanceDTO;
	}

	/**
	 * Do update profile.
	 * 
	 * @param masterDataDTO
	 *            the master data dto
	 * @return the master data dto
	 * @throws Exception
	 *             the exception
	 */
	@RequestMapping(value = "updateProfile", method = RequestMethod.POST)
	public @ResponseBody MasterDataDTO doUpdateProfile( @RequestBody MasterDataDTO masterDataDTO ) throws Exception {
		logger.info("************************ Inside doUpdateProfile *********************");
		try {
			masterDataDTO = otherBankingService.processUpdateProfileRequest(masterDataDTO);
			
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return doUpdateProfile ***********************");
		return masterDataDTO;
	}
	
	/**
	 * Gets the customer account details.
	 *
	 * @param saleDTO the sale dto
	 * @return the customer account details
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "getCustomerAccounts", method = RequestMethod.POST)
	public @ResponseBody MasterDataDTO getCustomerAccountDetails( @RequestBody SaleDTO saleDTO ) throws Exception {
		logger.info("************************ Inside getCustomerAccountDetails *********************");
		MasterDataDTO masterDataDTO = new MasterDataDTO();
		masterDataDTO.setTransactionType(saleDTO.getTransactionType());
		
		try {			
			masterDataDTO = otherBankingService.processGetCustomerAccountDetails(saleDTO);			
			masterDataDTO.setStatus(0);
		} catch (EOTException e) {
//			e.printStackTrace();
			masterDataDTO.setStatus(1);
			getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
			masterDataDTO.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}
		masterDataDTO.setApplicationId(saleDTO.getApplicationId());
		masterDataDTO.setTransactionType(saleDTO.getTransactionType());
		logger.info("************************* Return getCustomerAccountDetails ***********************");
		return masterDataDTO;
	}
	
	/**
	 * Send otp.
	 *
	 * @param otpdto the otpdto
	 * @return the otpdto
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "sendOTP", method = RequestMethod.POST)
	public @ResponseBody OTPDTO sendOTP( @RequestBody OTPDTO otpdto ) throws Exception {
		logger.info("************************ Inside sendOTP *********************");
		try {
			otpdto = otherBankingService.processSendOTP(otpdto);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(otpdto, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(otpdto, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return sendOTP ***********************");
		return otpdto;
	}
	
	/**
	 * Verify otp.
	 *
	 * @param otpdto the otpdto
	 * @return the otpdto
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "verifyOTP", method = RequestMethod.POST)
	public @ResponseBody OTPDTO verifyOTP( @RequestBody OTPDTO otpdto ) throws Exception {
		logger.info("************************ Inside verifyOTP *********************");
		try {
			otpdto = otherBankingService.processVerifyOTP(otpdto);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(otpdto, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(otpdto, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return verifyOTP ***********************");
		return otpdto;
	}
	
	/**
	 * Exchange rate.
	 *
	 * @param exchangeRateDTO the exchange rate dto
	 * @return the exchange rate dto
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "exchangeRate", method = RequestMethod.POST)
	public @ResponseBody ExchangeRateDTO exchangeRate( @RequestBody ExchangeRateDTO exchangeRateDTO ) throws Exception {
		logger.info("************************ Inside exchangeRate *********************");
		try {
			otherBankingService.getExchangeRate(exchangeRateDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(exchangeRateDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(exchangeRateDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return exchangeRate ***********************");
		return exchangeRateDTO;
	}
	
	/**
	 * Locate us.
	 *
	 * @param locateUsDTO the locate us dto
	 * @return the locate us dto
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "locateUs", method = RequestMethod.POST)
	public @ResponseBody LocateUsDTO locateUs( @RequestBody LocateUsDTO locateUsDTO ) throws Exception {
		logger.info("************************ Inside locateUs *********************");
		try {
			otherBankingService.getLocation(locateUsDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(locateUsDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(locateUsDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return locateUs ***********************");
		return locateUsDTO;
	}
	
	/**
	 * Convert currency.
	 *
	 * @param currencyConverterDTO the currency converter DTO
	 * @return the currency converter DTO
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "convertCurrency", method = RequestMethod.POST)
	public @ResponseBody CurrencyConverterDTO convertCurrency( @RequestBody CurrencyConverterDTO currencyConverterDTO ) throws Exception {
		logger.info("************************ Inside convertCurrency *********************");
		try {
			otherBankingService.convertCurrency(currencyConverterDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(currencyConverterDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(currencyConverterDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return convertCurrency ***********************");
		return currencyConverterDTO;
	}
	
	@RequestMapping(value = "numericCode", method = RequestMethod.GET)
	public @ResponseBody NumericCodeDTO getCountry(@RequestParam String cbsId, HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		
		//byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		NumericCodeDTO countryDTO =null;
		
		try {
			countryDTO = otherBankingService.getCountryCurrencyNumericCode(cbsId);
		} catch (EOTException e) {
//			e.printStackTrace();
			countryDTO =new NumericCodeDTO();
			servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
			getErrorResponse(countryDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			countryDTO =new NumericCodeDTO();
			servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(countryDTO, ErrorConstants.SERVICE_ERROR);
		}
		return countryDTO;		
		
	}


	@RequestMapping(value = "getCountry", method = RequestMethod.GET)
	public @ResponseBody NumericCodeDTO getCountryByCountryId(@RequestParam Integer cbsId, HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		NumericCodeDTO countryDTO =null;
		
		try {
			countryDTO = otherBankingService.getCountryByCountryId(cbsId);
		} catch (EOTException e) {
//			e.printStackTrace();
			countryDTO =new NumericCodeDTO();
			servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
			getErrorResponse(countryDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			countryDTO =new NumericCodeDTO();
			servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(countryDTO, ErrorConstants.SERVICE_ERROR);
		}
		return countryDTO;		
		
	}
	
	@RequestMapping(value = "getCity", method = RequestMethod.GET)
	public @ResponseBody NumericCodeDTO getCityByCityId(@RequestParam Integer cityId, HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		NumericCodeDTO countryDTO =null;
		
		try {
			countryDTO = otherBankingService.getCityByCityId(cityId);
		} catch (EOTException e) {
//			e.printStackTrace();
			
			servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
			
		} catch (Exception ex) {
//			ex.printStackTrace();
			
			servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			
		}
		return countryDTO;		
		
	}
	
	@RequestMapping(value = "getQuarter", method = RequestMethod.GET)
	public @ResponseBody NumericCodeDTO getQuarterByCountryId(@RequestParam Long quarterId, HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		NumericCodeDTO countryDTO =null;
		
		try {
			countryDTO = otherBankingService.getQuarterByQuarterId(quarterId);
		} catch (EOTException e) {
//			e.printStackTrace();
			servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
		} catch (Exception ex) {
//			ex.printStackTrace();
			servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return countryDTO;		
		
	}
	
	@RequestMapping(value = "loadDynamicMenu", method = RequestMethod.POST)
	public @ResponseBody MobileMenuMasterDataDTO getMenu( @RequestBody MasterDataDTO mobileMenuDto ) throws Exception {
		logger.info("************************ Inside convertCurrency *********************");
		MobileMenuMasterDataDTO mobileMenuMasterDataDTO= new MobileMenuMasterDataDTO();
		try {
			mobileMenuMasterDataDTO=mobileDynamicMenuService.loadMobileMenu(mobileMenuDto);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(mobileMenuDto, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(mobileMenuDto, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return dynamic Menu ***********************");
		return mobileMenuMasterDataDTO;
	}

	
	@RequestMapping(value = "validateBranch", method = RequestMethod.POST)
	public @ResponseBody MasterDataDTO validateBranch( @RequestBody MasterDataDTO masterDataDTO ) throws Exception {
		logger.info("************************ validateBranch *********************");
		MobileMenuMasterDataDTO mobileMenuMasterDataDTO= new MobileMenuMasterDataDTO();
		try {
			boolean flag=otherBankingService.validateBranch(masterDataDTO);
			if(flag)
				masterDataDTO.setStatus(0);
			else
				masterDataDTO.setStatus(1);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return validateBranch ***********************");
		return masterDataDTO;
	}
	/**
	 * Gets the error response.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @param errorCode
	 *            the error code
	 * @return the error response
	 */
	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO,Integer errorCode) {
		
		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_"+errorCode, null, 
				new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));

		return transactionBaseDTO;
	}
	
	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO,Integer errorCode, String fieldName) {
		
		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_"+errorCode, new String[] {fieldName}, 
				new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));

		return transactionBaseDTO;
	}
	
	@RequestMapping(value = "serviceCharge", method = RequestMethod.POST)
	public @ResponseBody ServiceChargeDTO getServiceCharge( @RequestBody ServiceChargeDTO serviceChargeDTO ) throws Exception {
		logger.info("##OtherBankingServiceController************************* service charge API *********************");
		try {
			serviceChargeDTO=otherBankingService.getServiceCharge(serviceChargeDTO);
			
			serviceChargeDTO.setStatus(0);
			
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(serviceChargeDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(serviceChargeDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("##OtherBankingServiceController************************* service charge API ***********************");
		return serviceChargeDTO;
	}
	

	@RequestMapping(value = "serviceChargeCalculater", method = RequestMethod.POST)
	public @ResponseBody MasterDataDTO getServiceChargeCalculater( @RequestBody MasterDataDTO masterDataDTO ) throws Exception {
		logger.info("##OtherBankingServiceController************************* getServiceChargeCalculater *********************");
		try {
			masterDataDTO=otherBankingService.getServiceChargeCalculater(masterDataDTO);
			
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("##OtherBankingServiceController************************* Return getServiceChargeCalculater ***********************");
		return masterDataDTO;
	}

	@RequestMapping(value = "getBankIdByBankCode", method = RequestMethod.POST)
	public @ResponseBody MasterDataDTO getBankIdByBankCode( @RequestBody MasterDataDTO masterDataDTO ) throws Exception {
		logger.info("************************ validateBranch *********************");
		MobileMenuMasterDataDTO mobileMenuMasterDataDTO= new MobileMenuMasterDataDTO();
		try {
			Integer bankId=otherBankingService.getBankIdByBankCode(masterDataDTO);
			if(bankId!=null) {
				masterDataDTO.setStatus(0);
				masterDataDTO.setCbsId(bankId+"");
			}
			else
				masterDataDTO.setStatus(1);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return validateBranch ***********************");
		return masterDataDTO;
	}
	
	@RequestMapping(value = "customerSelfOnboarding", method = RequestMethod.POST)
	public @ResponseBody CustomerProfileDTO customerSelfOnboarding(@RequestBody CustomerProfileDTO customerProfileDTO, HttpServletResponse servletResponse) throws EOTException, IOException {
		
		//byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		//CustomerProfileDTO customerProfileDTO = new JSONAdaptor().fromJSON(new String(payload), CustomerProfileDTO.class);
		try {
			customerProfileDTO = otherBankingService.createCutomerProfile(customerProfileDTO);
		} catch (EOTException e) {
			
//			e.printStackTrace();
		//	servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			getErrorResponse(customerProfileDTO, e.getErrorCode(), e.getFieldName());
			
		} catch (Exception ex) {
//			ex.printStackTrace();
		//	servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
		}
		customerProfileDTO.setAddressProof(null);
		customerProfileDTO.setIdProof(null);
		customerProfileDTO.setSignature(null);
		customerProfileDTO.setCustomerPhoto(null);
		return customerProfileDTO;		
		
	}
	
	
	@RequestMapping(value = "selfOnboardMasterData", method = RequestMethod.POST)
	public @ResponseBody MasterDataSelfOnboard fetchmasterData(@RequestBody MasterDataSelfOnboard masterSelfOnboard) throws EOTException, IOException {
		
		return otherBankingService.fetchSelfOnboardMasterData();		
		
	}
	@RequestMapping(value = "recent_recipients", method = RequestMethod.POST)
	public @ResponseBody RecentRecipeintsDTO recentRecipeints(@RequestBody RecentRecipeintsDTO recentRecipeintsDTO) throws EOTException, IOException {
		
		 recentRecipeintsDTO.setRecentReciepentList(otherBankingService.getRecentReciepnts(recentRecipeintsDTO));		
		 return recentRecipeintsDTO;
	}
	
	@RequestMapping(value = "updateKyc", method = RequestMethod.POST)
//	public @ResponseBody CustomerProfileDTO updateKYC( HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
	public @ResponseBody CustomerProfileDTO updateKYC( @RequestBody CustomerProfileDTO customerProfileDTO ) throws EOTException, IOException {
		
//		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
//		CustomerProfileDTO customerProfileDTO = new JSONAdaptor().fromJSON(new String(payload), CustomerProfileDTO.class);
		try {
			customerProfileDTO = otherBankingService.updateKYC(customerProfileDTO);
		} catch (EOTException e) {
			
//			e.printStackTrace();
			getErrorResponse(customerProfileDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
		}
		return customerProfileDTO;		
		
	}
	@RequestMapping(value = "fetchCustomer", method = RequestMethod.POST)
//	public @ResponseBody CustomerProfileDTO fetchCustomer( HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
	public @ResponseBody CustomerProfileDTO fetchCustomer( @RequestBody CustomerProfileDTO customerProfileDTO) throws EOTException, IOException {
		
		/*
		 * byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		 * CustomerProfileDTO customerProfileDTO = new JSONAdaptor().fromJSON(new
		 * String(payload), CustomerProfileDTO.class);
		 */
		try {
			customerProfileDTO = otherBankingService.fetchCustomer(customerProfileDTO);
		} catch (EOTException e) {
			
//			e.printStackTrace();
			getErrorResponse(customerProfileDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
		}
		return customerProfileDTO;		
		
	}
	
	@RequestMapping(value = "forgetPin", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO forgetPin( @RequestBody TransactionBaseDTO transactionBaseDTO ) throws EOTException,Exception  {
		logger.info("************************ Inside forgetPin *********************");
		try {
			transactionBaseDTO = otherBankingService.processForgetPin(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			transactionBaseDTO.setMessageDescription(getErrorResponse(transactionBaseDTO, e.getErrorCode()).getMessageDescription());
		} catch (Exception ex) {
//			ex.printStackTrace();
			transactionBaseDTO.setStatus(1);
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return forgetPin ***********************");
		return transactionBaseDTO;
	}
	
	@RequestMapping(value = "confirmPin", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO confirmPin( @RequestBody ConfirmPinDTO confirmPinDTO1 ) throws EOTException,Exception  {
		logger.info("************************ Inside confirmPin *********************");
		TransactionBaseDTO confirmPinDTO=new ConfirmPinDTO();
		int transactionType = confirmPinDTO1.getTransactionType();
		String applicationId = confirmPinDTO1.getApplicationId();
		try {
			confirmPinDTO = otherBankingService.processConfirmPin(confirmPinDTO1);
		}catch (EOTException e) {
//			e.printStackTrace();
		//	confirmPinDTO.setMessageDescription(getErrorResponse(confirmPinDTO, e.getErrorCode()).getMessageDescription());
			getErrorResponse(confirmPinDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();			
			confirmPinDTO.setStatus(1);
			getErrorResponse(confirmPinDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return confirmPin ***********************");
		confirmPinDTO.setApplicationId(applicationId);
		confirmPinDTO.setTransactionType(transactionType);
		return confirmPinDTO;
	}
	
	@RequestMapping(value = "otpForRegistration", method = RequestMethod.POST)
//	public @ResponseBody OTPDTO sendOtpForRegistration(@RequestBody OTPDTO otpDTO) throws EOTException, IOException {
	public  OTPDTO sendOtpForRegistration( HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		logger.info("##OtherBankingServiceController************************* otpForRegistration *********************");
		String applicationId = servletRequest.getHeader("applicationId");
		byte[] payload=null;
		byte[] wrappedTranEncKey = null;
		OTPDTO otpDTO=null;
		String jsonString=null;
		try {
			
//			applicationId=otpDTO.getApplicationId();

			 payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;

			/*if (isSecured) {
				
				logger.info("##Inside isSecured************************* otpForRegistration *********************");
				wrappedTranEncKey = Base64.decode(servletRequest.getHeader("wrappedTranEncKey"));
				byte[] encPayload = Base64.decode(payload) ;
				try{

					kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);

				}catch(Exception ex){

				}

				kmsHandler.unWrapAndStoreRmkWrappedKey(wrappedTranEncKey, applicationId,
						Constants.KEY_VERSION, Constants.KEY_OWNER, new Date(), new Date());
				payload =  kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, 
						Constants.KEY_OWNER, encPayload, false);

			}*/
			logger.info("Payload for reg otp======>>"+new String(payload));
			
			otpDTO = new JSONAdaptor().fromJSON(new String(payload), OTPDTO.class);
			otpDTO.setApplicationId(applicationId);
			otpDTO.setEncPayload(payload);
			otpDTO = otherBankingService.sendOtpForRegistration(otpDTO);
			
			
			
			
			
//			jsonString = new JSONAdaptor().toJSON(otpDTO) ;
//			System.out.println(jsonString);
//			payload = jsonString.getBytes() ;
			
			
			
		} catch (EOTException e) {
			e.printStackTrace();
			//servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
			getErrorResponse(otpDTO, e.getErrorCode());
		} catch (Exception ex) {
			ex.printStackTrace();
			otpDTO =new OTPDTO();
			//servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(otpDTO, ErrorConstants.SERVICE_ERROR);
		}
		
				
		if (true) {
			// transactionBaseDTO.setMasterKey("7sdfdiet43535j3");
			TransactionBaseDTO transactionBaseDTO=(TransactionBaseDTO)otpDTO;
			try {
				String masterKey = generateCheckSumKey();
				transactionBaseDTO.setEncPayload(null);
				transactionBaseDTO.setCheckSumString(null);
				otpDTO.setTransactionType(131);
				otpDTO.setMasterKey(masterKey);

				TransactionBaseDTO baseDTO2 = new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO), TransactionBaseDTO.class);
				String checkSumString = CheckSumUtil.getCheckSumUtil().genrateCheckSum(masterKey, baseDTO2);
				otpDTO.setCheckSumString(checkSumString);
				jsonString = new JSONAdaptor().toJSON(otpDTO) ;

			} catch (Exception e) {
//				e.printStackTrace();
				throw new HttpMessageNotReadableException("checkSum fail ", e);
			}
		}
		//jsonString = new JSONAdaptor().toJSON(otpDTO) ;
		System.out.println(jsonString);
		payload = jsonString.getBytes() ;
		servletResponse.getOutputStream().write(payload);
		servletResponse.getOutputStream().flush();
		servletResponse.getOutputStream().close();
		return otpDTO;		
		
	}
	
	@RequestMapping(value = "txnReports", method = RequestMethod.POST)
	public @ResponseBody ReportsModel transactionReports( @RequestBody ReportsModel transactionResponseDTO ) throws EOTException,Exception  {
		logger.info("************************ Inside txnReports *********************");
		//ReportsModel model = new ReportsModel();
		String applicationId= transactionResponseDTO.getApplicationId();
		System.out.println("Application Id::"+applicationId);
		try {
			transactionResponseDTO = otherBankingService.txnReports(transactionResponseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			transactionResponseDTO.setMessageDescription(getErrorResponse(transactionResponseDTO, e.getErrorCode()).getMessageDescription());
			transactionResponseDTO.setStatus(0);
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(transactionResponseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return txnReports ***********************");
		transactionResponseDTO.setApplicationId(applicationId);
		return transactionResponseDTO;
	}
	
	
	
	@RequestMapping(value = "customerReports", method = RequestMethod.POST)
	public @ResponseBody CustomerModel customerReports( @RequestBody CustomerModel customerResponseDTO ) throws EOTException,Exception  {
		logger.info("************************ Inside CustomerRports *********************");
		String applicationId= customerResponseDTO.getApplicationId();
		System.out.println("Application Id::"+applicationId);
		try {
			customerResponseDTO = otherBankingService.CustomerReports(customerResponseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			customerResponseDTO.setMessageDescription(getErrorResponse(customerResponseDTO, e.getErrorCode()).getMessageDescription());
		} catch (Exception ex) {
//			ex.printStackTrace();
			customerResponseDTO.setStatus(1);
			getErrorResponse(customerResponseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return CustomerRports ***********************");
		customerResponseDTO.setApplicationId(applicationId);
		return customerResponseDTO;
	}
	
	
	
	
	@RequestMapping(value = "uploadCustomerDocument", method = RequestMethod.POST)
	public  UploadCustomerDocument uploadCustomerDocument( HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		UploadCustomerDocument uploadCustomerDocDTO = new JSONAdaptor().fromJSON(new String(payload), UploadCustomerDocument.class);
		String applicationId=uploadCustomerDocDTO.getApplicationId();
		System.out.println("*********************** Inside Upload Document***************************");
		String jsonString =null;
		
		try {
			
			uploadCustomerDocDTO = otherBankingService.updoadDocument(uploadCustomerDocDTO);
			

			/*jsonString = new JSONAdaptor().toJSON(uploadCustomerDocDTO) ;
			System.out.println(jsonString);*/
			
			
			
			/*payload = jsonString.getBytes();
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();*/
		}catch (EOTException e) {
			uploadCustomerDocDTO.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(uploadCustomerDocDTO, e.getErrorCode(),e.getFieldName());
		} 
		catch (Exception e) {
			uploadCustomerDocDTO.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(uploadCustomerDocDTO, ErrorConstants.SERVICE_ERROR);
		}
		
		if (true) {
			// transactionBaseDTO.setMasterKey("7sdfdiet43535j3");
			TransactionBaseDTO transactionBaseDTO=(TransactionBaseDTO)uploadCustomerDocDTO;
			try {
				String masterKey = generateCheckSumKey();
				transactionBaseDTO.setEncPayload(null);
				transactionBaseDTO.setCheckSumString(null);
				uploadCustomerDocDTO.setMasterKey(masterKey);

				TransactionBaseDTO baseDTO2 = new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO), TransactionBaseDTO.class);
				String checkSumString = CheckSumUtil.getCheckSumUtil().genrateCheckSum(masterKey, baseDTO2);
				uploadCustomerDocDTO.setCheckSumString(checkSumString);
				jsonString = new JSONAdaptor().toJSON(uploadCustomerDocDTO) ;

			} catch (Exception e) {
//				e.printStackTrace();
				throw new HttpMessageNotReadableException("checkSum fail ", e);
			}
		}
		System.out.println(jsonString);
		payload = jsonString.getBytes();
		servletResponse.getOutputStream().write(payload);
		servletResponse.getOutputStream().flush();
		servletResponse.getOutputStream().close();

		uploadCustomerDocDTO.setEncPayload(payload);
		activationService.updateMobileRequest(uploadCustomerDocDTO);
		return uploadCustomerDocDTO;		
		
	}
	public static String generateCheckSumKey(){
        Random random =new Random();
        String alphabet = "qwertyuioplkjhgfdsazxcvbnm";
        int size = 16;
        char [] chars =new char[size];
        for (int i = 0; i < size; i++) {
            chars [i]= alphabet.charAt(random.nextInt(alphabet.length()));
        }
        return new String(chars);
    }
	
	@RequestMapping(value = "customerCareMobileNumbers", method = RequestMethod.GET)
	public HelpDeskModelDTO getCustomerCareMobileNumbers(HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		UploadCustomerDocument uploadCustomerDocDTO = new JSONAdaptor().fromJSON(new String(payload), UploadCustomerDocument.class);
		System.out.println("*********************** Inside customerCareMobileNumberst***************************");
		String jsonString ="";
		HelpDeskModelDTO helpDeskModelDTO = new HelpDeskModelDTO();
		try {
			List<HelpDesk> helpDeskList = otherBankingService.getHelpDeskDetails();
			helpDeskModelDTO.setHelpDeskList(helpDeskList);
			helpDeskModelDTO.setStatus(0);
			jsonString = new JSONAdaptor().toJSON(helpDeskModelDTO) ;
		} catch (Exception ex) {
			helpDeskModelDTO.setStatus(1);
			ex.printStackTrace();
			getErrorResponse(helpDeskModelDTO, ErrorConstants.SERVICE_ERROR);
		}
		System.out.println(jsonString);
		payload = jsonString.getBytes();
		servletResponse.getOutputStream().write(payload);
		servletResponse.getOutputStream().flush();
		servletResponse.getOutputStream().close();
		helpDeskModelDTO.setEncPayload(payload);
		return helpDeskModelDTO;					
	}
	
	@RequestMapping(value = "loadWithdrawalTransaction", method = RequestMethod.POST)
	public @ResponseBody WithdrawalTransactionsDTO loadTransactionWithdrawByAgentCode(@RequestBody WithdrawalTransactionsDTO withdrawalTransactionsDTO){
		
		
		System.out.println("*********************** Inside loadTransactionWithdrawByAgentCode***************************");
		
			List<ReversalTransactionDTO> withdrawTransactions=null;
			try {
				withdrawTransactions = otherBankingService.getTransactionforReversal(withdrawalTransactionsDTO);
				withdrawalTransactionsDTO.setWithdrawTransactions(withdrawTransactions);
				withdrawalTransactionsDTO.setStatus(0);
			} catch (EOTException e) {
//				e.printStackTrace();
				withdrawalTransactionsDTO.setWithdrawTransactions(withdrawTransactions);
				withdrawalTransactionsDTO.setStatus(1);
				withdrawalTransactionsDTO.setMessageDescription("No Transaction Found");
			}
			
//			jsonString = new JSONAdaptor().toJSON(withdrawalTransactionsDTO) ;
		
//		System.out.println(jsonString);
		return withdrawalTransactionsDTO;					
	}

}
