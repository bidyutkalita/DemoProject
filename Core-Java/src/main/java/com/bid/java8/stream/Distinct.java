package com.bid.java8.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Distinct {
	
	public static void main(String[] args) {
		List<String> strList = new ArrayList<String>();
		strList.add("bidyut");
		strList.add("ashim");
		strList.add("dibakar");
		strList.add("gita");
		strList.add("kalita");
		strList.add("kalita");
		
		Stream<String> listStream=strList.stream();
		//style 1
//		listStream.distinct().forEach((action)->{System.out.println(action);});
		//style 2
//		List lst=listStream.distinct().collect(Collectors.toList());
//		System.out.println(lst);
		
		listStream.flatMap(str-> Stream.of(str.toUpperCase())).forEach(System.out::println);
	}

}
