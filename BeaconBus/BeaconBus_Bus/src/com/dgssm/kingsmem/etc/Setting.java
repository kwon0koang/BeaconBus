package com.dgssm.kingsmem.etc;

public class Setting {
	// Setting
	private volatile static Setting	mInstance	= null;
	
	// IP
	private					String	mServerIP	= Constants.SERVER_IP;
	
	// Port
	private					int		mServerPort	= Constants.SERVER_PORT;
	
	private Setting() {
		
	}
	
	/**
	 * Setting Class는 Singleton 으로 구현되어 있음
	 * @return Setting 객체
	 */
	public static Setting getInstance() {
		if(mInstance == null) {
			synchronized (Setting.class) {
				if(mInstance == null) {
					mInstance = new Setting();
				}
			}
		}
		
		return mInstance;
	}
	
	/**
	 * 서버 아이피를 지정한다.
	 * @param serverIP : IPv4 String 주소 ex) "192.168.231.20"
	 */
	public void setServerIP(String serverIP) {
		this.mServerIP = serverIP;
	}
	
	/**
	 * 서버 포트를 지정한다.
	 * @param serverport : 서버 포트 번호
	 */
	public void setServerPort(int serverport) {
		this.mServerPort = serverport;
	}
	
	/**
	 * 서버 IP 주소를 String 형태로 반환한다.
	 * @return
	 */
	public String getServerIP() {
		return this.mServerIP;
	}
	
	/**
	 * 서버 포트 주소를 int type으로 반환한다.
	 * @return
	 */
	public int getServerPort() {
		return this.mServerPort;
	}
}

// End of Setting