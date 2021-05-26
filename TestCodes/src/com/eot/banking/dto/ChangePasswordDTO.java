package com.eot.banking.dto;


/**
 * @author Naqui
 *
 */
public class ChangePasswordDTO extends TransactionBaseDTO {

	private String oldPassword;
	private String newPassword;
	
	
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
}
