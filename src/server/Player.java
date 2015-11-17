package server;

public class Player {
	private static int counter = 1;
	private final int UUID;
	
	private String playerName;
	
	private boolean fly;
	
	private int points;
	
	private int x;
	private int y;
	
	private long joiningTime;
	private long lastScoreTime;
	private long lastEatenTime;	
	
	private int nextMove; // 4 => left 6=> right 8 => front 2 => back
	
	public Player() {
		UUID = counter++;
	}

	public int getUUID() {
		return UUID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getPoints() {
		return points;
	}

	public void addPoint() {
		this.points = this.points + 1;
		setLastScoreTime(System.currentTimeMillis()/1000L);
		setLastEatenTime(System.currentTimeMillis()/1000L);		
	}
	
	public void clearPoint() {
		this.points = 0;
	}	

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public long getJoiningTime() {
		return joiningTime;
	}
	
	public void setJoiningTime() {
		this.joiningTime = System.currentTimeMillis()/1000L;
		setLastScoreTime(System.currentTimeMillis()/1000L);
		setLastEatenTime(System.currentTimeMillis()/1000L);
	}
	
	public long getLastScoreTime() {
		return lastScoreTime;
	}

	public void setLastScoreTime(long lastScoreTime) {
		this.lastScoreTime = lastScoreTime;
	}

	public long getLastEatenTime() {
		return lastEatenTime;
	}

	public void clearLastEatenTime() {
		setLastEatenTime(System.currentTimeMillis()/1000L);		
	}
	
	public void setLastEatenTime(long lastEatenTime) {
		this.lastEatenTime = lastEatenTime;
	}	

	public int getNextMove() {
		int temp = nextMove;
		nextMove = 0;
		
		return temp;
	}

	public void setNextMove(int nextMove) {
		if(nextMove == 2 || nextMove == 4 || nextMove == 6 || nextMove == 8 || nextMove == 12 || nextMove == 14 || nextMove == 16 || nextMove == 18) {
			this.nextMove = nextMove;
		} else {
			this.nextMove = 0;
		}
	}

	public boolean isFly() {
		return fly;
	}

	public void setFly(boolean fly) {
		this.fly = fly;
	}

}
