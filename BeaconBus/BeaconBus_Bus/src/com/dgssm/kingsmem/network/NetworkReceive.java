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
	private					boolean				mSettingFlag	= false;

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
				
				// 전송 받은 패킷 타입에 따라 다른 동작을 한다.
				switch (protocolType) {
					case PacketType.REQ_BUSSETTINGINFO :
						String[] infos = mProtocol.getPacketString(buf);
						
						for (int i = 0; i < infos.length; i++) {
							try {
								infos[i] = infos[i].split(" ")[0];
								
								System.out.println(TAG + " : run() -> " + infos[i]);
								
								if (!mSettingFlag) {
									mSettingFlag = true;
								}
							} catch (Exception e) {
								break;
							}
						}
						
						mCallback.callback(Constants.UPDATE_BUS_SETTING_INFO, 0, null, infos);
						
						break;
					case PacketType.SEND_BUSLOCATIONUPDATE :
						if (mSettingFlag) {
							String[] locations = mProtocol.getPacketString(buf);
													
							locations[0] = locations[0].split(" ")[0];
							
							System.out.println(TAG + " : run() -> " + locations[0] + ", " + locations[1] + " 명 대기 중");
							
							mCallback.callback(Constants.UPDATE_BUS_LOCATION, 0, null, locations);
						}
						
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