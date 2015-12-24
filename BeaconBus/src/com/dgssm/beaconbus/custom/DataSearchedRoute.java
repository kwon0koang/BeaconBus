package com.dgssm.beaconbus.custom;

public class DataSearchedRoute
{
	private String transit;
	private String totalDistance;
	private String link;
	private String route1, busStopStart1, busStopEnd1, distanceGap1;
	private String route2, busStopStart2, busStopEnd2, distanceGap2;

	public DataSearchedRoute(String transit
			, String totalDistance
			, String link
			, String route1, String busStopStart1, String busStopEnd1, String distanceGap1
			, String route2, String busStopStart2, String busStopEnd2, String distanceGap2)	{
		super();
		this.transit = transit;
		this.totalDistance = totalDistance;
		this.link = link;
		this.route1 = route1;
		this.busStopStart1 = busStopStart1;
		this.busStopEnd1 = busStopEnd1;
		this.distanceGap1 = distanceGap1;
		this.route2 = route2;
		this.busStopStart2 = busStopStart2;
		this.busStopEnd2 = busStopEnd2;
		this.distanceGap2 = distanceGap2;
	}

	public String getTransit()	{
		return transit;
	}

	public String getTotalDistance()	{
		return totalDistance;
	}

	public String getLink()	{
		return link;
	}

	public String getRoute1()	{
		return route1;
	}

	public String getBusStopStart1()	{
		return busStopStart1;
	}

	public String getBusStopEnd1()	{
		return busStopEnd1;
	}
	
	public String getDistanceGap1()	{
		return distanceGap1;
	}
	
	public String getRoute2()	{
		return route2;
	}

	public String getBusStopStart2()	{
		return busStopStart2;
	}

	public String getBusStopEnd2()	{
		return busStopEnd2;
	}
	
	public String getDistanceGap2()	{
		return distanceGap2;
	}
}

