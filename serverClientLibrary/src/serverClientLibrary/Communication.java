package serverClientLibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Communication extends Thread {

	private Socket socket;
	private String localAddress;
	private DataOutputStream output = null;
	private DataInputStream input = null;

	public Communication(Socket socket) {

		this.socket = socket;
		this.localAddress = socket.getLocalAddress().toString();

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
