package com.dgssm.beaconbus.searchedactivity;

import java.io.IOException; 
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dgssm.beaconbus.Main;
import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.CustomAdapterBusLocation;
import com.dgssm.beaconbus.custom.DataFavorites;
import com.dgssm.beaconbus.db.FavoritesDbOpenHelper;
import com.dgssm.beaconbus.outerserver.NetWorkHandler;
import com.dgssm.beaconbus.outerserver.Setting;
import com.dgssm.beaconbus.utils.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchBus extends Activity {

	private static final String TAG = "SearchBus";
	
	private TextView tvSearchedBusNum, tvSearchedBusDirection;
	private String searchedBusNum, searchedBusDirection, searchedBusId;
	private int searchedBusType;
	private ImageView ivFavoritesBus, ivRefresh, ivDirection;
	public int direction = Constants.BUS_DIRECTION_FORWARD;
	
	private ListView lvSearchedBusLocation;
	private CustomAdapterBusLocation customAdapterSearchedBusLocation;
	private ArrayList<DataFavorites> searchedBusLocationList;
	
	private final int RESULT_EMPTY_TOAST = 10101;
	private final int SEARCH_BUS_LOCATION = 10102;
	
	public static NetWorkHandler mNetworkHandler;
	public static Setting mSetting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_bus);
				
		// 커스텀 액션 바
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);

		tvSearchedBusNum = (TextView) findViewById(R.id.tvSearchedBusNum);
		tvSearchedBusDirection = (TextView) findViewById(R.id.tvSearchedBusDirection);
		Intent i = getIntent();
		searchedBusNum = i.getStringExtra("searchedBusNum");
		searchedBusDirection = i.getStringExtra("searchedBusDirection");
		searchedBusId = i.getStringExtra("searchedBusId");
		searchedBusType = i.getIntExtra("searchedBusType", 0);
		tvSearchedBusNum.setText(searchedBusNum);
		tvSearchedBusDirection.setText(searchedBusDirection);
		
		ivFavoritesBus = (ImageView) findViewById(R.id.ivFavoritesBus);
		ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
		ivDirection = (ImageView) findViewById(R.id.ivDirection);
		ivFavoritesBus.setOnClickListener(mListener);
		ivRefresh.setOnClickListener(mListener);
		ivDirection.setOnClickListener(mListener);
		
		// 조회 기록 추가
		FavoritesDbOpenHelper.getInstance(SearchBus.this).insertSearchHistory(Constants.SEPARATOR_BUS, searchedBusNum, searchedBusDirection, searchedBusId, searchedBusType);
		
		// 즐겨찾기 존재 여부
		boolean isExist = FavoritesDbOpenHelper.getInstance(SearchBus.this).isExist(searchedBusNum);
		Log.e(TAG, ""+isExist);
		if(isExist){
			ivFavoritesBus.setBackgroundResource(R.drawable.star_down);
		} else{
			ivFavoritesBus.setBackgroundResource(R.drawable.star);
		}
		
		lvSearchedBusLocation = (ListView) findViewById(R.id.lvSearchedBusLocation);
		searchedBusLocationList = new ArrayList<DataFavorites>();
		customAdapterSearchedBusLocation = new CustomAdapterBusLocation(this, getApplicationContext(), R.layout.custom_list_view_favorites, searchedBusLocationList);
		lvSearchedBusLocation.setAdapter(customAdapterSearchedBusLocation);

		new getBusLocationTask().execute(null,null,null);
		
		// 외부서버 접속
		mSetting = Setting.getInstance();
		mSetting.setServerIP(Constants.OUTER_SERVER_IP);
		mSetting.setServerPort(Constants.OUTER_SERVER_PORT);
		mNetworkHandler = new NetWorkHandler(SearchBus.this);
		mNetworkHandler.start();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Main.activityLiveFlagSearchBus = true;
	}

	@Override
	protected void onDestroy() {
		// 외부서버 접속 끊음
		if (mNetworkHandler != null) {
			mNetworkHandler.closeNetWork(false);
			mNetworkHandler = null;
		}
		Main.activityLiveFlagSearchBus = false;
		
		super.onDestroy();
	}
	
	private View.OnClickListener mListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
			case R.id.ivFavoritesBus:
				// 즐겨찾기 존재 여부
				boolean isExist = FavoritesDbOpenHelper.getInstance(SearchBus.this).isExist(searchedBusNum);
				Log.e(TAG, ""+isExist);
				if(isExist){
					Log.e(TAG, "즐겨찾기 삭제");
					FavoritesDbOpenHelper.getInstance(SearchBus.this).deleteFavorites(searchedBusNum);
					Toast.makeText(getApplicationContext(), "즐겨찾기 삭제되었습니다.", Toast.LENGTH_SHORT).show();
					ivFavoritesBus.setBackgroundResource(R.drawable.star);
				} else{
					Log.e(TAG, "즐겨찾기 등록");
					FavoritesDbOpenHelper.getInstance(SearchBus.this).insertFavorites(Constants.SEPARATOR_BUS, searchedBusNum, searchedBusDirection, searchedBusId, searchedBusType);
					Toast.makeText(getApplicationContext(), "즐겨찾기 등록되었습니다.", Toast.LENGTH_SHORT).show();
					ivFavoritesBus.setBackgroundResource(R.drawable.star_down);
				}
				break;
			case R.id.ivRefresh:
				new getBusLocationTask().execute(null,null,null);
				break;
			case R.id.ivDirection:
				if(direction == Constants.BUS_DIRECTION_FORWARD){
					direction = Constants.BUS_DIRECTION_BACKWARD;
					ivDirection.setBackgroundResource(R.drawable.direction_backward);
					new getBusLocationTask().execute(null,null,null);
				}
				else if(direction == Constants.BUS_DIRECTION_BACKWARD){
					direction = Constants.BUS_DIRECTION_FORWARD;
					ivDirection.setBackgroundResource(R.drawable.direction_forward);
					new getBusLocationTask().execute(null,null,null);
				}
				break;
			}
			
		}
	}; 
	
	
	private class getBusLocationTask extends AsyncTask<Void, Void, Void>{
		
		ProgressDialog asyncDialog = new ProgressDialog(SearchBus.this);
 
        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다.");
             
            // show dialog
            asyncDialog.show();
            asyncDialog.setCanceledOnTouchOutside(false);
            super.onPreExecute();
        }
		
		@Override
		protected Void doInBackground(Void... params) {
			// 정방향 URL = http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByPos&routeId=1000003000
			// 역방향 URL = http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByPos&routeId=1000003000
			// 형태 = http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByPos&routeId=버스 ID
			
			searchedBusLocationList.clear();
			
			String busId = searchedBusId;
			String mUrl = "http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByPos&routeId=" + busId;
			
			try {
				Document doc = Jsoup.connect(mUrl)
						.timeout(60*1000)
						.header("Accept-Language", "ko-kr")
						.get();
				
				Elements lists = null;
				if(direction == Constants.BUS_DIRECTION_FORWARD){
					Log.e(TAG, "정방향");
					Log.e(TAG, "============================");
					lists = doc.select("#posForwardPanel table tbody tr td");				
				}
				else if(direction == Constants.BUS_DIRECTION_BACKWARD){
					Log.e(TAG, "역방향");
					Log.e(TAG, "============================");
					lists = doc.select("#posBackwardPanel table tbody tr td");
				}
				
				if(lists.size() == 0){
					mHandler.sendEmptyMessage(RESULT_EMPTY_TOAST);
				}
				
				for(int i = 0; i < lists.size(); i ++){
		        	Element e = lists.get(i);
		        	
		        	String s = e.text();
		        	if(s.equals("")) continue;
		        	
		        	String type;
		        	// 정류장
		        	if(e.attr("class").equals("body_col1")){
		        		type = Constants.SEPARATOR_BUSSTOP;
			        	System.out.println(i + "        " + type + "   /   " + s);
			        	searchedBusLocationList.add(new DataFavorites(type, R.drawable.icon_busstop, s, "", "", 0));
		        	}
		        	// 버스
		        	else { 
		        		type = Constants.SEPARATOR_BUS;
		        		String busDepart = s.substring(0, s.length() - 11);
		        		String busArrival = s.substring(s.length() - 9, s.length() - 1);
		        		System.out.println(i + "        " + type + "   /   " + busDepart + " / " + busArrival);
		        		// 저상 버스면
		        		if(busDepart.indexOf("저상") != -1){
		        			busDepart = busDepart.substring(0, busDepart.length() - 4);
			        		searchedBusLocationList.add(new DataFavorites(type, R.drawable.icon_bus, busDepart, busArrival, "저상", 0));	
		        		}
		        		// 저상 버스가 아니면
		        		else{
			        		searchedBusLocationList.add(new DataFavorites(type, R.drawable.icon_bus, busDepart, busArrival, "", 0));
		        		}
		        	}
		        }
				Log.e(TAG, customAdapterSearchedBusLocation.getCount()+"");
				
				mHandler.sendEmptyMessage(SEARCH_BUS_LOCATION);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case RESULT_EMPTY_TOAST:
				Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
				if(direction == Constants.BUS_DIRECTION_FORWARD){
					direction = Constants.BUS_DIRECTION_BACKWARD;
					ivDirection.setBackgroundResource(R.drawable.direction_backward);
					new getBusLocationTask().execute(null,null,null);
				}
				else if(direction == Constants.BUS_DIRECTION_BACKWARD){
					direction = Constants.BUS_DIRECTION_FORWARD;
					ivDirection.setBackgroundResource(R.drawable.direction_forward);
					new getBusLocationTask().execute(null,null,null);
				}
				mHandler.sendEmptyMessage(SEARCH_BUS_LOCATION);
				break;
			case SEARCH_BUS_LOCATION:
				customAdapterSearchedBusLocation.notifyDataSetChanged();
				break;
			}
		}
	};

	
}
