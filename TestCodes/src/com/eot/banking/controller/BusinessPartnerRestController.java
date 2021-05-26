package com.eot.banking.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eot.banking.dto.BaseDTO;
import com.eot.banking.dto.BusinessPartnerLoginDTO;
import com.eot.banking.dto.BusinessPartnerMasterDataDTO;
import com.eot.banking.dto.ChangePasswordDTO;
import com.eot.banking.dto.ConfirmPinDTO;
import com.eot.banking.dto.CustomerProfileDTO;
import com.eot.banking.dto.FAQsModelDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.BusinessPartnerService;
import com.eot.banking.utils.JSONAdaptor;

@Controller
@RequestMapping("/rest/businessPartner")
public class BusinessPartnerRestController {

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	
	/** The BusinessPartner service. */
	@Autowired
	private BusinessPartnerService businessPartnerService;
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerRestController.class);
	
	/*@RequestMapping(value = "auth", method = RequestMethod.POST)
	public @ResponseBody BusinessPartnerLoginDTO getLoginAuth( @RequestBody BusinessPartnerLoginDTO businessPartnerLoginDTO ) throws Exception {
		logger.info("************************ Inside Authentication *********************");
		try {
			businessPartnerLoginDTO = businessPartnerService.processLoginEnquiryRequest(businessPartnerLoginDTO);
		} catch (EOTException e) {
			e.printStackTrace();
		//	getErrorResponse(businessPartnerLoginDTO, e.getErrorCode());
		} catch (Exception ex) {
			ex.printStackTrace();
			businessPartnerLoginDTO.setStatus(1);
		//	getErrorResponse(businessPartnerLoginDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return Authentication ***********************");
		return businessPartnerLoginDTO;
	}
	*/
	/*@RequestMapping(value = "onboardFieldExecutive", method = RequestMethod.POST)
	public @ResponseBody CustomerProfileDTO customerSelfOnboarding(@RequestBody CustomerProfileDTO customerProfileDTO, HttpServletResponse servletResponse) throws EOTException, IOException {
		
		logger.info("************************ Inside onboardFieldExecutive *********************");
		CustomerProfileDTO customerProfileRespDTO= new CustomerProfileDTO();
		try {
			customerProfileRespDTO = businessPartnerService.createFieldExecutive(customerProfileDTO);
		} catch (EOTException e) {
			customerProfileRespDTO.setStatus(1);
			e.printStackTrace();
			//servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			getErrorResponse(customerProfileRespDTO, e.getErrorCode(), e.getFieldName());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			//servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			customerProfileRespDTO.setStatus(1);
			getErrorResponse(customerProfileRespDTO, ErrorConstants.SERVICE_ERROR);
		}
		finally {
			customerProfileRespDTO.setAddressProof(null);
			customerProfileRespDTO.setIdProof(null);
			customerProfileRespDTO.setSignature(null);
			customerProfileRespDTO.setCustomerPhoto(null);
		}
		logger.info("************************* Return onboardFieldExecutive ***********************");
		return customerProfileRespDTO;		
		
	}*/
	//encryption is not enabled for this service. bellow method with out encryption
