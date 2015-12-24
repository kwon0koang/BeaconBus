package com.dgssm.beaconbus.beacon;

import java.util.ArrayList;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.CustomAdapterDetectedBus;
import com.dgssm.beaconbus.db.DbOpenHelper;
import com.radiusnetworks.ibeacon.IBeacon;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class DetectedBus extends Activity{

	private static final String TAG = "DetectedBus";
	
	public static Activity detectedBusActivity;
	
	private ListView lvDetectedBus;
	public static CustomAdapterDetectedBus customAdapterDetectedBus;
	public static ArrayList<IBeacon> detectedBusArr = new ArrayList<IBeacon>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detected_bus);

		// 커스텀 액션 바
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);
		
		detectedBusActivity = DetectedBus.this;

		// 비콘에서 받은 버스 ID를 이용하여 커스텀어댑터에서 
		// 버스 번호를 받아오기 위함
		DbOpenHelper.getInstance(DetectedBus.this).createDatabase();
		DbOpenHelper.getInstance(DetectedBus.this).openDatabase();
		
		lvDetectedBus = (ListView) findViewById(R.id.lvDetectedBus);
		customAdapterDetectedBus = new CustomAdapterDetectedBus(detectedBusActivity, getApplicationContext(), R.layout.custom_list_view_bus, detectedBusArr);
		lvDetectedBus.setAdapter(customAdapterDetectedBus);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		BeaconDetactorService.runningDetectedBus = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		BeaconDetactorService.runningDetectedBus = false;
		detectedBusArr.clear();
	}

}
