package com.eot.banking.service.impl;

import java.text.DecimalFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eot.banking.common.EOTConstants;
import com.eot.banking.dto.CustomerProfileDTO;
import com.eot.banking.dto.LoginDTO;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.server.Constants;
import com.eot.banking.service.LoginService;
import com.eot.entity.AppMaster;
import com.eot.entity.Customer;
import com.eot.entity.CustomerScsubscription;
import com.security.kms.KMS;

@Service
public class LoginServiceImpl implements LoginService {
	
	@Autowired
	private BaseServiceImpl baseServiceImpl;
	


	@Override
	public LoginDTO login(LoginDTO loginDTO) throws EOTException {
		
		Customer customer = validate(loginDTO);
		loginDTO.setUserType(customer.getType());
	//	CustomerProfileDTO customerProfileDTO = setCustomerprofile(customer);
		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(customer.getAppId());
	/*	appMaster.setStatus(20);
		baseServiceImpl.eotMobileDao.save(appMaster);*/
		
		loginDTO.setApplicationStatus(appMaster.getStatus());
		loginDTO.setCustomerStatus(customer.getActive());
		loginDTO.setUserType(customer.getType());
		loginDTO.setAppType(customer.getType());
		loginDTO.setApplicationId(customer.getAppId());
		loginDTO.setCustomerName(customer.getFirstName());
		loginDTO.setIs_logged(true);
		loginDTO.setStatus(0);
		loginDTO.setAgentCode(customer.getAgentCode());
		loginDTO.setMobileNumber(customer.getMobileNumber());
		loginDTO.setApplicationId(customer.getAppId());
		loginDTO.setMessageDescription(baseServiceImpl.messageSource.getMessage("LOGIN_SUCCESS", null, new Locale(StringUtils.isNotEmpty(loginDTO.getDefaultLocale()) ? loginDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));
		loginDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("LOGIN_SUCCESS", null, new Locale(StringUtils.isNotEmpty(loginDTO.getDefaultLocale()) ? loginDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));

		return loginDTO;
	}


	private Customer validate(LoginDTO loginDTO) throws EOTException {
		
		
		if (null == loginDTO.getActivationPIN()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "PIN");
		}
		
		/*if (null == loginDTO.getMobileNumber()) {
			
			throw new EOTException(ErrorConstants.FIELD_NON_EMPTY ,  "Mobile number");
		}*/
		
		//Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobile(loginDTO.getMobileNumber());	
		//Customer customer = baseServiceImpl.eotMobileDao.getCustomerByMobile(loginDTO.getMobileNumber());	
		Customer customer = baseServiceImpl.eotMobileDao.getCustomer(loginDTO.getApplicationId());
		
		AppMaster app = baseServiceImpl.eotMobileDao.getApplicationType(customer.getAppId());
		/*if(app.getStatus()==Constants.ACTIVATION_REQ)
		{
			throw new EOTException(ErrorConstants.APPLICATION_BLOCKED);
		}*/
		if(Integer.valueOf(app.getStatus()).equals(EOTConstants.APP_STATUS_BLOCKED)){
			
			throw new EOTException(ErrorConstants.MAX_INCORRECT_LOGIN_ATTEMPT);
		}
		if (Integer.valueOf(customer.getLoginAttempts()).equals(EOTConstants.LOGIN_ATTEMPTS)) {

			app.setStatus(EOTConstants.APP_STATUS_BLOCKED);
			baseServiceImpl.eotMobileDao.update(app);

			// throw new EOTException(ErrorConstants.MAX_INCORRECT_LOGIN_ATTEMPT);
			throw new EOTException(ErrorConstants.APPLICATION_BLOCKED);
		}
		
		if(customer != null && customer.getLoginPin() !=null) {
			if (customer.getLoginPin().equals(loginDTO.getActivationPIN())) {
				customer.setLoginAttempts(0);
				//	app.setStatus(EOTConstants.APP_STATUS_ACTIVATED);
					baseServiceImpl.eotMobileDao.update(customer);
				
			} else {
				int loginAttempts=0;
				if(customer.getLoginAttempts()>=0)
				{
					 loginAttempts=customer.getLoginAttempts()+1;
					customer.setLoginAttempts(loginAttempts);
					baseServiceImpl.eotMobileDao.update(customer);
				}
				throw new EOTException(ErrorConstants.INCORRECT_LOGIN_ATTEMPT, (EOTConstants.LOGIN_ATTEMPTS.intValue()-loginAttempts)+"");
				
			}
			
		}else {
			
			throw new EOTException(ErrorConstants.INVALID_CUSTOMER);
		}
		
		return customer;
	}
	
	
	private CustomerProfileDTO setCustomerprofile(Customer customer) {
		
		CustomerProfileDTO customerProfileDTO=new CustomerProfileDTO();
		
		customerProfileDTO.setFirstName(customer.getFirstName());
		customerProfileDTO.setMiddleName(customer.getMiddleName());
		customerProfileDTO.setLastName(customer.getLastName());
		customerProfileDTO.setMobileNumber(customer.getMobileNumber());
		customerProfileDTO.setApplicationId(customer.getAppId());
		
		
		return customerProfileDTO;
	}
	
