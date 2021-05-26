package com.eot.banking.service;

import com.eot.banking.dto.DstvDTO;
import com.eot.banking.dto.KwiksyDTO;
import com.eot.banking.dto.NileBatDTO;
import com.eot.banking.exception.EOTException;


public interface OtherTransactionService {
	public KwiksyDTO getProvicers(KwiksyDTO dto) throws EOTException ;

	KwiksyDTO remittanceOutward(KwiksyDTO dto) throws EOTException;

	KwiksyDTO getexchangerate(KwiksyDTO dto) throws EOTException;

	NileBatDTO purchesNileBettingCoin(NileBatDTO dto) throws EOTException;

	DstvDTO getDstvPacakages(DstvDTO dto) throws EOTException;

	DstvDTO rechargeDstv(DstvDTO dto) throws EOTException;

	KwiksyDTO remittanceOutwardValidate(KwiksyDTO dto) throws EOTException;

}
