/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: SMSCashDTO.java,v 1.0
*
* Date Author Changes
* 21 Oct, 2015, 2:59:05 PM Sambit Created
*/
package com.eot.banking.dto;

import java.util.List;

import com.eot.entity.HelpDesk;

import java.util.ArrayList;

public class HelpDeskModelDTO extends TransactionBaseDTO {
    
    
    private List<HelpDesk> helpDeskList;

	public List<HelpDesk> getHelpDeskList() {
		return helpDeskList;
	}

	public void setHelpDeskList(List<HelpDesk> helpDeskList) {
		this.helpDeskList = helpDeskList;
	}
        
}

