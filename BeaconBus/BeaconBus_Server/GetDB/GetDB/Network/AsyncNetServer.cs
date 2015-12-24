using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.IO;
using System.Collections;
using System.Windows.Forms;
using System.Timers;
using System.Data.SQLite;

namespace GetDB.Network
{
    public class AsyncNetServer
    {

        public delegate void AsyncNetStateDatatest(StateObject UIP, byte[] data);
        public static event AsyncNetStateDatatest AsyncNetStateDataReceived;

        public delegate void AsyncNetGPSDatatest(StateObject UIP, byte[] data);
        public static event AsyncNetGPSDatatest AsyncNetGPSDataReceived;

        public static Socket _server;
        public static ManualResetEvent allDone = new ManualResetEvent(false);
        public static bool _isRunning = true;

        public AsyncNetServer()
        {
        }


        static bool IsSocketConnected(Socket s)
        {
            return !((s.Poll(1000, SelectMode.SelectRead) && (s.Available == 0)) || !s.Connected);
        }

        public void StartServer()
        {
            Thread t1 = new Thread(new ThreadStart(RunServer));
            t1.IsBackground = true;
            t1.Start();
        }

        public void RunServer()
        {
            
            try
            {
                IPEndPoint localEP = new IPEndPoint(IPAddress.Any, ConstType.ASYNC_NET_SERVER_PORT);
                //IPEndPoint localEP = new IPEndPoint(IPAddress.Parse("210.118.75.234"), ConstType.ASYNC_NET_SERVER_PORT);

                _server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

                //Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);

                _server.Bind(localEP);

                while (_isRunning)
                {
                    allDone.Reset();
                    _server.Listen(10);
                    _server.BeginAccept(new AsyncCallback(acceptCallback), _server);
                    bool isRequest = allDone.WaitOne(new TimeSpan(2, 0, 0));  // Blocks for 12 hours

                    if (!isRequest)
                    {
                        allDone.Set();
                        // Do some work here every 12 hours
                    }
                }
                ShutdownNetwork();
            }
            catch (Exception e)
            {
                Console.WriteLine("Winsock error: " + e.ToString());
                MessageBox.Show(e.ToString());
            }
        }

        public static void ShutdownNetwork()
        {
            try
            {
                _server.Shutdown(SocketShutdown.Both);
                _server.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("ShutdownNetwork()", ex.ToString());
            }
        }

        private static void acceptCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the socket from the state object.
                Socket server = (Socket)ar.AsyncState;

                if (server != null)
                {
                    Socket handler = server.EndAccept(ar);

                    // Signal main thread to continue
                    allDone.Set();

                    // Create state
                    StateObject state = new StateObject();
                    state.workSocket = handler;
                    state.ipaddress = handler.RemoteEndPoint.ToString();
                    state.heartbeatTime = DateTime.Now;
                    StateObjectManager.instence.AddUser(state);

                    handler.BeginReceive(state.buffer, 0, ConstType.TCP_BUFFER_SIZE,
                        0, new AsyncCallback(ReceiveCallback), state);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("acceptCallback()", ex.ToString());

            }
        }

