package com.dgssm.beaconbus.map;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.dgssm.beaconbus.FragmentSearchNearbyBusStop;

public class TouchableWrapper extends FrameLayout {
	
	public static final String TAG = "TouchableWrapper";
	
	public TouchableWrapper(Context context) {
		super(context);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		// ¸Ê ÅÍÄ¡
		case MotionEvent.ACTION_DOWN:
		    FragmentSearchNearbyBusStop.mMapIsTouched = true;
		    Log.e(TAG, "mMapIsTouched = "+FragmentSearchNearbyBusStop.mMapIsTouched);
		    break;
		// ¸Ê ÅÍÄ¡ ¶À
		case MotionEvent.ACTION_UP:
			FragmentSearchNearbyBusStop.mMapIsTouched = false;
			Log.e(TAG, "mMapIsTouched = "+FragmentSearchNearbyBusStop.mMapIsTouched);
			break;
		}
		return super.dispatchTouchEvent(event);
	}
	
}