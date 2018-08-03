package lib;

public final class FTPServerSideConstants extends FTPConstants{

	public static final String SERVER_HELLO_MESSAGE = "Secure File Routing Server - ver.0.1";
	public static final String SERVER_WELCOME = "200 Welcome";
	//“100 Continue with password - Salt: <salt bytes encoded in Base64> Iterations: <iteration count>”
	public static final String CONTINUE_WITH_PASSWORD = "100 Continue with password: ";
	public static final String ENTER_USERNAME = "Enter user name";
	public static final String ACCOUNT_CREATED = "Created account";
	public static final String LOGIN = "Login";
	//Server response: “200 Login Successful” or “403 Authentication failed”
	public static final String SERVER_LOGIN_SUCCESSFULL = "200 Login Successfull";
	public static final String SERVER_AUTHENTICATION_FAILED = "403 Authentication failed";
	//“100 Give the content” or “400 No such recipient”  or “401 Unauthorized” or “500 <other internal error description>”
	public static final String GIVE_THE_CONTENT = "100 Give the content";
	public static final String WRONG_RECIPIENT = "400 no such recipient";
	public static final String INTERNAL_ERROR = "500 other internal error description";
	public static final String UNAUTHORIZED = "401 Unauthorized";

	public static final String FILE_NOT_FOUND = "404 Not found";
	public static final String FILE_REMOVED = "200 File removed";


	public static final String PROVIDING_DIRECTORY_CONTENT = "200 Providing directory content";
	public static final String BYE = "200 Bye";
}
