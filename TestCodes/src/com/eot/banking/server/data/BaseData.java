/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BaseData.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:22 PM Sambit Created
*/
package com.eot.banking.server.data;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseData.
 */
public class BaseData {

	/** The id. */
	public int id;
	
	/** The name. */
	public String name;
	
	/** The display name. */
	public String displayName;
	
	/**
	 * Instantiates a new base data.
	 * 
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param displayName
	 *            the display name
	 */
	public BaseData(int id, String name, 
			String displayName) {
		
		this.id = id;
		this.name = name;
		this.displayName = displayName;
	}
	
}
