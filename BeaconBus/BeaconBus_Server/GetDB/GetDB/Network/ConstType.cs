using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GetDB.Network
{
    public enum PacketType
    {
        // Android PacketType
        SEND_HEARTBEAT = 1,
        REQ_HEARTBEAT = 2,

        SEND_BUSSETTINGINFO = 3,
	    REQ_BUSSETTINGINFO = 4,

        SEND_BUSSTOPINFO = 5,
        REQ_BUSSTOPINFO = 6,

        SEND_BUSLOCATIONUPDATE = 7,
        REQ_BUSLOCATIONUPDATE = 8,
        
        SEND_BUSSEAT = 9,
        REQ_BUSSEAT = 10,

        SEND_BUSSEATUP = 11,
        REQ_BUSSEATUP = 12,

        SEND_BUSSEATDOWN = 13,
        REQ_BUSSEATDOWN = 14,

        SEND_BUSSTOPSETTINGINFO = 15,
        REQ_BUSSTOPSETTINGINFO = 16,

        SEND_BUSSTOP_CNTUP = 19,
        REQ_BUSSTOP_CNTUP = 20,

        SEND_BUSSTOP_CNTDOWN = 21,
        REQ_BUSSTOP_CNTDOWN = 22,
    }

    static class ConstType
    {
        // Network
        public const int TCP_BUFFER_SIZE = 10240;
        public const int UDP_BUFFER_SIZE = 65536;
        public const int PACKET_HEADER_SIZE = 4;

        public const int ASYNC_NET_SERVER_PORT = 4389;
        public const int UDP_SERVER_PORT = 2005;

        // Graph
        public const int MAXIMUM_X = 200;

        // type size
        public const int BYTE_SIZE_DOUBLE = 8;
        public const int BYTE_SIZE_INT = 4;

        // KeyBoard
        public const int SC_CLOSE = 0xF060;
        public const int MF_ENABLED = 0x0;
        public const int MF_GRAYED = 0x1;
        public const int MF_DISABLED = 0x2;

        public const int DELAY_TIME = 1000;
        public const int LOG_VIEW_DELAY_TIME = 100;
    }
}
