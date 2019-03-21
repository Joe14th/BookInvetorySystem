package application;
	
import java.net.URL;
import java.sql.Connection;
//import java.util.List;

import controller.MainController;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.AuditTableGateway;
//import model.Book;
import model.BookTableGateway;
import model.PublisherTableGateway;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/*
 * CS 4743 Assignment 3 by Jasmine Ramirez
 */
public class Main extends Application {
	
	/*
	 * Load main view and set MainController as the controller
	 */
	@Override
	public void start(Stage stage) {
		try {
			URL url = this.getClass().getResource("../view/MainView.fxml");
			FXMLLoader loader = new FXMLLoader(url);
			
			
			MainController controller = MainController.getInstance();
			loader.setController(controller);
			Parent root = loader.load();
			controller.setBorderPane((BorderPane) root); //cast parent root to border pane
			
			Scene scene = new Scene(root);
			stage.setTitle("SDI - Book Inventory System");
			stage.setScene(scene);
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Launch application
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/*
	 * Create a db connection and set all gateway connections
	 */
	public void init() throws Exception{
		super.init();
		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://easel2.fulgentcorp.com/duv590");
		ds.setUser("duv590");
		ds.setPassword("gyfnRzmTrL6sewsYgXXQ");
		Connection connection = ds.getConnection();
		
		// assign connection to gateways
		BookTableGateway.getInstance().setConnection(connection);
		AuditTableGateway.getInstance().setConnection(connection);
		PublisherTableGateway.getInstance().setConnection(connection);
		
	}
	
	/*
	 * Close all gateway connections
	 */
	@Override
	public void stop() throws Exception{
		super.stop();
		BookTableGateway.getInstance().getConnection().close();
		AuditTableGateway.getInstance().getConnection().close();
		PublisherTableGateway.getInstance().getConnection().close();
	}
	
}
