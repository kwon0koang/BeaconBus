package com.dgssm.kingsmem.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.data.BusData;
import com.dgssm.kingsmem.etc.Constants;
import com.dgssm.kingsmem.main.BusMain;

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
				mLisenter = new ServerSocket(Constants.SERVER_PORT);
				
				System.out.println(TAG + " : init() -> Open Bus-InnerServer");
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
					
					// IP 중복 체크
					if (!checkIP(ip)) {
						System.out.println(TAG + " : accept() -> " + ip);
						
						// 접속된 클라이언트(안드로이드)로 부터 도착지 정보를 전달 받고 받았다는 신호를 전달
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
						
						// 도착지 정보 읽기
						String where = in.readLine();
						
						System.out.println(TAG + " : accept() -> " + ip + " Client's Bus Stop is " + where);
						
						if (where.length() > 0) {
							int whereIndex = 0;
							
							for (int i = 0; i < BusMain.mBusStopList.size(); i++) {
								if (BusMain.mBusStopList.get(i).getBusStopName().equals(where)) {
									whereIndex = i;
									break;
								}
							}
							
							if (BusData.ListIndex <= whereIndex) {
								// 잘 받았다는 신호 전달
								out.println(Constants.SEND_ACK);
								out.flush();
								
								// Thread를 시작하고 List에 추가
								NetworkAcceptThread aThread = new NetworkAcceptThread(ip, where, socket, mCallback);
								aThread.start();
								
								mList.add(aThread);
								
								// Callback 호출
								mCallback.callback(Constants.UPDATE_BUS_COUNT_DATA, 1, where, null);
							}
							else {
								out.println(Constants.SEND_ERROR);
								out.flush();
								
								socket.close();
							}
						}
					}
					else {
						System.err.println(TAG + " : accpet() -> 같은 아이피로 접속 시도");
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
	private boolean checkIP(String ip) {
		for (NetworkAcceptThread a : mList){
			if (a.getIP().equals(ip)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 접속한 Client에게 알람 신호를 전달하는 함수
	 * 다음 정류장 이름을 접속한 모든 리스트와 비교하며 있다면 NetworkAcceptThread.java의
	 * alarm() 함수를 호출한다.
	 * @param nextBusStop : 다음 정류장 이름
	 */
	public void alarm(String nextBusStop) {
		for (NetworkAcceptThread temp : mList) {
			if (temp.getWhere().equals(nextBusStop)) {
				temp.alarm();
			}
		}
	}
	
	public void sendNextBusStop(String nextBusStop) {
		for (NetworkAcceptThread temp : mList) {
			temp.send(nextBusStop);
		}
	}
	
	/**
	 * 접속한 클라이언트에게 내리라는 신호를 전달하는 함수
	 * 다음 정류장 이름을 접속한 모든 리스트와 비교하여 있다면 NetworkAcceptThread.java의
	 * getDown() 함수를 호출한다.
	 * @param nextBusStop : 다음 정류장 이름
	 */
	public void getDown(String nextBusStop) {
		for (NetworkAcceptThread temp : mList) {
			if (temp.getWhere().equals(nextBusStop)) {
				temp.getDown();
			}
		}
	}
	
	/**
	 * 접속한 클라이언트가 가지고 있는 NetworkAcceptThread에서 Socket을 끊을 때 서버쪽에 불리는 함수
	 * 클라이언트를 관리하는 리스트에서 해당 객체를 제거하고
	 * Callback을 호출하여 탑승인원 및 내릴 인원 카운트를 감소하고
	 * 해당 NetworkAcceptThread 객체를 null로 초기화한다.
	 * @param a : NetworkAcceptThread
	 */
	protected void disconnect(NetworkAcceptThread a) {
		mList.remove(a);
		
		System.out.println(TAG + " : " + a.getIP() + " Client Disconnect");
		
		a = null;
	}
	
	/**
	 * 접속하는 Loop를 끊어버리고
	 * 모든 연결된 클라이언트를 끊으며
	 * 최종적으로 서버를 닫는 함수
	 * @param isloop : accept() 함수의 loop를 종료 여부를 판단하는 Flag
	 */
	public void close() {		
		// 서버 닫기
		if (mLisenter != null) {
			try {
				// 연결된 모든 클라이언트 끊기
				for (NetworkAcceptThread a : mList) {
					a.stop();
				}
				
				mLisenter.close();
				
				System.out.println(TAG + " : close() -> 서버가 정상적으로 닫힘, 클라이언트 리스트 수 " + mList.size());
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		
		// ArrayList 초기화
		if (mList != null && mList.size() == 0) {
			mList = null;
		}
	}
}

// End of NetworkInnerServer