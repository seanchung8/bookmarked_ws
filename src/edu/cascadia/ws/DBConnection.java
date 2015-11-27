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
     * @param uname (lowercase)
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
			// only check for verified user
			String query = "SELECT * FROM user WHERE status = 2 AND LOWER(username) = '" + uname
					+ "' AND password=" + "'" + pwd + "'";
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				//System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3));
				isUserAvailable = true;
			}
			
			if (!isUserAvailable) {
				System.out.println("No matching verified username");
				// only check for verified user
				query = "SELECT * FROM user WHERE status = 1 AND LOWER(username) = '" + uname
						+ "' AND password=" + "'" + pwd + "'";
				System.out.println(query);
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					System.out.println("Found username but not verified");
					throw new Exception("User not verified");
				}
				
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
     * Method to check whether uname and pwd combination are correct
     * 
     * @param uname (lowercase)
     * @return
     * @throws Exception
     */
	public static String getUserInfo(String uname) throws Exception {
		Connection dbConn = null;
		System.out.println("in getUserInfo for " +uname);
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				System.out.println("Exception in getUserInfo. E:" +e.getMessage());
				e.printStackTrace();
				
				return Utility.constructJSON("getUserInfo",false, "Error occurred. " + e.getMessage());
			}
			Statement stmt = dbConn.createStatement();
			// get user info
			String query = "SELECT firstname, lastname, username, phone, zipcode FROM user WHERE status = 2 AND LOWER(username) = '" + uname
					+ "'";
			System.out.println(query);

			EntityFactory userEntityFactory = new EntityFactory(dbConn, query);
			Map<String, Object> user = userEntityFactory.findSingle(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(user);
	        System.out.println("JSON: "+ json);
			return json;

		} catch (SQLException sqle) {
				System.out.println("Exception in getUserInfo sqle: " + sqle.getMessage());
				return Utility.constructJSON("getUserInfo",false, "Error occurred. " + sqle.getMessage());

		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getUserInfo",false, "Error occurred. " + e.getMessage());
			
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
	}
	
	/**
	 * Method to insert user in DB
	 * 
	 * @param firstname
	 * @param lastname
	 * @param firstname
	 * @param lastname
	 * @param uname
	 * @param phone
	 * @param zipcode
	 * @param pwd
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertUser(String firstname, String lastname, String uname, String phone, String zipcode, String pwd, String verificationCode) throws SQLException, Exception {
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
			String query = "INSERT into user(firstname, lastname, username, phone, zipcode, password, verificationcode) values('" + firstname+ "', '" +
					lastname + "', '" + uname + "', '" 	+ phone + "', '" + zipcode + "', '" + pwd + "', '" + verificationCode + "')";
			System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in insertUser sqle: " + sqle.getMessage());
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
	 * Method to update user in DB
	 * 
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param newUsername
	 * @param phone
	 * @param zipcode
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean updateUser(String username, String firstname, String lastname, 
			String newUsername, String phone, String zipcode) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "UPDATE user SET firstname = \"" + firstname + "\", lastname = \"" + lastname + 
					"\", username = '" + newUsername + "', phone ='" + phone +
					"', zipcode = '" + zipcode + "' WHERE LOWER(username) = '" + username.toLowerCase() + "'";
			
			System.out.println(query);
			int records = stmt.executeUpdate(query);

			//When record is successfully update
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in updateUser sqle: " + sqle.getMessage());
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return updateStatus;
	}
	
	/**
	 * Method to update user password in DB
	 * 
	 * @param username (lower case)
	 * @param currentpass
	 * @param newpass
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static String updateUserPwd(String username, String currentPwd, String newPwd) throws SQLException, Exception {
		Connection dbConn = null;
		
		if (currentPwd.equals(newPwd)) {
			return Utility.constructJSON("updateUserPwd",false, "New password cannot be the same. ");
		}
		
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				return Utility.constructJSON("updateUserPwd",false, e.getMessage());
			}
			Statement stmt = dbConn.createStatement();
			
			// update the password where username = username and status = 2
			String query = "UPDATE user SET password = \"" + newPwd + "\" WHERE LOWER(username) = '" + username + "' AND status = 2";
			
			System.out.println(query);
			int records = stmt.executeUpdate(query);

			//When record is successfully update
			if (records > 0) {
				return Utility.constructJSON("updateUserPwd",true);
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in updateUser sqle: " + sqle.getMessage());
			return Utility.constructJSON("updateUserPwd",false, sqle.getMessage());
		} catch (Exception e) {
			//e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("updateUserPwd",false, e.getMessage());
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return "";
	}

	/**
	 * Method to insert book isbn, title, author, edition, description in DB
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
			
			// limit possible string overflow
			if (bookTitle.length() > 50) {
				bookTitle = bookTitle.substring(0, 50);
			}
	
			if (author.length() > 50) {
				author = author.substring(0, 50);
			}
	
			if (desc.length() > 250) {
				desc = desc.substring(0, 250);
			}
			
			Statement stmt = dbConn.createStatement();
			String query = "INSERT into book(isbn, title, author, edition, description) values('"+isbn+ "', '" +
					Utility.escapeCharString(bookTitle) + "', '" + Utility.escapeCharString(author) + "','" + Utility.escapeCharString(edition) + "', '" + Utility.escapeCharString(desc) + "')";
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
	 * Method to insert book isbn, title, author, edition, description in DB
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
	public static boolean updateBook(String id, String isbn, String bookTitle, String author, String edition, String desc) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// limit possible string overflow
			if (bookTitle.length() > 50) {
				bookTitle = bookTitle.substring(0, 50);
			}
	
			if (author.length() > 50) {
				author = author.substring(0, 50);
			}
	
			if (desc.length() > 250) {
				desc = desc.substring(0, 250);
			}
			
			Statement stmt = dbConn.createStatement();
			String query = "UPDATE book SET isbn='" + isbn + "', title=\"" + bookTitle + "\",author=\"" +  author + "\", edition='" + edition + "',description='" + Utility.escapeCharString(desc) + 
					"' WHERE id = " + id;
					
			System.out.println(query);
			int records = stmt.executeUpdate(query);
			//When record is successfully updated
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("In updateBook sqle: " + sqle.getMessage());
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return updateStatus;
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
			
			String query = "SELECT id, isbn, title, author, edition, description from book";
			EntityFactory bookEntityFactory = new EntityFactory(dbConn, query);
			List<Map<String, Object>> books = bookEntityFactory.findMultiple(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(books);
	        
	        queryStatus = true;
	        System.out.println("JSON: "+ json);
			return json;
			
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			return Utility.constructJSON("getallbooks",false, "Error occurred. " + sqle.getMessage());
			//throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getallbooks",false, "Error occurred. " + e.getMessage());
			//throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		//return "";
	}
	
	/**
	 * Method to insert book isbn, username, askingPrice, bookCondition and comment in DB
	 * 
	 * @param isbn
	 * @param username
	 * @param askingPrice
	 * @param bookCondition
	 * @param comment
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertBook4Sale(String isbn, String username, String askingPrice, String bookCondition, String comment) throws SQLException, Exception {
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
			System.out.println("Getting book ID");
			// need to get the book id first
			String queryBook = "SELECT id FROM book WHERE book.isbn = '" + isbn + "'";
			System.out.println("Executing:" + queryBook);;
			ResultSet rs = stmt.executeQuery(queryBook);
			int bookID = -1;
			if (rs.next()) {
				bookID = rs.getInt("id");
				System.out.println("book id to insert into book_for_sale:" + bookID);
			}
			System.out.println("BookID is " + bookID);
			
			System.out.println("askingPrice is >" + askingPrice + "<");
			if (askingPrice.trim().length() == 0 ) {
				askingPrice = "0";
			}
			System.out.println("askingPrice after trim is >" + askingPrice + "<");
			
			String query = "INSERT into book_for_sale(book_id, username, askingprice, bookcondition, comment) values("+ bookID + ", '"
					+ username + "'," + askingPrice + ",'" + bookCondition + "', '" + Utility.escapeCharString(comment) + "')";
			System.out.println(query);
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
	
	/**
	 * Method to delete book for sale
	 * 
	 * @param id
	 * @param status
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean deleteBook4Sale(String id, String status) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "UPDATE book_for_sale set status = '" + status + "', last_update = CURRENT_TIMESTAMP WHERE id = " + id ;
					
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in deleteBook4Sale sqle: " + sqle.getMessage());
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
		return updateStatus;
	}
	
	/**
	 * Method to insert book isbn, title and author in DB
	 * 
	 * @param id
	 * @param askingPrice
	 * @param bookCondition
	 * @param comment
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean updateBook4Sale(String id, String askingPrice, String bookCondition, String comment) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "UPDATE book_for_sale set askingprice = '" + askingPrice + 
					"', bookCondition = '" + bookCondition + "', comment = '" + 
					Utility.escapeCharString(comment) + "', last_update = CURRENT_TIMESTAMP WHERE id = " + id ;
					
			System.out.println(query);
			int records = stmt.executeUpdate(query);
			
			//When record is successfully inserted
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in updateBook4Sale sqle: " + sqle.getMessage());
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
		return updateStatus;
	}

	public static String getBook4SaleById(String id) throws SQLException, Exception {
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
					+ " bfs.id, bfs.book_id, bfs.bookcondition, bfs.username, bfs.comment, bfs.add_timestamp, u.phone " 
							+ "FROM book_for_sale AS bfs JOIN book AS b JOIN user AS u " + 
					"ON b.id=bfs.book_id AND u.username = bfs.username WHERE bfs.id = " + id + " AND bfs.status = 1";
			
			EntityFactory bookEntityFactory = new EntityFactory(dbConn, query);
			Map<String, Object> book = bookEntityFactory.findSingle(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(book);
	        
	        queryStatus = true;
	        System.out.println("JSON: "+ json);
			return json;
			
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			return Utility.constructJSON("getBook4SaleById",false, "Error occured. " + sqle.getMessage());
			//throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getBook4SaleById",false, "Error occured. " + e.getMessage());
			//throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		//return "";
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
					+ " bfs.id, bfs.book_id, bfs.bookcondition, bfs.username, bfs.comment, bfs.add_timestamp, u.phone " 
							+ "FROM book_for_sale AS bfs JOIN book AS b JOIN user AS u " + 
					"ON b.id=bfs.book_id AND u.username = bfs.username WHERE bfs.status = 1 ORDER BY add_timestamp DESC";
			
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

	public static String getBooks4SaleByUser(String username) throws SQLException, Exception {
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String query = "SELECT b.isbn, b.title, b.author, b.edition, b.description, bfs.askingprice, "
					+ " bfs.id, bfs.book_id, bfs.bookcondition, bfs.username, bfs.comment, bfs.add_timestamp, u.phone " 
							+ "FROM book_for_sale AS bfs JOIN book AS b JOIN user AS u " + 
					"ON b.id=bfs.book_id AND LOWER(u.username) = LOWER(bfs.username) WHERE bfs.status = 1 AND LOWER(bfs.username) = '" + username.toLowerCase() + "' ORDER BY add_timestamp DESC";
			
			System.out.println("Query for getBooks4SaleByUser: "+ query);
			
			EntityFactory bookEntityFactory = new EntityFactory(dbConn, query);
			List<Map<String, Object>> books = bookEntityFactory.findMultiple(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(books);
	        
	        //System.out.println("JSON: "+ json);
			return json;
			
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			return Utility.constructJSON("getBooks4SaleByUser",false, "Error occured. " + sqle.getMessage());
			//throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getBooks4SaleByUser",false, "Error occured. " + e.getMessage());
			//throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		//return "";
	}

	/**
	 * Method to insert book wanted
	 * 
	 * @param isbn
	 * @param username
	 * @param comment
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean insertBookWanted(String isbn, String username, String comment) throws SQLException, Exception {
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
			System.out.println("Getting book ID");
			// need to get the book id first
			String queryBook = "SELECT id FROM book WHERE book.isbn = '" + isbn + "'";
			System.out.println("Executing:" + queryBook);;
			ResultSet rs = stmt.executeQuery(queryBook);
			int bookID = -1;
			if (rs.next()) {
				bookID = rs.getInt("id");
				System.out.println("book id to insert into book_for_sale:" + bookID);
			}
			System.out.println("BookID is " + bookID);
			String query = "INSERT into book_wanted(book_id, username, comment) values("+ bookID + ", '"
					+ username + "', '" + Utility.escapeCharString(comment) + "')";
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in insertBookWanted sqle: " + sqle.getMessage());
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
	
	public static String getBooksWanted() throws SQLException, Exception {
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String query = "SELECT b.isbn, b.title, b.author, b.edition, b.description, "
					+ " bw.id, bw.book_id, bw.username, bw.comment, bw.add_timestamp, u.phone " 
							+ "FROM book_wanted AS bw JOIN book AS b JOIN user AS u " + 
					"ON b.id=bw.book_id AND LOWER(u.username) = LOWER(bw.username) WHERE bw.status = 1 ORDER BY bw.add_timestamp DESC";
			
			EntityFactory bookEntityFactory = new EntityFactory(dbConn, query);
			List<Map<String, Object>> books = bookEntityFactory.findMultiple(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(books);
	        
	        System.out.println("JSON: "+ json);
			return json;
			
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			return Utility.constructJSON("getBooksWanted",false, "Error occured. " + sqle.getMessage());
			//throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getBooksWanted",false, "Error occured. " + e.getMessage());
			//throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
	}

	public static String getBooksWantedByUser(String username) throws SQLException, Exception {
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String query = "SELECT b.isbn, b.title, b.author, b.edition, b.description, "
					+ " bw.id, bw.book_id, bw.username, bw.comment, bw.add_timestamp, u.phone " 
							+ "FROM book_wanted AS bw JOIN book AS b JOIN user AS u " + 
					"ON b.id=bw.book_id AND LOWER(u.username) = LOWER(bw.username) WHERE bw.status = 1 AND LOWER(bw.username) = '" + 
							username.toLowerCase() + "' ORDER BY bw.add_timestamp DESC";
			System.out.println("query in getBooksWantedByUser" + query);
			
			EntityFactory bookEntityFactory = new EntityFactory(dbConn, query);
			List<Map<String, Object>> books = bookEntityFactory.findMultiple(new Object[]{});

	        ObjectMapper mapper = new ObjectMapper();

	        String json = mapper.writeValueAsString(books);
	        
	        return json;
			
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			return Utility.constructJSON("getBooksWantedByUser",false, "Error occured. " + sqle.getMessage());
			//throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			return Utility.constructJSON("getBooksWantedByUser",false, "Error occured. " + e.getMessage());
			//throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
	}

	/**
	 * Method to delete book wanted
	 * 
	 * @param id
	 * @param status
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean deleteBookWanted(String id, String status) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "UPDATE book_wanted set status = '" + status + "', last_update = CURRENT_TIMESTAMP WHERE id = " + id ;
					
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//When record is successfully inserted
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in deleteBookWanted sqle: " + sqle.getMessage());
			throw sqle;
		} catch (Exception e) {
			//e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return updateStatus;
	}
	
	/**
	 * Method to update book wanted
	 * 
	 * @param id
	 * @param comment
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean updateBookWanted(String id, String comment) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "UPDATE book_wanted set comment = '" + Utility.escapeCharString(comment) + "', last_update = CURRENT_TIMESTAMP WHERE id = " + id ;
					
			System.out.println(query);
			int records = stmt.executeUpdate(query);
			
			//When record is successfully inserted
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in updateBookWanted sqle: " + sqle.getMessage());
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
		return updateStatus;
	}

	/**
	 * Method to verify registration
	 * 
	 * @param username (lowercase)
	 * @param password
	 * @param verification code
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean verifyRegistration(String username, String password, String code) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			// check if the record exist
			String newUserQuery = "SELECT verificationcode FROM user WHERE LOWER(username) = '" + username + "' AND password ='" + password + "'";
			System.out.println("Checking the user: " + newUserQuery);
			ResultSet result = stmt.executeQuery(newUserQuery);
			String verCode = "";
			if (result.next()) {
				verCode = result.getString("verificationcode");
				System.out.println("Verification code in table >" + verCode + "<");
			}
			if (!code.equals(verCode)) {
				// code does not match
				System.out.println("Verification code does not match. Invalid");
				// let's just delete the entry so user can re-register and not getting duplicate email 
				String delUserQuery = "DELETE FROM user WHERE LOWER(username) = '" + username.toLowerCase() + "' AND password ='" + password + "'";
				System.out.println("Deleting user: " + delUserQuery);
				stmt.executeUpdate(delUserQuery);
				
				return false;
			}
			String query = "UPDATE user set status = 2 WHERE LOWER(username) = '" + username.toLowerCase() + "' AND password = '" + password + "'" ;
					
			//System.out.println(query);
			int records = stmt.executeUpdate(query);
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
			System.out.println("in verifyRegistration sqle: " + sqle.getMessage());
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
		return updateStatus;
	}
	
}
