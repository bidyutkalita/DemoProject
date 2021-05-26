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
import java.util.ArrayList;

public class FAQsModelDTO extends TransactionBaseDTO {
    
    
    private ArrayList<FAQ> FAQsList;
    
    public ArrayList<FAQ> getFAQsList() {
        return FAQsList;
    }
    
    public void setFAQsList(ArrayList<FAQ> FAQsList) {
        this.FAQsList = FAQsList;
    }
    
    
   public class FAQ{
	   
	   private Integer id; 
	   private String question;
        private String answer;
    
        public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getQuestion() {
            return question;
        }
    
        public void setQuestion(String question) {
            this.question = question;
        }
    
        public String getAnswer() {
            return answer;
        }
    
        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
    
    
}

