package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.outerserver.PacketType;
import com.dgssm.beaconbus.searchedactivity.SearchBus;
import com.dgssm.beaconbus.utils.Constants;

public class CustomAdapterBusLocation extends ArrayAdapter<DataFavorites>{
	
	private static final String TAG = "CustomAdapterBusLocation";
	
	private Activity mActivity;
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataFavorites> mList, mListSearched;
	private Filter dataFilter;
	
	
	private LayoutInflater mInflater;
	
	public CustomAdapterBusLocation(Activity activity, Context context, int rowLayoutResource, ArrayList<DataFavorites> objects)	{
		super(context, rowLayoutResource, objects);
		this.mContext = context;
		this.mLayoutResource = rowLayoutResource;
		this.mList = objects;
		this.mListSearched = objects;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public class ViewHolder {
		ImageView ivFavorites;
		TextView tvTitle;
		TextView tvSubtitle;
		TextView tvExtra;
	}
	
	@Override
	public int getCount()	{
		return mListSearched.size();
	}

	@Override
	public DataFavorites getItem(int position)	{
		return mListSearched.get(position);
	}

	@Override
	public int getPosition(DataFavorites item)	{
		return mListSearched.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)	{
		final ViewHolder holder;
		
		// 캐시된 뷰 없을 경우, 새로 생성하고 뷰홀더 생성
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.ivFavorites = (ImageView)convertView.findViewById(R.id.ivFavorites);
			holder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
			holder.tvSubtitle = (TextView)convertView.findViewById(R.id.tvSubtitle);
			holder.tvExtra = (TextView)convertView.findViewById(R.id.tvExtra);
			convertView.setTag(holder);
		} 
		// 캐시된 뷰 있을 경우, 저장된 뷰홀더 사용
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.ivFavorites.setImageResource(mListSearched.get(position).getFavoritesImage());
		holder.tvTitle.setText(mListSearched.get(position).getTitle());
		holder.tvSubtitle.setText(mListSearched.get(position).getSubtitle());
		holder.tvExtra.setText(mListSearched.get(position).getId());

		// 버스
		if(mListSearched.get(position).getSeparator().equals(Constants.SEPARATOR_BUS)){
			holder.tvTitle.setTextSize(25);
			holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.theme_main_color));
			holder.tvTitle.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
			holder.tvExtra.setTextColor(mContext.getResources().getColor(R.color.theme_main_color));
		}
		// 버스 정류장
		else {
			holder.tvTitle.setTextSize(15);
			holder.tvTitle.setTextColor(Color.BLACK);
			holder.tvTitle.setPaintFlags(0);
		}
		
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String separator = mListSearched.get(pos).getSeparator();
            	Intent intent = null;
            	
            	// 버스
            	if(separator.equals(Constants.SEPARATOR_BUS)){
            		String tmpTitle = mListSearched.get(pos).getTitle();
            		String busRealNum = tmpTitle.substring(tmpTitle.length() - 4, tmpTitle.length());
            		// 테스트
                	//busRealNum = "1111";
                	// /테스트
            		
            		Handler mHandler = new Handler(Looper.getMainLooper());
            		
            		// 외부서버 연결 되었으면
            		if(SearchBus.mNetworkHandler.isConnected()){
            			new getNumOfPeopleSocketTask(busRealNum).execute(null,null,null);	
            		}
            		else{
            			Toast.makeText(mContext, "외부 서버 연결 X", Toast.LENGTH_SHORT).show();
            		}
            	}
            	// 버스 정류장
            	else if(separator.equals(Constants.SEPARATOR_BUSSTOP)){
//    				intent = new Intent(mContext, SearchBusStop.class);
//    				intent.putExtra( "searchedBusStopName", mListSearched.get(pos).getTitle());
//    				intent.putExtra( "searchedBusStopNum", mListSearched.get(pos).getSubtitle());
//    				intent.putExtra( "searchedBusStopId", mListSearched.get(pos).getId());
//    				mContext.startActivity(intent);
//                	((Activity) mContext).overridePendingTransition(R.anim.fade, R.anim.hold);
            	}
            	
            	
            }
        });
		
		return convertView;
	}

	private class getNumOfPeopleSocketTask extends AsyncTask<Void, Void, Void>{
		String busRealNum = null;
		public getNumOfPeopleSocketTask(String busRealNum) {
			this.busRealNum = busRealNum;
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// 외부서버 연결 되었으면
				if (SearchBus.mNetworkHandler.isConnected()) {
					busRealNum = busRealNum.substring(0, 4);
					if (SearchBus.mNetworkHandler.netWorkSend != null) {
						String[] businfo = {busRealNum};
						SearchBus.mNetworkHandler.netWorkSend
								.sendMessage(PacketType.SEND_BUSSEAT, businfo);
						Log.e(TAG, busRealNum + " 현재 인원 및 총 좌석 수 요청 완료");
					}						
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	
}