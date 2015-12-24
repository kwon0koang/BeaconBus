package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.db.DbOpenHelper;
import com.dgssm.beaconbus.searchedactivity.SearchBus;
import com.dgssm.beaconbus.searchedactivity.SearchBusStop;
import com.dgssm.beaconbus.utils.Constants;

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

public class CustomAdapterFavorites extends ArrayAdapter<DataFavorites>{
	
	private static final String TAG = "CustomAdapterFavorites";
	
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataFavorites> mList, mListSearched;
	private Filter dataFilter;
	
	
	private LayoutInflater mInflater;
	
	public CustomAdapterFavorites(Context context, int rowLayoutResource, ArrayList<DataFavorites> objects)	{
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
		
		// ĳ�õ� �� ���� ���, ���� �����ϰ� ��Ȧ�� ����
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.ivFavorites = (ImageView)convertView.findViewById(R.id.ivFavorites);
			holder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
			holder.tvSubtitle = (TextView)convertView.findViewById(R.id.tvSubtitle);
			holder.tvExtra = (TextView)convertView.findViewById(R.id.tvExtra);
			convertView.setTag(holder);
		} 
		// ĳ�õ� �� ���� ���, ����� ��Ȧ�� ���
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.ivFavorites.setImageResource(mListSearched.get(position).getFavoritesImage());
		holder.tvTitle.setText(mListSearched.get(position).getTitle());
		holder.tvSubtitle.setText(mListSearched.get(position).getSubtitle());
		
		// ����
		if(mListSearched.get(position).getSeparator().equals(Constants.SEPARATOR_BUS)){
			String busType = DbOpenHelper.getInstance(mContext).getBusType(mListSearched.get(position).getType());
			if(busType == null) busType = "1";
			holder.tvExtra.setText(busType);
			setBusColor(busType, holder);
		}
		// ���� ������
		else {
			holder.tvTitle.setTextColor(Color.BLACK);
			holder.tvExtra.setTextColor(Color.BLACK);
			holder.tvExtra.setText("");
		}
		
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String separator = mListSearched.get(pos).getSeparator();
            	Intent intent = null;
            	
            	// ����
            	if(separator.equals(Constants.SEPARATOR_BUS)){
					intent = new Intent(mContext, SearchBus.class);
					intent.putExtra( "searchedBusNum", mListSearched.get(pos).getTitle() );
					intent.putExtra( "searchedBusDirection", mListSearched.get(pos).getSubtitle());
					intent.putExtra( "searchedBusId", mListSearched.get(pos).getId());
					intent.putExtra( "searchedBusType", mListSearched.get(pos).getType());
            	}
            	// ���� ������
            	else if(separator.equals(Constants.SEPARATOR_BUSSTOP)){
    				intent = new Intent(mContext, SearchBusStop.class);
    				intent.putExtra( "searchedBusStopName", mListSearched.get(pos).getTitle());
    				intent.putExtra( "searchedBusStopNum", mListSearched.get(pos).getSubtitle());
    				intent.putExtra( "searchedBusStopId", mListSearched.get(pos).getId());
    				intent.putExtra( "searchedBusType", mListSearched.get(pos).getType());
            	}
            	
            	mContext.startActivity(intent);
            	((Activity) mContext).overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
		
		return convertView;
	}

	private void setBusColor(String type, ViewHolder holder){
		// ����
		if(type.equals("����")){
			holder.tvTitle.setTextColor(Color.RED);
			holder.tvExtra.setTextColor(Color.RED);
		}
		// ��ȯ
		else if(type.equals("��ȯ")){
			holder.tvTitle.setTextColor(Color.YELLOW);
			holder.tvExtra.setTextColor(Color.YELLOW);
		}
		// ����
		else if(type.equals("����")){
			holder.tvTitle.setTextColor(Color.BLUE);
			holder.tvExtra.setTextColor(Color.BLUE);
		}
		// ����
		else if(type.equals("����")){
			holder.tvTitle.setTextColor(Color.GREEN);
			holder.tvExtra.setTextColor(Color.GREEN);
		}
		// ĥ��
		else if(type.equals("ĥ��")){
			holder.tvTitle.setTextColor(Color.CYAN);
			holder.tvExtra.setTextColor(Color.CYAN);
		}
		// ���
		else if(type.equals("���")){
			holder.tvTitle.setTextColor(Color.MAGENTA);
			holder.tvExtra.setTextColor(Color.MAGENTA);
		}
	}
	
	
//
//	 // �˻� ����
//    private class DataFilter extends Filter {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//            if(constraint == null || constraint.length() == 0){
//                results.values = mList;
//                results.count = mList.size();
//            }
//            else {
//                ArrayList<DataFavorites> filterDataList = new ArrayList<DataFavorites>();
//                filterDataList.clear();
//                for(DataFavorites data : mListSearched){
//                    if(data.getBusNum().toUpperCase().startsWith(constraint.toString().toUpperCase()))
//                        filterDataList.add(data);
//                }
//                results.values = filterDataList;
//                results.count = filterDataList.size();
//            }
//            return results;
//        }
// 
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            if(results.count == 0)
//                notifyDataSetInvalidated();
//            else {
//            	mListSearched = (ArrayList<DataFavorites>) results.values;
//                notifyDataSetChanged();
//                for(int i=0; i < mListSearched.size(); i++){
//                    Log.d(TAG, "PublishResults [" + i + "] : " + mListSearched.get(i).getBusNum());
//                }
//            }
//        }
//    }
//    
//    public void resetData(){
//        mListSearched = mList;
//    }
// 
//    @Override
//    public Filter getFilter() {
//        if(dataFilter == null)
//            dataFilter = new DataFilter();
//        return dataFilter;
//    }
	
	
}