package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.List;

public class MobileMenuMasterDataDTO extends TransactionBaseDTO{
	
	private Integer bankId;
	private Integer pofileId;

	private List<MobileDynamicTab> listOfDynamicTabs = new ArrayList<MobileDynamicTab>();


	public List<MobileDynamicTab> getListOfDynamicTabs() {
		return listOfDynamicTabs;
	}

	public void setListOfDynamicTabs(List<MobileDynamicTab> listOfDynamicTabs) {
		this.listOfDynamicTabs = listOfDynamicTabs;
	}

	public Integer getBankId() {
		return bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public Integer getPofileId() {
		return pofileId;
	}

	public void setPofileId(Integer pofileId) {
		this.pofileId = pofileId;
	}


}
