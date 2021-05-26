package com.bid.lambda;


@FunctionalInterface
interface Drawalble{
	public void draw();
	
}


public class HelloLambda {
	
	
	public static void main(String[] args) {
		//lambda expression syntax
		//(argument-list) -> {body}
		Drawalble dr = ()->{
			System.out.println("Hello Lambda");
		};
		dr.draw();
	}
}
