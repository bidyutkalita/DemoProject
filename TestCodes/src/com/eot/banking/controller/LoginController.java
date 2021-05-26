
package com.eot.banking.controller;

import java.io.IOException;
import java.util.Date;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.dto.LoginDTO;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.ActivationService;
import com.eot.banking.service.LoginService;
import com.eot.banking.utils.JSONAdaptor;
import com.eot.crypto.CheckSumUtil;
import com.eot.entity.Customer;
import com.security.kms.KMS;
import com.security.kms.security.KMSSecurityException;

@Controller
@RequestMapping("/rest")
public class LoginController {

	/** The logger. */
	private KMS kmsHandler;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ActivationService activationService;

	/**
	 * Sets the kms handler.
	 *
	 * @param kmsHandler
	 *            the new kms handler
	 */
	public void setKmsHandler(KMS kmsHandler) {
		this.kmsHandler = kmsHandler;
	}

	/** The is encrypted. */
	private boolean isSecured = true;
	private boolean isCheckSumEnabled = true;

	@Autowired
	private LoginService loginService;
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public @ResponseBody LoginDTO login(@RequestBody LoginDTO loginDTO, HttpServletResponse servletResponse) throws EOTException, IOException {

		try {

			loginDTO = loginService.login(loginDTO);

		} catch (EOTException e) {

//			e.printStackTrace();
			// servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			getErrorResponse(loginDTO, e.getErrorCode(), e.getFieldName());

		} catch (Exception ex) {

//			ex.printStackTrace();
			// servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(loginDTO, ErrorConstants.SERVICE_ERROR);
		}

		return loginDTO;

	}

	@RequestMapping(value = "/relogin", method = RequestMethod.POST)
	public LoginDTO relogin(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws EOTException, IOException {
		logger.info("************************ Inside reLogin *********************");
		String applicationId = servletRequest.getHeader("applicationId");
		String identificationNumber = servletRequest.getHeader("identificationNumber");

		byte[] wrappedTranEncKey = null;
		byte[] encPayload = null;
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream());
		LoginDTO loginDTO = new LoginDTO();
		try {
		Customer customer = loginService.loadCustomerByMobileNumber(identificationNumber);
		if (customer == null) {
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER_MOBILE_NUMBER);
		}
		
		applicationId = customer.getAppId();

		

		if (isSecured) {

			wrappedTranEncKey = Base64.decode(servletRequest.getHeader("wrappedTranEncKey"));
			encPayload = Base64.decode(payload);
			try {
				kmsHandler.removeKey(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			//
			try {
				kmsHandler.unWrapAndStoreRmkWrappedKey(wrappedTranEncKey, applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, new Date(), new Date());
				payload = kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, encPayload, false);
				
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		loginDTO = new JSONAdaptor().fromJSON(new String(payload), LoginDTO.class);
		if (!applicationId.equals("errorHandlerKey"))
			loginDTO.setApplicationId(applicationId);

		

			loginDTO = loginService.login(loginDTO);

		} catch (EOTException e) {

//			e.printStackTrace();
			// servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			getErrorResponse(loginDTO, e.getErrorCode(), e.getFieldName());
			if (e.getErrorCode() == ErrorConstants.MAX_INCORRECT_LOGIN_ATTEMPT) {
				loginDTO.setApplicationStatus(EOTConstants.APP_STATUS_BLOCKED);
			}

		} catch (Exception ex) {

			ex.printStackTrace();
			// servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(loginDTO, ErrorConstants.SERVICE_ERROR);
		}
		
		String jsonString = new JSONAdaptor().toJSON(loginDTO);
		logger.info("************************ response reLogin *********************");
		System.out.println(jsonString);
		if (isCheckSumEnabled) {
			TransactionBaseDTO transactionBaseDTO = (TransactionBaseDTO) loginDTO;
			try {
				String masterKey = generateCheckSumKey();
				transactionBaseDTO.setEncPayload(null);
				transactionBaseDTO.setCheckSumString(null);
				loginDTO.setMasterKey(masterKey);

				TransactionBaseDTO baseDTO2 = new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO), TransactionBaseDTO.class);
				removeBaseDTOParameterForCheckSum(baseDTO2);
				String checkSumString = CheckSumUtil.getCheckSumUtil().genrateCheckSum(masterKey, baseDTO2);
				loginDTO.setCheckSumString(checkSumString);
				jsonString = new JSONAdaptor().toJSON(loginDTO);

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
				getErrorResponse(loginDTO, ErrorConstants.SERVICE_ERROR);
			}
		} else {
			payload = jsonString.getBytes();
		}
		try {
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();
		} catch (Exception e) {
			getErrorResponse(loginDTO, ErrorConstants.SERVICE_ERROR);
		}

		loginDTO.setEncPayload(payload);
		loginDTO.setTransactionPIN(null);
		loginDTO.setActivationPIN(null);

		return loginDTO;

	}

	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO, Integer errorCode, String fieldName) {

		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_" + errorCode, new String[] { fieldName }, new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));

		return transactionBaseDTO;
	}

	@RequestMapping(value = "initiate", method = RequestMethod.POST)
	public @ResponseBody MasterDataDTO initiate(@RequestBody MasterDataDTO masterDataDTO) {
		logger.info("************************ Inside initiate *********************");
	

		String applicationId = masterDataDTO.getApplicationId();
		
		int appType= masterDataDTO.getAppType();


		try {
			Customer customer = loginService.loadCustomerByApplicationId(applicationId);

			loginService.validateLoginForInitiate(masterDataDTO, customer);
			loginService.loginMaster(masterDataDTO, customer);
			activationService.processActivationRequest(masterDataDTO);
			
		} catch (EOTException e) {
//			e.printStackTrace();
			masterDataDTO.setStatus(1);
			if(!e.getErrorCode().equals(ErrorConstants.INCORRECT_LOGIN_ATTEMPT))
				getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			masterDataDTO.setStatus(1);
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}


		logger.info("************************ respinse initiate *********************");

		masterDataDTO.setAppType(appType);
		return masterDataDTO;

	}

	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO, Integer errorCode) {

		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_" + errorCode, null, new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));

		return transactionBaseDTO;
	}

	public static String generateCheckSumKey() {
		Random random = new Random();
		String alphabet = "qwertyuioplkjhgfdsazxcvbnm";
		int size = 16;
		char[] chars = new char[size];
		for (int i = 0; i < size; i++) {
			chars[i] = alphabet.charAt(random.nextInt(alphabet.length()));
		}
		return new String(chars);
	}

	private void removeBaseDTOParameterForCheckSum(TransactionBaseDTO transactionBaseDTO) {
		// transactionBaseDTO.setMessageDescription(null);
		// transactionBaseDTO.setSuccessResponse(null);
		// transactionBaseDTO.setApplicationId(null);
		// transactionBaseDTO.setDefaultLocale(null);
		// transactionBaseDTO.setStatus(null);

	}
}
