package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class RunClient {

	private static String host = "127.0.0.1";
	private static int port = 4000;
	private static boolean running = true;
	
	public static void main(String[] args) throws IOException {

		Socket communicationSocket = null;

		try {
			System.out.println("[Client]: Connecting to server " + host);
			communicationSocket = new Socket(host, port);
			System.out.println("[Client]: Connected to server host: " + host + " on port: " + port);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TCPClientCommunicationManager communication = new TCPClientCommunicationManager(communicationSocket);

		communication.start();
		
		try {
			communication.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}

}
