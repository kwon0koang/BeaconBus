//package com.dgssm.beaconbus.parser;
//
//import java.io.IOException;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import com.dgssm.beaconbus.custom.DataBusStopDisplay;
//import com.dgssm.beaconbus.utils.Constants;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.AsyncTask;
//
//public class GetBusStopDisplayTask extends AsyncTask<Void, Void, Void>{
//
//	Context mContext;
//	
//	ProgressDialog asyncDialog;
//
//	public GetBusStopDisplayTask(Context c) {
//		this.mContext = c;
//		asyncDialog = new ProgressDialog(mContext);
//	}
//	
//    @Override
//    protected void onPreExecute() {
//        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        asyncDialog.setMessage("�ε����Դϴ�.");
//         
//        // show dialog
//        asyncDialog.show();
//        asyncDialog.setCanceledOnTouchOutside(false);
//        super.onPreExecute();
//    }
//	
//	@Override
//	protected Void doInBackground(Void... params) {
//		String busArr[][];		// 0 : �뼱 ��ȣ / 1 : ���� ID / 2 : ����(0,1) / 3 : ���� ��ȣ / 4 : ���� ������ / 5 : ���� ������ / 6 : ���� ���� �ð� 
//		int busArrIdx = 0;
//		
//		// http://businfo.daegu.go.kr/ba/route/rtbsarr.do?act=findByPath&bsId=7001009900
//		
//		mContext.displayDataList.clear();
//		
//		String busStopName = mContext.searchedBusStopName;
//		String busStopId = searchedBusStopId;
//		String mUrl = "http://businfo.daegu.go.kr/ba/route/rtbsarr.do?act=findByPath&bsId="+busStopId;
//		
//		try {
//			Document doc = Jsoup.connect(mUrl).timeout(60*1000).header("Accept-Language", "ko-kr").get();
//			Elements lists = doc.select(".body_col2");
//
//			System.out.println();
//			System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
//			System.out.println("�ڡ� �ش� ������ �������� ���� ID, ���� ���� �ڡ�");
//			System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
//			System.out.println();
//			
//			busArr = new String[lists.size()][7];
//			
//			// �ش� ������ �������� ���� ID, ���� ���
//			for(int i = 0; i < lists.size(); i ++){
//	        	Element e = lists.get(i);
//	        	String busNum = e.text();
//	        	String attr = e.attr("onclick");
//	        	
//	        	int busStopIdStartIdx;
//	        	// ������ ���
//	        	if(attr.indexOf("treeToggle") == -1){
//	        		System.out.println(i + "       " + busNum);
//	        		busArr[busArrIdx][0] = busNum;
//	        		busStopIdStartIdx = attr.indexOf("('" + busStopId + "'");
//	        		System.out.println(i + "       " + attr);
//	        		
//	        		int startIdxForBusId = attr.indexOf("'" + busStopName + "'") + busStopName.length() + 5;
//	        		int finishIdxForBusId = attr.indexOf("'" + busNum + "'") - 3;
//	        		String tmpBusId = attr.substring(startIdxForBusId, finishIdxForBusId);
//	        		
//	        		int idxForMoveDir = attr.indexOf("'" + busNum + "'") + busNum.length() + 5;
//	        		char moveDir = attr.charAt(idxForMoveDir);
//	        	
//	        		System.out.println(i + "       " + tmpBusId + " / " + moveDir);
//	        		System.out.println();
//	        		busArr[busArrIdx][1] = tmpBusId;
//	        		busArr[busArrIdx++][2] = moveDir + "";
//	        	}
//	        	// ����ִ� ���
//	        	else{
//	        		String direc = null;
//	        		// ���� �������� while ����
//	        		while(true){
//	        			// IndexOutOfBoundsException ���ϱ� ����
//	        			if(i+1 == lists.size()) {
//	        				break;
//	        			}
//	        			
//	        			e = lists.get(++i);
//		        		direc = e.text();
//		        		attr = e.attr("onclick");
//		        		
//		        		System.out.println(e);
//		        		System.out.println(attr);
//		        		
//		        		// ���� ���� �߰�, while ���� ���� ����
//		        		// || �ڿ� �ִ� ������ '300'�� �ƴ϶� '300 ' �̷� ���̾��� ��찡 �߻��ؼ� ���� ó���Ѱ���
//		        		if(attr.indexOf("'" + direc + "'") != -1 || attr.indexOf("'" + direc + " '") != -1) {
//	        				i--;
//	        				break;
//	        			}
//		        		
//		        		direc = direc.substring(2);
//			        	System.out.println(i + "       " + busNum + " / " + direc);
//			        	busArr[busArrIdx][0] = busNum + " " + direc;
//		        		busStopIdStartIdx = attr.indexOf("('" + busStopId + "'");
//		        		System.out.println(i + "       " + attr);
//		        		
//		        		int startIdxForBusId = attr.indexOf("'" + busStopName + "'") + busStopName.length() + 5;
//		        		int finishIdxForBusId = attr.indexOf("'" + busNum + "'") - 3;
//		        		// �� �κе� '300 ' ���̾��� ��춧���� finishIdxForBusId ���� ó��
//		        		if(finishIdxForBusId == -4){
//		        			finishIdxForBusId = attr.indexOf("'" + busNum + " '") - 3;
//		        		}
//		        		String tmpBusId = attr.substring(startIdxForBusId, finishIdxForBusId);
//		        		
//		        		int idxForMoveDir = attr.indexOf("'" + busNum + "'") + busNum.length() + 5;
//		        		// �� �κе� '300 ' ���̾��� ��춧���� finishIdxForBusId ���� ó��
//		        		if(attr.charAt(idxForMoveDir) != '0' && attr.charAt(idxForMoveDir) != '1'){
//		        			idxForMoveDir = attr.indexOf("'" + busNum + " '") + busNum.length() + 6;
//		        		}
//		        		char moveDir = attr.charAt(idxForMoveDir);
//		        		
//		        		System.out.println(i + "       " + tmpBusId + " / " + moveDir);
//		        		System.out.println();
//		        		busArr[busArrIdx][1] = tmpBusId;
//		        		busArr[busArrIdx++][2] = moveDir + "";
//	        		}
//	        	}
//	        }
//			
//			System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
//			System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
//			System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
//			System.out.println("�ڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡڡ�");
//			
//			// ������ ID�� ���� ID, ��������  ���������� ������ �ڷ�� ���� ��
//			for(int i = 0; i < busArrIdx; i++){
//				String busNum = busArr[i][0];
//				String busId = busArr[i][1];
//				String moveDir = busArr[i][2];
//				
//				mUrl = "http://businfo.daegu.go.kr/ba/route/rtbsarr.do?act=findByArr&bsId="+busStopId+"&bsNm=&routeId="+busId+"&routeNo=&moveDir=" + moveDir + "&winc_id=";
//				doc = Jsoup.connect(mUrl).timeout(60*1000).header("Accept-Language", "ko-kr").get();
//				
//				System.out.println("==========================");
//				System.out.println(i + " " + busNum);
//				
//				// �Ϲ����� ���
//				lists = doc.select(".body_col4");
//				for(int j = 0; j < lists.size(); j++){
//					String key = lists.get(j).previousElementSibling().text();
//					String value = lists.get(j).text();
//					System.out.println(key + "  :  " + value);
//					if(key.equals("������ȣ"))	
//						busArr[i][3] = busArr[i][3] == null ? value.substring(0, 4) : busArr[i][3] + "\n" + value.substring(0, 4);		// 3414(����) �̷� ��찡 �־ �߶�� ��
//					if(key.equals("����������"))
//						busArr[i][4] = busArr[i][4] == null ? value : busArr[i][4] + "\n" + value;
//					if(key.equals("����������"))
//						busArr[i][5] = busArr[i][5] == null ? value : busArr[i][5] + "\n" + value;
//					if(key.equals("���������ð�"))
//						busArr[i][6] = busArr[i][6] == null ? value : busArr[i][6] + "\n" + value;
//				}
//				// /�Ϲ����� ���
//				
//				// ��� ��� ���̰ų� ��� ������ ���
//				lists = doc.select(".empty_col");
//				for(int j = 0; j < lists.size(); j++){
//					Element e = lists.get(j);
//					String message = e.text();
//					System.out.println(message);
//					busArr[i][4] = message;
//				}
//				// /��� ��� ���̰ų� ��� ������ ���
//				
//				displayDataList.add(new DataBusStopDisplay(busArr[i][0], busArr[i][3], busArr[i][4], busArr[i][5], busArr[i][6]));
//				
//				System.out.println();
//			}
//			// /������ ID�� ���� ID, ��������  ���������� ������ �ڷ�� ���� ��
//			
//			mHandler.sendEmptyMessage(Constants.NOTIFY_DATASET_CHANGED);
//			
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
//		return null;
//	}
//	
//	@Override
//    protected void onPostExecute(Void result) {
//        asyncDialog.dismiss();
//        super.onPostExecute(result);
//    }
//}	