        // 받는 부분
        private static void ReceiveCallback(IAsyncResult ar)
        {
            try
            {
                byte[] _recvBuffer = new byte[ConstType.TCP_BUFFER_SIZE];
                byte[] _tmpBuffer = new byte[ConstType.TCP_BUFFER_SIZE];
                int _recvBufferIndex = 0;
                int bytesRead = 0;
                StateObject state = (StateObject)ar.AsyncState;

                if (state.heartbeatTimeOut)
                    return;

                Socket handler = state.workSocket;

                if (!IsSocketConnected(handler))
                {
                    StateObjectManager.instence.Remove(state);
                    try
                    {
                        handler.Shutdown(SocketShutdown.Both);
                        handler.Close();
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show("ShutdownNetwork()", ex.ToString());
                    }
                    return;
                }

                bytesRead = handler.EndReceive(ar);

                if (bytesRead > 0)
                {
                    Array.Copy(state.buffer, 0, _recvBuffer, _recvBufferIndex, bytesRead);
                    _recvBufferIndex += bytesRead;

                    if (_recvBufferIndex >= ConstType.PACKET_HEADER_SIZE)
                    {
                        while (true)
                        {
                            byte[] bmsgLen = new byte[2];
                            byte[] bmsgType = new byte[2];
                            Array.Copy(_recvBuffer, 0, bmsgType, 0, 2);
                            Array.Copy(_recvBuffer, 2, bmsgLen, 0, 2);
                            Array.Reverse(bmsgType);
                            Array.Reverse(bmsgLen);
                            ushort _msgType = BitConverter.ToUInt16(bmsgType, 0);
                            ushort _msgLen = BitConverter.ToUInt16(bmsgLen, 0);
                            
                            PacketType msgType = (PacketType)_msgType;

                            if (_recvBufferIndex >= _msgLen + ConstType.PACKET_HEADER_SIZE)
                            {

                                byte[] tData = new byte[_msgLen];
                                Array.Copy(_recvBuffer, ConstType.PACKET_HEADER_SIZE, tData, 0, _msgLen);
                                Console.WriteLine("Log : " + msgType);
                                switch (msgType)
                                {
                                    case PacketType.SEND_HEARTBEAT:
                                        {
                                            state.heartbeatTime = DateTime.Now;
                                            break;
                                        }
                                    case PacketType.SEND_BUSSETTINGINFO:
                                        {
                                            int strbytepos = 0;

                                            byte[] bstrCount = new byte[2];
                                            Array.Copy(tData, strbytepos, bstrCount, 0, 2);
                                            Array.Reverse(bstrCount);
                                            ushort strCount = BitConverter.ToUInt16(bstrCount, 0);
                                            strbytepos += 2;

                                            for (int i = 0; i < strCount;i++ )
                                            {
                                                byte[] bstrLen = new byte[2];
                                                Array.Copy(tData, strbytepos, bstrLen, 0, 2);
                                                Array.Reverse(bstrLen);
                                                ushort strLen = BitConverter.ToUInt16(bstrLen, 0);
                                                strbytepos += 2;

                                                byte[] bstr = new byte[strLen];
                                                Array.Copy(tData, strbytepos, bstr, 0, strLen);
                                                strbytepos += strLen;

                                                string str = Encoding.UTF8.GetString(bstr);
                                                if (i == 0)
                                                    state.strBusID = str;
                                                else if (i == 1)
                                                    state.strBusNumber = str;
                                                else if (i == 2)
                                                    state.strBusSeatMax = str;
                                            }
                                            if (state.strBusID != null)
                                                state.StartState();
                                            SQLiteCommand sqlcmd2 = new SQLiteCommand(SQLiteManager.instence.cnn);
                                            sqlcmd2.CommandText = "select * from DAEGU_Route WHERE routeId1='" + state.strBusID + "'";

                                            try
                                            {
                                                SQLiteDataReader reader = sqlcmd2.ExecuteReader();
                                                int sendDataPos = 0;
                                                while (reader.Read())
                                                {
                                                    string name = reader["routeName"].ToString();
                                                    Console.WriteLine("Log Businfo Nnumber : " + name);
                                                    byte[] pStrCount = BitConverter.GetBytes((ushort)1);

                                                    byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                                    byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);

                                                    byte[] sendData = new byte[StrByte.Length + 2 + 2];

                                                    Array.Reverse(pStrCount);
                                                    Array.Copy(pStrCount, 0, sendData, sendDataPos, pStrCount.Length);
                                                    sendDataPos += 2;
                                                    Array.Reverse(pDataFieldSize);
                                                    Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                                    sendDataPos += 2;
                                                    Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                                    sendDataPos += StrByte.Length;

                                                    Send(sendData, PacketType.REQ_BUSSETTINGINFO, state);

                                                    URLInfo Info = APIManager.instence.pathlist[(int)PARSER_TYPE.SEARCH_BUSLOCATION2];
                                                    Info.searchText = state.strBusID;
                                                    Info.state = state;
                                                    APIManager.instence.GetWebRequest(Info);

                                                    break;
                                                }
                                                reader.Close();
                                            }
                                            catch (Exception ex)
                                            {
                                                MessageBox.Show("SQLiteDataReader()", ex.ToString());
                                            }
                                            break;
                                        }
                                        /*
                                    case PacketType.GPS_DATA:
                                        {
                                            //AsyncNetGPSDataReceived(state, tData);
                                            break;
                                        }
                                        */
                                    case PacketType.SEND_BUSSTOPINFO:
                                        {
                                            int strbytepos = 0;

                                            byte[] bstrCount = new byte[2];
                                            Array.Copy(tData, strbytepos, bstrCount, 0, 2);
                                            Array.Reverse(bstrCount);
                                            ushort strCount = BitConverter.ToUInt16(bstrCount, 0);
                                            strbytepos += 2;

                                            byte[] bstrLen = new byte[2];
                                            Array.Copy(tData, strbytepos, bstrLen, 0, 2);
                                            Array.Reverse(bstrLen);
                                            ushort strLen = BitConverter.ToUInt16(bstrLen, 0);
                                            strbytepos += 2;

                                            byte[] bstr = new byte[strLen];
                                            Array.Copy(tData, strbytepos, bstr, 0, strLen);
                                            strbytepos += strLen;

                                            string str = Encoding.UTF8.GetString(bstr);

                                            SQLiteCommand sqlcmd2 = new SQLiteCommand(SQLiteManager.instence.cnn);
                                            sqlcmd2.CommandText = "select * from DAEGU_Stop WHERE stopApiId='" + str + "'";

                                            try
                                            {
                                                SQLiteDataReader reader = sqlcmd2.ExecuteReader();
                                                int sendDataPos = 0;
                                                while (reader.Read())
                                                {
                                                    string name = reader["stopName"].ToString();

                                                    byte[] pStrCount = BitConverter.GetBytes((ushort)1);

                                                    byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                                    byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);

                                                    byte[] sendData = new byte[StrByte.Length + 2 + 2];

                                                    Array.Reverse(pStrCount);
                                                    Array.Copy(pStrCount, 0, sendData, sendDataPos, pStrCount.Length);
                                                    sendDataPos += 2;
                                                    Array.Reverse(pDataFieldSize);
                                                    Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                                    sendDataPos += 2;
                                                    Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                                    sendDataPos += StrByte.Length;

                                                    Send(sendData, PacketType.REQ_BUSSTOPINFO, state);

                                                    break;
                                                }
                                                reader.Close();
                                            }
                                            catch (Exception ex)
                                            {
                                                MessageBox.Show("SEND_BUSSTOPINFO()", ex.ToString());
                                            }
                                            break;
                                        }
                                    case PacketType.SEND_BUSSEAT:
                                        {
                                            int strbytepos = 0;

                                            byte[] bstrCount = new byte[2];
                                            Array.Copy(tData, strbytepos, bstrCount, 0, 2);
                                            Array.Reverse(bstrCount);
                                            ushort strCount = BitConverter.ToUInt16(bstrCount, 0);
                                            strbytepos += 2;
                                            byte[] bstrLen = new byte[2];
                                            Array.Copy(tData, strbytepos, bstrLen, 0, 2);
                                            Array.Reverse(bstrLen);
                                            ushort strLen = BitConverter.ToUInt16(bstrLen, 0);
                                            strbytepos += 2;

                                            byte[] bstr = new byte[strLen];
                                            Array.Copy(tData, strbytepos, bstr, 0, strLen);
                                            strbytepos += strLen;

                                            string str = Encoding.UTF8.GetString(bstr);

                                            StateObject tempstate = StateObjectManager.instence.BusNumFromSearchUser(str);

                                            if(tempstate != null )
                                            {
                                                // Send
                                                int sendDataPos = 0;
                                                string name = tempstate.strBusSeatNow;
                                                string name2 = tempstate.strBusSeatMax;

                                                byte[] pStrCount = BitConverter.GetBytes((ushort)2);

                                                byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                                byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);

                                                byte[] StrByte2 = Encoding.UTF8.GetBytes(name2);
                                                byte[] pDataFieldSize2 = BitConverter.GetBytes((ushort)StrByte2.Length);

                                                byte[] sendData = new byte[StrByte.Length + StrByte2.Length + 2 + 2 + 2];

                                                Array.Reverse(pStrCount);
                                                Array.Copy(pStrCount, 0, sendData, sendDataPos, pStrCount.Length);
                                                sendDataPos += 2;
                                                Array.Reverse(pDataFieldSize);
                                                Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                                sendDataPos += 2;
                                                Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                                sendDataPos += StrByte.Length;

                                                Array.Reverse(pDataFieldSize2);
                                                Array.Copy(pDataFieldSize2, 0, sendData, sendDataPos, pDataFieldSize2.Length);
                                                sendDataPos += 2;
                                                Array.Copy(StrByte2, 0, sendData, sendDataPos, StrByte2.Length);
                                                sendDataPos += StrByte2.Length;

                                                Send(sendData, PacketType.REQ_BUSSEAT, state);
                                            }
                                            else
                                            {
                                                // Send
                                                int sendDataPos = 0;
                                                string name = "false";
                                                byte[] pStrCount = BitConverter.GetBytes((ushort)2);
                                                byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                                byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);

                                                byte[] sendData = new byte[StrByte.Length + 2 + 2];

                                                Array.Reverse(pStrCount);
                                                Array.Copy(pStrCount, 0, sendData, sendDataPos, pStrCount.Length);
                                                sendDataPos += 2;
                                                Array.Reverse(pDataFieldSize);
                                                Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                                sendDataPos += 2;
                                                Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                                sendDataPos += StrByte.Length;

                                                Send(sendData, PacketType.REQ_BUSSEAT, state);
                                            }

                                            break;
                                        }

                                    case PacketType.SEND_BUSSEATUP:
                                        {
                                            if (state.strBusID != null)
                                            {
                                                state.strBusSeatNow = "" + (Convert.ToInt32(state.strBusSeatNow) + 1);
                                                StateObjectManager.instence.UpdateBusSeatNow(state);
                                            }

                                            break;
                                        }

                                    case PacketType.SEND_BUSSEATDOWN:
                                        {
                                            if (state.strBusID != null && Convert.ToInt32(state.strBusSeatNow) > 0)
                                            {
                                                state.strBusSeatNow = "" + (Convert.ToInt32(state.strBusSeatNow) - 1);
                                                StateObjectManager.instence.UpdateBusSeatNow(state);
                                            }

                                            break;
                                        }

                                    case PacketType.SEND_BUSSTOPSETTINGINFO:
                                        {
                                            int strbytepos = 0;

                                            byte[] bstrCount = new byte[2];
                                            Array.Copy(tData, strbytepos, bstrCount, 0, 2);
                                            Array.Reverse(bstrCount);
                                            ushort strCount = BitConverter.ToUInt16(bstrCount, 0);
                                            strbytepos += 2;

                                            byte[] bstrLen = new byte[2];
                                            Array.Copy(tData, strbytepos, bstrLen, 0, 2);
                                            Array.Reverse(bstrLen);
                                            ushort strLen = BitConverter.ToUInt16(bstrLen, 0);
                                            strbytepos += 2;

                                            byte[] bstr = new byte[strLen];
                                            Array.Copy(tData, strbytepos, bstr, 0, strLen);
                                            strbytepos += strLen;

                                            string str = Encoding.UTF8.GetString(bstr);
                                            state.strBusStopID = str;

                                            SQLiteCommand sqlcmd2 = new SQLiteCommand(SQLiteManager.instence.cnn);
                                            sqlcmd2.CommandText = "select * from DAEGU_Stop WHERE stopApiId='" + str + "'";

                                            try
                                            {
                                                SQLiteDataReader reader = sqlcmd2.ExecuteReader();
                                                int sendDataPos = 0;
                                                while (reader.Read())
                                                {
                                                    string name = reader["stopName"].ToString();
                                                    state.strBusStopName = name;

                                                    byte[] pStrCount = BitConverter.GetBytes((ushort)1);

                                                    byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                                    byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);

                                                    byte[] sendData = new byte[StrByte.Length + 2 + 2];

                                                    Array.Reverse(pStrCount);
                                                    Array.Copy(pStrCount, 0, sendData, sendDataPos, pStrCount.Length);
                                                    sendDataPos += 2;
                                                    Array.Reverse(pDataFieldSize);
                                                    Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                                    sendDataPos += 2;
                                                    Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                                    sendDataPos += StrByte.Length;

                                                    Send(sendData, PacketType.REQ_BUSSTOPSETTINGINFO, state);

                                                    break;
                                                }
                                                reader.Close();
                                            }
                                            catch (Exception ex)
                                            {
                                                MessageBox.Show("SEND_BUSSTOPINFO()", ex.ToString());
                                            }
                                            break;
                                        }

                                    case PacketType.SEND_BUSSTOP_CNTUP:
                                        {
                                            if (state.strBusStopID != null)
                                            {
                                                state.strBusStopNow = "" + (Convert.ToInt32(state.strBusStopNow) + 1);
                                                StateObjectManager.instence.UpdateBusStopNow(state);
                                            }
                                            break;
                                        }

                                    case PacketType.SEND_BUSSTOP_CNTDOWN:
                                        {
                                            if (state.strBusStopID != null && Convert.ToInt32(state.strBusStopNow) > 0)
                                            {
                                                state.strBusStopNow = "" + (Convert.ToInt32(state.strBusStopNow) - 1);
                                                StateObjectManager.instence.UpdateBusStopNow(state);
                                            }
                                            break;
                                        }
                                }

                                // 전역 수신 버퍼 재정렬..
                                _recvBufferIndex -= _msgLen + ConstType.PACKET_HEADER_SIZE;

                                if (_recvBufferIndex != 0)
                                {
                                    Array.Copy(_recvBuffer, (_msgLen + ConstType.PACKET_HEADER_SIZE), _tmpBuffer, 0, _recvBufferIndex);
                                    Array.Copy(_tmpBuffer, 0, _recvBuffer, 0, _recvBufferIndex);
                                }
                                else
                                {
                                    Array.Clear(_recvBuffer, 0, _recvBuffer.Length);
                                }
                                //System.Threading.Thread.Sleep(25);
                            }
                            if (_recvBufferIndex <= 0)
                                break;
                        }
                    }
                }
                else
                {
                }
                handler.BeginReceive(state.buffer, 0, ConstType.TCP_BUFFER_SIZE, 0, new AsyncCallback(ReceiveCallback), state);
            }
            catch (Exception ex)
            {
                MessageBox.Show("ReceiveCallback()", ex.ToString());
            }
        }

        public static void Send(byte[] data, PacketType packetType, StateObject state)
        {
            try
            {
                if (state == null) return;
                if (data.Length == 0) return;

                byte[] pProtocolID = BitConverter.GetBytes((ushort)packetType);
                byte[] pDataFieldSize = BitConverter.GetBytes((ushort)data.Length);

                int msgLen = data.Length + ConstType.PACKET_HEADER_SIZE;
                byte[] sendData = new byte[msgLen];

                // 엔디안 형식 변환을 위한 리버스
                Array.Reverse(pProtocolID);
                Array.Reverse(pDataFieldSize);

                Array.Copy(pProtocolID, 0, sendData, 0, pProtocolID.Length);
                Array.Copy(pDataFieldSize, 0, sendData, 2, pDataFieldSize.Length);
                Array.Copy(data, 0, sendData, ConstType.PACKET_HEADER_SIZE, data.Length);

                Socket handler = state.workSocket;
                handler.BeginSend(sendData, 0, sendData.Length, 0, new AsyncCallback(sendCallback), handler);
            }
            catch (Exception ex)
            {
                // MessageBox.Show("Send()", ex.ToString());
            }
        }
        /*
        public void Send(byte[] data, PacketType packetType)
        {
            try
            {
                if (data.Length == 0) return;

                byte[] pProtocolID = BitConverter.GetBytes((ushort)packetType);
                byte[] pDataFieldSize = BitConverter.GetBytes((ushort)data.Length);

                int msgLen = data.Length + ConstType.PACKET_HEADER_SIZE;
                byte[] sendData = new byte[msgLen];

                // 엔디안 형식 변환을 위한 리버스
                Array.Reverse(pProtocolID);
                Array.Reverse(pDataFieldSize);

                Array.Copy(pProtocolID, 0, sendData, 0, pProtocolID.Length);
                Array.Copy(pDataFieldSize, 0, sendData, 2, pDataFieldSize.Length);
                Array.Copy(data, 0, sendData, ConstType.PACKET_HEADER_SIZE, data.Length);
                StateObject state = UserManager.instence.GetSelectUserObject();
                if (state == null)
                    return;

                Socket handler = state.workSocket;
                handler.BeginSend(sendData, 0, sendData.Length, 0, new AsyncCallback(sendCallback), handler);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Send()", ex.ToString());
            }
        }*/

        static void sendCallback(IAsyncResult ar)
        {
            try
            {
                Socket handler = (Socket)ar.AsyncState;
                handler.EndSend(ar);
            }
            catch (Exception ex)
            {
                MessageBox.Show("sendCallback()", ex.ToString());
            }
        }
    }
}
