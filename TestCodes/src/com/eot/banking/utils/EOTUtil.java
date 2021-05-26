/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: EOTUtil.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:02:36 PM Sambit Created
*/
package com.eot.banking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * The Class EOTUtil.
 */
public class EOTUtil {
	
	/**
	 * String to date.
	 * 
	 * @param date
	 *            the date
	 * @return the date
	 */
	public static Date stringToDate(String date){
		Date formattedDate = null;
		try {
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			formattedDate = sd.parse(date);
		} catch (ParseException e) {
//			e.printStackTrace();
		}
		return formattedDate;
	}
	
	/**
	 * Generate uuid.
	 * 
	 * @return the string
	 */
	public static String generateUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "") ;
	}
	
	/**
	 * Generate app id.
	 * 
	 * @return the string
	 */
	public static String generateAppID(){
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0,16);
	}
	
	/**
	 * Generate confirmation code.
	 * 
	 * @return the integer
	 */
	public static Integer generateConfirmationCode(){
		String loginPin =  (UUID.randomUUID().getLeastSignificantBits()+"").substring(1,9) ;
		return new Integer(loginPin);
	}
	
	/**
	 * Generate login pin.
	 * 
	 * @return the integer
	 */
	public static Integer generateLoginPin(){
		String loginPin =  (UUID.randomUUID().getLeastSignificantBits()+"").substring(1,5) ;
		return new Integer(loginPin);
	}
	
	/**
	 * Generate transaction pin.
	 * 
	 * @return the integer
	 */
	public static Integer generateTransactionPin(){
		String txnPin = (UUID.randomUUID().getLeastSignificantBits()+"").substring(1,5) ;
		return new Integer(txnPin);
	}
	
	/**
	 * Generate web user password.
	 * 
	 * @return the string
	 */
	public static String generateWebUserPassword(){
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0,10);
	}
	
	/**
	 * Generate account number.
	 *
	 * @param accountNumSeq the account num seq
	 * @return the string
	 */
	public static String generateAccountNumber(Long accountNumSeq){
		final int[][] sumTable = {{0,2,4,6,8,1,3,5,7,9}, {0,1,2,3,4,5,6,7,8,9}};
        int sum = 0, flip = 0;
 
        String accountSeq = accountNumSeq+"";
        for (int i = accountSeq.length() - 1; i >= 0; i--) {
            sum += sumTable[flip++ & 0x1][Character.digit(accountSeq.charAt(i), 10)];
        }
        int modulusResult = (sum % 10);
        int checkDigit = ((modulusResult==0)? modulusResult: (10-modulusResult));
        return accountSeq+checkDigit;
	}
	
}
