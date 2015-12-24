using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Collections;
using System.Diagnostics;
using System.Timers;

namespace GetDB.Network
{
    public class StateObject
    {
        public const int HEARTBEAT_DELAY = 10;
        public const int LOCATION_UPDATE_DELAY = 10;
        public DateTime heartbeatTime;
        public DateTime locationupdateTime;
        public Socket workSocket = null;
        public byte[] buffer = new byte[ConstType.TCP_BUFFER_SIZE];
        public string ipaddress = "";
        public ArrayList waypoints = new ArrayList();
        public ArrayList positions = new ArrayList();
        public ArrayList locations = new ArrayList();
        public ArrayList pictures = new ArrayList();
        public bool heartbeatTimeOut = false;
        public System.Timers.Timer timer = new System.Timers.Timer();

        public string strBusID;
        public string strBusNumber;
        public string strBusSeatMax = "0";
        public string strBusSeatNow = "0";
        public string strNextBusStop = "None";

        public string strBusStopID;
        public string strBusStopName;
        public string strBusStopNow = "0";

        public StateObject()
        {
        }

        ~StateObject()
        {
            timer.Stop();
            timer.Close();
            timer = null;
        }

        public void StartState()
        {
            timer.Interval = 3000; // 3초
            timer.Elapsed += new ElapsedEventHandler(timer_Elapsed);
            timer.Start();
            locationupdateTime = DateTime.Now;
        }

        void timer_Elapsed(object sender, ElapsedEventArgs e)
        {
//             TimeSpan gap = (DateTime.Now - heartbeatTime);
//             if ((int)gap.TotalSeconds > HEARTBEAT_DELAY)
//             {
//                 try
//                 {
//                     heartbeatTimeOut = true;
//                     timer.Stop();
//                     UserManager.instence.Remove(this);
//                     workSocket.Shutdown(SocketShutdown.Both);
//                     workSocket.Close();
//                 }
//                 catch (Exception ex)
//                 {
// 
//                 }
//                 return;
//             }

            TimeSpan gap = (DateTime.Now - locationupdateTime);
            if ((int)gap.TotalSeconds > LOCATION_UPDATE_DELAY)
            {
                if (this.strBusID != null)
                {
                    Console.WriteLine("Log Update businfo Number : " + this.strBusNumber);
                    URLInfo Info = APIManager.instence.pathlist[(int)PARSER_TYPE.SEARCH_BUSLOCATION];
                    Info.searchText = this.strBusID;
                    Info.state = this;
                    APIManager.instence.GetWebRequest(Info);
                }
                locationupdateTime = DateTime.Now;
            }
        }
    }
}
