package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	private static final int PORT = 4000;

	public static void main(String[] args) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ServerSocket serverSocket = null;
		Socket communicationSocket = null;

		//Used to send terminate command to  worker threads
		boolean terminate = false;

		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("[Server]: Server is running...");

			// wait for a connection from client. Connection is made here. The  communication worker thread 
			//will close it when exiting
			communicationSocket = serverSocket.accept();

		} catch (IOException e) {
			e.printStackTrace();
		}

		TCPServerCommunicationManager communication = new TCPServerCommunicationManager(communicationSocket, terminate);

		communication.start();

		String consoleInput = null;
		while (true) {

			if (terminate != true) {
				try {
					consoleInput = br.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (consoleInput.equalsIgnoreCase("exit")) {
					terminate = true;
				}
				
				if (!communication.isAlive()) {
					System.out.println("Server is closed");
					break;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				
			}

		}

	}

}
