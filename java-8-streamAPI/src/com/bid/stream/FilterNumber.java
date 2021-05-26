package com.bid.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterNumber {
	
	public static void main(String[] args) {
		List<Integer> lst = new ArrayList();
		lst.add(5);
		lst.add(6);
		lst.add(7);
		lst.add(8);
		lst.add(9);
		lst.add(10);
		
		Stream<Integer> stream = lst.stream();
//		stream.filter((n)-> {return n%2==0;}).forEach(System.out::println);
		List output =stream.filter((n)-> {return n%2==0;}).collect(Collectors.toList());
		System.out.println(output);
		
	}

}
