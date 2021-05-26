package com.eot.banking.daos;

import java.util.List;
import java.util.Map;

import com.eot.banking.daos.base.BaseDao;
import com.eot.banking.dto.SCRuleDTO;

public interface ServiceChargeDao extends BaseDao{
	
	Map<Integer, Map<Long, List<SCRuleDTO>>> getIntraBankServiceChargeRules( Integer transactionType, String sourceType ,  Integer bankGroupId, Integer profileId, Double txnAmount );
	
	Map<Integer, Map<Long, List<SCRuleDTO>>> getInterBankServiceChargeRules( Integer transactionType, String sourceType, Double txnAmount  );
	
	/*float getBankServiceChargePercent( Integer transactionType, String bankAccountNumber );
	
	Map<String, Float> getStakeholderServiceChargePercent( Integer transactionType,Integer bankId );
	
	List<ServiceChargeSplit> getServiceChargeSplit( Integer transactionType, Integer bankId );
	
	List<ServiceChargeSplit> getAmountPercentListIntra( Integer transactionType, Integer bankId );
	
	List<ServiceChargeSplit> getAmountPercentListInter( Integer transactionType );
	
	List<ServiceChargeSplit> getStampFeeFromServiceChargeSplit( Integer transactionType, Integer bankId );
	
	List<ServiceChargeSplit> getTaxFromServiceChargeSplit( Integer transactionType, Integer bankId );*/
}
