package com.bid.hakerRank.warmUp;

import java.util.ArrayList;
import java.util.List;

public class RankCalculator {
	
	public static void main(String[] args) {
		List<Integer> rankList0 = new ArrayList<Integer>();
		List<Integer> rankList1 = new ArrayList<Integer>();
		rankList0.add(5);
		rankList0.add(6);
		rankList0.add(7);
		
		rankList1.add(3);
		rankList1.add(6);
		rankList1.add(10);
		System.out.println(compareTriplets(rankList0,rankList1));
	}
	
	static List<Integer> compareTriplets(List<Integer> a, List<Integer> b) {
		int alice=0,bob=0;
		List<Integer> rankList = new ArrayList<Integer>();
		
		for (int i = 0; i < a.size(); i++) {
			if(a.get(i)>b.get(i))
				alice++;
			else if(a.get(i)<b.get(i))
				bob++;
		}
		rankList.add(0,alice);
		rankList.add(1, bob);
		return rankList;

    }

}