//	@RequestMapping(value = "auth", method = RequestMethod.POST)
	public @ResponseBody BusinessPartnerLoginDTO getLoginAuth(HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws Exception {
		logger.info("************************ Inside Authentication *********************");
		BusinessPartnerLoginDTO businessPartnerLoginDTO  = (BusinessPartnerLoginDTO) genericPayload(servletRequest, servletResponse, BusinessPartnerLoginDTO.class);
		try {
			businessPartnerLoginDTO = businessPartnerService.processLoginEnquiryRequest(businessPartnerLoginDTO);
		}/* catch (EOTException e) {
//			e.printStackTrace();
		//	getErrorResponse(businessPartnerLoginDTO, e.getErrorCode());
		}*/ catch (Exception ex) {
//			ex.printStackTrace();
			businessPartnerLoginDTO.setStatus(1);
		//	getErrorResponse(businessPartnerLoginDTO, ErrorConstants.SERVICE_ERROR);
		}		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		businessPartnerLoginDTO = (BusinessPartnerLoginDTO) genricJson(servletRequest, servletResponse, payload, businessPartnerLoginDTO);
		logger.info("************************* Return Authentication ***********************");
		return businessPartnerLoginDTO;
	}
	
	@RequestMapping(value = "auth", method = RequestMethod.POST)
	public BusinessPartnerLoginDTO getLoginAuthUnencrypted(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		logger.info("************************ Inside onbording Login *********************");
		byte[] payload = null;
		BusinessPartnerLoginDTO businessPartnerLoginDTO = null;
		try {
			
			payload=IOUtils.toByteArray(servletRequest.getInputStream()) ;
			businessPartnerLoginDTO = new JSONAdaptor().fromJSON(new String(payload), BusinessPartnerLoginDTO.class);
			businessPartnerLoginDTO = businessPartnerService.processLoginEnquiryRequest(businessPartnerLoginDTO);
		}/* catch (EOTException e) {
			e.printStackTrace();
		//	getErrorResponse(businessPartnerLoginDTO, e.getErrorCode());
		}*/ catch (Exception ex) {
//			ex.printStackTrace();
			businessPartnerLoginDTO.setStatus(1);
		//	getErrorResponse(businessPartnerLoginDTO, ErrorConstants.SERVICE_ERROR);
		}		
		
		String jsonString = new JSONAdaptor().toJSON(businessPartnerLoginDTO) ;
		System.out.println(jsonString);
		payload = jsonString.getBytes() ;
		try {
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();
		} catch (Exception e) {
			getErrorResponse(businessPartnerLoginDTO, ErrorConstants.SERVICE_ERROR);
		}

		businessPartnerLoginDTO.setEncPayload(payload);
		logger.info("************************* response Inside onbording Login ***********************");
		return businessPartnerLoginDTO;

	}
	
	
	@RequestMapping(value = "onboardFieldExecutive", method = RequestMethod.POST)
	public  CustomerProfileDTO customerSelfOnboarding( HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		logger.info("************************ Inside onboardFieldExecutive *********************");
//		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		String applicationId=null;
		CustomerProfileDTO customerProfileDTO = (CustomerProfileDTO) genericPayload(servletRequest, servletResponse, CustomerProfileDTO.class);
//		CustomerProfileDTO customerProfileDTO = new JSONAdaptor().fromJSON(new String(payload), CustomerProfileDTO.class);
		try {
			applicationId = customerProfileDTO.getApplicationId();
			customerProfileDTO = businessPartnerService.createFieldExecutive(customerProfileDTO);
		} catch (EOTException e) {
			//CustomerProfileDTO error=new CustomerProfileDTO();
			customerProfileDTO.setStatus(1);
//			e.printStackTrace();
			getErrorResponse(customerProfileDTO, e.getErrorCode(),e.getFieldName());
			//return error;
		} catch (Exception ex) {
			//CustomerProfileDTO error=new CustomerProfileDTO();
			customerProfileDTO.setStatus(1);
//			ex.printStackTrace();
			getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
			//return error;
		}finally {
			customerProfileDTO.setAddressProof(null);
			customerProfileDTO.setIdProof(null);
			customerProfileDTO.setSignature(null);
			customerProfileDTO.setCustomerPhoto(null);
		}
		//byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		String jsonString = new JSONAdaptor().toJSON(customerProfileDTO) ;
		byte[] payload = jsonString.getBytes() ;
		customerProfileDTO = (CustomerProfileDTO) genricJson(servletRequest, servletResponse, payload, customerProfileDTO);
		
		
//		String jsonString = new JSONAdaptor().toJSON(customerProfileDTO) ;
//		byte[] payload = jsonString.getBytes() ;
		//customerProfileDTO.setApplicationId(applicationId);
		/*byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		String jsonString = new JSONAdaptor().toJSON(customerProfileDTO) ;
		System.out.println(jsonString);
		byte[] encPayload = null;
		payload = jsonString.getBytes() ;*/
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
		/*try {
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();
		} catch (Exception e) {
			getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
		}
		customerProfileDTO.setEncPayload(payload);*/
		logger.info("************************* Return onboardFieldExecutive ***********************");
		//activationService.updateMobileRequest(customerProfileDTO);
	
		return customerProfileDTO;		
	}
	
	public BaseDTO genricJson(HttpServletRequest servletRequest,HttpServletResponse servletResponse,byte[] payload,BaseDTO customerProfileDTO){
		logger.info("************************ Inside genricJson *********************");
		String jsonString = new JSONAdaptor().toJSON(customerProfileDTO) ;
		System.out.println(jsonString);
		byte[] encPayload = null;
		payload = jsonString.getBytes() ;
		try {
			servletResponse.getOutputStream().write(payload);
			servletResponse.getOutputStream().flush();
			servletResponse.getOutputStream().close();
		} catch (Exception e) {
			//getErrorResponse(customerProfileDTO, ErrorConstants.SERVICE_ERROR);
		}
		 customerProfileDTO.setEncPayload(payload);
		 logger.info("************************ Inside genricJson *********************");
		 return customerProfileDTO;
	}
		
	public  TransactionBaseDTO genericPayload( HttpServletRequest servletRequest, HttpServletResponse servletResponse, Class class1 ) throws EOTException, IOException {
		logger.info("************************ Inside genericPayload *********************");
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		String applicationId=null;
		TransactionBaseDTO TransactionBaseDTO = (com.eot.banking.dto.TransactionBaseDTO) new JSONAdaptor().fromJSON(new String(payload), class1);
		return TransactionBaseDTO;
		
	}
	
	/*@RequestMapping(value = "masterData", method = RequestMethod.GET)
	public @ResponseBody BusinessPartnerMasterDataDTO fetchBusinessPartnerMasterData() throws EOTException, IOException {
		logger.info("************************ Inside masterData *********************");
		return businessPartnerService.fetchBusinessPartnerMasterData();		
		
	}*/
	
	@RequestMapping(value = "masterData", method = RequestMethod.GET)
	public @ResponseBody BusinessPartnerMasterDataDTO fetchBusinessPartnerMasterData(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws EOTException, IOException {
		logger.info("************************ Inside masterData *********************");
		BusinessPartnerMasterDataDTO businessPartnerMasterDataDTO = (BusinessPartnerMasterDataDTO) genericPayload(servletRequest, servletResponse, BusinessPartnerMasterDataDTO.class);
		try{
		 businessPartnerMasterDataDTO =  businessPartnerService.fetchBusinessPartnerMasterData();		
		}catch (Exception e) {
//			e.printStackTrace();
		}
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		businessPartnerMasterDataDTO = (BusinessPartnerMasterDataDTO) genricJson(servletRequest, servletResponse, payload, businessPartnerMasterDataDTO);
		return businessPartnerMasterDataDTO;
	}
	
	@RequestMapping(value = "changeLoginPass", method = RequestMethod.POST)
	public @ResponseBody ChangePasswordDTO changeLoginPassword(HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws EOTException, IOException {
		
		logger.info("************************ Inside changeLoginPass *********************");
		ChangePasswordDTO changePasswordDTO  = (ChangePasswordDTO) genericPayload(servletRequest, servletResponse, ChangePasswordDTO.class);
		try {
			changePasswordDTO = businessPartnerService.changeLoginPassword(changePasswordDTO);
		} catch (EOTException e) {
			
//			e.printStackTrace();
			//servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			getErrorResponse(changePasswordDTO, e.getErrorCode(), e.getFieldName());
			
		} catch (Exception ex) {
//			ex.printStackTrace();
			//servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(changePasswordDTO, ErrorConstants.SERVICE_ERROR);
		}
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		changePasswordDTO = (ChangePasswordDTO) genricJson(servletRequest, servletResponse, payload, changePasswordDTO);
		logger.info("************************ returning changeLoginPass *********************");
		return changePasswordDTO;		
		
	}
	
	
	/*@RequestMapping(value = "changeLoginPass", method = RequestMethod.POST)
	public @ResponseBody ChangePasswordDTO changeLoginPassword(@RequestBody ChangePasswordDTO changePasswordDTO, HttpServletResponse servletResponse) throws EOTException, IOException {
		logger.info("************************ Inside changeLoginPass *********************");
		try {
			changePasswordDTO = businessPartnerService.changeLoginPassword(changePasswordDTO);
		} catch (EOTException e) {
			
			e.printStackTrace();
			//servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			getErrorResponse(changePasswordDTO, e.getErrorCode(), e.getFieldName());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			//servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			getErrorResponse(changePasswordDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************ returning changeLoginPass *********************");
		return changePasswordDTO;		
		
	}
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
	
	
	@RequestMapping(value = "faqs", method = RequestMethod.GET)
	public @ResponseBody FAQsModelDTO fetchFaqs(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws EOTException, IOException {
		logger.info("************************ returning faqs *********************");
		FAQsModelDTO faQsModelDTO  = (FAQsModelDTO) genericPayload(servletRequest, servletResponse, FAQsModelDTO.class);
		try{
		faQsModelDTO=  businessPartnerService.fetchFaqs();		
		}catch (Exception e) {
//			e.printStackTrace();
		}
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		faQsModelDTO  = (FAQsModelDTO)genricJson(servletRequest, servletResponse, payload, faQsModelDTO);
		return faQsModelDTO;
	}
	
	@RequestMapping(value = "faqsCustomer", method = RequestMethod.GET)
	public @ResponseBody FAQsModelDTO fetchFaqsCustomer(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws EOTException, IOException {
		logger.info("************************ returning faqs *********************");
		FAQsModelDTO faQsModelDTO  = (FAQsModelDTO) genericPayload(servletRequest, servletResponse, FAQsModelDTO.class);
		try{
		faQsModelDTO=  businessPartnerService.fetchFaqsCustomer();		
		}catch (Exception e) {
//			e.printStackTrace();
		}
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		faQsModelDTO  = (FAQsModelDTO)genricJson(servletRequest, servletResponse, payload, faQsModelDTO);
		return faQsModelDTO;
	}
	
	/*@RequestMapping(value = "faqs", method = RequestMethod.GET)
	public @ResponseBody FAQsModelDTO fetchFaqs() throws EOTException {
		logger.info("************************ returning faqs *********************");
		return businessPartnerService.fetchFaqs();		
		
	}*/
	/**
	 * vineeth
	 * helpdesk api
	 * @return
	 * @throws Exception
	 */
/*	
              * -------> Not in use this API <-----------
 * 
 * @RequestMapping(value = "helpDesk", method = RequestMethod.GET)
	public @ResponseBody HelpDeskModelDTO getHelpDeskDetails() throws Exception {
		logger.info("************************ Inside helpDesk *********************");
		HelpDeskModelDTO helpDeskModelDTO = null;
		try {
			helpDeskModelDTO = businessPartnerService.getAllHelpDeskList();	
		}catch (Exception ex) {
			ex.printStackTrace();
			//servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			//getErrorResponse(changePasswordDTO, ErrorConstants.SERVICE_ERROR);
		}
		logger.info("************************ returning helpDesk *********************");
		return helpDeskModelDTO;
	}*/
	
	/*@RequestMapping(value = "sendOtp", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO forgetPin( @RequestBody TransactionBaseDTO transactionBaseDTO ) throws EOTException,Exception  {
		logger.info("************************ Inside sendOtp *********************");
		try {
			transactionBaseDTO = businessPartnerService.processForgetPin(transactionBaseDTO);
		} catch (EOTException e) {
			e.printStackTrace();
			transactionBaseDTO.setMessageDescription(getErrorResponse(transactionBaseDTO, e.getErrorCode()).getMessageDescription());
		} catch (Exception ex) {
			ex.printStackTrace();
			transactionBaseDTO.setStatus(1);
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return sendOtp ***********************");
		return transactionBaseDTO;
	}*/
	
	@RequestMapping(value = "sendOtp", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO forgetPin( HttpServletRequest servletRequest, HttpServletResponse servletResponse  ) throws EOTException,Exception  {
		logger.info("************************ Inside sendOtp *********************");
		TransactionBaseDTO transactionBaseDTO  = (TransactionBaseDTO) genericPayload(servletRequest, servletResponse, TransactionBaseDTO.class);
		try {
			transactionBaseDTO = businessPartnerService.processForgetPin(transactionBaseDTO);
		} catch (EOTException e) {
//			e.printStackTrace();
			transactionBaseDTO.setMessageDescription(getErrorResponse(transactionBaseDTO, e.getErrorCode()).getMessageDescription());
		} catch (Exception ex) {
//			ex.printStackTrace();
			transactionBaseDTO.setStatus(1);
			getErrorResponse(transactionBaseDTO, ErrorConstants.SERVICE_ERROR);
		}		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		transactionBaseDTO = (TransactionBaseDTO) genricJson(servletRequest, servletResponse, payload, transactionBaseDTO);
		logger.info("************************* Return sendOtp ***********************");
		return transactionBaseDTO;
	}
	
	
	@RequestMapping(value = "forgetPinAgentOnBoarding", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO confirmPin( HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws EOTException,Exception  {
		logger.info("************************ Inside forgetPinAgentOnBoarding *********************");
		TransactionBaseDTO confirmPinDTO  = (TransactionBaseDTO) genericPayload(servletRequest, servletResponse, ConfirmPinDTO.class);
		//TransactionBaseDTO confirmPinDTO=new ConfirmPinDTO();
		try {
			confirmPinDTO = businessPartnerService.processConfirmPin((ConfirmPinDTO) confirmPinDTO);
		}catch (EOTException e) {
//			e.printStackTrace();
			confirmPinDTO.setMessageDescription(getErrorResponse(confirmPinDTO, e.getErrorCode()).getMessageDescription());
		} catch (Exception ex) {
//			ex.printStackTrace();			
			confirmPinDTO.setStatus(1);
			getErrorResponse(confirmPinDTO, ErrorConstants.SERVICE_ERROR);
		}		
		byte[] payload = IOUtils.toByteArray(servletRequest.getInputStream()) ;
		confirmPinDTO = (TransactionBaseDTO) genricJson(servletRequest, servletResponse, payload, confirmPinDTO);
		logger.info("************************* Return forgetPinAgentOnBoarding ***********************");
		return confirmPinDTO;
	}
	
	/*@RequestMapping(value = "forgetPinAgentOnBoarding", method = RequestMethod.POST)
	public @ResponseBody TransactionBaseDTO confirmPin( @RequestBody ConfirmPinDTO confirmPinDTO1 ) throws EOTException,Exception  {
		logger.info("************************ Inside forgetPinAgentOnBoarding *********************");
		TransactionBaseDTO confirmPinDTO=new ConfirmPinDTO();
		try {
			confirmPinDTO = businessPartnerService.processConfirmPin(confirmPinDTO1);
		}catch (EOTException e) {
			e.printStackTrace();
			confirmPinDTO.setMessageDescription(getErrorResponse(confirmPinDTO, e.getErrorCode()).getMessageDescription());
		} catch (Exception ex) {
			ex.printStackTrace();			
			confirmPinDTO.setStatus(1);
			getErrorResponse(confirmPinDTO, ErrorConstants.SERVICE_ERROR);
		}		
		logger.info("************************* Return forgetPinAgentOnBoarding ***********************");
		return confirmPinDTO;
	}*/
}
