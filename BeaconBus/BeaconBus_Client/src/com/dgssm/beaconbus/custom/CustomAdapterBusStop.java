package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.searchedactivity.SearchBusStop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapterBusStop extends ArrayAdapter<DataBusStop>{
	
	private static final String TAG = "CustomAdapterBusStop";
	
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataBusStop> mList, mListSearched;
	private Filter dataFilter;
	
	
	private LayoutInflater mInflater;
	
	public CustomAdapterBusStop(Context context, int rowLayoutResource, ArrayList<DataBusStop> objects)	{
		super(context, rowLayoutResource, objects);
		this.mContext = context;
		this.mLayoutResource = rowLayoutResource;
		this.mList = objects;
		this.mListSearched = objects;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public class ViewHolder {
		ImageView ivBusStop;
		TextView tvBusStopName;
		TextView tvBusStopNum;
	}
	
	@Override
	public int getCount()	{
		return mListSearched.size();
	}

	@Override
	public DataBusStop getItem(int position)	{
		return mListSearched.get(position);
	}

	@Override
	public int getPosition(DataBusStop item)	{
		return mListSearched.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)	{
		final ViewHolder holder;
		
		// ĳ�õ� �� ���� ���, ���� �����ϰ� ��Ȧ�� ����
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.ivBusStop = (ImageView)convertView.findViewById(R.id.ivBusStop);
			holder.tvBusStopName = (TextView)convertView.findViewById(R.id.tvBusStopName);
			holder.tvBusStopNum = (TextView)convertView.findViewById(R.id.tvBusStopNum);
			convertView.setTag(holder);
		} 
		// ĳ�õ� �� ���� ���, ����� ��Ȧ�� ���
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.ivBusStop.setImageResource(mListSearched.get(position).getBusStopImage());
		holder.tvBusStopName.setText(mListSearched.get(position).getBusStopName());
		holder.tvBusStopNum.setText(mListSearched.get(position).getBusStopNum());
				
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				Intent intent = new Intent(mContext, SearchBusStop.class);
				intent.putExtra( "searchedBusStopName", mListSearched.get(pos).getBusStopName() );
				intent.putExtra( "searchedBusStopNum", mListSearched.get(pos).getBusStopNum());
				intent.putExtra( "searchedBusStopId", mListSearched.get(pos).getBusStopId());
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        		
		return convertView;
	}


	
	

	 // �˻� ����
    private class DataFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() == 0){
                results.values = mList;
                results.count = mList.size();
            }
            else {
                ArrayList<DataBusStop> filterDataList = new ArrayList<DataBusStop>();
                filterDataList.clear();
                for(DataBusStop data : mListSearched){
                    if(data.getBusStopName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        filterDataList.add(data);
                }
                results.values = filterDataList;
                results.count = filterDataList.size();
            }
            return results;
        }
 
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count == 0)
                notifyDataSetInvalidated();
            else {
            	mListSearched = (ArrayList<DataBusStop>) results.values;
                notifyDataSetChanged();
//                for(int i=0; i < mListSearched.size(); i++){
//                    Log.d(TAG, "PublishResults [" + i + "] : " + mListSearched.get(i).getBusStopName());
//                }
            }
        }
    }
    
    public void resetData(){
        mListSearched = mList;
    }
 
    @Override
    public Filter getFilter() {
        if(dataFilter == null)
            dataFilter = new DataFilter();
        return dataFilter;
    }
	
	
}