package com.github.rish141.JChatServer;

import javax.swing.JFrame;

public class ServerTest {
	public static void main(String[] args){
		Server chatServer = new Server();
		chatServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatServer.startRunning();
	}
}
