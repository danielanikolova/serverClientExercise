package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class RunServer {

	public static void main(String[] args) throws IOException {

	int port = 4000;
	ServerSocket serverSocket = null;
	Socket communicationSocket = null;
	DataOutputStream  output = null;
	DataInputStream input = null;


	try {
		serverSocket = new ServerSocket(port);
		System.out.println("Server is running...");
		communicationSocket = serverSocket.accept();

		output = new DataOutputStream(communicationSocket.getOutputStream());
		input = new DataInputStream(communicationSocket.getInputStream());

	} catch (IOException e) {
		e.printStackTrace();
	}

	String inputLine;
	String outputLine = "Hello client";

	output.writeUTF(outputLine);

	inputLine = input.readUTF();
	System.out.println(inputLine);

	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	String consoleInput = br.readLine();

	while (true) {

		if (consoleInput.equalsIgnoreCase("exit")) {
			output.writeUTF(consoleInput);
			break;
		}
		output.writeUTF(consoleInput);

		inputLine = input.readUTF();

		if (inputLine.equalsIgnoreCase("exit")) {
			break;
		}

		System.out.println(inputLine);

		consoleInput = br.readLine();

	}

	output.close();
	input.close();
	communicationSocket.close();
	serverSocket.close();



	}

}
