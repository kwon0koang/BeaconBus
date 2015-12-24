package com.dgssm.kingsmem.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.etc.Constants;

public class NetworkAcceptThread implements Runnable {
	// Debug
	private static final	String			TAG			= "NetworkAcceptThread";
	
	// State Value
	private final 			int 			RUNNING		= 0;
	private final 			int 			SUSPENDED	= 1;
	private final 			int 			STOPPED		= 2;	
	private 				int 			state		= SUSPENDED;
	
	// Thread
	private					Thread			mThread		= null;
	
	// Network
	private					Socket			mSocket 	= null;
	private					BufferedReader	mIn			= null;
	private					PrintWriter 	mOut		= null;
	
	// Information
	private					String			mIP			= null;
	private					String			mWhere		= null;
	
	// Callback
	private					Callback		mCallback	= null;
	
	/**
	 * @param ip	 : Client의 IP 주소
	 * @param where	 : 도착 정류장 이름
	 * @param socket : 연결된 Socket 객체
	 */
	public NetworkAcceptThread(String ip, String where, Socket socket, Callback callback) {
		this.mIP = ip;
		this.mWhere = where;
		this.mSocket = socket;
		this.mCallback = callback;
		this.mThread = new Thread(this);		
		
		try {
			this.mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			this.mOut = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()));			
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
	
	/**
	 * 접속된 Client의 도착 정류장을 반환한다.
	 * @return String 형태의 도착 정류장
	 */
	public String getWhere() {
		return this.mWhere;
	}

	@Override
	public void run() {
		while (true) {
			if (checkState()) {
				mThread = null;
				break;
			}
			
			try {
				// Thread.sleep(3000);
				
				// read 대기 시간 설정
				mSocket.setSoTimeout(15000);
				
				String message = mIn.readLine();
				
				if (message != null) {
					int data = Integer.parseInt(message);
					
					if (data == 1) {
						System.out.println(TAG + " : run() -> " + mIP + "'s Message is " + message);
						
						mOut.println(Constants.SEND_ACK);
						mOut.flush();
					}
					else {						
						this.stop();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(TAG + " : run() -> " + e.getMessage());
				
				this.stop();
				
				mCallback.callback(Constants.EXCEPTION_NETWORK, 0, mWhere, null);
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
		} catch (IOException ioe) {
			System.err.println(TAG + " : stop() -> " + ioe.getMessage());
		}
	}
	
	public void send(String nextBusStop) {
		System.out.println(TAG + " : send() -> " + mIP);
	
		try {
			mOut.println(nextBusStop);
			mOut.flush();
		} catch (Exception e) {
			System.err.println(TAG + " : send() -> " + e.getMessage());
		}
	}
	
	/**
	 * 접속된 Client에게 Alarm 신호를 전송하는 함수
	 */
	public void alarm() {
		System.out.println(TAG + " : alarm() -> " + mIP);
		
		try {
			mOut.println(Constants.SEND_ALARM);
			mOut.flush();
		} catch (Exception e) {
			System.err.println(TAG + " : alarm() -> " + e.getMessage());
		}
	}
	
	/**
	 * 접속된 Client에게 Get Down 신호를 전송하는 함수
	 */
	public void getDown() {
		System.out.println(TAG + " : getDown() -> " + mIP);
		
		try {
			mOut.println(Constants.SEND_GET_DOWN);
			mOut.flush();
			
			// this.stop();
		} catch (Exception e) {
			System.err.println(TAG + " : getDown() -> " + e.getMessage());
		}
	}
}

// End of NetworkAcceptThread