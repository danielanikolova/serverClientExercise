package lib;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class FTPClientProtocolHandler implements FTPProtocolHandler
{

	private static Logger logger = MessagingLogger.getLogger();

	public static final String CLIENT_HELLO_MESSAGE = "HELLO";
	public static final String CREATE_ACCOUNT = "Create account";
	public static final String CREATE_ACCOUNT_USERNAME = "New account parameters:";
	public static final String REGISTER_PLAIN = "REGISTER PLAIN";
	public static final String REGISTER_TLS = "REGISTER TLS";
	public static final String LOGIN = "LOGIN";

	public static final String USERNAME_MESSAGE = "USER";
	public static final String USERPASSWORD_MESSAGE = "PASS";
	public static final String CLIENT_SEND_FILE = "SEND";
	public static final String LIST = "LIST";
	public static final String GET = "GET";
	public static final String DELETE = "DELETE";
	public static final String LOGOUT = "LOGOUT";
	public static final String EXIT = "EXIT";
	public static final String CLOSE_COMMUNICATION = "Close communication";
	public static final String PROVIDING_FILE_CONTENT = "200 Providing file content";

	private boolean serverHello = false;
	private boolean serverWelcome = false;
	private boolean isAuthorized = false;
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private DataOutputStream output;
	private DataInputStream input;
	private User user;
	private String userDirectoryPath;

	public FTPClientProtocolHandler(DataInputStream input, DataOutputStream output, String directorypath) {
		this.input = input;
		this.output = output;
		this.user = new User(directorypath);
		this.userDirectoryPath = directorypath;
	}

	/*
	 * Process a string command sent by the server Returns a string to be sent to
	 * the server or empty string "" when user need to specify next command
	 */
	public String processServerMessage(String message)
	{

		String clientInput = null;

		// Handle server hello message

		if (isAuthorized)
		{

			if (message.startsWith(FTPServerProtocolHandler.SERVER_NO_SUCH_RECIPIENT))
			{
				// here the user selects from the menu command
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.GIVE_FILE_CONTENT))
			{
				String sourceFile = readInputFromClient();
				return PROVIDING_FILE_CONTENT + "<" + sourceFile + ">";
			}


			if (message.startsWith(FTPClientProtocolHandler.PROVIDING_FILE_CONTENT))
			{

				CopyProcessor copyProcessor = new CopyProcessor(input, output);
				String source = message.substring(message.indexOf("<") + 1,
						message.indexOf(">"));
				String copyResult = copyProcessor.readFile(source);

				return copyResult;

			}

			if (message.startsWith(FTPServerProtocolHandler.FILE_ACCEPTED))
			{
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.INTERNAL_ERROR))
			{
				// here the user selects from the menu command
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.PROVIDING_DIRECTORY_CONTENT))
			{
				// TODO read files from the server
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.PROVIDING_FILE_CONTENT))
			{
				// TODO check how to start copying the file
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.FILE_REMOVED))
			{
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.BYE))
			{
				return FTPClientProtocolHandler.CLOSE_COMMUNICATION;
			}

		}

		else if (serverHello && serverWelcome)
		{
			if (message.startsWith(FTPServerProtocolHandler.ENTER_USERNAME))
			{

				System.out.println("Please enter user name: ");

				clientInput = readInputFromClient();
				user.setUserName(clientInput);

				return USERNAME_MESSAGE + "<" + clientInput + ">";
			}

			if (message.startsWith(FTPServerProtocolHandler.CONTINUE_WITH_PASSWORD))
			{
				System.out.println("Enter password:");
				String password = readInputFromClient();

				return FTPClientProtocolHandler.USERPASSWORD_MESSAGE + "<" + password + ">";
			}

			if (message.startsWith(FTPServerProtocolHandler.SERVER_LOGIN_SUCCESSFULL))
			{

				isAuthorized = true;

				return selectCommand();

			}

		}

		else if (serverHello)
		{

			System.out.println("we are in server wellcome message clientside");
			// check whether serverHello is true so that we know previous step is completed.
			// Handle server welcome message
			if (message.startsWith(FTPServerProtocolHandler.SERVER_WELCOME))
			{
				String FQDN = Utils.getFQDN();
				if (message.substring(message.lastIndexOf("<") + 1, message.indexOf(">")).equals(FQDN))
				{
					serverWelcome = true;

					logger.info("[Client]: Handshake successfull. ");

					System.out.println("Select action:");
					System.out.println("1 - register");
					System.out.println("2 - login");

					String clientCommand = readInputFromClient();

					switch (clientCommand)
						{
						case "1":
							return FTPClientProtocolHandler.REGISTER_PLAIN;
						case "2":
							return FTPClientProtocolHandler.LOGIN;

						}

				}
			}
		} else if (FTPServerProtocolHandler.SERVER_HELLO_MESSAGE.equals(message))
		{

			// mark serverHello so that for next step we know hello step passed ok
			serverHello = true;

			return CLIENT_HELLO_MESSAGE + " <" + Utils.getFQDN() + ">";

		}

		if (message.startsWith(FTPServerProtocolHandler.SERVER_AUTHENTICATION_FAILED))
		{
			return FTPClientProtocolHandler.LOGIN;
		}

		return null;
	}

	private String selectCommand()
	{

		System.out.println("Please enter menu option: ");
		System.out.println("1 - send file");
		System.out.println("2 - list files");
		System.out.println("3 - get");
		System.out.println("4 - delete");
		System.out.println("5 - logout");
		System.out.println("6 - exit");

		String input = readInputFromClient();
		String userCommand = null;
		String fileName = null;

		switch (input)
			{
			case "1":
				System.out.println("Enter file name:");
				fileName = readInputFromClient();
				System.out.println("Enter username:");
				String username = readInputFromClient();
				userCommand = FTPClientProtocolHandler.CLIENT_SEND_FILE + "<" + fileName + ">" + "<" + username + ">";
				break;
			case "2":
				userCommand = FTPClientProtocolHandler.LIST;
				break;
			case "3":
				fileName = readInputFromClient();
				userCommand = FTPClientProtocolHandler.GET + "<" + fileName + ">";
				break;
			case "4":
				fileName = readInputFromClient();
				userCommand = FTPClientProtocolHandler.DELETE + "<" + fileName + ">";
				break;
			case "5":
				serverHello = false;
				serverWelcome = false;
				isAuthorized = false;
				user = new User(userDirectoryPath);
				userCommand = FTPClientProtocolHandler.LOGOUT;
				break;
			case "6":
				userCommand = FTPClientProtocolHandler.EXIT;
				break;

			default:
				System.out.println("The command is not correct.");
				return selectCommand();

			}

		return userCommand;
	}

	private String readInputFromClient()
	{

		String clientInput = null;
		try
		{
			return br.readLine();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return clientInput;
	}

}
