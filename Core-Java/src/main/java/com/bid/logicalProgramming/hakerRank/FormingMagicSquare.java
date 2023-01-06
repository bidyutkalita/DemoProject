package com.bid.logicalProgramming.hakerRank;

public class FormingMagicSquare
{

	static int formingMagicSquare(int[][] s) {
		int cost=Integer.MAX_VALUE;
		int sourceMatrix[][][]= { 
				{{8,1,6},{3,5,7},{4,9,2}},
				{{6,1,8},{7,5,3},{2,9,4}},
				{{4,9,2},{3,5,7},{8,1,6}},
				{{2,9,4},{7,5,3},{6,1,8}},
				{{8,3,4},{1,5,9},{6,7,2}},
				{{4,3,8},{9,5,1},{2,7,6}},
				{{6,7,2},{1,5,9},{8,3,4}},
				{{2,7,6},{9,5,1},{4,3,8}} 
				};
		
		for(int i=0;i<sourceMatrix.length;i++)
		{
			int currentCost=calculateCost(sourceMatrix[i], s);
			if(currentCost<cost)
				cost=currentCost;
		}
		
		return cost;

    }
	public static int calculateCost(int surce[][],int mat2[][])
	{
		int cost=0;
		for(int i=0;i<surce.length;i++)
		{
			for(int j=0;j<surce[i].length;j++)
			{
				cost+=Math.abs(surce[i][j]-mat2[i][j]);
			}
			System.out.println();
		}
		
		return cost;
	}
	public static void main(String[] args){
		
		
		int mat[][] =new int [3][3];
		int a[]= {2,5,7};
		int b[][]={{2,3,4},{1,3,4}};
		int sourceMatrix[][][]= { 
					{{8,1,6},{3,5,7},{4,9,2}},
					{{6,1,8},{7,5,3},{2,9,4}},
					{{4,9,2},{3,5,7},{8,1,6}},
					{{2,9,4},{7,5,3},{6,1,8}},
					{{8,3,4},{1,5,9},{6,7,2}},
					{{4,3,8},{9,5,1},{2,7,6}},
					{{6,7,2},{1,5,9},{8,3,4}},
					{{2,7,6},{9,5,1},{4,3,8}} 
					};
		int count=0; 
//		System.out.println(sourceMatrix.length);
//		System.out.println(sourceMatrix[1].length);
//		System.out.println(sourceMatrix[0][1].length);
		for(int i=0;i<sourceMatrix.length;i++)
		{
			for(int j=0;j<sourceMatrix[i].length;j++)
			{
				for(int k=0;k<sourceMatrix[i][j].length;k++)
				{
					System.out.print(sourceMatrix[i][j][k]+" ");
				}
				System.out.println();
			}
			System.out.println("====================");
		}
		/*for(int i=0;i<mat.length;i++)
		{
			for(int j=0;j<mat[i].length;j++)
			{
				count++;
				mat[i][j]=count;
				System.out.print(count+" ");
			}
			System.out.println();
		}*/
//		int matrix[][]= {{5,3,4},{1,5,8},{6,4,2}};
		int matrix[][]= {{4,9,2},{3,5,7},{8,1,5}};
		System.out.println("final cost "+formingMagicSquare(matrix));
	}
	
	
	
}
