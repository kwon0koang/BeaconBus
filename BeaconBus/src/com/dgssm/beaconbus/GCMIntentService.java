package com.dgssm.beaconbus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.dgssm.beaconbus.Main;
import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.db.DbOpenHelper;
import com.dgssm.beaconbus.utils.Constants;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private final static String TAG = "GCMIntentService";
	
	@Override
	protected void onError(Context arg0, String arg1) {
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String msg = intent.getStringExtra("msg");
		Log.e(TAG, "getmessage : " + msg);
		 
		if(msg != null){
			String[] arr = msg.split("#");
			int gcmState = Integer.parseInt(arr[0]);
			String realMsg = arr[1];
			
			switch(gcmState){
			case Constants.GCM_STATE_GCM:
				Log.d(TAG, "gcmState = GCM_STATE_GCM");
				generateNotification(context, realMsg);
				break;
			case Constants.GCM_STATE_REFRESH_DB:		// DB 새롭게 갱신
				Log.d(TAG, "gcmState = GCM_REFRESH_DB");
				Log.e(TAG, "준비중");
				
				
//				Toast.makeText(context, "버스 데이터 업데이트", Toast.LENGTH_SHORT).show();
//				
//				DbOpenHelper.getInstance(context).removeDatabase();
//				DbOpenHelper.getInstance(context).createDatabase();
//				DbOpenHelper.getInstance(context).openDatabase();
				
				break;
			}			
		}
	}

	@Override
	protected void onRegistered(Context context, String reg_id) {
		Log.e(TAG, "키를 등록합니다.(GCM INTENTSERVICE) " + reg_id);
	}

	@Override
	protected void onUnregistered(Context context, String arg1) {
		Log.e(TAG, "키를 제거합니다.(GCM INTENTSERVICE) 제거되었습니다.");
	}

	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		 		 
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		 
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, Main.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP 
		        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		 
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}
	
}
