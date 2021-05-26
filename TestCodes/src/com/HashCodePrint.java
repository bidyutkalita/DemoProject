package com;

import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HashCodePrint {
	public static void main(String[] args) {
//		System.out.println("b".hashCode());
		
		//RFC date format --> http-date 
		System.out.println(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)));
		
		Double d=new Double(new DecimalFormat("#0.00").format(1000.00));
		System.out.println(d);
	}

}
