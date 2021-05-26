/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ErrorConstants.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:17 PM Sambit Created
*/
package com.eot.banking.exception;

// TODO: Auto-generated Javadoc
/**
 * The Interface ErrorConstants.
 */
public interface ErrorConstants {
	
	/** The INVALI d_ date. */
	int INVALID_DATE = 6001 ;
	
	/** The INVALI d_ time. */
	int INVALID_TIME = 6002 ;
	
	/** The INVALI d_ application. */
	int INVALID_APPLICATION = 6003 ;
	
	/** The INVALI d_ customer. */
	int INVALID_CUSTOMER = 6004 ;
	
	/** The INVALI d_ Mobile_Number. */
	int INVALID_MOBILE_NUMBER = 6105 ;
	
	/** The INVALI d_ Mobile_Number. */
	int INVALID_CUSTOMER_MOBILE_NUMBER = 6104 ;
	
	/** The APPLICATIO n_ alread y_ active. */
	int APPLICATION_ALREADY_ACTIVE = 6005 ;
	
	/** The APPLICATIO n_ blocked. */
	int APPLICATION_BLOCKED = 6006 ;
	
	/** The CUSTOME r_ deactivated. */
	int CUSTOMER_DEACTIVATED = 6007 ;
	
	/** The APPLICATIO n_ deactivated. */
	int APPLICATION_DEACTIVATED = 6022 ;
	
	/** The INVALI d_ use r_ pin. */
	int INVALID_USER_PIN = 6008 ;
	
	/** The INVALI d_ tx n_ pin. */
	int INVALID_TXN_PIN = 6009 ;
	/** The INVALI d_ tx n_ pin. */
	int INVALID_SMS_CASH_PIN = 6029 ;
	
	/** The ACCOUN t_ no t_ found. */
	int ACCOUNT_NOT_FOUND = 6010 ;
	
	/** The CAR d_ no t_ found. */
	int CARD_NOT_FOUND = 6023 ;
	
	/** The MERCHAN t_ accoun t_ no t_ found. */
	int MERCHANT_ACCOUNT_NOT_FOUND = 6011;
	
	/** The CUSTOME r_ accoun t_ no t_ found. */
	int CUSTOMER_ACCOUNT_NOT_FOUND = 6012;
	
	/** The CAR d_ tx n_ no t_ allowed. */
	int CARD_TXN_NOT_ALLOWED = 6013;
	
	/** The INVALI d_ custome r_ tx n_ pin. */
	int INVALID_CUSTOMER_TXN_PIN = 6014 ;
	
	/** The PAYE e_ no t_ found. */
	int PAYEE_NOT_FOUND = 6015 ;
	
	/** The VERIF y_ custome r_ rese t_ tx n_ pin. */
	int VERIFY_CUSTOMER_RESET_TXN_PIN = 6016 ;
	
	/** The INVALI d_ reinitiatio n_ request. */
	int INVALID_REINITIATION_REQUEST = 6017 ;
	
	/** The SM s_ aler t_ failed. */
	int SMS_ALERT_FAILED = 6018;
	
	/** The LOGI n_ pi n_ reset. */
	int LOGIN_PIN_RESET = 6019 ;
	
	/** The INVALI d_ custome r_ otp. */
	int INVALID_CUSTOMER_OTP = 6020;
	
	/** The INVALI d_ applicatio n_ state. */
	int INVALID_APPLICATION_STATE = 6021 ;
	
	/** The INVALI d_ operator. */
	int INVALID_OPERATOR = 6024;
	
	/** The N o_ bill s_ found. */
	int NO_BILLS_FOUND = 6025;
	
	/** The N o_ partia l_ payments. */
	int NO_PARTIAL_PAYMENTS = 6026;
	
	/** The INVALI d_ bil l_ amount. */
	int INVALID_BILL_AMOUNT = 6027;
	
	
	/** The INVALI d_ bil l_ amount. */
	int INVALID_FI_CUSTOMER = 6028;
	
	/** The SERVIC e_ error. */
	int SERVICE_ERROR = 9999 ;
	
	/** The INVALI d_ c h_ pool. */
	int INVALID_CH_POOL = 1022;
	
	/** The INVALI d_ biller. */
	int INVALID_BILLER = 7001;
	
	/** The INVALI d_ confimatio n_ code. */
	int INVALID_CONFIMATION_CODE = 7002;
	
	/** The LAS t_ tx n_ no t_ available. */
	int LAST_TXN_NOT_AVAILABLE = 7003;
	
	/** The INACTIV e_ customer. */
	int INACTIVE_CUSTOMER = 7009;
	
	/** The MOBIL e_ i d_ exist. */
	int MOBILE_ID_EXIST = 7050;
	
	/** The SAM e_ mobil e_ no. */
	int SAME_MOBILE_NO = 7051;
	
	/** The INACTIV e_ bank. */
	int INACTIVE_BANK = 7052;
	
	/** The INACTIV e_ payee. */
	int INACTIVE_PAYEE = 7053;
	
	/** The BAN k_ car d_ no t_ found. */
	int BANK_CARD_NOT_FOUND = 7054;
	
	/** The INVALI d_ ban k_ account. */
	Integer INVALID_BANK_ACCOUNT = 8001;
	
	/** The ACCOUN t_ numbe r_ exist. */
	Integer ACCOUNT_NUMBER_EXIST = 8002;
	
