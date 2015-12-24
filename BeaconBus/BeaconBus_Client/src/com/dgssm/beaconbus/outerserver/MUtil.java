package com.dgssm.beaconbus.outerserver;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MUtil {

	public static double byte2double(byte[] b) {
		return ByteBuffer.wrap(b).getDouble();

	}

	public static double[] byte2doubleArr(byte[] b, int doubleConnt) {
		double[] data = new double[doubleConnt];
		byte[] temp = new byte[8];
		for (int i = 0; i < doubleConnt; i++) {
			System.arraycopy(b, i * 8 + 4, temp, 0, 8);
			data[i] = byte2double(temp);
			Arrays.fill(temp, (byte) 0);
		}

		return data;
	}

	public static byte[] int2Byte(int i) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
		return bytes;
	}

	public static int byte2int(byte[] bytes, int start) {
		int s1 = bytes[start] & 0xFF;
		int s2 = bytes[start + 1] & 0xFF;
		int s3 = bytes[start + 2] & 0xFF;
		int s4 = bytes[start + 3] & 0xFF;
		return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
	}

	public static short byteToShort(byte[] bytes) {

		/*
		 * int newValue = 0; newValue |= (((int)bytes[0])<<8)&0xFF00; newValue
		 * |= (((int)bytes[1]))&0xFF;
		 */
		return ByteBuffer.wrap(bytes).getShort();
	}

	public static short byte2short(byte[] data) {
		return (short) ((data[0] << 8) | (data[1]));
	}

	public static byte[] shortToByteArray(short s) {

		return new byte[] { (byte) ((s & 0xFF00) >> 8), (byte) (s & 0x00FF) };
	}

	public static byte[] shortToByteArray2(short s) {
		byte[] bytes = ByteBuffer.allocate(2).putShort(s).array();
		return bytes;
	}

	public static byte[] doubleToByteArray(double d) {
		byte[] bytes = ByteBuffer.allocate(8).putDouble(d).array();
		return bytes;
	}
}
