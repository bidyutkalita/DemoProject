package com.bid.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class HelloStreamEX2 {
	public static void main(String[] args) {
		List<String> strList = new ArrayList<String>();
		strList.add("bidyut");
		strList.add("ashim");
		strList.add("dibakar");
		strList.add("gita");
		strList.add("kalita");

		Stream<String> stream = strList.stream();

		stream.map((mapper) -> {
			return mapper.toUpperCase();

		}).filter((predicate)->{
			return predicate.startsWith("B");
		}).forEach((value) -> {
			System.out.println(value);
		});

	}

}
