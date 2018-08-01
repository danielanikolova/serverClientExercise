package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import lib.CopyProcessor;
import lib.FTPClientProtocolHandler;
import lib.FTPProtocolHandler;
import lib.FTPServerProtocolHandler;
import lib.MessagingLogger;

/*
 * Responsible for TCP Communication. Receives and sends TCP data and pass the data as string to ProtocolHandler class.
 */

public class TCPServerCommunicationManager extends Thread
{
	private static Logger logger = MessagingLogger.getLogger();
	private Socket socket;
	private DataOutputStream output = null;
	private DataInputStream input = null;

	public TCPServerCommunicationManager(Socket socket)
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

			FTPServerProtocolHandler ftpServerProtocolHandler = new FTPServerProtocolHandler();

			while (!socket.isClosed() && !interrupted())
			{

				inputLine = input.readUTF();

				// log message from client to console
				logger.info("[Server]: Client Command: " + inputLine);

				if (inputLine.startsWith(FTPClientProtocolHandler.EXIT))
				{
					closeConnection();
					break;
				}

				if (inputLine.startsWith(FTPProtocolHandler.START_COPY_PROCESSOR_WRITE))
				{
					String fileName = inputLine.substring(inputLine.indexOf("<") + 1, inputLine.indexOf(">"));
					CopyProcessor copyProcessor = new CopyProcessor(input, output);
					String copyResult = copyProcessor.writeFile(fileName, ftpServerProtocolHandler.getRecipient());
					output.writeUTF(copyResult);

				} else if (inputLine.startsWith(FTPProtocolHandler.START_COPY_PROCESSOR_READ))
				{
					String source = inputLine.substring(inputLine.indexOf("<") + 1, inputLine.indexOf(">"));
					CopyProcessor copyProcessor = new CopyProcessor(input, output);
					copyProcessor.readFile(source);
				} else
				{
					outputLine = ftpServerProtocolHandler.executeMessage(inputLine);
				}

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

	private void closeConnection()
	{
		try
		{
			output.close();
			input.close();
			socket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
