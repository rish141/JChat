package com.github.rish141.JChatClient;

import javax.swing.JFrame;

public class ClientTest {
	public static void main(String[] args){
		Client CClient;
		CClient = new Client("127.0.0.1");
		CClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CClient.startRunning();
	}
}
