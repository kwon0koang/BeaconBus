using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Threading;
using System.Net.Sockets;

namespace GetDB
{
    public class TCPServer
    {
        private static volatile TCPServer instance;
        private static object syncRoot = new Object();
        
        private TcpListener Listener = null;
        private TcpClient client = null;

        public event Action<Receiver> eventHandler;
        
        int PORT = 5555;

        private TCPServer() { }

        public void Startsever()
        {
            Thread workerThread = new Thread(new ThreadStart(Serverwork));
            workerThread.IsBackground = true;
            workerThread.Start();
        }

        public void Serverwork()
        {
            try
            {
                Listener = new TcpListener(PORT);
                Listener.Start(); // Listener 동작 시작

                while (true)
                {
                    client = Listener.AcceptTcpClient();
                    Receiver r = new Receiver();
                    r.startClient(client);
                    eventHandler(r);
                }
            }
            catch (Exception e)
            {
                System.Console.WriteLine(e.Message);
            }
        }

        public static TCPServer Instance
        {
            get
            {
                if (instance == null)
                {
                    lock (syncRoot)
                    {
                        if (instance == null)
                            instance = new TCPServer();
                    }
                }
                return instance;
            }
        }

    }
}
