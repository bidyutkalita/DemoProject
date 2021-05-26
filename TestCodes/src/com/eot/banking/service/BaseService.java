/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BaseService.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:42 PM Sambit Created
*/
package com.eot.banking.service;

import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.exception.EOTException;

// TODO: Auto-generated Javadoc
/**
 * The Interface BaseService.
 */
public interface BaseService{

	/**
	 * Handle request.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	void handleRequest(TransactionBaseDTO transactionBaseDTO) throws EOTException;
	
	/**
	 * Update mobile request.
	 * 
	 * @param transactionBaseDTO
	 *            the transaction base dto
	 */
	void updateMobileRequest(TransactionBaseDTO transactionBaseDTO);

}
