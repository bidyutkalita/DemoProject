/* Copyright © EasOfTech 2016. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: IntegrationService.java
*
* Date Author Changes
* 23 Jan, 2017 Swadhin Created
*/
package com.eot.banking.service;

import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.exception.EOTException;

/**
 * The Interface IntegrationService.
 */
public interface IntegrationService {

	/**
	 * Gets the master data.
	 *
	 * @param masterDataDTO the master data DTO
	 * @return the master data
	 * @throws EOTException the EOT exception
	 */
	void getMasterData(MasterDataDTO masterDataDTO)throws EOTException;
	
}
