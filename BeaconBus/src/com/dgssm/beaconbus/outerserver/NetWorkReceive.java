package com.dgssm.beaconbus.outerserver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.dgssm.beaconbus.Main;
import com.dgssm.beaconbus.custom.CustomAdapterBusLocation;
import com.dgssm.beaconbus.custom.CustomAdapterBusStopDisplay;
import com.dgssm.beaconbus.custom.CustomDialogBusInfo;
import com.dgssm.beaconbus.searchedactivity.SearchBus;
import com.dgssm.beaconbus.searchedactivity.SearchBusStop;
import com.dgssm.beaconbus.utils.Constants;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class NetWorkReceive extends Thread {

	private static final String TAG = "NetWorkReceive";
	
	private Context mContext = null;
	
	Socket socket;
	InputStream inputStream;
	BufferedInputStream bin;
	boolean running = true;
	int a = 1;
	Protocol protocol;
	byte[] buf;

	public NetWorkReceive(Socket socket, Context context) {
		this.socket = socket;
		this.mContext = context;
		init();
	}

	private void init() {
		try {
			inputStream = socket.getInputStream();
			bin = new BufferedInputStream(inputStream);
			protocol = new Protocol();
		} catch (IOException e) {
			Log.e("intputstream create", "error");
		}
	}

	public void run() {
		while (running) {

			buf = protocol.getPacket();
			try {
				bin.read(buf);
				short protocolType = protocol.getPacketType(buf);
				short protooclLen = protocol.getPacketLength(buf);
				protocol.setPacket(protocolType, protooclLen, buf);
				switch (protocolType) {
				case PacketType.REQ_BUSSETTINGINFO:
				{
					String []strs = protocol.getPacketString(buf);
					for(int i=0;i<strs.length;i++)
						Log.e("REQ_BUSSETTINGINFO", strs[i]);
					break;
				}
				case PacketType.SEND_BUSLOCATIONUPDATE:
				{
					String []strs = protocol.getPacketString(buf);
					for(int i=0;i<strs.length;i++)
						Log.e("SEND_BUSLOCATIONUPDATE", strs[i]);
					break;
				}
				case PacketType.REQ_BUSSEAT:
				{
					// SearchBusStop 액티비티 살아 있으면
					if(Main.activityLiveFlagSearchBusStop == true){
						Log.e(TAG, "SearchBusStop REQ_BUSSEAT");
						final String []strs = protocol.getPacketString(buf);	// 0 : 현재 인원, 1 : 총 좌석
						
						Handler mHandler = new Handler(Looper.getMainLooper());
						
						String people = strs[0];
						String seat = strs[1];
						
						// 검색 결과 없으면
						if(people.equals("false")){
							SearchBusStop.arrPepleAndSeat[SearchBusStop.arrPepleAndSeatIdx][0] = "0";
							SearchBusStop.arrPepleAndSeat[SearchBusStop.arrPepleAndSeatIdx][1] = "0";
							SearchBusStop.arrPepleAndSeatIdx++;
							Log.e(TAG, "busListSize = " + SearchBusStop.busListSize * 2 + " / " + "arrPepleAndSeatIdx = " + SearchBusStop.arrPepleAndSeatIdx + " / " + "people = " + people + " / " + "seat = " + seat);
							
							// 인원수 다 받으면
							if(SearchBusStop.busListSize * 2 == SearchBusStop.arrPepleAndSeatIdx){
								Log.e(TAG, "인원 수 다 받음");
								SearchBusStop.mHandler.sendEmptyMessage(Constants.FINISHED_GET_PEOPLE_AND_SEAT);
							}
//							Log.e(TAG, "해당 버스가 없습니다.");
//							mHandler.post(new Runnable() {
//								@Override
//								public void run() {
//									Toast.makeText(mContext, "해당 버스가 없습니다.", Toast.LENGTH_SHORT).show();								
//								}
//							});
							break;
						}

						SearchBusStop.arrPepleAndSeat[SearchBusStop.arrPepleAndSeatIdx][0] = people;
						SearchBusStop.arrPepleAndSeat[SearchBusStop.arrPepleAndSeatIdx][1] = seat;
						SearchBusStop.arrPepleAndSeatIdx++;
						Log.e(TAG, "busListSize = " + SearchBusStop.busListSize * 2 + " / " + "arrPepleAndSeatIdx = " + SearchBusStop.arrPepleAndSeatIdx + " / " + "people = " + people + " / " + "seat = " + seat);
						
						// 인원수 다 받으면
						if(SearchBusStop.busListSize * 2 == SearchBusStop.arrPepleAndSeatIdx){
							Log.e(TAG, "인원 수 다 받음");
							SearchBusStop.mHandler.sendEmptyMessage(Constants.FINISHED_GET_PEOPLE_AND_SEAT);
						}
						
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								String bus1People = CustomAdapterBusStopDisplay.busInfoArr.get(0);
//								String bus1Seat = CustomAdapterBusStopDisplay.busInfoArr.get(1);
//								
//								//Toast.makeText(mContext, "BUS1 = " + bus1People + "명 / " + bus1Seat + "석", Toast.LENGTH_SHORT).show();
//							
//								// 테스트
//								for(int i = 0; i < CustomAdapterBusStopDisplay.busInfoArr.size(); i++){
//									Log.e(TAG, CustomAdapterBusStopDisplay.busInfoArr.get(i).toString());
//								}
//								
//								CustomDialogBusInfo mDialog = new CustomDialogBusInfo(mContext, bus1People, bus1Seat, null, null);
//								mDialog.show();
//							}
//						});
					}
					// SearchBus 액티비티 살아 있으면
					else if(Main.activityLiveFlagSearchBus == true){
						Log.e(TAG, "SearchBus REQ_BUSSEAT");
						final String []strs = protocol.getPacketString(buf);	// 0 : 현재 인원, 1 : 총 좌석

						Handler mHandler = new Handler(Looper.getMainLooper());
						
						final String people = strs[0];
						final String seat = strs[1];
						
						// 검색 결과 없으면
						if(people.equals("false")){
							Log.e(TAG, "해당 버스가 없습니다.");
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									CustomDialogBusInfo mDialog = new CustomDialogBusInfo(mContext, "0", "0", null, null);
									mDialog.show();
								}
							});
							break;
						}
						
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								CustomDialogBusInfo mDialog = new CustomDialogBusInfo(mContext, people, seat, null, null);
								mDialog.show();
							}
						});
					}
										
					break;
				}
				/*
				 * case PacketType.SET_FRONT: break; case PacketType.SET_BACK:
				 * break;
				 */
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				close();
			}
		}
	}

	public void close() {
		try {
			bin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		running = false;
	}
}
