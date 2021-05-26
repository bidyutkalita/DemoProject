/* Copyright � EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: EOTMobileDaoImpl.java,v 1.0
 *
 * Date Author Changes
 * 21 Oct, 2015, 2:58:10 PM Sambit Created
 */
package com.eot.banking.daos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.common.KycStatusEnum;
import com.eot.banking.common.OtpStatusEnum;
import com.eot.banking.controller.CustomerModel;
import com.eot.banking.daos.base.BaseDaoImpl;
import com.eot.banking.dto.CurrentBalance;
import com.eot.banking.dto.FAQsModelDTO.FAQ;
import com.eot.banking.dto.LocateUsDTO;
import com.eot.banking.dto.MobileMenuMasterDataDTO;
import com.eot.banking.dto.PendingTransactionDTO;
import com.eot.banking.dto.ReportsModel;
import com.eot.banking.dto.ReversalTransactionDTO;
import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.dto.WithdrawalTransactionsDTO;
import com.eot.banking.server.Constants;
import com.eot.banking.server.data.OtpDTO;
import com.eot.banking.utils.DateUtil;
import com.eot.entity.Account;
import com.eot.entity.AppMaster;
import com.eot.entity.Bank;
import com.eot.entity.Biller;
import com.eot.entity.BillerTypes;
import com.eot.entity.Branch;
import com.eot.entity.BusinessPartner;
import com.eot.entity.BusinessPartnerUser;
import com.eot.entity.City;
import com.eot.entity.ClearingHousePool;
import com.eot.entity.ClearingHousePoolMember;
import com.eot.entity.Country;
import com.eot.entity.Currency;
import com.eot.entity.CurrencyConverter;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.entity.CustomerDocument;
import com.eot.entity.CustomerProfiles;
import com.eot.entity.ElectricityBill;
import com.eot.entity.ExchangeRate;
import com.eot.entity.ExternalTransaction;
import com.eot.entity.HelpDesk;
import com.eot.entity.IncorrectKycDetails;
import com.eot.entity.KycType;
import com.eot.entity.Language;
import com.eot.entity.LocateUS;
import com.eot.entity.LocationType;
import com.eot.entity.MobileMenuConfiguration;
import com.eot.entity.MobileMenuLanguage;
import com.eot.entity.MobileRequest;
import com.eot.entity.Mobiletheamcolorconfig;
import com.eot.entity.NetworkType;
import com.eot.entity.Operator;
import com.eot.entity.Otp;
import com.eot.entity.Payee;
import com.eot.entity.PendingTransaction;
import com.eot.entity.Quarter;
import com.eot.entity.RemittanceCompany;
import com.eot.entity.RemittanceTransaction;
import com.eot.entity.SecurityQuestion;
import com.eot.entity.SenelecBills;
import com.eot.entity.ServiceChargeSplit;
import com.eot.entity.Transaction;
import com.eot.entity.TransactionType;
import com.eot.entity.VersionDetails;
import com.eot.entity.WebUser;

// TODO: Auto-generated Javadoc
/**
 * The Class EOTMobileDaoImpl.
 */
@SuppressWarnings("unchecked")
public class EOTMobileDaoImpl extends BaseDaoImpl implements EOTMobileDao {

