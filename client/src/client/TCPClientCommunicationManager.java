package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lib.FTPClientProtocolHandler;

public class TCPClientCommunicationManager extends Thread{

	private Socket socket;
	private DataOutputStream output = null;
	private DataInputStream input = null;

	public TCPClientCommunicationManager(Socket socket) {

		this.socket = socket;
	}

	@Override
	public void run() {

		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			FTPClientProtocolHandler ftpClientProtocolHandler = new FTPClientProtocolHandler();

			String clientResponce = "";

			while (!socket.isClosed()) {

				String inputFromServer = input.readUTF();
				System.out.println("[Client]: A message from server: "+ inputFromServer);

				clientResponce = ftpClientProtocolHandler.processServerMessage(inputFromServer);
				if (clientResponce.equals(""))
				{
					System.out.println("[Client]: Handshake successfull. Waiting for command. ");
//					TODO GET COMMAND FROM USER
				}
				output.writeUTF(clientResponce);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void closeSocket() {
		try {
			socket.close();
			input.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
