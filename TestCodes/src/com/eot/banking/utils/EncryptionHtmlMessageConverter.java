/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: EncryptionHtmlMessageConverter.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 3:02:33 PM Sambit Created
*/
package com.eot.banking.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.eot.banking.dto.TransactionBaseDTO;
import com.eot.banking.server.Constants;
import com.eot.banking.service.BaseService;
import com.eot.crypto.CheckSumUtil;
import com.security.kms.KMS;
import com.security.kms.security.KMSSecurityException;

// TODO: Auto-generated Javadoc
/**
 * The Class EncryptionHtmlMessageConverter.
 */
public class EncryptionHtmlMessageConverter extends AbstractHttpMessageConverter<Object> {
	
	private static final Logger logger = LoggerFactory.getLogger(EncryptionHtmlMessageConverter.class);

	/** The kms handler. */
	private KMS kmsHandler;

	/** The base service. */
	@Autowired
	private BaseService baseService;
	
	/*@Autowired
	private CheckSumUtil checkSumUtils;*/
	
	

	/**
	 * Sets the base service.
	 * 
	 * @param baseService
	 *            the new base service
	 */
	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}

	/** The is secured. */
	private boolean isSecured = true;
	
	private boolean isCheckSumEnabled = true;

	/**
	 * Sets the logger.
	 * 
	 * @param kmsHandler
	 *            the new logger
	 */
	public void setKmsHandler(KMS kmsHandler) {
		this.kmsHandler = kmsHandler;
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#readInternal(java.lang.Class, org.springframework.http.HttpInputMessage)
	 */
	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		try {
			
			boolean checkSumFlag=false;
			logger.info("request receive from external (MRH) "+printCurrentTime());
			byte[] payload = IOUtils.toByteArray(inputMessage.getBody()) ;
			HttpHeaders httpHeaders = inputMessage.getHeaders();
			String applicationId = httpHeaders.getFirst("applicationId");
			/*if(applicationId.isEmpty())
			{
				isSecured=false;
			}*/
			byte[] encPayload = null;
			System.out.println("payload: "+payload.length);
			
			if (isSecured) {

				encPayload = Base64.decode(payload) ;

				payload = kmsHandler.desOperation(applicationId, Constants.KEY_VERSION, Constants.KEY_OWNER, encPayload , false);

			}
			TransactionBaseDTO transactionBaseDTO = (TransactionBaseDTO) new JSONAdaptor().fromJSON(new String(payload), clazz);
			
			logger.info("transaction Type:=> "+transactionBaseDTO.getTransactionType());
			
			if (isCheckSumEnabled) {
				try
				{
					//byte[] encPayLoad = transactionBaseDTO.getEncPayload();
					String checkSumString = 	transactionBaseDTO.getCheckSumString();
					System.out.println("checksum String :"+checkSumString);
					
					
					TransactionBaseDTO checkSumDTO =  new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO),TransactionBaseDTO.class); 
					checkSumDTO.setEncPayload(null);
					checkSumDTO.setCheckSumString(null);
					checkSumFlag=	CheckSumUtil.getCheckSumUtil().verifycheckSum(transactionBaseDTO.getMasterKey(), checkSumDTO, checkSumString);
					
					if(!checkSumFlag)
					{
						transactionBaseDTO.setMessageDescription("request interrupted");
						throw new HttpMessageNotReadableException("checkSum false ");
					}
						
				}catch (Exception e) {
//					e.printStackTrace();
					throw new HttpMessageNotReadableException("checkSum failed " + e.getMessage(), e);
				}


			}
			
			transactionBaseDTO.setApplicationId(applicationId);
			transactionBaseDTO.setEncPayload(payload);

			return transactionBaseDTO;

		} catch (IOException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		} catch (KMSSecurityException e) {
			throw new HttpMessageNotReadableException("Could not decrypt Payload: " + e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal(java.lang.Object, org.springframework.http.HttpOutputMessage)
	 */
	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		System.out.println("response to external");
		String jsonString = null ;
		try {
			jsonString = new JSONAdaptor().toJSON(object) ;
		}catch (Exception e) {
//			e.printStackTrace();
		}
		/*if (!isCheckSumEnabled)
			System.out.println(jsonString);*/

		byte[] payload = null ;
		byte[] encPayload = null;

		TransactionBaseDTO transactionBaseDTO = ((TransactionBaseDTO) object);
		transactionBaseDTO.setActivationPIN(null);
		
		if (isCheckSumEnabled) {
			try {
				String masterKey = generateCheckSumKey();
				transactionBaseDTO.setEncPayload(null);
				transactionBaseDTO.setCheckSumString(null);
				transactionBaseDTO.setMasterKey(masterKey);

				TransactionBaseDTO baseDTO2 = new JSONAdaptor().fromJSON(new JSONAdaptor().toJSON(transactionBaseDTO), TransactionBaseDTO.class);
				String checkSumString = CheckSumUtil.getCheckSumUtil().genrateCheckSum(masterKey, baseDTO2);
				transactionBaseDTO.setCheckSumString(checkSumString);
				jsonString = new JSONAdaptor().toJSON(transactionBaseDTO);
				System.out.println(jsonString);

			} catch (Exception e) {
//				e.printStackTrace();
				throw new HttpMessageNotReadableException("checkSum fail ", e);
			}
		}

		if (isSecured) {
			try {
				encPayload = kmsHandler.desOperation(transactionBaseDTO.getApplicationId(), Constants.KEY_VERSION, Constants.KEY_OWNER, jsonString.getBytes(), true);
				payload = Base64.encode(encPayload);
			} catch (KMSSecurityException e) {
				throw new HttpMessageNotWritableException("Could not encrypt Payload: " + e.getMessage(), e);
			}
		} else {
			payload = jsonString.getBytes();
		}
		
		transactionBaseDTO.setEncPayload(payload);
		baseService.updateMobileRequest(transactionBaseDTO);
		transactionBaseDTO.setEncPayload(null);
		
		
		logger.info("response to external (MRH): "+printCurrentTime());
		IOUtils.write(payload, outputMessage.getBody());
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#supports(java.lang.Class)
	 */
	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}
	
	public static String generateCheckSumKey(){
        Random random =new Random();
        String alphabet = "qwertyuioplkjhgfdsazxcvbnm";
        int size = 16;
        char [] chars =new char[size];
        for (int i = 0; i < size; i++) {
            chars [i]= alphabet.charAt(random.nextInt(alphabet.length()));
        }
        return new String(chars);
    }

	/*public static void main(String[] args) throws Exception {
		BalanceEnquiryDTO balanceEnquiryDTO = new BalanceEnquiryDTO();
		balanceEnquiryDTO.setAvailableBalance(26668.0);
		balanceEnquiryDTO.setAccountAlias("EOT Mobile - Parker National");
		balanceEnquiryDTO.setAliasType(1);
		balanceEnquiryDTO.setTransmissionTime(1553678032677L);
		balanceEnquiryDTO.setTransactionTime(1553678032677L);
		balanceEnquiryDTO.setStan(81l);
		balanceEnquiryDTO.setRrn(81l);
		balanceEnquiryDTO.setApplicationId("1babb33338a84473");
		balanceEnquiryDTO.setDefaultLocale("en_US");
		balanceEnquiryDTO.setTransactionType(30);
		balanceEnquiryDTO.setVersionNumber("v1.0.13");
		balanceEnquiryDTO.setSuccessResponse("26668 SSP");
		balanceEnquiryDTO.setRequestID(74666L);
		balanceEnquiryDTO.setServerPKWrapKeyRequired(false);
		balanceEnquiryDTO.setRequestStan(0);
		balanceEnquiryDTO.setRequestType(30);
		balanceEnquiryDTO.setMasterKey("iketgwdbsounutgf");
		
		System.out.println(CheckSumUtil.getCheckSumUtil().verifycheckSum("iketgwdbsounutgf", balanceEnquiryDTO, "SYQoCubaFZSSPrRgMrI/g5wGG4uRtLZz5CAWlku2Odp0Dd/uWDB0xU3P1qzwJe8aUdBBXONnQ/1CP2WqIYdDknxUy+eqUqyVxwtkMeKineI="));
	}*/

	private String printCurrentTime() {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Calendar cal = Calendar.getInstance();
		String stringDate2 = sdf.format(cal.getTime());
		return stringDate2;
	}
	
}



