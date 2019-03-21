package model;

//import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//import com.mysql.jdbc.Statement;

public class BookTableGateway {
	//all JDBC queries must use parameterized queries
	
	private static BookTableGateway instance = null;
	
	private Connection connection;
	
	/*
	 * Empty Constructor
	 */
	private BookTableGateway() {
		
	}
	
	/*
	 * Get instance of BookTableGateway
	 */
	public static BookTableGateway getInstance() {
		if(instance == null) {
			instance = new BookTableGateway();
		}
		return instance;
	}
	
	/*
	 * Connects to db and returns a list of all books in db
	 */
	public List<Book> getBooks(){
		List<Book> books = new ArrayList<Book>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery("SELECT * FROM Books");
			while(rs.next()) {
				Book b = new Book();
				b.setId(rs.getInt("id"));
				b.setTitle(rs.getString("title"));
				b.setSummary(rs.getString("summary"));
				b.setIsbn(rs.getString("isbn"));
				if(rs.getInt("year_published")==0) {
					b.setYearPublished(null);
				}else {
					b.setYearPublished(rs.getInt("year_published"));
				}
				b.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
				b.setPublisher(rs.getInt("publisher_id"));
				
				books.add(b);
			}
		}catch(SQLException e){
			System.out.println("SQL Exception" + e);
		}/*finally {
			if(rs != null)
				try {
					//rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(getConnection() != null)
				try {
					getConnection().close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}*/
		return books;
	}
	
	/*
	 * inserts a new book into db
	 */
	public int insertBook(Book b) throws SQLException {
		String query = "insert into Books (title, summary, year_published, publisher_id, isbn) " 
				+ " values (?, ?, ?, ?, ?)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, b.getTitle());
		ps.setString(2, b.getSummary());
		ps.setInt(3, b.getYearPublished());
		ps.setInt(4, b.getPublisher());
	    ////PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		////b.setPublisher(rs.getInt(1));
		////ps.setInt(4, b.getPublisher());
		ps.setString(5, b.getIsbn());
		ps.executeUpdate();

		
		b.setId(lastSave());
		//////b.setPublisher(b.getId());
		b.setPublisher(b.getPublisher());
		//get time step for last modified from db
		b.setLastModified(getTimestep(b));
		
		//Create a audit trail msg for new book insertion to db
		//////AuditTableGateway.getInstance().createConnection();
		AuditTableGateway.getInstance().addBook(b);
		
		return b.getId();
		
		////ResultSet rs = ps.getGeneratedKeys();
		////rs.next();
		////b.setId(rs.getInt(1));
		////b.setPublisher(b.getId());
		////updateBook(b);
		////return rs.getInt(1);
	}
	
	/*
	 * Sets publisher to books id in db
	 * NOTE: this is old logic and is no longer required/used
	 */
	public void setPublisher(Book b) throws SQLException {
		String query = "update Books set publisher_id = ? where id = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, b.getPublisher());
		ps.setInt(2, b.getId());
		ps.executeUpdate();
	}
	
	/*
	 * Returns books last_modified column from db
	 */
	public LocalDateTime getTimestep(Book b) throws SQLException {
		String querey = "Select * FROM Books WHERE id=" + b.getId();
		PreparedStatement ps = connection.prepareStatement(querey);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getTimestamp("last_modified").toLocalDateTime();
	}
	
	/*
	 * Get previous book values saved into db before overriding them with update
	 * returns a book object with old values
	 */
	public Book getOldValues(Book b) throws SQLException {
		Book old = new Book();
		String querey = "Select * From Books WHERE id=" + b.getId();
		PreparedStatement ps = connection.prepareStatement(querey);
		ResultSet rs = ps.executeQuery();
		rs.next();
		old.setId(rs.getInt("id"));
		old.setTitle(rs.getString("title"));
		old.setSummary(rs.getString("summary"));
		old.setIsbn(rs.getString("isbn"));
		if(rs.getInt("year_published")==0) {
			old.setYearPublished(null);
		}else {
			old.setYearPublished(rs.getInt("year_published"));
		}
		old.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
		old.setPublisher(rs.getInt("publisher_id"));
		return old;
	}
	
	/*
	 * Returns a list of audit trail records for book
	 */
	public List<Audit> getAudit(Book b){
		////AuditTableGateway.getInstance().createConnection();
		return AuditTableGateway.getInstance().getAudit(b);
	}
	
	/*
	 * Note: this is old logic, and has been moved into Book save()
	 */
	public void saveBook(Book b) {
		try {
			if(b.getId()==0) {
				int newId;
				//newId = insertBook(b);
				System.out.println("new id is 0");
				//b.setId(newId);
			}else {
				updateBook(b);
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}	
	}
	
	/*
	 * Check if book is up to date by comparing books and db's last modified values
	 * if book is up to date save new values to database
	 * else throw exception + alert user that book is not up to date
	 */
	public void updateBook(Book b) throws SQLException {
		//save old book db values
		Book old = getOldValues(b);
		
		//get time stamp from db and check if it's the same as book model's
		LocalDateTime temp = getTimestep(b);
		if(temp.equals(b.getLastModified())) {
		}
		else {
			b.setAlert("Book Save Failure", "This book has been updated by another user\nreturn to BookListView to fetch a new copy of the book.");
			throw new BookException("Summary must be null or <65536 characters");
		}
		
		String query = "update Books set title = ? , summary = ? , year_published = ? , publisher_id = ? , isbn = ? " 
				+ " where id = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, b.getTitle());
		ps.setString(2, b.getSummary());
		try {
			ps.setInt(3, b.getYearPublished());
		}catch(NullPointerException e) {
			ps.setNull(3, java.sql.Types.INTEGER);
		}
		ps.setInt(4, b.getPublisher());
		ps.setString(5, b.getIsbn());
		ps.setInt(6, b.getId());
		ps.executeUpdate();
		
		//update models time stamp to that of the updated book's db timestamp
		b.setLastModified(getTimestep(b));
		
		//call method to create new audit trail entries for book updates
		/////AuditTableGateway.getInstance().createConnection();
		AuditTableGateway.getInstance().updateBook(old, b);
		
	}
	
	/*
	 * Delete a book from db
	 * call methods to delete all audit trails related to book
	 */
	public void deleteBook(Book b) throws SQLException {
		//Must delete records for this book from audit table first
		/////AuditTableGateway.getInstance().createConnection();
		AuditTableGateway.getInstance().deleteBook(b);
		
		//delete book from db
		String query = "delete from Books where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, b.getId());
		ps.executeUpdate();
		
	}
	
	/*
	 * Gets and return id of last saved book in db
	 * NOTE: this is old logic and is not longer required/used
	 */
	public int lastSave() throws SQLException {
		String query = "Select LAST_INSERT_ID()";
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getInt(1);
	}
	
	/*
	 * GETTERS AND SETTERS
	 */
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
