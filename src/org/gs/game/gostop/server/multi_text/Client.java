// 20120107-0844
// Author : Youngmin Kim (ymkim92@gmail.com)

package org.gs.game.gostop.server.multi_text;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread {

	private Socket socket=null;
	private GoStopServer server=null;
	private BufferedReader br=null;
	private BufferedWriter bw=null;
	private String username="";
	private String helpMessage="\n=======================================================\n"+
		"  /?, /help : Show help.\n"+
		"  /al [username] : Change username.\n"+
		"  /all : Show all users.\n"+
		"  /bye, /exit, /quit : Log out\n"+
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
		
		server.message(username+"is connected.");
		server.message("Welcome "+username);
		sendMessage("Press /? or /help to get a help");
		
		while (true) {
			try {
				str=br.readLine();
				if (isCommonMessage(str)) {
					server.message(username+":"+str);
				}
			} catch (IOException e) {
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
				server.message(username+"is changed to "+newname);
				username=newname;
			} else if (tmp.equalsIgnoreCase("/all")) {					// show all user
				sendMessage(server.getAllUser());
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

