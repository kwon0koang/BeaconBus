package com.dgssm.kingsmem.main;

import com.dgssm.kingsmem.beacon.BeaconCommand;
import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.data.BusStopData;
import com.dgssm.kingsmem.etc.Constants;
import com.dgssm.kingsmem.etc.PacketType;
import com.dgssm.kingsmem.network.NetworkHandler;
import com.dgssm.kingsmem.network.NetworkInnerServer;
import com.dgssm.kingsmem.ui.MainPanel;

public class BusStopMain {
	// Debug
	private static final	String				TAG				= "BusStopMain";
	
	// Instances
	private					MainPanel			mMainPanel		= null;
	private					BeaconCommand		mBeaconCommand	= null;
	private					NetworkHandler		mNetWorkHandler	= null;
	private					NetworkInnerServer	mServer			= null;
	private					CallbackImpl		mCallbackImpl	= null;
	
	public BusStopMain() {
		// 프로그램 종료 시점에 Thread를 실행한다.
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
	}
	
	public static void main(String[] args) {
		BusStopMain bsMain = new BusStopMain();
		
		bsMain.start();
	}
	
	/**
	 * 프로그램에 필요한 객체들을 초기화하고 실행한다.
	 */
	private void start() {
		if (mCallbackImpl == null) {
			mCallbackImpl = new CallbackImpl();
		}
		
		if (mBeaconCommand == null) {
			mBeaconCommand = new BeaconCommand();
		}
		
		if (mMainPanel == null) {
			mMainPanel = new MainPanel(mCallbackImpl);
		}
		
		if (mNetWorkHandler == null) {
			mNetWorkHandler = new NetworkHandler(mCallbackImpl);
			mNetWorkHandler.start();
		}
		
		if (mServer == null) {
			mServer = NetworkInnerServer.getInstance(mCallbackImpl);
			mServer.accept();
		}
	}
	
	/** 
	 * @author Bladek
	 * Callback Interface를 실제로 구현한 클래스, 모든 명령어들이 작업을 수행한다.
	 * 명령어들은 Constants.java를 참고
	 */
	private class CallbackImpl implements Callback {
		@Override
		public void callback(int cmd, int arg0, String arg1, Object arg2) {
			synchronized (this) {
				switch (cmd) {
					case Constants.SEND_BUSSTOP_SETTING_INFO :
						if (mNetWorkHandler.isConnected() && mNetWorkHandler.mNetWorkSend != null) {
							if (arg1 != null) {							
								final String[] datas = { arg1 };
								
								mNetWorkHandler.mNetWorkSend.sendMessage(PacketType.SEND_BUSSTOPSETTINGINFO, datas);
							}
						}
						
						break;
					case Constants.UPDATE_BUSSTOP_COUNT_DATA :
						if (mMainPanel != null) {							
							BusStopData.Count += arg0;
							BusStopData.Count = (BusStopData.Count < 0) ? 0 : BusStopData.Count;
							
							System.out.println(TAG + " : callback() -> " + BusStopData.Count);
							
							mMainPanel.updateCount(BusStopData.Count);
							
							if (mNetWorkHandler.isConnected() && mNetWorkHandler.mNetWorkSend != null) {
								
								final short type = (arg0 > 0) ? PacketType.SEND_BUSSTOP_CNTUP : PacketType.SEND_BUSSTOP_CNTDOWN;
								
								mNetWorkHandler.mNetWorkSend.sendMessage(type);
							}
						}
						
						break;
					case Constants.UPDATE_BUSSTOP_SETTING_INFO :
						// 이름을 업데이트 하면서 비콘 시작
						BusStopData.Name = arg1;
						BusStopData.Count = 0;
						
						System.out.println(TAG + " : callback() -> 정류장 이름 업데이트 " + BusStopData.Name + ", " + BusStopData.Count + " 명");
												
						break;
					case Constants.UPDATE_BUSSTOP_NAME_DATA :
						if (mMainPanel != null) {
							mMainPanel.updateName();
							
							if (mBeaconCommand != null) {
								mBeaconCommand.startBeacon();
							}
						}
						
						break;
					default : break;
				}
			}
		}
	}
	
	// End of CallbackImpl
	
	/**
	 * @author Bladek
	 * 프로그램 종료시점에 시작되는 Thread Class 자바 API에서 지원한다.
	 * 초기화 및 실행은 BusMain.java의 생성자 부분 참고
	 */
	private class ShutdownHookThread extends Thread {		
		@Override
		public void run() {			
			System.out.println("시스템 정상 종료 됨");
			
			super.run();			
		}
	}
	
	// End of ShutdownHookThread
}

// End BusStopMain