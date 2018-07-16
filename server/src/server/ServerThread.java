package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lib.AuthenticationProtocol;
import lib.FTPServerProtocolHandler;

public class ServerThread extends Thread{

	private Socket socket;
	private String localAddress;
	private DataOutputStream output = null;
	private DataInputStream input = null;
	private volatile boolean terminate = false;
	private String userName = null;
	private String password = null;

	public ServerThread(Socket socket, boolean terminate) {

		this.socket = socket;
		this.localAddress = socket.getLocalAddress().toString();
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

			String outputLine = AuthenticationProtocol.SERVER_HELLO_MESSAGE;

			// send hello message to client
			if (outputLine != null) {
				output.writeUTF(outputLine);
			}

			while (!socket.isClosed() && !terminate) {

				inputLine = input.readUTF();

				// log message from client to console
				System.out.println("[Server]: Client Command: " + inputLine);

				outputLine = executeMessage(inputLine);

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

	private String executeMessage(String inputLine) {

		String serverMessage = null;


		if (inputLine.startsWith(AuthenticationProtocol.CLIENT_HELLO_MESSAGE)) {

			String client_FQDN = inputLine.substring(inputLine.indexOf("<")+1, inputLine.indexOf(">"));

			serverMessage = AuthenticationProtocol.SERVER_WELCOME +  " <<" + client_FQDN + ">>";
		}

		if (inputLine.startsWith(AuthenticationProtocol.USERNAME_MESSAGE)) {
			 //here we have to check if the username is correct
			//Now we accept it is correct

			userName = AuthenticationProtocol.USERNAME_MESSAGE
					.substring(AuthenticationProtocol.USERNAME_MESSAGE.indexOf("<")+1,
							AuthenticationProtocol.USERNAME_MESSAGE.indexOf(">"));

			serverMessage = AuthenticationProtocol.SERVER_PASSWORD_EXPECTED_MESSAGE;

		}

		if (inputLine.startsWith(AuthenticationProtocol.USERPASSWORD_MESSAGE)) {
			password = AuthenticationProtocol.USERPASSWORD_MESSAGE
					.substring(AuthenticationProtocol.USERPASSWORD_MESSAGE.indexOf("<")+1,
							AuthenticationProtocol.USERPASSWORD_MESSAGE.indexOf(">"));


			//here we have to check if the password is correct
			//Now we accept it is correct

			serverMessage = AuthenticationProtocol.SERVER_LOGIN_SUCCESSFULL;
		}



		return serverMessage;
	}

}
