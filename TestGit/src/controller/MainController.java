package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
//import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import model.Book;
import model.BookTableGateway;
import view.ViewType;



public class MainController implements Initializable{

	private BorderPane borderPane;
	private static MainController instance = null;
	@FXML	
	private MenuItem quitMenuItem;
	@FXML
	private MenuItem bookListMenuItem;
	@FXML
	private MenuItem addBookMenuItem;
	
	//private ViewType currentview = null;
	
	private BookDetailController maincontroller = null;
	
	private MainController() {	
	}
	
	public static MainController getInstance() {
		if(instance == null)
			instance = new MainController();
		return instance;
	}
	
	
	public void switchView(ViewType viewType, Book book) {
    	//String viewString = "BookListView.fxml";
    	//!!!!USING DETAIL VIEW!!!!!!!!!!!!!!
		if(maincontroller != null) {
			if(maincontroller.changesToBook()) {

				Alert alert = new Alert(AlertType.CONFIRMATION);
				
				alert.getButtonTypes().clear();
				ButtonType buttonTypeOne = new ButtonType("Yes");
				ButtonType buttonTypeTwo = new ButtonType("No");
				ButtonType buttonTypeThree = new ButtonType("Cancel");
				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

				alert.setTitle("Save Changes?");
				alert.setHeaderText("The current view has unsaved changes.");
				alert.setContentText("Do you wish to save them before switching to a different view?");

				Optional<ButtonType> result = alert.showAndWait();
				if(result.get().getText().equalsIgnoreCase("Yes")) {
					//this.maincontroller.saveClicked(null);
					maincontroller.saveClicked(null);
					System.out.println("changes should be saved");
					//logger.error("*** save the view");
				} else if(result.get().getText().equalsIgnoreCase("Cancel")) {
					System.out.println("changes should not be saved");
					//return false;
				}

			}
		}
    	MyController controller = null;
    	//*************************************************
    	if(viewType==null) {
    		Platform.exit();
    		return;
    	}
    	String viewString = "";
    	switch(viewType) {
			case VIEW1:
				maincontroller = null;
				viewString = "../view/BookListView.fxml";
				controller = new BookListController(BookTableGateway.getInstance().getBooks());
				break;
			case VIEW2:
				maincontroller = new BookDetailController(book);
				viewString = "../view/BookDetailView.fxml";
				//controller = new BookDetailController(book);
				controller = maincontroller;
				break;
			case VIEW3:
				maincontroller = null;
				viewString = "../view/AuditTrailView.fxml";
				//TODO: get audit trail list from book table gateway method
				//instead of calling it here, logic has been moved
				//to AuditTrailController init method
				controller = new AuditTrailController(book);
				break;
				
			
    	}
    	//**********************************************
		try {
    		URL url = this.getClass().getResource(viewString);
    		FXMLLoader loader = new FXMLLoader(url);
    		loader.setController(controller);
			Parent viewNode = loader.load();
			
			// plug viewNode into MainView's borderpane center
			//controller.createList();
			borderPane.setCenter(viewNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void clickMenuItem(ActionEvent event) {
    	if(event.getSource() == quitMenuItem) {
    		//Platform.exit();
    		//moving logic to switchview
    		switchView(null, null);
    	}
    	else if(event.getSource() == bookListMenuItem) {
    		//currentview = ViewType.VIEW1;
    		switchView(ViewType.VIEW1, null);
    		//Platform.exit();
    	}
    	else if(event.getSource() == addBookMenuItem) {
    		//currentview = ViewType.VIEW2;
    		switchView(ViewType.VIEW2, new Book());
    	}
    }
	public BorderPane getBorderPane() {
		return borderPane;
	}
	
	public void setBorderPane(BorderPane borderPane) {
		this.borderPane = borderPane;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	/*public void setCurrentView(ViewType v) {
		currentview = v;
	}*/
}
