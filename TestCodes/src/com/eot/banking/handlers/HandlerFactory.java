/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: HandlerFactory.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:02 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.eot.banking.server.ServerException;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Handler objects.
 */
public class HandlerFactory implements ApplicationContextAware {

	/** The applicationcontext. */
	private ApplicationContext applicationcontext;
	
	/** The handler map. */
	private HashMap<Integer, BaseHandler>handlerMap;

	/**
	 * Sets the handler map.
	 * 
	 * @param handlerMap
	 *            the handler map
	 */
	public void setHandlerMap(HashMap<Integer, BaseHandler> handlerMap) {

		this.handlerMap = handlerMap;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {

		this.applicationcontext = applicationcontext;
	}

	/**
	 * Gets the handler.
	 * 
	 * @param requestType
	 *            the request type
	 * @return the handler
	 * @throws ServerException
	 *             the server exception
	 */
	public BaseHandler getHandler( Integer requestType ) throws ServerException{
		return (BaseHandler)applicationcontext.getBean(handlerMap.get(requestType).getClass());
	}
}
