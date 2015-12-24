package com.dgssm.beaconbus.beacon;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.CustomAdapterDetectedBusSelectDestination;
import com.dgssm.beaconbus.custom.DataFavorites;
import com.dgssm.beaconbus.utils.BackPressCloseHandler;
import com.dgssm.beaconbus.utils.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class DetectedBusSelectDestination extends Activity{

	private static final String TAG = "DetectedBusSelectDestination";
	
	private EditText etSearchBusStop;
	private ImageView imgSearchBusStopX;
	
	private ListView lvBusStop;
	private CustomAdapterDetectedBusSelectDestination customAdapterDetectedBusSelectDestination;
	private ArrayList<DataFavorites> busStopList;
	
	private String busId;

	private BackPressCloseHandler backPressCloseHandler;	
	
	private Vibrator vibrator;
	
	private final int RESULT_EMPTY_TOAST = 10101;
	private final int SEARCH_BUSSTOP = 10102;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detected_bus_select_destination);

		// 커스텀 액션 바
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);
		
		etSearchBusStop = (EditText) findViewById(R.id.etSearchBusStop);
		etSearchBusStop.addTextChangedListener(busStopWatcher);
		imgSearchBusStopX = (ImageView) findViewById(R.id.imgSearchBusStopX);
		imgSearchBusStopX.setOnClickListener(mClickListener);

		backPressCloseHandler = new BackPressCloseHandler(this);
		
		Intent i = getIntent();
		busStopList = (ArrayList<DataFavorites>) i.getSerializableExtra("busStopList");
		
		lvBusStop = (ListView) findViewById(R.id.lvBusStop);
		customAdapterDetectedBusSelectDestination = new CustomAdapterDetectedBusSelectDestination(DetectedBusSelectDestination.this, getApplicationContext(), R.layout.custom_list_view_favorites, busStopList);
		lvBusStop.setAdapter(customAdapterDetectedBusSelectDestination);
		customAdapterDetectedBusSelectDestination.notifyDataSetChanged();
		
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mHandler.post(vibratorRunnable);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		BeaconDetactorService.runningDetectedBusSelectDestination = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		BeaconDetactorService.runningDetectedBusSelectDestination = false;
		BeaconDetactorService.waitFlag = true;
	}
	
	@Override
	public void onBackPressed() {
		backPressCloseHandler.onBackPressed();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case  SEARCH_BUSSTOP:
				customAdapterDetectedBusSelectDestination.notifyDataSetChanged();
				break;
				
			}
		}
		
	};
	
	private Runnable vibratorRunnable = new Runnable() {
		@Override
		public void run() {
			long[] pattern = { 0, 
					500, 100, 
					500, 100,
					500, 100,
					500, 100,
					500, 100};
			vibrator.vibrate(pattern, -1);
		}
	};
	
	// mClickListener ===============================================================
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.imgSearchBusStopX:
				etSearchBusStop.setText("");
				break;
			}
		}
	};
	
	// busStopWatcher =======================================================================================================
	private TextWatcher busStopWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d(TAG, "버스 정류장 검색어 = " + s);
			customAdapterDetectedBusSelectDestination.resetData();
			customAdapterDetectedBusSelectDestination.getFilter().filter(s.toString());
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		@Override
		public void afterTextChanged(Editable s) {}
	};
	// /busStopWatcher =======================================================================================================
	
}
