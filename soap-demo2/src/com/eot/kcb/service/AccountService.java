package com.eot.kcb.service;

import com.eot.kcb.dto.AccountDeLinkRequestDTO;
import com.eot.kcb.dto.AccountLinkRequestDTO;
import com.eot.kcb.dto.AccountValidationResponse;

public interface AccountService {

	public AccountValidationResponse getAccountMappingByAccountNo(String acccountNo);

	void save(AccountLinkRequestDTO accountLinkRequestDTO);

	boolean deLink(AccountDeLinkRequestDTO accountDeLinkRequestDTO);
	
	

}
