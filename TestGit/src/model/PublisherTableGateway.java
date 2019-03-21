package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javafx.util.Pair;


public class PublisherTableGateway {
	private Connection connection;
	private static PublisherTableGateway instance = null;
	
	/*
	 * Empty Constructor
	 */
	private PublisherTableGateway(){
		
	}
	
	/*
	 * Get instance of PublisherTableGateway
	 */
	public static PublisherTableGateway getInstance() {
		if(instance == null) {
			instance = new PublisherTableGateway();
		}
		return instance;
	}
	
	/*
	 * Returns a list of all db publishers
	 */
	public List<Publisher> fetchPublishers(){
		List<Publisher> publishers = new ArrayList<Publisher>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery("SELECT * FROM Publisher");
			while(rs.next()) {
				String name = rs.getString("publisher_name");
				int id = rs.getInt("id");
				publishers.add(new Publisher(id,name));
			}
		}catch(SQLException e) {
			System.out.println("SQL Exception");
		}
		return publishers;
	}
	
	/*
	 * create a connection to db
	 * NOTE: old logic, it's no longer required/used 
	 */
	public void createConnection() {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://easel2.fulgentcorp.com/duv590");
		ds.setUser("duv590");
		//gyfnRzmTrL6sewsYgXXQ
		ds.setPassword("gyfnRzmTrL6sewsYgXXQ");
		Connection connection;
		try {
			connection = ds.getConnection();
			setConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*...................................
	 * GETTERS AND SETTERS 
	 * ..................................
	 */
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
