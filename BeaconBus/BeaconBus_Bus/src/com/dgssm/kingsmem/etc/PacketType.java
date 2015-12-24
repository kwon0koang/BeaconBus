package com.dgssm.kingsmem.etc;

public interface PacketType {
	// Packet Type
	public static final short	PT_UNDEFINED			= 0;
	public static final short	SEND_BUSSETTINGINFO		= 3;
	public static final short	REQ_BUSSETTINGINFO		= 4;
	public static final short	SEND_BUSSTOPINFO		= 5;
	public static final short	REQ_BUSSTOPINFO			= 6;
	public static final short	SEND_BUSLOCATIONUPDATE	= 7;
	public static final short	REQ_BUSLOCATIONUPDATE	= 8;
	public static final short	SEND_BUSSEAT			= 9;
	public static final short	REQ_BUSSEAT				= 10;
	public static final short	SEND_BUSSEATUP			= 11;
	public static final short	REQ_BUSSEATUP			= 12;
	public static final short	SEND_BUSSEATDOWN		= 13;
	public static final short	REQ_BUSSEATDOWN			= 14;
	public static final short	SEND_BUSSTOPSETTINGINFO	= 15;
	public static final short	REQ_BUSSTOPSETTINGINFO	= 16;
}

// End of PacketType