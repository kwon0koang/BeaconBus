package com.dgssm.beaconbus;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dgssm.beaconbus.custom.CustomAdapterBusStop;
import com.dgssm.beaconbus.custom.DataBusStop;
import com.dgssm.beaconbus.map.MySupportMapFragment;
import com.dgssm.beaconbus.searchedactivity.SearchBusStop;
import com.dgssm.beaconbus.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSearchNearbyBusStop extends Fragment {
	
	private static final String TAG = "FragmentSearchNearbyBusStop";
	
	private View rootView = null;
	private static FragmentSearchNearbyBusStop fragment = null;
	
	private GoogleMap mMap;
	private LatLng centerLatLng;
	private GpsInfo gps;
	private ArrayList<DataBusStop> filterDataList;
	private ListView lvNearbyBusStop;
	private CustomAdapterBusStop customAdapterBusStop;	
	private RelativeLayout rlGpsStatus;
	
	public static boolean mMapIsTouched = false;
	public static boolean mChkVisitSetGps = false;
	
	int sectionNumber;
	public FragmentSearchNearbyBusStop(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static FragmentSearchNearbyBusStop newInstance(int sectionNumber) {
		Log.d(TAG, sectionNumber + " / " + TAG);
		fragment = new FragmentSearchNearbyBusStop(sectionNumber);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try{
			rootView = inflater.inflate(R.layout.fragment_search_nearby_busstop, container, false);
        }catch (InflateException e){
            // 구글맵 View가 이미 inflate되어 있는 상태이므로, 에러를 무시, 이후 계속
        }
        
		gps = new GpsInfo(rootView.getContext());
		//SupportMapFragment smf = (SupportMapFragment) ((FragmentActivity)getActivity()).getSupportFragmentManager().findFragmentById(R.id.mapNearbyBusStop);
		MySupportMapFragment smf = (MySupportMapFragment) ((FragmentActivity)getActivity()).getSupportFragmentManager().findFragmentById(R.id.mapNearbyBusStop);
		mMap = smf.getMap();
		
		rlGpsStatus = (RelativeLayout) rootView.findViewById(R.id.rlGpsStatus);
		if (gps.isGetLocation()) {
			centerLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());
			//rlGpsStatus.setVisibility(View.INVISIBLE);
			rlGpsStatus.setVisibility(View.GONE);
			if(gps.getLatitude() == 0){		// 프래그먼트 왔다갔다하면서 GPS 껐다켰다하면 왜 0이 뜰까...................... 나중에 확인 해보자 
				LatLng tmpLatLng = new LatLng(35.869595, 128.597677);		// 2.28 기념 중앙 공원
				centerLatLng = tmpLatLng;
			}
		}
		else {
			// GPS 를 사용할수 없으므로 임의 지정
			centerLatLng = new LatLng(35.869595, 128.597677);		// 2.28 기념 중앙 공원
			rlGpsStatus.setVisibility(View.VISIBLE);
			ImageView btnSetGps = (ImageView) rootView.findViewById(R.id.btnSetGps);
			btnSetGps.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					gps.showSettingsAlert();	
				}
			});
		}
		Log.e(TAG, centerLatLng+"");
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, Constants.MAP_ZOOM_LEVEL));
		mMap.setOnCameraChangeListener(mOnCameraChangeListener);
		mMap.setOnMarkerClickListener(mOnMarkerClickListener);
		
		lvNearbyBusStop = (ListView) rootView.findViewById(R.id.lvNearbyBusStop);
		filterDataList = new ArrayList<DataBusStop>();
		customAdapterBusStop = new CustomAdapterBusStop(getActivity(), R.layout.custom_list_view_busstop, filterDataList);
		lvNearbyBusStop.setAdapter(customAdapterBusStop);		// busStopList에 정류장 정보가 다 있지만, 리스트에 띄우는 건 filterDataList
		customAdapterBusStop.notifyDataSetChanged();
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		GpsInfo tmpGps = new GpsInfo(rootView.getContext());
		
