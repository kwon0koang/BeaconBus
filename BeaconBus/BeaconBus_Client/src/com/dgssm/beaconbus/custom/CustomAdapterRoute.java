package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgssm.beaconbus.FragmentSearch;
import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.utils.Constants;

public class CustomAdapterRoute extends ArrayAdapter<DataBusStop>{
	
	private static final String TAG = "CustomAdapterRoute";
	
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataBusStop> mList, mListSearched;
	private Filter dataFilter;
	private EditText etSearchRouteDeparture, etSearchRouteDestination;
	
	private LayoutInflater mInflater;
	
	public CustomAdapterRoute(Context context, int rowLayoutResource, ArrayList<DataBusStop> objects, 
											EditText etSearchRouteDeparture, EditText etSearchRouteDestination)	{
		super(context, rowLayoutResource, objects);
		this.mContext = context;
		this.mLayoutResource = rowLayoutResource;
		this.mList = objects;
		this.mListSearched = objects;
		this.etSearchRouteDeparture = etSearchRouteDeparture;
		this.etSearchRouteDestination = etSearchRouteDestination;
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
		
		// 캐시된 뷰 없을 경우, 새로 생성하고 뷰홀더 생성
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.ivBusStop = (ImageView)convertView.findViewById(R.id.ivBusStop);
			holder.tvBusStopName = (TextView)convertView.findViewById(R.id.tvBusStopName);
			holder.tvBusStopNum = (TextView)convertView.findViewById(R.id.tvBusStopNum);
			convertView.setTag(holder);
		} 
		// 캐시된 뷰 있을 경우, 저장된 뷰홀더 사용
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
            	switch(FragmentSearch.nowRouteFocus){
            	case Constants.FOCUS_DEPARTURE:
            		FragmentSearch.searchRouteDepartureName = mListSearched.get(pos).getBusStopName();
            		FragmentSearch.searchRouteDepartureNum = mListSearched.get(pos).getBusStopNum();
            		FragmentSearch.searchRouteDepartureId = mListSearched.get(pos).getBusStopId();
            		etSearchRouteDeparture.setText(FragmentSearch.searchRouteDepartureName);
            		Log.e(TAG, "DEPARTURE =========");
            		Log.e(TAG, FragmentSearch.searchRouteDepartureName + " / " + FragmentSearch.searchRouteDepartureNum + " / " + FragmentSearch.searchRouteDepartureId);
            		break;
            	case Constants.FOCUS_DESTINATION:
            		FragmentSearch.searchRouteDestinationName = mListSearched.get(pos).getBusStopName();
            		FragmentSearch.searchRouteDestinationNum = mListSearched.get(pos).getBusStopNum();
            		FragmentSearch.searchRouteDestinationId = mListSearched.get(pos).getBusStopId();
            		etSearchRouteDestination.setText(FragmentSearch.searchRouteDestinationName);
            		Log.e(TAG, "DESTINATION =========");
            		Log.e(TAG, FragmentSearch.searchRouteDestinationName + " / " + FragmentSearch.searchRouteDestinationNum + " / " + FragmentSearch.searchRouteDestinationId);
            		break;
            	}
            }
        });
        
		return convertView;
	}


	
	

	 // 검색 필터
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