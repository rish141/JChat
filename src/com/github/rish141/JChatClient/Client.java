package com.github.rish141.JChatClient;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Client extends JFrame{

	private JTextField userText;
	private JTextPane chatWindow;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private String message = "";
	private String serverIP;
	private Socket connection;

	public Client(String host){
		super("JChat:Client");
		serverIP = host;
		userText = new JTextField();
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendToServer(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextPane();
		chatWindow.setEditable(false);
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

	private void connectToServer() throws IOException {
		appendToChat("Attempting Connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 5555);
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
				appendToChatServer("\n" + message);
			}catch(ClassNotFoundException cnfE){
				appendToChat("\n ERROR from the Other Side");
			}
		}while(!message.equals("SERVER - END"));
	}

	private void closeMessageStreams(){
		appendToChat("\nClosing the Connection");
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
			outputStream.writeObject("CLIENT : " + message);
			outputStream.flush();
			appendToChat("\nYOU - " + message);
		}catch(IOException ioe){
			appendToPane(chatWindow, "\n ERROR WITH SENDING THE MSG", Color.RED);
		}
	}

	private void appendToChat(final String message) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						appendToPane(chatWindow, message, Color.BLACK);
					}
				}
			);
	}

	private void appendToChatServer(final String message){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						appendToPane(chatWindow, message, Color.BLUE);
					}
				}
			);
	}

	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run() {
					userText.setEditable(tof);
				}
			}
		);		
	}
	
	 private void appendToPane(JTextPane tp, String msg, Color c)
	    {
			chatWindow.setEditable(true);
	        StyleContext sc = StyleContext.getDefaultStyleContext();
	        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

	        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
	        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

	        int len = tp.getDocument().getLength();
	        tp.setCaretPosition(len);
	        tp.setCharacterAttributes(aset, false);
			tp.replaceSelection(msg);
			chatWindow.setEditable(false);
	    }

}
