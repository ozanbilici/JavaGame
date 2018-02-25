package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import message.Message;

public class Client extends JFrame implements ActionListener{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1907207468416183999L;
	
	private int UUID;

	private String characterName;
	private String ipAddress;
	private static Socket socket;
	private static ObjectOutputStream oout;
	private static ObjectInputStream oin;
	
	private int nextMove = 0;
	
	private JPanel panel;	
	
	private JLabel labelIA;
	private JTextField textIP;
	private JLabel labelCName;
	private JTextField textCName;	
	private JButton bConnect;
	private JButton bDconnect;	

	private static JLabel labelPoint;
	private static JRadioButton radioTypeFrog;
	private static JRadioButton radioTypeFly;
	private ButtonGroup buttonGroup;
	private static JButton bEnter;	
	
	private static boolean shiftPressed = false;
	
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	
        	if(getSocket().isConnected()) {
        		if(!bEnter.isEnabled()) {
		            if (e.getID() == KeyEvent.KEY_PRESSED) {
		            	if(e.getKeyCode() ==  KeyEvent.VK_SHIFT) {
		            		shiftPressed = true;
		            	}
		            	if((e.getKeyCode() == (KeyEvent.VK_DOWN)) && shiftPressed && radioTypeFrog.isSelected()){
		            		nextMove = 12;
		            	} else if((e.getKeyCode() == (KeyEvent.VK_UP)) && shiftPressed && radioTypeFrog.isSelected()) {
		            		nextMove = 18;
		            	}else if((e.getKeyCode() == (KeyEvent.VK_LEFT)) && shiftPressed && radioTypeFrog.isSelected()) {
		            		nextMove = 14;
		            	} else if((e.getKeyCode() == (KeyEvent.VK_RIGHT)) && shiftPressed && radioTypeFrog.isSelected()) {
		            		nextMove = 16;
		            	} else if(e.getKeyCode() == KeyEvent.VK_UP) {
		            		nextMove = 8;
		            	} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
		            		nextMove = 2;
		            	} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
		            		nextMove = 4;
		            	} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
		            		nextMove = 6;
		            	}
		            	// For debugging
		            	//System.out.println("MV/" + String.valueOf(nextMove));
		            	//System.out.println(String.valueOf(e.getKeyCode()));
		            	Message command = new Message();
		            	command.setCode("MV/");
		            	command.setNumber(nextMove);
		            	try {
							getOout().writeObject(command);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
		            	if(e.getKeyCode() ==  KeyEvent.VK_SHIFT) {
		            		shiftPressed = false;
		            	}
		                nextMove = 0;
		            } else if (e.getID() == KeyEvent.KEY_TYPED) {
		                nextMove = 0;
		            }
        		}
        	}
            return false;
        }
    }	
	
	public Client() {
		this.setLayout(new BorderLayout());
		// Creating GUI
		panel = new JPanel();
		panel.setLayout(null);
		
		//-----------------------------------------------------------------------
		// [+] Server connection Information Elements
		labelIA = new JLabel("IP Address : ");
		textIP = new JTextField(10);
		textIP.setText("172.31.128.253");
		labelIA.setBounds(30,10,150,10);
		textIP.setBounds(180, 6, 150, 20);
		
		
		labelCName = new JLabel("Character Name :");
		textCName = new JTextField(10);
		textCName.setText("User_");
		labelCName.setBounds(30,35,150,10);
		textCName.setBounds(180,31,150,20);
		
		bConnect = new JButton("Connect");
		bConnect.setBounds(200, 55, 130, 20);
		bConnect.setEnabled(true);
		bConnect.addActionListener(this);
		bConnect.setActionCommand("Connect");
		
		
		bDconnect = new JButton("Disconnect");
		bDconnect.setBounds(60, 55, 130, 20);
		bDconnect.setEnabled(false);		
		
		bDconnect.addActionListener(this);
		bDconnect.setActionCommand("Disconnect");	

		// [-] Server connection Information Elements		
		//-----------------------------------------------------------------------	
		
		//-----------------------------------------------------------------------
		// [+] Game Character Information Elements		
		radioTypeFrog = new JRadioButton("Frog");
		radioTypeFly = new JRadioButton("Fly");		
		radioTypeFrog.setBounds(600, 10, 100, 20);
		radioTypeFly.setBounds(700, 10, 100, 20);
		
		buttonGroup = new ButtonGroup();	
		buttonGroup.add(radioTypeFly);
		buttonGroup.add(radioTypeFrog);
		
		bEnter = new JButton("Enter the Game");
		bEnter.setBounds(600, 34, 200, 20);
		bEnter.addActionListener(this);
		bEnter.setActionCommand("Enter");	
		
		labelPoint = new JLabel("Points : 0");
		labelPoint.setBounds(600, 60, 150, 10);
		
		bEnter.setEnabled(false);		
		radioTypeFrog.setEnabled(false);
		radioTypeFly.setEnabled(false);
		radioTypeFrog.setSelected(true);
		
		// [-] Game Character Information Elements			
		//-----------------------------------------------------------------------
		
		panel.add(labelIA);
		panel.add(textIP);
		panel.add(bConnect);
		panel.add(bDconnect);		
		
		panel.add(labelCName);
		panel.add(textCName);
		panel.add(radioTypeFly);
		panel.add(radioTypeFrog);
		panel.add(bEnter);
		panel.add(labelPoint);
		
		panel.setPreferredSize(new Dimension(900, 80));
		add(panel,BorderLayout.NORTH);
		add(new GameField(),BorderLayout.CENTER);  

        pack();
        setSize(900, 350);
        setResizable(false);
        setTitle("DS HW");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String action = arg0.getActionCommand();
		if(action.equals("Connect")) {
			characterName = textCName.getText();
			ipAddress = textIP.getText();
			
			try {
				setSocket(new Socket(ipAddress, 9876));
				
				setOout(new ObjectOutputStream(getSocket().getOutputStream()));
				setOin(new ObjectInputStream(getSocket().getInputStream()));
				
				Message command = new Message();
				command.setCode("CN/");
				command.setMsg(characterName);
				getOout().writeObject(command);
				
				command = (Message) getOin().readObject();
				
				if(command.getCode().equals("UUID/")) {
					UUID = command.getNumber();
					GameField.setUUID(UUID);
				}
				
				bConnect.setEnabled(false);
				textIP.setEnabled(false);
				textCName.setEnabled(false);
				bDconnect.setEnabled(true);
				bEnter.setEnabled(true);
				radioTypeFrog.setEnabled(true);
				radioTypeFly.setEnabled(true);	
				
		        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		        manager.addKeyEventDispatcher(new MyDispatcher());				
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				GameField.setInGame(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				GameField.setInGame(false);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				GameField.setInGame(false);
			}
		} else if(action.equals("Disconnect")) {
			bConnect.setEnabled(true);
			bDconnect.setEnabled(false);
			bEnter.setEnabled(false);
			radioTypeFrog.setEnabled(false);
			radioTypeFly.setEnabled(false);
			
			GameField.setInGame(false);
			
			try {
				getSocket().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(action.equals("Enter")) {
			try {
				Message command = new Message();
				command.setCode("AD/");
				if(radioTypeFrog.isSelected()) {
					command.setNumber(0);
					getOout().writeObject(command);
	
				} else {
					command.setNumber(1);
					getOout().writeObject(command);
				}
				
				radioTypeFrog.setEnabled(false);
				radioTypeFly.setEnabled(false);	
		
				bDconnect.setEnabled(true);
				bEnter.setEnabled(false);
				
				GameField.setInGame(true);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		new Client(); 
	}

	public static ObjectOutputStream getOout() {
		return oout;
	}

	public static void setOout(ObjectOutputStream oout) {
		Client.oout = oout;
	}

	public static ObjectInputStream getOin() {
		return oin;
	}

	public static void setOin(ObjectInputStream oin) {
		Client.oin = oin;
	}

	public static Socket getSocket() {
		return socket;
	}

	public static void setSocket(Socket socket) {
		Client.socket = socket;
	}
	
	public static void checkSituation() {
		if(!GameField.isInGame()) {
			radioTypeFrog.setEnabled(true);
			radioTypeFly.setEnabled(true);	
	
			bEnter.setEnabled(true);			
		}
	}
	
	public static void setPoints(int point) {
		labelPoint.setText("Points : " + String.valueOf(point));
	}
}
