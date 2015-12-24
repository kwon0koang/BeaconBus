package com.dgssm.beaconbus.searchedactivity;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.dgssm.beaconbus.FragmentSearch;
import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.CustomAdapterSearchedRoute;
import com.dgssm.beaconbus.custom.DataSearchedRoute;
import com.dgssm.beaconbus.utils.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchRouteDetail extends Activity {

	private TextView tvSearchedRouteDetail;

	private String url;
	private String str = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_route_detail);
		
		// 커스텀 액션 바
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);
		
		tvSearchedRouteDetail = (TextView) findViewById(R.id.tvSearchedRouteDetail);
		Intent i = getIntent();
		url = i.getStringExtra("searchedRouteLink");

		new getSearchedRouteTask().execute(null,null,null);
	}

	
	private class getSearchedRouteTask extends AsyncTask<Void, Void, Void>{

		ProgressDialog asyncDialog = new ProgressDialog(SearchRouteDetail.this);
 
        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다.");
             
            // show dialog
            asyncDialog.show();
            asyncDialog.setCanceledOnTouchOutside(false);
            super.onPreExecute();
        }
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
            	Document doc = Jsoup.connect(url)
    					.timeout(60*1000)
    					.header("Accept-Language", "ko-kr")
    					.get();
    			
    			Elements elementsDesc = doc.select(".desc li");
    			for(int i = 0; i < elementsDesc.size(); i++){
    				System.out.println(elementsDesc.get(i).text());
    				str += elementsDesc.get(i).text() + "\n";
    			}
    			mHandler.sendEmptyMessage(0);
        	}
        	catch (Exception e){
        		e.printStackTrace();
        	}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
	}	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				tvSearchedRouteDetail.setText(str);
				break;
			}
		}
	};

	
	
}
