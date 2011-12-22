// ChttingServer.java
// author: realmove

// 20111222-1328 : ymkim starts to develop go stop server
// Author : Youngmin Kim (ymkim92@gmail.com)

package org.gs.game.gostop;

import java.io.*;
import java.net.*;
import java.util.*;
/*
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
*/

/*
	This GoStopServer can serve only 3 players at one time
 */
public class GoStopServer {
	
	private ServerSocket srvSocket=null;
	private Socket socket=null;
	private Vector<Client> client=new Vector<Client>();
	
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
				addClient(t);
				t.start();
			} catch(IOException e) {
				System.out.println("Failed in connect to Clients");
			}
		}
		
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

class Client extends Thread {
	
	private Socket socket=null;
	private GoStopServer server=null;
	private BufferedReader br=null;
	private BufferedWriter bw=null;
	private String username="";
	private String helpMessage="\n=======================================================\n"+
								"  /?, /help : Show help\n"+
								"  /al [username] : Change user name\n"+
								"  /all : Show all users\n"+
								"  /bye, /exit, /quit \n0"+
								"=======================================================\n";
	
	public Client(Socket s, GoStopServer c) {
		socket=s;
		server=c;
		
		try {
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			username=br.readLine();
		} catch (IOException e) {
			// Thread kill
		}
	}
	
	public void run() {
		String str="";
		
		server.message(username+" is in.");
		server.message("Welcome "+username);
		sendMessage("Type /? or /help to get help.");
		
		while (true) {
			try {
				str=br.readLine();
//				assert (str != null);
				if (str == null) {
			//		server.message(username+ " 111");
					server.removeClient(this);
					break;
				}
					
				if (isCommonMessage(str)) {
					server.message(username+":"+str);
				}
			} catch (IOException e) {
			//	server.message(username+ " is out abnormally.");
				server.removeClient(this);
				break;
			}
		}
	}

	public boolean isCommonMessage(String str) {
		boolean is=true;
		StringTokenizer st=new StringTokenizer(str," ");
		
		if (str.substring(0,1).equals("/")) {
			String tmp=st.nextToken();
			is=false;
			
			if (tmp.equals("/?") || tmp.equalsIgnoreCase("/help")) {  // show help message
				sendMessage(helpMessage);
			} else if (tmp.equalsIgnoreCase("/al")) {					// change user name
				String newname=str.substring(4).trim();
				server.message(username+" is changed into "+newname);
				username=newname;
			} else if (tmp.equalsIgnoreCase("/all")) {					// show all user
				sendMessage(server.getAllUser());
			} else if (tmp.equalsIgnoreCase("/bye")||
					tmp.equalsIgnoreCase("/quit") || tmp.equalsIgnoreCase("/exit")) {	
			//	server.message(username+ " 222");
				this.closeSocket();

			} else if (tmp.equalsIgnoreCase("/send ")) {	


			} else {
				is=true;
			}
		} 
		return is;
	}
	
	public void sendMessage(String str) {
		try {
			bw.write(str);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {

		}
	}
	
	public String getUserName() {
		return username;
	}
	
	public void closeSocket() {
		try {
			br.close();
			bw.close();
			socket.close();
		} catch (IOException e) {
			
		}	
	}
}
