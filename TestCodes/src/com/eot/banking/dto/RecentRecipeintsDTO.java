package com.eot.banking.dto;

import java.util.List;

public class RecentRecipeintsDTO extends TransactionBaseDTO {
	
	List<RecentRecipeintsDTO> recentReciepentList;

	public List<RecentRecipeintsDTO> getRecentReciepentList() {
		return recentReciepentList;
	}

	public void setRecentReciepentList(List<RecentRecipeintsDTO> recentReciepentList) {
		this.recentReciepentList = recentReciepentList;
	}
	

}
