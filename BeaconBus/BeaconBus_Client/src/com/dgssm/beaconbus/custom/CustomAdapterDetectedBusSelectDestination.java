package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.beacon.BeaconDetactorService;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapterDetectedBusSelectDestination extends ArrayAdapter<DataFavorites>{
	
	private static final String TAG = "CustomAdapterBusLocation";
	
	private Activity mActivity;
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataFavorites> mList, mListSearched;
	private Filter dataFilter;
	
	
	private LayoutInflater mInflater;
	
	public CustomAdapterDetectedBusSelectDestination(Activity activity, Context context, int rowLayoutResource, ArrayList<DataFavorites> objects)	{
		super(context, rowLayoutResource, objects);
		this.mActivity = activity;
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
		
		holder.tvTitle.setTextColor(Color.BLACK);
		holder.tvSubtitle.setTextColor(Color.BLACK);
		
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Toast.makeText(mContext, mListSearched.get(pos).getTitle() + " 선택", Toast.LENGTH_SHORT).show();
            	BeaconDetactorService.DESTINATION = mListSearched.get(pos).getTitle();
            	BeaconDetactorService.waitFlag = true;
            	mActivity.finish();
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
                ArrayList<DataFavorites> filterDataList = new ArrayList<DataFavorites>();
                filterDataList.clear();
                for(DataFavorites data : mListSearched){
                    if(data.getTitle().toUpperCase().startsWith(constraint.toString().toUpperCase()))
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
            	mListSearched = (ArrayList<DataFavorites>) results.values;
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