package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	private static ServerSocket ssocket;
	
	public static void main(String []args) throws IOException {
		ssocket = new ServerSocket(9876);
		
		new GameThread().start();
		
		while(true) {
			new PlayerThread(ssocket.accept()).start();
		}
	}

}
