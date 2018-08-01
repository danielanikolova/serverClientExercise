package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import lib.MessagingLogger;

public class RunServer
{
	private static Logger logger = MessagingLogger.getLogger();

	public static void main(String[] args) throws IOException
	{

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ServerSocket serverSocket = null;
		Socket communicationSocket = null;

		try
		{
			serverSocket = new ServerSocket(Constants.PORT);
			System.out.println("[Server]: Server is running...");

			// wait for a connection from client. Connection is made here. The communication
			// worker thread
			// will close it when exiting
			communicationSocket = serverSocket.accept();

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		TCPServerCommunicationManager communication = new TCPServerCommunicationManager(communicationSocket);

		communication.start();
		try
		{
			communication.join();
		} catch (InterruptedException e2)
		{
			logger.info("Communication is closed");
		}

		System.out.println("Please enter \"exit\" to close the server.");
		String consoleInput = br.readLine();

		while (!consoleInput.equalsIgnoreCase("exit"))
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			consoleInput = br.readLine();
		}

		communication.interrupt();
		serverSocket.close();

	}

}
