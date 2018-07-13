package serverClientLibrary;

import java.net.Socket;

public class OutputThread extends Thread{

	private Socket socket;

	public OutputThread(Socket socket) {
		this.socket = socket;
	}

}
