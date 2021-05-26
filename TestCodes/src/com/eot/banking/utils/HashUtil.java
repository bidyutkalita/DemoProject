/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: HashUtil.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:02:38 PM Sambit Created
*/
package com.eot.banking.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.thinkways.util.HexString;

// TODO: Auto-generated Javadoc
/**
 * The Class HashUtil.
 */
public class HashUtil {
	
	/**
	 * Generate hash.
	 * 
	 * @param data
	 *            the data
	 * @param algorithm
	 *            the algorithm
	 * @return the string
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public static String generateHash(byte[] data,String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException  {

		MessageDigest digest = MessageDigest.getInstance(algorithm);
		byte[] hash = new byte[32];
		digest.update(data, 0, data.length);
		hash = digest.digest();
		return HexString.bufferToHex(hash);

	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		
		try {
			System.out.println(generateHash("1111".getBytes(), "SHA-1"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
}

