package lib;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class User
{
	public static boolean isAuthorized = false;
	private String client_FQDN;
	private String userName;
	private String password;
	private String directoryPath = "C:\\Users\\danie\\Desktop\\serverClientExercise\\destination" + File.separator
			+ userName;
	private Map<String, File> userFiles = new HashMap<>();

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getPassword()
	{
		return password;
	}

	public String getClient_FQDN()
	{
		return client_FQDN;
	}

	public void setClient_FQDN(String client_FQDN)
	{
		this.client_FQDN = client_FQDN;
	}

	// returns file from the file collection of the user
	public File getFileByName(String name)
	{

		File file = null;

		if (userFiles.containsKey(name))
		{
			file = userFiles.get(name);
			// file = new File(directoryPath + File.separator + name);
		}

		return file;

	}

	// adds file to the file collection of the user
	public void addFileInUserLibrary(String name, File file)
	{
		userFiles.put(name, file);
	}

	// checks if there is a file with given name
	public boolean containsFile(String name)
	{
		if (userFiles.containsKey(name))
		{
			return true;
		}
		return false;
	}

	public String getDirectoryPath()
	{
		return directoryPath;
	}

}
