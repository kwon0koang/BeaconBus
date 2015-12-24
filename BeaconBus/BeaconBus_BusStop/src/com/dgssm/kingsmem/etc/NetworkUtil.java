package com.dgssm.kingsmem.etc;

import java.nio.ByteBuffer;

public class NetworkUtil {
	/**
	 * byte 배열을 short 형태로 변환한다.
	 * @param data : byte 배열 데이터
	 * @return short 데이터
	 */
	public static short byteArray2short(byte[] data) {
		return (short) ((data[0] << 8) | (data[1]));
	}

	/**
	 * short 형태를 byte 배열로 변환한다.
	 * @param data : short 데이터
	 * @return byte 배열 데이터
	 */
	public static byte[] short2ByteArray(short data) {
		byte[] bytes = ByteBuffer.allocate(2).putShort(data).array();
		
		return bytes;
	}
}

// End of NetworkUtil