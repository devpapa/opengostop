// 20111223-1631
// Author : Youngmin Kim (ymkim92@gmail.com)

package org.gs.game.gostop.server.multi_text;


import java.io.*;
import java.net.*;
import java.util.*;

//import org.gs.game.gostop.server.multi_text.Client;
/*
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
*/
//import org.gs.game.gostop.TextGamePanel;

/*
	This can serve only 3 players at one time
 */

class PlayThread extends Thread {
	
	private Vector<Client> clients;
	
	public PlayThread(Vector<Client> client) {
		clients = client;
	}
	
	public void run() {
	}

/*
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
*/
}
