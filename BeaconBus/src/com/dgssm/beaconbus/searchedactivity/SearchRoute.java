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

public class SearchRoute extends Activity {

	private static final String TAG ="SearchRoute";
	
	private TextView tvSearchedRouteStart, tvSearchedRouteEnd;
	
	private String searchRouteDepartureName, searchRouteDestinationName;
	private String searchRouteDepartureNum, searchRouteDestinationNum;
	private String searchRouteDepartureId, searchRouteDestinationId;
	
	private ArrayList<DataSearchedRoute> searchedRouteDataList;
	private ListView lvSearchedRoute;
	private CustomAdapterSearchedRoute customAdapterSearchedRoute;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_route);
		
		// Ŀ���� �׼� ��
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);
		
		tvSearchedRouteStart = (TextView) findViewById(R.id.tvSearchedRouteStart);
		tvSearchedRouteEnd = (TextView) findViewById(R.id.tvSearchedRouteEnd);
		Intent i = getIntent();
		searchRouteDepartureName = i.getStringExtra("searchedRouteDepartureName");
		searchRouteDepartureNum = i.getStringExtra("searchedRouteDepartureNum");
		searchRouteDepartureId = i.getStringExtra("searchedRouteDepartureId");
		searchRouteDestinationName = i.getStringExtra("searchedRouteDestinationName");
		searchRouteDestinationNum = i.getStringExtra("searchedRouteDestinationNum");
		searchRouteDestinationId = i.getStringExtra("searchedRouteDestinationId");
		
		// �˻��� �ʱ�ȭ
		FragmentSearch.searchRouteDepartureName = "";
		FragmentSearch.searchRouteDepartureNum = "";
		FragmentSearch.searchRouteDepartureId = "";	
		FragmentSearch.searchRouteDestinationName = "";
		FragmentSearch.searchRouteDestinationNum = "";
		FragmentSearch.searchRouteDestinationId = "";
		// /�˻��� �ʱ�ȭ
		
		if(searchRouteDepartureName == null || searchRouteDestinationName == null ||
				searchRouteDepartureName.equals("") || searchRouteDestinationName.equals("")){
			Toast.makeText(getApplicationContext(), "����� Ȥ�� �������� �߸��Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		tvSearchedRouteStart.setText(searchRouteDepartureName);
		tvSearchedRouteEnd.setText(searchRouteDestinationName);
		
		lvSearchedRoute = (ListView) findViewById(R.id.lvSearchedRoute);
		searchedRouteDataList = new ArrayList<DataSearchedRoute>();
		customAdapterSearchedRoute = new CustomAdapterSearchedRoute(this, getApplicationContext(), R.layout.custom_list_view_route, searchedRouteDataList);
		lvSearchedRoute.setAdapter(customAdapterSearchedRoute);

		new getSearchedRouteTask().execute(null,null,null);
	}

	
	private class getSearchedRouteTask extends AsyncTask<Void, Void, Void>{

		ProgressDialog asyncDialog = new ProgressDialog(SearchRoute.this);
 
        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("�ε����Դϴ�.");
             
            // show dialog
            asyncDialog.show();
            asyncDialog.setCanceledOnTouchOutside(false);
            super.onPreExecute();
        }
		
		@Override
		protected Void doInBackground(Void... params) {
			String routeArr[][];		// 0 : ȯ�� ���� / 1 : �� �Ÿ� / 2 : ��ũ 
											// 3 : ��Ʈ1 / 4 : ����1 / 5 : ����1 / 6 : �Ÿ�1
											// 7 : ��Ʈ2 / 8 : ����2 / 9 : ����2 / 10 : �Ÿ�2
			int routeArrIdx = 0;
			routeArr = new String[20][11];
			
			// URL = http://m.businfo.go.kr/bp/m/sdRoute.do?act=search&sBsId=7001006900&sBsNm=0&dBsId=7041017300&dBsNm=0
			
			String departureId = searchRouteDepartureId;
			String destinationId = searchRouteDestinationId;
			String mUrl = "http://m.businfo.go.kr/bp/m/sdRoute.do?act=search&sBsId=" + departureId + "&sBsNm=0&dBsId=" + destinationId + "&dBsNm=0";
			
			try {
				Document doc = Jsoup.connect(mUrl)
						.timeout(60*1000)
						.header("Accept-Language", "ko-kr")
						.get();
				
				Elements elementsAll = doc.select(".dp");
				Elements elementsBusOrBusTrain = elementsAll.select("h3");
				// ���� || ����&����ö
		        for(int i = 0; i < elementsBusOrBusTrain.size(); i++){
		        	System.out.println("========== " + elementsBusOrBusTrain.get(i).text() + " ==========");
		        	Elements elementsTransit = elementsAll.get(i).select(".summ");
		        	Elements elementsDesc = elementsAll.get(i).select(".desc");
		        	int elementsDataIdx = 0;
		        	// ȯ�� || ��ȯ��
		        	for(int j = 0; j < elementsTransit.size(); j++){
		        		String notransitOrTransit = elementsTransit.get(j).select(".tran").text();
		        		String distance = elementsTransit.get(j).select(".dist").text();
		        		String routeLink = "http://m.businfo.go.kr/bp/m/" + elementsTransit.get(j).select(".btn a").attr("href");
		        		System.out.println();
		        		System.out.println(notransitOrTransit + " / " + distance);
		        		System.out.println(routeLink);
		        		// ��ȯ��
		        		if(notransitOrTransit.equals("��ȯ��")){
		        			String route = elementsDesc.get(elementsDataIdx).select(".route").text();
		        			String bsSt = elementsDesc.get(elementsDataIdx).select(".bs_st").text();
		        			String bsEd = elementsDesc.get(elementsDataIdx).select(".bs_ed").text();
		        			String distGap = elementsDesc.get(elementsDataIdx).select(".dist_gap").text();
		        			elementsDataIdx++;
		        			searchedRouteDataList.add(new DataSearchedRoute(notransitOrTransit, distance, routeLink, route, bsSt, bsEd, distGap, null, null, null, null));
		        			routeArrIdx++;
		        		}
		        		// ȯ��
		        		else if(notransitOrTransit.equals("ȯ��")){
	        				String route1 = elementsDesc.get(elementsDataIdx).select(".route").text();
		        			String bsSt1 = elementsDesc.get(elementsDataIdx).select(".bs_st").text();
		        			String bsEd1 = elementsDesc.get(elementsDataIdx).select(".bs_ed").text();
		        			String distGap1 = elementsDesc.get(elementsDataIdx).select(".dist_gap").text();
		        			elementsDataIdx++;
		        			String route2 = elementsDesc.get(elementsDataIdx).select(".route").text();
		        			String bsSt2 = elementsDesc.get(elementsDataIdx).select(".bs_st").text();
		        			String bsEd2 = elementsDesc.get(elementsDataIdx).select(".bs_ed").text();
		        			String distGap2 = elementsDesc.get(elementsDataIdx).select(".dist_gap").text();
		        			Log.e(TAG, route1);
		        			Log.e(TAG, bsSt1);
		        			Log.e(TAG, bsEd1);
		        			Log.e(TAG, distGap1);
		        			Log.e(TAG, route2);
		        			Log.e(TAG, bsSt2);
		        			Log.e(TAG, bsEd2);
		        			Log.e(TAG, distGap2);
		        			elementsDataIdx++;
		        			searchedRouteDataList.add(new DataSearchedRoute(notransitOrTransit, distance, routeLink, route1, bsSt1, bsEd1, distGap1, route2, bsSt2, bsEd2, distGap2));
		        			routeArrIdx++;
		        		}
		        	}
		        }
		        
		        Log.e(TAG, "11111111111111111");
		        
//		        // �˻� ��� ���� ��
//		        if(searchedRouteDataList.size() == 0)
//		        	mHandler.sendEmptyMessage(Constants.SEARCH_RESULT_IS_0);
//		        // �˻� ��� ���� ��
//		        else
//		        	mHandler.sendEmptyMessage(Constants.NOTIFY_DATASET_CHANGED);	
		        
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
            
            // �˻� ��� ���� ��
	        if(searchedRouteDataList.size() == 0)
	        	mHandler.sendEmptyMessage(Constants.SEARCH_RESULT_IS_0);
	        // �˻� ��� ���� ��
	        else
	        	mHandler.sendEmptyMessage(Constants.NOTIFY_DATASET_CHANGED);	
        }
	}	
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case Constants.NOTIFY_DATASET_CHANGED:
				customAdapterSearchedRoute.notifyDataSetChanged();
				break;
			case Constants.SEARCH_RESULT_IS_0:
				Toast.makeText(getApplicationContext(), "�˻� ����� �����ϴ�.", Toast.LENGTH_SHORT).show();
				finish();
				break;
			}
			
		}
	};
	
	
}
