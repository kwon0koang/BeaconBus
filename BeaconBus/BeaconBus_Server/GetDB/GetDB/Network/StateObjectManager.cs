using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Windows.Forms;
using System.Collections;
using System.Timers;
using System.Threading;
using System.Diagnostics;
using System.IO;

namespace GetDB.Network
{
    public class StateObjectManager
    {
        public static List<StateObject> userlist = new List<StateObject>();

        private static StateObjectManager _instance = new StateObjectManager();
        public static StateObjectManager instence
        {
            get
            {
                return _instance;
            }
        }

        private StateObjectManager()
        {
        }

        public List<StateObject> GetList()
        {
            return userlist;
        }

        public void UpdateBusSeatNow(StateObject state)
        {
            userlist[userlist.IndexOf(state)].strBusSeatNow = state.strBusSeatNow;
        }

        public void UpdateBusStopNow(StateObject state)
        {
            userlist[userlist.IndexOf(state)].strBusStopNow = state.strBusStopNow;
        }

        public delegate void UserManagerAddUserIP(string data);
        public static event UserManagerAddUserIP UserManagerUserAddIPData;

        public delegate void UserManagerRemoveUserIP(string data);
        public static event UserManagerRemoveUserIP UserManagerUserRemoveIPData;

        #region userlist Event

        public StateObject GetUserStateObject(int index)
        {
            if (userlist.Count > 0)
                return userlist[index];

            return null;
        }

        public int GetUserCount()
        {
            return userlist.Count;
        }

        public void AddUser(StateObject state)
        {
            try
            {
                userlist.Add(state);
                UserManagerUserAddIPData(state.ipaddress);
            }
            catch (Exception ex)
            {
                MessageBox.Show("AddUser()", ex.ToString());
            }
        }

        public void Remove(StateObject state)
        {
            string ip = state.ipaddress;
            state.timer.Stop();
            userlist.Remove(state);
            UserManagerUserRemoveIPData(ip);
            Console.WriteLine("Disconnect : " + ip);
        }

        public StateObject IPFromSearchUser(string ip)
        {
            return userlist.Find(x => x.ipaddress == ip);
        }

        public StateObject BusNumFromSearchUser(string busID)
        {
            for (int i = 0; i < userlist.Count;i++ )
            {
                if (userlist[i].strBusNumber != null && userlist[i].strBusNumber.IndexOf(busID) != -1)
                {
                    return userlist[i];
                }
            }
            return null;
        }

        public StateObject BusStopNameFromSearchUser(string busStopName)
        {
            for (int i = 0; i < userlist.Count; i++)
            {
                if (userlist[i].strBusStopName != null && userlist[i].strBusStopName.IndexOf(busStopName) != -1)
                {
                    return userlist[i];
                }
            }
            return null;
        }
        #endregion

    }
}
