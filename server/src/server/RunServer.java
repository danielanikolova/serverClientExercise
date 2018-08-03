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

		ConnectionThread connectionThread = new ConnectionThread(Constants.PORT);
		connectionThread.start();

//		try {
//			connectionThread.join();
//		} catch (InterruptedException e1) {
//			logger.info("500 Error. ConnectionManagerThread interrupted");
//		}

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

		connectionThread.interrupt();
		System.exit(0);

	}

}
