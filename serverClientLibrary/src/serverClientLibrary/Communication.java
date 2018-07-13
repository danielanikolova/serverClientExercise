package serverClientLibrary;


import java.net.Socket;

public class Communication extends Thread {

	private Socket socket;
	private String localAddress;


	public Communication(Socket socket) {

		this.socket = socket;
		this.localAddress = socket.getLocalAddress().toString();

	}

	@Override
	public void run() {

//		boolean continueCommunication = FTPServerprotocol.secureConnectionEstablished(socket);

		InputThread inputThread = new InputThread(socket);
		OutputThread outputThread = new OutputThread(socket);

		outputThread.start();
		inputThread.start();

		try {
			outputThread.join();
			inputThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
