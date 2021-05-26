package com.bid.hakerRank.warmUp;

import java.util.HashMap;
import java.util.Scanner;

public class Solution {
	
	public static void main(String[] args) {
//		int[] arr= {10, 20, 20, 10 ,10 ,30 ,50, 10 ,20};
		int[] ar= null;;
		
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter Number of socks");
		ar = new int[sc.nextInt()];
		System.out.println("enter color for each sock");
		for(int i=0;i<ar.length;i++)
		{
			ar[i]=sc.nextInt();
		}
		System.out.println(sockMerchant(ar.length,ar));
	}
	
	public static int sockMerchant(int n,int [] ar)
	{
		HashMap<Integer, Integer> colorMap= new HashMap<Integer, Integer>();
		int count=0,pairOfSoks=0;
		for(int i=0;i<n;i++)
		{
			if(!colorMap.containsKey(ar[i])) {
				count=1;
				for(int j=i+1;j<n;j++)
				{
					if(ar[i]==ar[j])
						count++;
				}
				colorMap.put(ar[i], count/2);
				pairOfSoks=pairOfSoks+(count/2);
			}
		}
		return pairOfSoks;
	}

}
