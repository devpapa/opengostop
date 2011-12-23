/// ChattingServer.java
// author: realmove

// 20111222-1328 : ymkim starts to develop a gostop server
// Author : Youngmin Kim (ymkim92@gmail.com)
// Base code : http://www.javastudy.co.kr/docs/lec_java/sang/stream_socket.html

package org.gs.game.gostop.server.multi_text;

import java.io.*;
import java.net.*;
import java.util.*;
/*
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
*/
import org.gs.game.gostop.TextGamePanel;
import org.gs.game.gostop.server.multi_text.Client;

/*
	This GoStopServer can serve only 3 players at one time
 */
public class GoStopServer {
	
	private ServerSocket srvSocket=null;
	private Socket socket=null;
	private Vector<Client> client=new Vector<Client>();
    private TextGamePanel gamePanel;
	
	public GoStopServer() {
	}
	
	public void message(String str) {
		System.out.println(str);
		for (int i=0; i<client.size(); i++) {
			(client.elementAt(i)).sendMessage(str);
		}
	}
	
	public void addClient(Client c) {
//		client.addElement(c);		
		client.add(c);		
	}
	
	public void removeClient(Client c) {
		String username=c.getUserName();
		c.closeSocket();
		boolean b=client.remove(c);
		message(username+" is out..");
	}
	
	public void setServerSocket(int port) {
		try {
			srvSocket=new ServerSocket(port);
			System.out.println("Server-Socket created in "+port);
			System.out.println("Waiting Client.");
		} catch (IOException e) {
			System.out.println("Failed in Set ServerSocket");
		}
		
		while (true) {
			try {
				socket=srvSocket.accept();
				Client t=new Client(socket, this);
				if (client.size() >=3) {
					// send waiting message to a new player
					t.sendMessage("Game is ongoing.. so you can't play at this server\n");
					// break this connection
					t.closeSocket();
					continue;
				}
				addClient(t);
				t.start();
				System.out.println("The number of clients is " + client.size());
				if (client.size() == 3) {
					start_play(client); // new Thread
					System.out.println("Start game...");
				}
			} catch(IOException e) {
				System.out.println("Failed in connect to Clients");
			}
		}
		
	}
	
	public void start_play(Vector<Client> client)
	{
		PlayThread playthread = new PlayThread(client);
		playthread.run();
	}

	public String getAllUser() {
		String str="\n==========================================================\n";
		for (int i=0; i<client.size(); i++)
			str+=(client.elementAt(i)).getUserName()+"\n";
		str+="==========================================================\n";

		return str;
	}
	
	public static void main(String args[]) {
		int port=5777;
		GoStopServer c=new GoStopServer();
		
		if (args.length>0) {
			try {
				port=Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Usage: java GoStopServer [portnumber:default=5777]");
				System.exit(-1);
			}
		}
		
		c.setServerSocket(port);
	}
	
}

