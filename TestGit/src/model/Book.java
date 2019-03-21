package model;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import controller.BookDetailController;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Book extends Observable {
	private int id;
	private String title;
	private String summary;
	private Integer yearPublished; //or int
	private String isbn;
	private int publisher;
	private LocalDateTime lastModified;
	
	
	/*
	 * Constructor
	 */
	public Book(){
		id = 0;
		title = "";
		summary = null;
		yearPublished = null;
		isbn = null;
		//publisher = id;
		publisher = 1; //Unkown publisher default
		lastModified = null;
	}
	
	/*
	 * Call getAudit book table gateway method to get a list of audit trails for this book
	 * returns this list of audit trails
	 */
	public List<Audit> getAuditTrail(){
		return BookTableGateway.getInstance().getAudit(this);
	}
	

	/*
	 * Validate user inputs throw exception + alert if any value is invalid
	 * Else save values into the book
	 * call insert to db if the book is new (id == 0)
	 * call update to db if book is not new (id != 0)
	 * returns nothing
	 */
	public void save(String t, String s, String y, String i, int p) {
		//validate user input and throw exception + alert user if values are invalid
		if(!titleValidation(t)) {
			setAlert("Title", "Title must be between 1 and 255 characters");
			throw new BookException("Title must be between 1 and 255 characters");
		}
		if(!summaryValidation(s)){
			setAlert("Summary", "Provided summary exceeds 65536 char length");
			throw new BookException("Summary must be null or <65536 characters");
		}
		if(!y.equals("") && !yearValidation(Integer.valueOf(y))){
			setAlert("Year Published", "Year must be between 1455 and present");
			throw new BookException("Year must be between 1455 and present");
		}
		if(!isbnValidation(i)) {
			setAlert("ISBN", "Isbn must be null or <= 13 characters");
			throw new BookException("Isbn must be null or <= 13 characters");
		} 
		
		//Set book values to new user entered values 
		//Note: id, lastModified are not create by user, but by db
		setPublisher(p);
		setTitle(t);
		setSummary(s);
		if(y.equals(""))
			setYearPublished(null);
		else
			setYearPublished(Integer.valueOf(y));
		setIsbn(i);
		
		//////Previous logic had publisher = id
		//////setPublisher(getId());
		
		//notify observers
		this.setChanged();
		this.notifyObservers();
		
		//call insert if book is new (id == 0)
		//call update if book is not new (id != 0)
		try {
			if(this.getId()==0) {
				int newId;
				newId = BookTableGateway.getInstance().insertBook(this);
				this.setId(newId);
			}else {
				BookTableGateway.getInstance().updateBook(this);
			}
		} catch (SQLException e) {
			setAlert("Update", "Failed to save update to database");
			e.printStackTrace();
		}
	}
	
	/*
	 * Creates an alert box of type ERROR
	 * Parameters: display title and message
	 */
	public void setAlert(String title, String message) {
		Alert a1 = new Alert(AlertType.ERROR);
		a1.setTitle(title);
		a1.setContentText(message);
		a1.setHeaderText(null);
		a1.showAndWait();
	}
	
	/*
	 * Validates that title is 1-255 characters long
	 */
	public boolean titleValidation(String s) {
		if(s.length() >=1 && s.length() <= 255)
			return true;
		return false;
	}
	
	/*
	 * Validates that summary is null or <65536 characters
	 */
	public boolean summaryValidation(String s) {
		if( s== null || s.length() < 65536) {
			return true;
		}
		return false;
	}
	
	/*
	 * Validates that year is 1455-2019
	 */
	public boolean yearValidation(int y) {
		//TODO: remove y==0 (I can't remember why I added that)
		if(y==0 || y >= 1455 && y <= 2019)
			return true;
		return false;
	}
	
	/*
	 * Validates that isbn is null or <=13 characters
	 */
	public boolean isbnValidation(String s) {
		if(s==null || s.length() <= 13)
			return true;
		return false;
	}
	
	/* .........................................................
	 * GETERS, SETTERS, AND TOSTRING
	 * .........................................................
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Integer getYearPublished() {
		return yearPublished;
	}

	public void setYearPublished(Integer yearPublished) {
		this.yearPublished = yearPublished;
	}

	public String getIsbn() {
		return isbn;
	}
	
	public int getPublisher() {
		return publisher;
	}

	public void setPublisher(int publisher) {
		this.publisher = publisher;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", summary=" + summary + ", yearPublished=" + yearPublished
				+ ", isbn=" + isbn + ", publisher=" + publisher + ", lastModified=" + lastModified + "]";
	}
}
