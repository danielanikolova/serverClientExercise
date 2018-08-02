package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import lib.FTPClientSideConstants;
import lib.FTPServerSideConstants;
import lib.MessagingLogger;

public class TCPClientCommunicationManager extends Thread
{
	private static Logger logger = MessagingLogger.getLogger();
	private Socket socket;
	private DataOutputStream output = null;
	private DataInputStream input = null;

	public TCPClientCommunicationManager(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	public void run()
	{
		try
		{
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			FTPClientProtocolHandler ftpClientProtocolHandler = new FTPClientProtocolHandler(input, output, Constants.CLIENT_DIRECTORY_PATH);

			String clientResponse = "";

			while (!socket.isClosed() && !Thread.interrupted())
			{
				String inputFromServer = input.readUTF();
				logger.info("[Client]: A message from server: " + inputFromServer);

				if (inputFromServer.startsWith(FTPServerSideConstants.BYE))
				{
					closeCommunication();
				}

				clientResponse = ftpClientProtocolHandler.processMessage(inputFromServer);

				if (clientResponse.equals(FTPClientSideConstants.CLOSE_COMMUNICATION))
				{
					closeCommunication();
				}

				output.writeUTF(clientResponse);

				if (clientResponse.startsWith(FTPClientSideConstants.QUIT))
				{
					closeCommunication();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void closeCommunication()
	{
		try
		{
			socket.close();
			input.close();
			output.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
