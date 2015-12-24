package com.dgssm.kingsmem.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.etc.Constants;
import com.dgssm.kingsmem.etc.PacketType;

public class NetworkReceive extends Thread {
	// Debug
	private static final 	String				TAG				= "NetWorkRecieve";
	
	// NetworkProtocol
	private					NetworkProtocol		mProtocol		= null;
	
	// Network
	private					Socket				mSocket			= null;
	private					BufferedInputStream	mIn				= null;
	
	// Callback
	private					Callback			mCallback		= null;
	
	// Flag
	private					boolean				mRunning		= true;

	/**
	 * @param socket   : 연결된 Socket 객체
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	public NetworkReceive(Socket socket, Callback callback) {
		this.mSocket = socket;
		this.mCallback = callback;
	}

	@Override
	public void run() {
		try {
			mIn = new BufferedInputStream(mSocket.getInputStream());
			mProtocol = new NetworkProtocol();
		} catch (IOException e) {
			
		}
		
		while (mRunning) {
			byte[] buf = mProtocol.getPacket();
			
			try {
				mIn.read(buf);
				
				short protocolType = mProtocol.getPacketType(buf);
				mProtocol.setPacket(protocolType, buf);
				
				switch (protocolType) {
					case PacketType.REQ_BUSSTOPSETTINGINFO :
						String[] strs = mProtocol.getPacketString(buf);
						
						System.out.println(TAG + " : run() -> " + strs[0]);
						
						mCallback.callback(Constants.UPDATE_BUSSTOP_SETTING_INFO, 0, strs[0], null);
						
						break;
					default : break;
				}
			} catch (IOException e) {
				close();
			}
		}
	}

	/**
	 * 소켓을 닫는다.
	 */
	public void close() {
		try {
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mRunning = false;
	}
}

// End of NetworkReceive