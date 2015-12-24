package com.dgssm.beaconbus.outerserver;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;

import android.util.Log;

public class NetWorkSend {
	Socket socket;
	OutputStream outputStream;
	BufferedOutputStream bout;
	DataOutputStream dout;
	NetWorkHandler networkHandler;
	
	public NetWorkSend(Socket socket, NetWorkHandler networkHandler)
	{
		this.socket=socket;
		this.networkHandler=networkHandler;
		init();
	}
	
	private void init()
	{
		try
		{
			outputStream=socket.getOutputStream();
			bout=new BufferedOutputStream(outputStream);
			dout=new DataOutputStream(bout);
		}
		catch(IOException e)
		{
			Log.e("network", "inputstream create error");
		}
	}
	
	public void sendMessage(byte[] buf)
	{
		try
		{
			bout.write(buf);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendMessage(short ProtocolType)
	{
		Protocol protocol=new Protocol(ProtocolType);
		protocol.setSendPacket(4);
		
		try {
			bout.write(protocol.getSendPacket());
			bout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		protocol=null;
	}
	
	public void sendMessage(short ProtocolType, double[] Data)
	{
		/*
		if(ProtocolType==PacketType.GPS_DATA)
		{
			Protocol protocol=new Protocol(PacketType.GPS_DATA);
			//protocol.setPosition(Data[0], Data[1], Data[2]);
			
			try {
				bout.write(protocol.getSendPacket());
				bout.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			protocol=null;
		}*/
	}

	public void sendMessage(short ProtocolType, String[] s_data) {
		Protocol protocol = new Protocol(ProtocolType);
		protocol.setString(s_data);

		try {
			bout.write(protocol.getSendPacket());
			bout.flush();
		} catch (IOException e) {
			Log.e("NetWork", "sendError");
			e.printStackTrace();
		}
		protocol=null;
	}
}
