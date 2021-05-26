package com.eot.banking.service;

import com.eot.dto.NileBatDepositReqDTO;
import com.eot.dto.NileBatDepositeResponse;

public interface ExternalTransactionService {

	NileBatDepositeResponse processNileBatDeposit(NileBatDepositReqDTO reqDTO);

}
