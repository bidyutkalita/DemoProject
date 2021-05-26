package com.bidyut.apiEndPoing;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.bidyut.common.CoreUrls;
import com.bidyut.dao.AccountDaoImpl;
import com.bidyut.dao.CustomerDaoImpl;
import com.bidyut.dto.AccountDeLinkRequestDTO;
import com.bidyut.dto.AccountDeLinkResponseDTO;
import com.bidyut.dto.AccountLinkRequestDTO;
import com.bidyut.dto.AccountLinkedResponseDTO;
import com.bidyut.dto.AccountValidationRequest;
import com.bidyut.dto.AccountValidationResponse;
import com.bidyut.dto.BaseDTO;
import com.bidyut.model.ApiCreadentials;
import com.bidyut.model.BankAccountMapping;
import com.bidyut.service.AccountService;
import com.bidyut.service.AccountServiceImpl;
import com.bidyut.service.RestClient;

@WebService(targetNamespace = "http://mgurush.test.com")
// @WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class AccountDetailsServiceImpl implements AccountDetailsService {

	AccountService accountService = new AccountServiceImpl();

	// @WebMethod(operationName = "AccountValidation")
	@WebMethod(operationName = "")
	public AccountValidationResponse getLinkedStatus(@WebParam(name = "AccountValidation") AccountValidationRequest req) {

		System.out.println("Account validation request ===>");
		System.out.println(req.toString());
		AccountValidationResponse accountValidationResponse = null;
		try {
			accountValidationResponse = (AccountValidationResponse) RestClient.processRequest(CoreUrls.KCB_ACCOUNT_VALIDATION, req, new AccountValidationResponse(), BaseDTO.class,AccountValidationResponse.class);
		} catch (Exception e) {
			accountValidationResponse = new AccountValidationResponse();
			accountValidationResponse.setStatusDescription("Unabale to process your request");
			accountValidationResponse.setStatusCode("01");
			e.printStackTrace();
		}
		/* if (validateCredientia(req.getTxnUserId(), req.getTxnPassword())) {
		 * 
		 * 
		 * 
		 * accountValidationResponse = accountService.getAccountMappingByAccountNo(req.getTxnAccountNumbar());
		 * if(accountValidationResponse==null)
		 * {
		 * accountValidationResponse = new AccountValidationResponse();
		 * accountValidationResponse.setStatus(1);
		 * accountValidationResponse.setStatusCode("01");
		 * accountValidationResponse.setStatusDescription("No Mapping Found");
		 * }else {
		 * accountValidationResponse.setStatusCode("00");
		 * }
		 * }
		 * else {
		 * accountValidationResponse = new AccountValidationResponse();
		 * accountValidationResponse.setStatus(1);
		 * accountValidationResponse.setStatusCode("01");
		 * accountValidationResponse.setStatusDescription("Invalid Crediential");
		 * } */
		System.out.println("Account validation response ===>");
		System.out.println(accountValidationResponse.toString());
		accountValidationResponse.setTransactionTimeStamp(dateFormat(new Date()));
		return accountValidationResponse;
	}

	@WebMethod(operationName = "BranchBankAccountLinking")
	public AccountLinkedResponseDTO linkedAccount(@WebParam(name = "BranchBankAccountLinking") AccountLinkRequestDTO req) {
		System.out.println("BranchBankAccountLinking request ===>");
		System.out.println(req.toString());
		AccountLinkedResponseDTO resp = null;
		try {
			resp = (AccountLinkedResponseDTO) RestClient.processRequest(CoreUrls.KCB_ACCOUNT_LINKING, req, new AccountValidationResponse(), BaseDTO.class,AccountLinkedResponseDTO.class);
		} catch (Exception e) {
			resp = new AccountLinkedResponseDTO();
			resp.setStatusDescription("Unabale to process your request");
			resp.setStatusCode("01");
			e.printStackTrace();
		}

		/* try {
		 * if(accountService.save(req)) {
		 * resp.setStatusCode("00");
		 * resp.setIMSI(req.getiMSI());
		 * resp.setTxnId(req.getTxnId());
		 * resp.setReference(req.getReferenceID());
		 * resp.setTimestamp(dateFormat(new Date()));
		 * resp.setStatusDescription("account link request successful");
		 * }
		 * else {
		 * resp.setStatusCode("01");
		 * resp.setIMSI(req.getiMSI());
		 * resp.setTxnId(req.getTxnId());
		 * resp.setReference(req.getReferenceID());
		 * resp.setTimestamp(dateFormat(new Date()));
		 * resp.setStatusDescription("account already linked");
		 * }
		 * 
		 * }
		 * catch (Exception e) {
		 * e.printStackTrace();
		 * resp.setStatusCode("01");
		 * resp.setIMSI(req.getiMSI());
		 * resp.setTxnId(req.getTxnId());
		 * resp.setReference(req.getReferenceID());
		 * resp.setTimestamp(dateFormat(new Date()));
		 * resp.setStatusDescription("account link request fail");
		 * } */
		System.out.println("BranchBankAccountLinking response ===>");
		System.out.println(resp.toString());
		return resp;
	}

	@WebMethod(operationName = "BranchBankAccountDeLinking")
	public AccountDeLinkResponseDTO linkedDeAccount(@WebParam(name = "BranchBankAccountDeLinking") AccountDeLinkRequestDTO req) {

		System.out.println("BranchBankAccountDeLinking request ===>");
		System.out.println(req.toString());
		AccountDeLinkResponseDTO resp = null;

		try {
			resp = (AccountDeLinkResponseDTO) RestClient.processRequest(CoreUrls.KCB_ACCOUNT_DELINKING, req, new AccountValidationResponse(), BaseDTO.class,AccountDeLinkResponseDTO.class);
		} catch (Exception e) {
			resp = new AccountDeLinkResponseDTO();
			resp.setStatusDescription("Unabale to process your request");
			resp.setStatusCode("01");
			e.printStackTrace();
		}
		/* try {
		 * if (accountService.deLink(req)) {
		 * resp.setStatusCode("00");
		 * resp.setIMSI(req.getiMSI());
		 * resp.setTxnId(req.getTxnId());
		 * resp.setReference(req.getReferenceID());
		 * resp.setTimestamp(dateFormat(new Date()));
		 * resp.setStatusDescription("account delink request successful");
		 * }
		 * else {
		 * resp.setStatusCode("01");
		 * resp.setIMSI(req.getiMSI());
		 * resp.setTxnId(req.getTxnId());
		 * resp.setReference(req.getReferenceID());
		 * resp.setTimestamp(dateFormat(new Date()));
		 * resp.setStatusDescription("No record found");
		 * }
		 * 
		 * }
		 * catch (Exception e) {
		 * e.printStackTrace();
		 * resp.setStatusCode("01");
		 * resp.setIMSI(req.getiMSI());
		 * resp.setTxnId(req.getTxnId());
		 * resp.setReference(req.getReferenceID());
		 * resp.setTimestamp(new Date().toString());
		 * resp.setStatusDescription("account delink request fail");
		 * } */
		System.out.println("BranchBankAccountDeLinking response ===>");
		System.out.println(resp.toString());
		return resp;
	}

	public boolean validateCredientia(String username, String password) {
		ApiCreadentials cred = new CustomerDaoImpl().validateRequest(username, password);
		if (cred == null)
			return false;
		else
			return true;
	}

	public String dateFormat(Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		return sf.format(date);
	}
	public static void main(String[] args) {
		System.out.println(CoreUrls.KCB_ACCOUNT_LINKING);
		
	}
}
