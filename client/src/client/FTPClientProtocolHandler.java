package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import lib.CopyProcessor;
import lib.FTPClientSideConstants;
import lib.FTPProtocolHandler;
import lib.FTPServerSideConstants;
import lib.MessagingLogger;
import lib.User;
import lib.Utils;

public class FTPClientProtocolHandler implements FTPProtocolHandler
{

	private static Logger logger = MessagingLogger.getLogger();


	private boolean serverHelloAccepted = false;
	private boolean serverWelcomeAccepted = false;
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
	public String processMessage(String message)
	{

		String clientInput = null;

		// Handle server hello message
		if (FTPServerSideConstants.SERVER_HELLO_MESSAGE.equals(message))
		{

			// mark serverHello so that for next step we know hello step passed ok
			serverHelloAccepted = true;

			return FTPClientSideConstants.CLIENT_HELLO_MESSAGE + " <" + Utils.getFQDN() + ">";

		}

		if (serverHelloAccepted)
		{

			System.out.println("we are in server wellcome message clientside");
			// check whether serverHello is true so that we know previous step is completed.
			// Handle server welcome message
			if (message.startsWith(FTPServerSideConstants.SERVER_WELCOME))
			{
				String FQDN = Utils.getFQDN();
				if (message.substring(message.lastIndexOf("<") + 1, message.indexOf(">")).equals(FQDN))
				{
					serverWelcomeAccepted = true;

					logger.info("[Client]: Handshake successfull. ");

					System.out.println("Select action:");
					System.out.println("1 - register");
					System.out.println("2 - login");
					System.out.println("3 - exit");

					String clientCommand = readInputFromClient();

					switch (clientCommand)
						{
						case "1":
							return FTPClientSideConstants.REGISTER_PLAIN;
						case "2":
							return FTPClientSideConstants.LOGIN;
						case "3":
							return FTPClientSideConstants.QUIT;

						}

				}
			}
		}

		if (serverHelloAccepted && serverWelcomeAccepted)
		{
			if (message.startsWith(FTPServerSideConstants.ENTER_USERNAME))
			{

				System.out.println("Please enter user name: ");

				clientInput = readInputFromClient();
				user.setUserName(clientInput);

				return FTPClientSideConstants.USER + "<" + clientInput + ">";
			}

			if (message.startsWith(FTPServerSideConstants.CONTINUE_WITH_PASSWORD))
			{
				System.out.println("Enter password:");
				String password = readInputFromClient();

				return FTPClientSideConstants.PASS + "<" + password + ">";
			}

			if (message.startsWith(FTPServerSideConstants.SERVER_LOGIN_SUCCESSFULL))
			{

				isAuthorized = true;

				return selectCommand();

			}

		}

		if (isAuthorized)
		{

			if (message.startsWith(FTPServerSideConstants.WRONG_RECIPIENT))
			{
				// here the user selects from the menu command
				return selectCommand();
			}

			if (message.startsWith(FTPServerSideConstants.GIVE_THE_CONTENT))
			{
				String sourceFile = readInputFromClient();
				return FTPClientSideConstants.PROVIDING_FILE_CONTENT + "<" + sourceFile + ">";
			}


			if (message.startsWith(FTPClientSideConstants.PROVIDING_FILE_CONTENT))
			{

				CopyProcessor copyProcessor = new CopyProcessor(input, output);
				String source = message.substring(message.indexOf("<") + 1,
						message.indexOf(">"));
				String copyResult = copyProcessor.readFile(source);

				return copyResult;

			}

			if (message.startsWith(FTPServerSideConstants.FILE_ACCEPTED))
			{
				return selectCommand();
			}

			if (message.startsWith(FTPServerSideConstants.INTERNAL_ERROR))
			{
				// here the user selects from the menu command
				return selectCommand();
			}

			if (message.startsWith(FTPServerSideConstants.PROVIDING_DIRECTORY_CONTENT))
			{
				// TODO read files from the server
				return selectCommand();
			}

			if (message.startsWith(FTPServerSideConstants.PROVIDING_FILE_CONTENT))
			{
				// TODO check how to start copying the file
				return selectCommand();
			}

			if (message.startsWith(FTPServerSideConstants.FILE_REMOVED))
			{
				return selectCommand();
			}

			if (message.startsWith(FTPServerSideConstants.BYE))
			{
				return FTPClientSideConstants.CLOSE_COMMUNICATION;
			}

		}


		if (message.startsWith(FTPServerSideConstants.SERVER_AUTHENTICATION_FAILED))
		{
			return FTPClientSideConstants.LOGIN;
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
				userCommand = FTPClientSideConstants.SEND + "<" + fileName + ">" + "<" + username + ">";
				break;
			case "2":
				userCommand = FTPClientSideConstants.LIST;
				break;
			case "3":
				fileName = readInputFromClient();
				userCommand = FTPClientSideConstants.GET + "<" + fileName + ">";
				break;
			case "4":
				fileName = readInputFromClient();
				userCommand = FTPClientSideConstants.DELETE + "<" + fileName + ">";
				break;
			case "5":
				serverHelloAccepted = false;
				serverWelcomeAccepted = false;
				isAuthorized = false;
				user = new User(userDirectoryPath);
				userCommand = FTPClientSideConstants.LOGOUT;
				break;
			case "6":
				userCommand = FTPClientSideConstants.QUIT;
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
