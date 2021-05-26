package com.eot.banking.service;

import com.eot.banking.dto.LoginDTO;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.exception.EOTException;
import com.eot.entity.Customer;

public interface LoginService {

	LoginDTO login(LoginDTO loginDTO) throws EOTException;

	Customer loadCustomerByMobileNumber(String mobileNumber);

	MasterDataDTO validateLoginForInitiate(MasterDataDTO masterDataDTO, Customer customer)  throws EOTException;


	MasterDataDTO loginMaster(MasterDataDTO masterDataDTO, Customer customer) throws EOTException;

	Customer loadCustomerByApplicationId(String applicationId);

}
