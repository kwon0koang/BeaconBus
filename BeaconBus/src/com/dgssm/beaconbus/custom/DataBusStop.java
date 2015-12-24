package com.dgssm.beaconbus.custom;

public class DataBusStop
{
	private int busStop;
	private String busStopName;
	private String busStopNum;
	private String id;
	private double lat;
	private double lng;
	
	public DataBusStop(int busStop, String busStopName, String busStopNum, String id, double lat, double lng)	{
		super();
		this.busStop = busStop;
		this.busStopName = busStopName;
		this.busStopNum = busStopNum;
		this.id = id;
		this.lat = lat;
		this.lng = lng;
	}
	
	public int getBusStopImage()	{
		return busStop;
	}
	
	public String getBusStopName()	{
		return busStopName;
	}
	
	public String getBusStopNum()	{
		return busStopNum;
	}

	public String getBusStopId()	{
		return id;
	}

	public double getBusStopLat(){
		return lat;
	}
	
	public double getBusStopLng(){
		return lng;
	}
}
