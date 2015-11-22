package edu.cascadia.ws;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.MimeMessage.RecipientType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
//Path: http://localhost/<appln-folder-name>/register
@Path("/user")
public class Register {
	// HTTP Get Method
	@GET 
	// Path: http://localhost/<appln-folder-name>/register/doregister
	@Path("/doregister")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://localhost/<appln-folder-name>/register/doregister?name=pqrs&username=abc&password=xyz
	public String doRegister(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname, @QueryParam("username") String uname,  @QueryParam("phone") String phone, @QueryParam("zipcode") String zipcode, @QueryParam("password") String pwd){
		String response = "";
		System.out.println("Inside doRegister. name:" + firstname + " " + lastname + " username:" + uname + " password:" + pwd);
		int retCode = registerUser(firstname, lastname, uname, phone, zipcode, pwd);
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
	
	private int registerUser(String firstname, String lastname, String uname, String phone, String zipcode, String pwd){
		System.out.println("Inside registerUser");
		int result = 3;
		if(Utility.isNotNull(uname) && Utility.isNotNull(pwd)){
			try {
				Random rand;
				int min = 100;
				int max = 99999;
				
				int verificationCode = ThreadLocalRandom.current().nextInt(min, max + 1);
				// format string to have leading 0 if necessary
				String verificationStr = String.format("%05d", verificationCode);
				if(DBConnection.insertUser(firstname, lastname, uname.toLowerCase(), phone, zipcode, pwd, verificationStr)){
					System.out.println("User " + firstname + " " + lastname + " inserted");
					sendVerificationEmail(firstname, lastname, uname, verificationCode);
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
	
	// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/register/doregister
		@Path("/verifyregistration")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/register/doregister?name=pqrs&username=abc&password=xyz
		public String verifyRegistration(@QueryParam("username") String uname,  @QueryParam("password") String pwd, @QueryParam("verificationcode") String code){
			String response = "";
			System.out.println("Inside verifyRegistration. username:" + uname + " verificationcode:" + code);
			int retCode = verifyRegistrationCode(uname, pwd, code);
			if(retCode == 0){
				response = Utility.constructJSON("verifyRegistration",true);
			}else if(retCode == 3){
				response = Utility.constructJSON("verifyRegistration",false, "Error occured");
			}else if(retCode == 4){
				response = Utility.constructJSON("verifyRegistration",false, "Invalid verification code");
			}
			return response;
					
		}
		
		private int verifyRegistrationCode(String username, String pwd, String code) {
			System.out.println("Inside verifyRegistrationCode");
			int result = 3;
			if(Utility.isNotNull(username) && Utility.isNotNull(pwd)){
				try {
					if (DBConnection.verifyRegistration(username, pwd, code)) {
						System.out.println("User " + username + " verified OK");
						result = 0;
					} else {
						result = 4; // indicate bad verification code
					}
				} catch(SQLException sqle){
					System.out.println("verifyRegistrationCode catch sqle:" + sqle.getMessage());
					//When Primary key violation occurs that means user is already registered
					if (sqle.getErrorCode() == 1062){
						result = 1;
					} 
					//When special characters are used in name,username or password
					else if (sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside verifyRegistrationCode catch e. Info: " + e.getMessage());
					result = 3;
				}
			}else{
				System.out.println("Inside checkCredentials else");
				result = 3;
			}
				
			return result;
		}
		
		// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/register/getuserinfo
		@Path("/getuserinfo")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/register/doregister?name=pqrs&username=abc&password=xyz
		public String getUserInfo(@QueryParam("username") String uname){
			String response = "";
			System.out.println("Inside getUserInfo. Username:" + uname);
			if (uname != null && uname.length() > 0) {
				response = getUser(uname);
			}
			
			return response;
		}

		private String getUser(String username) {
			try {
				String json = DBConnection.getUserInfo(username);
				if(json != null && json.length() > 0) {
					System.out.println("getUser was successful");
					return json;
				}
			} catch(SQLException sqle){
				System.out.println("getUser catch sqle. " + sqle.getMessage());
			}
			catch (Exception e) {
				System.out.println("Inside getUser catch e. Info: " + e.getMessage());
			}
			
			return "";
		}

		// HTTP Get Method
		@GET 
		// Path: http://localhost/<appln-folder-name>/register/updateuserinfo
		@Path("/updateuserinfo")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost/<appln-folder-name>/register/updateuserinfo?username=xxx
		public String updateUserInfo(@QueryParam("username") String username, @QueryParam("firstname") String firstname, 
				@QueryParam("lastname") String lastname,  @QueryParam("newusername") String newusername,
				@QueryParam("phone") String phone, 
				@QueryParam("zipcode") String zipcode){
			String response = "";
			System.out.println("Inside updateUserInfo. Username:" + username);
			if (username != null && username.length() > 0) {
				response = updateUser(username, firstname, lastname, newusername, phone, zipcode);
			}
			
			return response;
		}

		private String updateUser(String username, String firstname, String lastname, String newUsername, String phone, String zipcode) {
			try {
				if (DBConnection.updateUser(username, firstname, lastname, newUsername, phone, zipcode)) {
					return Utility.constructJSON("updateUser", true);
				}
				
			} catch(SQLException sqle){
				System.out.println("updateUser catch sqle. " + sqle.getMessage());
				return Utility.constructJSON("updateUser", false, "Error occurred. " + sqle.getMessage());
			}
			catch (Exception e) {
				System.out.println("Inside updateUser catch e. Info: " + e.getMessage());
				return Utility.constructJSON("updateUser", false, "Error occurred. " + e.getMessage());
			}
			
			return "";
		}
				

	private void sendVerificationEmail(String firstname, String lastname, String emailAddr, int verificationCode) {
		System.out.println("Inside sendVerificationEmail for " + firstname + " " + lastname + " " + emailAddr); 
		final String host = System.getenv("BOOKMARKED_E_HOST") != null ? System.getenv("BOOKMARKED_E_HOST") : "smtp.gmail.com";
		final int port = System.getenv("BOOKMARKED_E_PORT") != null ? Integer.parseInt(System.getenv("BOOKMARKED_E_PORT")) : 465;
		final String username = System.getenv("BOOKMARKED_E_ACCT") != null ? System.getenv("BOOKMARKED_E_ACCT") : "no_account@gmail.com";
		final String password = System.getenv("BOOKMARKED_E_PWD") != null ? System.getenv("BOOKMARKED_E_PWD") : "";
		
		final Email email = new Email();
		// assume email account/username is the same as the reply to
		email.setFromAddress("no-reply BookmarkEd", username);
		email.addRecipient(firstname + " " + lastname, emailAddr, RecipientType.TO);
		email.setText("Thank you for registering to Bookmarked\nYour validation code is " + verificationCode + ". Please enter this code to the registration screen to complete the registration process. <br><p>Regards, <br><p><p>BookmarkEd Team");
		email.setTextHTML("Thank you for registering to Bookmarked.<p>Your validation code is <b>" + verificationCode + "</b>. Please enter this code to the registration screen to complete the registration process.<br><p>Regards, <br><p><p>BookmarkEd Team");
		email.setSubject("BookmarkEd Registration");
		
		System.out.println("Sending email...");
		try {
		new Mailer(host, port, username, password, TransportStrategy.SMTP_SSL).sendMail(email);
		} catch (Exception e) {
			System.out.println("Exception in sending email. " + e.getMessage());
		}
	}

}
