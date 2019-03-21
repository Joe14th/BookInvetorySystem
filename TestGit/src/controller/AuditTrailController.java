package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import model.Audit;
import model.Book;
import view.ViewType;

public class AuditTrailController implements Initializable, MyController{
	@FXML
	private Text text;
	@FXML
	private ListView<String> auditList;
	@FXML
	private Button backButton;
	private Book book;

	/*
	 * Constructor takes a books
	 */
	public AuditTrailController(Book book) {
		this.book = book;
	}
	
	/*
	 * Sets text to books title
	 * Loads List of audit trail records into views ListView
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		text.setText("Audit Trail for " + book.getTitle());
		
		List<Audit> auditTrail = book.getAuditTrail();
		//TODO: use toString method instead
		for(Audit a : auditTrail) {
			String s = a.getDateAdded() +" : "+ a.getMsg();
			auditList.getItems().add(s);
		}
	}
	
	/*
	 * When user clicks back button return to view2 for specific book
	 * calls MainController switchView
	 */
	public void backClicked(ActionEvent event) {
		MainController.getInstance().switchView(ViewType.VIEW2, book);
	}
	
	

}
