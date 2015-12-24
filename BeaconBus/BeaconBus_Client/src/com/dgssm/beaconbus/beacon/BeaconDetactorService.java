package com.dgssm.beaconbus.beacon;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import net.daum.mf.speech.api.TextToSpeechManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.dgssm.beaconbus.utils.Constants;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

public class BeaconDetactorService extends Service implements IBeaconConsumer {

	private static final String TAG = "BeaconDetactorService";
	
	private ArrayList<IBeacon> beaconList = new ArrayList<IBeacon>();
	
	private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);
	
	public static boolean waitFlag = false;		// true될 때까지 도착지 설정을 기다리기 위한 플래그

	// DetectedBus 액티비티 돌아가고 있는지 판별
	public static boolean runningDetectedBus = false;
	// DetectedBusSelectDestination 액티비티 돌아가고 있는지 판별
	public static boolean runningDetectedBusSelectDestination = false;		
	
	public static boolean transferFlag = false;
	public static String transferRoute = null;
	public static String transferDeparture = null;
	public static String transferDestination = null;
	
	public static String DESTINATION = "";		// 버스에 타고 있고, 연결되어 있을 때 뜬금없이 소켓 연결 끊어져도 도착지 설정 다시 안하게끔 하기 위함
	private boolean mIsRunning;
	
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		
		// 등록된 알람은 제거
		unregisterRestartAlarm();

		super.onCreate();
		
		TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

		mIsRunning = false;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		Log.e(TAG, "도착지 = " + DESTINATION);
		Log.e(TAG, "/onStartCommand");
		
		// 스캔 시작
		iBeaconManager.bind(this);
		
		mIsRunning = true;
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		// 서비스가 죽었을때 알람 등록
		registerRestartAlarm();

		// 스캔 그만
		iBeaconManager.unBind(this);
		
		super.onDestroy();

		TextToSpeechManager.getInstance().finalizeLibrary();
		
		mIsRunning = false;
	}
	
	
	
	
	
	
	
	// onIBeaconServiceConnect ==========================================================================
	public static Socket busSocket = null;
	public static Socket busStopSocket = null;
	public static boolean takeBusFlag = false;		// 하차했을 때, 버스 비콘 신호 들어와도 바로 접속하지 않게끔 하기 위한 플래그
	public static String beaconBusId;
	public static int handshakeMsg;
	public static long nowTime = 0;
	private int busReceiveFailCnt = 0;
	private int busStopReceiveFailCnt = 0;
	@Override
	public void onIBeaconServiceConnect() {
		iBeaconManager.setRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {				
				Log.e(TAG, "didRangeBeaconsInRegion ===========================================");
				// 블루투스 O
				if(verifyBluetooth()){
					beaconList.clear();
					beaconList.addAll((ArrayList<IBeacon>)iBeacons);
					Log.e(TAG, "잡히는 비콘 갯수 = " + iBeacons.size());
					
					if(runningDetectedBus == true){
						DetectedBus.detectedBusArr.clear();
					}
					
					// 정류장 신호 잡히면 true
					boolean busStopFlag = false;
					// 버스 신호 잡히면 true
					boolean busFlag = false;
					
					// 비콘에 잡힌 것들 죄다 둘러 보기 ===============================================
					for(int i = 0; i < iBeacons.size(); i++){
						String busId = beaconList.get(i).getProximityUuid();
		            	busId = busId.substring(busId.length() - 10, busId.length());
						int rssi = beaconList.get(i).getRssi();
						int tx = beaconList.get(i).getTxPower();
						int major = beaconList.get(i).getMajor();
						int minor = beaconList.get(i).getMinor();
						String ip12 = Integer.toHexString(major);
						String ip34 = Integer.toHexString(minor);
						if(ip12.length() < 4){
							String tip12 = "";
							for (int j = 0; j < 4 - ip12.length(); j++) {
								tip12 += "0";
							}
							tip12 += ip12;
							ip12 = tip12;
						}
						if(ip34.length() < 4){
							String tip34 = "";
							for (int j = 0; j < 4 - ip34.length(); j++) {
								tip34 += "0";
							}
							tip34 += ip34;
							ip34 = tip34;
						}
						int ip1 = Integer.parseInt(ip12.substring(0, 2), 16);
						int ip2 = Integer.parseInt(ip12.substring(2), 16);
						int ip3 = Integer.parseInt(ip34.substring(0, 2), 16);
						int ip4 = Integer.parseInt(ip34.substring(2), 16);
						String ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
						
						Log.e(TAG, i + ".       " + busId + " / " + tx + " / " + rssi + " / " + major + " / " + minor + " / " + ip);
						
						// 버스 비콘 =======================
						if(tx == Constants.SERVER_SEPARATOR_BUS){
							// 신호 수신 실패 카운트 초기화
							busReceiveFailCnt = 0;
							// 정류장에 있지 않으면
							if (busStopSocket == null) {
								busFlag = true;
								if (busSocket == null && takeBusFlag == false) {
									try {
										// DetectedBusSelectDestination 액티비티가 켜져있지 않으면
										if(runningDetectedBusSelectDestination == false){
											beaconBusId = busId;
											Log.e(TAG, ip + "    버스 연결 시도");
											new SocketThread(getApplicationContext(), ip, Constants.SOCKET_THREAD_CONNECT_BUS).execute(null,null,null);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								else{
									if(busSocket == null){
										// 아무 일 X
									}
									else if(busSocket.isConnected()){
										//generateNotification(getApplicationContext(), "버스 탑승 중");
										Log.e(TAG, ip + " 버스 연결 되어 있음" + " /    도착지 : " + DESTINATION);							
									}
								}							
							}
						}
						// /버스 비콘 =======================			

						// 정류장 비콘 =======================
						else if(tx == Constants.SERVER_SEPARATOR_BUS_STOP){
//							// 신호 수신 실패 카운트 초기화
//							busStopReceiveFailCnt = 0;
//							// 버스에 타고 있지 않으면
//							if (busSocket == null) {
//								busStopFlag = true;
//								if (busStopSocket == null) {
//									try {
//										Log.e(TAG, ip + "    정류장 연결 시도");
//										new SocketThread(getApplicationContext(), ip, Constants.SOCKET_THREAD_CONNECT_BUS_STOP).execute(null,null,null);
//									} catch (Exception e) {
//										e.printStackTrace();
//									} 
//								}
//								else{
//									if(busStopSocket.isConnected()){
//										Log.e(TAG, ip + " 정류장 연결 되어 있음");
//									}
//								}						
//							}
						}
						// /정류장 비콘 =======================
					}
					// /비콘에 잡힌 것들 죄다 둘러 보기 ===============================================

					// 버스 신호가 안잡히면
					if(busFlag == false){
						// 버스 연결되어 있다면
						if (busSocket != null) {
							// 3번 신호 안잡히면
							busReceiveFailCnt++;
							Log.e(TAG, "버스 신호 수신 실패 카운트 : " + busReceiveFailCnt);
							if(busReceiveFailCnt == 3){
								// 신호 수신 실패 카운트 초기화
								busReceiveFailCnt = 0;
								// 버스 연결 끊음
								try {
									takeBusFlag = false;
									new SocketThread(getApplicationContext(), null, Constants.SOCKET_THREAD_DISCONNECT_BUS).execute(null,null,null);
								} catch (Exception e) {
									e.printStackTrace();
								}	
							}
						}
						else{
							// 신호 수신 실패 카운트 초기화
							busReceiveFailCnt = 0;
						}
					}
					// 정류장 신호가 안잡히면
					if(busStopFlag == false){
//						// 정류장 연결되어 있다면
//						if (busStopSocket != null) {
//							// 3번 신호 안잡히면
//							busStopReceiveFailCnt++;
//							Log.e(TAG, "정류장 신호 수신 실패 카운트 : " + busStopReceiveFailCnt);
//							if(busStopReceiveFailCnt == 3){
//								// 신호 수신 실패 카운트 초기화
//								busStopReceiveFailCnt = 0;
//								// 정류장 연결 끊음
//								try {
//									new SocketThread(getApplicationContext(), null, Constants.SOCKET_THREAD_DISCONNECT_BUS_STOP).execute(null,null,null);
//								} catch (Exception e) {
//									e.printStackTrace();
//								}	
//							}
//						}
//						else{
//							// 신호 수신 실패 카운트 초기화
//							busStopReceiveFailCnt = 0;
//						}
					}
					
					if(runningDetectedBus == true){
						if(DetectedBus.detectedBusArr.size() == 0){
							Toast.makeText(getApplicationContext(), "버스 리스트가 비었습니다.", Toast.LENGTH_SHORT).show();
							DetectedBus.detectedBusActivity.finish();
						}
						DetectedBus.customAdapterDetectedBus.notifyDataSetChanged();
					}
				}
				// 블루투스 X
				else{
					Log.e(TAG, "블루투스 X");
//					ActivityManager am  = (ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
//					am.killBackgroundProcesses(getPackageName());
				}
				Log.e(TAG, "/didRangeBeaconsInRegion ===========================================");
			}
		});

		iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
			@Override
			public void didEnterRegion(Region region) {
				Toast.makeText(getApplicationContext(), "didEnterRegion", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "didEnterRegion");
//				generateNotification(BeaconDetactorService.this, region.getUniqueId()
//						+ " : just saw this iBeacon for the first time");
				// logStatus("I just saw an iBeacon for the first time!");
			}

			@Override
			public void didExitRegion(Region region) {
				Toast.makeText(getApplicationContext(), "didExitRegion", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "didExitRegion");
				// logStatus("I no longer see an iBeacon");
			}

			@Override
			public void didDetermineStateForRegion(int state, Region region) {
				Toast.makeText(getApplicationContext(), "didDetermineStateForRegion", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "didDetermineStateForRegion");
				// logStatus("I have just switched from seeing/not seeing iBeacons: " + state);
			}

		});

		try {
			iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
			iBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	// /onIBeaconServiceConnect ==========================================================================
	
	
	
	
	private boolean verifyBluetooth() {
		if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
			//Toast.makeText(getApplicationContext(), "블루투스 X", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	// 서비스가 시스템에 의해서 또는 강제적으로 종료되었을 때 호출되어
	// 알람을 등록해서 10초 후에 서비스가 실행되도록 함
	private void registerRestartAlarm() {
		Log.e(TAG, "registerRestartAlarm");
		Intent intent = new Intent(BeaconDetactorService.this, RestartServiceReceiver.class);
		intent.setAction(Constants.ACTION_RESTART_IMMORTALSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(BeaconDetactorService.this, 0, intent, 0);

		long firstTime = SystemClock.elapsedRealtime();
		firstTime += Constants.REBOOT_DELAY_TIMER; // 10초 후에 알람이벤트 발생

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, Constants.REBOOT_DELAY_TIMER, sender);
	}

	// 기존 등록되어있는 알람을 해제
	private void unregisterRestartAlarm() {
		Log.e(TAG, "unregisterRestartAlarm");
		Intent intent = new Intent(BeaconDetactorService.this, RestartServiceReceiver.class);
		intent.setAction(Constants.ACTION_RESTART_IMMORTALSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(BeaconDetactorService.this, 0, intent, 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}

	
	
}