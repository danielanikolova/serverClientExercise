package lib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.logging.Logger;



public class FTPServerProtocolHandler implements FTPProtocolHandler
{

	private static Logger logger = MessagingLogger.getLogger();

	public static final String SERVER_HELLO_MESSAGE = "Secure File Routing Server - ver.0.1";
	public static final String SERVER_WELCOME = "200 Welcome";
	public static final String ACCOUNT_CREATED = "Created account";
	public static final String CONTINUE_WITH_PASSWORD = "100 Continue with password: ";
	public static final String ENTER_USERNAME = "Enter user name";
	public static final String LOGIN = "Login";
	public static final String SERVER_LOGIN_SUCCESSFULL = "200 Login Successfull";
	public static final String SERVER_AUTHENTICATION_FAILED = "403 Authentication failed";

	public static final String SEND_COMMAND_POSITIVE_RESPONSE = "100 Give the content";
	public static final String SEND_COMMAND_NEGATIVE_RESPONSE_WRONG_RECIPIENT = "400 no such recipient";
	public static final String SEND_COMMAND_NEGATIVE_RESPONSE_UNAUTHORIZED = "400 no such recipient";
	public static final String SEND_COMMAND_NEGATIVE_RESPONSE_OTHER = "500 no such recipient";

	public static final String SERVER_NO_SUCH_RECIPIENT = "400 No such recipient";
	public static final String UNAUTHORIZED = "401 Unauthorized";
	public static final String BYE = "200 Bye";

	public static final String FILE_NOT_FOUND = "404 Not found";
	public static final String FILE_REMOVED = "200 File removed";
	public static final String GIVE_FILE_CONTENT = "100 Give the content";
	public static final String PROVIDING_FILE_CONTENT = "200 Providing file content";
	public static final String PROVIDING_DIRECTORY_CONTENT = "200 Providing directory content";

	private UserManager userManager = UserManager.getInstance();
	private User user;
	private User recipient = null;
	private boolean createNewUser = false;
	private DataOutputStream output = null;
	private DataInputStream input = null;
	private String serverDirectoryPath;

	public FTPServerProtocolHandler(DataInputStream input, DataOutputStream output,  String serverDirectoryPath) {
		this.output = output;
		this.input = input;
		this.serverDirectoryPath = serverDirectoryPath;
	}

	/*
	 * Returns hello message as first step of handshake
	 */
	public static String getHelloMessage()
	{
		return SERVER_HELLO_MESSAGE;
	}

