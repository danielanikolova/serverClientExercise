package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FTPClientProtocolHandler {

	public static final String CLIENT_HELLO_MESSAGE = "HELLO";
	public static final String USERNAME_MESSAGE = "USER";
	public static final String USERPASSWORD_MESSAGE = "PASS";
	public static final String CLIENT_SEND_FILE = "SEND";
	public static final String LIST = "LIST";
	public static final String GET = "GET";
	public static final String DELETE = "DELETE";
	public static final String QUIT = "QUIT";
	public static final String CLOSE_COMMUNICATION = "Close communication";

	boolean serverHello = false;
	boolean serverWelcome = false;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	boolean isAuthorized = false;

	/*
	 * Process a string command sent by the server Returns a string to be sent to
	 * the server or empty string "" when user need to specify next command
	 */
	public String processServerMessage(String message) {

		String clientInput = null;

		// Handle server hello message

		if (isAuthorized) {

			if (message.startsWith(FTPServerProtocolHandler.SERVER_NO_SUCH_RECIPIENT)) {
				// here the user selects from the menu command
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.SERVER_GIVE_FILE_CONTENT_COMMAND)) {
				// TODO implement class that will sends encoded file line by line Base64
			}

			if (message.startsWith(FTPServerProtocolHandler.FILE_ACCEPTED)) {

			}

			if (message.startsWith(FTPServerProtocolHandler.INTERNAL_ERROR)) {
				// here the user selects from the menu command
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.PROVIDING_DIRECTORY_CONTENT)) {
				// TODO read files from the server
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.PROVIDING_FILE_CONTENT)) {
				// TODO check how to start copying the file
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.FILE_REMOVED)) {
				return selectCommand();
			}

			if (message.startsWith(FTPServerProtocolHandler.BYE)) {
				return FTPClientProtocolHandler.CLOSE_COMMUNICATION;
			}

		}

		else if (serverHello && serverWelcome) {
			if (message.startsWith(FTPServerProtocolHandler.SERVER_PASSWORD_EXPECTED_MESSAGE)) {

				System.out.println("Please enter password: ");

				clientInput = readInputFromClient();

				return USERPASSWORD_MESSAGE + "<" + clientInput + ">";
			}

			if (message.startsWith(FTPServerProtocolHandler.SERVER_LOGIN_SUCCESSFULL)) {

				isAuthorized = true;

				return selectCommand();

			}

		} else if (serverHello) {
			// check whether serverHello is true so that we know previous step is completed.
			// Handle server welcome message
			if (message.startsWith(FTPServerProtocolHandler.SERVER_WELCOME)) {
				String FQDN = Utils.getFQDN();
				if (message.substring(message.lastIndexOf("<") + 1, message.indexOf(">")).equals(FQDN)) {
					serverWelcome = true;

					System.out.println("[Client]: Handshake successfull. ");

					System.out.println("Please enter username: ");

					clientInput = readInputFromClient();

					return USERNAME_MESSAGE + "<" + clientInput + ">";
				}
			}
		} else if (FTPServerProtocolHandler.SERVER_HELLO_MESSAGE.equals(message)) {

			// mark serverHello so that for next step we know hello step passed ok
			serverHello = true;

			return CLIENT_HELLO_MESSAGE + " <" + Utils.getFQDN() + ">";

		}

		return null;
	}

	private String selectCommand() {

		System.out.println("Please enter menu option: ");
		System.out.println("1 - send file");
		System.out.println("2 - list files");
		System.out.println("3 - get");
		System.out.println("4 - delete");
		System.out.println("5 - quit");

		String input = readInputFromClient();
		String userCommand = null;
		String fileName = null;

		switch (input) {
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
			fileName = readInputFromClient();
			userCommand = FTPClientProtocolHandler.QUIT;
			break;

		default:
			System.out.println("The command is not correct.");
			return selectCommand();

		}

		return userCommand;
	}

	private String readInputFromClient() {

		String clientInput = null;
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return clientInput;
	}

}
