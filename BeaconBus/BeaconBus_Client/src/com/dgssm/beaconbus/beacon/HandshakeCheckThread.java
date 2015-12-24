package com.dgssm.beaconbus.beacon;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;

//HandshakeCheckThread ======================================================
public class HandshakeCheckThread extends Thread{
	
	private static final String TAG = "HandshakeCheckThread";
	
	private static HandshakeCheckThread handshakeCheckThreadInstance = null;
	private static Socket mSocket = null;
	
	private HandshakeCheckThread(){}
	public static HandshakeCheckThread getInstance(Socket socket){
		mSocket = socket;
		if(handshakeCheckThreadInstance == null){
			handshakeCheckThreadInstance = new HandshakeCheckThread();
		}
		return handshakeCheckThreadInstance;
	}
	
	@Override
	public void run() {
		super.run();
		while(true){
			if(BeaconDetactorService.handshakeMsg == 0){
				Log.e(TAG, "Handshake ��ٸ��� �ð� = " + (System.currentTimeMillis() - BeaconDetactorService.nowTime));
				// Handshake 15�� �Ѱ� �ȳ������
				if(System.currentTimeMillis() - BeaconDetactorService.nowTime > 15000){
					// ���� ����
					if (mSocket != null) {
						try {
								Log.e(TAG, "HandshakeCheckThread   DISCONNECT : Handshake ERROR");
								Log.e(TAG, "HandshakeCheckThread   DISCONNECT : Handshake ERROR");
								Log.e(TAG, "HandshakeCheckThread   DISCONNECT : Handshake ERROR");
								mSocket.close();
								mSocket = null;
								handshakeCheckThreadInstance.interrupt();
								handshakeCheckThreadInstance = null;
								break;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
// /HandshakeCheckThread ======================================================