package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FTPClientProtocolHandler {

	public static final String CLIENT_HELLO_MESSAGE = "HELLO";
	public static final String USERNAME_MESSAGE = "USER";
	public static final String USERPASSWORD_MESSAGE = "PASS";

	boolean serverHello = false;
	boolean serverWelcome = false;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	/*
	 * Process a string command sent by the server Returns a string to be sent to
	 * the server or empty string "" when user need to specify next command
	 */
	public String processServerMessage(String message) {

		String clientInput = null;

		// Handle server hello message
		if (FTPServerProtocolHandler.SERVER_HELLO_MESSAGE.equals(message)) {

			// mark serverHello so that for next step we know hello step passed ok
			serverHello = true;

			return CLIENT_HELLO_MESSAGE + " <" + Utils.getFQDN() + ">";

		} else if (serverHello) { // check whether serverHello is true so that we know previous step is completed.
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
		}else if(serverHello && serverWelcome) {
			if (message.startsWith(FTPServerProtocolHandler.SERVER_PASSWORD_EXPECTED_MESSAGE)) {


				System.out.println("Please enter password: ");

				clientInput = readInputFromClient();

				return USERPASSWORD_MESSAGE +  "<" + clientInput + ">";
			}
		}

		return null;
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
