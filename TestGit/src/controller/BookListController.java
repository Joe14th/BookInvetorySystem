package controller;

//import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.InvalidationListener;
//import javafx.beans.Observable;
import java.util.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
//import javafx.event.ActionEvent;
//import javafx.event.ActionEvent;
import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
//import javafx.scene.Parent;
//import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.BorderPane;
import model.Book;
import model.BookTableGateway;
import view.ViewType;

public class BookListController implements Initializable, MyController{
	
	private static Logger logger = LogManager.getLogger(BookListController.class);
	@FXML
	private Button delete;
	@FXML
	private ListView<String> list;
	private List<Book> books;
	
	/*
	 * Constructor takes a list of Book records
	 */
	public BookListController(List<Book> books) {
		this.books = books;
	}
	
	/*
	 * Populates view's ListView with list of book records
	 */
	public void populateList() {
		for(Book b : books) {
			list.getItems().add(b.getTitle());
		}
	}
	
	/*
	 * Calls populate method to initialize view's listView
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		populateList();
		
	}

	/*
	 * When user double clicks listView entry load specified books View2
	 * calls MainController switch view method
	 */
	public void doubleMouseClick(MouseEvent click){
		if(click.getClickCount() == 2) { // a double click
			Book book = null;
			for(Book b: books) {
				if(b.getTitle().equals(list.getSelectionModel().getSelectedItem()))
					book = b;
			}
			logger.debug("User double clicked book");
			MainController.getInstance().switchView(ViewType.VIEW2, book);
		}
	}
	
	/*
	 * When user selects an item and clicks delete button
	 * the listView entry will be deleted from the db
	 * and view will be reloaded - 
	 * calls MainController switchView
	 */
	public void deleteItem(ActionEvent event) {
		if(list.getSelectionModel().isEmpty())
			return;
		Book book = null;
		for(Book b: books) {
			if(b.getTitle().equals(list.getSelectionModel().getSelectedItem()))
				book = b;
		}
		try {
			BookTableGateway.getInstance().deleteBook(book);
			MainController.getInstance().switchView(ViewType.VIEW1, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//update view
	}

}
