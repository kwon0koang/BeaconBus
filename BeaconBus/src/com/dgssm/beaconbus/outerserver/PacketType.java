package com.dgssm.beaconbus.outerserver;

public interface PacketType {
	short PT_UNDEFINED = 0;
	// HEARTBEAT
	short SEND_HEARTBEAT = 1;
	short REQ_HEARTBEAT = 2;
	
	short SEND_BUSSETTINGINFO = 3;
	short REQ_BUSSETTINGINFO = 4;

	short SEND_BUSSTOPINFO = 5;
	short REQ_BUSSTOPINFO = 6;
	
	short SEND_BUSLOCATIONUPDATE = 7;
	short REQ_BUSLOCATIONUPDATE = 8;
	
	short SEND_BUSSEAT = 9;
	short REQ_BUSSEAT = 10;

	short SEND_BUSSEATUP = 11;
	short REQ_BUSSEATUP = 12;

	short SEND_BUSSEATDOWN = 13;
	short REQ_BUSSEATDOWN = 14;    
}
