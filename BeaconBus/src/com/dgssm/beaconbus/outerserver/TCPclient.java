package com.dgssm.beaconbus.outerserver;

import java.io.*;
import java.net.*;

import android.util.*;

public class TCPclient implements Runnable {
	
    private String msg;
	private String return_msg;
  
    public TCPclient(String _msg){
    	this.msg = _msg;
    }
    
    public void run() {
    	try {
    		Log.d("TCP", "C: Connecting...");
    		Socket socket = new Socket("127.0.0.1", 5555);
    		
    		try {
    			Log.d("TCP", "C: Sending: '" + msg + "'");
    			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
    			out.println(msg);
    			Log.d("TCP", "C: Sent.");
    			Log.d("TCP", "C: Done.");
         
    			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    			return_msg = in.readLine();
         
    			Log.d("TCP", "C: Server send to me this message -->" + return_msg);
    		} catch(Exception e) {
    			Log.e("TCP", "C: Error1", e);
    		} finally {
    			socket.close();
    		}
    	} catch (Exception e) {
    		Log.e("TCP", "C: Error2", e);
    	}
    }
}
