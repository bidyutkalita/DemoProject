/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SecuredPropertyPlaceholderConfigurer.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:02:48 PM Sambit Created
*/
package com.eot.banking.utils;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

// TODO: Auto-generated Javadoc
/**
 * The Class SecuredPropertyPlaceholderConfigurer.
 */
public class SecuredPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.PropertyResourceConfigurer#convertPropertyValue(java.lang.String)
	 */
	@Override
	protected String convertPropertyValue(String originalValue) {
		
		DesEncrypter enc = new DesEncrypter() ;
		if( originalValue!=null && originalValue.startsWith("<") && originalValue.endsWith(">") ){
			return enc.decrypt(originalValue.substring(1,originalValue.length()-1));
		}else
			return originalValue;
	}

}
