package com.bid.lambda;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

interface Test1{
	int add(int a, int b);
}

interface Test2
{
	int iterate(int i);
}
public class HelloLambda2 {

	
	public static void main(String[] args) {
		Test1 t = (a,b)->{return a+b;};
		System.out.println(t.add(4,	 4));
		
		
		
		Test2 t2 = (i)->i;
		System.out.println(t2.iterate(3));
		
	}
	
}
