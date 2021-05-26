package com.eot.banking.common;

import java.util.HashMap;
import java.util.Map;

public enum OtpStatusEnum {

	NEW(0, "New"),
	
	USED(1, "Used"),
	
	EXPIRED(2, "Expired");
	
	/** The name. */
	private String name;

	/** The code. */
	private Integer code;
	
	private static Map<Integer, String> otpStatusMap = new HashMap<Integer, String>();
	
	static {
		for ( OtpStatusEnum status : OtpStatusEnum.values() ) {
			otpStatusMap.put( status.getCode(), status.getName() );
		}
	}

	OtpStatusEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
	
	public static String getEnumText(Integer enumValue) {
		
		for(OtpStatusEnum s:OtpStatusEnum.values())
		{
		if(s.getCode().equals(enumValue))
		
			return s.getName();
		}
		return "";
	}
		
	public static Map<Integer, String> getOtpStatusMap() {
		return otpStatusMap;
	}
	
	public static String getOtpStatusMap( final Integer code ) {
		if ( otpStatusMap.get( code ) != null )  {
			return otpStatusMap.get( code );
		}
		return "";
	}

}
