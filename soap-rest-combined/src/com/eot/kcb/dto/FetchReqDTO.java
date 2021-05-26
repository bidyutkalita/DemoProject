package com.eot.kcb.dto;

public class FetchReqDTO {
	private String code;
	private String username;
	private String password;
	
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "FetchReqDTO [code=" + code + ", username=" + username + ", password=" + password + "]";
	}
	
	

}
