package com.eot.banking.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.jstl.core.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eot.banking.daos.EOTMobileDao;
import com.eot.banking.dto.MasterDataDTO;
import com.eot.banking.dto.MobileDynamicGridMenu;
import com.eot.banking.dto.MobileDynamicTab;
import com.eot.banking.dto.MobileMenuMasterDataDTO;
import com.eot.banking.exception.EOTException;
import com.eot.banking.exception.ErrorConstants;
import com.eot.banking.service.MobileDynamicMenuService;
import com.eot.coreclient.EOTCoreException;
import com.eot.entity.Customer;
import com.eot.entity.CustomerAccount;
import com.eot.entity.MobileMenuConfiguration;
import com.eot.entity.MobileMenuLanguage;
import com.eot.entity.Mobiletheamcolorconfig;

@Service("mobileDynamicMenuService")
public class MobileDynamicMenuServiceImpl implements MobileDynamicMenuService {

	@Autowired
	protected EOTMobileDao eotMobileDao;

	@Override
	public MobileMenuMasterDataDTO loadMobileMenu(MasterDataDTO masterDataDTO) throws  EOTException {
		
		
		Customer customer=eotMobileDao.getCustomer(masterDataDTO.getApplicationId());
		CustomerAccount customrAccount=eotMobileDao.getCustomerAccounts(customer.getCustomerId()).get(0);
		
		MobileMenuMasterDataDTO menuMasterDataDTO = new MobileMenuMasterDataDTO();
		menuMasterDataDTO.setBankId(customrAccount.getBank().getBankId());
		menuMasterDataDTO.setPofileId(customer.getCustomerProfiles().getProfileId());
		menuMasterDataDTO.setAppType(customer.getType());
		
		List<MobileMenuConfiguration> mobileMenuConf = eotMobileDao.loadMobileMenus(menuMasterDataDTO);
		
		if(mobileMenuConf==null)
			throw new  EOTException(ErrorConstants.MOBILE_MENU_NNOT_FOUND);

		MobileMenuMasterDataDTO responseMasterMenu = new MobileMenuMasterDataDTO();

		List<MobileDynamicTab> mobileTabList = new ArrayList<MobileDynamicTab>();

		for (int i = 0; i < mobileMenuConf.size(); i++) {
			MobileMenuConfiguration configuration = mobileMenuConf.get(i);
			if (configuration.getMobileMasterMenu().getMmTypeId()== 1) {

				MobileDynamicTab tab = new MobileDynamicTab();
				if(configuration.getMobileMasterIcon()!=null) {
					tab.setTabIconCode(configuration.getMobileMasterIcon().getIconCode());
				}
				tab.setTabIndex(configuration.getMobileMasterMenu().getMmTabType());
				tab.setTabId(configuration.getMobileMasterMenu().getMmId());
				tab.setTabTitle(configuration.getMobileMasterMenu().getMmName());
				Set<MobileMenuLanguage> languages = configuration.getMobileMasterMenu().getMobileMenuLanguages();
				if(languages!=null)
				{
					for(MobileMenuLanguage lang : languages){
						  if(masterDataDTO.getDefaultLocale().equals(lang.getLanguageCode()))
								tab.setTabTitle(lang.getMenuName());
						}
				}
				
				mobileTabList.add(tab);
			}
		}

		for (int i = 0; i < mobileTabList.size(); i++) {
			List<MobileDynamicGridMenu> gridMenuList = new ArrayList<MobileDynamicGridMenu>();
			for (int j = 0; j < mobileMenuConf.size(); j++) {
				MobileMenuConfiguration configuration = mobileMenuConf.get(j);
				if (mobileTabList.get(i).getTabId() == configuration.getTabTypeId()) {
					if (configuration.getMobileMasterMenu().getMmTypeId() != 1) {
						MobileDynamicGridMenu gridMenu = new MobileDynamicGridMenu();
						gridMenu.setGridMenuFunctionalCode(configuration.getMobileMasterMenu().getFunctionalCode());
						gridMenu.setGridMenuIconCode(configuration.getMobileMasterIcon().getIconCode());
						gridMenu.setGridMenuIndex(configuration.getMobileIndex());
						gridMenu.setGridMenuTitle(configuration.getMobileMasterMenu().getMmName());
						Set<MobileMenuLanguage> languages = configuration.getMobileMasterMenu().getMobileMenuLanguages();
						if(languages!=null)
						{
							for(MobileMenuLanguage lang : languages){
								  if(masterDataDTO.getDefaultLocale().equals(lang.getLanguageCode()))
									  gridMenu.setGridMenuTitle(lang.getMenuName());
								}
						}
						gridMenuList.add(gridMenu);
					}
					mobileTabList.get(i).setDynamicGridMenus(gridMenuList);
				}
			}
		}

		responseMasterMenu.setListOfDynamicTabs(mobileTabList);
		return responseMasterMenu;
	}
	
	@Override
	public Map loadTheamColorConfig(MasterDataDTO masterDataDTO) throws  EOTException {
		
		
		Map<String, String> colorConfigMap = new HashMap<String, String>();
		
		Customer customer=eotMobileDao.getCustomer(masterDataDTO.getApplicationId());
		CustomerAccount customrAccount=eotMobileDao.getCustomerAccounts(customer.getCustomerId()).get(0);
		
		MobileMenuMasterDataDTO menuMasterDataDTO = new MobileMenuMasterDataDTO();
		menuMasterDataDTO.setBankId(customrAccount.getBank().getBankId());
		menuMasterDataDTO.setPofileId(customer.getCustomerProfiles().getProfileId());
		menuMasterDataDTO.setAppType(customer.getType());
		
		Mobiletheamcolorconfig mobileTheamConfig = eotMobileDao.loadMobileTheameColors(menuMasterDataDTO);
		
		if(mobileTheamConfig!=null)
		{
			colorConfigMap.put("TOOL_BAR_BG_COLOR", mobileTheamConfig.getToolBarColor());
			colorConfigMap.put("TAB_SELECTED_COLOR", mobileTheamConfig.getTabColorSelected());
			colorConfigMap.put("TAB_UNSELECTED_COLOR", mobileTheamConfig.getTabColorUnselected());
			colorConfigMap.put("GRID_SELECTED_COLOR", mobileTheamConfig.getGridColorSelected());
			colorConfigMap.put("GRID_UNSELECTED_COLOR", mobileTheamConfig.getGridColorUnselected());
			colorConfigMap.put("SCREEN_BG_COLOR", mobileTheamConfig.getScreenBgColor());
			colorConfigMap.put("LIST_HEADER_BG_COLOR", mobileTheamConfig.getListHeaderColor());
			colorConfigMap.put("TEXT_COLOR", mobileTheamConfig.getTextColor());
		}
		
		return colorConfigMap;

	}

	
}
