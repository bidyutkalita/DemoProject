/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: Constants.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:07 PM Sambit Created
*/
package com.eot.banking.server;

// TODO: Auto-generated Javadoc
/**
 * The Interface Constants.
 */
public interface Constants {

	/** The KE y_ version. */
	String KEY_VERSION = "1.0";

	/** The KE y_ owner. */
	String KEY_OWNER = "eot";

	/** The PI n_ has h_ algorithm. */
	String PIN_HASH_ALGORITHM = "SHA-512";

	/** The MOBREQUES t_ statu s_ logged. */
	int MOBREQUEST_STATUS_LOGGED = 0;

	/** The MOBREQUES t_ statu s_ success. */
	int MOBREQUEST_STATUS_SUCCESS = 1;

	/** The MOBREQUES t_ statu s_ failure. */
	int MOBREQUEST_STATUS_FAILURE = 2;

	/** The MO b_ res p_ statu s_ success. */
	int MOB_RESP_STATUS_SUCCESS = 0;

	/** The MO b_ res p_ statu s_ failure. */
	int MOB_RESP_STATUS_FAILURE = 1;

	/** The MO b_ updat e_ status. */
	int MOB_UPDATE_STATUS = 2;

	/** The ACTIVATIO n_ req. */
	int ACTIVATION_REQ = 10;

	/** The CHANG e_ pi n_ req. */
	int CHANGE_PIN_REQ = 15;

	/** The CHANG e_ tx n_ pi n_ req. */
	int CHANGE_TXN_PIN_REQ = 20;

	/** The PROFIL e_ updat e_ req. */
	int PROFILE_UPDATE_REQ = 25;

	/** The BA l_ en q_ req. */
	int BAL_ENQ_REQ = 30;

	/** The MINISTATEMEN t_ req. */
	int MINISTATEMENT_REQ = 35;

	/** The AD d_ car d_ req. */
	int ADD_CARD_REQ = 40;

	/** The CONFIR m_ car d_ req. */
	int CONFIRM_CARD_REQ = 45;

	/** The DELET e_ car d_ req. */
	int DELETE_CARD_REQ = 50;

	/** The TRANSFE r_ direc t_ req. */
	int TRANSFER_DIRECT_REQ = 55;

	/** The SE t_ defaul t_ ac c_ req. */
	int SET_DEFAULT_ACC_REQ = 60;

	/** The RESE t_ pi n_ req. */
	int RESET_PIN_REQ = 65;

	/** The REACTIVATIO n_ req. */
	int REACTIVATION_REQ = 70;

	/** The RESE t_ tx n_ pi n_ req. */
	int RESET_TXN_PIN_REQ = 75;

	/** The MOBIL e_ topu p_ req. */
	int MOBILE_TOPUP_REQ = 80;

	/** The GE t_ custome r_ account s_ req. */
	int GET_CUSTOMER_ACCOUNTS_REQ = 85;

	/** The SAL e_ req. */
	int SALE_REQ = 90;

	/** The txn id deposit. */
	int TXN_ID_DEPOSIT = 95;

	/** The txn id withdrawal. */
	int TXN_ID_WITHDRAWAL = 99;

	/** The GI m_ channel. */
	String EOT_CHANNEL = "1";

	/** The RE f_ typ e_ customer. */
	int REF_TYPE_CUSTOMER = 0;

	/** The RE f_ typ e_ merchant. */
	int REF_TYPE_AGENT = 1;

	/** The RE f_ typ e_ sol e_ merchant. */
	int REF_TYPE_MERCHANT = 2;

	int REF_TYPE_AGENT_SOLE_MERCHANT = 3;

	/** The CUSTOME r_ statu s_ new. */
	int CUSTOMER_STATUS_NEW = 10;

	/** The CUSTOME r_ statu s_ active. */
	int CUSTOMER_STATUS_ACTIVE = 20;

	/** The CUSTOME r_ statu s_ inactive. */
	int CUSTOMER_STATUS_INACTIVE = 30;

	/** The CUSTOME r_ statu s_ deactivated. */
	int CUSTOMER_STATUS_DEACTIVATED = 40;
	
	/** The CUSTOME r_ statu s_ suspended. */
	int CUSTOMER_STATUS_SUSPENDED = 3;

