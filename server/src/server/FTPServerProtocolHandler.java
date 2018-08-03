package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.logging.Logger;

import lib.CopyProcessor;
import lib.FTPClientSideConstants;
import lib.FTPConstants;
import lib.FTPProtocolHandler;
import lib.FTPServerSideConstants;
import lib.MessagingLogger;
import lib.User;



public class FTPServerProtocolHandler implements FTPProtocolHandler
{

	private static Logger logger = MessagingLogger.getLogger();
	private CommunicationManager userManager = CommunicationManager.getInstance();
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
		return FTPServerSideConstants.SERVER_HELLO_MESSAGE;
	}

	public String processMessage(String message)
	{

		String messageResponse = null;

		if (message == null)
		{
			throw new IllegalArgumentException("executeMessage() received null argument.");
		}

		// HELLO message
		// HELLO <client full qualified domain host name FQDN>
		if (message.startsWith(FTPClientSideConstants.CLIENT_HELLO_MESSAGE))
		{
			user = new User(serverDirectoryPath);
			user.setClient_FQDN(message.substring(message.indexOf("<") + 1, message.indexOf(">")));
			return FTPServerSideConstants.SERVER_WELCOME + " <<" + user.getClient_FQDN() + ">>";
		}

		// Continue with user name message
		if (message.startsWith(FTPClientSideConstants.REGISTER_PLAIN))
		{
			createNewUser = true;
			return FTPServerSideConstants.ENTER_USERNAME;
		}

		if (message.startsWith(FTPClientSideConstants.LOGIN))
		{
			return FTPServerSideConstants.ENTER_USERNAME;
		}

		// Here we get the user name from the client
		if (message.startsWith(FTPClientSideConstants.USER))
		{
			String username = message.substring(message.indexOf("<") + 1, message.indexOf(">"));

			if (createNewUser)
			{
				// here we check if there is a registered user with user name entered from the
				// client.Returns SERVER_AUTHENTICATION_FAILED if there is someone with the
				// given user name
				if (userManager.containsUserWithUsername(username))
				{
					return FTPServerSideConstants.SERVER_AUTHENTICATION_FAILED;
				}
				user.setUserName(username);
				return FTPServerSideConstants.CONTINUE_WITH_PASSWORD;
			}

			if (userManager.containsUserWithUsername(username))
			{
				user.setUserName(username);
				return FTPServerSideConstants.CONTINUE_WITH_PASSWORD;
			}

			// returns SERVER_AUTHENTICATION_FAILED if there is no user with the entered
			// user name;
			return FTPServerSideConstants.SERVER_AUTHENTICATION_FAILED;

		}

		// here we get the user password
		if (message.startsWith(FTPClientSideConstants.PASS))
		{

			String userPassword = message.substring(message.indexOf("<") + 1, message.indexOf(">"));

			if (createNewUser)
			{
				user.setPassword(userPassword);
				userManager.addUser(user);
				userManager.addLoggedInUser(user);
				return FTPServerSideConstants.SERVER_LOGIN_SUCCESSFULL;
			}

			User userInDB = userManager.getUserByUsername(user.getUserName());

			// here we check if the entered password equals the password of the registered
			// user
			if (userPassword.equals(userInDB.getPassword()))

			{
				user.setPassword(userPassword);
				return FTPServerSideConstants.SERVER_LOGIN_SUCCESSFULL;
			}

			return FTPServerSideConstants.SERVER_AUTHENTICATION_FAILED;
		}

		if (message.startsWith(FTPConstants.START_COPY_PROCESSOR_WRITE))
		{
			// TODO create command response to copying file
			System.out.println("Error");
		}

		if (message.startsWith(FTPClientSideConstants.SEND))
		{

			String fileName = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
			String userName = message.substring(message.lastIndexOf("<") + 1, message.lastIndexOf(">"));

			// here we check in the database the existence of the user to whom we want to
			// send a file
			boolean userNameIsCorrect = userManager.containsUserWithUsername(userName);

			if (userNameIsCorrect)
			{
				recipient = userManager.getUserByUsername(userName);
				return FTPServerSideConstants.GIVE_THE_CONTENT;
			}

			return FTPServerSideConstants.WRONG_RECIPIENT;

		}

		if (message.startsWith(FTPClientSideConstants.PROVIDING_FILE_CONTENT))
		{
			return FTPServerSideConstants.PROVIDING_FILE_CONTENT;
		}

		//****************************

		if (message.startsWith(FTPConstants.START_COPY_PROCESSOR_WRITE))
		{
			String fileName = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
			CopyProcessor copyProcessor = new CopyProcessor(input, output);
			String copyResult = copyProcessor.writeFile(fileName, recipient);
			return copyResult;

		}
		else if (message.startsWith(FTPConstants.START_COPY_PROCESSOR_READ))
		{
			String source = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
			CopyProcessor copyProcessor = new CopyProcessor(input, output);
			copyProcessor.readFile(source);
		}


		//************************


		if (message.startsWith(FTPClientSideConstants.LIST))
		{
			// here we list all files in current user directory
			// TODO SQL prepared statement

			return FTPServerSideConstants.PROVIDING_DIRECTORY_CONTENT;
		}

		if (message.startsWith(FTPClientSideConstants.GET))
		{
			String fileName = message.substring(message.indexOf("<") + 1, (message.indexOf(">")));

			if (user.containsFile(fileName))
			{
				// here we send message to the client that the file is existing in the database
				// and the server starts file transfer
				return FTPServerSideConstants.PROVIDING_FILE_CONTENT;
			}
			return messageResponse = FTPServerSideConstants.FILE_NOT_FOUND;

		}

		if (message.startsWith(FTPClientSideConstants.DELETE))
		{
			String fileName = message.substring(message.indexOf("<") + 1, (message.indexOf(">")));

			boolean isExistingFile = true;

			if (isExistingFile)
			{
				// TODO remove file from DB
				messageResponse = FTPServerSideConstants.FILE_REMOVED;
			} else if (isExistingFile = false)
			{
				messageResponse = FTPServerSideConstants.FILE_NOT_FOUND;
			}
			return messageResponse;
		}

		if (message.startsWith(FTPClientSideConstants.LOGOUT))
		{
			if (user != null && userManager.containsUserWithUsername(user.getUserName()))
			{
				userManager.removeLoggedUser(user);
			}
			user = new User(serverDirectoryPath);
			createNewUser = false;
			return FTPServerSideConstants.SERVER_HELLO_MESSAGE;
		}

		if (message.startsWith(FTPClientSideConstants.QUIT))
		{
			return FTPServerSideConstants.BYE;
		}

		return FTPServerSideConstants.INTERNAL_ERROR;
	}


}
