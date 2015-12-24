package com.dgssm.beaconbus.custom;

public class DataBus
{
	private int bus;
	private String busNum;
	private String busDirection;
	private String id;
	private int busType;

	public DataBus(int bus, String busNum, String busDirection, String id, int busType)	{
		super();
		this.bus = bus;
		this.busNum = busNum;
		this.busDirection = busDirection;
		this.id = id;
		this.busType = busType;
	}
	
	public int getBusImage()	{
		return bus;
	}
	
	public String getBusNum()	{
		return busNum;
	}
	
	public String getBusDirection()	{
		return busDirection;
	}

	public String getBusId()	{
		return id;
	}

	public int getBusType()	{
		return busType;
	}
}