	@Override
	public Customer loadCustomerByMobileNumber(String mobileNumber)
	{
		return baseServiceImpl.eotMobileDao.getCustomerByMobile(mobileNumber);
	}
	@Override
	public Customer loadCustomerByApplicationId(String applicationId)
	{
		return baseServiceImpl.eotMobileDao.getCustomer(applicationId);
	}
	@Override
	public MasterDataDTO validateLoginForInitiate(MasterDataDTO masterDataDTO, Customer customer)  throws EOTException
	{
		String userPinHash = masterDataDTO.getActivationPIN();
		AppMaster app = baseServiceImpl.eotMobileDao.getApplicationType(customer.getAppId());
		/*System.out.println("Activation Pin==>"+userPinHash);
		System.out.println("Activation Pin==>"+customer.getLoginPin());*/
		
		if(Integer.valueOf(app.getStatus()).equals(EOTConstants.APP_STATUS_BLOCKED)){
			
			throw new EOTException(ErrorConstants.APPLICATION_BLOCKED);
		}
		
		if(Integer.valueOf(customer.getLoginAttempts()).equals(EOTConstants.LOGIN_ATTEMPTS)){
			
			app.setStatus(EOTConstants.APP_STATUS_BLOCKED);
			baseServiceImpl.eotMobileDao.update(app);
			
			//throw new EOTException(ErrorConstants.MAX_INCORRECT_LOGIN_ATTEMPT);
			throw new EOTException(ErrorConstants.APPLICATION_BLOCKED);
		}
		
		if( ! userPinHash.equalsIgnoreCase(customer.getLoginPin())){
			
			int loginAttempts = customer.getLoginAttempts();
			customer.setLoginAttempts(++loginAttempts);
			baseServiceImpl.eotMobileDao.update(customer);
			masterDataDTO.setMessageDescription(baseServiceImpl.messageSource.getMessage("ERROR_"+ErrorConstants.INCORRECT_LOGIN_ATTEMPT+"", new String[] { (EOTConstants.LOGIN_ATTEMPTS.intValue()-loginAttempts)+"" }, new Locale(customer.getDefaultLanguage())));
			throw new EOTException(ErrorConstants.INCORRECT_LOGIN_ATTEMPT);
		}
		
		customer.setLoginAttempts(0);
	//	app.setStatus(EOTConstants.APP_STATUS_ACTIVATED);
//		baseServiceImpl.eotMobileDao.update(app);
		baseServiceImpl.eotMobileDao.update(customer);
		
		return masterDataDTO;
	}
	
	
	@Override
	public MasterDataDTO loginMaster(MasterDataDTO masterDataDTO,Customer customer) throws EOTException {
		
	//	CustomerProfileDTO customerProfileDTO = setCustomerprofile(customer);
		AppMaster appMaster = baseServiceImpl.eotMobileDao.getApplicationType(customer.getAppId());
	/*	appMaster.setStatus(20);
		baseServiceImpl.eotMobileDao.save(appMaster);*/
		
		masterDataDTO.setApplicationStatus(appMaster.getStatus());
		masterDataDTO.setCustomerStatus(customer.getActive());
		masterDataDTO.setUserType(customer.getType());
		masterDataDTO.setCustomerName(customer.getFirstName());
		masterDataDTO.setStatus(0);
		masterDataDTO.setAgentCode(customer.getAgentCode());
		masterDataDTO.setMessageDescription(baseServiceImpl.messageSource.getMessage("LOGIN_SUCCESS", null, new Locale(StringUtils.isNotEmpty(masterDataDTO.getDefaultLocale()) ? masterDataDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));
		masterDataDTO.setSuccessResponse(baseServiceImpl.messageSource.getMessage("LOGIN_SUCCESS", null, new Locale(StringUtils.isNotEmpty(masterDataDTO.getDefaultLocale()) ? masterDataDTO.getDefaultLocale() : Constants.DEFAULT_LANGUAGE)));

		return masterDataDTO;
	}
}
