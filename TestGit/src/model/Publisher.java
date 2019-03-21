package model;

public class Publisher {
	private int id;
	private String name;
	
	/*
	 * Constructor takes publisher id and publishers name
	 */
	public Publisher(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/*...................................
	 * GETTERS AND SETTERS
	 * ..................................
	 */
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
