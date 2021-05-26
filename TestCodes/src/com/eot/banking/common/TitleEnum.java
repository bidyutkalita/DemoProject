package com.eot.banking.common;

import java.util.HashMap;
import java.util.Map;

public enum TitleEnum {
	
	MR(1, "Mr"),
	
	MRS(2, "Mrs"),
	
	MS(3, "Ms"),
	
	Prof(4, "Prof"),
	
	Dr(5, "Dr");
	
	/** The name. */
	private String name;

	/** The code. */
	private Integer code;
	
	private static Map<Integer, String> titleMap = new HashMap<Integer, String>();
	
	static {
		for ( TitleEnum title : TitleEnum.values() ) {
			titleMap.put( title.getCode(), title.getName() );
		}
	}
	
	TitleEnum(Integer code, String name) {
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
		
		for(TitleEnum s:TitleEnum.values())
		{
		if(s.getCode().equals(enumValue))
		
			return s.getName();
		}
		return "";
	}
		
	public static Map<Integer, String> getTitleMap() {
		return titleMap;
	}
	
	public static String getTitleMap( final Integer code ) {
		if ( titleMap.get( code ) != null )  {
			return titleMap.get( code );
		}
		return "";
	}
}
