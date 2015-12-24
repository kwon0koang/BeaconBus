package com.dgssm.beaconbus.custom;

public class DataBusStopDisplay
{
	private String busNum;
	private String busRealNum1, nowBusStop1, time1, busStopAmount1, people1, seat1;
	private String busRealNum2, nowBusStop2, time2, busStopAmount2, people2, seat2;

	public DataBusStopDisplay(String busNum
			, String busRealNum1, String nowBusStop1, String busStopAmount1, String time1
			, String busRealNum2, String nowBusStop2, String busStopAmount2, String time2
			, String people1, String seat1
			, String people2, String seat2){
		super();
		this.busNum = busNum;
		this.busRealNum1 = busRealNum1;
		this.nowBusStop1 = nowBusStop1;
		this.busStopAmount1 = busStopAmount1;
		this.time1 = time1;
		this.busRealNum2 = busRealNum2;
		this.nowBusStop2 = nowBusStop2;
		this.busStopAmount2 = busStopAmount2;
		this.time2 = time2;
		this.people1 = people1;
		this.seat1 = seat1;
		this.people2 = people2;
		this.seat2 = seat2;
	}
                 
	public String getBusNum()	{
		return busNum;
	}

	public String getBusRealNum1()	{
		return busRealNum1;
	}

	public String getNowBusStop1()	{
		return nowBusStop1;
	}

	public String getBusStopAmount1()	{
		return busStopAmount1;
	}

	public String getTime1()	{
		return time1;
	}

	public String getBusRealNum2()	{
		return busRealNum2;
	}

	public String getNowBusStop2()	{
		return nowBusStop2;
	}

	public String getBusStopAmount2()	{
		return busStopAmount2;
	}

	public String getTime2()	{
		return time2;
	}

	public String getPeople1()	{
		return people1;
	}
	
	public String getSeat1()	{
		return seat1;
	}
	
	public String getPeople2()	{
		return people2;
	}
	
	public String getSeat2()	{
		return seat2;
	}
}

