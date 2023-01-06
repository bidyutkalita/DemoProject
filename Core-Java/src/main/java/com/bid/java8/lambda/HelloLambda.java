package com.bid.java8.lambda;

@FunctionalInterface
interface Drawable {
	public void draw();
	
}

public class HelloLambda {
	public static void main(String[] args) {
		//lambda expression syntax
		//(argument-list) -> {body}
		Drawable dr = ()->{
			System.out.println("Hello Lambda");
		};
		dr.draw();
	}
}
