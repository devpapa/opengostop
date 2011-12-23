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

/*
	This GoStopServer can serve only 3 players at one time
 */

class PlayThread extends Thread {
	
	
	public PlayThread() {
	}
	
	public void run() {
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
