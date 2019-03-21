package controller;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import model.Book;
import model.BookException;
import model.BookTableGateway;
import model.Publisher;
import model.PublisherTableGateway;
import view.ViewType;

public class BookDetailController implements Initializable, MyController, Observer{
	@FXML
	private Button saveButton;
	@FXML
	private TextField detailTitle;
	@FXML
	private TextField detailYear;
	@FXML
	private TextField detailIsbn;
	@FXML
	private TextArea detailSummary;
	@FXML
	private ComboBox<String> comboBox;
	@FXML
	private Button auditButton;
	
	private Book book;
	
	private List<Publisher> publishers = null;
	
	private static Logger logger = LogManager.getLogger(BookDetailController.class);
	
	/*
	 * Constructor
	 */
	BookDetailController(Book b){
		this.book = b;
	}
	
	/*
	 * When user clicks save button this method will retrieve all user input data from view
	 * and call Book's save method
	 * enables audit trail records button after user clicks save
	 */
	public void saveClicked(ActionEvent event) {
		logger.debug("User clicked save button");
		int i = 0;
		for(Publisher p: publishers) {
			if(p.getName().equals(comboBox.getSelectionModel().getSelectedItem())) {
				i = p.getId(); break;
			}
		}
		book.save(detailTitle.getText(), detailSummary.getText(), detailYear.getText(), detailIsbn.getText(), i);
		auditButton.setDisable(false);
	}
	
	/*
	 * When user clicks audit trail button 
	 * the view will switch to View3 for specific book 
	 * calls MainController switchView method
	 */
	public void auditClicked(ActionEvent event) {
		MainController.getInstance().switchView(ViewType.VIEW3, book);
	}
	
	/*
	 * Sets view's content to display specific book's data
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//Set view's content to specific book's data
		detailTitle.setText(book.getTitle());
		if(book.getYearPublished() == null)
			detailYear.setText("");
		else
			detailYear.setText(book.getYearPublished().toString());
		detailIsbn.setText(book.getIsbn());
		detailSummary.setText(book.getSummary());
		
		//Add list of publishers to views comboBox
		/////PublisherTableGateway.getInstance().createConnection();
		publishers = PublisherTableGateway.getInstance().fetchPublishers();
		for(Publisher p : publishers) {
			comboBox.getItems().add(p.getName());
		}
		
		//set comboBox default to the books publisher
		//if new book set it to "UNKNOWN" == first item in list
		if(book.getId()==0)comboBox.getSelectionModel().selectFirst();
		else {
			for(Publisher p : publishers) {
				if(p.getId() == book.getPublisher()) {
					comboBox.getSelectionModel().select(publishers.indexOf(p)); break;
				}
			}
		}
		
		//if the book is new user should not be allowed to use audit trail button
		if(book.getId()==0) {
			auditButton.setDisable(true);
		}
		
	}

	/*
	 * Checks if the user has made any changes to book's data in current view
	 * returns true if there was a change
	 * else returns false
	 */
	public boolean changesToBook() {
		if(!book.getTitle().equals(detailTitle.getText())) return true;
		try {
			if(!book.getSummary().equals(detailSummary.getText()))return true;
		}catch(NullPointerException e){
			if(book.getSummary()!=null && detailSummary.getText()!=null)return true;
		}
		try {
			if(!book.getIsbn().equals(detailIsbn.getText())) return true;
		}catch(NullPointerException e) {
			if(book.getIsbn()!=null && detailIsbn.getText()!=null) return true;
		}
		try {
			if(!book.getYearPublished().toString().equals(detailYear.getText())) return true;
		}catch(NullPointerException e) {
			if(book.getYearPublished()!=null && !detailYear.getText().equals("")) return true;
		}
		int i = 0;
		for(Publisher p: publishers) {
			if(p.getName().equals(comboBox.getSelectionModel().getSelectedItem())) {
				i = p.getId(); 
			}
		}
		if(book.getPublisher()!=i) return true;
		return false;
		
	}
	
	/*
	 * Observable method, sets view's content to new book values
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO set ComboBox to books publisher value
		Book book = (Book) arg0;
		detailTitle.setText(book.getTitle());
		detailYear.setText(book.getYearPublished().toString());
		detailIsbn.setText(book.getIsbn());
		detailSummary.setText(book.getSummary());
	}
	
	/*
	 * GETTER
	 */
	
	public List<Publisher> getPublishers(){
		return publishers;
	}


}
