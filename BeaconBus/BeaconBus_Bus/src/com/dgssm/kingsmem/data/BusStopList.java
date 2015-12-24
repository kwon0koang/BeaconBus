package com.dgssm.kingsmem.data;


public class BusStopList {
	// 버스 정류장 이름
	private String	mBusStopName	= null;
	
	// 내릴 사람 카운트
	private int		mGetDownCount	= 0;
	
	/**
	 * @param name  : 해당 버스가 거처가는 정류장의 이름
	 * @param count : 해당 정류장에 내릴 사람
	 */
	public BusStopList(String name, int count) {
		this.mBusStopName = name;
		this.mGetDownCount = count;
	}
	
	/**
	 * @return 해당 버스가 거쳐가는 정류장 이름
	 */
	public String getBusStopName() {
		return this.mBusStopName;
	}
	
	/**
	 * @return 해당 정류장에 내릴 인원
	 */
	public int getGetDownCount() {
		return this.mGetDownCount;
	}
	
	/**
	 * 내릴 사람 카운트를 업데이트한다.
	 * @param count : 내릴 사람 인원 수
	 */
	public void updateGetDownCount(int count) {
		this.mGetDownCount += count;
	}
}

// End of BusStopList