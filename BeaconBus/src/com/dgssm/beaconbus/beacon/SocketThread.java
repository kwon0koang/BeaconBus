package com.dgssm.beaconbus.beacon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import net.daum.mf.speech.api.TextToSpeechClient;
import net.daum.mf.speech.api.TextToSpeechListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.DataFavorites;
import com.dgssm.beaconbus.utils.Constants;
import com.radiusnetworks.ibeacon.IBeaconManager;

public class SocketThread extends AsyncTask<Void, Void, Void> implements TextToSpeechListener {
	
	private static final String TAG = "SocketThread";
	
	private Context mContext;
	
	private String ip = null;
	private int what;
	
	private TextToSpeechClient ttsClient;
	
	public SocketThread(Context context, String ip, int what) {
		this.mContext = context;
		this.ip = ip;
		this.what = what;
		
		ttsClient = new TextToSpeechClient.Builder()
						        .setApiKey(Constants.DAUM_NEWTONETALK_API_KEY)              // �߱޹��� api key
						        .setSpeechSpeed(1.0)            // ���� �ӵ� (0.5~4.0)
						        .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT)  //TTS ���� ��� ����
						        .setListener(this)
						        .build();
	}
	
	// doInBackground ================================================================================================
	@Override
	protected Void doInBackground(Void... params) {
		switch(what){
		case Constants.SOCKET_THREAD_CONNECT_BUS_STOP:
			try {
				BeaconDetactorService.busStopSocket = new Socket(ip, Constants.SERVER_PORT_BUS_OR_BUS_STOP);
				final BufferedInputStream socketIn = new BufferedInputStream(BeaconDetactorService.busStopSocket.getInputStream());
				final BufferedOutputStream socketOut = new BufferedOutputStream(BeaconDetactorService.busStopSocket.getOutputStream());
				
				mHandler.sendEmptyMessage(Constants.CONNECTED_BUSSTOP);
				
				// Handshake START
				new Thread() {
					public void run() {
						try {
							while(true){
								socketOut.write(1);
								socketOut.flush();
								
								BeaconDetactorService.handshakeMsg = 0;
								BeaconDetactorService.busStopSocket.setSoTimeout(15000);
//								// �������� ������������ �ȳ������ ���
//								// üũ�ϱ� ���� ������
//								BeaconDetactorService.nowTime = System.currentTimeMillis();
//								if(HandshakeCheckThread.getInstance(BeaconDetactorService.busStopSocket).isAlive() == false){
//									HandshakeCheckThread.getInstance(BeaconDetactorService.busStopSocket).start();	
//								}
								
								// �� Handshake üũ �����忡�� ���� Close���� ���
								// ���� �߻�, try catch !
								BeaconDetactorService.handshakeMsg = socketIn.read();
								
								// Handshake OK
								if(BeaconDetactorService.handshakeMsg > 0) {
									Log.e(TAG, "Handshake OK / handshakeMsg = " + BeaconDetactorService.handshakeMsg);
								}
								// Handshake NO
								else {
									Log.e(TAG, "Handshake NO / handshakeMsg = " + BeaconDetactorService.handshakeMsg);
									// ���� ����
									if (BeaconDetactorService.busStopSocket != null) {
										Log.e(TAG, "DISCONNECT_BUS_STOP");
										BeaconDetactorService.busStopSocket.close();
										BeaconDetactorService.busStopSocket = null;
										this.interrupt();
										break;
									}
								}
								
								// 3�ʸ��� ������� Handshake
								Thread.sleep(3000);				
							}
						} catch (Exception e) {
							e.printStackTrace();
							// ���� ����
							try{
								if (BeaconDetactorService.busStopSocket != null) {
									Log.e(TAG, "DISCONNECT_BUS_STOP 1");
									BeaconDetactorService.busStopSocket.close();
									BeaconDetactorService.busStopSocket = null;
									this.interrupt();
								}
							}
							catch(Exception e1){
								e1.printStackTrace();
							}
						}
					};
				}.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case Constants.SOCKET_THREAD_DISCONNECT_BUS_STOP:
			if (BeaconDetactorService.busStopSocket != null) {
				try {
					Log.e(TAG, "DISCONNECT_BUS_STOP 2");
					BeaconDetactorService.busStopSocket.close();
					BeaconDetactorService.busStopSocket = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case Constants.SOCKET_THREAD_CONNECT_BUS:
			try {
				BeaconDetactorService.waitFlag = false;
				
				// ������ ���� �Ǿ� ���� �ʴٸ� ������ ����
				if(BeaconDetactorService.DESTINATION.equals("") == true || BeaconDetactorService.DESTINATION == null){
					// �Ľ� �� ������ ���� ��Ƽ��Ƽ ���
					getBusStop();

					while(true){
						if(BeaconDetactorService.waitFlag == true) break;
						if(isServiceRunning("com.dgssm.beaconbus.beacon.BeaconDetactorService") == false) break;
						Log.e(TAG, "������ ���� ��ٸ��� ��..........................");
						Thread.sleep(1000);
					}					
				}
				
				BeaconDetactorService.waitFlag = false;

				Log.e(TAG, "=========================================");
				Log.e(TAG, "������ : " + BeaconDetactorService.DESTINATION);
				Log.e(TAG, "=========================================");
				
				// ������ ���������� ���� �����ϰ� ������ ������
				// �������κ��� ���������� ����
				if(BeaconDetactorService.DESTINATION.equals("") == false && BeaconDetactorService.DESTINATION != null){
					BeaconDetactorService.busSocket = new Socket(ip, Constants.SERVER_PORT_BUS_OR_BUS_STOP);
					final BufferedReader socketIn = new BufferedReader(new InputStreamReader(BeaconDetactorService.busSocket.getInputStream()));
					final PrintWriter socketOut = new PrintWriter(BeaconDetactorService.busSocket.getOutputStream(), true);
					socketOut.println(BeaconDetactorService.DESTINATION);
					
					new Thread() {
						public void run() {
							try {
								while(true){
									BeaconDetactorService.handshakeMsg = 0;
									BeaconDetactorService.busSocket.setSoTimeout(15000);
//									// �������� ������������ �ȳ������ ���
//									// üũ�ϱ� ���� ������
//									BeaconDetactorService.nowTime = System.currentTimeMillis();
//									if(HandshakeCheckThread.getInstance(BeaconDetactorService.busSocket).isAlive() == false){
//										HandshakeCheckThread.getInstance(BeaconDetactorService.busSocket).start();	
//									}
									
									// �� Handshake üũ �����忡�� ���� Close���� ���
									// ���� �߻�, try catch !
									String handshakeMsg = socketIn.readLine();

									if(handshakeMsg == null) {
										// ��Ƽ�����̼� �˸�
										//generateNotification(mContext, "���� ž�� ��");
										
										// ���� ����
										try{
											if (BeaconDetactorService.busSocket != null) {
												Log.e(TAG, "DISCONNECT_BUS 1");
												BeaconDetactorService.busSocket.close();
												BeaconDetactorService.busSocket = null;
												this.interrupt();
											}										
										}
										catch(Exception e1){
											e1.printStackTrace();
										}
									}
									
									
									switch(handshakeMsg){
									// Handshake OK
									case "1":
										// ��Ƽ�����̼� �˸�
										//generateNotification(mContext, "���� ž�� ��");
										
										Log.e(TAG, "Handshake OK / handshakeMsg = " + handshakeMsg);
										break;
									// ���� �˸�	
									case "-3":
										mHandler.sendEmptyMessage(Constants.ARRIVE_DESTINATION);
										break;
									// ����
									case "-1":
										// ��Ƽ�����̼� ����
										//cancelNotification(mContext);
										
										// �˸�
										BeaconDetactorService.takeBusFlag = true;		// true�� �� ������ ���� ���� ��ȣ ������ ���� X
										BeaconDetactorService.DESTINATION = "";			// ������ �ʱ�ȭ
										Log.e(TAG, "handshakeMsg = " + handshakeMsg + "   /   ������ ����");
										
										// �������� �����ߴٰ� ����
										socketOut.println("-1");
										socketOut.flush();
										
										// ���� ����
										if (BeaconDetactorService.busSocket != null) {
											Log.e(TAG, "DISCONNECT_BUS 2");
											BeaconDetactorService.busSocket.close();
											BeaconDetactorService.busSocket = null;
											this.interrupt();
											break;
										}
										break;
									// ������ �� �� ����
									case "-2":
										// ��Ƽ�����̼� ����
										//cancelNotification(mContext);
										
										Log.e(TAG, "Handshake NO / len = " + handshakeMsg);
										mHandler.sendEmptyMessage(Constants.ERROR_DESTINATION);
										BeaconDetactorService.DESTINATION = "";			// ������ �ʱ�ȭ
										
										// ���� ����
										if (BeaconDetactorService.busSocket != null) {
											Log.e(TAG, "DISCONNECT_BUS 3");
											BeaconDetactorService.busSocket.close();
											BeaconDetactorService.busSocket = null;
											this.interrupt();
											break;
										}
										break;
									// ���� ������ �˸�
									default:
										SharedPreferences prefs = mContext.getSharedPreferences("AlarmNextBusStop", mContext.MODE_PRIVATE);
										// �˶� O
										if(prefs.getBoolean("isAlarmOn", true)){
											Log.e(TAG, "�˶� OOOOOOOOOOOOOOOOOOOO");
											Log.e(TAG, "���� ������ = " + handshakeMsg);
											String voice = "���� �������� ," + handshakeMsg + ", �Դϴ�.";
											ttsClient.play(voice);	
										}
										else{
											Log.e(TAG, "�˶� XXXXXXXXXXXXXXXXXXXXXX");
										}
										
										break;
									}
									
									// �������� ����
									socketOut.println("1");
									socketOut.flush();
									
									// 3�ʸ��� ������ Handshake
									Thread.sleep(3000);		
								}
							} catch (Exception e) {
								e.printStackTrace();
								
								// ��Ƽ�����̼� ����
								//cancelNotification(mContext);
								
								// ���� ����
								try{
									if (BeaconDetactorService.busSocket != null) {
										Log.e(TAG, "DISCONNECT_BUS 5");
										BeaconDetactorService.busSocket.close();
										BeaconDetactorService.busSocket = null;
										this.interrupt();
									}										
								}
								catch(Exception e1){
									e1.printStackTrace();
								}
							}
						};
					}.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case Constants.SOCKET_THREAD_DISCONNECT_BUS:
			// ��Ƽ�����̼� ����
			//cancelNotification(mContext);
			
			if(BeaconDetactorService.busSocket != null){
				try {
					Log.e(TAG, "DISCONNECT_BUS 6" + " /    ������ : " + BeaconDetactorService.DESTINATION);
					BeaconDetactorService.busSocket.close();
					BeaconDetactorService.busSocket = null;
				} catch (IOException e) {
					e.printStackTrace();
				}					
			}
			break;
		}
		
		return null;
	}
	// /doInBackground ================================================================================================
	
	// mHandler ============================================================================================================
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case Constants.ARRIVE_DESTINATION:
				AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(mContext, ArriveDestination.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
				
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pIntent);
				break;
			case Constants.ERROR_DESTINATION:
				Toast.makeText(mContext, "�������� �ùٸ��� �ʽ��ϴ�.", Toast.LENGTH_SHORT).show();
				break;
			case Constants.CONNECTED_BUSSTOP:
				Toast.makeText(mContext, "���� ������ ����", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	// /mHandler ============================================================================================================
	
	
	// getBusStop =======================================================================================
	public void getBusStop(){
		 ArrayList<DataFavorites> busStopList = new ArrayList<DataFavorites>();
		 busStopList.clear();
		 
		// URL = http://m.businfo.go.kr/bp/m/route.do?act=route&roId=1000003000&roNo=&m=01
		
		String mUrl = "http://m.businfo.go.kr/bp/m/route.do?act=route&roId=" + BeaconDetactorService.beaconBusId + "&roNo=&m=01";
		
		try {
			Document doc = Jsoup.connect(mUrl)
					.timeout(60*1000)
					.header("Accept-Language", "ko-kr")
					.get();
			
			Elements elementsAll = doc.select(".nx");
			
	        for(int i = 0; i < elementsAll.size(); i++){
	        	String eTmpBusStopName = elementsAll.get(i).text();
	        	String[] arr = eTmpBusStopName.split(" ");
	        	String busStopName = arr[1];
	        	System.out.println(busStopName);
	        	busStopList.add(new DataFavorites(Constants.SEPARATOR_BUSSTOP, R.drawable.icon_busstop, busStopName, "", "", 0));
	        }
	        
	        // ������ ���� ��Ƽ��Ƽ �̵�
	        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(mContext, DetectedBusSelectDestination.class)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("busStopList", busStopList);
			PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
			
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pIntent);
	        
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	// /getBusStop =======================================================================================
	
	private static Intent notiIntent;
	private static NotificationManager nm;
	private static Notification noti;
	private static int notiId = 1111;
	// Issues a notification to inform the user that server has sent a message.
	public static void generateNotification(Context context, String message) {
		nm = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
		noti = new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher).setTicker(message)
				.setContentTitle(context.getString(R.string.app_name)).setContentText(message)
				.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0)).setAutoCancel(true)
				.build();
		
		nm.notify(notiId, noti);		
	}
	public static void cancelNotification(Context context){
		nm.cancelAll();
	}
	
	private boolean verifyBluetooth() {
		if (!IBeaconManager.getInstanceForApplication(mContext).checkAvailability()) {
			//Toast.makeText(getApplicationContext(), "������� X", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public boolean isServiceRunning(String serviceName) {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
		for (RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			  }
		}
		return false;
	}

	@Override
	public void onError(int arg0, String arg1) {
		
	}

	@Override
	public void onFinished() {
		
	}
}