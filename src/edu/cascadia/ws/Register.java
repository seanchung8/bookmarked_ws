package edu.cascadia.ws;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
//Path: http://localhost/<appln-folder-name>/register
@Path("/register")
public class Register {
	// HTTP Get Method
	@GET 
	// Path: http://localhost/<appln-folder-name>/register/doregister
	@Path("/doregister")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://localhost/<appln-folder-name>/register/doregister?name=pqrs&username=abc&password=xyz
	public String doRegister(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname, @QueryParam("username") String uname,  @QueryParam("phone") String phone, @QueryParam("password") String pwd){
		String response = "";
		System.out.println("Inside doRegister. name:" + firstname + " " + lastname + " username:" + uname + " password:" + pwd);
		int retCode = registerUser(firstname, lastname, uname, phone, pwd);
		if(retCode == 0){
			response = Utility.constructJSON("register",true);
		}else if(retCode == 1){
			response = Utility.constructJSON("register",false, "You are already registered");
		}else if(retCode == 2){
			response = Utility.constructJSON("register",false, "Special Characters are not allowed in Username and Password");
		}else if(retCode == 3){
			response = Utility.constructJSON("register",false, "Error occured");
		}
		return response;
				
	}
	
	private int registerUser(String firstname, String lastname, String uname, String phone, String pwd){
		System.out.println("Inside registerUser");
		int result = 3;
		if(Utility.isNotNull(uname) && Utility.isNotNull(pwd)){
			try {
				if(DBConnection.insertUser(firstname, lastname, uname, phone, pwd)){
					System.out.println("User " + firstname + " " + lastname + " inserted");
					result = 0;
				}
			} catch(SQLException sqle){
				System.out.println("RegisterUser catch sqle:" + sqle.getMessage());
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				//When special characters are used in name,username or password
				else if(sqle.getErrorCode() == 1064){
					System.out.println(sqle.getErrorCode());
					result = 2;
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside registerUser catch e. Info: " + e.getMessage());
				result = 3;
			}
		}else{
			System.out.println("Inside checkCredentials else");
			result = 3;
		}
			
		return result;
	}
	
}
