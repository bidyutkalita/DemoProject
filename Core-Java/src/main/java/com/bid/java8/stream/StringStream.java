package com.bid.java8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringStream {
	public static void main(String[] args) {
		Stream<String> names = Stream.of("aBc", "d", "ef");
		
		names.map(m->m.toUpperCase()).forEach(p->System.out.print(p+" "));
		
		System.out.println();
		Stream<String> str = Stream.of("this", "is", "a", "test");
		System.out.println(str.anyMatch(s->s.equalsIgnoreCase("is")));
		
		
		List<Integer> number = Arrays.asList(2,3,4,5); 
		  
	    // demonstration of map method 
	    Map<Integer,Integer> map = number.stream().collect(Collectors.toMap(x->x, x->x*x*x)); 
	    System.out.println(map); 

	/*	System.out.println(names.map(s -> {
			return s.toUpperCase();
		}).collect(Collectors.toList()));*/
	}
}