	/** The AP p_ statu s_ new. */
	int APP_STATUS_NEW = 10;

	/** The AP p_ statu s_ downloaded. */
	int APP_STATUS_DOWNLOADED = 20;

	/** The AP p_ statu s_ activatio n_ s c_ debited. */
	int APP_STATUS_ACTIVATION_SC_DEBITED = 30;
	int APP_STATUS_ACTIVATION_SC_DEBITED_temp = 20;// only for resolving change pin issue on login. it may effect our old flow..
													// need to take care

	/** The AP p_ statu s_ activated. */
	int APP_STATUS_ACTIVATED = 40;

	/** The AP p_ statu s_ rese t_ pi n_ verified. */
	int APP_STATUS_RESET_PIN_VERIFIED = 50;

	/** The AP p_ statu s_ rese t_ pi n_ s c_ debited. */
	int APP_STATUS_RESET_PIN_SC_DEBITED = 60;

	/** The AP p_ statu s_ ne w_ pi n_ sent. */
	int APP_STATUS_NEW_PIN_SENT = 70;

	/** The AP p_ statu s_ blocked. */
	int APP_STATUS_BLOCKED = 80;

	/** The AP p_ statu s_ deactivated. */
	int APP_STATUS_DEACTIVATED = 90;

	/** The ALIA s_ typ e_ mobil e_ acc. */
	Integer ALIAS_TYPE_MOBILE_ACC = 1;

	/** The ALIA s_ typ e_ car d_ acc. */
	Integer ALIAS_TYPE_CARD_ACC = 2;

	/** The ALIA s_ typ e_ ban k_ acc. */
	Integer ALIAS_TYPE_BANK_ACC = 3;

	/** The alias type fi acc. */
	Integer ALIAS_TYPE_FI_ACC = 4;

	/** The ALIA s_ typ e_ other. */
	Integer ALIAS_TYPE_OTHER = 0;

	/** The TYP e_ mobil e_ wallet. */
	String TYPE_MOBILE_WALLET = "1";

	/** The OT p_ typ e_ we b_ user. */
	int OTP_TYPE_WEB_USER = 1;

	/** The OT p_ typ e_ customer. */
	int OTP_TYPE_CUSTOMER = 2;

	/** The OT p_ typ e_ customer. */
	int OTP_TYPE_FORGOT_PIN = 3;

	int OTP_TYPE_WITHDRAWA = 4;
	int OTP_TYPE_REMITTANCE_OUTWARD = 5;

	/** The CAR d_ statu s_ no t_ confirmed. */
	int CARD_STATUS_NOT_CONFIRMED = 1;

	/** The CAR d_ statu s_ confirmed. */
	int CARD_STATUS_CONFIRMED = 2;

	/** The CAR d_ statu s_ deleted. */
	int CARD_STATUS_DELETED = 3;

	/** The INACTIV e_ ban k_ status. */
	int INACTIVE_BANK_STATUS = 0;

	/** The ACTIVE. */
	int ACTIVE = 2;

	/** The INACTIVE. */
	int INACTIVE = 3;

	/** The wallet payee. */
	Integer WALLET_PAYEE = 1;

	/** The account payee. */
	Integer ACCOUNT_PAYEE = 2;

	/** The default country id. */
	Integer DEFAULT_COUNTRY_ID = 1;

	/** The default language. */
	String DEFAULT_LANGUAGE = "en_US";

	/** The default app version. */
	String DEFAULT_APP_VERSIOn = "1.0";

	/** The account type personal. */
	int ACCOUNT_TYPE_PERSONAL = 1;

	/** The account type nominal. */
	int ACCOUNT_TYPE_NOMINAL = 2;

	/** The account type real. */
	int ACCOUNT_TYPE_REAL = 3;

	/** The account status active. */
	Integer ACCOUNT_STATUS_ACTIVE = 1;

	/** The account balance type debit. */
	int ACCOUNT_BALANCE_TYPE_DEBIT = 0;

	/** The account balance type credit. */
	int ACCOUNT_BALANCE_TYPE_CREDIT = 1;

	/** The default bank. */
	Integer DEFAULT_BANK = 1;

	/** The default branch. */
	Long DEFAULT_BRANCH = 1L;

	/** The active status. */
	Integer ACTIVE_STATUS = 10;

