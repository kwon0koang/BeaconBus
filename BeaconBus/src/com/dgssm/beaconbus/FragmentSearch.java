package com.dgssm.beaconbus;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dgssm.beaconbus.custom.CustomAdapterBus;
import com.dgssm.beaconbus.custom.CustomAdapterBusStop;
import com.dgssm.beaconbus.custom.CustomAdapterRoute;
import com.dgssm.beaconbus.searchedactivity.SearchRoute;
import com.dgssm.beaconbus.utils.Constants;
	

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSearch extends Fragment {
	
	private static final String TAG = "FragmentSearch";
	
	
	
	private View rootView;
	
	private RadioGroup rgSearch;
	private RelativeLayout rlSearchBus, rlSearchBusStop, rlSearchRoute;
	private EditText etSearchBus, etSearchBusStop, etSearchRouteDeparture, etSearchRouteDestination;
	private ImageView imgSearchBusX, imgSearchBusStopX, imgSearchRouteDepartureX, imgSearchRouteDestinationX;
	private Button btnSearchRoute;
	
	private ListView lvBus, lvBusStop, lvRoute;
	private CustomAdapterBus customAdapterBus;
	private CustomAdapterBusStop customAdapterBusStop;
	private CustomAdapterRoute customAdapterRoute;
	public static int nowRouteFocus = Constants.FOCUS_DEPARTURE;
	
	public static String searchRouteDepartureName, searchRouteDestinationName;
	public static String searchRouteDepartureNum, searchRouteDestinationNum;
	public static String searchRouteDepartureId, searchRouteDestinationId;
	
	int sectionNumber;
	public FragmentSearch(int sectionNumber) {
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
	public static FragmentSearch newInstance(int sectionNumber) {
		Log.d(TAG, sectionNumber + " / " + TAG);
		FragmentSearch fragment = new FragmentSearch(sectionNumber);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_search, container, false);
		
		// 리스트뷰 위 부분 =====================
		rgSearch = (RadioGroup) rootView.findViewById(R.id.rgSearch);
		rgSearch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		
		rlSearchBus = (RelativeLayout) rootView.findViewById(R.id.rlSearchBus);
		rlSearchBusStop = (RelativeLayout) rootView.findViewById(R.id.rlSearchBusStop);
		rlSearchRoute = (RelativeLayout) rootView.findViewById(R.id.rlSearchRoute);
		rlSearchBusStop.setVisibility(View.INVISIBLE);
		rlSearchRoute.setVisibility(View.INVISIBLE);
		
		etSearchBus = (EditText) rootView.findViewById(R.id.etSearchBus);
		etSearchBus.addTextChangedListener(busWatcher);
		imgSearchBusX = (ImageView) rootView.findViewById(R.id.imgSearchBusX);
		imgSearchBusX.setOnClickListener(mClickListener);
		
		etSearchBusStop = (EditText) rootView.findViewById(R.id.etSearchBusStop);
		etSearchBusStop.addTextChangedListener(busStopWatcher);
		imgSearchBusStopX = (ImageView) rootView.findViewById(R.id.imgSearchBusStopX);
		imgSearchBusStopX.setOnClickListener(mClickListener);
		
		etSearchRouteDeparture = (EditText) rootView.findViewById(R.id.etSearchRouteDeparture);
		etSearchRouteDestination = (EditText) rootView.findViewById(R.id.etSearchRouteDestination);
		etSearchRouteDeparture.addTextChangedListener(routeWatcher);
		etSearchRouteDestination.addTextChangedListener(routeWatcher);
		etSearchRouteDeparture.setOnFocusChangeListener(departureFocusChangeListener);
		etSearchRouteDestination.setOnFocusChangeListener(destinationFocusChangeListener);
		imgSearchRouteDepartureX = (ImageView) rootView.findViewById(R.id.imgSearchRouteDepartureX);
		imgSearchRouteDestinationX = (ImageView) rootView.findViewById(R.id.imgSearchRouteDestinationX);
		btnSearchRoute = (Button) rootView.findViewById(R.id.btnSearchRoute);
		imgSearchRouteDepartureX.setOnClickListener(mClickListener);
		imgSearchRouteDestinationX.setOnClickListener(mClickListener);
		btnSearchRoute.setOnClickListener(mClickListener);
		// 리스트뷰 위 부분 end =====================
		
		mHandler.post(mlistViewInitRunnable);
		
		return rootView;
	}

	
	
	
	
	private Handler mHandler = new Handler();
	private Runnable mlistViewInitRunnable = new Runnable() {
		@Override
		public void run() {
			lvBus = (ListView) rootView.findViewById(R.id.lvBus);
			customAdapterBus = new CustomAdapterBus(getActivity(), R.layout.custom_list_view_bus, Main.busList);
			lvBus.setAdapter(customAdapterBus);
			customAdapterBus.notifyDataSetChanged();
			
			lvBusStop = (ListView) rootView.findViewById(R.id.lvBusStop);
			customAdapterBusStop = new CustomAdapterBusStop(getActivity(), R.layout.custom_list_view_busstop, Main.busStopList);
			lvBusStop.setAdapter(customAdapterBusStop);
			customAdapterBusStop.notifyDataSetChanged();
			
			lvRoute = (ListView) rootView.findViewById(R.id.lvRoute);
			customAdapterRoute = new CustomAdapterRoute(getActivity(), R.layout.custom_list_view_busstop, Main.routeList, 
																				etSearchRouteDeparture, etSearchRouteDestination);
			lvRoute.setAdapter(customAdapterRoute);
			customAdapterRoute.notifyDataSetChanged();
		}
	};
	
	
	
	// Watchers =======================================================================================================
	private TextWatcher busWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d(TAG, "버스 검색어 = " + s);
			customAdapterBus.resetData();
			customAdapterBus.getFilter().filter(s.toString());
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		@Override
		public void afterTextChanged(Editable s) {}
	};
	private TextWatcher busStopWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d(TAG, "버스 정류장 검색어 = " + s);
			customAdapterBusStop.resetData();
			customAdapterBusStop.getFilter().filter(s.toString());
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		@Override
		public void afterTextChanged(Editable s) {}
	};
	private TextWatcher routeWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d(TAG, "경로 검색어 = " + s);
			customAdapterRoute.resetData();
			customAdapterRoute.getFilter().filter(s.toString());
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		@Override
		public void afterTextChanged(Editable s) {}
	};
	// Watchers end =======================================================================================================
	
	
	
	
	// OnFocusChangeListener =======================================================================================================
	private OnFocusChangeListener departureFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				nowRouteFocus = Constants.FOCUS_DEPARTURE;
				customAdapterRoute.resetData();
				customAdapterRoute.getFilter().filter(etSearchRouteDeparture.getText().toString());
				customAdapterRoute.notifyDataSetChanged();
			}
		}
	};
	private OnFocusChangeListener destinationFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				nowRouteFocus = Constants.FOCUS_DESTINATION;
				customAdapterRoute.resetData();
				customAdapterRoute.getFilter().filter(etSearchRouteDestination.getText().toString());
				customAdapterRoute.notifyDataSetChanged();
			}
		}
	};
	// OnFocusChangeListener end =======================================================================================================

	
	
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.imgSearchBusX:
				etSearchBus.setText("");
				break;
			case R.id.imgSearchBusStopX:
				etSearchBusStop.setText("");
				break;
			case R.id.imgSearchRouteDepartureX:
				etSearchRouteDeparture.setText("");
				break;
			case R.id.imgSearchRouteDestinationX:
				etSearchRouteDestination.setText("");
				break;
			case R.id.btnSearchRoute:
				if(etSearchRouteDeparture.getText().toString().equals("")){
					Toast.makeText(getActivity(), "출발지를 입력하세요.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(etSearchRouteDestination.getText().toString().equals("")){
					Toast.makeText(getActivity(), "도착지를 입력하세요.", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(getActivity(), SearchRoute.class);
				intent.putExtra( "searchedRouteDepartureName", searchRouteDepartureName );
				intent.putExtra( "searchedRouteDepartureNum", searchRouteDepartureNum );
				intent.putExtra( "searchedRouteDepartureId", searchRouteDepartureId );
				intent.putExtra( "searchedRouteDestinationName", searchRouteDestinationName );
				intent.putExtra( "searchedRouteDestinationNum", searchRouteDestinationNum );
				intent.putExtra( "searchedRouteDestinationId", searchRouteDestinationId );
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade, R.anim.hold);
				etSearchRouteDeparture.setText("");
				etSearchRouteDestination.setText("");
				
				break;
			}
		}
	};
	
	private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch(rgSearch.getCheckedRadioButtonId()){
			case R.id.rSearchBus:
				rlSearchBus.setVisibility(View.VISIBLE);
				rlSearchBusStop.setVisibility(View.INVISIBLE);
				rlSearchRoute.setVisibility(View.INVISIBLE);
				break;
			case R.id.rSearchBusStop:
				rlSearchBus.setVisibility(View.INVISIBLE);
				rlSearchBusStop.setVisibility(View.VISIBLE);
				rlSearchRoute.setVisibility(View.INVISIBLE);
				break;
			case R.id.rSearchRoute:
				rlSearchBus.setVisibility(View.INVISIBLE);
				rlSearchBusStop.setVisibility(View.INVISIBLE);
				rlSearchRoute.setVisibility(View.VISIBLE);
				break;
			}
		}
	};




}
