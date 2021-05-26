/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: EOTReportsDao.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:13 PM Sambit Created
*/
package com.eot.banking.daos;

import com.eot.banking.daos.base.BaseDao;
import com.eot.banking.server.data.ReportDTO;

// TODO: Auto-generated Javadoc
/**
 * The Interface EOTReportsDao.
 */
public interface EOTReportsDao extends BaseDao{
	
	/**
	 * Find reports.
	 * 
	 * @param reportDto
	 *            the report dto
	 */
	void findReports(ReportDTO reportDto);

}
