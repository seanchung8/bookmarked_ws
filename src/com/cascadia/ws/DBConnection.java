package com.cascadia.ws;

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
			con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
		} catch (Exception e) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "SELECT * FROM user WHERE username = '" + uname
					+ "' AND password=" + "'" + pwd + "'";
			//System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				//System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3));
				isUserAvailable = true;
			}
		} catch (SQLException sqle) {
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
	 * Method to insert uname and pwd in DB
	 * 
	 * @param name
	 * @param uname
	 * @param pwd
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertUser(String name, String uname, String pwd) throws SQLException, Exception {
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
			String query = "INSERT into user(name, username, password) values('"+name+ "',"+"'"
					+ uname + "','" + pwd + "')";
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
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
		return insertStatus;
	}
	/**
	 * Method to insert book isbn, title and author in DB
	 * 
	 * @param isbn
	 * @param bookTitle
	 * @param author
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertBook(String isbn, String bookTitle, String author) throws SQLException, Exception {
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
			String query = "INSERT into book(isbn, title, author) values('"+isbn+ "',"+"'"
					+ bookTitle + "','" + author + "')";
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
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
		return insertStatus;
	}
	
	/**
	 * Method to delete book isbn, title and author in DB
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
	
	
}
