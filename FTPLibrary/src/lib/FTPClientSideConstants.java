package lib;


public final class FTPClientSideConstants extends FTPConstants{

	public static final String CLIENT_HELLO_MESSAGE = "HELLO";
	public static final String REGISTER_PLAIN = "REGISTER PLAIN";
	//sends registration password salted iteration count of time
	public static final String REGISTRATION_PASS = "REGISTRATION PASSWORD";
	//There is no command "Create account" AND "login" in the specification.
	public static final String REGISTER = "REGISTER";
	public static final String LOGIN = "LOGIN";
	//Client user command – USER <user name> - After receiving “200 Welcome <<client full qualified domain host name>>”
	//the user sends "USER" <user name>
	public static final String USER = "USER";

	public static final String REGISTER_TLS = "REGISTER TLS";


	//PASS <password – salted iteration count of times>
	public static final String PASS = "PASS";
	//SEND <file name> <user name>”
	public static final String SEND = "SEND";
	public static final String LIST = "LIST";
	//“GET <filename>”
	public static final String GET = "GET";
	//“DELETE <filename>”
	public static final String DELETE = "DELETE";
	public static final String LOGOUT = "LOGOUT";
	//“QUIT”
	public static final String QUIT = "QUIT";
	public static final String CLOSE_COMMUNICATION = "Close communication";

}
