package edu.cascadia.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class DBConnection {
	/**
	 * Method to create DB Connection
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("finally")
	public static Connection createConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName(Constants.dbClass);
			//con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
			// preparing openshift env variables
			String openShiftDBUrl = "jdbc:mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + ":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/bookmarked";
			System.out.println("*** openShiftDBUrl: " + openShiftDBUrl);
			String dbUser = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
			String dbPwd = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
			con = DriverManager.getConnection(openShiftDBUrl, dbUser, dbPwd);
		} catch (Exception e) {
			System.out.println("Exception in createConnection " + e.getMessage());
			throw e;
		} finally {
			return con;
		}
	}
	
    /**
     * Method to check whether uname and pwd combination are correct
     * 
     * @param uname
     * @param pwd
     * @return
     * @throws Exception
     */
	public static boolean checkLogin(String uname, String pwd) throws Exception {
		boolean isUserAvailable = false;
		Connection dbConn = null;
		System.out.println("in checkLogin for " +uname + " and " + pwd);
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				System.out.println("Exception in checkLogin. E:" +e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "SELECT * FROM user WHERE username = '" + uname
					+ "' AND password=" + "'" + pwd + "'";
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				//System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3));
				isUserAvailable = true;
			}
		} catch (SQLException sqle) {
				System.out.println("Exception in checkLogin sqle: " + sqle.getMessage());
				throw sqle;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return isUserAvailable;
	}
	/**
	 * Method to insert name, uname and pwd in DB
	 * 
	 * @param firstname
	 * @param lastname
	 * @param uname
	 * @param phone
	 * @param pwd
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertUser(String firstname, String lastname, String uname, String phone, String pwd) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "INSERT into user(firstname, lastname, username, phone, password) values('" + firstname+ "', '" +
					lastname + "', '" + uname + "', '" 	+ phone + "', '" + pwd + "')";
			System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in insertUser sqle: " + sqle.getMessage());throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	/**
	 * Method to insert book isbn, title and author in DB
	 * 
	 * @param isbn
	 * @param bookTitle
	 * @param author
	 * @param edition
	 * @param description
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertBook(String isbn, String bookTitle, String author, String edition, String desc) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "INSERT into book(isbn, title, author, edition, description) values('"+isbn+ "',"+"'"
					+ bookTitle + "','" + author + "','" + edition + "','" + desc + "')";
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in insertBook sqle: " + sqle.getMessage());
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	/**
	 * Method to delete book
	 * 
	 * @param isbn
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean deleteBook(String isbn) throws SQLException, Exception {
		boolean deleteStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "DELETE from book WHERE isbn = '"+isbn+ "'";
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				deleteStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return deleteStatus;
	}
	
	// being used just for testing/dev only
	public static String getAllBooks() throws SQLException, Exception {
		boolean queryStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*Statement stmt = dbConn.createStatement();
			String query = "SELECT isbn, title, author, edition, description from book";
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query); */
			
			String query = "SELECT isbn, title, author, edition, description  from book";
			EntityFactory bookEntityFactory = new EntityFactory(dbConn, query);
			List<Map<String, Object>> books = bookEntityFactory.findMultiple(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(books);
	        
	        queryStatus = true;
	        System.out.println("JSON: "+ json);
			return json;
			
			// extract resultset to system.out for now
//			while (rs.next()) {
//				System.out.println("isbn:" + rs.getString("isbn"));
//				System.out.println("title:" + rs.getString("title"));
//				System.out.println("author:" + rs.getString("author"));
//				System.out.println("edition:" + rs.getString("edition"));
//				System.out.println("description:" + rs.getString("description"));
//			}
//			rs.close();
//			stmt.close();
			
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			return Utility.constructJSON("getallbooks",false, "Error occured. " + sqle.getMessage());
			//throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getallbooks",false, "Error occured. " + e.getMessage());
			//throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		//return "";
	}
	
	/**
	 * Method to insert book isbn, title and author in DB
	 * 
	 * @param isbn
	 * @param username
	 * @param askingPrice
	 * @param bookCondition
	 * @param note
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertBook4Sale(String isbn, String username, String askingPrice, String bookCondition, String note) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "INSERT into book_for_sale(isbn, username, askingprice, bookcondition, note) values('"+isbn+ "',"+"'"
					+ username + "','" + askingPrice + "','" + bookCondition + "','" + note + "')";
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in insertBook4Sale sqle: " + sqle.getMessage());
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	public static String getBooks4Sale() throws SQLException, Exception {
		boolean queryStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String query = "SELECT b.isbn, b.title, b.author, b.edition, b.description, bfs.askingprice, "
					+ " bfs.id, bfs.bookcondition, bfs.username, bfs.note, bfs.add_timestamp, u.phone " 
							+ "FROM book_for_sale AS bfs JOIN book AS b JOIN user AS u " + 
					"WHERE b.isbn=bfs.isbn AND u.username = bfs.username";
			
			EntityFactory bookEntityFactory = new EntityFactory(dbConn, query);
			List<Map<String, Object>> books = bookEntityFactory.findMultiple(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(books);
	        
	        queryStatus = true;
	        System.out.println("JSON: "+ json);
			return json;
			
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			return Utility.constructJSON("getBooksForSale",false, "Error occured. " + sqle.getMessage());
			//throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getBooksForSale",false, "Error occured. " + e.getMessage());
			//throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		//return "";
	}

}
