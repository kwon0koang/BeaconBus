package com.dgssm.kingsmem.network;

import java.net.Socket;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.etc.Setting;

public class NetworkHandler extends Thread {
	// Debug
	private static final	String			TAG				= "NetworkHandler";
	
	// Network
	private					Socket			mSocket			= null;
	private					NetworkReceive	mNetWorkReceive = null;
	public					NetworkSend		mNetWorkSend	= null;
	private					Setting			mSetting		= null;

	// Flag
	private					boolean			mIsConnected	= false;
	private					boolean			mLoop			= true;
	
	// Callback
	private					Callback		mCallback		= null;

	/**
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	public NetworkHandler(Callback callback) {
		this.mCallback = callback;
		this.mSetting = Setting.getInstance();
	}

	@Override
	public void run() {
		try {
			mSocket = new Socket(mSetting.getServerIP(), mSetting.getServerPort());
			mIsConnected = true;
			
			System.out.println(TAG + " : run() -> " + mSetting.getServerIP() + " 접속 됨");
			
			if (mIsConnected) {
				while (mLoop) {
					try {
						if (mIsConnected) {
							mNetWorkSend = new NetworkSend(mSocket);
							mNetWorkReceive = new NetworkReceive(mSocket, mCallback);
							
							mNetWorkReceive.start();
							mNetWorkReceive.join();
							close(true);
						} else {
							Thread.sleep(1000);
							break;
						}
					} catch (InterruptedException ie) {
						System.err.println(TAG + " : run() -> " + ie.getMessage());
					}
				}
				
				close(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(TAG + " : run() -> 연결 해제 ," + e.getMessage());
			
			close(true);
		}
	}
	
	/**
	 * 연결 상태인지 검사하는 함수
	 * @return 연결 여부에 대한 boolean 값을 반환한다.
	 */
	public boolean isConnected() {
		return this.mIsConnected;
	}

	/**
	 * NetworkReceive와 Send를 없애고 NetworkHandler를 종료하는 부분
	 * @param setloopConnect : Loop를 해제할 것인지의 boolean 값
	 */
	public void close(boolean setloopConnect) {
		if (!setloopConnect) {
			mLoop = false;
		}

		mIsConnected = false;

		if (mNetWorkReceive != null) {
			mNetWorkReceive.close();
			mNetWorkReceive = null;
		}

		if (mNetWorkSend != null) {
			mNetWorkSend = null;
		}

		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (Exception e) {
				System.err.println(TAG + " : close() -> " + e.getMessage());
			}
		}
	}
}

// End of NetworkHandler