	/** The location type branch. */
	Integer LOCATION_TYPE_BRANCH = 1;

	/** The account alias customer. */
	String ACCOUNT_ALIAS_CUSTOMER = "mGURUSH Mobile";

	/** The default customer profile. */
	Integer DEFAULT_CUSTOMER_PROFILE = 1;

	/** The txn status. */
	Integer TXN_STATUS_SMS_CASH_INITIATED = 1998;

	/** The txn status sucessful. */
	Integer TXN_STATUS_SUCESSFUL = 2000;
	Integer TXN_STATUS_RECONSILE = 1999;
	Integer PAYEE_NAME_LEGNTH = 10;
	Integer DYNAMIC_MENU_ACTIVE = 10;

	Integer APP_TYPE_INTER_OPERABILITY = 20;

	int TXN_TYPE_MERCHANT_DEPOSIT = 115;
	int TXN_ID_MERCHANT_WITHDRAWAL = 116;
	int TXN_ID_AGENT_CASHOUT = 138;
	int TXN_ID_MERCHANT_MERCHANT_PAYOUT = 140;
	int TXN_ID_REMITTANCE_OUTWARD = 149;
	int TXN_ID_REMITTANCE_OUTWARD_AGENT = 153;
	int TXN_TYPE_SET_UP_APPLICATION = 139;
	int TXN_TYPE_PURCHES_NILE_UNIT = 152;
	int TXN_TYPE_RECHARGE_DSTV= 151;
	Integer ALIAS_TYPE_MERCHANT_ACC = 0;
	Integer TRANSACTION_INITIATED_FOR_APPROVAL = 100;
	Integer TRANSACTION_STATUS_SUCCESS = 101;
	Integer TRANSACTION_STATUS_FAIL = 105;
	Integer TRANSACTION_STATUS_REJECTED = 107;
	int TXN_ID_MERCHANT_SALE = 90;
	Integer MERCHANT_NETWORK = 2;
	// Integer TRANSACTION_PENDING=108;

	int REQUEST_FOR_APPROVE = 1;

	Integer PIN_TO_BENEFICIARY = 1;

	Integer PIN_TO_REMITTER = 2;

	Integer PIN_TO_BOTH = 3;
	int TXN_TYPE_PAY = 128;
	int TXN_TYPE_SMS_CASH = 83;
	int TXN_TYPE_SMS_CASH_RECEIVE = 126;

	/** The RE f_ typ e_ BusinessPartner. */
	int REF_TYPE_BUSINESS_PARTNER_USER = 4;

	int TXN_TYPE_FLOAT_MANAGEMENT = 133;
	int FLOAT_MANAGEMENT_REQ = 133;

	String ACCOUNT_ALIAS_COMMISSION = "Agent Commission Account";

	int ALIAS_TYPE_WALLET_ACCOUNT = 1;

	int ALIAS_TYPE_COMMISSION_ACCOUNT = 10;
	
	/** The reference type customer. */
	int REFERENCE_TYPE_CUSTOMER = 0;
	
	/** The reference type merchant. */
//	int REFERENCE_TYPE_MERCHANT = 2;
	
	/** The reference type merchant. */
	int REFERENCE_TYPE_AGENT = 1;
	
	/** The reference type sole merchant. */
	int REFERENCE_TYPE_MERCHANT = 2;
	
	/** The reference type sole merchant. */
	int REFERENCE_TYPE_AGENT_SOLE_MERCHANT = 3;
	
	/*String APP_TYPE_CUSTOMER = "customer";
	String APP_TYPE_AGENT = "agent";
	String APP_TYPE_MERCHANT = "merchant";*/
	
	String APP_TYPE_CUSTOMER = "0eb49y";
	String APP_TYPE_AGENT = "br159y";
	String APP_TYPE_MERCHANT = "as159y";
	
	/** The transaction type merchnat float. */
	int EXEC_PAY_MERCHANT = 146;
	Integer VALIDATE_MERCHANT_PAY = 147;
	
	int REMITANCE_STATUS_INITIATED = 1;
	int REMITANCE_STATUS_SUCCESS =0;
	int REMITANCE_STATUS_FAILED = 2;
	
	int MESSAGE_TYPE_WALLET_TO_BANK = 135;
	int MESSAGE_TYPE_BANK_TO_WALLET = 136;

}
