package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import lib.CopyProcessor;
import lib.FTPClientSideConstants;
import lib.FTPConstants;
import lib.FTPProtocolHandler;
import lib.FTPServerSideConstants;
import lib.MessagingLogger;
import lib.MyCrypto;
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
	private String salt = null;
	int iterations = 0;
	String secretKey = null;

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

					return FTPClientSideConstants.REGISTER_PLAIN;

				}
			}
		}

		if (serverHelloAccepted && serverWelcomeAccepted)
		{

			if (message.startsWith(FTPServerSideConstants.CONTINUE_WITH_PASSWORD))
			{

				salt = message.substring(message.charAt('<')+1,message.charAt('>') );
				iterations =Integer.parseInt(message.substring(message.lastIndexOf(('<')+ 1, message.lastIndexOf('>'))));


				secretKey = MyCrypto.saltParameter(salt, FTPConstants.REGISTRATION_PASS, iterations);

				return FTPClientSideConstants.PASS + "<" + secretKey + ">";
			}

			if (message.startsWith(FTPServerSideConstants.REGISTRATION_ALLOWED)) {

				isAuthorized = true;
				String userInput = selectLoginOrRegister();



				if (userInput.equals("register")) {

					//gets user name from client and encrypt it
					String username = MyCrypto.encodeBase64(getPassword());
					//gets password from client, hashes and encrypt it
					String password = MyCrypto.encodeBase64(MyCrypto.generateHash(getUserName()));

						return FTPClientSideConstants.REGISTER + "<" + username + ">" + "<" + password + ">";
				}else {
					//login existing user in the database

					String salt = MyCrypto.generateRandomSalt();
					int iterations = MyCrypto.getRandomIterations();

					//gets user name from client and encrypt it
					String username = getPassword();
					//gets password from client, hashes and encrypt it
					String passwordHash = MyCrypto.generateHash(getUserName());

					String usernameSalted = MyCrypto.saltParameter(salt, username, iterations);
					String passwordSalted = MyCrypto.saltParameter(salt, passwordHash, iterations);

					 return FTPClientSideConstants.LOGIN + "<" + usernameSalted + ">" + "<" + passwordSalted + ">"
					 							+ "<" + salt + ">" + "<" + iterations + ">";
				}

			}

//			if (message.startsWith(FTPServerSideConstants.SERVER_LOGIN_SUCCESSFULL))
//			{
//
//				isAuthorized = true;
//
//				return selectCommand();
//
//			}

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

	private String getPassword() {
		System.out.println("Enter username: ");

		return readInputFromClient();
	}

	private String getUserName() {
		System.out.println("Enter password: ");
		return readInputFromClient();
	}

	private String selectLoginOrRegister() {
		System.out.println("Select action:");
		System.out.println("1 - register");
		System.out.println("2 - login");

		String userInput = readInputFromClient();
		String choise = null;
		switch (userInput) {
		case "1":
			choise = "register";
			break;
		case "2":
			choise = "login";
			break;

		default:
			selectLoginOrRegister();
			break;
		}
		return choise;
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
