package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RunClient {

	public static void main(String[] args) throws IOException {

		String host = "127.0.0.1";

		int port = 4000;

		Socket communicationSocket = null;
		DataInputStream input = null;
		DataOutputStream output = null;



		try {
			communicationSocket = new Socket(host, port);
			System.out.println("Connecting to server " + host);
			input = new DataInputStream(communicationSocket.getInputStream());
			output = new DataOutputStream(communicationSocket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}


		String inputFromServer = input.readUTF();
		System.out.println(inputFromServer);
		output.writeUTF("Hello Server " + host);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String consoleInput = null;
		
		while (true) {
			inputFromServer = input.readUTF();
			
			if (inputFromServer.equalsIgnoreCase("exit")) {
				break;
			}
			
			System.out.println(inputFromServer);
			
			consoleInput = br.readLine();
						
			if (consoleInput.equalsIgnoreCase("exit")) {
				output.writeUTF(consoleInput);
				break;
			}
			
			output.writeUTF(consoleInput);
		}


		communicationSocket.close();
		input.close();
		output.close();




	}

}
