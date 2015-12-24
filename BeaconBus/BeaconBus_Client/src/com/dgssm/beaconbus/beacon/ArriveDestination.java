package com.dgssm.beaconbus.beacon;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.utils.BackPressCloseHandler;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class ArriveDestination extends Activity {
	
	private static final String TAG = "ArriveDestination";
	
	private ImageView logo;
	private AnimationDrawable logoAnim;
	
	private Vibrator vibrator;
	
	private boolean activityliveFlag = true;

	private BackPressCloseHandler backPressCloseHandler;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.arrive_destination);

//		// 커스텀 액션 바
//		final ActionBar actionBar = getActionBar();
//		actionBar.setDisplayShowHomeEnabled(false);
//		actionBar.setDisplayShowTitleEnabled(false);
//		LayoutInflater mInflater = LayoutInflater.from(this);
//		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
//		actionBar.setCustomView(mCustomView);
//		actionBar.setDisplayShowCustomEnabled(true);
		
		logo = (ImageView)findViewById(R.id.imageView1);
		logo.setBackgroundResource(R.drawable.logo_animation);
		logoAnim = (AnimationDrawable)logo.getBackground();
		
		activityliveFlag = true;
		
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mHandler.post(vibratorRunnable);
		
		backPressCloseHandler = new BackPressCloseHandler(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		activityliveFlag = false;
	}
	
	@Override
	public void onBackPressed() {
		backPressCloseHandler.onBackPressed();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus)
			logoAnim.start();
	}
	
	private Handler mHandler = new Handler();
	private Runnable vibratorRunnable = new Runnable() {
		@Override
		public void run() {
			if(activityliveFlag == true){
				vibrator.vibrate(500);
				mHandler.postDelayed(vibratorRunnable, 1000);
			}
		}
	};
	
	
}
