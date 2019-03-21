package model;

import java.util.Date;

public class Audit {
	private int id;
	private String msg;
	private Date dateAdded;
	
	/*
	 * Constructor takes book id, date msg was added, and the msg
	 */
	public Audit(int id, Date dateAdded, String msg) {
		this.id = id;
		this.msg = msg;
		this.dateAdded = dateAdded;
	}

	/*....................................
	 * GETTER AND SETTERS
	 * ...................................
	 */
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	

}
