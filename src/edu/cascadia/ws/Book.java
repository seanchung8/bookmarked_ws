package edu.cascadia.ws;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

//Path: http://localhost/<appln-folder-name>/book
@Path("/book")

public class Book {
	// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/getallbooks
		@Path("/getallbooks")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/book/getallbooks
		public String getAllBooks(){
			System.out.println("Inside getAllBooks ");
			return queryAllBooks();
		}
		
		// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/addbook
		@Path("/addbook")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		public String insertBook(@QueryParam("isbn") String isbn, @QueryParam("title") String title, 
			@QueryParam("author") String author, @QueryParam("edition") String edition, @QueryParam("description") String desc){
			String response = "";
			System.out.println("Inside insertBook ");
			
			int retCode = insertABook(isbn, title, author, edition, desc);
			if(retCode == 0){
				response = Utility.constructJSON("insertbook",true);
			}else if(retCode == 1){
				response = Utility.constructJSON("insertbook",false, "Book already exists");
			}else if(retCode == 2){
				response = Utility.constructJSON("insertbook",false, "Special Characters are not allowed in book");
			}else if(retCode == 3){
				response = Utility.constructJSON("insertbook",false, "Error occured");
			}
			return response;
					
		}

	// HTTP Get Method
	@GET
	// Path: http://localhost/<appln-folder-name>/book/deletebook
	@Path("/deletebook")
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteBook(@QueryParam("isbn") String isbn) {
		String response = "";
		int retCode = deleteABook(isbn);
		if (retCode == 0) {
			response = Utility.constructJSON("deletebook", true);
		} else if (retCode == 1) {
			response = Utility.constructJSON("deletebook", false, "Book does not exists");
		} else if (retCode == 3) {
			response = Utility.constructJSON("deletebook", false, "Error occured");
		}
		return response;
	}

