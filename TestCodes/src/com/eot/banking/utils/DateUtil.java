/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: DateUtil.java,v 1.0
 *
 * Date Author Changes
 * 21 Oct, 2015, 3:02:27 PM Sambit Created
 */
package com.eot.banking.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class DateUtil.
 */
public class DateUtil {

	/**
	 * Format date and time.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formatDateAndTime(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sd.format(date);
	}

	/**
	 * Format date.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formatDate(Date date){

		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		return sd.format(date);
	}

	/**
	 * String to date time.
	 * 
	 * @param date
	 *            the date
	 * @return the date
	 */
	public static Date stringToDateTime(String date){
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			return sd.parse(date);
		} catch (ParseException e) {
//			e.printStackTrace();
		}
		return null;
	}

	public static String formatDateToString(Date date,String format){
		SimpleDateFormat sd = new SimpleDateFormat(format);
		return sd.format(date);
	}
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
			System.out.println("<<<<error >>>>"+e.getMessage());
//			e.printStackTrace();
		}
		return formattedDate;
	}

	/**
	 * Format date.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formatDate(String date){
		/* Function to change the date format from mm-dd-yy to yy-mm-dd*/
		String invDt1[] = date.toString().split("-");
		String invDt = invDt1[2]+"-"+invDt1[1]+"-"+invDt1[0];
		System.out.println("<<<<date>>>> "+invDt);
		return invDt;
	}

	/**
	 * Format date to str.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formatDateToStr(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		return sd.format(date);
	}

	/**
	 * Date and time.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String dateAndTime(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sd.format(date);
	}

	/**
	 * Formatted date and time.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formattedDateAndTime(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sd.format(date);
	}

	/**
	 * String to date time seconds.
	 * 
	 * @param date
	 *            the date
	 * @return the date
	 */
	public static Date stringToDateTimeSeconds(String date){
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			return sd.parse(date);
		} catch (ParseException e) {
//			e.printStackTrace();
		}
		return null;
	}


	public static Date stringToDateforTimeSeconds(String date){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sd.parse(date);
		} catch (ParseException e) {
//			e.printStackTrace();
		}
		return null;
	}

	public static String formatDateWithSlash(String date,int HH, int mm, int ss){
		/* Function to change the date format from mm-dd-yy to yy-mm-dd*/
		String invDt1[] = date.toString().split("/");
		String invDt = invDt1[2]+"-"+invDt1[1]+"-"+invDt1[0];
		//System.out.println("<<<<date>>>> "+invDt+" "+HH+":"+mm+":"+ss);
		return invDt+" "+HH+":"+mm+":"+ss;
	}

	public static Date mySQLdateAndTime(String date){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Calcutta"));
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//sd.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		Date d1 = DateUtil.stringToDateforTimeSeconds(date);
		date = sd.format(d1);


		try {
			return sd.parse(date);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
	public static String millisToDate(long millis){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone("EAT"));
		return formatter.format(new Date(millis));
	}
	public static void main(String[] args) {
		System.out.println(new Date());
		System.out.println(millisToDate(1564684200000L));
	}

}
