/* Copyright © EasOfTech 2015. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of EasOfTech. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms and
 * conditions entered into with EasOfTech.
 *
 * Id: ChangePinHandler.java,v 1.0
 *
 * Date Author Changes
 * 21 Oct, 2015, 2:59:44 PM Sambit Created
 */
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.dto.LocateUsDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.coreclient.webservice.UtilityServicesCleintSub;
import com.eot.entity.Bank;
import com.eot.entity.Branch;
import com.eot.entity.Country;
import com.eot.entity.CustomerAccount;
import com.eot.entity.LocateUS;

/**
 * The Class ChangePinHandler.
 */
public class LocationHandler extends BaseHandler {

	/** The utility services cleint sub. */
	@Autowired
	private UtilityServicesCleintSub utilityServicesCleintSub;

	/**
	 * Sets the utility services cleint sub.
	 * 
	 * @param utilityServicesCleintSub
	 *            the new utility services cleint sub
	 */
	public void setUtilityServicesCleintSub( UtilityServicesCleintSub utilityServicesCleintSub ) {
		this.utilityServicesCleintSub = utilityServicesCleintSub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData) throws EOTException {

		System.out.println("******** LocationHandler *************");

		String accountAlias = new String(plainData[dataOffset++]);
		LocateUsDTO locateUsDTO = new LocateUsDTO();
		locateUsDTO.setLocationTypeId(Integer.parseInt(new String(plainData[dataOffset++])));
		locateUsDTO.setCountryId(Integer.parseInt(new String(plainData[dataOffset++])));
		locateUsDTO.setCityId(Integer.parseInt(new String(plainData[dataOffset++])));
		locateUsDTO.setQuaterId(Integer.parseInt(new String(plainData[dataOffset++])));
		locateUsDTO.setBankId(new String(plainData[dataOffset++]));

		CustomerAccount account = eotMobileDao.getAccountFromAccountAlias(customer.getCustomerId(), accountAlias);
		if(account == null){
			throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
		}
		
		Country country = eotMobileDao.getCountry(locateUsDTO.getCountryId());
		
		if(country != null){
			locateUsDTO.setCountryId(country.getCountryCodeNumeric());
		}
		
		Bank bank = eotMobileDao.getBankFromBankId(Integer.parseInt(locateUsDTO.getBankId()));
		
		if(bank != null){
			locateUsDTO.setBankId(bank.getBankCode());
		}
		
		if(locateUsDTO.getLocationTypeId().equals(Constants.LOCATION_TYPE_BRANCH)){
			List<Branch> branchs = eotMobileDao.getBranchList(locateUsDTO);
			if(null == branchs){
				throw new EOTException(ErrorConstants.LOCATION_DETAILS_NOT_AVAILABLE);
			}
			try {
				return packLocateBranch(branchs);
			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
				throw new EOTException(ErrorConstants.SERVICE_ERROR);
			}
		}else{
			List<LocateUS> locateUSList = eotMobileDao.getServiceLocation(locateUsDTO);
			if(null == locateUSList){
				throw new EOTException(ErrorConstants.LOCATION_DETAILS_NOT_AVAILABLE);
			}
			try {
				return packLocateUs(locateUSList);
			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
				throw new EOTException(ErrorConstants.SERVICE_ERROR);
			}
		}
	}

	/**
	 * Pack response.
	 *
	 * @param locateUSList the locate US list
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public byte[][] packLocateUs(List<LocateUS> locateUSList) throws UnsupportedEncodingException {

		ArrayList<String> data = new ArrayList<String>();
		data.add(locateUSList.size() + "");
		for(LocateUS locateUS : locateUSList){
			data.add(locateUS.getAddress());
		}
		System.out.println(data);
		byte[][] response = new byte[data.size()][];
		for(int i = 0; i < response.length; i++) {
			response[i] = data.get(i).getBytes("UTF8");
		}

		return response;
	}
	
	/**
	 * Pack locate branch.
	 *
	 * @param branchs the branchs
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public byte[][] packLocateBranch(List<Branch> branchs) throws UnsupportedEncodingException {

		ArrayList<String> data = new ArrayList<String>();
		data.add(branchs.size() + "");
		for(Branch branch : branchs){
			data.add(branch.getAddress());
		}
		System.out.println(data);
		byte[][] response = new byte[data.size()][];
		for(int i = 0; i < response.length; i++) {
			response[i] = data.get(i).getBytes("UTF8");
		}

		return response;
	}

}
