package edu.umbc.test;

public class HelloWord {

	public HelloWord() {
		// TODO Auto-generated constructor stub
	}
	
	public void change(String word){
		word = "abc";
		System.out.println(word);
	}
	
	public void change2(StringBuffer word){
		word = new StringBuffer("abc");
		System.out.println(word);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World!");

		HelloWord a = new HelloWord();
		
		String word = "car";
		System.out.println(word);
		a.change(word);
		System.out.println(word);
		
		System.out.println();

		StringBuffer word2 = new StringBuffer("car");
		System.out.println(word2);
		a.change2(word2);
		System.out.println(word2);

		System.out.println(String.format("%1.3f", 1.0));
		
		for (double transitRate = 0; transitRate <= 1.01; transitRate += 0.125){
			System.out.println(transitRate);
		}
			
	}

}
