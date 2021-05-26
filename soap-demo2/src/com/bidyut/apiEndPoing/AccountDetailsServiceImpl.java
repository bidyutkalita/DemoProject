package com.bidyut.apiEndPoing;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.bidyut.dao.AccountDaoImpl;
import com.bidyut.dao.CustomerDaoImpl;
import com.bidyut.dto.AccountDeLinkRequestDTO;
import com.bidyut.dto.AccountDeLinkResponseDTO;
import com.bidyut.dto.AccountLinkRequestDTO;
import com.bidyut.dto.AccountLinkedResponseDTO;
import com.bidyut.dto.AccountValidationRequest;
import com.bidyut.dto.AccountValidationResponse;
import com.bidyut.model.ApiCreadentials;
import com.bidyut.model.BankAccountMapping;
import com.bidyut.service.AccountService;
import com.bidyut.service.AccountServiceImpl;

@WebService(targetNamespace = "http://bidyut.test.com")
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
	if (validateCredientia(req.getTxnUserId(), req.getTxnPassword())) {
		accountValidationResponse = accountService.getAccountMappingByAccountNo(req.getTxnAccountNumbar());
		accountValidationResponse.setStatusCode("00");
	}
	else {
		accountValidationResponse = new AccountValidationResponse();
		accountValidationResponse.setStatus(1);
		accountValidationResponse.setStatusCode("01");
		accountValidationResponse.setStatusDescription("Invalid Crediential");
	}

	System.out.println("Account validation response ===>");
	System.out.println(accountValidationResponse.toString());
	accountValidationResponse.setTransactionTimeStamp(dateFormat(new Date()));
	return accountValidationResponse;
}

@WebMethod(operationName = "BranchBankAccountLinking")
public AccountLinkedResponseDTO linkedAccount(@WebParam(name = "BranchBankAccountLinking") AccountLinkRequestDTO req) {
	System.out.println("BranchBankAccountLinking request ===>");
	System.out.println(req.toString());
	AccountLinkedResponseDTO resp = new AccountLinkedResponseDTO();
	try {
		accountService.save(req);
		resp.setStatusCode("00");
		resp.setIMSI(req.getiMSI());
		resp.setTxnId(req.getTxnId());
		resp.setReference(req.getReferenceID());
		resp.setTimestamp(dateFormat(new Date()));
		resp.setStatusDescription("account link request successful");

	}
	catch (Exception e) {
		e.printStackTrace();
		resp.setStatusCode("01");
		resp.setIMSI(req.getiMSI());
		resp.setTxnId(req.getTxnId());
		resp.setReference(req.getReferenceID());
		resp.setTimestamp(dateFormat(new Date()));
		resp.setStatusDescription("account link request fail");
	}
	System.out.println("BranchBankAccountLinking response ===>");
	System.out.println(resp.toString());
	return resp;
}

@WebMethod(operationName = "BranchBankAccountDeLinking")
public AccountDeLinkResponseDTO linkedDeAccount(@WebParam(name = "BranchBankAccountDeLinking") AccountDeLinkRequestDTO req) {
	System.out.println("BranchBankAccountDeLinking request ===>");
	System.out.println(req.toString());
	AccountDeLinkResponseDTO resp = new AccountDeLinkResponseDTO();
	try {
		if (accountService.deLink(req)) {
			resp.setStatusCode("00");
			resp.setIMSI(req.getiMSI());
			resp.setTxnId(req.getTxnId());
			resp.setReference(req.getReferenceID());
			resp.setTimestamp(dateFormat(new Date()));
			resp.setStatusDescription("account delink request successful");
		}
		else {
			resp.setStatusCode("01");
			resp.setIMSI(req.getiMSI());
			resp.setTxnId(req.getTxnId());
			resp.setReference(req.getReferenceID());
			resp.setTimestamp(dateFormat(new Date()));
			resp.setStatusDescription("No record found");
		}

	}
	catch (Exception e) {
		e.printStackTrace();
		resp.setStatusCode("01");
		resp.setIMSI(req.getiMSI());
		resp.setTxnId(req.getTxnId());
		resp.setReference(req.getReferenceID());
		resp.setTimestamp(new Date().toString());
		resp.setStatusDescription("account delink request fail");
	}
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

public String dateFormat(Date date)
{
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
	return sf.format(date);
}
}
