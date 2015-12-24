package com.dgssm.kingsmem.network;

import java.io.Serializable;

import com.dgssm.kingsmem.etc.Constants;
import com.dgssm.kingsmem.etc.NetworkUtil;
import com.dgssm.kingsmem.etc.PacketType;

public class NetworkProtocol implements Serializable {
	// Serializable ID
	private static final	long	serialVersionUID	= 1L;

	// Packet
	private					byte[]	mPacket				= null;
	private					byte[]	mSendPacket			= null;

	// Protocol
	private					int 	mProtocolHead		= 2;
	private					int 	mProtocolLen		= 2;
	private					int 	mProtocolInfo		= 4;
	private					short	mProtocolType		= PacketType.PT_UNDEFINED;

	public NetworkProtocol() {
		this(PacketType.PT_UNDEFINED);
	}

	public NetworkProtocol(short protocolType) {
		this.mProtocolType = protocolType;
		
		getPacket(this.mProtocolType);
	}
	
	/**
	 * Packet을 셋팅하는 함수
	 * @param type : short 형태의 Packet Type, PacketType.java 참고
	 * @param buf  : byte 배열 형태의 데이터
	 */
	public void setPacket(short type, byte[] buf) {
		this.mPacket = null;
		this.mPacket = getPacket(type);
		this.mProtocolType = type;
		
		System.arraycopy(buf, 0, mPacket, 0, mPacket.length);
	}
	
	/**
	 * 전송할 Packet을 셋팅하는 함수
	 * @param len : int 형태의 Packet 길이
	 */
	public void setSendPacket(int len) {
		mSendPacket = new byte[len];
		
		System.arraycopy(mPacket, 0, mSendPacket, 0, len);
	}
	
	/**
	 * 전송될 Packet 내부 데이터 중 String 배열 형태의 데이터를 전처리하는 과정의 함수
	 * @param datas : String 배열 형태의 데이터
	 */
	public void setString(String[] datas) {
		short dataLen = 2;
		
		for (int i = 0; i < datas.length; i++) {
			short strlen = (short) (datas[i].getBytes().length + 2);
			dataLen += strlen;
		}
		
		byte[] protocolLen = NetworkUtil.short2ByteArray(dataLen);
		System.arraycopy(protocolLen, 0, mPacket, mProtocolHead, protocolLen.length);
		
		byte[] strcount = NetworkUtil.short2ByteArray((short) datas.length);
		System.arraycopy(strcount, 0, mPacket, mProtocolInfo, strcount.length);
		
		dataLen = 2;
		for (int i = 0; i < datas.length; i++) {
			short strlen = (short) datas[i].getBytes().length;
			byte[] bytestrlen = NetworkUtil.short2ByteArray(strlen);

			System.arraycopy(bytestrlen, 0, mPacket, mProtocolInfo + dataLen, bytestrlen.length);
			dataLen += bytestrlen.length;
			
			System.arraycopy(datas[i].getBytes(), 0, mPacket, mProtocolInfo + dataLen, strlen);
			dataLen += strlen;
		}

		setSendPacket(dataLen + mProtocolInfo);
	}

	/**
	 * byte 배열 형태의 Packet을 반환한다.
	 * Packet Type이 PT_UNDEFINED일 경우 호출된다.
	 * @return byte 배열 형태의 Packet 데이터를 반환한다.
	 */
	public byte[] getPacket() {
		return this.mPacket;
	}
	
	/**
	 * byte 배열 형태의 전송할 Packet을 반환한다.
	 * Packet Type이 PT_UNDEFINED일 경우 호출된다.
	 * @return byte 배열 형태의 전송할 Packet 데이터를 반환한다.
	 */
	public byte[] getSendPacket() {
		return this.mSendPacket;
	}
	
	/**
	 * Packet 객체를 할당하여  byte 배열 형태의 Packet 객체를 반환한다.
	 * @param packetType : short 형태의 Packet Type
	 * @return byte 배열 형태의 Packet 데이터를 반환한다.
	 */
	public byte[] getPacket(short packetType) {		
		if (mPacket == null) {
			mPacket = new byte[Constants.LEN_MAX];
		}
		
		byte[] protocolType = NetworkUtil.short2ByteArray(packetType);
		System.arraycopy(protocolType, 0, mPacket, 0, protocolType.length);

		return mPacket;
	}

	/**
	 * byte 배열 형태의 buffer의 패킷 타입을 반환한다.
	 * @param buf : buffer에 담긴 데이터
	 * @return short 형태의 Pack Type을 반환한다.
	 */
	public short getPacketType(byte[] buf) {
		byte[] type = new byte[2];
		
		System.arraycopy(buf, 0, type, 0, mProtocolHead);
		
		return NetworkUtil.byteArray2short(type);
	}

	/**
	 * byte 배열 형태의 buffer의 패킷 길이를 반환한다.
	 * @param buf : buffer에 담긴 데이터
	 * @return 버퍼의 길이를 short 형태로 반환한다.
	 */
	public short getPacketLength(byte[] buf) {
		byte[] len = new byte[2];
		
		System.arraycopy(buf, mProtocolHead, len, 0, mProtocolLen);
		
		return NetworkUtil.byteArray2short(len);
	}		

	/**
	 * byte 배열 형태의 buffer 내용 중 일부를 문자열로 변환하여 반환한다.
	 * @param buf : buffer에 담긴 데이터
	 * @return String 배열 형태로 데이터를 반환한다.
	 */
	public String[] getPacketString(byte[] buf) {
        int strbytepos = 0;
		byte[] bstrCount = new byte[2];
		System.arraycopy(buf, mProtocolInfo + strbytepos, bstrCount, 0, bstrCount.length);
		short strCount = NetworkUtil.byteArray2short(bstrCount);
		strCount = (strCount < 0) ? 200 : strCount;
		String[] reqstr = new String[strCount];
		strbytepos += 2;
		
		for (int i = 0; i < strCount; i++) {
			byte[] bstrLen = new byte[2];
			
			System.arraycopy(buf, mProtocolInfo + strbytepos, bstrLen, 0, bstrLen.length);
			int strLen = NetworkUtil.byteArray2short(bstrLen);
			strbytepos += 2;
			
			if (strLen > 0) {
				byte[] bstr = new byte[strLen];			
				System.arraycopy(buf, mProtocolInfo + strbytepos, bstr, 0, bstr.length);
				strbytepos += strLen;
				reqstr[i] = new String(bstr);
			}
		}
		
		return reqstr;
	}	
}

// End of NetworkProtocol