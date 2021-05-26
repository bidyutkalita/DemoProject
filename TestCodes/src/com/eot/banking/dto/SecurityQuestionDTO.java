package com.eot.banking.dto;

import java.util.Set;

public class SecurityQuestionDTO {

	private Integer questionId;
	private String question;
	private String locale;
	private Integer active;
	private Set customerSecurityAnswers;
	
	public Integer getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public Integer getActive() {
		return active;
	}
	public void setActive(Integer active) {
		this.active = active;
	}
	public Set getCustomerSecurityAnswers() {
		return customerSecurityAnswers;
	}
	public void setCustomerSecurityAnswers(Set customerSecurityAnswers) {
		this.customerSecurityAnswers = customerSecurityAnswers;
	}
	
	
}
