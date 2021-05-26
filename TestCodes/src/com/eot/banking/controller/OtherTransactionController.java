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

import com.eot.banking.dto.DstvDTO;
import com.eot.banking.dto.KwiksyDTO;
import com.eot.banking.dto.NileBatDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.OtherTransactionService;

@Controller
@RequestMapping("/rest")
public class OtherTransactionController {
	
	@Autowired 
	OtherTransactionService otherTransactionServiceImpl;
	
	@Autowired
	private MessageSource messageSource;
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(OtherTransactionController.class);
	
	@RequestMapping(value = "/kqiksy/getProviders", method = RequestMethod.POST)
	public @ResponseBody KwiksyDTO getProviders(@RequestBody KwiksyDTO dto) throws Exception {
		logger.info("************************ Inside getProviders *********************");
		try {
			otherTransactionServiceImpl.getProvicers(dto);
			dto.setStatus(0);
		} catch (EOTException e) {
			dto.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(dto, e.getErrorCode());
		} catch (Exception ex) {
			dto.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(dto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return getProviders ***********************");
		return dto;
	}
	
	@RequestMapping(value = "/kqiksy/remittanceValidation", method = RequestMethod.POST)
	public @ResponseBody KwiksyDTO remittanceValidation(@RequestBody KwiksyDTO dto) throws Exception {
		logger.info("************************ Inside Validation *********************");
		try {
			otherTransactionServiceImpl.remittanceOutwardValidate(dto);
			dto.setStatus(0);
		} catch (EOTException e) {
			dto.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(dto, e.getErrorCode());
		} catch (Exception ex) {
			dto.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(dto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return Validation ***********************");
		return dto;
	}

	
	@RequestMapping(value = "/kqiksy/remittanceOutward", method = RequestMethod.POST)
	public @ResponseBody KwiksyDTO remittanceOutward(@RequestBody KwiksyDTO dto) throws Exception {
		logger.info("************************ Inside remittanceOutward *********************");
		try {
			otherTransactionServiceImpl.remittanceOutward(dto);
			dto.setStatus(0);
		} catch (EOTException e) {
			dto.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(dto, e.getErrorCode());
		} catch (Exception ex) {
			dto.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(dto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return remittanceOutward ***********************");
		return dto;
	}
	
	@RequestMapping(value = "/kqiksy/getexchangerate", method = RequestMethod.POST)
	public @ResponseBody KwiksyDTO getexchangerate(@RequestBody KwiksyDTO dto) throws Exception {
		logger.info("************************ Inside getexchangerate *********************");
		try {
			otherTransactionServiceImpl.getexchangerate(dto);
			dto.setStatus(0);
		} catch (EOTException e) {
			dto.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(dto, e.getErrorCode());
		} catch (Exception ex) {
			dto.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(dto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return getexchangerate ***********************");
		return dto;
	}
	
	@RequestMapping(value = "/nileBat/unitPurchase", method = RequestMethod.POST)
	public @ResponseBody NileBatDTO unitPurchase(@RequestBody NileBatDTO dto) throws Exception {
		logger.info("************************ Inside unitPurchase *********************");
		try {
			otherTransactionServiceImpl.purchesNileBettingCoin(dto);
			dto.setStatus(0);
		} catch (EOTException e) {
			dto.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(dto, e.getErrorCode());
		} catch (Exception ex) {
			dto.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(dto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return unitPurchase ***********************");
		return dto;
	}
	
	
	@RequestMapping(value = "/dstv/getDstvPackages", method = RequestMethod.POST)
	public @ResponseBody DstvDTO getDstvPackages(@RequestBody DstvDTO dto) throws Exception {
		logger.info("************************ Inside getDstvPackages *********************");
		try {
			dto=otherTransactionServiceImpl.getDstvPacakages(dto);
			dto.setStatus(0);
		} catch (EOTException e) {
			dto.setStatus(1);
			e.printStackTrace();
			getErrorResponse(dto, e.getErrorCode());
		} catch (Exception ex) {
			dto.setStatus(1);
			ex.printStackTrace();
			getErrorResponse(dto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return getDstvPackages ***********************");
		return dto;
	}
	@RequestMapping(value = "/dstv/rechargeDstv", method = RequestMethod.POST)
	public @ResponseBody DstvDTO rechargeDstv(@RequestBody DstvDTO dto) throws Exception {
		logger.info("************************ Inside rechargeDstv *********************");
		try {
			otherTransactionServiceImpl.rechargeDstv(dto);
			dto.setStatus(0);
		} catch (EOTException e) {
			dto.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(dto, e.getErrorCode());
		} catch (Exception ex) {
			dto.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(dto, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************* Return rechargeDstv ***********************");
		return dto;
	}
	private TransactionBaseDTO getErrorResponse(TransactionBaseDTO transactionBaseDTO, Integer errorCode) {

		transactionBaseDTO.setStatus(Constants.MOB_RESP_STATUS_FAILURE);
		transactionBaseDTO.setMessageDescription(messageSource.getMessage("ERROR_" + errorCode, null, new Locale(transactionBaseDTO.getDefaultLocale() != null ? transactionBaseDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));
		return transactionBaseDTO;
	}


}
