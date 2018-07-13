package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RunServer {

	public static void main(String[] args) throws IOException {

		int port = 4000;
		ServerSocket serverSocket = null;
		Socket communicationSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server is running...");
			communicationSocket = serverSocket.accept();

		} catch (IOException e) {
			e.printStackTrace();
		}

		Communication communication = new Communication(communicationSocket);

		communication.start();

		try {
			communication.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		System.out.println("Server is closed");

	}

}
