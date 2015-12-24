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
import android.widget.ImageView;
import android.widget.TextView;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.db.DbOpenHelper;
import com.dgssm.beaconbus.searchedactivity.SearchBus;
import com.dgssm.beaconbus.utils.Constants;

public class CustomAdapterBus extends ArrayAdapter<DataBus>{
	
	private static final String TAG = "CustomAdapterBus";
	
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataBus> mList, mListSearched;
	private Filter dataFilter;
	
	
	private LayoutInflater mInflater;
	
	public CustomAdapterBus(Context context, int rowLayoutResource, ArrayList<DataBus> objects)	{
		super(context, rowLayoutResource, objects);
		this.mContext = context;
		this.mLayoutResource = rowLayoutResource;
		this.mList = objects;
		this.mListSearched = objects;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public class ViewHolder {
		ImageView ivBus;
		TextView tvBusNum;
		TextView tvBusDirection;
		TextView tvBusType;
	}
	
	@Override
	public int getCount()	{
		return mListSearched.size();
	}

	@Override
	public DataBus getItem(int position)	{
		return mListSearched.get(position);
	}

	@Override
	public int getPosition(DataBus item)	{
		return mListSearched.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)	{
		final ViewHolder holder;
		
		// 캐시된 뷰 없을 경우, 새로 생성하고 뷰홀더 생성
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.ivBus = (ImageView)convertView.findViewById(R.id.ivBus);
			holder.tvBusNum = (TextView)convertView.findViewById(R.id.tvBusNum);
			holder.tvBusDirection = (TextView)convertView.findViewById(R.id.tvBusDirection);
			holder.tvBusType = (TextView)convertView.findViewById(R.id.tvBusType);
			convertView.setTag(holder);
		} 
		// 캐시된 뷰 있을 경우, 저장된 뷰홀더 사용
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.ivBus.setImageResource(mListSearched.get(position).getBusImage());
		holder.tvBusNum.setText(mListSearched.get(position).getBusNum());
		holder.tvBusDirection.setText(mListSearched.get(position).getBusDirection());
		String busType = DbOpenHelper.getInstance(mContext).getBusType(mListSearched.get(position).getBusType());
		holder.tvBusType.setText(busType);
		setBusColor(busType, holder);
		
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              	mListSearched.get(pos).getBusNum();
				Intent intent = new Intent(mContext, SearchBus.class);
				intent.putExtra( "searchedBusNum", mListSearched.get(pos).getBusNum() );
				intent.putExtra( "searchedBusDirection", mListSearched.get(pos).getBusDirection() );
				intent.putExtra( "searchedBusId", mListSearched.get(pos).getBusId() );
				intent.putExtra( "searchedBusType", mListSearched.get(pos).getBusType() );
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
		
		return convertView;
	}

	private void setBusColor(String type, ViewHolder holder){
		// 급행
		if(type.equals("급행")){
			holder.tvBusNum.setTextColor(Color.RED);
			holder.tvBusType.setTextColor(Color.RED);
		}
		// 순환
		else if(type.equals("순환")){
			holder.tvBusNum.setTextColor(Color.YELLOW);
			holder.tvBusType.setTextColor(Color.YELLOW);
		}
		// 간선
		else if(type.equals("간선")){
			holder.tvBusNum.setTextColor(Color.BLUE);
			holder.tvBusType.setTextColor(Color.BLUE);
		}
		// 지선
		else if(type.equals("지선")){
			holder.tvBusNum.setTextColor(Color.GREEN);
			holder.tvBusType.setTextColor(Color.GREEN);
		}
		// 칠곡
		else if(type.equals("칠곡")){
			holder.tvBusNum.setTextColor(Color.CYAN);
			holder.tvBusType.setTextColor(Color.CYAN);
		}
		// 경산
		else if(type.equals("경산")){
			holder.tvBusNum.setTextColor(Color.MAGENTA);
			holder.tvBusType.setTextColor(Color.MAGENTA);
		}
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
                ArrayList<DataBus> filterDataList = new ArrayList<DataBus>();
                filterDataList.clear();
                for(DataBus data : mListSearched){
                    if(data.getBusNum().toUpperCase().startsWith(constraint.toString().toUpperCase()))
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
            	mListSearched = (ArrayList<DataBus>) results.values;
                notifyDataSetChanged();
//                for(int i=0; i < mListSearched.size(); i++){
//                    Log.d(TAG, "PublishResults [" + i + "] : " + mListSearched.get(i).getBusNum());
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