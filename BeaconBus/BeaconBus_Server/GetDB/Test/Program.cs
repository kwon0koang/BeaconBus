using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.IO;
using System.Runtime.InteropServices; //관리되지않는 코드 (포인터 사용및 구조체를 시퀜셜하게 만들기위해)

namespace Test
{
    [StructLayout(LayoutKind.Sequential)]
    public class patket
    {
        public char Command;
        public ushort Length;
    };
    class Program
    {
        //network
        private static const int portNum = 33333;
        public static EndPoint localEP = null;
        public static EndPoint remoteEP = null;
        public static StreamWriter writer = null;
        public static bool udpstate;
        public static TcpClient client = null;
        public static NetworkStream ns = null;
        public static IPAddress ipAddress = null;
        public static bool isWork;


        static void Main(string[] args)
        {
            patket sendpatket = new patket();
            IPHostEntry host = Dns.GetHostByName(Dns.GetHostName());
            string cmd;
            string myip = host.AddressList[0].ToString();
            ipAddress = IPAddress.Parse(myip);
            isWork = false;
            udpstate = false;
            SC.isLog = true;

            SC.log("---------------------------Basic Info---------------------------");
            SC.log("Server IP :" + ipAddress);
            SC.log("Server Portnum : " + portNum);
            SC.log("---------------------------Basic Info---------------------------");

            TcpListener tcp_Listener = new TcpListener(ipAddress, portNum);
            tcp_Listener.Start();
            while (true)
            {
                try
                {
                    SC.log("Server(TCP) Start...");
                    SC.log("Server(TCP) Listenning...");

                    client = tcp_Listener.AcceptTcpClient();      //클라이언트와 접속
                    SC.log("Client(TCP) Connected.");

                    ns = client.GetStream();
                    writer = new StreamWriter(ns);

                    while (client.Connected == true)
                    {
                        cmd = Console.ReadLine();

                        if(cmd.Equals("1"))
                        {
                            try
                            {
                                sendpatket.Command = '1';
                                byte[] temp = new byte[8];
                                sendpatket.Length = 123;////
                                SC.log("Send MSG : " + (ushort)(256));

                                byte[] buffer = new byte[Marshal.SizeOf(sendpatket)];

                                unsafe
                                {
                                    fixed (byte* fixed_buffer = buffer)
                                    {
                                        Marshal.StructureToPtr(sendpatket, (IntPtr)fixed_buffer, false);
                                    }
                                }

                            }
                            catch (ArgumentException are)
                            {
                                SC.log(are.Message);
                            }
                            catch (IOException ie)
                            {
                                SC.log(ie.Message);
                            }
                        }
                        else
                        {
                              try
                            {
                                sendpatket.Command = '2';
                                byte[] temp = new byte[8];
                                sendpatket.Length = 1333;////
                                SC.log("Send MSG : " + (ushort)(256));

                                byte[] buffer = new byte[Marshal.SizeOf(sendpatket)];

                                unsafe
                                {
                                    fixed (byte* fixed_buffer = buffer)
                                    {
                                        Marshal.StructureToPtr(sendpatket, (IntPtr)fixed_buffer, false);
                                    }
                                }

                            }
                            catch (ArgumentException are)
                            {
                                SC.log(are.Message);
                            }
                            catch (IOException ie)
                            {
                                SC.log(ie.Message);
                            }
                        }
                     
                       


                    }

                    SC.log("Client(TCP) Disconnect");

                    writer.Close();
                    client.Close();
                    Thread.Sleep(100);

                }
                catch (SocketException se)
                {
                    SC.log(se.Message);
                    break;
                }
            }
        }

        
    }
}
