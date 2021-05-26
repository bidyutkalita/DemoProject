/* Copyright © EasOfTech 2015. All rights reserved.
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

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.IntegrationService;

/**
 * The Class OtherBankingServiceController.
 */
@Controller
@RequestMapping("/rest/integration")
public class IntegrationController {

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	
	/** The integration service. */
	@Autowired
	private IntegrationService integrationService;

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IntegrationController.class);

	/**
	 * Fetch master data.
	 *
	 * @param masterDataDTO the master data DTO
	 * @return the master data DTO
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "fetchMasterData", method = RequestMethod.POST)
	public @ResponseBody MasterDataDTO fetchMasterData( @RequestBody MasterDataDTO masterDataDTO ) throws Exception {
		logger.info("************************ Inside fetchMasterData *********************");
		try {
			integrationService.getMasterData(masterDataDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			getErrorResponse(masterDataDTO, e.getErrorCode());
		} catch (Exception ex) {
//			ex.printStackTrace();
			getErrorResponse(masterDataDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return fetchMasterData ***********************");
		return masterDataDTO;
	}
	
	/**
	 * Gets the error response.
	 *
	 * @param transactionBaseDTO the transaction base DTO
	 * @param errorCode the error code
	 * @return the error response
	 */
	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO,Integer errorCode) {

		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_"+errorCode, null, 
				new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));

		return transactionBaseDTO;
	}
}
