

public class FTPClientProtocolHandler {

public static final String CLIENT_HELLO_MESSAGE = "HELLO";

	boolean serverHello = false;
	boolean serverWelcome = false;

	/*
	 * Process a string command sent by the server
	 * Returns a string to be sent to the server or empty string "" when user need to specify next command
	 */
	public String processServerMessage(String message) {
		// Handle server hello message
		if (FTPServerProtocolHandler.SERVER_HELLO_MESSAGE.equals(message)) {

			//mark serverHello so that for next step we know hello step passed ok
			serverHello = true;

			return CLIENT_HELLO_MESSAGE + " <" + Utils.getFQDN() + ">";

		} else if (serverHello) { // check whether serverHello is true so that we know previous step is completed.
			// Handle server welcome message
			if (message.startsWith(FTPServerProtocolHandler.SERVER_WELCOME)) {
				String FQDN = Utils.getFQDN();
				if (message.substring(message.lastIndexOf("<")+1, message.indexOf(">"))
						.equals(FQDN)) {
					serverWelcome = true;
					return "";
				}
			}
		}

		return null;
	}

}
