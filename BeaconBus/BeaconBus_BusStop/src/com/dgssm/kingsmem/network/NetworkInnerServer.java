package com.dgssm.kingsmem.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.etc.Constants;

public class NetworkInnerServer {
	// Debug
	private static final		String							TAG			= "NetworkInnerServer";
	
	// NetworkInnerServer
	private static				NetworkInnerServer				mInstance	= null;
	
	// ArrayList
	private static				ArrayList<NetworkAcceptThread>	mList		= null;
	
	// Network
	private						ServerSocket					mLisenter	= null;
	
	// Callback
	private						Callback						mCallback	= null;
	
	private NetworkInnerServer() {
		
	}
	
	/**
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 */
	private NetworkInnerServer(Callback callback) {
		this.mCallback = callback;
	}
	
	/**
	 * NetworkInnerServer Class는 Singleton 으로 구현되어 있음
	 * @return NetworkInnerServer 객체
	 */
	public static NetworkInnerServer getInstance() {
		if (mInstance == null) {
			mInstance = new NetworkInnerServer();
		}
		
		return mInstance;
	}
	
	/**
	 * NetworkInnerServer Class는 Singleton 으로 구현되어 있음
	 * @param callback : BusMain에 정의되어 있는 CallbackImpl 객체
	 * @return NetworkInnerServer 객체
	 */
	public static NetworkInnerServer getInstance(Callback callback) {
		if (mInstance == null) {
			mInstance = new NetworkInnerServer(callback);
		}
		
		return mInstance;
	}
	
	/**
	 * 내부 서버에 접속을 받기 전에 서버를 초기화하고 연다.
	 */
	private void init() {
		if (mLisenter == null) {
			try {
				mLisenter = new ServerSocket(4389);
				
				System.out.println(TAG + " : init() -> Open BusStop-InnerServer");
			} catch (Exception e) {
				System.err.println(TAG + " : init() -> " + e.getMessage());
			}
		}
				
		if (mList == null) {
			mList = new ArrayList<NetworkAcceptThread>();
		}
	}
	
	/**
	 * 여러 클라이언트를 받기 위해 Loop를 이용하여 접속을 기다린다. 
	 */
	public void accept() {
		try {
			this.init();
			
			while (true) {
				Socket socket = mLisenter.accept();
				
				if (socket.isConnected()) {
					String ip = socket.getInetAddress().getHostAddress();
					
					if (!checkIp(ip)) {
						NetworkAcceptThread cThread = new NetworkAcceptThread(ip, socket);
						cThread.start();
						
						mList.add(cThread);
						
						mCallback.callback(Constants.UPDATE_BUSSTOP_COUNT_DATA, 1, null, null);
						
						System.out.println(TAG + " : " + ip + " Client Connected, Number = " + mList.size());
					}
					else {
						System.err.println(TAG + " : accpet() -> 같은 아이피 접속 시도");
					}
				}
				else {
					socket.close();
				}
			}
		} catch (Exception e) {
			System.err.println(TAG + " : accept() -> " + e.getMessage());
		}
	}
	
	/**
	 * 접속한 클라이언트에 대해서 아이피 중복을 체크하는 함수
	 * @param ip : 접속한 아이피
	 * @return 중복 여부에 대한 boolean 값
	 */
	private boolean checkIp(String ip) {
		for (NetworkAcceptThread a : mList) {
			if (a.getIP().equals(ip)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 접속한 클라이언트가 가지고 있는 NetworkAcceptThread에서 Socket을 끊을 때 서버쪽에 불리는 함수
	 * 클라이언트를 관리하는 리스트에서 해당 객체를 제거하고
	 * Callback을 호출하여 탑승인원 및 내릴 인원 카운트를 감소하고
	 * 해당 NetworkAcceptThread 객체를 null로 초기화한다.
	 * @param a : NetworkAcceptThread
	 */
	public void disconnect(NetworkAcceptThread a) {
		System.out.println(TAG + " : disconnect() -> 끊음");
		
		mList.remove(a);
		
		a = null;
		
		mCallback.callback(Constants.UPDATE_BUSSTOP_COUNT_DATA, -1, null, null);
	}
}

// End of InnerServer