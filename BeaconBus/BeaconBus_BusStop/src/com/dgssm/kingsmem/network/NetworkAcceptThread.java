package com.dgssm.kingsmem.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkAcceptThread implements Runnable {	
	// Debug
	private static final	String					TAG			= "NetworkAcceptThread";
	
	// State Value
	private final 			int 					RUNNING		= 0;
	private final 			int 					SUSPENDED	= 1;
	private final 			int 					STOPPED		= 2;	
	private 				int 					state		= SUSPENDED;

	// Thread
	private					Thread					mThread		= null;
	
	// Network
	private					Socket					mSocket 	= null;
	private					BufferedInputStream		mIn			= null;
	private					BufferedOutputStream 	mOut		= null;
	
	// Information
	private					String					mIP			= null;
	
	/**
	 * @param ip	 : Client의 IP 주소
	 * @param where	 : 도착 정류장 이름
	 */
	public NetworkAcceptThread(String ip, Socket socket) {
		this.mIP = ip;
		this.mSocket = socket;
		this.mThread = new Thread(this);
		
		try {
			this.mIn = new BufferedInputStream(mSocket.getInputStream());
			this.mOut = new BufferedOutputStream(mSocket.getOutputStream());
		} catch (IOException ioe) {
			System.err.println(TAG + " : Constructor() -> " + ioe.getMessage());
		}
	}
	
	/**
	 * 접속된 Client의 IP 주소를 반환한다.
	 * @return String 형태의 IP 주소
	 */
	public String getIP() {
		return this.mIP;
	}

	@Override
	public void run() {		
		while (true) {
			if (checkState()) {
				mThread = null;
				break;
			}
			
			try {
				this.mSocket.setSoTimeout(15000);
				int data = mIn.read();
				
				if (data < 0) {
					System.out.println(TAG + " : run() -> " + mIP + " Clinet Disconnect");
					
					this.stop();
				}
				else {
					System.out.println(TAG + " : run() -> " + mIP + "'s Message is " + data);
					
					mOut.write(1);
					mOut.flush();
				}
			} catch (Exception e) {
				this.stop();
				
				System.err.println(TAG + " : run() -> " + e.getMessage());
			}
		}
	}
	
	/** 
	 * Thread 상태를 지정하고 RUNNING 이외에는 Thread에 interrupt를 건다.
	 * @param state : Thread 상태
	 */
	private synchronized void setState(int state) {
		this.state = state;
		
		if (this.state == RUNNING) {
			notify();
		} else {
			mThread.interrupt();
		}
	}
	
	/**
	 * run() 에서 Thread의 상태를 체크하기 위한 함수
	 * @return state 변수가 STOPPED 인지를 검사해 리턴한다.
	 */
	private synchronized boolean checkState() {		
		while (state == SUSPENDED) {
			try {
				wait();
			} catch (InterruptedException ie) {
				System.err.println(TAG + " : checkState() -> " + ie.getMessage());
			}
		}
		
		return state == STOPPED;
	}
	
	/**
	 * Thread를 시작하는 함수
	 */
	public void start() {		
		mThread.start();
		
		setState(RUNNING);
	}
	
	/**
	 * Thread를 종료하는 함수
	 * Socket 객체를 닫고 NetworkInnerServer에 있는 disconnect 함수를 호출한다.
	 */
	public void stop() {
		try {
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;				
			}
		
			setState(STOPPED);
			
			NetworkInnerServer.getInstance().disconnect(this);
		} catch (Exception e) {
			System.err.println(TAG + " : stop() -> " + e.getMessage());
		}
	}
}

// End of ClientThread