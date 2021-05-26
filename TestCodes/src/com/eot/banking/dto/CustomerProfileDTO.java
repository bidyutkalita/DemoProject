/* Copyright © EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: CustomerProfileDTO.java
*
* Date Author Changes
* 18 Feb, 2016 Swadhin Created
*/
package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomerProfileDTO.
 */
public class CustomerProfileDTO extends TransactionBaseDTO{

	/** The title. */
	private String title;

	/** The first name. */
	private String firstName;

	/** The middle name. */
	private String middleName;

	/** The last name. */
	private String lastName;

	/** The dob. */
	private Long dob;
	
	/** The gender. */
	private String gender;

	/** The address. */
	private String address;

	/** The city id. */
	private Integer cityId ;

	/** The question id. */
	private Integer questionId;

	/** The answer. */
	private String answer;
	
	/** The language. */
	private String languageCode;

	/** The customer photo. */
	private String customerPhoto;

	/** The id proof. */
	private String idProof;

	/** The address proof. */
	private String addressProof;
	
	/** The customer id. */
	private Long customerId;
	
	/** The signature. */
	private String signature;
	
	/** The country id. */
	private Integer countryId;
	
	/** The bank id. */
	private Integer bankId;
	
	/** The bank code. */
	private String bankCode;
	
	/** The branch id. */
	private Long branchId;
	
	/** The profile id. */
	private Integer profileId;
	
	/** The email id. */
	private String emailId;
	
	/** The qaurter id. */
	private Long qaurterId;
	
	/** The cbs branch code. */
	private String cbsBranchCode;
	
	private String kycTypeId;
	
	private int type;
	
	private Double commission;
	
	private String idType;
	
	public String getKycTypeId() {
		return kycTypeId;
	}

	public void setKycTypeId(String kycTypeId) {
		this.kycTypeId = kycTypeId;
	}

	/**
	 * Gets the cbs branch code.
	 *
	 * @return the cbs branch code
	 */
	public String getCbsBranchCode() {
		return cbsBranchCode;
	}

	/**
	 * Sets the cbs branch code.
	 *
	 * @param cbsBranchCode the new cbs branch code
	 */
	public void setCbsBranchCode(String cbsBranchCode) {
		this.cbsBranchCode = cbsBranchCode;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the middle name.
	 *
	 * @return the middle name
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Sets the middle name.
	 *
	 * @param middleName the new middle name
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the dob.
	 *
	 * @return the dob
	 */
	public Long getDob() {
		return dob;
	}

	/**
	 * Sets the dob.
	 *
	 * @param dob the new dob
	 */
	public void setDob(Long dob) {
		this.dob = dob;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the city id.
	 *
	 * @return the city id
	 */
	public Integer getCityId() {
		return cityId;
	}

	/**
	 * Sets the city id.
	 *
	 * @param cityId the new city id
	 */
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	/**
	 * Gets the question id.
	 *
	 * @return the question id
	 */
	public Integer getQuestionId() {
		return questionId;
	}

	/**
	 * Sets the question id.
	 *
	 * @param questionId the new question id
	 */
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	/**
	 * Gets the answer.
	 *
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * Sets the answer.
	 *
	 * @param answer the new answer
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * Gets the customer photo.
	 *
	 * @return the customer photo
	 */
	public String getCustomerPhoto() {
		return customerPhoto;
	}

	/**
	 * Sets the customer photo.
	 *
	 * @param customerPhoto the new customer photo
	 */
	public void setCustomerPhoto(String customerPhoto) {
		this.customerPhoto = customerPhoto;
	}

	/**
	 * Gets the id proof.
	 *
	 * @return the id proof
	 */
	public String getIdProof() {
		return idProof;
	}

	/**
	 * Sets the id proof.
	 *
	 * @param idProof the new id proof
	 */
	public void setIdProof(String idProof) {
		this.idProof = idProof;
	}

	/**
	 * Gets the address proof.
	 *
	 * @return the address proof
	 */
	public String getAddressProof() {
		return addressProof;
	}

	/**
	 * Sets the address proof.
	 *
	 * @param addressProof the new address proof
	 */
	public void setAddressProof(String addressProof) {
		this.addressProof = addressProof;
	}

	
	/**
	 * Gets the signature.
	 *
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Sets the signature.
	 *
	 * @param signature the new signature
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Sets the gender.
	 *
	 * @param gender the new gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Gets the customer id.
	 *
	 * @return the customer id
	 */
	public Long getCustomerId() {
		return customerId;
	}

	/**
	 * Sets the customer id.
	 *
	 * @param customerId the new customer id
	 */
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	/**
	 * Gets the country id.
	 *
	 * @return the country id
	 */
	public Integer getCountryId() {
		return countryId;
	}

	/**
	 * Sets the country id.
	 *
	 * @param countryId the new country id
	 */
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	/**
	 * Gets the branch id.
	 *
	 * @return the branch id
	 */
	public Long getBranchId() {
		return branchId;
	}

	/**
	 * Sets the branch id.
	 *
	 * @param branchId the new branch id
	 */
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	/**
	 * Gets the bank id.
	 *
	 * @return the bank id
	 */
	public Integer getBankId() {
		return bankId;
	}

	/**
	 * Sets the bank id.
	 *
	 * @param bankId the new bank id
	 */
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * Gets the profile id.
	 *
	 * @return the profile id
	 */
	public Integer getProfileId() {
		return profileId;
	}

	/**
	 * Sets the profile id.
	 *
	 * @param profileId the new profile id
	 */
	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	/**
	 * Gets the email id.
	 *
	 * @return the email id
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * Sets the email id.
	 *
	 * @param emailId the new email id
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * Gets the qaurter id.
	 *
	 * @return the qaurter id
	 */
	public Long getQaurterId() {
		return qaurterId;
	}

	/**
	 * Sets the qaurter id.
	 *
	 * @param qaurterId the new qaurter id
	 */
	public void setQaurterId(Long qaurterId) {
		this.qaurterId = qaurterId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}


	
	
}
