package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.Message;

public class PlayerThread extends Thread {
	
	private String playerName;
	private Socket socket = null;
	
	
	private ObjectInputStream oin;
	private ObjectOutputStream oout;
	
	private Player player;
	
	private Message command;
	
	public PlayerThread(Socket socket) {
		this.socket = socket;
		
		try {
			oin = new ObjectInputStream(socket.getInputStream());
			oout = new ObjectOutputStream(socket.getOutputStream());			
		} catch (IOException e) {
			try {
				if(player != null) {
					GameEngine.deleteCH(player);
				}
				socket.close();
			} catch (IOException b) {
				// TODO Auto-generated catch block
				b.printStackTrace();
			}			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void run() {
		try {	
			command = (Message) oin.readObject();
			
			
			if(command.getCode().equals("CN/")) {
				playerName = command.getMsg();
			}
			
			player = new Player();
			player.setPlayerName(playerName);
			int UUID = player.getUUID();
			
			command.setCode("UUID/");
			command.setNumber(UUID);
			
			oout.writeObject(command);

	
			GameEngine.addSpectator(player);
				
			while(true) {
				Message command = new Message();
				command = (Message) oin.readObject();				
				// Ask GameField
				if(command.getCode().equals("GF/")) {
					int [][]game = GameEngine.getGame();
					command.setData(game);

					oout.writeObject(command);
					oout.flush();			
				} else if(command.getCode().equals("AD/")) {
					if(command.getNumber() == 0)
						player.setFly(false);
					else if(command.getNumber() == 1)
						player.setFly(true);
					GameEngine.spectator2Player(player);
				} else if(command.getCode().contains("MV/")) {
					int nextMove = command.getNumber();

					player.setNextMove(nextMove);
				} else if(command.getCode().contains("LC/")) {
					int newUUID = command.getNumber();
					Player player;
					synchronized(this) {
						player = GameEngine.getPlayingList().get(newUUID);
					}
					if(player != null) {
						if(player.isFly()) {
							command.setNumber(1);
						} else {
							command.setNumber(0);
						}
					} else {
						command.setNumber(-1);
					}
					oout.writeObject(command);
				} else if(command.getCode().contains("LIG/")) {
					if(GameEngine.getPlayingList().get(command.getNumber()) == null) {
						command.setNumber(1);
					} else {
						command.setNumber2(GameEngine.getPlayingList().get(command.getNumber()).getPoints());
						command.setNumber(0);
					}
					oout.writeObject(command);
				}
			} 			

		} catch (IOException | ClassNotFoundException e) {
			GameEngine.deleteCH(player);
			try {
				socket.close();
			} catch (IOException e1) { }
		}
	}

}
