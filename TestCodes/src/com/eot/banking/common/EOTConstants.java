package com.eot.banking.common;

public interface EOTConstants {
	
	Integer TXN_TYPE_DEPOSIT=95;
	
	Integer SERVICE_CHARGE_IMPOSED_ON_CUSTOMER = 0;
	Integer SERVICE_CHARGE_IMPOSED_ON_OTHER = 1;
	
	Integer TXN_TYPE_INTRA = 1;
	Integer TXN_TYPE_INTER = 2;
	
	String AMT_TYPE_SERVICE_CHARGE = "SC";
	String AMT_TYPE_TAX_AMT = "TAX_AMT";
	String AMT_TYPE_BANK_REVENUE = "BANK_REVENUE";
	String AMT_TYPE_EOT_SC_SHARE = "EOT_SC_SHARE";
	String AMT_TYPE_TXN_AMT = "TXN_AMT";
	String AMT_TYPE_INTER_BANK_FEE = "INTER_BANK_FEE";
	String AMT_TYPE_STAMP_FEE = "STAMP_FEE";
	
	Integer RULE_LEVEL_GLOBAL = 1;
	Integer RULE_LEVEL_BANK_GROUP = 2;
	Integer RULE_LEVEL_CUSTOMER_PROFILE = 3;
	Integer RULE_LEVEL_INTER_BANK = 4;
	
	Integer SERVICE_CHARGE_NOT_DEFINED = 2024;
	Integer INVALID_PARAMETERS_ERROR = 2003;
	
	Integer DEFAULT_COUNTRY_ID=1;
	
	String INVALID_BUSINESS_PARTNER = "User doesn't exists";
	String INVALID_USER_PIN = "You have entered invalid Pin";
	String FIELD_NON_EMPTY_PASSWORD = "Please enter Password";
	
	Integer PROFILE_TYPE_SELF_REGISTER  = 99;
	
	Integer TRANSACTION_REPORT = 134;
	
	public static Integer TXN_TYPE_PAY = 128;
	public static Integer TXN_TYPE_SALE = 90;
	public static Integer TXN_TYPE_TRFDIRECT = 55;
	public static Integer TXN_TYPE_MERCHANT_PAYOUT = 140;
	public static Integer TXN_NO_ERROR = 2000;
	public static Integer TXN_INITIATE_ADJUSTMENT = 11;
	public static Integer TXN_TYPE_Transaction_OTP = 132;
	
	String REQUEST_CHANNEL_MOBILE = "Mobile";
	
	Integer LOGIN_ATTEMPTS=4;
	
	int APP_STATUS_BLOCKED = 80;
	
	int APP_STATUS_ACTIVATED = 40 ;
	
	  public final String PROFILE_PIC = "profile_image";
	  public final String ID_PROOF_PIC = "id_proof_image";
	  public final String SIGNATURE_PIC = "id_sign_image";
	  public final String ADDRESS_PROOF_PIC = "address_image";
	  public final String COLUMN_FORM_DATA = "registration_form_data";
	  
	Integer DEFAULT_APPLICATION_TYPE = 1;
	
	int CUSTOMER_REGISTERED_SMS_CASH=1;
	int ALIAS_TYPE_WALLET_ACCOUNT = 1;
	Integer CUSTOMER_REPORT = 142;
	int REFERENCE_TYPE_CUSTOMER = 0;
	int REFERENCE_TYPE_AGENT = 1;
	int REFERENCE_TYPE_MERCHANT = 2;
	int REFERENCE_TYPE_AGENT_SOLE_MERCHANT = 3;
//	String APP_TYPE_CUSTOMER = "customer";
//	String APP_TYPE_AGENT = "agent";
//	String APP_TYPE_MERCHANT = "merchant";
	int TXN_REVERSAL = 10;
	int CUSTOMER_STATUS_DEACTIVATED = 40;
	
	int KYC_STATUS_APPROVE_PENDING = 1;
	
	int CUSTOMER_STATUS_SUSPENDED = 3;
	
	int MOBEQUEST_STATUS_FAILURE = 2;
	
	int TXN_ID_TYPE_REVERSAL = 61;
	
	String EOT_CHANNEL = "1";
	
	int TXN_SUCCESS = 2000;
	
	Integer TXN_TYPE_WITHDRAWAL=116;
	
	int BP_TYPE_REMITTANCE=5;
	int BP_TYPE_NILE_BET=6;
	int BP_TYPE_DSTV=7;
	
	int KYC_STATUS_PENDING=0;
	int KYC_STATUS_APPROVED=11;
	int KYC_STATUS_REGEJETED=21;
	
}
