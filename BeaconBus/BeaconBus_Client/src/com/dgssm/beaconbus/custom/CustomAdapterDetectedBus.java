package com.dgssm.beaconbus.custom;

import java.util.ArrayList;

import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.beacon.DetectedBusSelectDestination;
import com.dgssm.beaconbus.db.DbOpenHelper;
import com.radiusnetworks.ibeacon.IBeacon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapterDetectedBus extends ArrayAdapter<IBeacon>{
	
	private static final String TAG = "CustomAdapterDetectedBus";
	
	private Activity mActivity;
	private Context mContext;
	private int mLayoutResource;
	private ArrayList<IBeacon> mList;
	
	private LayoutInflater mInflater;
	
	public CustomAdapterDetectedBus(Activity activity, Context context, int rowLayoutResource, ArrayList<IBeacon> objects)	{
		super(context, rowLayoutResource, objects);
		this.mActivity = activity;
		this.mContext = context;
		this.mLayoutResource = rowLayoutResource;
		this.mList = objects;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public class ViewHolder {
		TextView tvBusNum;
		TextView tvBusDirection;
	}
	
	@Override
	public int getCount()	{
		return mList.size();
	}

	@Override
	public IBeacon getItem(int position)	{
		return mList.get(position);
	}

	@Override
	public int getPosition(IBeacon item)	{
		return mList.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)	{
		final ViewHolder holder;
		
		// Ä³½ÃµÈ ºä ¾øÀ» °æ¿ì, »õ·Î »ý¼ºÇÏ°í ºäÈ¦´õ »ý¼º
		if(convertView == null)		{
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder();
			holder.tvBusNum = (TextView) convertView.findViewById(R.id.tvBusNum);
			holder.tvBusDirection = (TextView) convertView.findViewById(R.id.tvBusDirection);
			convertView.setTag(holder);
		} 
		// Ä³½ÃµÈ ºä ÀÖÀ» °æ¿ì, ÀúÀåµÈ ºäÈ¦´õ »ç¿ë
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String tmpBusId = mList.get(position).getProximityUuid();
    	final String busId = tmpBusId.substring(tmpBusId.length() - 10, tmpBusId.length());
    	final int rssi = mList.get(position).getRssi();
    	final int tx = mList.get(position).getTxPower();
    	final int major = mList.get(position).getMajor();
    	final int minor = mList.get(position).getMinor();
    	String ip12 = Integer.toHexString(major);
		String ip34 = Integer.toHexString(minor);
		int ip1 = Integer.parseInt(ip12.substring(0, 2), 16);
		int ip2 = Integer.parseInt(ip12.substring(2), 16);
		int ip3 = Integer.parseInt(ip34.substring(0, 2), 16);
		int ip4 = Integer.parseInt(ip34.substring(2), 16);
		final String ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
		final String[] busRouteAndDirection = DbOpenHelper.getInstance(mContext).getBusNameByUsingId(busId);
		
		if (mList.get(position).getProximityUuid() != null)
			holder.tvBusNum.setText(busRouteAndDirection[0]);
		holder.tvBusDirection.setText(busRouteAndDirection[1]);
		
		// ¹æ¸é ¾øÀ¸¸é
		if(busRouteAndDirection[1].equals("") || busRouteAndDirection[1] == null)
			holder.tvBusDirection.setVisibility(View.INVISIBLE);
			
		final int pos = position;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				Toast.makeText(mContext, busRouteAndDirection[0] + " / " + busId + " / " + tx + " / " + rssi + " / " + major + " / " + minor + " / " + ip, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(mContext, DetectedBusSelectDestination.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("ip", ip);
				mContext.startActivity(intent);
				//((Activity) mContext).overridePendingTransition(R.anim.fade, R.anim.hold);
				mActivity.overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        
		return convertView;
	}

	
}