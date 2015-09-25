import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	//constructor
	public Client(String host){
		super("JChat:Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}
	
	//connect to Server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofE){
			showMessage("\n Client Terminated the Connection");
		}catch(IOException ioE){
			ioE.printStackTrace();
		}finally{
			closeCrap();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting Connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP),5555);
		showMessage("Connected to : "+connection.getInetAddress().getHostName());
	}
	
	//setup for send/recieve msg
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Say Hello!");
	}
	
	//while conversation
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n"+ message);
			}catch(ClassNotFoundException cnfE){
				showMessage("\n ERROR from the Other Side");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	//close streams
	private void closeCrap(){
		showMessage("Closing the Connection");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	//send the msg to server
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - "+ message);
			output.flush();
			showMessage("\nCLIENT - "+ message);
		}catch(IOException ioe){
			chatWindow.append("\n Error while sending your MESSAGE.");
		}
	}
	
	//update chat windows
	private void showMessage(final String message){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(message);
					}
				}
			);
	}
	
	// ablity to type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);		
	}
}
