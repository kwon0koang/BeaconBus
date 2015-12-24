package com.dgssm.beaconbus.outerserver;

import java.io.IOException;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetWorkHandler extends Thread {

	private static final String TAG = "NetWorkHandler";
	
	private Context mContext = null;
	
	Setting setting;
	Socket socket;
	NetWorkReceive netWorkReceive;
	public NetWorkSend netWorkSend;

	boolean isConnected = false;
	Handler mHandler;
	boolean loop = true;

	public NetWorkHandler(Context mContext) {
		this.mContext = mContext;
		init();
	}

	private void init() {
		setting = Setting.getInstance();
	}

	public void run() {
		ServerConnect(true);

		if (isConnected) {
			while (loop) {
				if (isConnected) {
					netWorkSend = new NetWorkSend(socket, this);
					netWorkReceive = new NetWorkReceive(socket, mContext);
					netWorkReceive.start();
					try {
						netWorkReceive.join();
						closeNetWork(true);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1000);
						break;
						// ServerConnect(true);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			closeNetWork(false);
		}

	}

	private void ServerConnect(boolean setloopConnect) {
		Log.e("dd", setting.getServerIP() + " " + setting.getServerPort() + " ");
		try {

			socket = new Socket(setting.getServerIP(), setting.getServerPort());
			isConnected = true;

			if (mHandler != null) {
				Message message = new Message();
				message.what = HandlerID.HANDLER_SERVER_CONNECT;
				mHandler.sendMessage(message);
			}

			Log.e("Network", "ServerConnect");
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			Message message = new Message();
//			message.what = HandlerID.HANDLER_FAIL_CONNECT;
//			mHandler.sendMessage(message);
//			closeNetWork(setloopConnect);
//			Log.e("dd", "fail");
			// 외부서버 연결 실패
			Log.e(TAG, "외부서버 연결 실패");
			Log.e(TAG, "외부서버 연결 실패");
			Log.e(TAG, "외부서버 연결 실패");
		}

	}

	public boolean isConnected() {
		return isConnected;
	}

	public void closeNetWork(boolean setloopConnect) {
		if (!setloopConnect)
			loop = false;

		isConnected = false;

		if (netWorkReceive != null) {
			netWorkReceive.close();
			netWorkReceive = null;
		}

		if (netWorkSend != null)
			netWorkSend = null;

		if (socket != null)
			try {
				socket.close();
//				Message message = new Message();
//				message.what = HandlerID.HANDLER_SERVER_DISCONNECT;
//				mHandler.sendMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
