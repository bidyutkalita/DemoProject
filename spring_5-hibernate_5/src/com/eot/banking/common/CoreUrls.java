package com.eot.banking.common;


public interface CoreUrls {
	final static String PROTOCOL="http://";
	final static String HTTP_PORT="7070";
	final static String HOST="localhost";
	
	final static String BALANCE_ENQ_WALLET=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/basicBanking/balanceEnquiry";
	final static String MINI_STATEMENT=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/basicBanking/miniStatement";
	final static String TRANSACTION_STATEMENT=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/basicBanking/transactionStatement";
	
	
	final static String DEPOSITE_TXN_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/deposit";
	final static String WITHDRAWAL_TXN_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/withdrawal";
	final static String TRANSFER_DIRECT_TXN_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/transferDirect";
	final static String VOID_TXN_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/voidTransaction";
	final static String SMS_CASH_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/smsCash";
	final static String ADJUSTMENT_TXN_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/adjustmentTransaction";
	final static String COMMISSION_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/commissionShare";
	final static String SALE_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/saleTxn";
	final static String REMITANCE_TRANSACTION=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/remittanceOutward";
	final static String NILE_BETTING_UNIT_PURCHASE=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/bettingCoinPurchase";
	final static String DSTV_RECHARGE=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/dthTopup";
	
	final static String NILE_BETTING_DEPOSITE=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/banking/bettingDeposit";
	
	
	final static String SERVICE_CHARGE_DEBIT_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/utility/serviceChargeDebitReq";
	final static String BILL_PAYMENT_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/utility/billPayment";
	final static String TOPUP_URL=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/utility/topUp";
	
	final static String KCB_ACCOUNT_VALIDATION=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/integtation/accountValidation";
	final static String KCB_ACCOUNT_LINKING=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/integtation/linkKcbAccount";
	final static String KCB_ACCOUNT_DELINKING=PROTOCOL+HOST+":"+HTTP_PORT+"/EOT-Banking-Core/rest/integtation/deLinkKcbAccount";
	

}