		private int insertABook(String isbn, String title, String author, String edition, String desc){
			System.out.println("Inside insertABook");
			int result = 3;
			if(Utility.isNotNull(isbn) && Utility.isNotNull(title)){
				try {
					if(DBConnection.insertBook(isbn, title, author, edition, desc)){
						System.out.println("Book " + title + " inserted successfully");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("insertABook catch sqle. " + sqle.getMessage());
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						result = 1;
					} 
					else if(sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside insertABook catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside insertABook else");
				result = 3;
			}
				
			return result;
		}
		
		private int deleteABook(String isbn){
			System.out.println("Inside deleteABook");
			int result = 3;
			if (Utility.isNotNull(isbn) ){
				try {
					if(DBConnection.deleteBook(isbn)){
						System.out.println("Book isbn:" + isbn + " deleted successfully");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("deleteABook catch sqle. " + sqle.getMessage());
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						result = 1;
					} 
					else if(sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					System.out.println("Inside deleteABook catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside deleteABook else");
				result = 3;
			}
				
			return result;
		}

		private String queryAllBooks() {
			try {
				String json = DBConnection.getAllBooks();
				if(json != null && json.length() > 0) {
					System.out.println("getAllBooks was successfully");
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("queryAllBooks catch sqle. " + sqle.getMessage());
				//When Primary key violation occurs that means user is already registered
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside queryAllBooks catch e. Info: " + e.getMessage());
			}
			
			return "";
		}
		
		// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/getabookforsalebyid
		@Path("/getabookforsalebyid")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		public String getABookForSaleById(@QueryParam("id") String id){
			System.out.println("Inside getABookForSaleById ");
			return queryABookForSaleById(id);
		}

		private String queryABookForSaleById(String id) {
			try {
				String json = DBConnection.getBook4SaleById(id);
				
				if (json != null && json.length() > 0) {
					System.out.println("queryBookForSale was successfully");
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("queryBookForSale catch sqle. " + sqle.getMessage());
				
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside queryBookForSale catch e. Info: " + e.getMessage());
			}
			
			return "";
		}

		//======================================
		// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/addbookforsale
		@Path("/addbookforsale")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		public String addBookForSale(@QueryParam("isbn") String isbn, @QueryParam("username") String username, 
			@QueryParam("askingprice") String askingPrice, @QueryParam("bookcondition") String bookCondition, @QueryParam("comment") String comment){
			String response = "";
			System.out.println("Inside addBookForSale ");
			int retCode = addABookForSale(isbn, username, askingPrice, bookCondition, comment);
			if(retCode == 0){
				response = Utility.constructJSON("addBookForSale",true);
			}else if(retCode == 1){
				response = Utility.constructJSON("addBookForSale",false, "Book already exists");
			}else if(retCode == 2){
				response = Utility.constructJSON("addBookForSale",false, "Special Characters are not allowed in book");
			}else if(retCode == 3){
				response = Utility.constructJSON("addBookForSale",false, "Error occured");
			}
			return response;
					
		}

		private int addABookForSale(String isbn, String username, String askingPrice, String bookCondition, String comment){
			System.out.println("Inside insertABook");
			int result = 3;
			if(Utility.isNotNull(isbn) && Utility.isNotNull(username)){
				try {
					if(DBConnection.insertBook4Sale(isbn, username, askingPrice, bookCondition, comment)){
						System.out.println("Book " + isbn + " posted for sale successfully");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("addABookForSale catch sqle. " + sqle.getMessage());
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						result = 1;
					} 
					else if(sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside addABookForSale catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside addABookForSale else");
				result = 3;
			}
				
			return result;
		}
		
		//======================================
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/getbooksforsale
		@Path("/getbooksforsale")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/book/getbooksforsale
		public String getBooksForSale(){
			String response = "";
			System.out.println("Inside getBooksForSale ");
			response = queryBooksForSale();
			
			return response;
		}
		
		private String queryBooksForSale() {
			try {
				String json = DBConnection.getBooks4Sale();
				if(json != null && json.length() > 0) {
					System.out.println("queryBooksForSale was successfully");
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("queryBooksForSale catch sqle. " + sqle.getMessage());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside queryAllBooks catch e. Info: " + e.getMessage());
			}
			
			return "";
		}
		
		//======================================
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/getbooksforsalebyusername
		@Path("/getbooksforsalebyusername")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/book/getbooksforsalebyusername?username=abc
		public String getBooksForSaleByUsername(@QueryParam("username") String username){
			System.out.println("Inside getBooksForSaleByUsername ");
			try {
				return DBConnection.getBooks4SaleByUser(username);
				
			} catch(SQLException sqle) {
				System.out.println("getBooksForSaleByUsername catch sqle. " + sqle.getMessage());
			} catch(Exception e) {
				System.out.println("getBooksForSaleByUsername catch sqle. " + e.getMessage());
			}
			return "";	
		}
		
		//======================================
		// HTTP Get Method
		@GET
		// Path: http://localhost/<appln-folder-name>/book/deletebook
		@Path("/deletebookforsale")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON)
		public String deleteBookForSale(@QueryParam("id") String id, @QueryParam("status") String status) {
			String response = "";
			int retCode = deleteABook4Sale(id, status);
			if (retCode == 0) {
				response = Utility.constructJSON("deletebookforsale", true);
			} else if (retCode == 1) {
				response = Utility.constructJSON("deletebookforsale", false, "Book does not exists");
			} else if (retCode == 3) {
				response = Utility.constructJSON("deletebookforsale", false, "Error occured");
			}
			return response;
		}

		private int deleteABook4Sale(String id, String status){
			System.out.println("Inside deleteBookForSale");
			int result = 3;
			if (Utility.isNotNull(id) ){
				try {
					if (DBConnection.deleteBook4Sale(id, status)) {
						System.out.println("Book id:" + id + " deleted successfully");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("deleteABook4Sale catch sqle. " + sqle.getMessage());
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						result = 1;
					} 
					else if(sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside deleteABook4Sale catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside deleteABook4Sale else");
				result = 3;
			}
				
			return result;
		}

		//======================================
		// HTTP Get Method
		@GET
		// Path: http://localhost/<appln-folder-name>/book/updatebookforsale
		@Path("/updatebookforsale")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON)
		public String updateBookForSale(@QueryParam("id") String id, @QueryParam("askingprice") String askingprice, @QueryParam("bookcondition") String bookcondition, @QueryParam("comment") String comment) {
			String response = "";
			int retCode = updateABook4Sale(id, askingprice, bookcondition, comment);
			if (retCode == 0) {
				response = Utility.constructJSON("updatebookforsale", true);
			} else if (retCode == 1) {
				response = Utility.constructJSON("updatebookforsale", false, "Book does not exists");
			} else if (retCode == 3) {
				response = Utility.constructJSON("updatebookforsale", false, "Error occured");
			}
			return response;
		}

		private int updateABook4Sale(String id, String askingprice, String bookcondition, String comment){
			System.out.println("Inside updateABook4Sale");
			int result = 3;
			if (Utility.isNotNull(id) ){
				try {
					if (DBConnection.updateBook4Sale(id, askingprice, bookcondition, comment)) {
						System.out.println("Book id:" + id + " updated successfully");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("updateABook4Sale catch sqle. " + sqle.getMessage());
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						result = 1;
					} 
					else if(sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside updateABook4Sale catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside updateABook4Sale else");
				result = 3;
			}
				
			return result;
		}

		//======================================
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/getbookswanted
		@Path("/getbookswanted")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/register/doregister?name=pqrs&username=abc&password=xyz
		public String getBooksWanted(){
			System.out.println("Inside getBooksWanted ");
			try {
				String json = DBConnection.getBooksWanted();
				if (json != null && json.length() > 0) {
					System.out.println("queryBooksWanted was successfully");
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("queryBooksWanted catch sqle. " + sqle.getMessage());
			}
			catch (Exception e) {
				System.out.println("Inside queryBooksWanted catch e. Info: " + e.getMessage());
			}
			
			return "";
		}
		
		//======================================
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/getbookswanted
		@Path("/getbookswantedbyusername")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/book/getbookswantedbyusername
		public String getBooksWantedByUsername(@QueryParam("username") String username){
			System.out.println("Inside getBooksWantedByUsername ");
			try {
				String json = DBConnection.getBooksWantedByUser(username);
				if (json != null && json.length() > 0) {
					System.out.println("getBooksWantedByUsername was successfully");
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("getBooksWantedByUsername catch sqle. " + sqle.getMessage());
			}
			catch (Exception e) {
				System.out.println("Inside getBooksWantedByUsername catch e. Info: " + e.getMessage());
			}
			
			return "";
		}
		
		//======================================
		// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/addbookwanted
		@Path("/addbookwanted")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		public String addBookWanted(@QueryParam("isbn") String isbn, @QueryParam("username") String username, 
			@QueryParam("comment") String comment){
			String response = "";
			System.out.println("Inside addBookWanted ");
			int retCode = addABookWanted(isbn, username, comment);
			if(retCode == 0){
				response = Utility.constructJSON("addBookWanted",true);
			}else if(retCode == 1){
				response = Utility.constructJSON("addBookWanted",false, "Book already exists");
			}else if(retCode == 2){
				response = Utility.constructJSON("addBookWanted",false, "Special Characters are not allowed in book");
			}else if(retCode == 3){
				response = Utility.constructJSON("addBookWanted",false, "Error occured");
			}
			return response;
					
		}

		private int addABookWanted(String isbn, String username, String comment){
			System.out.println("Inside insertABook");
			int result = 3;
			if (Utility.isNotNull(isbn) && Utility.isNotNull(username)){
				try {
					if (DBConnection.insertBookWanted(isbn, username, comment)){
						System.out.println("Wanted book " + isbn + " posted successfully");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("addABookWanted catch sqle. " + sqle.getMessage());
					//When Primary key violation occurs that means user is already registered
					if (sqle.getErrorCode() == 1062){
						result = 1;
					} 
					else if (sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside addABookWanted catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside addABookWanted else");
				result = 3;
			}
				
			return result;
		}

		//======================================
		// HTTP Get Method
		@GET
		// Path: http://localhost/<appln-folder-name>/book/deletebookwanted
		@Path("/deletebookwanted")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON)
		public String deleteBookWanted(@QueryParam("id") String id, @QueryParam("status") String status) {
			String response = "";
			int retCode = deleteABookWanted(id, status);
			if (retCode == 0) {
				response = Utility.constructJSON("deleteBookWanted", true);
			} else if (retCode == 1) {
				response = Utility.constructJSON("deleteBookWanted", false, "Book wanted does not exists");
			} else if (retCode == 3) {
				response = Utility.constructJSON("deleteBookWanted", false, "Error occured");
			}
			return response;
		}

		private int deleteABookWanted(String id, String status){
			System.out.println("Inside deleteABookWanted");
			int result = 3;
			
			if (Utility.isNotNull(id) ){
				try {
					if (DBConnection.deleteBookWanted(id, status)) {
						System.out.println("Book wanted id:" + id + " deleted successfully");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("deleteABookWanted catch sqle. " + sqle.getMessage());
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside deleteABookWanted catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside deleteABookWanted else");
				result = 3;
			}
				
			return result;
		}

		//======================================
		// HTTP Get Method
		@GET
		// Path: http://localhost/<appln-folder-name>/book/updatebookwanted
		@Path("/updatebookwanted")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON)
		public String updateBookWanted(@QueryParam("bookid") String bookid, @QueryParam("isbn") String isbn, 
				@QueryParam("title") String title, @QueryParam("author") String author, 
				@QueryParam("edition") String edition, @QueryParam("description") String desc, 
				@QueryParam("id") String id, @QueryParam("comment") String comment) {
			String response = "";
			
			// update the book table, first
			try {
				if (DBConnection.updateBook(bookid, isbn, title, author, edition, desc)) {
					// now update the book wanted table
					return updateABookWanted(id, comment);
				}
			} catch (SQLException e) {
				response = Utility.constructJSON("updateBookWanted", false, "Error occured. " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				response = Utility.constructJSON("updateBookWanted", false, "Error occured. " + e.getMessage());
				e.printStackTrace();
			}
			
			return response;
		}

		private String updateABookWanted(String id, String comment){
			System.out.println("Inside updateABookWanted");
			String response = "";
			if (Utility.isNotNull(id) ){
				try {
					if (DBConnection.updateBookWanted(id, comment)) {
						System.out.println("Book wanted id:" + id + " updated successfully");
						response = Utility.constructJSON("updateABookWanted", true);
					}
				} catch(SQLException sqle){
					System.out.println("updateABookWanted catch sqle. " + sqle.getMessage());
					response = Utility.constructJSON("updateABookWanted", false, "Error occurred. " + sqle.getMessage());
				}
				catch (Exception e) {
					System.out.println("Inside updateABookWanted catch e. Info: " + e.getMessage());
					response = Utility.constructJSON("updateABookWanted", false, "Error occurred. " + e.getMessage());
				}
			}
			
			return response;
		}

}
