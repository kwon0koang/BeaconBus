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
	
	public static boolean waitFlag = false;		// true�� ������ ������ ������ ��ٸ��� ���� �÷���

	// DetectedBus ��Ƽ��Ƽ ���ư��� �ִ��� �Ǻ�
	public static boolean runningDetectedBus = false;
	// DetectedBusSelectDestination ��Ƽ��Ƽ ���ư��� �ִ��� �Ǻ�
	public static boolean runningDetectedBusSelectDestination = false;		
	
	public static boolean transferFlag = false;
	public static String transferRoute = null;
	public static String transferDeparture = null;
	public static String transferDestination = null;
	
	public static String DESTINATION = "";		// ������ Ÿ�� �ְ�, ����Ǿ� ���� �� ��ݾ��� ���� ���� �������� ������ ���� �ٽ� ���ϰԲ� �ϱ� ����
	private boolean mIsRunning;
	
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		
		// ��ϵ� �˶��� ����
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
		Log.e(TAG, "������ = " + DESTINATION);
		Log.e(TAG, "/onStartCommand");
		
		// ��ĵ ����
		iBeaconManager.bind(this);
		
		mIsRunning = true;
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		// ���񽺰� �׾����� �˶� ���
		registerRestartAlarm();

		// ��ĵ �׸�
		iBeaconManager.unBind(this);
		
		super.onDestroy();

		TextToSpeechManager.getInstance().finalizeLibrary();
		
		mIsRunning = false;
	}
	
	
	
	
	
	
	
	// onIBeaconServiceConnect ==========================================================================
	public static Socket busSocket = null;
	public static Socket busStopSocket = null;
	public static boolean takeBusFlag = false;		// �������� ��, ���� ���� ��ȣ ���͵� �ٷ� �������� �ʰԲ� �ϱ� ���� �÷���
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
				// ������� O
				if(verifyBluetooth()){
					beaconList.clear();
					beaconList.addAll((ArrayList<IBeacon>)iBeacons);
					Log.e(TAG, "������ ���� ���� = " + iBeacons.size());
					
					if(runningDetectedBus == true){
						DetectedBus.detectedBusArr.clear();
					}
					
					// ������ ��ȣ ������ true
					boolean busStopFlag = false;
					// ���� ��ȣ ������ true
					boolean busFlag = false;
					
					// ���ܿ� ���� �͵� �˴� �ѷ� ���� ===============================================
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
						
						// ���� ���� =======================
						if(tx == Constants.SERVER_SEPARATOR_BUS){
							// ��ȣ ���� ���� ī��Ʈ �ʱ�ȭ
							busReceiveFailCnt = 0;
							// �����忡 ���� ������
							if (busStopSocket == null) {
								busFlag = true;
								if (busSocket == null && takeBusFlag == false) {
									try {
										// DetectedBusSelectDestination ��Ƽ��Ƽ�� �������� ������
										if(runningDetectedBusSelectDestination == false){
											beaconBusId = busId;
											Log.e(TAG, ip + "    ���� ���� �õ�");
											new SocketThread(getApplicationContext(), ip, Constants.SOCKET_THREAD_CONNECT_BUS).execute(null,null,null);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								else{
									if(busSocket == null){
										// �ƹ� �� X
									}
									else if(busSocket.isConnected()){
										//generateNotification(getApplicationContext(), "���� ž�� ��");
										Log.e(TAG, ip + " ���� ���� �Ǿ� ����" + " /    ������ : " + DESTINATION);							
									}
								}							
							}
						}
						// /���� ���� =======================			

						// ������ ���� =======================
						else if(tx == Constants.SERVER_SEPARATOR_BUS_STOP){
//							// ��ȣ ���� ���� ī��Ʈ �ʱ�ȭ
//							busStopReceiveFailCnt = 0;
//							// ������ Ÿ�� ���� ������
//							if (busSocket == null) {
//								busStopFlag = true;
//								if (busStopSocket == null) {
//									try {
//										Log.e(TAG, ip + "    ������ ���� �õ�");
//										new SocketThread(getApplicationContext(), ip, Constants.SOCKET_THREAD_CONNECT_BUS_STOP).execute(null,null,null);
//									} catch (Exception e) {
//										e.printStackTrace();
//									} 
//								}
//								else{
//									if(busStopSocket.isConnected()){
//										Log.e(TAG, ip + " ������ ���� �Ǿ� ����");
//									}
//								}						
//							}
						}
						// /������ ���� =======================
					}
					// /���ܿ� ���� �͵� �˴� �ѷ� ���� ===============================================

					// ���� ��ȣ�� ��������
					if(busFlag == false){
						// ���� ����Ǿ� �ִٸ�
						if (busSocket != null) {
							// 3�� ��ȣ ��������
							busReceiveFailCnt++;
							Log.e(TAG, "���� ��ȣ ���� ���� ī��Ʈ : " + busReceiveFailCnt);
							if(busReceiveFailCnt == 3){
								// ��ȣ ���� ���� ī��Ʈ �ʱ�ȭ
								busReceiveFailCnt = 0;
								// ���� ���� ����
								try {
									takeBusFlag = false;
									new SocketThread(getApplicationContext(), null, Constants.SOCKET_THREAD_DISCONNECT_BUS).execute(null,null,null);
								} catch (Exception e) {
									e.printStackTrace();
								}	
							}
						}
						else{
							// ��ȣ ���� ���� ī��Ʈ �ʱ�ȭ
							busReceiveFailCnt = 0;
						}
					}
					// ������ ��ȣ�� ��������
					if(busStopFlag == false){
//						// ������ ����Ǿ� �ִٸ�
//						if (busStopSocket != null) {
//							// 3�� ��ȣ ��������
//							busStopReceiveFailCnt++;
//							Log.e(TAG, "������ ��ȣ ���� ���� ī��Ʈ : " + busStopReceiveFailCnt);
//							if(busStopReceiveFailCnt == 3){
//								// ��ȣ ���� ���� ī��Ʈ �ʱ�ȭ
//								busStopReceiveFailCnt = 0;
//								// ������ ���� ����
//								try {
//									new SocketThread(getApplicationContext(), null, Constants.SOCKET_THREAD_DISCONNECT_BUS_STOP).execute(null,null,null);
//								} catch (Exception e) {
//									e.printStackTrace();
//								}	
//							}
//						}
//						else{
//							// ��ȣ ���� ���� ī��Ʈ �ʱ�ȭ
//							busStopReceiveFailCnt = 0;
//						}
					}
					
					if(runningDetectedBus == true){
						if(DetectedBus.detectedBusArr.size() == 0){
							Toast.makeText(getApplicationContext(), "���� ����Ʈ�� ������ϴ�.", Toast.LENGTH_SHORT).show();
							DetectedBus.detectedBusActivity.finish();
						}
						DetectedBus.customAdapterDetectedBus.notifyDataSetChanged();
					}
				}
				// ������� X
				else{
					Log.e(TAG, "������� X");
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
			//Toast.makeText(getApplicationContext(), "������� X", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	// ���񽺰� �ý��ۿ� ���ؼ� �Ǵ� ���������� ����Ǿ��� �� ȣ��Ǿ�
	// �˶��� ����ؼ� 10�� �Ŀ� ���񽺰� ����ǵ��� ��
	private void registerRestartAlarm() {
		Log.e(TAG, "registerRestartAlarm");
		Intent intent = new Intent(BeaconDetactorService.this, RestartServiceReceiver.class);
		intent.setAction(Constants.ACTION_RESTART_IMMORTALSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(BeaconDetactorService.this, 0, intent, 0);

		long firstTime = SystemClock.elapsedRealtime();
		firstTime += Constants.REBOOT_DELAY_TIMER; // 10�� �Ŀ� �˶��̺�Ʈ �߻�

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, Constants.REBOOT_DELAY_TIMER, sender);
	}

	// ���� ��ϵǾ��ִ� �˶��� ����
	private void unregisterRestartAlarm() {
		Log.e(TAG, "unregisterRestartAlarm");
		Intent intent = new Intent(BeaconDetactorService.this, RestartServiceReceiver.class);
		intent.setAction(Constants.ACTION_RESTART_IMMORTALSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(BeaconDetactorService.this, 0, intent, 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}

	
	
}