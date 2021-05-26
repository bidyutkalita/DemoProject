/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: WebAppUtils.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:02:51 PM Sambit Created
*/
package com.eot.banking.utils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class WebAppUtils.
 */
public class WebAppUtils {

	/** The context. */
	private static ApplicationContext context = null ;

	/**
	 * Inits the.
	 * 
	 * @param request
	 *            the request
	 */
	public static void init(HttpServletRequest request){
		if(context==null){
			context = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
		}
	}

	/**
	 * Gets the message.
	 * 
	 * @param messageCode
	 *            the message code
	 * @param language
	 *            the language
	 * @return the message
	 */
	public static String getMessage(String messageCode, String language){
		return context.getMessage(messageCode, null , new Locale(language));
	}

	/**
	 * Gets the error message.
	 * 
	 * @param messageCode
	 *            the message code
	 * @param language
	 *            the language
	 * @return the error message
	 */
	public static String getErrorMessage(Integer messageCode, String language){
		String msg=context.getMessage("ERROR_"+messageCode, null , new Locale(language));
		return msg;
	}

	/**
	 * Gets the dynamic message.
	 * 
	 * @param messageCode
	 *            the message code
	 * @param str
	 *            the str
	 * @param language
	 *            the language
	 * @return the dynamic message
	 */
	public static String getDynamicMessage(String messageCode,String[] str, String language){
		String msg=context.getMessage(messageCode, str , new Locale(language));
		return msg;
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public static ApplicationContext getContext() {
		return context;
	}

	/**
	 * Sets the context.
	 * 
	 * @param cntext
	 *            the new context
	 */
	public static void setContext(ApplicationContext cntext) {
		context = cntext;
	}

}