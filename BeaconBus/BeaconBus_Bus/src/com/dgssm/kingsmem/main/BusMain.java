package com.dgssm.kingsmem.main;

import java.util.ArrayList;

import javax.swing.JTextArea;

import com.dgssm.kingsmem.beacon.BeaconCommand;
import com.dgssm.kingsmem.callback.Callback;
import com.dgssm.kingsmem.data.BusData;
import com.dgssm.kingsmem.data.BusStopList;
import com.dgssm.kingsmem.etc.Constants;
import com.dgssm.kingsmem.etc.PacketType;
import com.dgssm.kingsmem.gpio.GPIOThread;
import com.dgssm.kingsmem.network.NetworkHandler;
import com.dgssm.kingsmem.network.NetworkInnerServer;
import com.dgssm.kingsmem.ui.MainPanel;

public class BusMain {
	// Debug
	private static final	String					TAG				= "BusMain";
	
	// Instances
	private					MainPanel				mMainPanel		= null;
	private					BeaconCommand			mBeaconCommand	= null;
	private					GPIOThread				mGPIO			= null;
	private					NetworkHandler			mNetWorkHandler	= null;
	private					NetworkInnerServer		mServer			= null;
	private					CallbackImpl			mCallbackImpl	= null;
	
	// Bus Stop List
	public static			ArrayList<BusStopList>	mBusStopList	= null;
	
	private					String mNextBusStop = "";
			
	public BusMain() {
		// 프로그램 종료 시점에 Thread를 실행한다.
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
	}
	
	public static void main(String[] args) {
		BusMain bMain = new BusMain();
		
		bMain.start();
	}
	
