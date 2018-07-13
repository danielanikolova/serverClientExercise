package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

	@Override
	public void run() {

		try {
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			String inputLine;
			String outputLine = "Hello client";

			output.writeUTF(outputLine);

			inputLine = input.readUTF();
			System.out.println(inputLine);

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String consoleInput = br.readLine();

			while (!socket.isClosed()) {

				if (consoleInput.equalsIgnoreCase("exit")) {
					output.writeUTF(consoleInput);
					closeConnection();
					break;
				}
				output.writeUTF(consoleInput);

				inputLine = input.readUTF();

				if (inputLine.equalsIgnoreCase("exit")) {
					closeConnection();
					break;
				}

				System.out.println(inputLine);

				consoleInput = br.readLine();

//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

			}

		} catch (IOException e) {
			e.printStackTrace();
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
