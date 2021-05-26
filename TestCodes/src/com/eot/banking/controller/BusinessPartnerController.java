package com.eot.banking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eot.banking.dto.BalanceEnquiryDTO;
import com.eot.banking.dto.BusinessPartnerLoginDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.service.BusinessPartnerService;
import com.eot.banking.service.TransactionService;

/*@Controller
@RequestMapping("/rest/businessPartner")
public class BusinessPartnerController {

	*//** The message source. *//*
	@Autowired
	private MessageSource messageSource;
	
	*//** The BusinessPartner service. *//*
	@Autowired
	private BusinessPartnerService businessPartnerService;
	
	*//** The Constant logger. *//*
	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
	
	@RequestMapping(value = "auth", method = RequestMethod.POST)
	public @ResponseBody BusinessPartnerLoginDTO getLoginAuth( @RequestBody BusinessPartnerLoginDTO businessPartnerLoginDTO ) throws Exception {
		logger.info("************************ Inside Authentication *********************");
		try {
			businessPartnerLoginDTO = businessPartnerService.processLoginEnquiryRequest(businessPartnerLoginDTO);
		} catch (EOTException e) {
			e.printStackTrace();
		//	getErrorResponse(businessPartnerLoginDTO, e.getErrorCode());
		} catch (Exception ex) {
			ex.printStackTrace();
		//	getErrorResponse(businessPartnerLoginDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return Authentication ***********************");
		return businessPartnerLoginDTO;
	}

}
*/