package com.dgssm.beaconbus;

import java.util.ArrayList;

import com.dgssm.beaconbus.beacon.BeaconDetactorService;
import com.dgssm.beaconbus.utils.Constants;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Test extends Activity{

	private static final String TAG = "Test";

	private Button btnTest;
	private ListView lvTest;
	private ArrayList<IBeacon> beaconList = new ArrayList<IBeacon>();
	private LayoutInflater inflater;

	private Intent beaconDetactorServiceintent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.test);

//		// 블루투스 체크
//		verifyBluetooth();
//
//		btnTest = (Button)findViewById(R.id.btnTest);
//		lvTest = (ListView)findViewById(R.id.lvTest);
//		btnTest.setOnClickListener(mListener);
//		
//		beaconDetactorServiceintent = new Intent(getApplicationContext(), BeaconDetactorService.class);
//		startService(beaconDetactorServiceintent);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopService(beaconDetactorServiceintent);
	}
	
	private OnClickListener mListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnTest:
				Toast.makeText(getApplicationContext(), "ㄲㅈ", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	private void verifyBluetooth() {
		if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
			Toast.makeText(getApplicationContext(), "블루투스 X", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	public boolean isServiceRunning(String serviceName) {
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			  }
		}
		return false;
	}

}
