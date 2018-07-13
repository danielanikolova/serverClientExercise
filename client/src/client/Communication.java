package client;

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
		System.out.println("Connected to server.");
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
			String inputFromServer = input.readUTF();
			System.out.println(inputFromServer);
			output.writeUTF("Hello Server ");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String consoleInput = null;

			while (!socket.isClosed()) {
				inputFromServer = input.readUTF();

				if (inputFromServer.equalsIgnoreCase("exit")) {
					closeSocket();
					break;
				}

				System.out.println(inputFromServer);

				consoleInput = br.readLine();

				if (consoleInput.equalsIgnoreCase("exit")) {
					output.writeUTF(consoleInput);
					closeSocket();
					break;
				}

				output.writeUTF(consoleInput);
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
