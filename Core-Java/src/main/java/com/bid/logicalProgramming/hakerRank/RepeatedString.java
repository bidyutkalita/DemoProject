package com.bid.logicalProgramming.hakerRank;


public class RepeatedString
{

	public static void main(String[] args){
		
	}
	 static long repeatedString(String s, long n) {
		 
		 long remainder=n%s.length();
		 long required_number_of_str=n/s.length();
		 
		 int count_of_a_in_string = 0;
		 int count_of_a_in_sub_string=0;
		 for(int i=0;i<remainder;i++)
		 {
			 if(s.charAt(i)=='a')
				 count_of_a_in_sub_string++;
		 }
		 for(int i=0;i<s.length();i++)
		 {
			 if(s.charAt(i)=='a')
				 count_of_a_in_string++;
		 }
			 return (required_number_of_str*count_of_a_in_string)+count_of_a_in_sub_string;

	    }
}
