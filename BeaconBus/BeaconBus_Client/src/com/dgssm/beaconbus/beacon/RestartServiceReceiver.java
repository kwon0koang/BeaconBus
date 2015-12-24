package com.dgssm.beaconbus.beacon;

import com.dgssm.beaconbus.beacon.BeaconDetactorService;
import com.dgssm.beaconbus.utils.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class RestartServiceReceiver extends BroadcastReceiver {

	private static final String TAG = "RestartServiceReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "RestartServiceReceiver called!!!!!!!!!!!!!!!!!!!!!!!");

		// 서비스 죽일때 알람으로 다시 서비스 등록 
		if (intent.getAction().equals(Constants.ACTION_RESTART_IMMORTALSERVICE)) {
			Log.d(TAG, "Service dead, but resurrection");

			Intent i = new Intent(context,BeaconDetactorService.class);
			context.startService(i);
		}

		// 폰 재부팅할때 서비스 등록
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			Log.d(TAG, "ACTION_BOOT_COMPLETED");

			Intent i = new Intent(context, BeaconDetactorService.class);
			context.startService(i);
						
		}
	}
}
