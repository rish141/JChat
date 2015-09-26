package com.github.rish141.JChatClient;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private String message = "";
	private String serverIP;
	private Socket connection;

	public Client(String host){
		super("JChat:Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendToServer(event.getActionCommand());
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

	public void startRunning(){
		try{
			connectToServer();
			setupMessageStreams();
			whileChatting();
		}catch(EOFException eofE){
			appendToChat("\n Client Terminated the Connection");
		}catch(IOException ioE){
			ioE.printStackTrace();
		}finally{
			closeMessageStreams();
		}
	}

	private void connectToServer() throws IOException{
		appendToChat("Attempting Connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP),5555);
		appendToChat("Connected to : " + connection.getInetAddress().getHostName());
	}

	private void setupMessageStreams() throws IOException{
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(connection.getInputStream());
		appendToChat("\n Say Hello!");
	}

	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) inputStream.readObject();
				appendToChat("\n" + message);
			}catch(ClassNotFoundException cnfE){
				appendToChat("\n ERROR from the Other Side");
			}
		}while(!message.equals("SERVER - END"));
	}

	private void closeMessageStreams(){
		appendToChat("Closing the Connection");
		ableToType(false);
		try{
			outputStream.close();
			inputStream.close();
			connection.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	private void sendToServer(String message){
		try{
			outputStream.writeObject("CLIENT - " + message);
			outputStream.flush();
			appendToChat("\nCLIENT - " + message);
		}catch(IOException ioe){
			chatWindow.append("\n Error while sending your MESSAGE.");
		}
	}

	private void appendToChat(final String message){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(message);
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
