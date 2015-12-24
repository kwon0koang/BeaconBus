package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.outerserver.PacketType;
import com.dgssm.beaconbus.searchedactivity.SearchBusStop;
import com.dgssm.beaconbus.utils.Constants;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapterBusStopDisplay extends ArrayAdapter<DataBusStopDisplay>{
	
	private static final String TAG = "CustomAdapterBusStopDisplay";
	
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<DataBusStopDisplay> mList;
	private Filter dataFilter;
	
	
	private LayoutInflater mInflater;
	
	public CustomAdapterBusStopDisplay(Context context, int rowLayoutResource, ArrayList<DataBusStopDisplay> objects)	{
		super(context, rowLayoutResource, objects);
		this.mContext = context;
		this.mLayoutResource = rowLayoutResource;
		this.mList = objects;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public class ViewHolder {
		TextView tvBusNum;
		TextView tvBusRealNum1, tvNowBusStop1, tvTime1, tvBusStopAmount1;
		TextView tvBusRealNum2, tvNowBusStop2, tvTime2, tvBusStopAmount2;
		TextView tvBus1People, tvBus1Seat;
		TextView tvBus2People, tvBus2Seat;
	}
	
	@Override
	public int getCount()	{
		return mList.size();
	}

	@Override
	public DataBusStopDisplay getItem(int position)	{
		return mList.get(position);
	}

	@Override
	public int getPosition(DataBusStopDisplay item)	{
		return mList.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)	{
		final ViewHolder holder;
		
		// 캐시된 뷰 없을 경우, 새로 생성하고 뷰홀더 생성
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.tvBusNum = (TextView)convertView.findViewById(R.id.tvBusNum);
			holder.tvBusRealNum1 = (TextView)convertView.findViewById(R.id.tvBusRealNum1);
			holder.tvNowBusStop1 = (TextView)convertView.findViewById(R.id.tvNowBusStop1);
			holder.tvTime1 = (TextView)convertView.findViewById(R.id.tvTime1);
			holder.tvBusStopAmount1 = (TextView)convertView.findViewById(R.id.tvBusStopAmount1);
			holder.tvBusRealNum2 = (TextView)convertView.findViewById(R.id.tvBusRealNum2);
			holder.tvNowBusStop2 = (TextView)convertView.findViewById(R.id.tvNowBusStop2);
			holder.tvTime2 = (TextView)convertView.findViewById(R.id.tvTime2);
			holder.tvBusStopAmount2 = (TextView)convertView.findViewById(R.id.tvBusStopAmount2);
			holder.tvBus1People = (TextView)convertView.findViewById(R.id.tvBus1People);
			holder.tvBus1Seat = (TextView)convertView.findViewById(R.id.tvBus1Seat);
			holder.tvBus2People = (TextView)convertView.findViewById(R.id.tvBus2People);
			holder.tvBus2Seat = (TextView)convertView.findViewById(R.id.tvBus2Seat);
			
			convertView.setTag(holder);
		} 
		// 캐시된 뷰 있을 경우, 저장된 뷰홀더 사용
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvBusNum.setText(mList.get(position).getBusNum());
		holder.tvBusRealNum1.setText(mList.get(position).getBusRealNum1());
		holder.tvNowBusStop1.setText(mList.get(position).getNowBusStop1());
		holder.tvTime1.setText(mList.get(position).getTime1());
		holder.tvBusStopAmount1.setText(mList.get(position).getBusStopAmount1());
		holder.tvBusRealNum2.setText(mList.get(position).getBusRealNum2());
		holder.tvNowBusStop2.setText(mList.get(position).getNowBusStop2());
		holder.tvTime2.setText(mList.get(position).getTime2());
		holder.tvBusStopAmount2.setText(mList.get(position).getBusStopAmount2());
		holder.tvBus1People.setText(mList.get(position).getPeople1());
		holder.tvBus1Seat.setText(mList.get(position).getSeat1());
		holder.tvBus2People.setText(mList.get(position).getPeople2());
		holder.tvBus2Seat.setText(mList.get(position).getSeat2());
		
		// 도착예정인 버스가 없는 경우
		if(mList.get(position).getBusRealNum1().equals(Constants.NOT_EXIST_BUS_NUM) || mList.get(position).getBusRealNum1() == null){
			convertView.findViewById(R.id.tvBusRealNum1).setVisibility(View.GONE);
			convertView.findViewById(R.id.ivPeople1).setVisibility(View.GONE);
			convertView.findViewById(R.id.tvBus1People).setVisibility(View.GONE);
			convertView.findViewById(R.id.ivSeat1).setVisibility(View.GONE);
			convertView.findViewById(R.id.tvBus1Seat).setVisibility(View.GONE);
		}
		else{
			convertView.findViewById(R.id.tvBusRealNum1).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.ivPeople1).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.tvBus1People).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.ivSeat1).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.tvBus1Seat).setVisibility(View.VISIBLE);
		}
		
		if(mList.get(position).getBusRealNum2().equals(Constants.NOT_EXIST_BUS_NUM) || mList.get(position).getBusRealNum2() == null)
			convertView.findViewById(R.id.rlBus2).setVisibility(View.GONE);
		else
			convertView.findViewById(R.id.rlBus2).setVisibility(View.VISIBLE);
		
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Toast.makeText(mContext, mList.get(pos).getBusNum(), Toast.LENGTH_SHORT).show();
            	Log.e(TAG, mList.get(pos).getBusRealNum1() + "\n" + mList.get(pos).getBusRealNum2());
            	
//            	String busRealNum = mList.get(pos).getBusRealNum1();
//            	// 테스트
//            	//busRealNum = "1111\n1111";
//            	// /테스트
//            	new getNumOfPeopleSocketTask(busRealNum).execute(null,null,null);
            }
        });
        		
		return convertView;
	}

	public static ArrayList<String> busInfoArr = new ArrayList<String>();		// 0 : 버스1 현재 인원,    1 : 버스1 총 좌석,    2 : 버스2 현재 인원,    3 : 버스2 총 좌석
	public static int busCnt = 0;
	public static int ackCnt = 0;
	private class getNumOfPeopleSocketTask extends AsyncTask<Void, Void, Void>{
		String busRealNum = null;
		public getNumOfPeopleSocketTask(String busRealNum) {
			this.busRealNum = busRealNum; 
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// 외부서버 연결 되었으면
				if (SearchBusStop.mNetworkHandler.isConnected()) {
					busInfoArr.clear();
					busRealNum = busRealNum.substring(0, 4);
					if (SearchBusStop.mNetworkHandler.netWorkSend != null) {
						String[] businfo = {busRealNum};
						SearchBusStop.mNetworkHandler.netWorkSend
								.sendMessage(PacketType.SEND_BUSSEAT, businfo);
						Log.e(TAG, busRealNum + " 현재 인원 및 총 좌석 수 요청 완료");
					}						
				}
				// 외부서버 연결 안되었으면
				else{
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	
}