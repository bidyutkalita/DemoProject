/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: LastTxnReceiptHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:27 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.DateUtil;
import com.eot.entity.MobileRequest;
import com.thinkways.util.TLVUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class LastTxnReceiptHandler.
 */
public class LastTxnReceiptHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public  byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData) throws EOTException {

		System.out.println("******** LastTxnReceiptHandler *************");
		String txnTypeOfLastTransaction = new String(plainData[dataOffset++]);

		byte[] encLoggedData = null;
		byte[] loggedData = null;
		byte[][] temp = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

		List<MobileRequest> mobileRequest = eotMobileDao.lastSuccessfulTransaction(applicationId,Integer.parseInt(txnTypeOfLastTransaction));

		if(mobileRequest == null){
			throw new EOTException(ErrorConstants.LAST_TXN_NOT_AVAILABLE);
		}

		for(int i=0;i<(mobileRequest.size()>5 ? 5 : mobileRequest.size());i++){

			try {
				encLoggedData = mobileRequest.get(i).getResponseString().getBytes(1,(int) mobileRequest.get(i).getResponseString().length());

				loggedData = kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, encLoggedData, false);

				temp = TLVUtil.getAllDataFromLVArray(loggedData, 0) ;

				outputStream.write((DateUtil.dateAndTime(mobileRequest.get(i).getTransactionTime())+"").getBytes());
				outputStream.write("&".getBytes());
				outputStream.write(temp[4]);

				if(i != mobileRequest.size()){
					outputStream.write("|".getBytes());
				}

			} catch (Exception e) {
//				e.printStackTrace();
				throw new EOTException(ErrorConstants.SERVICE_ERROR);
			}

		}

		byte[][] response = new byte[][] {outputStream.toByteArray()};

		return response ;

	}


}
