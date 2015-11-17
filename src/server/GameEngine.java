package server;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class GameEngine {
	private static final int N = 15;
	private static final int M = 50;
	
	private static int[][]game;
	
	private static ConcurrentHashMap<Integer, Player> playingList = new ConcurrentHashMap<Integer, Player>();
	private static ConcurrentHashMap<Integer, Player> spectatorList = new ConcurrentHashMap<Integer, Player>();
	
	public GameEngine() {
		setGame(new int[N][M]);
	}
	
	public synchronized static void updateGF() {
				

		Iterator<Entry<Integer, Player>> it = GameEngine.getPlayingList().entrySet().iterator();

		
		while (it.hasNext()) {
	        Map.Entry<Integer,Player> pair = it.next();
	        Player player = (Player) pair.getValue();
	        
	        //----------------------------------------------------------------------------------
	        // Check if frog should die because of starving, or fly should get more point
	        long time = player.getLastEatenTime();
	        	
	        time = (System.currentTimeMillis()/1000L)-time;
	        	
	        if((time > 120) && !player.isFly()) {
	        	playing2Spectator(player.getUUID());
	        	continue;
	        } else if((time > 120) && player.isFly()) {
	        	player.addPoint();
	        	player.clearLastEatenTime();
	        }
	        //----------------------------------------------------------------------------------
	        
	        int playerNextMove = player.getNextMove();
	        
	        if(playerNextMove != 0) {
        		int x = player.getX();
        		int y = player.getY();
        		
        		int movemantx = 0;
        		int movemanty = 0;
        		
	        	// 2 => back, 8 => forward, 4 => left, 6 => right, 12 => fast back, 18 => fast forward, 14 fast left, 16 fast right
	        	if(playerNextMove == 2) {
	        		movemantx = +1;
	        	} else if(playerNextMove == 8) {
	        		movemantx = -1;
	        	} else if(playerNextMove == 4) {
	        		movemanty = -1;
	        	} else if(playerNextMove == 6) {
	        		movemanty = +1;
	        	} else if(playerNextMove == 12) {
	        		movemantx = +2;
	        	} else if(playerNextMove == 18) {
	        		movemantx = -2;
	        	} else if(playerNextMove == 14) {
	        		movemanty = -2;
	        	} else if(playerNextMove == 16) {
	        		movemanty = +2;
	        	}
	        	
        		if(((y+movemanty >= 0) && (x+movemantx >= 0)) && ((y+movemanty < M) && (x+movemantx < N))) {
        			// If there is no player just move
        			if(game[x+movemantx][y+movemanty] == 0) {
        				setGame(x+movemantx,y+movemanty,game[x][y]);
        				//game[x+movemantx][y+movemanty] = game[x][y];
        				
        				player.setX(x+movemantx);
        				player.setY(y+movemanty);
        				setGame(x,y,0);
        				//game[x][y] = 0;
        			} else {
        				
        				// if there is player check the situation
        				int UUID = game[x+movemantx][y+movemanty];
        				Player newPlayer = playingList.get(UUID);
        				// if player is frog
        				if(!player.isFly()) {
        					// if occupied player is fly
        					if(newPlayer.isFly()) {
        						// kill the player
        						playing2Spectator(UUID);
        						player.addPoint();
        						setGame(x+movemantx,y+movemanty,game[x][y]);
        						//game[x+movemantx][y+movemanty] = game[x][y];
                				player.setX(x+movemantx);
                				player.setY(y+movemanty); 
                				setGame(x,y,0);
        						//game[x][y] = 0;
        					}
        				} else {
        					if(!newPlayer.isFly()) {
        						playing2Spectator(player.getUUID());
        						newPlayer.addPoint();
        						setGame(x,y,0);
        						//game[x][y] = 0;      						
        					}
        				}
        			}
        		}	        	
	        }
	    }		
	}
	
	public static int getN() {
		return N;
	}
	
	public static int getM() {
		return M;
	}

	public synchronized static int[][] getGame() {
		return game;
	}

	public synchronized static void setGame(int[][] game) {
		GameEngine.game = game;
	}

	public synchronized static void setGame(int x, int y, int value) {
		GameEngine.game[x][y] = value;
	}
	
	public synchronized static ConcurrentHashMap<Integer, Player> getPlayingList() {
		return playingList;
	}

	public synchronized static void setPlayingList(ConcurrentHashMap<Integer, Player> playingList) {
		GameEngine.playingList = playingList;
	}

	public synchronized static ConcurrentHashMap<Integer, Player> getSpectatorList() {
		return spectatorList;
	}

	public synchronized static void setSpectatorList(ConcurrentHashMap<Integer, Player> spectatorList) {
		GameEngine.spectatorList = spectatorList;
	}
	
	public synchronized static void playing2Spectator(int UUID) {
		Player player = playingList.get(UUID);
		
		int x = player.getX();
		int y = player.getY();
		
		game[x][y] = 0;
		
		playingList.remove(UUID);
		
		player.clearPoint();
		spectatorList.put(UUID, player);
	}
	
	public synchronized static void spectator2Player(Player player) {
		int UUID = player.getUUID();
		spectatorList.remove(UUID);
		
		Random rand = new Random();
		int x = rand.nextInt(N);
		int y = rand.nextInt(M);
		
		while(game[x][y] != 0) {
			x = rand.nextInt(N);
			y = rand.nextInt(M);			
		}
		
		player.setX(x);
		player.setY(y);
		
		game[x][y] = UUID;
		
		player.clearPoint();
		player.clearLastEatenTime();
		playingList.put(UUID, player);
	}	
	
	public synchronized static void addSpectator(Player player) {
		spectatorList.put(player.getUUID(), player);
	}
	
	public synchronized static void deleteCH(Player player) {
		int UUID = player.getUUID();
		int x = player.getX();
		int y = player.getY();
		
		game[x][y] = 0;
		
		if(playingList.containsKey(UUID)) {
			playingList.remove(UUID);
		} else if(spectatorList.containsKey(UUID)) {
			spectatorList.remove(UUID);
		}
	}	
}
