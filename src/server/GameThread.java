package server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class GameThread extends Thread {
	private final long GameStartTime;
	private Iterator<Entry<Integer, Player>> it;
	
	public GameThread() {
		GameStartTime = System.currentTimeMillis()/1000L;
	}
	
	public void run() {
		
		new GameEngine();
		while(true) {
			// Clear the screen
			final String ANSI_CLS = "\u001b[2J";
	        final String ANSI_HOME = "\u001b[H";
	        System.out.print(ANSI_CLS + ANSI_HOME);
	        System.out.flush();
			
			long time = (System.currentTimeMillis()/1000L) - GameStartTime;
			
			// Debugging Tools
			
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("Game Time : " + String.valueOf(time) + " seconds");
			System.out.println("Note : In the game field, X means Fly and Y  means Frog");
			System.out.println("--------------------------------------------------------------------------------");
			
			// Writing Game Field to console
			int [][]game = GameEngine.getGame();
			
			for(int i = 0; i < GameEngine.getN(); i++) {
				for(int j = 0; j < GameEngine.getM(); j++) {
					int pUUID = game[i][j];
					if(pUUID > 0) {
						Player player = GameEngine.getPlayingList().get(pUUID);
					
						if(player.isFly()) {
							System.out.print("X");
						} else {
							System.out.print("Y");
						}
					} else {
						System.out.print(".");
					}
				}
				
				System.out.println("");
			}
			
			System.out.println("--------------------------------------------------------------------------------");	
			System.out.println("Player List - " + GameEngine.getPlayingList().size() + " Players");
			
			it = GameEngine.getPlayingList().entrySet().iterator();
				
			while (it.hasNext()) {
				Map.Entry<Integer,Player> pair = it.next();
				Player player = (Player) pair.getValue();
				    
				System.out.println(player.getPlayerName() + " - " + player.getPoints() + " points ");
			}
			
			
			System.out.println("--------------------------------------------------------------------------------");	
			System.out.println("Spectator List - " + GameEngine.getSpectatorList().size() + " Players");	
			
			it = GameEngine.getSpectatorList().entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry<Integer,Player> pair = it.next();
				Player player = (Player) pair.getValue();
				
				System.out.println(player.getPlayerName() + " - " + player.getPoints() + " points ");
			}

			
			GameEngine.updateGF();
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
