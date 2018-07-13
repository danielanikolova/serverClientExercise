package serverClientLibrary;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class InputThread extends Thread {

	private Socket socket;

	public InputThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		DataInputStream input = null;

		try {
			input = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}


		String inputLine = null;

		try {
			inputLine = input.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (inputLine != null) {

		}

	}

}
