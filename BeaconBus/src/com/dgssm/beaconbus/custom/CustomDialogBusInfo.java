package com.dgssm.beaconbus.custom;

import com.dgssm.beaconbus.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialogBusInfo extends Dialog{
	
	private Context mContext;
	private String bus1People;
	private String bus1Seat;
	private String bus2People;
	private String bus2Seat;

	private LinearLayout llBusInfo1, llBusInfo2;
	private TextView tvBus1People, tvBus1Seat;
	private TextView tvBus2People, tvBus2Seat;
	private Button btnOK; 
	
	public CustomDialogBusInfo(Context context
			, String bus1People, String bus1Seat
			, String bus2People, String bus2Seat){
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog_bus_info);

		this.mContext = context;
		this.bus1People = bus1People;
		this.bus1Seat = bus1Seat;
		this.bus2People = bus2People;
		this.bus2Seat = bus2Seat;
		
		llBusInfo1 = (LinearLayout)findViewById(R.id.llBusInfo1);
		llBusInfo2 = (LinearLayout)findViewById(R.id.llBusInfo2);
		tvBus1People = (TextView)findViewById(R.id.tvBus1People);
		tvBus1Seat = (TextView)findViewById(R.id.tvBus1Seat);
		tvBus2People = (TextView)findViewById(R.id.tvBus2People);
		tvBus2Seat = (TextView)findViewById(R.id.tvBus2Seat);
		btnOK = (Button)findViewById(R.id.btnOK);
		btnOK.setOnClickListener(mClickListener);
		
		tvBus1People.setText(bus1People + " 명");
		tvBus1Seat.setText(bus1Seat + " 석");
		tvBus2People.setText(bus2People + " 명");
		tvBus2Seat.setText(bus2Seat + " 석");
		
		if(bus2People == null){
			llBusInfo2.setVisibility(View.GONE);
		}
		
		
	}
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnOK:
				dismiss();
				break;
			}
		}
	}; 
	
	
}

