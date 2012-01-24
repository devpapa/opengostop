package org.gs.game.gostop.server.multi_text;

import java.io.*; 
import java.net.*; 
import java.util.*; 

import org.gs.game.gostop.server.multi_text.GoStopServer;

public class Client extends Thread {
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
