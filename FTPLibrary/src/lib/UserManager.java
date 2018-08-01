package lib;

import java.util.HashMap;
import java.util.Map;

public class UserManager
{
	private static UserManager instance = new UserManager();
	private Map<String, User> allUsers = new HashMap<>();
	private Map<String, User> loggedUsers = new HashMap<>();

	private UserManager()
	{
	}

	public static UserManager getInstance()
	{
		return instance;
	}

	public void addUser(User user)
	{
		allUsers.put(user.getUserName(), user);
	}

	public void addLoggedInUser(User user)
	{
		loggedUsers.put(user.getUserName(), user);
	}

	public void removeLoggedUser(User user)
	{
		loggedUsers.remove(user.getUserName());
	}

	public boolean containsUserWithUsername(String username)
	{
		return allUsers.containsKey(username);
	}

	public User getUserByUsername(String username)
	{
		return allUsers.get(username);
	}

}
