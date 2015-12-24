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
		// �� ��ġ
		case MotionEvent.ACTION_DOWN:
		    FragmentSearchNearbyBusStop.mMapIsTouched = true;
		    Log.e(TAG, "mMapIsTouched = "+FragmentSearchNearbyBusStop.mMapIsTouched);
		    break;
		// �� ��ġ ��
		case MotionEvent.ACTION_UP:
			FragmentSearchNearbyBusStop.mMapIsTouched = false;
			Log.e(TAG, "mMapIsTouched = "+FragmentSearchNearbyBusStop.mMapIsTouched);
			break;
		}
		return super.dispatchTouchEvent(event);
	}
	
}