	private static final int List = 0;


	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomer(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Customer getCustomer(String applicationId) {

		List<Customer> custList = getHibernateTemplate().findByNamedParam("from Customer cust where " +
				"cust.appId=:applicationId","applicationId", applicationId);

		return (Customer)(custList.size() > 0 ? custList.get(0) : null) ;

	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public CustomerProfiles getCustomerProfile(Integer  bankId ,Integer customerType) {

		 Query query = getSessionFactory().getCurrentSession().createQuery("from CustomerProfiles cust where cust.bank.bankId=:bankId and cust.customerType=:customerType ORDER BY cust.authorizedAmount")
				 .setParameter("bankId", bankId)
				 .setParameter("customerType", customerType);
		 List<CustomerProfiles> custProfileList=query.list();
		 System.out.println(custProfileList.get(0).getProfileName());
		return (CustomerProfiles)(custProfileList.size() > 0 ? custProfileList.get(0) : null) ;

	}
	
	@Override
	@SuppressWarnings("unchecked")
	public CustomerProfiles getSelfRegisterProfile() {

		 Query query = getSessionFactory().getCurrentSession().createQuery("from CustomerProfiles cust where  cust.customerType=:customerType").setParameter("customerType", EOTConstants.PROFILE_TYPE_SELF_REGISTER);
		 List<CustomerProfiles> custProfileList=query.list();
		return (CustomerProfiles)(custProfileList.size() > 0 ? custProfileList.get(0) : null) ;

	}
	
	@SuppressWarnings("unchecked")
	public Customer getCustomerByCustomerId(String customerId) {

		List<Customer> custList = getHibernateTemplate().findByNamedParam("from Customer cust where " +
				"cust.customerId=:customerId","customerId", customerId);

		return (Customer)(custList.size() > 0 ? custList.get(0) : null) ;

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerFromMobileNo(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Customer getCustomerFromMobileNo(String mobileNumber) {

		/*List<Customer> custList = getHibernateTemplate().findByNamedParam("from Customer cust where cust.mobileNumber=:mobileNumber","mobileNumber", mobileNumber);

		return (Customer)(custList.size() > 0 ? custList.get(0) : null) ;*/
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer cs where concat(cs.country.isdCode,mobileNumber)=:mobileNumber")
				.setParameter("mobileNumber",mobileNumber);
		List<Customer>list=query.list();
		return list.size() > 0 ? list.get(0) : null ;

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerAccounts(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<CustomerAccount> getCustomerAccounts(Long customerId) {

		return getHibernateTemplate().findByNamedParam("from CustomerAccount acc where " +
				"acc.customer.customerId=:customerId","customerId", customerId);

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBanks()
	 */
	@SuppressWarnings("unchecked")
	public List<Bank> getBanks() {

		return getHibernateTemplate().find("from Bank");

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCardDetails(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<CustomerCard> getCardDetails(Long customerId) {

		/*return getHibernateTemplate().findByNamedParam("from CustomerCard card where card.referenceId=:customerId and " +
				"status !=3","customerId", customerId+""); */
		
		return getHibernateTemplate().findByNamedParam("from CustomerCard card where card.referenceId=:customerId and " +
				"status !=:status",new String[]{"customerId","status"}, new Object[]{customerId+"",3}); 

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAccountFromAccountAlias(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public CustomerAccount getAccountFromAccountAlias(Long customerId,String accountAlias) {
		String custID =  String.valueOf(customerId);
		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam(
				"from CustomerAccount acc where acc.customer.customerId=:customerId and acc.account.alias=:alias" ,
				new String[]{"customerId","alias"}, new Object[]{customerId,accountAlias}
				);

		return accountList.size() > 0 ? accountList.get(0) : null ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getPayeeAccountFromMobileNo(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public CustomerAccount getPayeeAccountFromMobileNo(String mobileNo) {

		//		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam("from CustomerAccount acc where acc.customer.mobileNumber=:mobileNumber","mobileNumber", mobileNo);

		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam("from CustomerAccount acc where " +
				"concat(acc.customer.country.isdCode,acc.customer.mobileNumber)=:mobileNumber","mobileNumber", mobileNo);

		return accountList.size() > 0 ? accountList.get(0) : null ;

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getRequest(java.lang.String, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public MobileRequest getRequest(String applicationId, Long stan, Long rrn, String transmissionTime ) {

		/*List<MobileRequest> mobileRequestList = getHibernateTemplate().find("from MobileRequest mr where " +
				"mr.appMaster.appId='"+applicationId+"' and mr.stan="+stan+" and mr.rrn="+rrn+" and " +
				"STR_TO_DATE('"+transmissionTime+"','%Y-%m-%d') = mr.transmissionTime");

		return mobileRequestList.size()>0 ? mobileRequestList.get(0) : null;*/
		
		List<MobileRequest> mobileRequestList = getHibernateTemplate().find("from MobileRequest mr where " +
				"mr.appMaster.appId=? and mr.stan=? and mr.rrn=? and " +
				"STR_TO_DATE('"+transmissionTime+"','%Y-%m-%d') = mr.transmissionTime", new Object[]{applicationId,stan,rrn});

		return mobileRequestList.size()>0 ? mobileRequestList.get(0) : null;

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getApplicationType(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public AppMaster getApplicationType(String applicationId) {

		List<AppMaster> appList = getHibernateTemplate().findByNamedParam("from AppMaster app where app.appId=:appId","appId", applicationId);

		return appList.size() > 0 ? appList.get(0) : null ;

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCutomerAccountFromMobileNo(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public CustomerAccount getCutomerAccountFromMobileNo(String mobileNo,String accountAlias) {

		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam(
				"from CustomerAccount acc where acc.customer.mobileNumber=:mobileNumber and acc.account.alias=:alias" ,
				new String[]{"mobileNumber","alias"},new Object[]{ mobileNo, accountAlias }
				);

		return accountList.size() > 0 ? accountList.get(0) : null ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getOperatorList(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<Operator> getOperatorList(Integer countryId) {

		/*return getHibernateTemplate().find("from Operator operator where operator.active=1");*/
		
		return getHibernateTemplate().find("from Operator operator where operator.active=?",new Object[]{1});

	}
	@Override
	public List<Operator> getOperatorListByBankCode(String cbsBankCode) {

		/*return getHibernateTemplate().findByNamedParam("from Operator operator where operator.active=1 and operator.bank.bankCode=:cbsBankCode","cbsBankCode",cbsBankCode);*/
		
		return getHibernateTemplate().findByNamedParam("from Operator operator where operator.active=:active and operator.bank.bankCode=:cbsBankCode",
				new String[]{"active","cbsBankCode"},new Object[]{ 1, cbsBankCode });

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getClearingHouse(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<ClearingHousePoolMember> getClearingHouse(Integer customerBank,Integer otherPartyBank){

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession() ;

		/*Query query = session.createSQLQuery("select ch1.* from ClearingHousePoolMembers ch1 join ClearingHousePoolMembers ch2 " +
				"join ClearingHousePool chp " +
				" where ch1.clearingPoolId=ch2.clearingPoolId and ch1.bankId=:customerBank and ch2.bankId=:otherPartyBank and chp.Status=1")
				.setParameter("customerBank", customerBank)
				.setParameter("otherPartyBank", otherPartyBank);*/
		
		Query query = session.createSQLQuery("select ch1.* from ClearingHousePoolMembers ch1 join ClearingHousePoolMembers ch2 " +
				"join ClearingHousePool chp " +
				" where ch1.clearingPoolId=ch2.clearingPoolId and ch1.bankId=:customerBank and ch2.bankId=:otherPartyBank and chp.Status=:Status")
				.setParameter("customerBank", customerBank)
				.setParameter("otherPartyBank", otherPartyBank)
				.setParameter("Status", 1);

		List<ClearingHousePoolMember> chPoolList = query.list();

		return chPoolList.size()>0 ? chPoolList : null;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#verifyOTP(com.eot.banking.server.data.OtpDTO)
	 */
	@SuppressWarnings("unchecked")
	public Otp verifyOTP(OtpDTO otpDTO) {

		String[] params = new String[]{"referenceId","referenceType", "otpHash","otpType","status" } ;
		Object[] values = new Object[]{otpDTO.getReferenceId(),otpDTO.getReferenceType(),otpDTO.getOtphash(),otpDTO.getOtpType(),OtpStatusEnum.NEW.getCode()};

		List<Otp> list= getHibernateTemplate().findByNamedParam("from Otp as otp where otp.comp_id.referenceId=:referenceId and " +
				"otp.comp_id.referenceType=:referenceType and otp.otpHash=:otpHash and otp.otpType=:otpType and otp.status=:status",params,values);

		return list.size()>0 ? list.get(0) : null ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Otp verifyOTPWithAmount(OtpDTO otpDTO) {

		String[] params = new String[]{"referenceId","referenceType", "otpHash","otpType","amount", "status" } ;
		Object[] values = new Object[]{otpDTO.getReferenceId(),otpDTO.getReferenceType(),otpDTO.getOtphash(),otpDTO.getOtpType(),otpDTO.getAmount(), OtpStatusEnum.NEW.getCode()};

		List<Otp> list= getHibernateTemplate().findByNamedParam("from Otp as otp where otp.comp_id.referenceId=:referenceId and " +
				"otp.comp_id.referenceType=:referenceType and otp.otpHash=:otpHash and otp.otpType=:otpType and otp.amount=:amount and otp.status=:status",params,values);

		return list.size()>0 ? list.get(0) : null ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerCardForConfirmation(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public CustomerCard getCustomerCardForConfirmation(Long customerId,String cardAlias) {

		List<CustomerCard> cardList = getHibernateTemplate().findByNamedParam(
				"from CustomerCard custCard where custCard.referenceId=:customerId and custCard.alias=:alias and custCard.status=:status",
				new String[]{"customerId","alias","status"},new Object[]{customerId,cardAlias,Constants.CARD_STATUS_NOT_CONFIRMED}
				);

		return cardList.size() > 0 ? cardList.get(0) : null ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerCardFromAlias(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public CustomerCard getCustomerCardFromAlias(Long customerId,String cardAlias) {

		List<CustomerCard> cardList = getHibernateTemplate().findByNamedParam(
				"from CustomerCard custCard where custCard.referenceId=:customerId and custCard.alias=:alias ",
				new String[]{"customerId","alias"},new Object[]{customerId,cardAlias}
				);

		return cardList.size() > 0 ? cardList.get(0) : null ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#findOperator(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public Operator findOperator(Long operatorId) {

		List<Operator> opList = getHibernateTemplate().findByNamedParam("from Operator op where op.operatorId=:operatorId","operatorId", operatorId);

		return (Operator)(opList.size() > 0 ? opList.get(0) : null) ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getElectricityBill(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public ElectricityBill getElectricityBill(String billerCustomerId) {

		/*List<ElectricityBill> elecBill = getHibernateTemplate().findByNamedParam("from ElectricityBill eb where " +
				"eb.billerCustomerId=:billerCustomerId and eb.dueDate=(select max(ineb.dueDate) from ElectricityBill ineb where " +
				"ineb.billerCustomerId=:billerCustomerId and ineb.status=0)",
				new String[]{"billerCustomerId"},new Object[]{billerCustomerId});
*/
		List<ElectricityBill> elecBill = getHibernateTemplate().findByNamedParam("from ElectricityBill eb where " +
				"eb.billerCustomerId=:billerCustomerId and eb.dueDate=(select max(ineb.dueDate) from ElectricityBill ineb where " +
				"ineb.billerCustomerId=:billerCustomerId and ineb.status=:status)",
				new String[]{"billerCustomerId","status"},new Object[]{billerCustomerId,0});
	
		
		return (ElectricityBill)(elecBill.size() > 0 ? elecBill.get(0) : null) ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBiller(java.lang.Integer)
	 */
	public Biller getBiller(Integer billerId) {
		return getHibernateTemplate().load(Biller.class, billerId);
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAllCountry()
	 */
	public List<Country> getAllCountry() {

		List<Country> countryList = getHibernateTemplate().find("from Country");

		return countryList;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBillerList(java.lang.Integer)
	 */
	public List<Biller> getBillerList(Integer countryId) {

		return getHibernateTemplate().findByNamedParam("from Biller biller where biller.country.countryId =:countryId" , "countryId",countryId);
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getElectricityBillFromPolicyId(java.lang.String)
	 */
	public SenelecBills getElectricityBillFromPolicyId(String policyNumber) {

		/*List<SenelecBills> senelecBill = getHibernateTemplate().findByNamedParam("from SenelecBills sb where " +
				"sb.senelecCustomers.policyNumber=:policyNumber and sb.status in (0,2)", "policyNumber", policyNumber);*/
		
		String[] params = new String[]{"policyNumber","status1", "status2"} ;
		Object[] values = new Object[]{policyNumber,0,2};
		
		List<SenelecBills> senelecBill = getHibernateTemplate().findByNamedParam("from SenelecBills sb where " +
				"sb.senelecCustomers.policyNumber=:policyNumber and sb.status in (:status1,:status2)", params,values);

		return (SenelecBills)(senelecBill.size() > 0 ? senelecBill.get(0) : null);
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#lastSuccessfulTransaction(java.lang.String, java.lang.Integer)
	 */
	public List<MobileRequest> lastSuccessfulTransaction(String applicationId,Integer txnTypeOfLastTransaction) {

		/*List<MobileRequest> mobileRequest = getHibernateTemplate().findByNamedParam("from MobileRequest mr where mr.appMaster.appId=:applicationId " +
				"and mr.status='1' and mr.transactionType.transactionType in(55,80,82,83,90) order by mr.requestId desc limit 1 ", "applicationId", applicationId);*/
		
		/*List<MobileRequest> mobileRequest = getHibernateTemplate().findByNamedParam("from MobileRequest mr where mr.appMaster.appId=:applicationId " +
				"and mr.status='1' and mr.transactionType.transactionType ="+ txnTypeOfLastTransaction +"order by mr.requestId desc limit 5 ", "applicationId", applicationId);
		return mobileRequest.size()>0 ? mobileRequest : null;*/
		
		String[] params = new String[]{"applicationId","status","txnTypeOfLastTransaction"} ;
		Object[] values = new Object[]{applicationId,1,txnTypeOfLastTransaction};
		
		List<MobileRequest> mobileRequest = getHibernateTemplate().findByNamedParam("from MobileRequest mr where mr.appMaster.appId=:applicationId " +
				"and mr.status=:status and mr.transactionType.transactionType =:txnTypeOfLastTransaction order by mr.requestId desc limit 5 ", params, values);
		return mobileRequest.size()>0 ? mobileRequest : null;
		
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerCardByCardNumber(java.lang.String)
	 */
	public CustomerCard getCustomerCardByCardNumber(String cardNumber) {

		/*List<CustomerCard> customerCard = getHibernateTemplate().findByNamedParam("from CustomerCard cd where " +
				"cd.cardNumber=:cardNumber and status !=3","cardNumber",cardNumber);
		return (CustomerCard)(customerCard.size() > 0 ? customerCard.get(0) : null);*/
		
		String[] params = new String[]{"cardNumber","status"} ;
		Object[] values = new Object[]{cardNumber,3};
		
		List<CustomerCard> customerCard = getHibernateTemplate().findByNamedParam("from CustomerCard cd where " +
				"cd.cardNumber=:cardNumber and status !=:status",params,values);
		return (CustomerCard)(customerCard.size() > 0 ? customerCard.get(0) : null);
		
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getClearingHouse(java.lang.Integer)
	 */
	public ClearingHousePool getClearingHouse(Integer clearingHouseId) {

		/*List<ClearingHousePool> clearingHousePool=getHibernateTemplate().find("from ClearingHousePool chp " +
				"where chp.clearingPoolId=:clearingPoolId and chp.status=1");
		return (ClearingHousePool)(clearingHousePool.size() > 0 ? clearingHousePool.get(0) : null);*/
		
		
		List<ClearingHousePool> clearingHousePool=getHibernateTemplate().find("from ClearingHousePool chp " +
				"where chp.clearingPoolId=? and chp.status=?",new Object[]{clearingHouseId,1});
		return (ClearingHousePool)(clearingHousePool.size() > 0 ? clearingHousePool.get(0) : null);
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getVirtualCardforBank(java.lang.String)
	 */
	public CustomerCard getVirtualCardforBank(String bankId) {

	/*	List<CustomerCard> bankVirtualCard = getHibernateTemplate().find("from CustomerCard ca where " +
				"ca.referenceType=5 and ca.status=2 and ca.referenceId=?",bankId);
		return bankVirtualCard.size()>0?bankVirtualCard.get(0):null;*/
	
		
		
		List<CustomerCard> bankVirtualCard = getHibernateTemplate().find("from CustomerCard ca where " +
				"ca.referenceType=? and ca.status=? and ca.referenceId=?",new Object[]{5,2,bankId});
		return bankVirtualCard.size()>0?bankVirtualCard.get(0):null;
	
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getApplicationVersion(java.lang.String)
	 */
	public VersionDetails getApplicationVersion(String versionNumber) {

		List<VersionDetails> versionList = getHibernateTemplate().find("from VersionDetails vd where vd.currentVersion=?",versionNumber);

		return versionList.size()>0?versionList.get(0):null;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBankAccountFromAccountAlias(java.lang.Long, java.lang.String)
	 */
	public CustomerBankAccount getBankAccountFromAccountAlias(Long customerId,String accountAlias) {
		/*List<CustomerBankAccount> bankAccountList = getHibernateTemplate().findByNamedParam(
				"from CustomerBankAccount cba where cba.referenceId=:customerId and cba.alias=:alias and status !=3",
				new String[]{"customerId","alias"},new Object[]{customerId+"",accountAlias}
				);

		return bankAccountList.size() > 0 ? bankAccountList.get(0) : null ;*/
		
		
		List<CustomerBankAccount> bankAccountList = getHibernateTemplate().findByNamedParam(
				"from CustomerBankAccount cba where cba.referenceId=:customerId and cba.alias=:alias and status !=:status",
				new String[]{"customerId","alias","status"},new Object[]{customerId,accountAlias,3}
				);

		return bankAccountList.size() > 0 ? bankAccountList.get(0) : null ;
		
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerBankAccountFromAccountNumber(java.lang.String)
	 */
	public CustomerBankAccount getCustomerBankAccountFromAccountNumber(String accountNumber) {
		/*List<CustomerBankAccount> bankAccountList = getHibernateTemplate().findByNamedParam(
				"from CustomerBankAccount cba where cba.bankAccountNumber=:accountNumber and status !=3",
				new String[]{"accountNumber"},new Object[]{accountNumber}
				);
		return bankAccountList.size() > 0 ? bankAccountList.get(0) : null ;*/
		
		List<CustomerBankAccount> bankAccountList = getHibernateTemplate().findByNamedParam(
				"from CustomerBankAccount cba where cba.bankAccountNumber=:accountNumber and status !=:status",
				new String[]{"accountNumber","status"},new Object[]{accountNumber,3}
				);
		return bankAccountList.size() > 0 ? bankAccountList.get(0) : null ;
		
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBranchs()
	 */
	public List<Branch> getBranchs() {

		return getHibernateTemplate().find("from Branch");
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBankAccountDetails(java.lang.Long)
	 */
	public List<CustomerBankAccount> getBankAccountDetails(Long customerId) {

		/*return getHibernateTemplate().findByNamedParam("from CustomerBankAccount cba where " +
				"cba.referenceId=:customerId and status !=3","customerId", customerId+""); */
		
		String[] params = new String[]{"status","customerId"} ;
		Object[] values = new Object[]{3,customerId+""};
		
		return getHibernateTemplate().findByNamedParam("from CustomerBankAccount cba where " +
				"cba.referenceId=:customerId and status !=:status",params,values); 

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBankFromBankId(java.lang.Integer)
	 */
	public Bank getBankFromBankId(Integer bankId) {
		List<Bank> bankList = getHibernateTemplate().findByNamedParam("from Bank bank where " +
				"bank.bankId=:bankId","bankId", bankId);

		return (Bank)(bankList.size() > 0 ? bankList.get(0) : null) ;
	}
	
	@Override
	public Long getBranchId(Integer bankId, String branchCode) {
		List<Branch> branch = getHibernateTemplate().findByNamedParam("from Branch where bank.bankId=:bankId and branchCode=:branchCode", new String[]{"bankId","branchCode"},new Object[]{bankId,branchCode});
		return CollectionUtils.isNotEmpty(branch)?branch.get(0).getBranchId():null ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBranchFromBranchId(java.lang.Long)
	 */
	public Branch getBranchFromBranchId(Long branchId) {
		List<Branch> branchList = getHibernateTemplate().findByNamedParam("from Branch branch where " +
				"branch.branchId=:branchId","branchId", branchId);

		return (Branch)(branchList.size() > 0 ? branchList.get(0) : null) ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getMobileRequest(java.lang.Long)
	 */
	public MobileRequest getMobileRequest(Long requestID) {
		List<MobileRequest> mobileRequestList = getHibernateTemplate().find("from MobileRequest mr where " +
				"mr.requestId=?",requestID);

		return mobileRequestList.size()>0 ? mobileRequestList.get(0) : null;
	}

	public TransactionType getLoadTransactionType(Integer transactionType) {
		TransactionType mobileRequestList = getHibernateTemplate().load(TransactionType.class,transactionType);
			

		return mobileRequestList;
	}
	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBillerTypeList()
	 */
	public List<BillerTypes> getBillerTypeList() {
		List<BillerTypes> billerTypeList = getHibernateTemplate().find("from BillerTypes");
		return billerTypeList;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getPayeeFromAlias(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Payee getPayeeFromAlias(Long customerId, String payeeAlias) {
		List<Payee> payeeList = getHibernateTemplate().findByNamedParam(
				"from Payee payee where payee.referenceId=:customerId and payee.alias=:payeeAlias ",
				new String[]{"customerId","payeeAlias"},new Object[]{customerId,payeeAlias}
				);

		return payeeList.size() > 0 ? payeeList.get(0) : null ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCountry(java.lang.Integer)
	 */
	@Override
	public Country getCountry(Integer countryID) {
		return getHibernateTemplate().get(Country.class, countryID);
	}
	
	
	/*@Override
	public String getCountryNames(Integer countryCodeNumeric) {
		
		Query query=getSessionFactory().getCurrentSession().createQuery("from Country cs where cs.countryCodeNumeric=:countryCode")
				.setParameter("countryCode",countryCodeNumeric);
	}*/

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerByMobileNumber(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Customer getCustomerByMobileNumber(String mobileNumber) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer cs where concat(cs.country.isdCode,mobileNumber)=:mobileNumber")
				.setParameter("mobileNumber",mobileNumber);
		List<Customer> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getNextAccountNumberSequence()
	 */
	@Override
	public Long getNextAccountNumberSequence() {
		Query query = getSessionFactory().getCurrentSession()
				.createSQLQuery("select IFNULL(max(mid(AccountNumber,1,12)),100000000000)+10 as seq from Account")
				.addScalar("seq",Hibernate.LONG);
		return new Long( query.list().get(0).toString() ) ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerAccountFromAlias(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CustomerAccount getCustomerAccountFromAlias(Long customerId,String alias) {

		Query query=getSessionFactory().getCurrentSession().createQuery("from CustomerAccount acc where acc.customer.customerId=:customerId and acc.account.alias=:alias")
				.setParameter("customerId",customerId)
				.setParameter("alias",alias);		                               
		List<CustomerAccount>list=query.list();
		return list.size() > 0 ? list.get(0) : null ;  

	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getPayeeList(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Payee> getPayeeList(Long customerId) {
		/*return getHibernateTemplate().findByNamedParam("from Payee payee where payee.referenceId=:customerId and " +
				"status !=3","customerId", customerId+""); */
		
		String[] params = new String[]{"customerId","status"} ;
		Object[] values = new Object[]{customerId+"",3};
		
		return getHibernateTemplate().findByNamedParam("from Payee payee where payee.referenceId=:customerId and " +
				"status !=:status",params,values); 
		
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAllCurrency()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Currency> getAllCurrency() {
		List<Currency> currencyList = getHibernateTemplate().find("from Currency");

		return currencyList;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getExchangeRateFromCurrecyIdAndBankId(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExchangeRate getExchangeRateFromCurrecyIdAndBankId(Integer currencyId, Integer bankId) {
		List<ExchangeRate> list = getHibernateTemplate().find("from ExchangeRate er where er.currency.currencyId =? and" +
				" er.bank.bankId =?", currencyId,bankId);
		return list.size() > 0 ? list.get(0) : null;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getServiceLocation(LocateUsDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LocateUS> getServiceLocation(LocateUsDTO locateUsDTO) {

		DetachedCriteria criteria = DetachedCriteria.forClass(LocateUS.class, "loc");

		criteria.setFetchMode("nt", FetchMode.JOIN);
		criteria.setFetchMode("locationType", FetchMode.JOIN);
		criteria.createAlias("loc.locateUsNetworkTypeMappings", "nt", CriteriaSpecification.LEFT_JOIN);
		criteria.add(Restrictions.eq("loc.country.countryId", getCountryIdFromNumbericCode(locateUsDTO.getCountryId()).getCountryId()));
		criteria.add(Restrictions.eq("loc.city.cityId", locateUsDTO.getCityId()));
		criteria.add(Restrictions.eq("loc.quarter.quarterId", Long.parseLong(locateUsDTO.getQuaterId().toString())));
		criteria.add(Restrictions.eq("loc.bank.bankId", getBankIdFromBankCode(locateUsDTO.getBankId()).getBankId()));
		criteria.add(Restrictions.eq("loc.locationType.locationTypeId", locateUsDTO.getLocationTypeId()));
		criteria.add(Restrictions.eq("loc.status", Constants.ACTIVE_STATUS));
		if(null!=locateUsDTO.getLocationTypeId()){
			if(1!=locateUsDTO.getLocationTypeId()) {
			criteria.add(Restrictions.ge("nt.comp_id.networkTypeId", locateUsDTO.getNetworkTypeId()));
			}
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<LocateUS> locationList = getHibernateTemplate().findByCriteria(criteria);

		return CollectionUtils.isNotEmpty(locationList) ? locationList : null; 
	}

	/**
	 * Gets the bank id from bank code.
	 *
	 * @param bankCode the bank code
	 * @return the bank id from bank code
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Bank getBankIdFromBankCode(String bankCode){
		List<Bank> list = getHibernateTemplate().find("from Bank bk where bk.bankCode =?", bankCode);
		return list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * Gets the country id from numberic code.
	 *
	 * @param contryNumericCode the contry numeric code
	 * @return the country id from numberic code
	 */
	public Country getCountryIdFromNumbericCode(Integer contryNumericCode){
		List<Country> list = getHibernateTemplate().find("from Country ctry where ctry.countryCodeNumeric =?", contryNumericCode);
		return list.size() > 0 ? list.get(0) : null;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.dao.LocationDao#getAllActiveLocationType(java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<LocationType> getAllActiveLocationType(String locale, Integer status) {
		@SuppressWarnings("unchecked")
		List<LocationType> list = getHibernateTemplate().find("from LocationType lt where lt.locale =? and" +
				" lt.status =?", locale,status);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAllCity()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<City> getAllCity() {
		List<City> cityList = getHibernateTemplate().find("from City");
		return cityList;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAllQuarter()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Quarter> getAllQuarter() {
		List<Quarter> quarterList = getHibernateTemplate().find("from Quarter");
		return quarterList;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAllNetWorkType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NetworkType> getAllNetWorkType() {
		List<NetworkType> networkTypes = getHibernateTemplate().find("from NetworkType where status=?" , Constants.ACTIVE_STATUS);
		return networkTypes;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getBranchList(com.eot.banking.dto.LocateUsDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> getBranchList(LocateUsDTO locateUsDTO) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Branch.class, "branch");
		criteria.add(Restrictions.eq("branch.country.countryId", getCountryIdFromNumbericCode(locateUsDTO.getCountryId()).getCountryId()));
		criteria.add(Restrictions.eq("branch.city.cityId", locateUsDTO.getCityId()));
		criteria.add(Restrictions.eq("branch.quarter.quarterId", Long.parseLong(locateUsDTO.getQuaterId().toString())));
		criteria.add(Restrictions.eq("branch.bank.bankId", getBankIdFromBankCode(locateUsDTO.getBankId()).getBankId()));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<Branch> branchs = getHibernateTemplate().findByCriteria(criteria);

		return CollectionUtils.isNotEmpty(branchs) ? branchs : null; 
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getConversionRateFromCurrecyIdsAndBankId(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CurrencyConverter getConversionRateFromCurrecyIdsAndBankId(Integer baseCurrencyId, Integer counterCurrencyId, Integer bankId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(CurrencyConverter.class, "cc");
		criteria.add(Restrictions.eq("cc.currencyByBaseCurrencyId.currencyId", baseCurrencyId));
		criteria.add(Restrictions.eq("cc.currencyByCounterCurrencyId.currencyId", counterCurrencyId));
		criteria.add(Restrictions.eq("cc.bank.bankId", bankId));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<CurrencyConverter> currencyConverters = getHibernateTemplate().findByCriteria(criteria);
		
		return CollectionUtils.isNotEmpty(currencyConverters) ? currencyConverters.get(0) : null; 
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAllRemittanceCompany()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RemittanceCompany> getAllRemittanceCompany() {
		return getHibernateTemplate().find("from RemittanceCompany");
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCustomerAccountFromAccountNumber(java.lang.String)
	 */
	@Override
	public CustomerAccount getCustomerAccountFromAccountNumber(String accountNumber) {
		return (CustomerAccount) getHibernateTemplate().findByNamedParam("from CustomerAccount acc where " +
				"acc.accountNumber=:accountNumber","accountNumber", accountNumber).get(0);
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getSCRules()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object []> getSCRules(Integer sourceType) {
		
		
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();

		Query query = session.createSQLQuery(
				"SELECT RuleLevel,sc.ImposedOn,sc.ApplicableFrom,sc.ApplicableTo,sca.Day,sca.FromHH,sca.ToHH,scv.ServiceChargeRuleID, "
				+" scv.serviceChargeRuleValuesId,scv.ServiceChargePct,scv.ServiceChargeFxd ,scv.DiscountLimit,scv.MinServiceCharge,  "
						+"scv.MaxServiceCharge ,scv.MinTxnValue,scv.MaxTxnValue,sc.TxnTimeZoneRule, "
			+	"  sct.TransactionType  "
				+"  FROM ServiceChargeRules sc "
			+"	JOIN ServiceChargeRuleTxn sct ON sc.ServiceChargeRuleID=sct.ServiceChargeRuleID " 
			+"	JOIN ServiceChargeRuleValues  scv ON sc.ServiceChargeRuleID=scv.ServiceChargeRuleID "
			+"	JOIN SCApplicableTime sca ON sc.ServiceChargeRuleID=sca.ServiceChargeRuleID  where sct.SourceType="+sourceType )
				.addScalar("RuleLevel",Hibernate.INTEGER)
				.addScalar("ServiceChargeRuleID",Hibernate.LONG)
				.addScalar("ServiceChargePct",Hibernate.FLOAT)
				.addScalar("ServiceChargeFxd",Hibernate.LONG)
				.addScalar("DiscountLimit",Hibernate.LONG)
				.addScalar("MinServiceCharge",Hibernate.LONG)
				.addScalar("MaxServiceCharge",Hibernate.LONG)
				.addScalar("MinTxnValue",Hibernate.LONG)
				.addScalar("MaxTxnValue",Hibernate.LONG)
				.addScalar("TxnTimeZoneRule", Hibernate.INTEGER)
				.addScalar("ImposedOn", Hibernate.INTEGER)
				.addScalar("applicableFrom",Hibernate.DATE)
				.addScalar("applicableTo",Hibernate.DATE)
				.addScalar("Day",Hibernate.INTEGER)
				.addScalar("FromHH",Hibernate.INTEGER)
				.addScalar("ToHH",Hibernate.INTEGER)
				.addScalar("TransactionType", Hibernate.INTEGER) ;
		
		List<Object[]> list = query.list();
		return CollectionUtils.isEmpty(list)? null :list;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAllLanguageData()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Language> getAllLanguageData() {
		List<Language> language = getHibernateTemplate().find("from Language");
		return CollectionUtils.isEmpty(language) ? null :language;
	}
	
	@Override
	public Long getStampFeeDebitTransaction(Integer bankId) {
		
		List<ServiceChargeSplit> stampFeeDeposit = getHibernateTemplate().find("from ServiceChargeSplit where bank.bankId="+bankId+" and transactionType.transactionType=" +EOTConstants.TXN_TYPE_DEPOSIT+" and amountType='STAMP_FEE'");
		
		
		return stampFeeDeposit.size()!=0?(Long)stampFeeDeposit.get(0).getServiceChargePct().longValue():0L;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getCountryFromCountryCode(java.lang.Integer)
	 */
	@Override
	public Country getCountryFromCountryCode(Integer countryCode) {
		return (Country) getHibernateTemplate().findByNamedParam("from Country as country where  " +
				"country.countryCodeNumeric=:countryCode","countryCode", countryCode).get(0);
	}
	
	@Override
	public City getCityFromCityId(Integer cityId) {
		return (City) getHibernateTemplate().findByNamedParam("from City as city where  " +
				"city.cityId=:cityId","cityId", cityId).get(0);
	}
	
	@Override
	public Quarter getQuarterFromQuarterId(Long quarterId) {
		return (Quarter) getHibernateTemplate().findByNamedParam("from Quarter as quarter where  " +
				"quarter.quarterId=:quarterId","quarterId", quarterId).get(0);
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getAccountFromCustomerId(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CustomerAccount getAccountFromCustomerId(Long customerId) {
		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam(
				"from CustomerAccount acc where acc.customer.customerId=:customerId" ,
				new String[]{"customerId"}, new Object[]{customerId}
				);

		return accountList.size() > 0 ? accountList.get(0) : null ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CustomerAccount getCommissionAccountFromAgentId(Long customerId) {
		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam(
				"from CustomerAccount acc where acc.customer.customerId=:customerId and acc.account.aliasType=:aliasType" ,
				new String[]{"customerId","aliasType"}, new Object[]{customerId,Constants.ALIAS_TYPE_COMMISSION_ACCOUNT}
				
				);

		return accountList.size() > 0 ? accountList.get(0) : null ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CustomerAccount getAgentPoolAccountFromAgentId(Long customerId) {
		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam(
				"from CustomerAccount acc where acc.customer.customerId=:customerId and acc.account.aliasType=:aliasType" ,
				new String[]{"customerId","aliasType"}, new Object[]{customerId,Constants.ALIAS_TYPE_WALLET_ACCOUNT}
				
				);

		return accountList.size() > 0 ? accountList.get(0) : null ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerAccount> getAccountListFromCustomerId(Long customerId) {
		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam(
				"from CustomerAccount acc where acc.customer.customerId=:customerId" ,
				new String[]{"customerId"}, new Object[]{customerId}
				);

		return accountList ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.EOTMobileDao#getExternalTxnsFromBeneficiaryMobileNumber(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalTransaction> getExternalTxnsFromBeneficiaryMobileNumber(String payeeMobileNumber) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ExternalTransaction.class, "ee");
		criteria.add(Restrictions.eq("ee.beneficiaryMobileNumber",payeeMobileNumber));
		criteria.add(Restrictions.ne("ee.status", Constants.TXN_STATUS_SUCESSFUL));

		List<ExternalTransaction> externalTransactions = getHibernateTemplate().findByCriteria(criteria);
		
		return externalTransactions; 
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExternalTransaction getExternalTxnsFromId(Long smsCashId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ExternalTransaction.class, "ee");
		criteria.add(Restrictions.eq("ee.externalTxnId", smsCashId));
		criteria.add(Restrictions.eq("ee.status", Constants.TXN_STATUS_SMS_CASH_INITIATED));

		List<ExternalTransaction> externalTransactions = getHibernateTemplate().findByCriteria(criteria);
		
		return CollectionUtils.isNotEmpty(externalTransactions) ? externalTransactions.get(0) : null; 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ExternalTransaction getExternalTransaction(String payeeMobileNumber) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ExternalTransaction.class, "ee");
		criteria.add(Restrictions.eq("ee.beneficiaryMobileNumber", payeeMobileNumber));
		criteria.add(Restrictions.eq("ee.status", Constants.REMITANCE_STATUS_INITIATED));
		criteria.addOrder(Order.desc("ee.externalTxnId"));

		List<ExternalTransaction> externalTransactions = getHibernateTemplate().findByCriteria(criteria);
		
		return CollectionUtils.isNotEmpty(externalTransactions) ? externalTransactions.get(0) : null; 
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<MobileMenuConfiguration> loadMobileMenus(MobileMenuMasterDataDTO mobileMenuDTO) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MobileMenuConfiguration.class);
		//criteria.setProjection(Projections.projectionList().add(Projections.rowCount()));
		if( mobileMenuDTO.getBankId()!= null ){
			criteria.add(Restrictions.eq("bankId", mobileMenuDTO.getBankId()));
		}else
		{
			criteria.add(Restrictions.eq("bankId", null));
		}
		if(mobileMenuDTO.getPofileId()!=null)
		{
			criteria.add(Restrictions.eq("profileId", mobileMenuDTO.getPofileId()));
		}
		
		if( mobileMenuDTO.getAppType()!=null)
		{
			criteria.createCriteria("mobilemenuapptype", "appType");
			criteria.add(Restrictions.eq("appType.appTypeId", mobileMenuDTO.getAppType()));
		}
		
		Disjunction status = Restrictions.disjunction();
		status.add(Restrictions.eq("status", Constants.DYNAMIC_MENU_ACTIVE));
		status.add(Restrictions.eq("status",0));
		
		criteria.add(status);
		
		//criteria.add(Restrictions.eq("mobileMasterMenu.mobileMenuLanguages.languageCode", mobileMenuDTO.getDefaultLocale()));
		List<MobileMenuConfiguration> list = criteria.list();
		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Mobiletheamcolorconfig loadMobileTheameColors(MobileMenuMasterDataDTO mobileMenuDTO) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Mobiletheamcolorconfig.class);
		//criteria.setProjection(Projections.projectionList().add(Projections.rowCount()));
		if( mobileMenuDTO.getBankId()!= null ){
			criteria.createCriteria("bank", "bnk");
			criteria.add(Restrictions.eq("bnk.bankId", mobileMenuDTO.getBankId()));
		}else
		{
			criteria.createCriteria("bank", "bnk");
			criteria.add(Restrictions.eq("bnk.bankId", null));
		}
		criteria.add(Restrictions.eq("appTypeId", mobileMenuDTO.getAppType()+""));
		List<Mobiletheamcolorconfig> list = criteria.list();
		return list.size()>0?list.get(0):null;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MobileMenuLanguage> loadMenuLanguagesByFCNCode(List<MobileMenuConfiguration> mobileMenuConf) {
		
		/*String functionalCode="";
		
		for(int i=0;i<mobileMenuConf.size();i++)
		{
			functionalCode=functionalCode+mobileMenuConf.get(i).getMobileMasterMenu().getFunctionalCode();
			if(mobileMenuConf.size()-i>1)
			{
				functionalCode=functionalCode+",";
			}
		}
		List<MobileMenuLanguage> list = getHibernateTemplate().find("from MobileMenuLanguage ml where ml.locale =? and" +
				" lt.status =?", locale,status);*/
		
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Biller> getBillerList() {

		return getHibernateTemplate().find("from Biller");
	}


	@Override
	public List<Country> getCountries() {
		return getHibernateTemplate().find("from Country");
	}


	@Override
	public List<KycType> getKycType() {
		return getHibernateTemplate().find("from KycType");
	}
	
	@Override
	public List<KycType> getKycTypeForCustomerType(int customerType) {
			return getHibernateTemplate().find("from KycType where type=0 and status=1");
	}

	@Override
	public BusinessPartnerUser getUser(String name) {
		/*Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query query = session.createQuery("from BusinessPartnerUser as user where user.userName=:userName")
				.setParameter("userName", name);
		List<BusinessPartnerUser> list = query.list();
		return list.size()>0 ? list.get(0) : null ;*/
		return getHibernateTemplate().get(BusinessPartnerUser.class,name);
	}


	@Override
	public Customer getCustomerByAppId(String appId) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer cs where appId=:appId")
				.setParameter("appId",appId);
		List<Customer> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}
	
	@Override
	public Customer getCustomerByMobile(String mobile) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer cs where mobileNumber=:mobile")
				.setParameter("mobile",mobile);
		List<Customer> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}
	
	
	// vineeth on, 18-10-2018
	@Override
	public ArrayList<PendingTransactionDTO.TransactionDTO> getPendingTransactions(TransactionBaseDTO transactionBaseDTO,String accountNumber) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from PendingTransaction transaction where transaction.status=:status and transaction.customerAccount =:accountNumber")
				.setParameter("status",Constants.TRANSACTION_INITIATED_FOR_APPROVAL)
				.setParameter("accountNumber",accountNumber);
		
		 List<PendingTransaction> pendingTransactionsList =query.list();
		 ArrayList<PendingTransactionDTO.TransactionDTO> pendingTransactionsLists = new ArrayList<PendingTransactionDTO.TransactionDTO>();
		 if(pendingTransactionsList.size()<0) {
			// transactionBaseDTO.setMessageDescription("No pending Transactions");
		 }
		 else {
			 for(PendingTransaction tx: pendingTransactionsList) {
				 PendingTransactionDTO.TransactionDTO transactionDto = new PendingTransactionDTO(). new TransactionDTO();
				 //transactionDto.setAmount(tx.getAmount());
				 //transactionDto.setElectricityBills(tx.getElectricityBills());
				 transactionDto.setCustomerAccount(tx.getCustomerAccount());
				 transactionDto.setCustomerAccountType(tx.getCustomerAccountType());
				 //transactionDto.setMobileRequests(tx.getMobileRequests());
				// transactionDto.setReferenceId(tx.getReferenceId());
				 //transactionDto.setOperatorVouchers(tx.getOperatorVouchers());
				
				 transactionDto.setReferenceType(tx.getReferenceType());
				 transactionDto.setCustomerAccount(tx.getCustomerAccount());
				 transactionDto.setOtherAccount(tx.getOtherAccount());
				 //transactionDto.setStatus(tx.getStatus());
				 transactionDto.setTransactionDate(tx.getTransactionDate());
				 transactionDto.setTransactionId(tx.getTransactionId());
				 transactionDto.setAmount(tx.getAmount());
				 transactionDto.setTransactionStatus(tx.getStatus());
				 transactionDto.setTxnType(tx.getTransactionType().getTransactionType());
				 transactionDto.setApprovedBy(tx.getApprovedBy());
				 transactionDto.setTransactionId(tx.getTransactionId());
				 transactionDto.setInitiatedBy(tx.getInitiatedBy());
				Customer customer =  getInitiatedByUserName(Long.parseLong(tx.getInitiatedBy()));
				if(customer != null) {
					 transactionDto.setName(customer.getFirstName()+customer.getLastName());
					 transactionDto.setPhone(customer.getMobileNumber());
				}		
				//transactionDto.customer(tx.getTransactionJournals());
				//transactionDto.setTxnType(tx.getTransactionType());
				//transactionDto.setWebRequests(tx.getWebRequests());
				//transactionDto.setOtherAccount(tx.getOtherAccount());
				//transactionDto.setOtherAccountType(tx.getOtherAccountType());
				 pendingTransactionsLists.add(transactionDto);
			 }
			 //transactionBaseDTO.setPendingTransactionsList(pendingTransactionsLists);
			// transactionBaseDTO.setMessageDescription("List of Pending Transactions");
		 }
	
		return pendingTransactionsLists;
	}
		
	@Override
	public List<PendingTransaction> loadPendingTransactions(PendingTransactionDTO transaction) {
		
		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL);
		
		TransactionType transactionTypeSale = new TransactionType();
		transactionTypeSale.setTransactionType(Constants.TXN_ID_MERCHANT_SALE);
		
		Query query=getSessionFactory().getCurrentSession().createQuery("from  PendingTransaction pt where pt.customerAccount=? and customer=? and (transactionType=? OR transactionType=?) and status=?" );
		query.setParameter(0, transaction.getAccountNumber());
		query.setParameter(1, transaction.getCustomer());
		query.setParameter(2,transactionType);
		query.setParameter(3,transactionTypeSale);
		
		query.setParameter(4, Constants.TRANSACTION_INITIATED_FOR_APPROVAL);
		
		return query.list();
	}
	
	@Override
	public PendingTransaction loadPendingTransaction(PendingTransactionDTO transaction) {
		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(Constants.TXN_ID_MERCHANT_WITHDRAWAL);
		Query query=getSessionFactory().getCurrentSession().createQuery("from  PendingTransaction pt where pt.customerAccount=?  and status=? and transactionId=?" );
		query.setParameter(0, transaction.getAccountNumber());
		//query.setParameter(1, transaction.getCustomer().getCustomerId());
		//query.setParameter(1,transactionType);
		query.setParameter(1, Constants.TRANSACTION_INITIATED_FOR_APPROVAL);
		query.setParameter(2, transaction.getPendingTxnRecordId());
		//query.setParameter(3, transaction.getListOfPendingTxn().get(0).getTransactionId());
		
		return query.list().size()>0?(PendingTransaction)query.list().get(0):null;
	}
	
	@Override
	public PendingTransaction loadSaleTransaction(PendingTransactionDTO transaction) {
		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(Constants.TXN_ID_MERCHANT_SALE);
		Query query=getSessionFactory().getCurrentSession().createQuery("from  PendingTransaction pt where pt.customerAccount=?  and transactionType=? and status=? and transactionId=?" );
		query.setParameter(0, transaction.getAccountNumber());
		//query.setParameter(1, transaction.getCustomer().getCustomerId());
		query.setParameter(1,transactionType);
		query.setParameter(2, Constants.TRANSACTION_INITIATED_FOR_APPROVAL);
		query.setParameter(3, transaction.getPendingTxnRecordId());
	//	query.setParameter(3, transaction.getListOfPendingTxn().get(0).getTransactionId());
		
		return query.list().size()>0?(PendingTransaction)query.list().get(0):null;
	}
	@Override
	public PendingTransaction loadPendingTxn(Customer customer, int transactionTypeID) {
		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(transactionTypeID);
		Query query=getSessionFactory().getCurrentSession().createQuery("from  PendingTransaction pt where pt.customer=?  and transactionType=? and status=? " );
		query.setParameter(0, customer);
		query.setParameter(1,transactionType);
		query.setParameter(2, Constants.TRANSACTION_INITIATED_FOR_APPROVAL);
		
		return query.list().size()>0?(PendingTransaction)query.list().get(0):null;
	}
	
	@Override
	public PendingTransaction loadPendingRecord(String parameter,int transactionTypeID) {
		TransactionType transactionType = new TransactionType();
		transactionType.setTransactionType(transactionTypeID);
		Query query=getSessionFactory().getCurrentSession().createQuery("from  PendingTransaction pt where (pt.customerMobileNo=? or pt.customerCode=?  )  and transactionType=? and status=? " );
		query.setParameter(0, parameter);
		query.setParameter(1, parameter);
		query.setParameter(2,transactionType);
		query.setParameter(3, Constants.TRANSACTION_INITIATED_FOR_APPROVAL);
		
		return query.list().size()>0?(PendingTransaction)query.list().get(0):null;
	}
	
	@Override
	public Customer getCustomerFromCustomerId(Long customerId) {

		/*List<Customer> custList = getHibernateTemplate().findByNamedParam("from Customer cust where cust.mobileNumber=:mobileNumber","mobileNumber", mobileNumber);

		return (Customer)(custList.size() > 0 ? custList.get(0) : null) ;*/
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer cs where customerId=:customerId")
				.setParameter("customerId",customerId);
		List<Customer>list=query.list();
		return list.size() > 0 ? list.get(0) : null ;

	}

// vineeth
	@Override
	public CustomerAccount getCustomerAccountWithCustomerId(Long customerId) {
		Query query=getSessionFactory().getCurrentSession().createQuery("from CustomerAccount customerAccount where customerAccount.customer.customerId =:customerId")
				.setParameter("customerId",customerId);
		List<CustomerAccount> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}
	
	public Customer getInitiatedByUserName(Long customerId){
		
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer customer where customer.customerId =:customerId")
				.setParameter("customerId",customerId);
		List<Customer> list=query.list();
		return list.size()>0?list.get(0):null;
				
	}
	
	@Override
	public List<SecurityQuestion> getQuestions(String locale) {
		Query query = getSessionFactory().getCurrentSession().createQuery("from SecurityQuestion where locale=:locale").setCacheable(true);
		query.setParameter("locale",locale);
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getRecentRecipeints(Long customerId) {

		/*List<Customer> custList = getHibernateTemplate().findByNamedParam("from Customer cust where cust.mobileNumber=:mobileNumber","mobileNumber", mobileNumber);

		return (Customer)(custList.size() > 0 ? custList.get(0) : null) ;*/
		
		/*Query query=getSessionFactory().getCurrentSession().createSQLQuery("SELECT * FROM Customer cust INNER JOIN CustomerAccounts custAcc ON cust.customerId=custAcc.customerId WHERE custAcc.AccountNumber IN(SELECT DISTINCT(otherAccount) FROM Transactions WHERE referenceId =:customerId AND transactionType=55 ORDER BY TransactionId DESC ) LIMIT 10")
				.setParameter("customerId",customerId+"");
		List<Object[]> list=query.list();*/
		
		
		
		Query query=getSessionFactory().getCurrentSession().createSQLQuery("SELECT * FROM Customer cust INNER JOIN CustomerAccounts custAcc ON cust.customerId=custAcc.customerId WHERE custAcc.AccountNumber IN(SELECT DISTINCT(otherAccount) FROM Transactions WHERE referenceId =:customerId AND transactionType=:txn1 ORDER BY TransactionId DESC ) LIMIT :limit")
				.setParameter("customerId",customerId+"")
				.setParameter("txn1", 55)
				.setParameter("limit", 10);
		List<Object[]> list=query.list();
		
		
		
		return list;

	}
	
	@Override
	@SuppressWarnings("unchecked")
	public CustomerAccount getPayeeAccountFromAgentCode(String agentCode) {

		//		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam("from CustomerAccount acc where acc.customer.mobileNumber=:mobileNumber","mobileNumber", mobileNo);

		List<CustomerAccount> accountList = getHibernateTemplate().findByNamedParam("from CustomerAccount acc where customer.agentCode=:agentCode","agentCode", agentCode);

		return accountList.size() > 0 ? accountList.get(0) : null ;

	}
	@Override
	public Customer getAgentByAgentCode(String agentCode) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer cs where agentCode=:agentCode")
				.setParameter("agentCode",agentCode);
		List<Customer> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}


	@Override
	public String getAgentCode(Integer type) {
		Query query = getSessionFactory().getCurrentSession()
				.createQuery("SELECT MAX(AgentCode)+1 AS seq FROM Customer where type=:type")
				.setParameter("type",type);
				List list = query.list();
				return list.get(0) == null ? "1" :list.get(0).toString();
	}
	
	@Override
	public Otp getOtp(String referenceId) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from Otp otp where otp.comp_id.referenceId=:referenceId and otp.otpType=3")
				.setParameter("referenceId",referenceId);
		List<Otp> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}

	@Override
	public WebUser getWebUser(String name) {		
		return getHibernateTemplate().get(WebUser.class,name);
	}


	@Override
	public List<ReversalTransactionDTO> loadTransactionReversalForApprove(
			ReversalTransactionDTO reversalTransactionDTO) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		StringBuffer qryStr = new StringBuffer("SELECT DISTINCT(transactions.TransactionID),customer.FirstName,customer.LastName,customer.MobileNumber,transactions.TransactionType,transactions.TransactionDate,transactions.amount FROM Transactions AS transactions " +
				"INNER JOIN Customer AS customer ON transactions.ReferenceId=customer.CustomerID JOIN CustomerAccounts AS custacc ON customer.CustomerID=custacc.CustomerID JOIN Bank AS bank ON custacc.BankID=bank.BankID" +
				"	JOIN Branch AS branch ON custacc.BranchID=branch.BranchID JOIN Country AS country ON bank.CountryID=country.CountryID JOIN CustomerProfiles AS custprof ON bank.BankID=custprof.BankID JOIN SettlementJournals AS sj ON sj.TransactionID=transactions.TransactionID " +
				"AND (transactions.TransactionType=:txnidDepo OR transactions.TransactionType=:txnidwith OR transactions.TransactionType=:txnidreve) AND transactions.Status!=:status");

		qryStr.append(" and transactions.Status=:notstatus and customer.appId =:appId and customer.ProfileID=custprof.ProfileID AND sj.BookID=:bookID AND sj.BatchID IS NULL ORDER BY transactions.TransactionDate DESC");
	
		SQLQuery qryResult = session.createSQLQuery(qryStr.toString());
		qryResult.setParameter("txnidDepo", EOTConstants.TXN_TYPE_PAY);
		qryResult.setParameter("txnidwith", EOTConstants.TXN_TYPE_SALE);
		qryResult.setParameter("txnidreve", EOTConstants.TXN_TYPE_TRFDIRECT);
		qryResult.setParameter("status", EOTConstants.TXN_NO_ERROR);
		
		qryResult.setParameter("appId", reversalTransactionDTO.getAppId());
		qryResult.setParameter("notstatus",EOTConstants.TXN_INITIATE_ADJUSTMENT);
		qryResult.setParameter("bookID", 2);
		qryResult.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return qryResult.list();
	}


	@Override
	public Transaction getTransactionByTxnId(Integer transactionId) {
		Query query=getSessionFactory().getCurrentSession().createQuery("from Transaction where transactionId=:transactionId")
				.setParameter("transactionId",transactionId.longValue());
		List<com.eot.entity.Transaction>list=query.list();
		return CollectionUtils.isNotEmpty(list) ? list.get(0) : null ;
	}


	@Override
	public Account getAccount(String accountNumber) {
		Query query=getSessionFactory().getCurrentSession().createQuery("from Account as acc where acc.accountNumber=:accountNumber");
		query.setParameter("accountNumber",accountNumber);
		List<Account> list= query.list();	
		return CollectionUtils.isNotEmpty(list) ? list.get(0) :null;
	}


	@Override
	public ArrayList<HelpDesk> getAllHelpDeskList() {
		Query query=getSessionFactory().getCurrentSession().createQuery("from HelpDesk");
		ArrayList<HelpDesk> list= (ArrayList<HelpDesk>) query.list();	
		return CollectionUtils.isNotEmpty(list) ? list :null;
	}
	
	@Override
	public BusinessPartnerUser getBusinessPartnerUser(String userName) {
		
		return getHibernateTemplate().get(BusinessPartnerUser.class,userName);		
	}
	
	@Override
	public BusinessPartner getBusinessPartnerByPartnerType(int partnerType) {
		
		
		Query query=getSessionFactory().getCurrentSession().createQuery("from BusinessPartner where partnerType=:partnerType")
				.setParameter("partnerType",partnerType);
		List<BusinessPartner> list=query.list();
		return CollectionUtils.isNotEmpty(list) ? list.get(0) : null ;
	}


	@Override
	public WebUser getUserByMobileNumber(String mobileNumber) {
		
		Query query=getSessionFactory().getCurrentSession().createQuery("from WebUser where mobileNumber=:mobileNumber")
				.setParameter("mobileNumber",mobileNumber);
		List<WebUser> list=query.list();
		return CollectionUtils.isNotEmpty(list) ? list.get(0) : null ;
	}


	@Override
	public List<Object[]> getTransactions(Integer bankId, ReportsModel baseDTO) {
		/*Two Query is divided*/
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		/*StringBuffer qryStr = new StringBuffer("SELECT customer.FirstName,customer.MobileNumber,customer.AgentCode,customer.partnerId, transactions.amount,transactions.TransactionType,transactions.TransactionDate,transactions.status,transactions.requestChannel,bank.BankName,branch.Location,country.CountryID," +
				"customer.ProfileID,customer.ProfileID,customer.LastName,(SELECT amount FROM TransactionJournals WHERE TransactionID=transactions.TransactionID" +
				" AND JournalType=:journalType) AS SC,(SELECT SUM(sj.amount) FROM SettlementJournals sj WHERE sj.TransactionID=transactions.TransactionID AND sj.BookID=:bookID2) AS bankShare," +
				"(SELECT SUM(sj.amount) FROM SettlementJournals sj WHERE sj.TransactionID=transactions.TransactionID AND sj.BookID=:bookID3) AS gimShare FROM" +
				" Transactions AS transactions INNER JOIN Customer AS customer ON transactions.ReferenceId=customer.CustomerID JOIN CustomerAccounts AS custacc" +
				" ON customer.CustomerID=custacc.CustomerID JOIN Bank AS bank ON custacc.BankID=bank.BankID JOIN Branch AS branch ON custacc.BranchID=branch.BranchID" +
				" JOIN Country AS country ON bank.CountryID=country.CountryID  JOIN CustomerProfiles AS custprof ON bank.BankID=custprof.BankID ");*/
		
		StringBuffer qryStr = new StringBuffer("SELECT customer.FirstName,customer.MobileNumber,customer.AgentCode,customer.partnerId, transactions.amount,transactions.TransactionType,transactions.TransactionDate,transactions.status,transactions.requestChannel,bank.BankName,branch.Location,country.CountryID," +
				"customer.ProfileID,customer.ProfileID,customer.LastName,(SELECT amount FROM TransactionJournals WHERE TransactionID=transactions.TransactionID" +
				" AND JournalType=:journalType) AS SC,(SELECT SUM(sj.amount) FROM SettlementJournals sj WHERE sj.TransactionID=transactions.TransactionID AND sj.BookID=:bookID2) AS bankShare," +
				"(SELECT SUM(sj.amount) FROM SettlementJournals sj WHERE sj.TransactionID=transactions.TransactionID AND sj.BookID=:bookID3) AS gimShare FROM" +
				" Transactions AS transactions INNER JOIN Customer AS customer ON transactions.ReferenceId=customer.CustomerID JOIN CustomerAccounts AS custacc" +
				" ON customer.CustomerID=custacc.CustomerID  JOIN Account acc ON acc.accountNumber = custacc.accountNumber AND acc.aliasType=1 "
				+ "JOIN Bank AS bank ON custacc.BankID=bank.BankID JOIN Branch AS branch ON custacc.BranchID=branch.BranchID" +
				" JOIN Country AS country ON bank.CountryID=country.CountryID");
		int pageIndex = 0;
		int totalNumberOfRecords = 0;
		int numberOfRecordsPerPage = 10;
		
		int sPageIndex = baseDTO.getPageNo();
		
		if(sPageIndex ==0)
		{
		pageIndex = 1;
		}else
		{
		pageIndex =  baseDTO.getPageNo();
		}
		
		int s = (pageIndex*numberOfRecordsPerPage) -numberOfRecordsPerPage;
		Date fromDate = new Date();
		fromDate.setTime(baseDTO.getFromDate());
		
		Date toDate = new Date();
		toDate.setTime(baseDTO.getToDate());
		
		if(baseDTO.getMobileNumber() != null && !"".equals(baseDTO.getMobileNumber())){	

			qryStr.append(" and customer.MobileNumber=:mobileNumber");
		}	
		
			
	/*	if(txnSummaryDTO.getCountryId() != null && !"".equals(txnSummaryDTO.getCountryId())){

			qryStr.append(" and country.CountryID=:countryID");
		}*/
		if(bankId != null && !"".equals(bankId)){

			qryStr.append(" and bank.BankID=:bankID");   
		}
		/*if(txnSummaryDTO.getBranchId() != null && !"".equals(txnSummaryDTO.getBranchId())){

			qryStr.append(" and branch.BranchID=:branchID");  
		} 
		if(txnSummaryDTO.getBankGroupId() != null && !"".equals(txnSummaryDTO.getBankGroupId())){
			qryStr.append(" JOIN BankGroups as bankgroups ON bank.BankGroupID=bankgroups.BankGroupID");
			qryStr.append(" and bankgroups.BankGroupID=:bankGroupID");  
		} 
		if(txnSummaryDTO.getUserId() != null && !"".equals(txnSummaryDTO.getUserId())){

			qryStr.append(" JOIN WebRequests as webrequests ON transactions.TransactionID=webrequests.TransactionID and webrequests.UserName like:userID");  
		}
		if(txnSummaryDTO.getProfileId() != null && !"".equals(txnSummaryDTO.getProfileId())){

			qryStr.append(" and customer.ProfileID =:ProfileID");  
		} 
		if(bankGroupId != null){
			qryStr.append(" JOIN BankGroups as bankgroups ON bank.BankGroupID=bankgroups.BankGroupID");
			qryStr.append(" and bankgroups.BankGroupID=:bankGroupID"); 
		} */
		if(bankId != null){

			qryStr.append(" and bank.BankID=:bankID");   
		} 
		/*if(branchId != null){

			qryStr.append(" and branch.BranchID=:branchID");   
		} */

		/*Author name <vinod joshi>, Date<6/22/2018>, purpose of change <Date format is not working > ,*/
		/*Start*/
		if(baseDTO!=null){
			if(baseDTO.getTransactionType()==null){
				qryStr.append(" and transactions.TransactionType !=:Txn1 and transactions.TransactionType !=:Txn2 and transactions.TransactionType !=:Txn3");
			}
		}
		/*End*/
		
		/*if(txnSummaryDTO!=null){
			if(txnSummaryDTO.getTransactionType()==null){
				qry.append(" and transactions.TransactionType != 31 ");
			}
		}*/

		/*if(txnSummaryDTO!=null){
			if(txnSummaryDTO.getTransactionType()!=null){
				qryStr.append(" and transactions.TransactionType=:Txn");
			}
		}*/

		if(baseDTO!=null){
			if(baseDTO.getFromDate()!=null){
				qryStr.append(" where DATE(transactions.TransactionDate)>=:fromDate and DATE(transactions.TransactionDate)<=:toDate ");
			}
		}
		
	
		if(baseDTO!=null){
			if(baseDTO.getFromDate()!=null){
		qryStr.append("AND transactions.TransactionType IN(30,35,55,82,80,83,128,90,126,115,116,133,140,120)");
			}
			
		}
		

		/*if(StringUtils.isNotEmpty(txnSummaryDTO.getPartnerType()) && StringUtils.isNotEmpty(txnSummaryDTO.getPartnerId())){	
			
			qryStr.append(" and customer.partnerId=:partnerID");
		}*/	
		
		/*if(txnSummaryDTO.getAgentCode() != null && !"".equals(txnSummaryDTO.getAgentCode())){

			qryStr.append(" and customer.AgentCode=:agentCode");   
		}*/
		
		//qryStr.append(" and customer.ProfileID=custprof.ProfileID"); 
		
		/*if(txnSummaryDTO.getStatus() != null && txnSummaryDTO.getStatus().equals("2"))
			qryStr.append(" and transactions.Status!=:status");   
		else 
			qryStr.append(" and transactions.Status=:status"); 	*/
		
	
		qryStr.append("  ORDER BY TransactionDate DESC");

		SQLQuery qryResult1 = session.createSQLQuery(qryStr.toString()); 
		    qryResult1.setParameter("journalType", 1);
		    qryResult1.setParameter("bookID2", 2);
		    qryResult1.setParameter("bookID3", 3);
		if(baseDTO.getMobileNumber() != null && !"".equals(baseDTO.getMobileNumber())){{
		    qryResult1.setParameter("mobileNumber", baseDTO.getMobileNumber());
		   }
		}
		/*if(txnSummaryDTO.getCountryId() != null && !"".equals(txnSummaryDTO.getCountryId())){
		    qryResult1.setParameter("countryID", txnSummaryDTO.getCountryId());	
		}*/
		if(bankId != null && !"".equals(bankId)){
			qryResult1.setParameter("bankID", bankId);
		}
		/*if(txnSummaryDTO.getBranchId() != null && !"".equals(txnSummaryDTO.getBranchId())){
			qryResult1.setParameter("branchID", txnSummaryDTO.getBranchId());
		}*/

		if(baseDTO!=null){ 				
			if(baseDTO.getFromDate()!=null){
				qryResult1.setParameter("fromDate", DateUtil.formatDate(fromDate));
			}
			if(baseDTO.getToDate()!=null){
				qryResult1.setParameter("toDate", DateUtil.formatDate(toDate));
			}
		}
		/*if(txnSummaryDTO.getBankGroupId() != null && !"".equals(txnSummaryDTO.getBankGroupId())){
			qryResult1.setParameter("bankGroupID", txnSummaryDTO.getBankGroupId());
		}
		if(txnSummaryDTO.getUserId() != null && !"".equals(txnSummaryDTO.getUserId())){
			qryResult1.setParameter("userID",txnSummaryDTO.getUserId()+"%");
			}
		if(txnSummaryDTO.getProfileId() != null && !"".equals(txnSummaryDTO.getProfileId())){
				qryResult1.setParameter("ProfileID",txnSummaryDTO.getProfileId());
			}
		if(bankGroupId != null){
				qryResult1.setParameter("bankGroupID", bankGroupId);
		}*/
		if(bankId != null){
			qryResult1.setParameter("bankID", bankId);
		}
		/*if(branchId != null){
			qryResult1.setParameter("branchID", bankId);
		}*/
		if(baseDTO!=null){
			if(baseDTO.getTransactionType()==null){
			qryResult1.setParameter("Txn1", 60);
			qryResult1.setParameter("Txn2", 84);
			qryResult1.setParameter("Txn3", 31);
		}
		}
		/*if(txnSummaryDTO!=null){
			if(txnSummaryDTO.getTransactionType()!=null){
				qryResult1.setParameter("Txn", txnSummaryDTO.getTransactionType());
		}
		}*/
	/*	if(StringUtils.isNotEmpty(txnSummaryDTO.getPartnerType()) && StringUtils.isNotEmpty(txnSummaryDTO.getPartnerId())){
			qryResult1.setParameter("partnerID", txnSummaryDTO.getPartnerId());
		}
		if(txnSummaryDTO.getAgentCode() != null && !"".equals(txnSummaryDTO.getAgentCode())){
			qryResult1.setParameter("agentCode", txnSummaryDTO.getAgentCode());
		}*/
		//qryResult1.setParameter("status", EOTConstants.TXN_NO_ERROR);
				
		/*if (!EOTConstants.ACTION_EXPORT.equals(txnSummaryDTO.getActionType())) {
			qryResult1.setFirstResult((pageNumber-1)*appConfig.getResultsPerPage());
			qryResult1.setMaxResults(appConfig.getResultsPerPage());
		}*/
		
		qryResult1.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	/*	return PaginationHelper.getPage(qryResult1.list(), totalCount, appConfig.getResultsPerPage(), pageNumber);*/
		qryResult1.setFirstResult(s);
		qryResult1.setMaxResults(10);
		List<Object[]>list=qryResult1.list();
		return list;
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CustomerDocument loadCustomerDocumentByCustomerId(Long customerId) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from CustomerDocument cd where cd.customerId=:customerId")
				.setParameter("customerId",customerId);
		List<CustomerDocument> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}


	@Override
	public CurrentBalance getCount(CurrentBalance currentBalance,String onBoardedBy) {

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Customer.class);
		criteria.setProjection(Projections.projectionList().add(Projections.rowCount()));
		criteria.add(Restrictions.eq("onbordedBy",onBoardedBy));
		int totalCount = Integer.parseInt(criteria.list().get(0).toString());
		
		Criteria criteria1 = session.createCriteria(Customer.class);
		criteria1.setProjection(Projections.projectionList().add(Projections.rowCount()));
		criteria1.add(Restrictions.eq("onbordedBy",onBoardedBy));
		criteria1.add(Restrictions.sqlRestriction("createdDate like '%"+DateUtil.formatDate(new Date())+"%'"));
	
		int todayCount = Integer.parseInt(criteria1.list().isEmpty()?"0":criteria1.list().get(0).toString());	
		
		Criteria criteria2 = session.createCriteria(Customer.class);
		criteria2.setProjection(Projections.projectionList().add(Projections.rowCount()));
		criteria2.add(Restrictions.eq("onbordedBy",onBoardedBy));
		criteria2.add(Restrictions.eq("kycStatus",KycStatusEnum.KYC_PENDING.getCode()));
		
		int pendingCount = Integer.parseInt(criteria2.list().isEmpty()?"0":criteria1.list().get(0).toString());
		
		currentBalance.setTotalCount(totalCount);
		currentBalance.setTodayCount(todayCount);
		currentBalance.setPendingCount(pendingCount);
		
		return currentBalance;
	}


	@SuppressWarnings("deprecation")
	@Override
	public List<Customer> getCustomerReports(Integer type, CustomerModel customerModel,String onBoardedBy) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Criteria criteria1 = null;
		criteria1 = session.createCriteria(Customer.class);
		criteria1.setProjection(Projections.projectionList().add(Projections.rowCount()));
		criteria1.addOrder(Order.desc("createdDate"));
		Date fromDate=null,toDate=null;
		TimeZone timeZone = null;
		String fromDateNew = null,toDateNew=null;
		int pageIndex = 0;
		int totalNumberOfRecords = 0;
		int numberOfRecordsPerPage = 10;
		
		int sPageIndex = customerModel.getPageNo();
		
		if(sPageIndex ==0)
		{
		pageIndex = 1;
		}else
		{
		pageIndex =  customerModel.getPageNo();
		}
		
		int s = (pageIndex*numberOfRecordsPerPage) -numberOfRecordsPerPage;
		if(customerModel.getFromDate()!=0 && customerModel.getToDate()!=0 && customerModel.getFromDate()!= null && customerModel.getToDate()!= null && !"".equals(customerModel.getFromDate()) && !"".equals(customerModel.getToDate())){ 
			
			fromDate = new Date();
			fromDate.setTime(customerModel.getFromDate());
			fromDateNew =  DateUtil.formatDateToStr(fromDate);
			int i = fromDate.getDay();
			fromDateNew = DateUtil.formatDateWithSlash(fromDateNew, 00, 00, 00);
			
			toDate = new Date();
			toDate.setTime(customerModel.getToDate());
			toDateNew =  DateUtil.formatDateToStr(toDate);
			toDateNew = DateUtil.formatDateWithSlash(toDateNew, 23, 59, 59);
			
		}
		if(null != onBoardedBy  && !"".equals(onBoardedBy)) {
			criteria1.add(Restrictions.eq("onbordedBy",onBoardedBy));
		}
		
		if(null != type  && !"".equals(type.toString())) {
			criteria1.add(Restrictions.eq("type",type));
		}
		
		if( customerModel.getMobileNumber()!= null && ! "".equals(customerModel.getMobileNumber()) ){
			criteria1.add(Restrictions.eq("mobileNumber",customerModel.getMobileNumber()));
		}
		
		
		 if((customerModel.getFromDate()!=null && ! "".equals( customerModel.getFromDate().toString()) && customerModel.getFromDate()!=0) && (customerModel.getToDate()!=null && ! "".equals( customerModel.getToDate().toString())&& customerModel.getToDate()!=0)){
	           /* criteria.add(Restrictions.sqlRestriction("DATE_FORMAT(CreatedDate,\"%d/%m/%Y\")  between '"+fromDate.toUpperCase()+"' " +"and '"+toDate.toUpperCase()+"'"));*/
	    	   criteria1.add(Restrictions.between("createdDate", DateUtil.mySQLdateAndTime(fromDateNew),DateUtil.mySQLdateAndTime(toDateNew)));
	       
	   		System.out.println(DateUtil.mySQLdateAndTime(fromDateNew) +"-----"+DateUtil.mySQLdateAndTime(toDateNew));
  }
	      

		int totalCount = Integer.parseInt(criteria1.list().get(0).toString());

		criteria1 = getSessionFactory().getCurrentSession().createCriteria(Customer.class);
		criteria1.addOrder(Order.desc("createdDate"));
		/*if( customerModel.getMobileNumber()!= null && ! "".equals( customerModel.getMobileNumber()) ){
			criteria1.add(Restrictions.like("mobileNumber", "%"+ customerModel.getMobileNumber()+"%"));
		}*/
		
		if(null != onBoardedBy  && !"".equals(onBoardedBy)) {
			criteria1.add(Restrictions.eq("onbordedBy",onBoardedBy));
		}
		
		if(null != type  && !"".equals(type.toString())) {
			criteria1.add(Restrictions.eq("type",type));
		}
		
		if( customerModel.getMobileNumber()!= null && ! "".equals(customerModel.getMobileNumber()) ){
			criteria1.add(Restrictions.eq("mobileNumber",customerModel.getMobileNumber()));
		}
		
		 if((customerModel.getFromDate()!=null && ! "".equals( customerModel.getFromDate().toString()) && customerModel.getFromDate()!=0) && (customerModel.getToDate()!=null && ! "".equals( customerModel.getToDate().toString())&& customerModel.getToDate()!=0)){
	           /* criteria.add(Restrictions.sqlRestriction("DATE_FORMAT(CreatedDate,\"%d/%m/%Y\")  between '"+fromDate.toUpperCase()+"' " +"and '"+toDate.toUpperCase()+"'"));*/
	    	   criteria1.add(Restrictions.between("createdDate", DateUtil.mySQLdateAndTime(fromDateNew),DateUtil.mySQLdateAndTime(toDateNew)));
	       
	    	   System.out.println("===========================================================================");
	   		System.out.println(DateUtil.mySQLdateAndTime(fromDateNew) +"-----"+DateUtil.mySQLdateAndTime(toDateNew));
  }
	
			criteria1.setFirstResult(s);
			criteria1.setMaxResults(10);
   			
   			return  criteria1.list();
	}


	@Override
	public ArrayList<FAQ> getFAQsList() {
		Query query=getSessionFactory().getCurrentSession().createQuery("from FAQ");
		ArrayList<FAQ> list= (ArrayList<FAQ>) query.list();	
		return CollectionUtils.isNotEmpty(list) ? list :null;
	}


	@Override
	public List<Object[]> getTransactionsReports(String accountNumber,Integer bankId, ReportsModel baseDTO,Integer type) {
		Session session = getHibernateTemplate(). getSessionFactory().getCurrentSession();
		
		Date fromDate = new Date();
		fromDate.setTime(baseDTO.getFromDate());
		Date toDate = new Date();
		toDate.setTime(baseDTO.getToDate()+86340000);
		
		int pageIndex = 0,totalNumberOfRecords = 0,numberOfRecordsPerPage = 10;
		
		int sPageIndex = baseDTO.getPageNo();
		if(sPageIndex ==0){pageIndex = 1;}else
		{pageIndex =  baseDTO.getPageNo();}
		int s = (pageIndex*numberOfRecordsPerPage) -numberOfRecordsPerPage;
		
		Query query = session.createSQLQuery(
				"select myresult.*, txn.TransactionDate from (  " + 
				"select TransactionID, Amount as Amount, 'C' as Type,JournalType,CreditDesc as Descript,FromAccountBalance,ToAccountBalance " + 
				"from TransactionJournals where CreditAccount=:accountNumber and JournalType= 0 and BookID=1  " + 
				"UNION ALL     " + 
				"select TransactionID, Amount as Amount, 'D' AS Type, JournalType,DebitDesc as Descript,FromAccountBalance,ToAccountBalance  " + 
				"from TransactionJournals where DebitAccount=:accountNumber and JournalType= 0 and BookID=1  " + 
				"UNION ALL     " + 
				"select TransactionID, sum(Amount) as Amount, 'D' AS Type, JournalType,DebitDesc as Descript,FromAccountBalance,ToAccountBalance " + 
				"from TransactionJournals where DebitAccount=:accountNumber and journaltype= 1 and BookID=1  " + 
				"group by TransactionId, JournalType   " + 
				")  myresult, Transactions txn   " + 
				"                where myresult.TransactionID=txn.TransactionID " + 
				"                       and (txn.TransactionDate between :fromDateString and :toDateString)  " + 
				"                ORDER BY txn.TransactionDate desc "
		)
		.addScalar("TransactionDate",Hibernate.TIMESTAMP).addScalar("Descript",Hibernate.STRING)
		.addScalar("Type",Hibernate.STRING).addScalar("Amount",Hibernate.DOUBLE)
		.addScalar("FromAccountBalance",Hibernate.DOUBLE).addScalar("ToAccountBalance",Hibernate.DOUBLE)
		.setParameter("accountNumber", accountNumber , Hibernate.STRING)
		.setParameter("fromDateString",fromDate , Hibernate.TIMESTAMP)
		.setParameter("toDateString", toDate , Hibernate.TIMESTAMP);
		
			query.setFirstResult(s);
			query.setMaxResults(10);
			List<Object[]>list=query.list();
			return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IncorrectKycDetails getIncorrectKycByMobileNumber(String mobileNumber) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from IncorrectKycDetails kyc where kyc.mobileNumber=:mobileNumber")
				.setParameter("mobileNumber",mobileNumber);
		List<IncorrectKycDetails> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}


	@Override
	public java.util.List<String> getGeographyLocationOfAgents(LocateUsDTO locateUsDTO) {
		Query query=getSessionFactory().getCurrentSession().createQuery("select address from Customer cust where cust.country.countryId=:countryId "
				+ "and cust.city.cityId=:cityId and cust.quarter.quarterId=:quaterId and cust.type=:type")
				.setParameter("countryId", 1)
				.setParameter("cityId", locateUsDTO.getCityId())
				.setParameter("quaterId", locateUsDTO.getQuaterId().longValue())
				.setParameter("type", 1);
				
		List<String> list=query.list();
		return list;
	}
	

	@Override
	public java.util.List<ReversalTransactionDTO> getTransactionforReversal(WithdrawalTransactionsDTO withdrawalTransactionsDTO) {
		StringBuilder queryBuilder = new StringBuilder("SELECT txn.TransactionID, txn.TransactionDate, txn.ReferenceId, txn.Amount, txn.CustomerAccount, txn.OtherAccount, txn.TransactionType, txn.Status, CONCAT(cust.FirstName,' ',cust.LastName) AS CustomerName FROM Transactions txn ");
		if(withdrawalTransactionsDTO.getTransactionType() != EOTConstants.TXN_TYPE_WITHDRAWAL){
			queryBuilder.append(" INNER JOIN Customer cust ON cust.CustomerID = txn.ReferenceId AND txn.TransactionType=:TransactionType INNER JOIN CustomerAccounts cacc ON cacc.CustomerID=cust.CustomerID AND txn.CustomerAccount = cacc.AccountNumber"
				+ " AND txn.OtherAccount IN (SELECT AccountNumber FROM CustomerAccounts aacc INNER JOIN Customer c ON c.CustomerID=aacc.CustomerID AND c.AgentCode=:AgentCode)"
				+ " AND cust.MobileNumber=:mobileNumber ");
		}else{
			queryBuilder.append("INNER JOIN Customer agent ON agent.CustomerID = txn.ReferenceId AND txn.TransactionType=:TransactionType AND agent.AgentCode=:AgentCode"
					+ " INNER JOIN CustomerAccounts cacc ON cacc.AccountNumber=txn.CustomerAccount"
					+ " INNER JOIN Customer cust ON cust.CustomerID = cacc.CustomerID "
					+ " AND txn.CustomerAccount IN (SELECT AccountNumber FROM CustomerAccounts cacc INNER JOIN Customer c ON c.CustomerID=cacc.CustomerID AND c.MobileNumber=:mobileNumber)");
		}
		queryBuilder.append("AND txn.Status=:txnStatus ORDER BY txn.TransactionDate DESC");
		
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(queryBuilder.toString());
		query.setParameter("TransactionType", withdrawalTransactionsDTO.getTransactionType())
		.setParameter("AgentCode", withdrawalTransactionsDTO.getAgentCode()).setParameter("mobileNumber", withdrawalTransactionsDTO.getMobileNumber())
		.setParameter("txnStatus", EOTConstants.TXN_SUCCESS);
		List<Object[]> agentTxns = query.list();
		List<ReversalTransactionDTO> listTxn = new ArrayList<>();
		for(Object[] obj: agentTxns){
			ReversalTransactionDTO txn = new ReversalTransactionDTO();
			txn.setTransactionId(obj[0].toString());
			txn.setTransactionDate(DateUtil.stringToDateforTimeSeconds(obj[1].toString()));
			txn.setReferenceId(obj[2].toString());
			txn.setAmount(Double.parseDouble(obj[3].toString()));
			txn.setCustAccount(obj[4].toString());
			txn.setAgentAccount(obj[5].toString());
			txn.setTransactionType(Integer.parseInt(obj[6].toString()));
			txn.setStatus(Integer.parseInt(obj[7].toString()));
			txn.setCustomerName(obj[8].toString());
			listTxn.add(txn);
		}
		return listTxn;
	}
	@Override
	@SuppressWarnings("unchecked")
	public RemittanceTransaction getRemitanceTxnByRefTxnId(String refTxnNumber) {		
		Query query=getSessionFactory().getCurrentSession().createQuery("from RemittanceTransaction  where refTxnNumber=:refTxnNumber")
				.setParameter("refTxnNumber",refTxnNumber);
		List<RemittanceTransaction> list=query.list();
		return list.size()>0 ? list.get(0) :null;
	}


	@Override
	public CustomerBankAccount getCustomerBankAccountByID(String customrId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Customer> getAllAgents(LocateUsDTO locateUsDTO) {
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer cust where cust.country.countryId=:countryId "
				+ "and cust.city.cityId=:cityId and cust.quarter.quarterId=:quaterId and cust.type=:type")
				.setParameter("countryId", 1)
				.setParameter("cityId", locateUsDTO.getCityId())
				.setParameter("quaterId", locateUsDTO.getQuaterId().longValue())
				.setParameter("type", 1);
				
		List<Customer> list=query.list();
		return list;
	}

	/*@Override
	public Customer getSmsCashByNumber(String payeenumber) {
		Query query=getSessionFactory().getCurrentSession().createQuery("from Customer where mobileNumber=:mobileNumber")
				.setParameter("mobileNumber",payeenumber);
		List<Customer> list=query.list();
		return CollectionUtils.isNotEmpty(list) ? list.get(0) : null ;
	}*/
}
