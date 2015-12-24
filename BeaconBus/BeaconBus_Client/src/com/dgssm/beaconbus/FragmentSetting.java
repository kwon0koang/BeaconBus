package com.dgssm.beaconbus;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dgssm.beaconbus.db.DbOpenHelper;
import com.dgssm.beaconbus.db.FavoritesDbOpenHelper;
import com.dgssm.beaconbus.utils.Constants;
	

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSetting extends Fragment {
	
	private static final String TAG = "FragmentSetting";
	
	private Button btnUpdateDatabase, btnInquire, btnRemoveSearchHistory;
	private ToggleButton tbAlarmNextBusStop;
	
	int sectionNumber;	
	public FragmentSetting(int sectionNumber) {
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
	public static FragmentSetting newInstance(int sectionNumber) {
		Log.d(TAG, sectionNumber + " / " + TAG);
		FragmentSetting fragment = new FragmentSetting(sectionNumber);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
		
		btnUpdateDatabase = (Button) rootView.findViewById(R.id.btnUpdateDatabase);
		btnInquire = (Button) rootView.findViewById(R.id.btnInquire);
		btnRemoveSearchHistory = (Button) rootView.findViewById(R.id.btnRemoveSearchHistory);
		btnUpdateDatabase.setOnClickListener(mOnClickListener);
		btnInquire.setOnClickListener(mOnClickListener);
		btnRemoveSearchHistory.setOnClickListener(mOnClickListener);
		
		tbAlarmNextBusStop = (ToggleButton) rootView.findViewById(R.id.tbAlarmNextBusStop);
		tbAlarmNextBusStop.setOnClickListener(mOnClickListener);
		
		SharedPreferences prefs = getActivity().getSharedPreferences("AlarmNextBusStop", getActivity().MODE_PRIVATE);
		if(prefs.getBoolean("isAlarmOn", true)){
			tbAlarmNextBusStop.setChecked(true);
		}
		else{
			tbAlarmNextBusStop.setChecked(false);
		}
		
		return rootView;
	}
	
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnUpdateDatabase:
				DbOpenHelper.getInstance(getActivity()).removeDatabase();
				DbOpenHelper.getInstance(getActivity()).createDatabase();
				DbOpenHelper.getInstance(getActivity()).openDatabase();
				
				Toast.makeText(getActivity(), "업데이트 완료되었습니다.", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btnInquire:
				Uri uri = Uri.parse(Constants.DEVELOPER_EMAIL);
				Intent i = new Intent(Intent.ACTION_SENDTO, uri);
				startActivity(i);
				
				//Toast.makeText(getActivity(), "애플리케이션 문의 및 오류 보고", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btnRemoveSearchHistory:
				FavoritesDbOpenHelper.getInstance(getActivity()).getWritableDB();
				FavoritesDbOpenHelper.getInstance(getActivity()).dropTableSearchHistory();
				FavoritesDbOpenHelper.getInstance(getActivity()).createTableSearchHistory();
				FavoritesDbOpenHelper.getInstance(getActivity()).selectSearchHistory();
				
				Toast.makeText(getActivity(), "삭제 완료되었습니다.", Toast.LENGTH_SHORT).show();
				
				break;
			case R.id.tbAlarmNextBusStop:
				SharedPreferences prefs = getActivity().getSharedPreferences("AlarmNextBusStop", getActivity().MODE_PRIVATE);
	            SharedPreferences.Editor ed = prefs.edit();
	            if(tbAlarmNextBusStop.isChecked()){
	                ed.putBoolean("isAlarmOn" , true ); // value : 저장될 값,
	                Toast.makeText(getActivity(), "다음 정류장 알림을 켰습니다.", Toast.LENGTH_SHORT).show();
	            }
	            else{
	                ed.putBoolean("isAlarmOn" , false ); // value : 저장될 값,
	                Toast.makeText(getActivity(), "다음 정류장 알림을 껐습니다.", Toast.LENGTH_SHORT).show();
	            }

	            ed.commit(); // 필수! 이것을 안해주면 저장이 안되요!
				
				break;
			}
		}
	};
	
	
}
