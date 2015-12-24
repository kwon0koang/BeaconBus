package com.dgssm.beaconbus;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dgssm.beaconbus.custom.CustomAdapterFavorites;
import com.dgssm.beaconbus.db.FavoritesDbOpenHelper;
	

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentFavorites extends Fragment {
	
	private static final String TAG = "FragmentFavorites";
	
	private RadioGroup rgFavorites;
	private RelativeLayout rlFavorites, rlSearchHistory;
	private ListView lvFavorites, lvSearchHistory;
	public static CustomAdapterFavorites customAdapterFavorites, customAdapterSearchHistory;
	private Button btnRemoveSearchHistory;
	
	int sectionNumber;
	public FragmentFavorites(int sectionNumber) {
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
	public static FragmentFavorites newInstance(int sectionNumber) {
		Log.d(TAG, sectionNumber + " / " + TAG);
		FragmentFavorites fragment = new FragmentFavorites(sectionNumber);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

		rgFavorites = (RadioGroup) rootView.findViewById(R.id.rgFavorites);
		rgFavorites.setOnCheckedChangeListener(mOnCheckedChangeListener);
		
		rlFavorites = (RelativeLayout) rootView.findViewById(R.id.rlFavorites);
		rlSearchHistory = (RelativeLayout) rootView.findViewById(R.id.rlSearchHistory);
		rlSearchHistory.setVisibility(View.INVISIBLE);
		lvFavorites = (ListView) rootView.findViewById(R.id.lvFavorites);
		lvSearchHistory = (ListView) rootView.findViewById(R.id.lvSearchHistory);
		customAdapterFavorites = new CustomAdapterFavorites(getActivity(), R.layout.custom_list_view_favorites, Main.favoritesList);
		customAdapterSearchHistory = new CustomAdapterFavorites(getActivity(), R.layout.custom_list_view_favorites, Main.searchHistoryList);
		lvFavorites.setAdapter(customAdapterFavorites);
		lvSearchHistory.setAdapter(customAdapterSearchHistory);

		btnRemoveSearchHistory = (Button) rootView.findViewById(R.id.btnRemoveSearchHistory);
		btnRemoveSearchHistory.setOnClickListener(mOnClickListener);
		
		// Favorites DB ==============
		FavoritesDbOpenHelper.getInstance(getActivity()).getWritableDB();
		//FavoritesDbOpenHelper.getInstance(getActivity()).dropTableFavorites();				// 테이블 삭제, 테스트 목적
		//FavoritesDbOpenHelper.getInstance(getActivity()).dropTableSearchHistory();			// 테이블 삭제, 테스트 목적
		FavoritesDbOpenHelper.getInstance(getActivity()).createTableFavorites();
		FavoritesDbOpenHelper.getInstance(getActivity()).createTableSearchHistory();
		FavoritesDbOpenHelper.getInstance(getActivity()).selectFavorites();
		FavoritesDbOpenHelper.getInstance(getActivity()).selectSearchHistory();
				
		return rootView;
	}
	
	
	
	
	
	private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch(rgFavorites.getCheckedRadioButtonId()){
			case R.id.rFavorites:
				rlFavorites.setVisibility(View.VISIBLE);
				rlSearchHistory.setVisibility(View.INVISIBLE);
				break;
			case R.id.rSearchHistory:
				rlFavorites.setVisibility(View.INVISIBLE);
				rlSearchHistory.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnRemoveSearchHistory:
				//FavoritesDbOpenHelper.getInstance(getActivity()).getWritableDB();
				FavoritesDbOpenHelper.getInstance(getActivity()).dropTableSearchHistory();
				FavoritesDbOpenHelper.getInstance(getActivity()).createTableSearchHistory();
				FavoritesDbOpenHelper.getInstance(getActivity()).selectSearchHistory();
				
				Toast.makeText(getActivity(), "삭제 완료되었습니다.", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
