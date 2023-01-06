package com.bid.logicalProgramming.test;

import java.util.ArrayList;
import java.util.List;

public class Test
{

	public static void main(String[] args){
		List list= new ArrayList();
		list.add(0,null);
		list.add(1,null);
		list.add(1,10);
		System.out.println(list);
		list.add(0,11);
		 while (list.remove(null)) { 
	        }
		System.out.println(list);
		
		
	}
}
