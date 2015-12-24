package com.dgssm.beaconbus;

import java.util.ArrayList;

import com.dgssm.beaconbus.beacon.BeaconDetactorService;
import com.dgssm.beaconbus.beacon.RestartServiceReceiver;
import com.dgssm.beaconbus.custom.DataBus;
import com.dgssm.beaconbus.custom.DataBusStop;
import com.dgssm.beaconbus.custom.DataFavorites;
import com.dgssm.beaconbus.db.DbOpenHelper;
import com.dgssm.beaconbus.db.FavoritesDbOpenHelper;
import com.dgssm.beaconbus.utils.BackPressCloseHandler;
import com.dgssm.beaconbus.utils.Constants;
import com.google.android.gcm.GCMRegistrar;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

public class Main extends FragmentActivity implements ActionBar.TabListener {

	private static final String TAG = "Main";
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	
	private BroadcastReceiver mReceiver;
	
	public static ArrayList<DataBus> busList;
	public static ArrayList<DataBusStop> busStopList, routeList;
	public static ArrayList<DataFavorites> favoritesList, searchHistoryList;
	
	private BackPressCloseHandler backPressCloseHandler;	
	
	public static boolean activityLiveFlagSearchBus = false;
	public static boolean activityLiveFlagSearchBusStop = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 커스텀 액션 바
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this.getApplicationContext());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			
			int[] tabTmpLayout = {R.layout.custom_tab_favorites, R.layout.custom_tab_search, R.layout.custom_tab_nearby_busstop, R.layout.custom_tab_setting};
			actionBar.addTab(actionBar.newTab()
												.setCustomView(tabTmpLayout[i])
												.setTabListener(this));
		}
		actionBar.setDisplayShowCustomEnabled(true);
		// 리시버========================
		// immortal service 등록
		Intent intentMyService = new Intent(this, BeaconDetactorService.class);
		// 리시버 등록
		mReceiver = new RestartServiceReceiver();
		try 
		{
			// xml에서 정의해도 됨?
			// 이것이 정확히 무슨 기능을 하는지?	더 찾아봐야 하는데 일단 딴게 급하니까 패스
			IntentFilter mainFilter = new IntentFilter("com.hamon.GPSservice.ssss");
			// 리시버 저장
			registerReceiver(mReceiver, mainFilter);
			// 서비스 시작
			startService(intentMyService);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage()+"");
			e.printStackTrace();
		}
		registerGCM();
		
		// DB ==============
		DbOpenHelper.getInstance(Main.this).createDatabase();
		DbOpenHelper.getInstance(Main.this).openDatabase();
		
		busList = new ArrayList<DataBus>();
		busStopList = new ArrayList<DataBusStop>();
		routeList = new ArrayList<DataBusStop>();
		favoritesList = new ArrayList<DataFavorites>();
		searchHistoryList = new ArrayList<DataFavorites>();
		DbOpenHelper.getInstance(Main.this).getBus(busList);
		DbOpenHelper.getInstance(Main.this).getBusStop(busStopList);
		DbOpenHelper.getInstance(Main.this).getBusStop(routeList);
		
		// 백버튼 핸들러
		backPressCloseHandler = new BackPressCloseHandler(this);
		
	}
	
	@Override
	protected void onDestroy() {
		// 리시버 삭제를 하지 않으면 에러
    	Log.d(TAG, "Service Destroy");
    	unregisterReceiver(mReceiver);
		
    	DbOpenHelper.getInstance(Main.this).close();
    	FavoritesDbOpenHelper.getInstance(Main.this).close();
    	
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	private void registerGCM(){
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		
		final String regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) {
			GCMRegistrar.register(this, Constants.PROJECT_NUM );		// GCMRegistrar.register(this, "프로젝트 번호" );
		} else {
			Log.e(TAG, "id = " + regId);
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

	@Override
	public void onBackPressed() {
		backPressCloseHandler.onBackPressed();
	}
}
