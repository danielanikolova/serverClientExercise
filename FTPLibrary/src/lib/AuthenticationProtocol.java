package lib;

public class AuthenticationProtocol {

	public static final String SERVER_HELLO_MESSAGE = "Secure File Routing Server - ver.0.1";
	public static final String SERVER_WELCOME = "200 Welcome";
	public static final String SERVER_PASSWORD_EXPECTED_MESSAGE = "100 Continue with password: ";
	public static final String SERVER_LOGIN_SUCCESSFULL = "200 LoginSuccessfull";
	public static final String SERVER_AUTHENTICATION_FAILED = "403 Authentication failed";
	public static final String CLIENT_HELLO_MESSAGE = "HELLO";
	public static final String USERNAME_MESSAGE = "USER";
	public static final String USERPASSWORD_MESSAGE = "PASS";

	public static String sendHelloMessage() {

		return SERVER_HELLO_MESSAGE;
	}

	public static String sendClientHelloMessage() {
		return null;
	}

}
