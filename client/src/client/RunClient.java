package client;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import lib.MessagingLogger;

public class RunClient
{
	public static void main(String[] args) throws IOException
	{

		Logger logger = MessagingLogger.getLogger();
		Socket communicationSocket = null;

		try
		{
			logger.info("[Client]: Connecting to server " + Constants.HOST);
			communicationSocket = new Socket(Constants.HOST, Constants.PORT);
			logger.info("[Client]: Connected to server host: " + Constants.HOST + " on port: " + Constants.PORT);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		TCPClientCommunicationManager communication = new TCPClientCommunicationManager(communicationSocket);

		communication.start();

		try
		{
			communication.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

	}

}
