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
		// Query parameters are parameters: http://localhost/<appln-folder-name>/register/doregister?name=pqrs&username=abc&password=xyz
		public String getAllBooks(){
			String response = "";
			System.out.println("Inside getAllBooks ");
			response = queryAllBooks();
			
//			if(retCode == 0){
//				response = Utility.constructJSON("getallbooks",true);
//			}else if(retCode == 1){
//				response = Utility.constructJSON("register",false, "You are already registered");
//			}else if(retCode == 2){
//				response = Utility.constructJSON("register",false, "Special Characters are not allowed in Username and Password");
//			}else if(retCode == 3){
//				response = Utility.constructJSON("register",false, "Error occured");
//			}
			return response;
					
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
					// TODO Auto-generated catch block
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
			int result = 3;
			try {
				String json = DBConnection.getAllBooks();
				if(json != null && json.length() > 0) {
					System.out.println("getAllBooks was successfully");
					result = 0;
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("queryAllBooks catch sqle. " + sqle.getMessage());
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside queryAllBooks catch e. Info: " + e.getMessage());
				result = 3;
			}
			
			return "";
		}
		
		// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/book/addbookforsale
		@Path("/addbookforsale")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		public String addBookForSale(@QueryParam("isbn") String isbn, @QueryParam("username") String username, 
			@QueryParam("askingprice") String askingPrice, @QueryParam("bookcondition") String bookCondition, @QueryParam("note") String note){
			String response = "";
			System.out.println("Inside addBookForSale ");
			int retCode = addABookForSale(isbn, username, askingPrice, bookCondition, note);
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

		private int addABookForSale(String isbn, String username, String askingPrice, String bookCondition, String note){
			System.out.println("Inside insertABook");
			int result = 3;
			if(Utility.isNotNull(isbn) && Utility.isNotNull(username)){
				try {
					if(DBConnection.insertBook4Sale(isbn, username, askingPrice, bookCondition, note)){
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
		// Path: http://localhost/<appln-folder-name>/book/getallbooks
		@Path("/getbooksforsale")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/register/doregister?name=pqrs&username=abc&password=xyz
		public String getBooksForSale(){
			String response = "";
			System.out.println("Inside getBooksForSale ");
			response = queryBooksForSale();
			
			return response;
					
		}
		
		private String queryBooksForSale() {
			int result = 3;
			try {
				String json = DBConnection.getBooks4Sale();
				if(json != null && json.length() > 0) {
					System.out.println("queryBooksForSale was successfully");
					result = 0;
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("queryBooksForSale catch sqle. " + sqle.getMessage());
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside queryAllBooks catch e. Info: " + e.getMessage());
				result = 3;
			}
			
			return "";
		}
		
}
