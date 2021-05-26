/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: RemoteBean.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:02:59 PM Sambit Created
*/
package com.eot.kms;

import gnu.cajo.utils.extra.TransparentItemProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;

import com.security.kms.KMS;
import com.security.kms.security.KMSSecurityException;

// TODO: Auto-generated Javadoc
/**
 * The Class RemoteBean.
 */
public class RemoteBean implements InvocationHandler {

	/** The remote stub. */
	private Object remoteStub;
	
	/** The local proxy. */
	private Object localProxy;
	
	/** The connection url. */
	private String connectionUrl;
	
	/** The interface class. */
	private Class<?> interfaceClass;
	
	/**
	 * Instantiates a new remote bean.
	 * 
	 * @param ip
	 *            the ip
	 * @param port
	 *            the port
	 * @param className
	 *            the class name
	 * @param beanName
	 *            the bean name
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public RemoteBean(String ip, String port, String className, String beanName) throws ClassNotFoundException {
	
		connectionUrl = "//" + ip + ":" + port + "/" + beanName;
		interfaceClass = Class.forName(className);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		try {
			
			if(remoteStub == null) {
				
				initializeRemoteObject();
			}
			return method.invoke(remoteStub, args);
		}
		catch (Exception e) {

			try {
				
				throw getInnermostException(e);
			}
			catch (NoSuchObjectException e1) {
				
				initializeRemoteObject();
				return invoke(proxy, method, args);
			}
			catch (ConnectException e2) {

				throw new KMSSecurityException("Could not connect to remote KMS");
			}
		}
	}
	
	/**
	 * Initialize remote object.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void initializeRemoteObject() throws Exception {

		System.out.println("Initializing remote object. connectionUrl = " + connectionUrl);
		try {
			
			System.out.println("On lookup = " + Naming.lookup(connectionUrl));
		}
		catch (Exception e) {
			
			System.out.println("Unable to lookup. e = " + e.getMessage());
//			e.printStackTrace();
		}
		System.out.println("Remote reference obtained");
		remoteStub = TransparentItemProxy.getItem(connectionUrl, new Class[]{interfaceClass});
		System.out.println("Remote stub configured");
	}
	
	/**
	 * Gets the remote instance.
	 * 
	 * @return the remote instance
	 * @throws Exception
	 *             the exception
	 */
	public Object getRemoteInstance() throws Exception {

		localProxy =  localProxy == null ? 
				localProxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(), 
						new Class[]{interfaceClass}, this) : 
							localProxy;
				
				return localProxy;
	}
	
	/**
	 * Gets the innermost exception.
	 * 
	 * @param allExceptions
	 *            the all exceptions
	 * @return the innermost exception
	 */
	private Throwable getInnermostException(Throwable allExceptions) {

		Throwable lastException = allExceptions;
		
		while(lastException.getCause() != null) {
			
			lastException = lastException.getCause();
		}
		
		System.out.println("lastException = " + lastException );
		return lastException;
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		
		try {
			RemoteBean connection = new RemoteBean("54.93.121.155", "9091", "com.security.kms.KMS", "kms");
			KMS kms = (KMS)connection.getRemoteInstance();
			System.out.println("After getting proxy instance");
			kms.exportRMKPvtKey();
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
}
