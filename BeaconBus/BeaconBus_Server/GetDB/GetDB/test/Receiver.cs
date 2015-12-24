using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace GetDB
{
    public class Receiver
    {
        NetworkStream NS = null;
        StreamReader SR = null;
        StreamWriter SW = null;
        public TcpClient client;

        public void startClient(TcpClient clientSocket)
        {
            client = clientSocket;
            Thread echo_thread = new Thread(echo);
            echo_thread.Start();
        }

        public void echo()
        {
            NS = client.GetStream(); // 소켓에서 메시지를 가져오는 스트림
            SR = new StreamReader(NS, Encoding.UTF8); // Get message
            SW = new StreamWriter(NS, Encoding.UTF8); // Send message

            string GetMessage = string.Empty;
            try
            {
                while (client.Connected == true) //클라이언트 메시지받기
                {
                    GetMessage = SR.ReadLine();

                    SW.WriteLine("Server: {0} [{1}]", GetMessage, DateTime.Now); // 메시지 보내기
                    SW.Flush();
                    Console.WriteLine("Log: {0} [{1}]", GetMessage, DateTime.Now);
                }
            }
            catch (Exception ee)
            {

            }
            finally
            {
                SW.Close();
                SR.Close();
                client.Close();
                NS.Close();
            }
        }
    }
}
