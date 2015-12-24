package com.dgssm.kingsmem.beacon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.dgssm.kingsmem.data.BusData;
import com.dgssm.kingsmem.etc.Constants;

public class BeaconCommand {
	// Debug
	private static final	String	TAG			= "BeaconCommand";
	
	// Runtime
	private					Runtime	mRuntime	= null;
	
	public BeaconCommand() {
		if (mRuntime == null) {
			mRuntime = Runtime.getRuntime();
		}
	}
	
	/**
	 * Beacon의 모든 셋팅이 끝난 후 실행되는 함수
	 */
	public void startBeacon() {
		String input = createBeaconInput();
		
		// Debugging 용 출력
		System.out.println(TAG + " : startBeacon() -> " + input);
		
		// 리눅스 상의 .sh 파일을 /bin/bash로 실행
		String[] cmd = {"/bin/bash", "-c", "kings_mem/BeaconBus.sh " + input + Constants.TX};
		
		try {
			Process process = mRuntime.exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.err.println(TAG + " : " + e.getMessage());
		}
	}
	
	/** 
	 * Beacon에 들어갈 UUID와 Major, Minor 값을 만들어 냄
	 * @return Beacon UUID 끝 12자리, Major, Minor, TX 값이 합쳐진 String[]
	 */
	private String createBeaconInput() {
		String result = "";
		String[] strs = new String[10];
		int idx = 0;
		
		// UUID 첫 자리는 무조건 00
		strs[idx++] = "00";
		
		// UUID 다음 자리부터 버스 ID가 들어감
		int beginIndex = 0, endIndex = 2;
		for (int i = 0; i < (BusData.ID.length() / 2); i++) {
			strs[idx++] = BusData.ID.substring(beginIndex, endIndex);
			beginIndex = endIndex;
			endIndex = beginIndex + 2;
		}
		
		// IP 정보를 받아옴
		String ip = getIpAddress();
		
		// IP를 Major와 Minor가 들어갈 데이터로 변경함
		int j = 0, len = ip.length();		
		for (int i = 0; i < len; i++) {
			char ch = ip.charAt(i);
			
			if (ch == '.' || (i + 1) == len) {
				int pivot = (((i + 1) == len) ? len : i);
				int number = 0;
				
				for (; j < pivot; j++) {
					number += ((ip.charAt(j) - '0') * Math.pow(10, pivot - j - 1));
				}
				++j;
				
				strs[idx++] = Integer.toHexString(number).toUpperCase();
			}
		}
		
		// 띄어쓰기를 추가함
		for (String s : strs) {
			result += (s + " ");
		}
		
		return result;
	}
	
	/**
	 * 윈도우, 리눅스 시스템에서 Local IP 알아내는 함수
	 * @author http://cranix.net/261 닉스로그
	 * @return "210.118.75.???"
	 */
	private String getIpAddress() {
		String ip = "";
		
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			boolean isLoopBack = true;
		    
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				
				if (ni.isLoopback()) {
					continue;
				}
				
				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				
				while (inetAddresses.hasMoreElements()) {
					InetAddress ia = inetAddresses.nextElement();
					
					if (ia.getHostAddress() != null && ia.getHostAddress().indexOf(".") != -1) {
						ip = ia.getHostAddress();
						isLoopBack = false;
						break;
					}
				}
				
				if (!isLoopBack) {
					break;
				}
			}
		} catch (SocketException se) {
			System.err.println(TAG + " : " + se.getMessage());
		}
		
		return ip;
	}
}

// End of BeaconCommand