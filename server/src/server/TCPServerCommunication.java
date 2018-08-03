package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import lib.FTPClientSideConstants;
import lib.MessagingLogger;

/*
 * Responsible for TCP Communication. Receives and sends TCP data and pass the data as string to ProtocolHandler class.
 */

public class TCPServerCommunication extends Thread
{
	private static Logger logger = MessagingLogger.getLogger();
	private Socket socket;
	private DataOutputStream output = null;
	private DataInputStream input = null;


	public TCPServerCommunication(Socket socket)
	{
		this.socket = socket;

	}

	@Override
	public void run()
	{

		try
		{
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			String inputLine = null;

			/*
			 * Decide who speaks first; server or client. Check whether protocol requires
			 * that first message comes from server and if yes, server speaks first so send
			 * message to client.
			 */

			String outputLine = FTPServerProtocolHandler.getHelloMessage();

			// send hello message to client
			if (outputLine != null)
			{
				output.writeUTF(outputLine);
			}

			FTPServerProtocolHandler ftpServerProtocolHandler = new FTPServerProtocolHandler(input, output, Constants.SERVER_DIRECTORY_PATH);
			CommunicationManager.getInstance().addCommunication(this);

			while (!socket.isClosed() && !interrupted())
			{

				inputLine = input.readUTF();

				// log message from client to console
				logger.info("[Server]: Client Command: " + inputLine);

				if (inputLine.startsWith(FTPClientSideConstants.QUIT))
				{
					closeConnection();
					break;
				}

				outputLine = ftpServerProtocolHandler.processMessage(inputLine);


				if (outputLine != null && outputLine.length() > 0)
				{
					output.writeUTF(outputLine);
				}
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnection();
		}

	}

	public void closeConnection()
	{
		try
		{
			CommunicationManager.getInstance().removeCommunication(this);
			output.close();
			input.close();
			socket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}


}
