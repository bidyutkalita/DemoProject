/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: AppConfigurations.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:02:24 PM Sambit Created
*/
package com.eot.banking.utils;

// TODO: Auto-generated Javadoc
/**
 * The Class AppConfigurations.
 */
public class AppConfigurations {
	
	/** The app download url. */
	private String appDownloadURL ;
	
	/** The locale file download URL. */
	private String localeFileDownloadURL;

	/**
	 * Gets the app download url.
	 * 
	 * @return the app download url
	 */
	public String getAppDownloadURL() {
		return appDownloadURL;
	}

	/**
	 * Sets the app download url.
	 * 
	 * @param appDownloadURL
	 *            the new app download url
	 */
	public void setAppDownloadURL(String appDownloadURL) {
		this.appDownloadURL = appDownloadURL;
	}

	/**
	 * Gets the locale file download URL.
	 *
	 * @return the locale file download URL
	 */
	public String getLocaleFileDownloadURL() {
		return localeFileDownloadURL;
	}

	/**
	 * Sets the locale file download URL.
	 *
	 * @param localeFileDownloadURL the new locale file download URL
	 */
	public void setLocaleFileDownloadURL(String localeFileDownloadURL) {
		this.localeFileDownloadURL = localeFileDownloadURL;
	}
	

}
