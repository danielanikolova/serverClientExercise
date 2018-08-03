package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lib.User;

public class CommunicationManager
{
	private static CommunicationManager instance = new CommunicationManager();
	private Map<String, User> allUsers = new HashMap<>();
	private Map<String, User> loggedUsers = new HashMap<>();
	private static List<TCPServerCommunication> activeCommunications = new LinkedList<TCPServerCommunication>();

	private CommunicationManager()
	{
	}

	public static CommunicationManager getInstance()
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

	public void addCommunication(TCPServerCommunication communication) {
		activeCommunications.add(communication);
	}

	public void removeCommunication(TCPServerCommunication communication) {
		activeCommunications.remove(communication);
	}

	public static void closeCommunications() {
		for (TCPServerCommunication communication : activeCommunications) {
			communication.closeConnection();
		}

	}

}
