/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BaseDaoImpl.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:58:23 PM Sambit Created
*/
package com.eot.banking.daos.base;


import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseDaoImpl.
 */
public class BaseDaoImpl extends HibernateDaoSupport implements BaseDao {

    /* (non-Javadoc)
     * @see com.eot.banking.daos.base.BaseDao#save(java.lang.Object)
     */
    public Serializable save(Object obj) {
        return getHibernateTemplate().save(obj);
    }

    /* (non-Javadoc)
     * @see com.eot.banking.daos.base.BaseDao#update(java.lang.Object)
     */
    public void update(Object obj) {
        getHibernateTemplate().update(obj);
    }

    /* (non-Javadoc)
     * @see com.eot.banking.daos.base.BaseDao#delete(java.lang.Object)
     */
    public void delete(Object obj) {
        getHibernateTemplate().delete(obj);
    }

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.base.BaseDao#saveList(java.util.List)
	 */
	public void saveList(List list) {
		getHibernateTemplate().saveOrUpdateAll(list) ;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.daos.base.BaseDao#flush()
	 */
	public void flush() {
		getHibernateTemplate().flush() ;		
	}
}
