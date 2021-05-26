package com.eot.banking.service;

import java.io.IOException;

import com.eot.banking.dto.BusinessPartnerLoginDTO;
import com.eot.banking.dto.BusinessPartnerMasterDataDTO;
import com.eot.banking.dto.ChangePasswordDTO;
import com.eot.banking.dto.ConfirmPinDTO;
import com.eot.banking.dto.CustomerProfileDTO;
import com.eot.banking.dto.FAQsModelDTO;
import com.eot.banking.dto.HelpDeskModelDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;

public interface BusinessPartnerService {

	BusinessPartnerLoginDTO processLoginEnquiryRequest(BusinessPartnerLoginDTO businessPartnerLoginDTO) throws EOTException;
	
	CustomerProfileDTO createFieldExecutive(CustomerProfileDTO customerProfileDTO) throws Exception;
	
	BusinessPartnerMasterDataDTO fetchBusinessPartnerMasterData() throws EOTException;
	
	ChangePasswordDTO changeLoginPassword(ChangePasswordDTO changePasswordDTO) throws EOTException;
	
	FAQsModelDTO fetchFaqs() throws EOTException;

	HelpDeskModelDTO getAllHelpDeskList();

	TransactionBaseDTO processForgetPin(TransactionBaseDTO transactionBaseDTO) throws EOTException, Exception;

	ConfirmPinDTO processConfirmPin(ConfirmPinDTO confirmPinDTO) throws EOTException, Exception;

	FAQsModelDTO fetchFaqsCustomer();

}
