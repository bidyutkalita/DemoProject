package com.bid.logicalProgramming.hakerRank;

public class JumpingOnTheCloud
{

	public static void main(String[] args){
		int arr[] = { 0, 0, 0, 1, 0, 1, 0 };
		System.out.println(jumpingOnClouds(arr));

	}

	static int jumpingOnClouds(int[] c){

		int count = 0;
		int noOfJump = 0;
		int i = 0;
		while (i < c.length - 1) {
			if(i < c.length - 2) {
				if((c[i + 1] == 0 && c[i + 2] == 0) || (c[i + 1] == 1 && c[i + 2] == 0)) {
					noOfJump++;
					i = i + 2;
				}
				else if(c[i + 1] == 0 && c[i + 2] == 1) {
					noOfJump++;
					i = i + 1;
				}
				else if(c[i + 1] == 1 && c[i + 2] == 1) {
					return noOfJump;
				}
			}
			else if(i < c.length - 1) {
				if(c[i + 1] == 0) {
					noOfJump++;

				}
				i++;
			}
		}
		return noOfJump;

	}

}
