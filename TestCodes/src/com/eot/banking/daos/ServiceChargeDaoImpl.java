package com.eot.banking.daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.daos.base.BaseDaoImpl;
import com.eot.banking.dto.SCRuleDTO;
import com.eot.banking.utils.Util;

@Repository("serviceChargeDao")
public class ServiceChargeDaoImpl extends BaseDaoImpl implements ServiceChargeDao {

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, Map<Long, List<SCRuleDTO>>> getInterBankServiceChargeRules(Integer transactionType, String sourceType, Double txnAmount) {
		
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Calendar calendar = Calendar.getInstance() ;

		String date = Util.formatDateToString(calendar.getTime(), "dd-MM-yyyy");

	/*	Query query = session.createSQLQuery(
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID  " +
				"     where sct.TransactionType="+transactionType+" and sct.sourceType="+ sourceType +" and RuleLevel="+EOTConstants.RULE_LEVEL_INTER_BANK +
				" and " +
				" ( STR_TO_DATE('"+ date +"','%d-%m-%Y') between sc.ApplicableFrom and sc.ApplicableTo )  and" +
				" sca.Day="+calendar.get(Calendar.DAY_OF_WEEK)+" and " +
				" ( "+calendar.get(Calendar.HOUR_OF_DAY)+" between sca.FromHH and sca.ToHH ) "		
		).addScalar("RuleLevel",Hibernate.INTEGER).addScalar("ServiceChargeRuleID",Hibernate.LONG)
		 .addScalar("ServiceChargePct",Hibernate.FLOAT).addScalar("ServiceChargeFxd",Hibernate.LONG)
		 .addScalar("DiscountLimit",Hibernate.LONG).addScalar("MinServiceCharge",Hibernate.LONG)
		 .addScalar("MaxServiceCharge",Hibernate.LONG).addScalar("MinTxnValue",Hibernate.LONG)
		 .addScalar("MaxTxnValue",Hibernate.LONG).addScalar("TxnTimeZoneRule", Hibernate.INTEGER)
		 .addScalar("ImposedOn", Hibernate.INTEGER) ;*/

		// sql injection done
		Query query = session.createSQLQuery(
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID  " +
				"     where sct.TransactionType=:transactionType and sct.sourceType=:sourceType and RuleLevel=:ruleLevel"+
				" and " +
				" ( STR_TO_DATE(:date,'%d-%m-%Y') between sc.ApplicableFrom and sc.ApplicableTo )  and" +
				" sca.Day=:day and " +
				" ( :hoursOfDay between sca.FromHH and sca.ToHH ) "		
		).addScalar("RuleLevel",Hibernate.INTEGER).addScalar("ServiceChargeRuleID",Hibernate.LONG)
		 .addScalar("ServiceChargePct",Hibernate.FLOAT).addScalar("ServiceChargeFxd",Hibernate.LONG)
		 .addScalar("DiscountLimit",Hibernate.LONG).addScalar("MinServiceCharge",Hibernate.LONG)
		 .addScalar("MaxServiceCharge",Hibernate.LONG).addScalar("MinTxnValue",Hibernate.LONG)
		 .addScalar("MaxTxnValue",Hibernate.LONG).addScalar("TxnTimeZoneRule", Hibernate.INTEGER)
		 .addScalar("ImposedOn", Hibernate.INTEGER) ;
		query.setParameter("transactionType", transactionType)
			.setParameter("sourceType", sourceType)
			.setParameter("ruleLevel", EOTConstants.RULE_LEVEL_INTER_BANK)
			.setParameter("date", date)
			.setParameter("day", calendar.get(Calendar.DAY_OF_WEEK))
			.setParameter("hoursOfDay", calendar.get(Calendar.HOUR_OF_DAY));
		
		List<Object[]> list = query.list();

		Map<Integer, Map<Long, List<SCRuleDTO>>> dtoMap = new HashMap<Integer, Map<Long, List<SCRuleDTO>>>();

		dtoMap.put(EOTConstants.RULE_LEVEL_GLOBAL, new HashMap<Long, List<SCRuleDTO>>());
		dtoMap.put(EOTConstants.RULE_LEVEL_BANK_GROUP, new HashMap<Long, List<SCRuleDTO>>());
		dtoMap.put(EOTConstants.RULE_LEVEL_CUSTOMER_PROFILE, new HashMap<Long, List<SCRuleDTO>>());
		dtoMap.put(EOTConstants.RULE_LEVEL_INTER_BANK, new HashMap<Long, List<SCRuleDTO>>());

		for(Object[] obj : list){

			SCRuleDTO dto = new SCRuleDTO();
			dto.setRuleType((Integer)obj[0]);
			dto.setServiceChargeRuleId((Long)obj[1]);
			dto.setServiceChargePct((Float)obj[2]);
			dto.setServiceChargeFxd((Long)obj[3]);
			dto.setDiscountLimit((Long)obj[4]);
			dto.setMinServiceCharge((Long)obj[5]);
			dto.setMaxServiceCharge((Long)obj[6]);
			dto.setMinTxnValue((Long)obj[7]);
			dto.setMaxTxnValue((Long)obj[8]);
			dto.setTimeZoneRule((Integer)obj[9]);
			dto.setImposedOn((Integer)obj[10]);

//			System.out.println(dto);

			Map<Long, List<SCRuleDTO>> ruleMap = dtoMap.get(dto.getRuleType());
			List<SCRuleDTO> serviceRuleList = ruleMap.get( dto.getServiceChargeRuleId() );
			if(serviceRuleList == null ){
				serviceRuleList = new ArrayList<SCRuleDTO>();
				ruleMap.put( dto.getServiceChargeRuleId() , serviceRuleList );
			}

			serviceRuleList.add(dto);

		}

		return dtoMap;
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public Map<Integer, Map<Long, List<SCRuleDTO>>> getIntraBankServiceChargeRules( Integer transactionType, String sourceType , Integer bankGroupId1, Integer profileId1,Double txnAmount ){

		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Calendar calendar = Calendar.getInstance() ;

		String date = Util.formatDateToString(calendar.getTime(), "dd-MM-yyyy");

		/*Query query = session.createSQLQuery(
				"select * from ( " +
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID  " +
				"     where sct.TransactionType="+transactionType+" and sct.sourceType="+ sourceType +" and RuleLevel="+EOTConstants.RULE_LEVEL_GLOBAL +
				" union all " +
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID " +
				"     where sct.TransactionType="+transactionType+" and sct.sourceType="+ sourceType+" and RuleLevel="+EOTConstants.RULE_LEVEL_BANK_GROUP  +" and sc.ReferenceID="+bankGroupId+" and RuleLevel="+EOTConstants.RULE_LEVEL_BANK_GROUP +
				" union all " +
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID  " +
				"      where sct.TransactionType="+transactionType+" and sct.sourceType="+ sourceType+" and RuleLevel="+EOTConstants.RULE_LEVEL_CUSTOMER_PROFILE  +" and sc.ReferenceID="+profileId +" and RuleLevel="+EOTConstants.RULE_LEVEL_CUSTOMER_PROFILE+
				") a " +
				" where " +
				" ( STR_TO_DATE('"+ date +"','%d-%m-%Y') between a.ApplicableFrom and a.ApplicableTo )  and" +
				" a.Day="+calendar.get(Calendar.DAY_OF_WEEK)+" and " +
				" ( "+calendar.get(Calendar.HOUR_OF_DAY)+" between a.FromHH and a.ToHH ) "		
		).addScalar("RuleLevel",Hibernate.INTEGER).addScalar("ServiceChargeRuleID",Hibernate.LONG)
		.addScalar("ServiceChargePct",Hibernate.FLOAT).addScalar("ServiceChargeFxd",Hibernate.LONG)
		.addScalar("DiscountLimit",Hibernate.LONG).addScalar("MinServiceCharge",Hibernate.LONG)
		.addScalar("MaxServiceCharge",Hibernate.LONG).addScalar("MinTxnValue",Hibernate.LONG)
		.addScalar("MaxTxnValue",Hibernate.LONG).addScalar("TxnTimeZoneRule", Hibernate.INTEGER)
		.addScalar("ImposedOn", Hibernate.INTEGER) ;*/
		
		// sql injection done
		Query query = session.createSQLQuery(
				"select * from ( " +
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID  " +
				"     where sct.TransactionType=:transactionType1 and sct.sourceType=:sourceType1 and RuleLevel=:ruleLevel1" +
				" union all " +
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID " +
				"     where sct.TransactionType=:transactionType2 and sct.sourceType=:sourceType2 and RuleLevel=:ruleLevel2"  /*+" and sc.ReferenceID="+bankGroupId+" and RuleLevel="+EOTConstants.RULE_LEVEL_BANK_GROUP */+
				" union all " +
				"select RuleLevel,sc.TxnTimeZoneRule,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.* from ServiceChargeRules sc" +
				" left join ServiceChargeRuleTxn sct on sc.ServiceChargeRuleID=sct.ServiceChargeRuleID" +
				" left join ServiceChargeRuleValues  scv on sc.ServiceChargeRuleID=scv.ServiceChargeRuleID" +
				" left join SCApplicableTime sca on sc.ServiceChargeRuleID=sca.ServiceChargeRuleID  " +
				"      where sct.TransactionType=:transactionType3 and sct.sourceType=:sourceType3 and RuleLevel=:ruleLevel3"  /*+" and sc.ReferenceID="+profileId +" and RuleLevel="+EOTConstants.RULE_LEVEL_CUSTOMER_PROFILE*/+
				") a " +
				" where " +
				" ( STR_TO_DATE(:date,'%d-%m-%Y') between a.ApplicableFrom and a.ApplicableTo )  and" +
				" a.Day=:day and " +
				" ( :hoursOfDay between a.FromHH and a.ToHH ) "		
		).addScalar("RuleLevel",Hibernate.INTEGER).addScalar("ServiceChargeRuleID",Hibernate.LONG)
		.addScalar("ServiceChargePct",Hibernate.FLOAT).addScalar("ServiceChargeFxd",Hibernate.LONG)
		.addScalar("DiscountLimit",Hibernate.LONG).addScalar("MinServiceCharge",Hibernate.LONG)
		.addScalar("MaxServiceCharge",Hibernate.LONG).addScalar("MinTxnValue",Hibernate.LONG)
		.addScalar("MaxTxnValue",Hibernate.LONG).addScalar("TxnTimeZoneRule", Hibernate.INTEGER)
		.addScalar("ImposedOn", Hibernate.INTEGER) ;
		
		query.setParameter("transactionType1", transactionType)
			 .setParameter("transactionType2", transactionType)
			 .setParameter("transactionType3", transactionType)
			 .setParameter("sourceType1", sourceType)
			 .setParameter("sourceType2", sourceType)
			 .setParameter("sourceType3", sourceType)
			 .setParameter("ruleLevel1", EOTConstants.RULE_LEVEL_GLOBAL)
			 .setParameter("ruleLevel2", EOTConstants.RULE_LEVEL_BANK_GROUP)
			 .setParameter("ruleLevel3", EOTConstants.RULE_LEVEL_CUSTOMER_PROFILE)
			 .setParameter("date", date).setParameter("day", calendar.get(Calendar.DAY_OF_WEEK))
			 .setParameter("hoursOfDay", calendar.get(Calendar.HOUR_OF_DAY));
		
		List<Object[]> list = query.list();

		Map<Integer, Map<Long, List<SCRuleDTO>>> dtoMap = new HashMap<Integer, Map<Long, List<SCRuleDTO>>>();

		dtoMap.put(EOTConstants.RULE_LEVEL_GLOBAL, new HashMap<Long, List<SCRuleDTO>>());
		dtoMap.put(EOTConstants.RULE_LEVEL_BANK_GROUP, new HashMap<Long, List<SCRuleDTO>>());
		dtoMap.put(EOTConstants.RULE_LEVEL_CUSTOMER_PROFILE, new HashMap<Long, List<SCRuleDTO>>());
		dtoMap.put(EOTConstants.RULE_LEVEL_INTER_BANK, new HashMap<Long, List<SCRuleDTO>>());


		for(Object[] obj : list){

			SCRuleDTO dto = new SCRuleDTO();
			dto.setRuleType((Integer)obj[0]);
			dto.setServiceChargeRuleId((Long)obj[1]);
			dto.setServiceChargePct((Float)obj[2]);
			dto.setServiceChargeFxd((Long)obj[3]);
			dto.setDiscountLimit((Long)obj[4]);
			dto.setMinServiceCharge((Long)obj[5]);
			dto.setMaxServiceCharge((Long)obj[6]);
			dto.setMinTxnValue((Long)obj[7]);
			dto.setMaxTxnValue((Long)obj[8]);
			dto.setTimeZoneRule((Integer)obj[9]);
			dto.setImposedOn((Integer)obj[10]);

//			System.out.println(dto);

			Map<Long, List<SCRuleDTO>> ruleMap = dtoMap.get(dto.getRuleType());
			List<SCRuleDTO> serviceRuleList = ruleMap.get( dto.getServiceChargeRuleId() );
			if(serviceRuleList == null ){
				serviceRuleList = new ArrayList<SCRuleDTO>();
				ruleMap.put( dto.getServiceChargeRuleId() , serviceRuleList );
			}

			serviceRuleList.add(dto);

		}

		return dtoMap;
	}

	/*@SuppressWarnings("unchecked")
	@Override
	public float getBankServiceChargePercent(Integer transactionType, String accountNumber) {

		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createSQLQuery("select scs.ServiceChargePct from ServiceChargeSplits scs " +
		"where scs.AccountNumber=:accountNumber and scs.TransactionType=:transactionType")
		.addScalar("ServiceChargePct",Hibernate.FLOAT)
		.setParameter("transactionType", transactionType)
		.setParameter("accountNumber", accountNumber);

		List list = query.list();

		return list.size() == 0 ? 0.0F : (Float) list.get(0) ;
	}
*/
	/*@SuppressWarnings("unchecked")
	public Map<String, Float> getStakeholderServiceChargePercent(Integer transactionType,Integer bankId) {

		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createSQLQuery("select scs.AccountNumber,scs.ServiceChargePct from ServiceChargeSplits scs " +
				" join StakeHolder sth on scs.AccountNumber=sth.AccountNumber " +
		"where scs.TransactionType=:transactionType")
		.addScalar("accountNumber",Hibernate.STRING)
		.addScalar("ServiceChargePct",Hibernate.FLOAT)
		.setParameter("transactionType", transactionType) ;

		List<Object[]> list = query.list();

		Map<String, Float> map = new HashMap<String, Float>();

		for(Object[] obj:list){
			map.put((String)obj[0], (Float)obj[1]);
		}

		return map;
	}*/

	/*@SuppressWarnings("unchecked")
	@Override
	public List<ServiceChargeSplit> getServiceChargeSplit(Integer transactionType, Integer bankId) {
		
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createQuery("from ServiceChargeSplit scs where " +
				"scs.transactionType.transactionType=:transactionType and scs.bank.bankId=:bankId")
		.setParameter("bankId",bankId)
		.setParameter("transactionType", transactionType);

		return query.list();

	}*/
	
	/*@SuppressWarnings("unchecked")
	@Override
	public List<ServiceChargeSplit> getAmountPercentListIntra(
			Integer transactionType, Integer bankId) {
		
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createQuery("from ServiceChargeSplit scs where " +
				"scs.transactionType.transactionType=:transactionType and scs.bank.bankId=:bankId and scs.isInter=" + CoreConstants.TXN_TYPE_INTRA )
		.setParameter("bankId",bankId)
		.setParameter("transactionType", transactionType) ;

		return query.list();
	}
	*/
	/*@SuppressWarnings("unchecked")
	@Override
	public List<ServiceChargeSplit> getAmountPercentListInter(
			Integer transactionType) {
		
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createQuery("from ServiceChargeSplit scs where " +
				"scs.transactionType.transactionType=:transactionType and scs.bank.bankId is null and scs.isInter=" + CoreConstants.TXN_TYPE_INTER )
		.setParameter("transactionType", transactionType) ;

		return query.list();
	}*/
/*
	@Override
	public List<ServiceChargeSplit> getStampFeeFromServiceChargeSplit(
			Integer transactionType, Integer bankId) {
		
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createQuery("from ServiceChargeSplit scs where " +
				"scs.transactionType.transactionType=:transactionType and scs.bank.bankId=:bankId and scs.amountType='" + CoreConstants.AMT_TYPE_STAMP_FEE + "'" )
		.setParameter("bankId",bankId)
		.setParameter("transactionType", transactionType) ;

		return query.list();
	}*/

	/*@Override
	public List<ServiceChargeSplit> getTaxFromServiceChargeSplit(
			Integer transactionType, Integer bankId) {
		
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createQuery("from ServiceChargeSplit scs where " +
				"scs.transactionType.transactionType=:transactionType and scs.bank.bankId=:bankId and scs.amountType='" + CoreConstants.AMT_TYPE_TAX_AMT + "'" )
		.setParameter("bankId",bankId)
		.setParameter("transactionType", transactionType) ;

		return query.list();
	}*/

}