//		Log.e(TAG, "=========================");
//		Log.e(TAG, "tmpGps.isGetLocation = " + tmpGps.isGetLocation);
//		Log.e(TAG, "mChkVisitSetGps = " + mChkVisitSetGps);
//		Log.e(TAG, "=========================");
		
		if(tmpGps.isGetLocation()){		// GPS가 켜져 있고,
			if(mChkVisitSetGps){			// 설정창 다녀 왔으면 액티비티 재시작
				mChkVisitSetGps = false;
				getActivity().finish();
				getActivity().startActivity(new Intent(getActivity(), Main.class));
			}
		}
		else{		// GPS가 꺼져 있으면 그냥 그대로
			mChkVisitSetGps = false;
		}
	}
	

	
	
	private String prevMarkerTitle = "";
	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker m) {
			// mOnCameraChangeListener 실행되서 마커 새로고침하기 전에
			// mMapIsTouched를 true로 바꿔 줌
			mMapIsTouched = true;
			
			// 같은 거 두번 선택하면 액티비티 이동
			String nowMarkerTitle = m.getTitle();
			if(prevMarkerTitle.equals(nowMarkerTitle)){
				for(int i = 0; i < filterDataList.size(); i++){
					if(filterDataList.get(i).getBusStopName().equals(nowMarkerTitle)){
						filterDataList.get(i).getBusStopName();
						Intent intent = new Intent(getActivity(), SearchBusStop.class);
						intent.putExtra( "searchedBusStopName", filterDataList.get(i).getBusStopName() );
						intent.putExtra( "searchedBusStopNum", filterDataList.get(i).getBusStopNum());
						intent.putExtra( "searchedBusStopId", filterDataList.get(i).getBusStopId());
						getActivity().startActivity(intent);
						getActivity().overridePendingTransition(R.anim.fade, R.anim.hold);
						break;
					}
				}
			}
			else{
				prevMarkerTitle = nowMarkerTitle;
			}
			
			return false;
		}
	};
	
	private OnCameraChangeListener mOnCameraChangeListener = new OnCameraChangeListener() {
		@Override
		public void onCameraChange(CameraPosition cp) {
			if(mMapIsTouched == false){
				mHandler.post(markBusStopRunnable);
			}
		}
	};
	

	public static Handler mHandler = new Handler();
	private Runnable markBusStopRunnable = new Runnable() {
		@Override
		public void run() {
			Log.e(TAG, "버스 마커 새로고침");
			LatLng flLatLng = mMap.getProjection().getVisibleRegion().farLeft;			// 좌측 상단
			LatLng nrLatLng = mMap.getProjection().getVisibleRegion().nearRight;		// 우측 하단
			// 중앙 좌표 계산
			double centerLat = (nrLatLng.latitude + flLatLng.latitude) / 2;
			double centerLng = (nrLatLng.longitude + flLatLng.longitude) / 2;
			LatLng tmpCenterLatLng = new LatLng(centerLat, centerLng);
			centerLatLng = tmpCenterLatLng;
			
			// 클리어
			mMap.clear();
			
			// 현재 중앙
			//mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)).position(centerLatLng).title("현재 중앙"));
			
			// 센터 LatLng에서부터 Constants.MAP_RADIUS 이하로 떨어져 있는 것들만 마킹 ==========
			double latPlus = centerLatLng.latitude + Constants.MAP_RADIUS;
			double latMinus = centerLatLng.latitude - Constants.MAP_RADIUS;
			double lngPlus = centerLatLng.longitude + Constants.MAP_RADIUS;
			double lngMinus = centerLatLng.longitude - Constants.MAP_RADIUS;
			double lat, lng;
			
            filterDataList.clear();
            
			for(int i = 0; i < Main.busStopList.size(); i++){
				lat = Main.busStopList.get(i).getBusStopLat();
				lng = Main.busStopList.get(i).getBusStopLng();
				if(lat < latPlus && lng > lngMinus &&
						lat > latMinus && lng < lngPlus){
					LatLng tmpLatLng = new LatLng(lat, lng);
					String tmpTitle = Main.busStopList.get(i).getBusStopName();
					mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).position(tmpLatLng).title(tmpTitle));
					filterDataList.add(Main.busStopList.get(i));
				}
			}
			customAdapterBusStop.notifyDataSetChanged();
		}
	};
	
	
	
	
	
}
