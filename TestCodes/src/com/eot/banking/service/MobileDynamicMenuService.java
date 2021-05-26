package com.eot.banking.service;

import java.util.Map;

import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.MobileMenuMasterDataDTO;
import com.eot.banking.exception.EOTException;
import com.eot.coreclient.EOTCoreException;
import com.eot.entity.Mobiletheamcolorconfig;

public interface MobileDynamicMenuService {
	
	public MobileMenuMasterDataDTO loadMobileMenu(MasterDataDTO masterDataDTO)  throws EOTException;

	Map loadTheamColorConfig(MasterDataDTO masterDataDTO) throws EOTException;


}
