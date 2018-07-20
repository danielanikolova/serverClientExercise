package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lib.FTPServerProtocolHandler;

/*
 * Responsible for TCP Communication. Receives and sends TCP data and pass the data as string to ProtocolHandler class.
 */

public class TCPServerCommunicationManager extends Thread{


	private Socket socket;
	private DataOutputStream output = null;
	private DataInputStream input = null;
	private volatile boolean terminate;

	public TCPServerCommunicationManager(Socket socket, boolean terminate) {

		this.socket = socket;
		this.terminate = terminate;
	}

	@Override
	public void run() {

		try {
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			String inputLine;

			/*
			 * Decide who speaks first; server or client. Check whether protocol requires
			 * that first message comes from server and if yes, server speaks first so send
			 * message to client.
			 */

			String outputLine = FTPServerProtocolHandler.getHelloMessage();

			// send hello message to client
			if (outputLine != null) {
				output.writeUTF(outputLine);
			}

			FTPServerProtocolHandler ftpServerProtocolHandler = new FTPServerProtocolHandler();

			while (!socket.isClosed() && !terminate) {

				inputLine = input.readUTF();

				// log message from client to console
				System.out.println("[Server]: Client Command: " + inputLine);

				outputLine = ftpServerProtocolHandler.executeMessage(inputLine);
				
				if (outputLine.equals("200 Bye")) {
					output.writeUTF(outputLine);
					closeConnection();
					terminate = true;
				}
				
				if (outputLine != null && outputLine.length()>0) {
					output.writeUTF(outputLine);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}

	}

	private void closeConnection() {
		try {
			output.close();
			input.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
