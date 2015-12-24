package com.dgssm.beaconbus.utils;



public class  Constants {

	public static final String ACTION_RESTART_IMMORTALSERVICE = "ACTION_RESTART_IMMORTALSERVICE";
	
	public static final String PROJECT_NUM = "835634768978";
	
	public static final String DB_PATH = "/data/data/com.dgssm.beaconbus/databases/";
	public static final String DB_NAME = "beaconbus.db";
	public static final int DB_VERSION = 1;
	public static final String FAVORITES_DB_NAME = "favorites.db";
	public static final int FAVORITES_DB_VERSION = 1;
	
	public static final int GCM_STATE_GCM = 1;
	public static final int GCM_STATE_REFRESH_DB = 2;
	
	public static final int FOCUS_DEPARTURE = 19191;
	public static final int FOCUS_DESTINATION = 19192;
	
	public static final String SEPARATOR_BUS = "bus";
	public static final String SEPARATOR_BUSSTOP = "busstop";
	
	public static final double MAP_RADIUS = 0.005;
	public static final int MAP_ZOOM_LEVEL = 15;
	
	public static final int BUS_DIRECTION_FORWARD = 1000001;
	public static final int BUS_DIRECTION_BACKWARD = 1000002;
	
	public static final String DEVELOPER_EMAIL = "mailto:kwon0koang@gmail.com";
	
	public static final int NOTIFY_DATASET_CHANGED = 9001;
	public static final int SEARCH_RESULT_IS_0 = 9002;
	public static final int FINISHED_GET_PEOPLE_AND_SEAT = 9003;
	
	public static final String OUTER_SERVER_IP = "210.118.75.114";
	public static final int OUTER_SERVER_PORT = 4389;
	public static final int SERVER_PORT_BUS_OR_BUS_STOP = 4389;
	public static final int SERVER_SEPARATOR_BUS_STOP = 11;
	public static final int SERVER_SEPARATOR_BUS = 22;
	
	public static final int THREAD_RUNNING = 100;
	public static final int THREAD_SUSPENDED = 100;
	public static final int THREAD_STOPPED = 102;
	
	public static final int SOCKET_THREAD_CONNECT_BUS_STOP = 9001;
	public static final int SOCKET_THREAD_DISCONNECT_BUS_STOP = 9002;
	public static final int SOCKET_THREAD_CONNECT_BUS = 9003;
	public static final int SOCKET_THREAD_DISCONNECT_BUS = 9004;
	
	public static final String NOT_EXIST_BUS_NUM = "ABCD";
	
	// SocketThread
	public static final int ARRIVE_DESTINATION = 500;
	public static final int ERROR_DESTINATION = 501;
	public static final int CONNECTED_BUSSTOP = 502;
	
	// 서비스 종료시 재부팅 딜레이 시간, activity의 활성 시간을 범
	public static final int REBOOT_DELAY_TIMER = 10 * 1000;
	
	// 다음 뉴톤톡 API KEY
	public static final String DAUM_NEWTONETALK_API_KEY = "78c3efa818e46e61a77b9665225af4e4";
	
}
