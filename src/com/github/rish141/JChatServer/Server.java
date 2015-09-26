package com.github.rish141.JChatServer;

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

public class Server extends JFrame{
 
	private JTextField userText;
	private JTextPane chatWindow;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private ServerSocket serverSocket;
	private Socket connection;

	public Server(){
		super("JChat:Server");
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
		chatWindow = new JTextPane();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
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
				appendToChatClient("\n" + message);
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
			appendToChat("\nYOU : " + message);
		}catch(IOException ioE){
			appendToPane(chatWindow, "\n ERROR WITH SENDING THE MSG", Color.MAGENTA);
		}
	}

	private void appendToChat(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
					    appendToPane(chatWindow, text, Color.MAGENTA);
					}
				}
			);
	}

	private void appendToChatClient(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
					    appendToPane(chatWindow, text, Color.BLUE);
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
	
	 private void appendToPane(JTextPane tp, String msg, Color c)
	    {
	        StyleContext sc = StyleContext.getDefaultStyleContext();
	        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

	        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
	        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

	        int len = tp.getDocument().getLength();
	        tp.setCaretPosition(len);
	        tp.setCharacterAttributes(aset, false);
	        tp.replaceSelection(msg);
	    }
	
}
