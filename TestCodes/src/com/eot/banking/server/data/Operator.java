/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: Operator.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:31 PM Sambit Created
*/
package com.eot.banking.server.data;

// TODO: Auto-generated Javadoc
/**
 * The Class Operator.
 */
public class Operator extends BaseData {

	/** The denominations. */
	public int[] denominations;
	
	/**
	 * Instantiates a new operator.
	 * 
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param displayName
	 *            the display name
	 * @param denominations
	 *            the denominations
	 */
	public Operator(int id, String name, String displayName, int[] denominations ) {
		
		super(id, name, displayName);
		this.denominations = denominations;
	}
}
