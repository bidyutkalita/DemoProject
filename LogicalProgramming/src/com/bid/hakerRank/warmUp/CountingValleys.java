package com.bid.hakerRank.warmUp;

public class CountingValleys {
	public static void main(String[] args) {
//		System.out.println(countingValleys(8,"UDDDUDUU"));
		System.out.println("Number of vallies: "+countingValleys(10,"DUDDDUUDUU"));
	}
	
	// Complete the countingValleys function below.
    static int countingValleys(int n, String s) {

    	int count =0, valies=0,current=0;
    	char path[]=s.toCharArray();
    	boolean flag= false;
    	for(int i=0;i<n;i++)
    	{
    		if(path[i] =='u' || path[i]=='U' )
    			count++;
    		if(path[i] =='D' || path[i]=='d' )
    			count--;
    		if(count<=-1)
    		{
    			flag=true;
    		}
    		if(count==0 && flag==true) {
    			flag=false;
    			valies++;
    		}
    	}
    	return valies;
    }

}
