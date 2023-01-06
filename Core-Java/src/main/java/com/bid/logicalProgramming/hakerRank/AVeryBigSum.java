package com.bid.logicalProgramming.hakerRank;


public class AVeryBigSum
{
	public static void main(String[] args){
		long [] ar= {10,12,13};
		System.out.println(aVeryBigSum(ar));
	}
	static long aVeryBigSum(long[] ar) {

		for(int i=1;i<ar.length;i++)
		{
			ar[0]+=ar[i];
		}

		return ar[0];
    }

}
