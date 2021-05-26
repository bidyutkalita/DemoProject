package com.eot.kcb.apiEndPoing;

import com.eot.kcb.dto.AccountValidationRequest;
import com.eot.kcb.dto.AccountValidationResponse;

public interface AccountDetailsService {
	
	public AccountValidationResponse getLinkedStatus(AccountValidationRequest accountValidation);

}
