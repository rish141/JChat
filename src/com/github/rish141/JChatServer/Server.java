package com.github.rish141.JChatServer;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
 
	private JTextField userText;
	private JTextArea chatWindow;
	private JLabel aboutLabel;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private ServerSocket serverSocket;
	private Socket connection;

	public Server(){
		super("JChat:Server");

		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendToServer(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		add(userText, BorderLayout.NORTH);

		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);

		aboutLabel = new JLabel("Created by rish141 and shkesar", SwingConstants.RIGHT);
		add(aboutLabel, BorderLayout.SOUTH);

		setSize(300,450);
		setVisible(true);
	}

	public void startRunning(){
		try{
			serverSocket = new ServerSocket(5555,100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException){
					appendToChat("\n Server ended");
				}finally{
					killConnection();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		
	}

	private void waitForConnection() throws IOException{
		// wait for connection,then send connection info
		appendToChat("Waiting for Other User to Connect..\n");
		connection = serverSocket.accept();
		appendToChat("Now Connected to " + connection.getInetAddress().getHostName());
	}

	private void setupStreams() throws IOException{
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(connection.getInputStream());
		appendToChat("\n Streams are Setup!\n");
	}

	private void whileChatting() throws IOException{
		String message = "Connected!";
		sendToServer(message);
		ableToType(true);
		do{
			try{
				message = (String) inputStream.readObject();
				appendToChat("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				appendToChat("\n Error OCccured from Other End");
			}
		}while(!message.equals("CLIENT - END"));
	}

	private void killConnection(){
		appendToChat("\n Closing Connection... \n");
		ableToType(false);
		try{
			outputStream.close();
			inputStream.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	private void sendToServer(String message){
		try{
			outputStream.writeObject("SERVER : " + message);
			outputStream.flush();
			appendToChat("\nSERVER : " + message);
		}catch(IOException ioE){
			chatWindow.append("\n ERROR WITH SENDING THE MSG");
		}
	}

	private void appendToChat(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);
	}

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
