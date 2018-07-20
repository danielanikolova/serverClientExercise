package lib;

public class FTPServerProtocolHandler {

	static final String SERVER_HELLO_MESSAGE = "Secure File Routing Server - ver.0.1";
	static final String SERVER_WELCOME = "200 Welcome";
	static final String SERVER_PASSWORD_EXPECTED_MESSAGE = "100 Continue with password: ";
	static final String SERVER_LOGIN_SUCCESSFULL = "200 Login Successfull";	
	static final String SERVER_AUTHENTICATION_FAILED = "403 Authentication failed";
	static final String SERVER_SEND_COMMAND_POSITIVE_RESPONSE = "100 Give the content";
	static final String SERVER_SEND_COMMAND_NEGATIVE_RESPONSE_WRONG_RECIPIENT = "400 no such recipient";
	static final String SERVER_SEND_COMMAND_NEGATIVE_RESPONSE_UNAUTHORIZED = "400 no such recipient";
	static final String SERVER_SEND_COMMAND_NEGATIVE_RESPONSE_OTHER = "500 no such recipient";
	static final String SERVER_GIVE_FILE_CONTENT_COMMAND = "100 Give the content";
	static final String SERVER_NO_SUCH_RECIPIENT = "400 No such recipient";
	static final String SERVER_UNAUTHORIZED = "401 Unauthorized";
	static final String FILE_ACCEPTED = "200 File accepted";
	static final String INTERNAL_ERROR = "500 Internal error description";
	static final String PROVIDING_DIRECTORY_CONTENT = "200 Providing directory content";
	static final String PROVIDING_FILE_CONTENT = "200 Providing file content";
	static final String FILE_NOT_FOUND = "404 Not found";
	static final String FILE_REMOVED = "200 File removed";
	static final String BYE = "200 Bye";

	private String client_FQDN = "";
	String username = null;
	String userPassword = null;

	/*
	 * Returns hello message as first step of handshake
	 */
	public static String getHelloMessage() {
		return SERVER_HELLO_MESSAGE;
	}

	public String executeMessage(String message) {

		if (message == null) {
			throw new IllegalArgumentException("executeMessage() received null argument.");
		}

		//HELLO message
		//HELLO <client full qualified domain host name FQDN>
		if (message.startsWith(FTPClientProtocolHandler.CLIENT_HELLO_MESSAGE)) {
			client_FQDN = message.substring(message.indexOf("<")+1, message.indexOf(">"));
			return SERVER_WELCOME + " <<" + client_FQDN + ">>";
		}


		//Here we get the username from the client
		if (message.startsWith(FTPClientProtocolHandler.USERNAME_MESSAGE)) {
			username = message.substring(message.indexOf("<") + 1, message.indexOf(">"));

			//TODO implement UserManager class to manage the authorized users;

			return FTPServerProtocolHandler.SERVER_PASSWORD_EXPECTED_MESSAGE;
		}
		
		if (message.startsWith(FTPClientProtocolHandler.USERPASSWORD_MESSAGE)) {
			
			userPassword = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
			
			//TODO here we have to check if the password is correct
			//The next message: Login or Authentication failed
			//For the moment we accept that the password is correct
			
			return FTPServerProtocolHandler.SERVER_LOGIN_SUCCESSFULL;
		}
		
		if (message.startsWith(FTPClientProtocolHandler.CLIENT_SEND_FILE)) {
			
			String fileName = message.substring(
					FTPClientProtocolHandler.CLIENT_SEND_FILE.indexOf("<")+1 , 
					FTPClientProtocolHandler.CLIENT_SEND_FILE.indexOf(">"));
			String userName = message.substring(
					FTPClientProtocolHandler.CLIENT_SEND_FILE.lastIndexOf("<")+1 , 
					FTPClientProtocolHandler.CLIENT_SEND_FILE.lastIndexOf(">"));
			
			//here we check in the database the existence of the user to whom we want to send a file
			boolean userNameIsCorrect = checkUsernameExistence(userName);
			
			if (userNameIsCorrect) {
				return FTPServerProtocolHandler.SERVER_GIVE_FILE_CONTENT_COMMAND;
			}else {
				/*
				 * here we have to check if the user is authorized
				 */
				return FTPServerProtocolHandler.SERVER_NO_SUCH_RECIPIENT;
			}
			
		}
		
		if (message.startsWith(FTPClientProtocolHandler.CLIENT_SEND_FILE)) {
			//TODO implement fileReader class, witch will read a file line by line and returns true
			//if the file reading is completed and false if file reading is interrupted
			
			boolean fileTransferIsCompleted = true;
			
			if (fileTransferIsCompleted) {
				return FTPServerProtocolHandler.FILE_ACCEPTED;
			}else {
				return FTPServerProtocolHandler.INTERNAL_ERROR;
			}
			
		}
		
		if (message.startsWith(FTPClientProtocolHandler.LIST)) {
			//here we list all files in current user directory
			//TODO SQL prepared statement
			
			return FTPServerProtocolHandler.PROVIDING_DIRECTORY_CONTENT;
		}
		
		if (message.startsWith(FTPClientProtocolHandler.GET)) {
			String fileName = message
					.substring(message.indexOf("<") + 1,(message.indexOf(">")));
			
			//TODO find the file in the database
			
			boolean isExistingFile = true;
			
			if (isExistingFile) {
				//TODO here we have to decide how to  send file content after sending the server response 
				return FTPServerProtocolHandler.PROVIDING_FILE_CONTENT;
			}else if(isExistingFile = false) {
				return FTPServerProtocolHandler.FILE_NOT_FOUND;
			}
		}
		
		if (message.startsWith(FTPClientProtocolHandler.DELETE)) {
			String fileName = message
					.substring(message.indexOf("<") + 1,(message.indexOf(">")));
			
			boolean isExistingFile = true;
			
			if (isExistingFile) {
				//TODO  remove file from DB
				return FTPServerProtocolHandler.FILE_REMOVED;
			}else if(isExistingFile = false) {
				return FTPServerProtocolHandler.FILE_NOT_FOUND;
			}
			
		}
		
		if (message.startsWith(FTPClientProtocolHandler.QUIT)) {
			return FTPServerProtocolHandler.BYE;
		}
		
		return FTPServerProtocolHandler.INTERNAL_ERROR;
	}
	
	// returns true if there is a user with the given user name and false if there is no such user
	private boolean checkUsernameExistence(String userName) {
		//TODO
		return false;
	}

}
