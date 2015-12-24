package com.dgssm.kingsmem.etc;

import java.awt.Font;
import javax.swing.ImageIcon;

public interface Constants {
	// Callback
	public static final int 		SEND_BUS_SETTING_INFO	= 100;
	public static final int 		START_BUS_BEACON		= 200;
	public static final int 		UPDATE_BUS_SETTING_INFO = 300;
	public static final int 		UPDATE_BUS_DATA_INFO	= 301;
	public static final int 		UPDATE_BUS_COUNT_DATA	= 302;
	public static final int 		UPDATE_BUS_LOCATION		= 303;
	public static final int			PRESH_BUZZER_BUTTON		= 400;
	public static final int 		GPIO_LED_ON				= 500;
	public static final int 		GPIO_LED_OFF			= 501;
	public static final int			EXCEPTION_NETWORK		= 600;
	
	// UI
	public static final int 		LIMIT_WORD_1			= 10;
	public static final int 		LIMIT_WORD_2			= 4;
	public static final int 		LIMIT_WORD_3			= 2;
	public static final int 		SETTING_LABLE_FONT		= 20;
	
	// Image	
	public static final	ImageIcon	SETTING_BUTTON_IMG		= new ImageIcon(Constants.class.getClassLoader().getResource("setting.png"));
	public static final	ImageIcon 	QUIT_BUTTON_IMG			= new ImageIcon(Constants.class.getClassLoader().getResource("quit.png"));
	public static final	ImageIcon 	NEXT_ON_IMG				= new ImageIcon(Constants.class.getClassLoader().getResource("current_on.png"));
	public static final	ImageIcon 	NEXT_OFF_IMG			= new ImageIcon(Constants.class.getClassLoader().getResource("current_off.png"));
	
	// Font
	public static final Font		FONT_SIZE_100			= new Font("굴림", Font.BOLD, 100);
	public static final Font		FONT_SIZE_50			= new Font("굴림", Font.BOLD, 50);
	
	// Beacon
	public static final String 		TX						= "16";	
	
	// Network Protocol
	public static final	int			LEN_MAX 				= 10240;
	
	// Network Handler
	public static final String 		SERVER_IP				= "210.118.75.114";
	public static final int 		SERVER_PORT				= 4389;
	
	// Network Inner Server
	public static final String		SEND_ACK				= "1";
	public static final String		SEND_GET_DOWN			= "-1";
	public static final String		SEND_ERROR				= "-2";
	public static final String		SEND_ALARM				= "-3";
}

// End of Constants