	/**
	 * 프로그램에 필요한 객체들을 초기화하고 실행한다.
	 */
	private void start() {
		if (mCallbackImpl == null) {
			mCallbackImpl = new CallbackImpl();
		}
		
		if (mBusStopList == null) {
			mBusStopList = new ArrayList<BusStopList>();
		}
		
		if (mBeaconCommand == null) {
			mBeaconCommand = new BeaconCommand();
		}
		
		if (mMainPanel == null) {
			mMainPanel = new MainPanel(mCallbackImpl);
		}		
		
		if (mGPIO == null) {
			mGPIO = new GPIOThread(mCallbackImpl);
			mGPIO.start();
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
			synchronized (CallbackImpl.class) {
				switch (cmd) {
					case Constants.SEND_BUS_SETTING_INFO :
						if (mNetWorkHandler.isConnected() && mNetWorkHandler.mNetWorkSend != null) {
							mNetWorkHandler.mNetWorkSend.sendMessage(PacketType.SEND_BUSSETTINGINFO, (String []) arg2);
						}
						
						break;
					case Constants.UPDATE_BUS_COUNT_DATA :
						// BusStopList & UI 업데이트 및 서버 전송
						if (mMainPanel != null) {
							BusData.SeatCount += arg0;
							
							System.out.println(TAG + " : callback() -> UPDATE_BUS_COUNT_DATA " + BusData.SeatCount);
							
							mMainPanel.updateCount("bus", BusData.SeatCount);
							
							// 같은 정류장 이름 찾아서 해당 카운트 업데이트
							for (int i = 0; i < mBusStopList.size(); i++) {
								if (mBusStopList.get(i).getBusStopName().equals(arg1)) {
									mBusStopList.get(i).updateGetDownCount(arg0);
								}
							}
							
							if (mNetWorkHandler.isConnected() && mNetWorkHandler.mNetWorkSend != null) {
								mNetWorkHandler.mNetWorkSend.sendMessage(PacketType.SEND_BUSSEATUP);
							}						
						}
						
						break;
					case Constants.UPDATE_BUS_SETTING_INFO :
						String[] datas = (String[]) arg2;
						JTextArea textArea = mMainPanel.getSettingPanel().getmDisplayArea();
						
						// 정류장 리스트 초기화
						if (mBusStopList != null) {
							mBusStopList = null;
							mBusStopList = new ArrayList<BusStopList>();
							
							BusData.ListIndex = 0;
						}
						
						if (textArea != null && datas != null) {						
							// 스트링 배열의 길이가 1이면 버스 번호로 판별
							if (datas.length == 1) {
								BusData.BusNumber = datas[0];
							}
							else {							
								for (String s : datas) {
									if (s != null) {
										BusStopList temp = new BusStopList(s, 0);
										
										mBusStopList.add(temp);
										textArea.append("List에 추가 : " + s + "\n");
										textArea.setCaretPosition(textArea.getDocument().getLength());
									}
								}
							}
						}
						
						break;
					case Constants.UPDATE_BUS_DATA_INFO :
						if (mMainPanel != null) {
							if (BusData.BusNumber != "0000" && BusData.CarNumber != "0000") {
								mMainPanel.updateBusNumber(BusData.BusNumber);
								mMainPanel.updateCarNumber(BusData.CarNumber);
								mMainPanel.setNextBusStop(mBusStopList.get(BusData.ListIndex).getBusStopName());
							}
						}
						
						break;
					case Constants.UPDATE_BUS_LOCATION :
						String[] locations = (String[]) arg2;
						
						if (mMainPanel != null && locations != null) {
							if (mMainPanel.getTextFields(0).isEnabled() && mMainPanel.getTextFields(1).isEnabled() && mMainPanel.getTextFields(2).isEnabled() &&
								mMainPanel.getTextFields(3).isEnabled() && mMainPanel.getTextFields(4).isEnabled() && mMainPanel.getTextFields(5).isEnabled()) {
								
								if (!mNextBusStop.equals(locations[0])) {
									mNextBusStop = locations[0];
									int getDownCount = 0;
									int getUpCount = 0;
									
									for (int i = 0; i < mBusStopList.size(); i++) {
										if (mBusStopList.get(i).getBusStopName().equals(locations[0])) {
											BusData.ListIndex = i;
											getDownCount = mBusStopList.get(i).getGetDownCount();
											getUpCount = Integer.parseInt(locations[1]);								
											break;
										}
									}
										
									// Debugging 용 출력
									System.out.println(TAG + " : callback() -> UPDATE_BUS_LOCATION " + mNextBusStop);
									
									// 정류장 이름 업데이트
									mMainPanel.setNextBusStop(mNextBusStop);
					
									// 정류장 인원을 업데이트 한다.
									mMainPanel.updateCount("next", getUpCount);
									
									// 내릴 사람 정보 업데이트
									mMainPanel.updateCount("down", getDownCount);
									
									// 다음 정류장 정보 다 주기
									mServer.sendNextBusStop(mNextBusStop);
									
									// 내릴 사람이 있으면 불을 킨다.
									if (mGPIO != null && !mGPIO.isOn() && getDownCount > 0) {
										System.out.println(TAG + " : callback() -> 내릴사람 " + getDownCount + " 명");
															
										// Led On
										mGPIO.ledOn();
										
										// 내릴 사람 알람 울림
										mServer.alarm(mNextBusStop);
									}
								}
							}
						}
						
						break;
					case Constants.START_BUS_BEACON :
						if (mBeaconCommand != null) {
							mBeaconCommand.startBeacon();
						}
						
						break;
					case Constants.PRESH_BUZZER_BUTTON :
						int getDownCount = 0;
						
						try {
							getDownCount = mBusStopList.get(BusData.ListIndex).getGetDownCount();
						} catch (Exception e) {
							System.err.println(TAG + " : " + e.getMessage());
						}
						
						if (mMainPanel != null && getDownCount > 0) {
							mBusStopList.get(BusData.ListIndex).updateGetDownCount(-getDownCount);
							
							// 탑승 인원 쪽 정보 업데이트
							BusData.SeatCount -= getDownCount;
							mMainPanel.updateCount("bus", BusData.SeatCount);
							
							System.out.println(TAG + " : callback() -> PRESH_BUZZER_BUTTON " + BusData.SeatCount);
							
							// 내릴 인원 쪽 정보 업데이트
							getDownCount = mBusStopList.get(BusData.ListIndex).getGetDownCount();
							mMainPanel.updateCount("down", getDownCount);
							
							// 내려할 인원들에게 신호 전달
							String nextBusStop = mBusStopList.get(BusData.ListIndex).getBusStopName();
							mServer.getDown(nextBusStop);
							
							// 서버에 정보 업데이트
							if (mNetWorkHandler.isConnected() && mNetWorkHandler.mNetWorkSend != null) {
								mNetWorkHandler.mNetWorkSend.sendMessage(PacketType.SEND_BUSSEATDOWN);
							}
						}
						
						if (mGPIO != null) {
							if (mGPIO.isOn()) {
								mGPIO.ledOff();
							}
							else {
								mGPIO.ledOn();
							}
						}
						
						break;
					case Constants.EXCEPTION_NETWORK :
						if (mMainPanel != null) {
							for (BusStopList temp : mBusStopList) {
								if (temp.getBusStopName().equals(arg1) && temp.getGetDownCount() > 0) {									
									// 버스 내릴 승객 리스트 업데이트
									temp.updateGetDownCount(-1);
									
									// 탑승 인원 쪽 잘못된 인원 정보 업데이트
									BusData.SeatCount -= 1;
									mMainPanel.updateCount("bus", BusData.SeatCount);
									
									// Debugging 용 출력
									System.out.println(TAG + " : callback() -> EXCEPTION_NETWORK " + BusData.SeatCount);
									
									// 잘못된 인원 소켓 제거
									mServer.getDown(arg1);
									
									// 서버에 정보 업데이트
									if (mNetWorkHandler.isConnected() && mNetWorkHandler.mNetWorkSend != null) {
										mNetWorkHandler.mNetWorkSend.sendMessage(PacketType.SEND_BUSSEATDOWN);
									}
									
									break;
								}
							}							
						}
						
						break;
					case Constants.GPIO_LED_ON  :
						if (mMainPanel != null) {
							mMainPanel.setBuzzerFlag(true);
							mMainPanel.changeBuzzerImage();
						}
						
						break;
					case Constants.GPIO_LED_OFF :						
						if (mMainPanel != null) {
							mMainPanel.setBuzzerFlag(false);
							mMainPanel.changeBuzzerImage();
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
		private static final String TAG = "ShutdownHookThread";
		
		@Override
		public void run() {
			if (mGPIO != null) {
				mGPIO.stop();
				mGPIO = null;
			}
			
			if (mServer != null) {
				mServer.close();
				mServer = null;
			}
			
			if (mNetWorkHandler != null) {
				mNetWorkHandler.close(false);
				mNetWorkHandler = null;
			}
			
			System.out.println(TAG + " : run() -> 시스템 정상 종료 됨");
			
			super.run();			
		}
	}
	
	// End of ShutdownHookThread
}

// End of BusMain
