package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class AuditTableGateway {
	private Connection connection;
	private static AuditTableGateway instance = null;
	
	/*
	 * Empty Constructor
	 */
	private AuditTableGateway() {
		
	}
	
	/*
	 * Connect to db to get a list of audit trails for a book
	 * return the list of audit trails
	 */
	public List<Audit> getAudit(Book b){
		List<Audit> trail = new ArrayList<Audit>();
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM book_audit_trail WHERE book_id=" + b.getId());
			while(rs.next()) {
				Audit a = new Audit(rs.getInt("id"), rs.getTimestamp("date_added"), rs.getString("entry_msg"));
				trail.add(a);
			}
			//TODO: close connection
		}catch(SQLException e) {
			System.out.println("SQL Exception " + e);
		}
		return trail;
	}
	
	/*
	 * get instance for AuditTableGateway
	 */
	public static AuditTableGateway getInstance() {
		if(instance == null) {
			instance = new AuditTableGateway();
		}
		return instance;
	}
	
	/*
	 * Adds a "New Book" msg to the db when a new book is created
	 */
	public void addBook(Book b) throws SQLException {
		//book_id, entry_msg
		String query = "insert into book_audit_trail (book_id, entry_msg) "
				+ " values (?, ?)";
		
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, b.getId());
		ps.setString(2, "Book added");
		ps.executeUpdate();
		//"Book added"
	}
	
	/*
	 * Deletes all messages related to a book when a book is deleted from db
	 */
	public void deleteBook(Book b) throws SQLException {
		String query = "DELETE FROM book_audit_trail WHERE book_id="+b.getId();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.executeUpdate();
	}
	
	/*
	 * Check what values have changed (updated), for each change
	 * create a new msg entry in db describing the change
	 */
	public void updateBook(Book old, Book b) throws SQLException {
		//compare values to see what was updated
		if(!old.getTitle().equals(b.getTitle()))
			updateAuditMsg(b.getId(),"title", old.getTitle(), b.getTitle());
		if(!old.getSummary().equals(b.getSummary()))
			updateAuditMsg(b.getId(), "summary", old.getSummary(), b.getSummary());
		if(old.getYearPublished()!=b.getYearPublished())
			updateAuditMsg(b.getId(), "year_published", old.getYearPublished().toString(), b.getYearPublished().toString());
		if(!old.getIsbn().equals(b.getIsbn()))
			updateAuditMsg(b.getId(), "isbn", old.getIsbn(), b.getIsbn());
		if(old.getPublisher()!=b.getPublisher())
			updateAuditMsg(b.getId(), "publisher_id", Integer.toString(old.getPublisher()), Integer.toString(b.getPublisher()));
		//TODO: I don't think i need an update message for lastModified but ill go ahead and do it
		if(!old.getLastModified().equals(b.getLastModified()))
			updateAuditMsg(b.getId(), "last_modified", old.getLastModified().toString(), b.getLastModified().toString());
	}
	
	/*
	 * Insert a msg into db describing a change that occured when a book is updated
	 */
	public void updateAuditMsg(int id, String field, String old, String update) throws SQLException {
		String query = "insert into book_audit_trail (book_id, entry_msg) "
				+ " values (?, ?)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		String msg = field + " changed from " + old + " to " + update;
		ps.setString(2, msg);
		ps.executeUpdate();
	}
	
	/*
	 * NOTE: this is old logic, and is no longer required/used
	 * Create a connection to the audit_trail db
	 * user must remember to close connection
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
	
	/*..................................................
	 * GETTERS and SETTERS
	 * .................................................
	 */
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
