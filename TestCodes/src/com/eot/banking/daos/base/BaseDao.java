/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BaseDao.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:20 PM Sambit Created
*/
package com.eot.banking.daos.base;

import java.io.Serializable;
import java.util.List;

import com.eot.entity.Customer;
import com.eot.entity.PendingTransaction;

// TODO: Auto-generated Javadoc
/**
 * The Interface BaseDao.
 */
public interface BaseDao {
	
    /**
	 * Save.
	 * 
	 * @param obj
	 *            the obj
	 * @return the serializable
	 */
    public abstract Serializable save(Object obj);

    /**
	 * Update.
	 * 
	 * @param obj
	 *            the obj
	 */
    public abstract void update(Object obj);

    /**
	 * Delete.
	 * 
	 * @param obj
	 *            the obj
	 */
    public abstract void delete(Object obj);
    
    /**
	 * Save list.
	 * 
	 * @param list
	 *            the list
	 */
    public abstract void saveList(List list);
    
    /**
	 * Flush.
	 */
    public abstract void flush();

}