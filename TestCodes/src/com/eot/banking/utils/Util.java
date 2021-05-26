package com.eot.banking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.eot.banking.common.EOTConstants;
import com.eot.dtos.common.AmountType;
import com.eot.dtos.common.Header;


public class Util {

	public static String formatDateToString(Date date,String format){
		SimpleDateFormat sd = new SimpleDateFormat(format);
		return sd.format(date);
	}

	public static Date stringToDate(String date){
		Date formattedDate = null;
		try {
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			formattedDate = sd.parse(date);
		} catch (ParseException e) {
			System.out.println("<<<<error >>>>"+e.getMessage());
//			e.printStackTrace();
		}
		return formattedDate;
	}

	public static Calendar strToCalender(String date){
		Date formattedDate = null;
		Calendar cal = Calendar.getInstance();
		try {
			SimpleDateFormat sd = new SimpleDateFormat("yyMMdd");
			formattedDate = sd.parse(date);
			cal.setTime(formattedDate);
		} catch (ParseException e) {
			System.out.println("<<<<error >>>>"+e.getMessage());
//			e.printStackTrace();
		}
		return cal;
	}

	public static String formatDate(String date){
		/* Function to change the date format from mm-dd-yy to yy-mm-dd*/
		String invDt1[] = date.toString().split("-");
		String invDt = invDt1[2]+"-"+invDt1[1]+"-"+invDt1[0];
		System.out.println("<<<<date>>>> "+invDt);
		return invDt;
	}

	public static String formatDateToStr(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		return sd.format(date);
	}

	public static String dateAndTime(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sd.format(date);
	}

	public static String getTime(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
		return sd.format(date);
	}

	public static String formatLocaleString( String input ) {

		if( input != null ) {

			input = input.indexOf('_') > 0 ? input.substring( 0, input.indexOf('_')) : input;
		}

		return input;
	}

	public static String maskAccountNo( String accountNo ) {

		if( accountNo != null && (accountNo.length() - 5 > 0 )) {

			return "xxxxxxxx" + accountNo.substring( accountNo.length() - 5 );
		}
		else return accountNo;
	}

	private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");

	public static Double evaluateAmount( String expression, Map<String, 
			Object> bindingData ) throws ScriptException {

		Bindings bindings = new SimpleBindings( bindingData );
		Object retValue = scriptEngine.eval( expression, bindings );
		//Below commented code used when amount was long
		/*if( retValue instanceof Double ) {

			return ((Double)retValue).longValue();
		}*/
		return (Double)retValue;
	}

	public static Double getServiceCharge( Header header ) {

		return getAmountFromHeader(header, EOTConstants.AMT_TYPE_SERVICE_CHARGE );
	}

	public static Double getAmountFromHeader( Header header, String amtType ) {

		Double amt = 0D;
		if( header != null && amtType!= null && header.getAmountList() != null ) {

			for( int i = 0; i < header.getAmountList().length; i++ ) {

				if( amtType.equals( 
						header.getAmountList(i).getAmountType() )) {

					return header.getAmountList(i).getAmount();
				}
			}
		}

		return amt;
	}

	public static void setAmountInHeader( Header header, String amtType, Double value ) {

		if( header != null && amtType != null ) {

			for( int i = 0; header.getAmountList() != null && i < header.getAmountList().length; i++ ) {

				if( amtType.equals( header.getAmountList(i).getAmountType())) {

					header.setAmountList( i, new AmountType( amtType, value));
					return;
				}
			}

			int currentSize = header.getAmountList() == null ? 0 : header.getAmountList().length;
			AmountType[] newAmountList = new AmountType[currentSize + 1];
			for( int i = 0; i < currentSize; i++ ) {

				newAmountList[i] = header.getAmountList(i);
			}
			newAmountList[currentSize] = new AmountType( amtType, value );
			header.setAmountList(newAmountList);
		}
	}

	public static int getIntraOrInter( Header header ) {

		int interOrIntra = 0;
		if( header.getOtherAccount() == null ||
				header.getCustomerAccount().getBankCode().equals( header.getOtherAccount().getBankCode() )) {

			interOrIntra = EOTConstants.TXN_TYPE_INTRA;
		}
		else  {

			interOrIntra = EOTConstants.TXN_TYPE_INTER;
		}

		return interOrIntra;
	}
}
