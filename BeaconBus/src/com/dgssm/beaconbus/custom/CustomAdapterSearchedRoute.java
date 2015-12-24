package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.searchedactivity.SearchRouteDetail;

public class CustomAdapterSearchedRoute extends ArrayAdapter<DataSearchedRoute>{
	
	private static final String TAG = "CustomAdapterSearchedRoute";
	
	private Activity mActivity;
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataSearchedRoute> mList;
	private Filter dataFilter;
	
	private LayoutInflater mInflater;
	
	public CustomAdapterSearchedRoute(Activity activity, Context context, int rowLayoutResource, ArrayList<DataSearchedRoute> objects)	{
		super(context, rowLayoutResource, objects);
		this.mActivity = activity;
		this.mContext = context;
		this.mLayoutResource = rowLayoutResource;
		this.mList = objects;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public class ViewHolder {
		TextView tvTransit;
		TextView tvTotalDistance;
		TextView tvRoute1, tvBusStopStart1, tvBusStopEnd1, tvDistanceGap1;
		TextView tvRoute2, tvBusStopStart2, tvBusStopEnd2, tvDistanceGap2;
	}
	
	@Override
	public int getCount()	{
		return mList.size();
	}

	@Override
	public DataSearchedRoute getItem(int position)	{
		return mList.get(position);
	}

	@Override
	public int getPosition(DataSearchedRoute item)	{
		return mList.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)	{
		final ViewHolder holder;
		
		// 캐시된 뷰 없을 경우, 새로 생성하고 뷰홀더 생성
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.tvTransit = (TextView)convertView.findViewById(R.id.tvTransit);
			holder.tvTotalDistance = (TextView)convertView.findViewById(R.id.tvTotalDistance);
			holder.tvRoute1 = (TextView)convertView.findViewById(R.id.tvRoute1);
			holder.tvBusStopStart1 = (TextView)convertView.findViewById(R.id.tvBusStopStart1);
			holder.tvBusStopEnd1 = (TextView)convertView.findViewById(R.id.tvBusStopEnd1);
			holder.tvDistanceGap1 = (TextView)convertView.findViewById(R.id.tvDistanceGap1);
			holder.tvRoute2 = (TextView)convertView.findViewById(R.id.tvRoute2);
			holder.tvBusStopStart2 = (TextView)convertView.findViewById(R.id.tvBusStopStart2);
			holder.tvBusStopEnd2 = (TextView)convertView.findViewById(R.id.tvBusStopEnd2);
			holder.tvDistanceGap2 = (TextView)convertView.findViewById(R.id.tvDistanceGap2);			
			convertView.setTag(holder);
		} 
		// 캐시된 뷰 있을 경우, 저장된 뷰홀더 사용
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvTransit.setText(mList.get(position).getTransit());
		holder.tvTotalDistance.setText(mList.get(position).getTotalDistance());
		holder.tvRoute1.setText(mList.get(position).getRoute1());
		holder.tvBusStopStart1.setText(mList.get(position).getBusStopStart1());
		holder.tvBusStopEnd1.setText(mList.get(position).getBusStopEnd1());
		holder.tvDistanceGap1.setText(mList.get(position).getDistanceGap1());
		holder.tvRoute2.setText(mList.get(position).getRoute2());
		holder.tvBusStopStart2.setText(mList.get(position).getBusStopStart2());
		holder.tvBusStopEnd2.setText(mList.get(position).getBusStopEnd2());
		holder.tvDistanceGap2.setText(mList.get(position).getDistanceGap2());
		
		if(mList.get(position).getTransit().equals("미환승")){
			holder.tvRoute2.setVisibility(View.GONE);
			holder.tvBusStopStart2.setVisibility(View.GONE);
			holder.tvBusStopEnd2.setVisibility(View.GONE);
			holder.tvDistanceGap2.setVisibility(View.GONE);
		}
		else if(mList.get(position).getTransit().equals("환승")){
			holder.tvRoute2.setVisibility(View.VISIBLE);
			holder.tvBusStopStart2.setVisibility(View.VISIBLE);
			holder.tvBusStopEnd2.setVisibility(View.VISIBLE);
			holder.tvDistanceGap2.setVisibility(View.VISIBLE);

			// 첫 하차역에서 내렸는데 환승역이 다를 때
			String busStopEnd1 = mList.get(position).getBusStopEnd1().substring(5);
			String busStopStart2 = mList.get(position).getBusStopStart2().substring(5);
			if(busStopEnd1.equals(busStopStart2) == false){
				holder.tvBusStopEnd1.setTextColor(Color.RED);
				holder.tvBusStopStart2.setTextColor(Color.RED);
			}
			// 첫 하차역과 환승역이 같을 때
			else if(busStopEnd1.equals(busStopStart2) == true){
				holder.tvBusStopEnd1.setTextColor(Color.BLACK);
				holder.tvBusStopStart2.setTextColor(Color.BLACK);
			}
		}
		
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	// 테스트
//            	String busStopEnd1 = mList.get(pos).getBusStopEnd1().substring(5);
//    			String busStopStart2 = mList.get(pos).getBusStopStart2().substring(5);
//            	Toast.makeText(mContext, busStopEnd1 + " / " + busStopStart2, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(mContext, SearchRouteDetail.class);
				intent.putExtra( "searchedRouteLink", mList.get(pos).getLink());
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				mActivity.overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        		
		return convertView;
	}



}