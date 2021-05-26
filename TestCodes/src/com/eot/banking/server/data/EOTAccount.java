/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: EOTAccount.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:28 PM Sambit Created
*/
package com.eot.banking.server.data;

// TODO: Auto-generated Javadoc
/**
 * The Class EOTAccount.
 */
public class EOTAccount {
	
	/** The account alias. */
	public String accountAlias;
	
	/** The branch id. */
	public int branchId;
	
	/**
	 * Instantiates a new eOT account.
	 */
	public EOTAccount() {}
	
	/**
	 * Instantiates a new eOT account.
	 * 
	 * @param accountNo
	 *            the account no
	 * @param branchId
	 *            the branch id
	 */
	public EOTAccount(String accountNo, int branchId) {

		this.accountAlias = accountNo;
		this.branchId = branchId;
	}
}
