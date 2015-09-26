package com.github.rish141.JChatServer;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
 
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public Server(){
		super("JChat:Server");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	
	//set up and run Server
	public void startRunning(){
		try{
			server = new ServerSocket(5555,100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException){
					showMessage("\n Server ended");
				}finally{
					closeCrap();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		
	}
	
	//wait for connection,then send connection info
	private void waitForConnection() throws IOException {
		showMessage("Waiting for Other User to Connect..\n");
		connection = server.accept();
		showMessage("Now Connected to " + connection.getInetAddress().getHostName());
	}
	
	// get Streams to send/recieve msgs
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are Setup!\n");
	}
	
	// while conversation
	private void whileChatting() throws IOException{
		String message = "Connected!";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n"+ message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Error OCccured from Other End");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	//close sockets etc
	private void closeCrap() {
		showMessage("\n Closing Connection... \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	// send msg function NOT SHOW
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER : "+ message);
			output.flush();
			showMessage("\nSERVER : "+ message);
		}catch(IOException ioE){
			chatWindow.append("\n ERROR WITH SENDING THE MSG");
		}
	}
	
	// update chat window
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(text);
					}
				}
			);
	}
	
	//user ability to type in box
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
