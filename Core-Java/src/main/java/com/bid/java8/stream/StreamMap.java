package com.bid.java8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamMap {
	
	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
		
//		list.stream().filter((n)->{ return n%2==0;}).map((n->n*10)).forEach(System.out::println);
		Map<Integer, Integer> map=list.stream().filter((n)->{ return n%2==0;}).collect(Collectors.toMap(n->n, n->n*10));
		System.out.println(map);
	}

}
