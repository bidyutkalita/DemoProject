/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: MiniStatementHandler.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:00:35 PM Sambit Created
*/
package com.eot.banking.handlers;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.utils.DateUtil;
import com.eot.coreclient.EOTCoreException;
import com.eot.coreclient.webservice.BasicBankingServiceClientStub;
import com.eot.dtos.basic.MiniStatementDTO;
import com.eot.entity.CustomerAccount;
import com.eot.entity.CustomerBankAccount;
import com.eot.entity.CustomerCard;
import com.eot.dtos.basic.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class MiniStatementHandler.
 */
public class MiniStatementHandler extends BaseHandler {

	/** The basic banking service client stub. */
	@Autowired
	private BasicBankingServiceClientStub basicBankingServiceClientStub;

	/**
	 * Sets the basic banking service client stub.
	 * 
	 * @param basicBankingServiceClientStub
	 *            the new basic banking service client stub
	 */
	public void setBasicBankingServiceClientStub(BasicBankingServiceClientStub basicBankingServiceClientStub) {
		this.basicBankingServiceClientStub = basicBankingServiceClientStub;
	}

	/* (non-Javadoc)
	 * @see com.eot.banking.handlers.BaseHandler#processRequest(java.lang.String, java.lang.Integer, byte[][])
	 */
	@Override
	public byte[][] processRequest( String applicationId, Integer transactionType, byte[][] plainData ) throws EOTException {

		String accountAlias = new String(plainData[dataOffset++]);
		Integer aliasType = Integer.parseInt(new String(plainData[dataOffset++]));

		com.eot.dtos.common.Account accountDto = new com.eot.dtos.common.Account();

		if( Constants.ALIAS_TYPE_CARD_ACC == aliasType){

			CustomerCard card = eotMobileDao.getCustomerCardFromAlias(customer.getCustomerId(), accountAlias);
			if(card == null){
				throw new EOTException(ErrorConstants.CARD_NOT_FOUND);
			}

			accountDto.setAccountNO(card.getCardNumber());
			accountDto.setBankCode(card.getBank().getBankId().toString());

		} else if( Constants.ALIAS_TYPE_MOBILE_ACC == aliasType ){

			CustomerAccount account = eotMobileDao.getAccountFromAccountAlias(customer.getCustomerId(), accountAlias);
			if(account == null){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getAccountNumber());
			accountDto.setBankCode(account.getBank().getBankId().toString());
			accountDto.setBranchCode(account.getBranch().getBranchId().toString());

		} else if( Constants.ALIAS_TYPE_BANK_ACC == aliasType ){

			CustomerBankAccount account = eotMobileDao.getBankAccountFromAccountAlias(customer.getCustomerId(), accountAlias);
			if(account == null){
				throw new EOTException(ErrorConstants.ACCOUNT_NOT_FOUND);
			}

			accountDto.setAccountNO(account.getBankAccountNumber());
		//	accountDto.setBankCode(account.getBank().getBankId().toString());

		}  

		accountDto.setAccountAlias(accountAlias);
		accountDto.setAccountType(aliasType.toString());

		MiniStatementDTO miniStatementDTO = new MiniStatementDTO();

		miniStatementDTO.setTransactionType(transactionType.toString());
		miniStatementDTO.setCustomerAccount(accountDto);
		miniStatementDTO.setReferenceID(customer.getCustomerId().toString());
		miniStatementDTO.setReferenceType(referenceType);
		miniStatementDTO.setRequestID(requestID.toString());
		miniStatementDTO.setChannelType(Constants.EOT_CHANNEL);
		miniStatementDTO.setAmount(0D);

		try {

			miniStatementDTO = basicBankingServiceClientStub.miniStatement(miniStatementDTO);

			com.eot.entity.Transaction txn = new com.eot.entity.Transaction();
			txn.setTransactionId(new Long(miniStatementDTO.getTransactionNO()));
			mobileRequest.setTransaction(txn);

			if(miniStatementDTO.getTransactionList()== null){
				throw new EOTException(ErrorConstants.NO_TRANSACTION_FOUND);
			}

			List<Transaction> list = Arrays.asList(miniStatementDTO.getTransactionList());

			return packResponse(list);

		} catch (EOTCoreException e) {
//			e.printStackTrace();
			throw new EOTException(Integer.parseInt(e.getMessageKey()));
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			throw new EOTException(ErrorConstants.SERVICE_ERROR);
		}

	}

	/**
	 * Pack response.
	 * 
	 * @param txnList
	 *            the txn list
	 * @return the byte[][]
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public byte[][] packResponse(List<Transaction> txnList) throws UnsupportedEncodingException {
		StringBuffer buff = new StringBuffer();
		for(Transaction txn:txnList){
			String dt = DateUtil.formatDateToStr(txn.getTransDate().getTime());
			buff.append(dt+"|");
			//			buff.append(txn.getTransDesc()+"  "+txn.getAmount()+ " ["+txn.getTransType()+ "]\n");
			buff.append(txn.getTransDesc()+" - ["+txn.getTransType()+"] "+txn.getAmount()+ " INR\n");
		}
		return new byte[][] {buff.toString().getBytes("UTF-8")};
	}

}
