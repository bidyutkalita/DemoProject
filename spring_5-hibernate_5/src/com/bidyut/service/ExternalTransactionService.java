package com.bidyut.service;

import com.bidyut.dto.DepositReqDTO;
import com.bidyut.dto.DepositeResponse;

public interface ExternalTransactionService {

	DepositeResponse processNileBatDeposit(DepositReqDTO reqDTO);

}
