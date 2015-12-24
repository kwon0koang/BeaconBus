package com.dgssm.beaconbus.searchedactivity;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dgssm.beaconbus.Main;
import com.dgssm.beaconbus.R;
import com.dgssm.beaconbus.custom.CustomAdapterBusStopDisplay;
import com.dgssm.beaconbus.custom.DataBusStopDisplay;
import com.dgssm.beaconbus.custom.MarqueeTextView;
import com.dgssm.beaconbus.db.FavoritesDbOpenHelper;
import com.dgssm.beaconbus.outerserver.NetWorkHandler;
import com.dgssm.beaconbus.outerserver.PacketType;
import com.dgssm.beaconbus.outerserver.Setting;
import com.dgssm.beaconbus.utils.Constants;
import com.radiusnetworks.ibeacon.IBeaconManager;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class SearchBusStop extends Activity {

	private static final String TAG = "SearchBusStop";
	
	private MarqueeTextView tvSearchedBusStopName;
	private String searchedBusStopName, searchedBusStopNum, searchedBusStopId;
	private ImageView ivFavoritesBusStop, ivRefresh;
	
	private static ArrayList<DataBusStopDisplay> displayDataList;
	private ListView lvBusStopDisplay;
	private static CustomAdapterBusStopDisplay customAdapterBusStopDisplay;	
	
	public static NetWorkHandler mNetworkHandler;
	public static Setting mSetting;
	
	private static String busArr[][];		// 0 : �뼱 ��ȣ / 1 : ���� ID / 2 : ����(0,1) / 3 : ���� ��ȣ1 / 4 : ���� ������1 / 5 : ���� ������1 /  6 : ���� ���� �ð�1
								// 														   7 : ���� ��ȣ2 / 8 : ���� ������2 / 9 : ���� ������2 / 10 : ���� ���� �ð�2
								//														   11 : �ο�1 / 12 : �¼�1
								//														   13 : �ο�2 / 14 : �¼�2
	private static int busArrIdx = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_busstop);
		
		// Ŀ���� �׼� ��
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		actionBar.setCustomView(mCustomView);
		actionBar.setDisplayShowCustomEnabled(true);
		
		tvSearchedBusStopName = (MarqueeTextView) findViewById(R.id.tvSearchedBusStopName);
		Intent i = getIntent();
		searchedBusStopName = i.getStringExtra("searchedBusStopName");
		searchedBusStopNum = i.getStringExtra("searchedBusStopNum");
		searchedBusStopId = i.getStringExtra("searchedBusStopId");
		tvSearchedBusStopName.setText(searchedBusStopName);
		
		ivFavoritesBusStop = (ImageView) findViewById(R.id.ivFavoritesBusStop);
		ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
		ivFavoritesBusStop.setOnClickListener(mListener);
		ivRefresh.setOnClickListener(mListener);
		
		
		// ��ȸ ��� �߰�
		FavoritesDbOpenHelper.getInstance(SearchBusStop.this).insertSearchHistory(Constants.SEPARATOR_BUSSTOP, searchedBusStopName, searchedBusStopNum, searchedBusStopId, 0);
		
		// ���ã�� ���� ����
		boolean isExist = FavoritesDbOpenHelper.getInstance(SearchBusStop.this).isExist(searchedBusStopName);
		if(isExist){
			ivFavoritesBusStop.setBackgroundResource(R.drawable.star_down);
		} else{
			ivFavoritesBusStop.setBackgroundResource(R.drawable.star);
		}
		
		lvBusStopDisplay = (ListView) findViewById(R.id.lvBusStopDisplay);
		displayDataList = new ArrayList<DataBusStopDisplay>();
		customAdapterBusStopDisplay = new CustomAdapterBusStopDisplay(getApplicationContext(), R.layout.custom_list_view_busstop_display, displayDataList);
		lvBusStopDisplay.setAdapter(customAdapterBusStopDisplay);
		
		new getBusStopDisplayTask().execute(null,null,null);
		
		// �ܺμ��� ����
		mSetting = Setting.getInstance();
		mSetting.setServerIP(Constants.OUTER_SERVER_IP);
		mSetting.setServerPort(Constants.OUTER_SERVER_PORT);
		mNetworkHandler = new NetWorkHandler(SearchBusStop.this);
		mNetworkHandler.start();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Main.activityLiveFlagSearchBusStop = true;
	}
	
	@Override
	protected void onDestroy() {
		// �ܺμ��� ���� ����
		if (mNetworkHandler != null) {
			mNetworkHandler.closeNetWork(false);
			mNetworkHandler = null;
		}
		
		Main.activityLiveFlagSearchBusStop = false;
		
		super.onDestroy();
	}


	
	
	
	
	
	

	private View.OnClickListener mListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case  R.id.ivFavoritesBusStop:
				// ���ã�� ���� ����
				boolean isExist = FavoritesDbOpenHelper.getInstance(SearchBusStop.this).isExist(searchedBusStopName);
				Log.e(TAG, ""+isExist);
				if(isExist){
					Log.e(TAG, "���ã�� ����");
					FavoritesDbOpenHelper.getInstance(SearchBusStop.this).deleteFavorites(searchedBusStopName);
					Toast.makeText(getApplicationContext(), "���ã�� �����Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
					ivFavoritesBusStop.setBackgroundResource(R.drawable.star);
				} else{
					Log.e(TAG, "���ã�� ���");
					FavoritesDbOpenHelper.getInstance(SearchBusStop.this).insertFavorites(Constants.SEPARATOR_BUSSTOP, searchedBusStopName, searchedBusStopNum, searchedBusStopId, 0);
					Toast.makeText(getApplicationContext(), "���ã�� ��ϵǾ����ϴ�.", Toast.LENGTH_SHORT).show();
					ivFavoritesBusStop.setBackgroundResource(R.drawable.star_down);
				}
				
				break;
			case R.id.ivRefresh:
				new getBusStopDisplayTask().execute(null,null,null);
				
				break;
			}
		}
	}; 
	
	
	
	
	
	
	
	
	
	
	
	
	private class getBusStopDisplayTask extends AsyncTask<Void, Void, Void>{

		ProgressDialog asyncDialog = new ProgressDialog(SearchBusStop.this);
 
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
			// http://businfo.daegu.go.kr/ba/route/rtbsarr.do?act=findByPath&bsId=7001009900
			
			displayDataList.clear();
			
			String busStopName = searchedBusStopName;
			String busStopId = searchedBusStopId;
			String mUrl = "http://businfo.daegu.go.kr/ba/route/rtbsarr.do?act=findByPath&bsId="+busStopId;
			
			try {
				Document doc = Jsoup.connect(mUrl).timeout(60*1000).header("Accept-Language", "ko-kr").get();
				Elements lists = doc.select(".body_col2");

				System.out.println();
				System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
				System.out.println("�ڡ� �ش� ������ �������� ���� ID, ���� ���� �ڡ�");
				System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
				System.out.println();
				
				busArr = new String[lists.size()][15];
				busArrIdx = 0;
				
				// �ش� ������ �������� ���� ID, ���� ���
				for(int i = 0; i < lists.size(); i ++){
		        	Element e = lists.get(i);
		        	String busNum = e.text();
		        	String attr = e.attr("onclick");
		        	
		        	int busStopIdStartIdx;
		        	// ������ ���
		        	if(attr.indexOf("treeToggle") == -1){
		        		System.out.println(i + "       " + busNum);
		        		busArr[busArrIdx][0] = busNum;
		        		busStopIdStartIdx = attr.indexOf("('" + busStopId + "'");
		        		System.out.println(i + "       " + attr);
		        		
		        		int startIdxForBusId = attr.indexOf("'" + busStopName + "'") + busStopName.length() + 5;
		        		int finishIdxForBusId = attr.indexOf("'" + busNum + "'") - 3;
		        		String tmpBusId = attr.substring(startIdxForBusId, finishIdxForBusId);
		        		
		        		int idxForMoveDir = attr.indexOf("'" + busNum + "'") + busNum.length() + 5;
		        		char moveDir = attr.charAt(idxForMoveDir);
		        	
		        		System.out.println(i + "       " + tmpBusId + " / " + moveDir);
		        		System.out.println();
		        		busArr[busArrIdx][1] = tmpBusId;
		        		busArr[busArrIdx++][2] = moveDir + "";
		        	}
		        	// ����ִ� ���
		        	else{
		        		String direc = null;
		        		// ���� �������� while ����
		        		while(true){
		        			// IndexOutOfBoundsException ���ϱ� ����
		        			if(i+1 == lists.size()) {
		        				break;
		        			}
		        			
		        			e = lists.get(++i);
			        		direc = e.text();
			        		attr = e.attr("onclick");
			        		
			        		System.out.println(e);
			        		System.out.println(attr);
			        		
			        		// ���� ���� �߰�, while ���� ���� ����
			        		// || �ڿ� �ִ� ������ '300'�� �ƴ϶� '300 ' �̷� ���̾��� ��찡 �߻��ؼ� ���� ó���Ѱ���
			        		if(attr.indexOf("'" + direc + "'") != -1 || attr.indexOf("'" + direc + " '") != -1) {
		        				i--;
		        				break;
		        			}
			        		
			        		direc = direc.substring(2);
				        	System.out.println(i + "       " + busNum + " / " + direc);
				        	busArr[busArrIdx][0] = busNum + " " + direc;
			        		busStopIdStartIdx = attr.indexOf("('" + busStopId + "'");
			        		System.out.println(i + "       " + attr);
			        		
			        		int startIdxForBusId = attr.indexOf("'" + busStopName + "'") + busStopName.length() + 5;
			        		int finishIdxForBusId = attr.indexOf("'" + busNum + "'") - 3;
			        		// �� �κе� '300 ' ���̾��� ��춧���� finishIdxForBusId ���� ó��
			        		if(finishIdxForBusId == -4){
			        			finishIdxForBusId = attr.indexOf("'" + busNum + " '") - 3;
			        		}
			        		String tmpBusId = attr.substring(startIdxForBusId, finishIdxForBusId);
			        		
			        		int idxForMoveDir = attr.indexOf("'" + busNum + "'") + busNum.length() + 5;
			        		// �� �κе� '300 ' ���̾��� ��춧���� finishIdxForBusId ���� ó��
			        		if(attr.charAt(idxForMoveDir) != '0' && attr.charAt(idxForMoveDir) != '1'){
			        			idxForMoveDir = attr.indexOf("'" + busNum + " '") + busNum.length() + 6;
			        		}
			        		char moveDir = attr.charAt(idxForMoveDir);
			        		
			        		System.out.println(i + "       " + tmpBusId + " / " + moveDir);
			        		System.out.println();
			        		busArr[busArrIdx][1] = tmpBusId;
			        		busArr[busArrIdx++][2] = moveDir + "";
		        		}
		        	}
		        }
				
				System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
				System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
				System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
				System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
				
				// ������ ID�� ���� ID, ��������  ���������� ������ �ڷ�� ���� ��
				for(int i = 0; i < busArrIdx; i++){
					String busNum = busArr[i][0];
					String busId = busArr[i][1];
					String moveDir = busArr[i][2];
					
					mUrl = "http://businfo.daegu.go.kr/ba/route/rtbsarr.do?act=findByArr&bsId="+busStopId+"&bsNm=&routeId="+busId+"&routeNo=&moveDir=" + moveDir + "&winc_id=";
					doc = Jsoup.connect(mUrl).timeout(60*1000).header("Accept-Language", "ko-kr").get();
					
					System.out.println("==========================");
					System.out.println(i + " " + busNum);
					
					// �Ϲ����� ���
					lists = doc.select(".body_col4");
					for(int j = 0; j < lists.size(); j++){
						String key = lists.get(j).previousElementSibling().text();
						String value = lists.get(j).text();
						System.out.println(key + "  :  " + value);
						if(key.equals("������ȣ")){	
							//busArr[i][3] = busArr[i][3] == null ? value.substring(0, 4) : busArr[i][3] + "\n" + value.substring(0, 4);		
							if(busArr[i][3] == null)		// 3414(����) �̷� ��찡 �־ �߶�� ��
								busArr[i][3] = value.substring(0, 4);
							else
								busArr[i][7] = value.substring(0, 4);
						}
						if(key.equals("����������")){
							//busArr[i][4] = busArr[i][4] == null ? value : busArr[i][4] + "\n" + value;
							if(busArr[i][4] == null)
								busArr[i][4] = value;
							else
								busArr[i][8] = value;
						}
						if(key.equals("����������")){
							//busArr[i][5] = busArr[i][5] == null ? value : busArr[i][5] + "\n" + value;
							if(busArr[i][5] == null)
								busArr[i][5] = value;
							else
								busArr[i][9] = value;
						}
						if(key.equals("���������ð�")){
							//busArr[i][6] = busArr[i][6] == null ? value : busArr[i][6] + "\n" + value;
							if(busArr[i][6] == null)
								busArr[i][6] = value;
							else
								busArr[i][10] = value;
						}
					}
					// /�Ϲ����� ���
					
					// ��� ��� ���̰ų� ��� ������ ���
					lists = doc.select(".empty_col");
					for(int j = 0; j < lists.size(); j++){
						Element e = lists.get(j);
						String message = e.text();
						System.out.println(message);
						busArr[i][4] = message;
					}
					// /��� ��� ���̰ų� ��� ������ ���
					
					System.out.println();
				}
				// /������ ID�� ���� ID, ��������  ���������� ������ �ڷ�� ���� ��
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
             
            asyncDialog.dismiss();

            // �ܺμ��� ���� �Ǿ�����
			if (mNetworkHandler.isConnected()) {
				new getBusPeopleTask().execute(null,null,null);	
			}
			else{
				for(int i = 0; i < busArrIdx; i++){
	            	// 0 : �뼱 ��ȣ / 1 : ���� ID / 2 : ����(0,1) / 3 : ���� ��ȣ1 / 4 : ���� ������1 / 5 : ���� ������1 /  6 : ���� ���� �ð�1
					// 														   7 : ���� ��ȣ2 / 8 : ���� ������2 / 9 : ���� ������2 / 10 : ���� ���� �ð�2
					//														   11 : �ο�1 / 12 : �¼�1
					//														   13 : �ο�2 / 14 : �¼�2
					displayDataList.add(new DataBusStopDisplay(busArr[i][0], busArr[i][3], busArr[i][4], busArr[i][5], busArr[i][6], busArr[i][7], busArr[i][8], busArr[i][9], busArr[i][10]
																				, "0", "0", "0", "0"));
																				//, busArr[i][11], busArr[i][12], busArr[i][13], busArr[i][14]));
	            }
				customAdapterBusStopDisplay.notifyDataSetChanged();
			}
        }
	}	
	
	private static final int ARR_PEOPLE_AND_SEAT_SIZE = 99;
	public static String arrPepleAndSeat[][] = new String[ARR_PEOPLE_AND_SEAT_SIZE][2];
	public static int arrPepleAndSeatIdx = 0;
	public static int busListSize = 0;
	private class getBusPeopleTask extends AsyncTask<Void, Void, Void>{
		
		ProgressDialog asyncDialog = new ProgressDialog(SearchBusStop.this);
		 
        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("�ο�, �¼� �� �޾ƿ��� ���Դϴ�.");
             
            // show dialog
            asyncDialog.show();
            asyncDialog.setCanceledOnTouchOutside(false);
            super.onPreExecute();
        }
		
		@Override
		protected Void doInBackground(Void... params) {
			// �ο��� üũ �迭 �ʱ�ȭ
			for(int i = 0; i < ARR_PEOPLE_AND_SEAT_SIZE; i++){
				arrPepleAndSeat[i][0] = "0";		// �ο� ��
				arrPepleAndSeat[i][1] = "0";		// �¼� ��
			}
			
			busListSize = busArrIdx;		// ���� ����Ʈ ������ �ʱ�ȭ
			arrPepleAndSeatIdx = 0;		// �ο�, �¼� �� �迭 �ε��� �ʱ�ȭ
			
			for(int i = 0; i < busArrIdx; i++){;
				Log.e(TAG, i + ".  " + busArr[i][3]);
				Log.e(TAG, i + ".  " + busArr[i][7]);
				try {
					// �ܺμ��� ���� �Ǿ�����
					if (mNetworkHandler.isConnected()) {
						if (mNetworkHandler.netWorkSend != null) {
							if(busArr[i][3] == null) busArr[i][3] = Constants.NOT_EXIST_BUS_NUM;
							if(busArr[i][7] == null) busArr[i][7] = Constants.NOT_EXIST_BUS_NUM;
							// ���� 1 ��û
							String[] businfo = {busArr[i][3]};
							mNetworkHandler.netWorkSend
									.sendMessage(PacketType.SEND_BUSSEAT, businfo);
							Thread.sleep(100);
							// ���� 2 ��û
							Log.e(TAG, busArr[i][3] + " ���� �ο� �� �� �¼� �� ��û �Ϸ�");
							String[] businfo2 = {busArr[i][7]};
							mNetworkHandler.netWorkSend
									.sendMessage(PacketType.SEND_BUSSEAT, businfo2);
							Log.e(TAG, busArr[i][7] + " ���� �ο� �� �� �¼� �� ��û �Ϸ�");
							Thread.sleep(100);
						}						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            asyncDialog.dismiss();

        }
	}	
	
	
	
	
	
	
	

	
	public static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case Constants.NOTIFY_DATASET_CHANGED:
				Log.e(TAG, "NOTIFY_DATASET_CHANGED");
				customAdapterBusStopDisplay.notifyDataSetChanged();
				break;
			case Constants.FINISHED_GET_PEOPLE_AND_SEAT:
				Log.e(TAG, "FINISHED_GET_PEOPLE_AND_SEAT");
				int tmpArrPepleAndSeatIdx = 0;
//				for(int i=0; i<busArrIdx*2; i++){
//					Log.e(TAG, arrPepleAndSeat[i][0] + " / " + arrPepleAndSeat[i][1]);
//				}
				for(int i = 0; i < busArrIdx; i++){
	            	// 0 : �뼱 ��ȣ / 1 : ���� ID / 2 : ����(0,1) / 3 : ���� ��ȣ1 / 4 : ���� ������1 / 5 : ���� ������1 /  6 : ���� ���� �ð�1
					// 														   7 : ���� ��ȣ2 / 8 : ���� ������2 / 9 : ���� ������2 / 10 : ���� ���� �ð�2
					//														   11 : �ο�1 / 12 : �¼�1
					//														   13 : �ο�2 / 14 : �¼�2
					displayDataList.add(new DataBusStopDisplay(busArr[i][0], busArr[i][3], busArr[i][4], busArr[i][5], busArr[i][6], busArr[i][7], busArr[i][8], busArr[i][9], busArr[i][10]
																				, arrPepleAndSeat[tmpArrPepleAndSeatIdx][0]
																				, arrPepleAndSeat[tmpArrPepleAndSeatIdx++][1]
																				, arrPepleAndSeat[tmpArrPepleAndSeatIdx][0]
																				, arrPepleAndSeat[tmpArrPepleAndSeatIdx][1]));
																				//, busArr[i][11], busArr[i][12], busArr[i][13], busArr[i][14]));
																				tmpArrPepleAndSeatIdx++;
	            }
	            
				customAdapterBusStopDisplay.notifyDataSetChanged();
				break;
			}
		}
	};
	
}
