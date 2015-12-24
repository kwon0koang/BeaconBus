package com.dgssm.beaconbus.outerserver;

public class Setting {
	
	private volatile static Setting instance;
	
	String serverIP;
	int serverPort;
	
	private Setting(){}
	
	public static Setting getInstance()
	{
		if(instance==null)
		{
			synchronized (Setting.class) 
			{
				if(instance==null)
					instance=new Setting();
			}
		}
		return instance;
	}
	
	public void setServerIP(String serverIP)
	{
		this.serverIP=serverIP;
	}
	
	public String getServerIP()
	{
		return serverIP;
	}
	
	public void setServerPort(int serverport)
	{
		this.serverPort=serverport;
	}
	
	public int getServerPort()
	{
		return serverPort;
	}
}
