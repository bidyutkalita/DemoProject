package com.eot.banking.dto;

import java.util.List;

public class MobileDynamicTab {
	private Integer tabIndex;

	private String tabTitle;

	private String tabIconCode;

	private Integer tabId;

	private List<MobileDynamicGridMenu> dynamicGridMenus;

	public Integer getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getTabTitle() {
		return tabTitle;
	}

	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	public String getTabIconCode() {
		return tabIconCode;
	}

	public void setTabIconCode(String tabIconCode) {
		this.tabIconCode = tabIconCode;
	}

	public List<MobileDynamicGridMenu> getDynamicGridMenus() {
		return dynamicGridMenus;
	}

	public void setDynamicGridMenus(List<MobileDynamicGridMenu> dynamicGridMenus) {
		this.dynamicGridMenus = dynamicGridMenus;
	}

	public Integer getTabId() {
		return tabId;
	}

	public void setTabId(Integer tabId) {
		this.tabId = tabId;
	}

}
