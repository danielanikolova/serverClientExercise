package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import lib.MessagingLogger;

public class ConnectionThread extends Thread{

	private int serverPort;
	private ServerSocket connectionSocket;
	private static Logger logger = MessagingLogger.getLogger();

	public ConnectionThread(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public void run() {

		try {
			connectionSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}


		System.out.println("[Server]: Server is running...");
		while (!Thread.interrupted()) {

			try {

				// wait for a connection from client. Connection is made here. The communication
				// worker thread
				// will close it when exiting
				Socket communicationSocket = connectionSocket.accept();
				logger.info("New connection from " + communicationSocket.getInetAddress().getHostAddress());

				TCPServerCommunication communication = new TCPServerCommunication(communicationSocket);

				communication.start();


			} catch (Exception e) {
				logger.info("500 Error. ConnectionThread interrupted");
			}
		}

		try {
			CommunicationManager.closeCommunications();
			connectionSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}



	}

}
