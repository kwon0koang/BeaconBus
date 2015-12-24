package com.dgssm.kingsmem.etc;

import java.awt.Font;
import javax.swing.ImageIcon;

public class Constants {
	// Callback
	public static final int			SEND_BUSSTOP_SETTING_INFO	= 100;
	public static final int			UPDATE_BUSSTOP_SETTING_INFO	= 200;
	public static final int			UPDATE_BUSSTOP_COUNT_DATA	= 201;
	public static final int			UPDATE_BUSSTOP_NAME_DATA	= 202;
	
	// UI
	public static final int			LIMIT_WORD = 10;
	
	// Image	
	public static final	ImageIcon	SETTING_BUTTON_IMG		= new ImageIcon(Constants.class.getClassLoader().getResource("setting.png"));
	public static final	ImageIcon 	QUIT_BUTTON_IMG			= new ImageIcon(Constants.class.getClassLoader().getResource("quit.png"));
	
	// Beacon
	public static final String 		TX = "0B";
	
	// Font
	public static final Font		FONT_SIZE_150			= new Font("굴림", Font.BOLD, 150);
	public static final Font		FONT_SIZE_100			= new Font("굴림", Font.BOLD, 100);
	public static final Font		FONT_SIZE_50			= new Font("굴림", Font.BOLD, 50);
	public static final Font		FONT_SIZE_20			= new Font("굴림", Font.BOLD, 20);
	
	// Network Protocol
	public static final	int			LEN_MAX 				= 10240;
	
	// Network Handler
	public static final String 		SERVER_IP				= "210.118.75.122";
	public static final int 		SERVER_PORT				= 4389;
	
	// Network Inner Server
	public static final String		SEND_ACK				= "1";
}

// End of Constants