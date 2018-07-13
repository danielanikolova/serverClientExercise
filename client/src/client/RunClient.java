package client;

import java.io.IOException;
import java.net.Socket;

public class RunClient {

	public static void main(String[] args) throws IOException {

		String host = "127.0.0.1";

		int port = 4000;

		Socket communicationSocket = null;

		try {
			System.out.println("Connecting to server " + host);
			communicationSocket = new Socket(host, port);			
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		Communication communication = new Communication(communicationSocket);
		
		communication.start();
		
		try {
			communication.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("Client disconnected.");

	}

}