	public String executeMessage(String message)
	{

		String messageResponse = null;

		if (message == null)
		{
			throw new IllegalArgumentException("executeMessage() received null argument.");
		}

		// HELLO message
		// HELLO <client full qualified domain host name FQDN>
		if (message.startsWith(FTPClientProtocolHandler.CLIENT_HELLO_MESSAGE))
		{
			user = new User(serverDirectoryPath);
			user.setClient_FQDN(message.substring(message.indexOf("<") + 1, message.indexOf(">")));
			return SERVER_WELCOME + " <<" + user.getClient_FQDN() + ">>";
		}

		// Continue with user name message
		if (message.startsWith(FTPClientProtocolHandler.REGISTER_PLAIN))
		{
			createNewUser = true;
			return FTPServerProtocolHandler.ENTER_USERNAME;
		}

		if (message.startsWith(FTPClientProtocolHandler.LOGIN))
		{
			return FTPServerProtocolHandler.ENTER_USERNAME;
		}

		// Here we get the user name from the client
		if (message.startsWith(FTPClientProtocolHandler.USERNAME_MESSAGE))
		{
			String username = message.substring(message.indexOf("<") + 1, message.indexOf(">"));

			if (createNewUser)
			{
				// here we check if there is a registered user with user name entered from the
				// client.Returns SERVER_AUTHENTICATION_FAILED if there is someone with the
				// given user name
				if (userManager.containsUserWithUsername(username))
				{
					return FTPServerProtocolHandler.SERVER_AUTHENTICATION_FAILED;
				}
				user.setUserName(username);
				return FTPServerProtocolHandler.CONTINUE_WITH_PASSWORD;
			}

			if (userManager.containsUserWithUsername(username))
			{
				user.setUserName(username);
				return FTPServerProtocolHandler.CONTINUE_WITH_PASSWORD;
			}

			// returns SERVER_AUTHENTICATION_FAILED if there is no user with the entered
			// user name;
			return FTPServerProtocolHandler.SERVER_AUTHENTICATION_FAILED;

		}

		// here we get the user password
		if (message.startsWith(FTPClientProtocolHandler.USERPASSWORD_MESSAGE))
		{

			String userPassword = message.substring(message.indexOf("<") + 1, message.indexOf(">"));

			if (createNewUser)
			{
				user.setPassword(userPassword);
				userManager.addUser(user);
				userManager.addLoggedInUser(user);
				return FTPServerProtocolHandler.SERVER_LOGIN_SUCCESSFULL;
			}

			User userInDB = userManager.getUserByUsername(user.getUserName());

			// here we check if the entered password equals the password of the registered
			// user
			if (userPassword.equals(userInDB.getPassword()))

			{
				user.setPassword(userPassword);
				return FTPServerProtocolHandler.SERVER_LOGIN_SUCCESSFULL;
			}

			return FTPServerProtocolHandler.SERVER_AUTHENTICATION_FAILED;
		}

		if (message.startsWith(FTPProtocolHandler.START_COPY_PROCESSOR_WRITE))
		{
			// TODO create command response to copying file
			System.out.println("Error");
		}

		if (message.startsWith(FTPClientProtocolHandler.CLIENT_SEND_FILE))
		{

			String fileName = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
			String userName = message.substring(message.lastIndexOf("<") + 1, message.lastIndexOf(">"));

			// here we check in the database the existence of the user to whom we want to
			// send a file
			boolean userNameIsCorrect = userManager.containsUserWithUsername(userName);

			if (userNameIsCorrect)
			{
				recipient = userManager.getUserByUsername(userName);
				return FTPServerProtocolHandler.GIVE_FILE_CONTENT;
			}

			return FTPServerProtocolHandler.SERVER_NO_SUCH_RECIPIENT;

		}

		if (message.startsWith(FTPClientProtocolHandler.PROVIDING_FILE_CONTENT))
		{
			return FTPServerProtocolHandler.PROVIDING_FILE_CONTENT;
		}

		//****************************

		if (message.startsWith(FTPProtocolHandler.START_COPY_PROCESSOR_WRITE))
		{
			String fileName = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
			CopyProcessor copyProcessor = new CopyProcessor(input, output);
			String copyResult = copyProcessor.writeFile(fileName, recipient);
			return copyResult;

		}
		else if (message.startsWith(FTPProtocolHandler.START_COPY_PROCESSOR_READ))
		{
			String source = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
			CopyProcessor copyProcessor = new CopyProcessor(input, output);
			copyProcessor.readFile(source);
		}


		//************************


		if (message.startsWith(FTPClientProtocolHandler.LIST))
		{
			// here we list all files in current user directory
			// TODO SQL prepared statement

			return FTPServerProtocolHandler.PROVIDING_DIRECTORY_CONTENT;
		}

		if (message.startsWith(FTPClientProtocolHandler.GET))
		{
			String fileName = message.substring(message.indexOf("<") + 1, (message.indexOf(">")));

			if (user.containsFile(fileName))
			{
				// here we send message to the client that the file is existing in the database
				// and the server starts file transfer
				return FTPServerProtocolHandler.PROVIDING_FILE_CONTENT;
			}
			return messageResponse = FTPServerProtocolHandler.FILE_NOT_FOUND;

		}

		if (message.startsWith(FTPClientProtocolHandler.DELETE))
		{
			String fileName = message.substring(message.indexOf("<") + 1, (message.indexOf(">")));

			boolean isExistingFile = true;

			if (isExistingFile)
			{
				// TODO remove file from DB
				messageResponse = FTPServerProtocolHandler.FILE_REMOVED;
			} else if (isExistingFile = false)
			{
				messageResponse = FTPServerProtocolHandler.FILE_NOT_FOUND;
			}
			return messageResponse;
		}

		if (message.startsWith(FTPClientProtocolHandler.LOGOUT))
		{
			if (user != null && userManager.containsUserWithUsername(user.getUserName()))
			{
				userManager.removeLoggedUser(user);
			}
			user = new User(serverDirectoryPath);
			createNewUser = false;
			return SERVER_HELLO_MESSAGE;
		}

		if (message.startsWith(FTPClientProtocolHandler.EXIT))
		{
			return FTPServerProtocolHandler.BYE;
		}

		return FTPServerProtocolHandler.INTERNAL_ERROR;
	}


}
