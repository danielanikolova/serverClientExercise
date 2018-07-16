package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import lib.AuthenticationProtocol;

public class ClientThread extends Thread{

	private Socket socket;
	private DataOutputStream output = null;
	private DataInputStream input = null;
	boolean serverHello = false;
	boolean serverWelcome = false;
	private String client_FQDN = "";
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public ClientThread(Socket socket) {

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


			String clientResponce = "";

			while (!socket.isClosed()) {

				String inputFromServer = input.readUTF();
				System.out.println("[Client]: A message from server: "+ inputFromServer);

				clientResponce = getClientResponseMessage(inputFromServer);

				output.writeUTF(clientResponce);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

}

	private String getClientResponseMessage(String message) {

		if (serverHello && serverWelcome) {

			String password = null;
			if (message.equals(AuthenticationProtocol.SERVER_PASSWORD_EXPECTED_MESSAGE)) {
				try {
					password = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return AuthenticationProtocol.USERNAME_MESSAGE + "<" + password + ">";
			}
		}
		// sends username to the server
		if (serverHello) {
			if (message.equals(AuthenticationProtocol.SERVER_WELCOME + " <<" + client_FQDN + ">>")) {

				serverWelcome = true;
				String username = null;
				try {
					username = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return AuthenticationProtocol.USERNAME_MESSAGE + "<" + username + ">";
			}
		}

		//Receives first message from Server
		if (serverHello == false && serverWelcome == false) {

			if (message.equals(AuthenticationProtocol.SERVER_HELLO_MESSAGE)) {

				serverHello = true;
				return AuthenticationProtocol.sendClientHelloMessage();
			}

		}


		return null;
	}
}
