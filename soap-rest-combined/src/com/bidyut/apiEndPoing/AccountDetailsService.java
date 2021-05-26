package com.bidyut.apiEndPoing;

import com.bidyut.dto.AccountValidationRequest;
import com.bidyut.dto.AccountValidationResponse;

public interface AccountDetailsService {
	
	public AccountValidationResponse getLinkedStatus(AccountValidationRequest accountValidation);

}
