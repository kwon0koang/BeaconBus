package com.dgssm.beaconbus.outerserver;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class Protocol implements Serializable {

	public static final short POSITION_LEN = 30;
	public static final int LEN_MAX = 10240;

	byte[] packet;
	byte[] sendPacket;

	short protocolLen;
	short protocolTYPE;

	int lenProtocolHead = 2;
	int lenProtocolLen = 2;
	int lenProtocolnfo = 4;

	public Protocol() {
		this(PacketType.PT_UNDEFINED);
	}

	public Protocol(short protocolType) {
		this.protocolTYPE = protocolType;
		getPacket(protocolType);
	}

	public byte[] getPacket(short packetType) {
		if (packet == null) {
			switch (packetType) {
			case PacketType.PT_UNDEFINED:
				packet = new byte[LEN_MAX];
				break;
			default:
				packet = new byte[10240];
				break;
			}
		}

		byte[] protocolType = MUtil.shortToByteArray2(packetType);
		System.arraycopy(protocolType, 0, packet, 0, protocolType.length);

		return packet;
	}

	public byte[] getPacket() {
		return packet;
	}

	public short getPacketType(byte[] buf) {
		byte[] type = new byte[2];
		System.arraycopy(buf, 0, type, 0, lenProtocolHead);
		return MUtil.byte2short(type);
	}

	public short getPacketLength(byte[] buf) {
		byte[] len = new byte[2];
		System.arraycopy(buf, lenProtocolHead, len, 0, lenProtocolLen);
		return MUtil.byte2short(len);
	}

	public void creatPacket(short type, short len) {

	}

	public void setPacket(short type, short len, byte[] buf) {
		packet = null;
		packet = getPacket(type);
		protocolTYPE = type;
		System.arraycopy(buf, 0, packet, 0, packet.length);
		protocolLen = len;
		// setPacketLen(len,buf);
		// setPacktData(len,buf);

	}

	public double[] getGPSData() {
		int arraySize = protocolLen / 8;
		double[] gps_DestinationPosition;
		gps_DestinationPosition = MUtil.byte2doubleArr(packet, arraySize);

		return gps_DestinationPosition;
	}

	public double[] getDGPSData() {
		double[] dgpsData = MUtil.byte2doubleArr(packet, 2);
		return dgpsData;
	}

	public void setPacketLen(short len, byte[] buf) {
		byte[] alen = MUtil.shortToByteArray2(len);
		System.arraycopy(alen, 0, packet, lenProtocolHead, alen.length);

	}

	public void setPacktData(short len, byte[] buf) {
		System.arraycopy(buf, 0, packet, lenProtocolnfo, len);
	}

	public String[] getPacketString(byte[] buf) {
		
        int strbytepos = 0;
        
		byte[] bstrCount = new byte[2];
		System.arraycopy(buf, lenProtocolnfo + strbytepos, bstrCount, 0, bstrCount.length);
		short strCount = MUtil.byte2short(bstrCount);
        String[] reqstr = new String[strCount];
		strbytepos += 2;
		for(int i=0;i<strCount;i++)
		{
			byte[] bstrLen = new byte[2];
			System.arraycopy(buf, lenProtocolnfo + strbytepos, bstrLen, 0, bstrLen.length);
			short strLen = MUtil.byte2short(bstrLen);
			strbytepos += 2;
			byte[] bstr = new byte[strLen];
			System.arraycopy(buf, lenProtocolnfo + strbytepos, bstr, 0, bstr.length);
			strbytepos += strLen;
			reqstr[i] = new String(bstr);
		}
		
		return reqstr;
	}

	public void setString(String[] s_data) {
		
		short dataLen = 2;
		
		for (int i = 0; i < s_data.length; i++) {
			
			short strlen = (short)(s_data[i].getBytes().length + 2);
			dataLen += strlen;
		}
		byte[] protocolLen = MUtil.shortToByteArray2(dataLen);
		System.arraycopy(protocolLen, 0, packet, lenProtocolHead,
				protocolLen.length);

		byte[] strcount = MUtil.shortToByteArray2((short)s_data.length);
		System.arraycopy(strcount, 0, packet, lenProtocolnfo,
				strcount.length);
		
		dataLen = 2;
		for (int i = 0; i < s_data.length; i++) {
			
			short strlen = (short) s_data[i].getBytes().length;

			byte[] bytestrlen = MUtil.shortToByteArray2(strlen);

			System.arraycopy(bytestrlen, 0, packet, lenProtocolnfo + dataLen,
					bytestrlen.length);

			dataLen += bytestrlen.length;

			System.arraycopy(s_data[i].getBytes(), 0, packet, lenProtocolnfo
					+ dataLen, strlen);
			
			dataLen += strlen;
		}

		setSendPacket(dataLen + lenProtocolnfo);
	}

	public void setPosition(double yaw, double pitch, double rol) {
		Log.e("NetWork", yaw + " " + pitch + " " + rol);

		byte[] arrYaw = MUtil.doubleToByteArray(yaw);
		byte[] arrPitch = MUtil.doubleToByteArray(pitch);
		byte[] arrRol = MUtil.doubleToByteArray(rol);
		short dataLen = (short) (arrYaw.length + arrPitch.length + arrRol.length);
		byte[] protocolLen = MUtil.shortToByteArray2(dataLen);

		System.arraycopy(protocolLen, 0, packet, lenProtocolHead,
				protocolLen.length);

		System.arraycopy(arrYaw, 0, packet, lenProtocolnfo, arrYaw.length);
		System.arraycopy(arrPitch, 0, packet, lenProtocolnfo + arrYaw.length,
				arrPitch.length);
		System.arraycopy(arrRol, 0, packet, lenProtocolnfo + arrYaw.length
				+ arrPitch.length, arrRol.length);

		setSendPacket(dataLen + lenProtocolnfo);
	}

	public void setSendPacket(int len) {
		sendPacket = new byte[len];
		System.arraycopy(packet, 0, sendPacket, 0, len);
	}

	public byte[] getSendPacket() {
		return sendPacket;
	}
}
