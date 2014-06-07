package edu.umbc.similarity.dictionary;

public class WebsterDefinition {

	String definition;
	int state;   // 0
	
	public WebsterDefinition() {
		// TODO Auto-generated constructor stub
		definition = "";
		state = 0;
	}

	public void append(String line){
		
		if (state == 0 && line.trim().equals("")){
			state = 1;
			return;
		}
		
		if (state == 1 || state == 2){

			if (line.length() > 3 && Character.isDigit(line.charAt(0)) && line.charAt(1) == '.' && line.charAt(2) == ' '){
				
				if (!line.endsWith(".)")){
					line = line.substring(3, line.length());
					state = 1;
				}else
					state = 2;
			}

			if (line.length() > 3 && Character.isDigit(line.charAt(0)) && Character.isDigit(line.charAt(1)) && line.charAt(2) == '.' && line.charAt(3) == ' '){

				if (!line.endsWith(".)")){
					line = line.substring(4, line.length());
					state = 1;
				}else
					state = 2;
			}

			if (line.length() > 6 && line.startsWith("Defn: ")){
				line = line.substring(6, line.length());
				state = 1;
			}
			
			
			if (state == 1){
		
				int dot_position = line.indexOf(".");
				
				if (dot_position >= 0){
					definition += " " + line.substring(0, dot_position + 1);
					state = 2;
	
				}else{
					definition += " " + line;
				}
			}
			
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
