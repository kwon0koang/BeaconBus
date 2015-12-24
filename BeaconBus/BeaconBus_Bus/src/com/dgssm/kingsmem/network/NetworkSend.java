package com.dgssm.kingsmem.network;

import java.io.BufferedOutputStream;
import java.net.Socket;

public class NetworkSend {
	// Debug
	private static final	String					TAG		= "NetworkSend";
	
	// Network
	private					Socket					mSocket = null;
	private					BufferedOutputStream	mOut	= null;
	
	/**
	 * @param socket : 연결된 Socket 객체
	 */
	public NetworkSend(Socket socket) {
		this.mSocket=socket;
		
		init();
	}
	
	/**
	 * BufferedOutputStream을 초기화 해주는 함수
	 */
	private void init() {
		try {
			mOut = new BufferedOutputStream(mSocket.getOutputStream());
		} catch(Exception e) {
			System.err.println(TAG + " : init() -> " + e.getMessage());
		}
	}

	/**
	 * Server에 Packet Type을 전송한다.
	 * 버스나 정류장 인원 정보에 대한 Packet Type만 동작한다.
	 * @param PacketType : short 형태의 Packet Type
	 */
	public void sendMessage(short PacketType) {
		NetworkProtocol protocol=new NetworkProtocol(PacketType);
		protocol.setSendPacket(4);
		
		try {
			mOut.write(protocol.getSendPacket());
			mOut.flush();
			
			System.out.println(TAG + " : sendMessage() -> " + PacketType);
		} catch (Exception e) {
			System.err.println(TAG + " : sendMessage() -> " + e.getMessage());
		}
		
		protocol = null;
	}
	
	/**
	 * Server에 Packet Type과 String 배열 형태의 데이터를 묶어 전송한다.
	 * @param PacketType : short 형태의 Packet Type
	 * @param datas		 : Server에 String 배열 형태의 데이터
	 */
	public void sendMessage(short PacketType, String[] datas) {
		NetworkProtocol protocol = new NetworkProtocol(PacketType);
		protocol.setString(datas);

		try {
			mOut.write(protocol.getSendPacket());
			mOut.flush();
			
			System.out.print(TAG + " : sendMessage() -> " + PacketType + ", ");
			for (String s : datas) {
				System.out.print(s + " ");
			}
			System.out.println();
		} catch (Exception e) {
			System.err.println(TAG + " : sendMessage() -> " + e.getMessage());
		}
		
		protocol = null;
	}
}

// End of NetworkSend