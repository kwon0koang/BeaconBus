using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GetDB.Network;

namespace GetDB
{
    public enum PARSER_TYPE
    {
        START_STATION = 0,
        END_STATION = 1,
        SEARCH_BUSNUM = 2,
        SEARCH_BUSLOCATION = 3,
        SEARCH_BUSLOCATION2 = 4,
        SEARCH_STATION = 5,
        SEARCH_STATION2 = 6,
    }

    public class URLInfo
    {
        public StateObject state = null;
        public PARSER_TYPE parserType = PARSER_TYPE.SEARCH_STATION;
        public string bagicPath = "";
        public string searchText = "";
    }
}
