package com.bid.java8.lambda;


@FunctionalInterface
interface Example1
{
	public String test(String str);
	
}
public class LambdaExample1 {

	public static void main(String[] args) {

		Example1 ex1=(String str)->{
			return "your have entered "+str;
		};
		System.out.println(ex1.test("261"));
	}
}
