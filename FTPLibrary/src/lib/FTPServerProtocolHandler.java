package lib;

public class FTPServerProtocolHandler {

	static final String SERVER_HELLO_MESSAGE = "Secure File Routing Server - ver.0.1";
	static final String SERVER_WELCOME = "200 Welcome";
	static final String SERVER_PASSWORD_EXPECTED_MESSAGE = "100 Continue with password: ";

	private String client_FQDN = "";

	public static String executeCommand(String command) {

		return null;

	}
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
			String username = message.substring(message.indexOf("<") + 1, message.indexOf(">"));

			//TODO implemet UserManager class to manage the authorized users;

			return FTPServerProtocolHandler.SERVER_PASSWORD_EXPECTED_MESSAGE;
		}


		return null;
	}

}
