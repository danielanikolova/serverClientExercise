
public class FTPServerProtocolHandler {

	static final String SERVER_HELLO_MESSAGE = "Secure File Routing Server - ver.0.1";
	static final String SERVER_WELCOME = "200 Welcome";
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


		return null;
	}

}
