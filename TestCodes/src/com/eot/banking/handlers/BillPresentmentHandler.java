/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: BillPresentmentHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:41 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.utils.DateUtil;
import com.eot.entity.Biller;
import com.eot.entity.SenelecBills;

// TODO: Auto-generated Javadoc
/**
 * The Class BillPresentmentHandler.
 */
public class BillPresentmentHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public  byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData) throws EOTException {

		System.out.println("******** BillPresentmentHandler *************");

		String policyNumber = new String(plainData[dataOffset++]);
		String billerId = new String(plainData[dataOffset++]);

		Biller biller = eotMobileDao.getBiller(Integer.parseInt(billerId));
		
		if(biller == null){
			throw new EOTException(ErrorConstants.INVALID_BILLER);
		}

		SenelecBills bill = eotMobileDao.getElectricityBillFromPolicyId(policyNumber);

		if(bill==null){
			throw new EOTException(ErrorConstants.NO_BILLS_FOUND);
		}

		try {
			return packResponse(bill,biller.getPartialPayments(),customer.getDefaultLanguage());
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}	

	}

	/**
	 * Pack response.
	 * 
	 * @param senelecBill
	 *            the senelec bill
	 * @param paymentType
	 *            the payment type
	 * @param defaultLang
	 *            the default lang
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(SenelecBills senelecBill , Integer paymentType,String defaultLang) throws UnsupportedEncodingException {

		//	String message = messageSource.getMessage("VIEW_BILL", new String[]{electricityBill.getCustomerName(),electricityBill.getBillerCustomerId(),electricityBill.getMeter(),electricityBill.getPeriod(),electricityBill.getNumberOfDays().toString(),DateUtil.formatDate(electricityBill.getBillDate()),electricityBill.getOutStandingAmount().toString()}, new Locale(defaultLang));

		String message = messageSource.getMessage("VIEW_BILL", new String[]{senelecBill.getSenelecCustomers().getCustomerName().trim(),
				senelecBill.getSenelecCustomers().getPolicyNumber(),DateUtil.formatDate(senelecBill.getPaymentDate()),DateUtil.formatDate(senelecBill.getExtensionDate()),
				senelecBill.getRecordAmount().intValue()+"",paymentType+""}, new Locale(defaultLang));

		return new byte[][] {message.getBytes("UTF-8")};

	}

}
