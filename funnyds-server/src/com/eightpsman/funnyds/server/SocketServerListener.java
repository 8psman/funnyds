package com.eightpsman.funnyds.server;

import com.eightpsman.funnyds.core.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketServerListener extends Thread{

	ServerSocket serverSocket;
	
	public SocketServerListener() throws IOException{
		serverSocket = new ServerSocket(Constants.SERVER_SOCKET_PORT);
	}
	
	public void stopListening(){
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		if (serverSocket == null) return;
		while (true){
			try {
				Socket socket = serverSocket.accept();
				handleNewSocket(socket);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Stop listen to client");
			}
		}
	}

	public void handleNewSocket(Socket socket){
		try {
			new SocketClientHandler(socket);
		} catch (IOException e) {
			e.printStackTrace();
			if (!socket.isClosed())
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
	}
}
