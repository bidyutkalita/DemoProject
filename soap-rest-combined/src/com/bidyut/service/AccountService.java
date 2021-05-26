package com.bidyut.service;

import com.bidyut.dto.AccountDeLinkRequestDTO;
import com.bidyut.dto.AccountLinkRequestDTO;
import com.bidyut.dto.AccountValidationResponse;

public interface AccountService {

	public AccountValidationResponse getAccountMappingByAccountNo(String acccountNo);

	boolean save(AccountLinkRequestDTO accountLinkRequestDTO);

	boolean deLink(AccountDeLinkRequestDTO accountDeLinkRequestDTO);
	
	

}
