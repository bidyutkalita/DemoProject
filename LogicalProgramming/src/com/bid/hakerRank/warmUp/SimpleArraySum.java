package com.bid.hakerRank.warmUp;

public class SimpleArraySum {
	public static void main(String[] args) {
		int[] arr= {10, 20, 20,33,23,232,2,222,22222,354554,344};
		System.out.println(simpleArraySum(arr));
	}
	
	/*
     * Complete the simpleArraySum function below.
     */
    static int simpleArraySum(int[] ar) {
    	for(int i=1;i<ar.length;i++)
    	{
    		ar[0]+=ar[i];
    	}
    	return ar[0];
    }

}
