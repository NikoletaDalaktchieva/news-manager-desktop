package application.news;

import java.util.Arrays;

public enum Category {
	ALL("All"),
	ECONOMY ("Economy"), 
	INTERNATIONAL("International"), 
	NATIONAL("National"),
	SPORTS("Sports"), 
	TECHNOLOGY("Technology");
	
	private String name;
	private Category (String name){
		this.name= name;
	}
	
	public String toString() {
		return name;
	}
	
	public static Category getByName(String name) {
		return Arrays.stream(Category.values())
				.filter(c -> c.name.equals(name))
				.findFirst()
				.orElse(ALL);
	}
}
