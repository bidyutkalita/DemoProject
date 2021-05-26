/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: ActivationService.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:01:39 PM Sambit Created
*/
package com.eot.banking.service;

import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.exception.EOTException;

// TODO: Auto-generated Javadoc
/**
 * The Interface ActivationService.
 */
public interface ActivationService extends BaseService{
	
	/**
	 * Process activation request.
	 * 
	 * @param activationDTO
	 *            the activation dto
	 * @return the master data dto
	 * @throws EOTException
	 *             the eOT exception
	 */
	MasterDataDTO processActivationRequest( MasterDataDTO activationDTO ) throws EOTException;
//	MasterDataDTO processKeyExchangeRequest( MasterDataDTO activationDTO ) throws EOTException;

}
