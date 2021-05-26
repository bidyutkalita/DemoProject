/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: ActivationController.java,v 1.0
 *
 * Date Author Changes
 * 21 Oct, 2015, 2:57:31 PM Sambit Created
 */
package com.eot.banking.controller;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.MobileMenuMasterDataDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.ActivationService;
import com.eot.banking.service.LoginService;
import com.eot.banking.service.MobileDynamicMenuService;
import com.eot.banking.utils.JSONAdaptor;
import com.eot.crypto.CheckSumUtil;
import com.eot.entity.Customer;
import com.security.kms.KMS;
import com.security.kms.security.KMSSecurityException;

/**
 * The Class ActivationController.
 */
@Controller
@RequestMapping("/rest/act")
public class ActivationController {

	/** The logger. */
	private KMS kmsHandler;

	/** The activation service. */
	@Autowired
	private ActivationService activationService;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private MobileDynamicMenuService mobileDynamicMenuService;
	
	@Autowired
	private LoginService loginService;

	/**
	 * Sets the kms handler.
	 *
	 * @param kmsHandler the new kms handler
	 */
	public void setKmsHandler(KMS kmsHandler) {
		this.kmsHandler = kmsHandler;
	}

	/** The is encrypted. */
	private boolean isSecured =true;
	private boolean isCheckSumEnabled = true;
	/**
	 * Activate.
	 * 
	 * @param servletRequest
	 *            the servlet request
	 * @param servletResponse
	 *            the servlet response
	 * @return the master data dto
	 */
	@RequestMapping(value = "activate", method = RequestMethod.POST)
	public MasterDataDTO activate(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

		String applicationId = servletRequest.getHeader("applicationId");

		byte[] wrappedTranEncKey = null;

		MasterDataDTO masterDataDTO = null;
		try {

			byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;

			if (isSecured) {

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

			}
			masterDataDTO = new JSONAdaptor().fromJSON(new String(payload), MasterDataDTO.class);
			masterDataDTO.setApplicationId(applicationId);
			masterDataDTO.setEncPayload(payload);
			masterDataDTO = (MasterDataDTO) activationService.processActivationRequest(masterDataDTO);
		/*
		 * No need for loading theme and menu
		 * 	try
			{
				MobileMenuMasterDataDTO mobileMenuMasterDto=mobileDynamicMenuService.loadMobileMenu(masterDataDTO);
				Map theamConfigMap=mobileDynamicMenuService.loadTheamColorConfig(masterDataDTO);
				masterDataDTO.setListOfDynamicTabs(mobileMenuMasterDto.getListOfDynamicTabs());
				masterDataDTO.setDynamicThemeColorCode(theamConfigMap);
			}
			catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}*/

		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}		

		String jsonString = new JSONAdaptor().toJSON(masterDataDTO) ;

		System.out.println(jsonString);

		byte[] payload = null ;
		byte[] encPayload = null;
		if (isCheckSumEnabled) {
			// transactionBaseDTO.setMasterKey("7sdfdiet43535j3");
			TransactionBaseDTO transactionBaseDTO=(TransactionBaseDTO)masterDataDTO;
			try {
				String masterKey = generateCheckSumKey();
				transactionBaseDTO.setEncPayload(null);
				transactionBaseDTO.setCheckSumString(null);
				masterDataDTO.setMasterKey(masterKey);

				TransactionBaseDTO baseDTO2 = new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO), TransactionBaseDTO.class);
				String checkSumString = CheckSumUtil.getCheckSumUtil().genrateCheckSum(masterKey, baseDTO2);
				masterDataDTO.setCheckSumString(checkSumString);
				jsonString = new JSONAdaptor().toJSON(masterDataDTO) ;

			} catch (Exception e) {
//				e.printStackTrace();
				throw new HttpMessageNotReadableException("checkSum fail ", e);
			}
		}


		if (isSecured) {

			try {

				encPayload = kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, jsonString.getBytes(), true);

				payload = Base64.encode(encPayload);

			} catch (KMSSecurityException e) {
				getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
			}
		} else {
			payload = jsonString.getBytes() ;
		}

		try {
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();
		} catch (Exception e) {
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}

		masterDataDTO.setEncPayload(payload);
		activationService.updateMobileRequest(masterDataDTO);

		return masterDataDTO;

	}

	@RequestMapping(value = "setUpApplication", method = RequestMethod.POST)
	public MasterDataDTO setUpApplication(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		System.out.println("************************************* set up application ************************");
		String identificationNumber = servletRequest.getHeader("identificationNumber");
		String applicationId = servletRequest.getHeader("applicationId");
		byte[] wrappedTranEncKey = null;

		MasterDataDTO masterDataDTO = new MasterDataDTO();
		try {
			
			Customer customer = loginService.loadCustomerByMobileNumber(identificationNumber);
			if (customer == null) {
				throw new EOTException(ErrorConstants.INVALID_CUSTOMER_MOBILE_NUMBER);
			}
			applicationId=customer.getAppId();
			byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;

			if (isSecured) {
				wrappedTranEncKey = Base64.decode(servletRequest.getHeader("wrappedTranEncKey"));
				byte[] encPayload = Base64.decode(payload) ;
				try{
					kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				kmsHandler.unWrapAndStoreRmkWrappedKey(wrappedTranEncKey, applicationId,
						Constants.KEY_VERSION, Constants.KEY_OWNER, new Date(), new Date());
				payload =  kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, 
						Constants.KEY_OWNER, encPayload, false);
			}
			masterDataDTO = new JSONAdaptor().fromJSON(new String(payload), MasterDataDTO.class);
			masterDataDTO.setApplicationId(applicationId);
			masterDataDTO.setEncPayload(payload);
			
			if(masterDataDTO.getAppType().intValue()!=customer.getType().intValue())
			{
				throw new EOTException(ErrorConstants.INVALID_USER_TYPE);
			}
			

		} catch (EOTException e) {
			masterDataDTO.setDefaultLocale("en_US");
			getErrorResponse(masterDataDTO, e.getErrorCode());
//			e.printStackTrace();
			applicationId = "errorHandlerKey";
			try {
				wrappedTranEncKey = Base64.decode(servletRequest.getHeader("wrappedTranEncKey"));
				try {
				kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);
				}catch (Exception e1) {
				}
				kmsHandler.unWrapAndStoreRmkWrappedKey(wrappedTranEncKey, applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, new Date(), new Date());
			} catch (Exception e1) {
//				e.printStackTrace();
				// TODO: handle exception
			}
			masterDataDTO.setDefaultLocale(Constants.DEFAULT_LANGUAGE);
			
			masterDataDTO.setTransactionType(Constants.TXN_TYPE_MERCHANT_DEPOSIT);
			masterDataDTO.setDefaultLocale(null);
		} catch (Exception ex) {
			applicationId = "errorHandlerKey";
			try {
				wrappedTranEncKey = Base64.decode(servletRequest.getHeader("wrappedTranEncKey"));
				kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);
				kmsHandler.unWrapAndStoreRmkWrappedKey(wrappedTranEncKey, applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, new Date(), new Date());
			} catch (Exception e1) {
				e1.printStackTrace();
				// TODO: handle exception
			}
			masterDataDTO.setDefaultLocale(Constants.DEFAULT_LANGUAGE);
			
			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}		

		String jsonString = new JSONAdaptor().toJSON(masterDataDTO) ;
		

		byte[] payload = null ;
		byte[] encPayload = null;
		if (isCheckSumEnabled) {
			// transactionBaseDTO.setMasterKey("7sdfdiet43535j3");
			TransactionBaseDTO transactionBaseDTO=(TransactionBaseDTO)masterDataDTO;
			try {
				String masterKey = generateCheckSumKey();
				transactionBaseDTO.setEncPayload(null);
				transactionBaseDTO.setCheckSumString(null);
				masterDataDTO.setMasterKey(masterKey);

				TransactionBaseDTO baseDTO2 = new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO), TransactionBaseDTO.class);
				String checkSumString = CheckSumUtil.getCheckSumUtil().genrateCheckSum(masterKey, baseDTO2);
				masterDataDTO.setCheckSumString(checkSumString);
				jsonString = new JSONAdaptor().toJSON(masterDataDTO) ;
				System.out.println(new JSONAdaptor().toJSON(transactionBaseDTO));

			} catch (Exception e) {
//				e.printStackTrace();
				throw new HttpMessageNotReadableException("checkSum fail ", e);
			}
		}


		if (isSecured) {

			try {

				encPayload = kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, jsonString.getBytes(), true);

				payload = Base64.encode(encPayload);

			} catch (KMSSecurityException e) {
				getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
			}
		} else {
			payload = jsonString.getBytes() ;
		}

		try {
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();
		} catch (Exception e) {
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}

		masterDataDTO.setEncPayload(payload);
		activationService.updateMobileRequest(masterDataDTO);

		return masterDataDTO;

	}
	
	
	/**
	 * Gets the error response.
	 *
	 * @param transactionBaseDTO the transaction base dto
	 * @param errorCode the error code
	 * @return the error response
	 */
	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO,Integer errorCode) {

		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_"+errorCode, null, new Locale(transactionBaseDTO.getDefaultLocale())));

		return transactionBaseDTO;
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

}