	/** The INVALI d_ chequ e_ number. */
	Integer INVALID_CHEQUE_NUMBER = 8003;
	
	/** The N o_ transactio n_ found. */
	Integer NO_TRANSACTION_FOUND = 8004;
	
	/** The INVALI d_ ban k_ custome r_ id. */
	Integer INVALID_BANK_CUSTOMER_ID = 8005;
	
	/** The INVALI d_ ban k_ custome r_ i d_ fo r_ cheque. */
	Integer INVALID_BANK_CUSTOMER_ID_FOR_CHEQUE = 8006;

	/** The payee exist. */
	Integer PAYEE_EXIST = 8007;

	/** The mobile number registered already. */
	Integer MOBILE_NUMBER_REGISTERED_ALREADY = 1019;
	
	/** The non empty field. */
	Integer FIELD_NON_EMPTY = 1016;

	/** The invalid customer account. */
	Integer INVALID_CUSTOMER_ACCOUNT = 1017;
	
	/** The invalid agent account. */
	Integer INVALID_AGENT_ACCOUNT = 1277;

	/** The application update available. */
	Integer APPLICATION_UPDATE_AVAILABLE = 8008;

	/** The payee does not exist. */
	Integer PAYEE_DOES_NOT_EXIST = 8009;

	/** The exchange rate not available. */
	Integer EXCHANGE_RATE_NOT_AVAILABLE = 8010;

	/** The location details not available. */
	Integer LOCATION_DETAILS_NOT_AVAILABLE = 8011;

	/** The conversion rate not available. */
	Integer CONVERSION_RATE_NOT_AVAILABLE = 8012;
	
	/** The invalid signature size. */
	Integer INVALID_SIGNATURE_SIZE = 8013;
	
	/** The invalid idproof size. */
	Integer INVALID_IDPROOF_SIZE = 8014;
	

	/** The txn details not available. */
	Integer TXN_DETAILS_NOT_AVAILABLE = 8015;
	Integer MOBILE_MENU_NNOT_FOUND = 8016;
	Integer PAYEE_NAME_TO_LONG = 8017;
	
	Integer INVALID_MERCHANT = 8018 ;
	Integer INVALID_MERCHANT_ACCOUNT = 8019 ;

	Integer INVALID_BANK_ID = 4001;
	Integer INVALID_BUSINESS_PARTNER_USER = 6028;
	Integer INACTIVE_BUSINESS_PARTNER = 6030;
	/** The INVALI d_ businesspartner. */
	int INVALID_BUSINESS_PARTNER = 6174 ;
	
	int USER_NOT_ACTIVE= 4002;
	int CUSTOMR_DOCUMENT_ALREADY_EXIST= 4003;
	
	int TXN_NOT_ALLOWED = 8020;
	
	Integer USER_NOT_EXISTS = 4112;
	Integer INVALID_OTP = 4113 ;
	Integer INVALID_NUMBER = 4114;
	Integer UNVALID_SMS_CASH = 4115;
	
	/** The AGENT_CODE_ not_ found. */
	int AGENT_CODE_NOT_FOUND = 6115 ;
	
	Integer DATA_NOT_AVAILABLE = 4116;
	Integer UNVAILABLE_REGISTRATION = 4117;
	
	int MAX_INCORRECT_LOGIN_ATTEMPT = 6030 ;
	Integer INCORRECT_LOGIN_ATTEMPT = 6031 ;
	
	int CHANK_SIZE_NOT_MATCHING=6032;
	
	/** The INVALI d_ app type. */
	int INVALID_USER_TYPE = 6121 ;
	
	int NOT_CUSTOMER = 6122;
	int CUSTOMER_ACC_DEACTIVATED = 6123;
	int AGENT_ACC_DEACTIVATED = 6124;
	int MERCHANT_ACC_DEACTIVATED = 6125;
	int Y_CUSTOMER_ACC_DEACTIVATED = 6126;
	int Y_AGENT_ACC_DEACTIVATED = 6127;
	int Y_MERCHANT_ACC_DEACTIVATED = 6128;
	Integer INVALID_MERCHANT_CODE = 6129;
	int MERCHANT_CODE_NOT_FOUND = 6132 ;
	
	int Y_ACCOUNT_BLOCKED = 6133;
	
	int CUSTOMER_ACC_BLOCKED = 6134;
	int AGENT_ACC_BLOCKED = 6135;
	int MERCHANT_ACC_BLOCKED = 6136;
	
	int Y_ACCOUNT_SUSPENDED = 6137;
	int TXN_ALREADY_EXIST = 6138;
	int INSUFFIENT_BALANCE = 2021;
	int CORE_CONNECTION_ERROR = 1027;
	int COUNTRY_FIELD_EMPTY = 6139;
	int NO_PROVIDER_FOUND = 6140;
	
	int CUSTOMER_KYC_PENDING = 6141;
	int AGENT_KYC_PENDING = 6142;
	int MERCHANT_KYC_PENDING = 6143;
	
	int CUSTOMER_KYC_REJECTED = 6144;
	int AGENT_KYC_REJECTED = 6145;
	int MERCHANT_KYC_REJECTED = 6146;
	int LOCATION_NOT_FOUND = 6147;
	
	int CUSTOMER_ACC_SUSPENDED = 6148;
	
}
