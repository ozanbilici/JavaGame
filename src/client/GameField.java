package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.Timer;

import message.Message;

public class GameField extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int N = 15;
	public final static int M = 50;
	
	private static int UUID;
	private static boolean inGame = false;
	
	public static int points = 0;

	private Timer timer;
	private static int[][] game = new int[N][M];
	
	public GameField() {
        setFocusable(true);
        setBackground(Color.BLACK);     
        
        timer = new Timer(300, this);
        timer.start();
	}
	
    @Override
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        
        if(!inGame) {
	        for(int i = 0; i < N; i++) {
	        	for(int j = 0; j < M; j++) {
	        		if(game[i][j] > 0) {
	        			Message command = new Message();
	        			command.setCode("LC/"); // learn character
	        			command.setNumber(game[i][j]);
	        			try {
							Client.getOout().writeObject(command);
							
							command = new Message();
							command = (Message) Client.getOin().readObject();
							
							if(command.getNumber() == 0) {
								//frog
								g2d.fillOval(j*18, i*18, 18, 18);
							} else if(command.getNumber() == 1) {
								// fly
								g2d.fillRect(j*18, i*18, 18, 18);
							}
						} catch (IOException | ClassNotFoundException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							GameField.setInGame(false);
						}
	        			
	        		}
	        	}
	        }
        } else {
        	// learn if you are still in the game 
			Message command = new Message();
			command.setCode("LIG/"); // learn if in game
			command.setNumber(UUID);       	
			try {
				Client.getOout().writeObject(command);
				
				command = new Message();
				command = (Message) Client.getOin().readObject();
				if(command.getNumber() == 1) {
					setInGame(false);
					Client.checkSituation();
			        Toolkit.getDefaultToolkit().sync();
					return;
				}
				
				points = command.getNumber2();
				
				Client.setPoints(points);
				
			} catch (IOException | ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				GameField.setInGame(false);
			}

			
        	for(int i = 0; i < N; i++) {
        		for(int j = 0; j < M; j++) {
        			if(game[i][j] == UUID) {
	        			command = new Message();
	        			command.setCode("LC/"); // learn character
	        			command.setNumber(game[i][j]);
	        			try {
							Client.getOout().writeObject(command);
							
							command = new Message();
							command = (Message) Client.getOin().readObject();
							
							int visible = 0;
							int sub = 0;
							

							
							if(command.getNumber() == 0) {
								g2d.fillOval(j*18, i*18, 18, 18);
								// Frog just can see 5x5
								
								visible = 5;
								sub = 2;
							} else if(command.getNumber() == 1) {							
								g2d.fillRect(j*18, i*18, 18, 18);
								// fly can see 11x11
								visible = 11;
								sub = 5;
							}
							
							
							for(int k = 0; k < visible; k++) {
								for(int l = 0; l < visible; l++ ) {
									if(((i-sub+k) > 0) && (j-sub+l) > 0 && (i-sub+k) < N && (j-sub+l) < M) {
										// prevent diagonal seeing
										if(((-sub+k) != (-sub+l)) && ((-sub+k-sub+l) !=0)) {
											if(game[i-sub+k][j-sub+l] > 0) {
							        			command = new Message();
							        			command.setCode("LC/"); // learn character
							        			command.setNumber(game[i-sub+k][j-sub+l]);
							        			try {
													Client.getOout().writeObject(command);
													
													command = new Message();
													command = (Message) Client.getOin().readObject();
													
													if(command.getNumber() == 0) {
														//frog
														g2d.fillOval((j-sub+l)*18, (i-sub+k)*18, 18, 18);
													} else if(command.getNumber() == 1) {
														// fly
														g2d.fillRect((j-sub+l)*18, (i-sub+k)*18, 18, 18);
													}
												} catch (IOException | ClassNotFoundException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}											
											}
										}
									}
 								}
							}
						} catch (IOException | ClassNotFoundException e) {
							// TODO Auto-generated catch block
							GameField.setInGame(false);
						}
	        			
	        			break;
        			}
        		}
        	}
        }

        Toolkit.getDefaultToolkit().sync();
    }

	@Override
	public synchronized void actionPerformed(ActionEvent arg0) {
		
		if(Client.getSocket() != null) {
			if(Client.getSocket().isConnected() && !Client.getSocket().isClosed()) {
				Message command = new Message();
				command.setCode("GF/");
				try {
					Client.getOout().writeObject(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				try {
					command = (Message) Client.getOin().readObject();
					setGame(command.getData());
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
				this.repaint();
			}
		}
	}	
	
	public static void setGame(int[][] data) {
	    if (data == null) {
	        return;
	    }
	    
	    game = new int[data.length][];
	    
	    for (int i = 0; i < data.length; i++) {
	        game[i] = Arrays.copyOf(data[i], data[i].length);
	    }
	}

	public static void setUUID(int uUID) {
		UUID = uUID;
	}

	public static boolean isInGame() {
		return inGame;
	}

	public static void setInGame(boolean inGame) {
		GameField.inGame = inGame;
	}
}
