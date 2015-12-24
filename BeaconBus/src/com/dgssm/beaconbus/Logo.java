package com.dgssm.beaconbus;

import com.radiusnetworks.ibeacon.IBeaconManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class Logo extends Activity {

	private static final String TAG = "Logo";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.logo);
		
		if(availableNetwork()){
			if(verifyBluetooth()){
				Log.e(TAG, "블루투스 O");
				Handler handler = new Handler(){
					public void handleMessage(Message msg){
						super.handleMessage(msg);
						Intent intent = new Intent(Logo.this, Main.class);
						// 테스트
//						Intent intent = new Intent(Logo.this, ArriveDestination.class);
//						intent.putExtra("id", "1000003000");		// 급행3 ID
						// /테스트
						startActivity(intent);
						overridePendingTransition(R.anim.fade, R.anim.hold);
						finish();
					}
				};			
				handler.sendEmptyMessageDelayed(0, 1 * 500);		// 0.5s	
			}
			else{
				Log.e(TAG, "블루투스 X");
				Toast.makeText(getApplicationContext(), "블루투스 X", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "네트워크 X", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		
		
	}
	
	
	private Boolean availableNetwork(){
		ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
		boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
		boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

		if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){
			return true;
		}else{
			return false;
		}
	}

	private boolean verifyBluetooth() {
		if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
			//Toast.makeText(getApplicationContext(), "블루투스 X", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
}
