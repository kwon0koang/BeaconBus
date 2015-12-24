using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Net;
using System.Web;
using mshtml;
using System.IO;
using GetDB.Network;

namespace GetDB
{
    public class APIManager
    {
        public delegate void ReqResultTextBox(string data);
        public static event ReqResultTextBox ReqResultTextBoxEvent;

        public delegate void ClearItemListBox();
        public static event ClearItemListBox ClearItemListBoxEvent;

        public delegate void AddItemListBox(string data);
        public static event AddItemListBox AddItemListBoxEvent;

        private static APIManager _instance = new APIManager();
        public static APIManager instence
        {
            get
            {
                return _instance;
            }
        }

        public List<URLInfo> pathlist = new List<URLInfo>();

        private APIManager()
        {
        }

        public void GetWebRequest(URLInfo url)
        {
            Thread workerThread = new Thread(new ParameterizedThreadStart(webRequestStream));
            workerThread.Start(url);
        }

        void webRequestStream(object sURL)
        {
            try
            {
                URLInfo Info = (URLInfo)sURL;
                // 한글부분만 UTF-8 인코딩 (한글이 깨지면)
                // 내가 했던 부분에선 날짜 형식을 인코딩하면 문제가 발생되는거 같아서 한글부분만 따로 인코딩
                int i = 0;
                HttpWebRequest myReq;
                if (int.TryParse(Info.searchText, out i) || Info.parserType == PARSER_TYPE.SEARCH_STATION2) //i now = 108
                {
                    //주소부분에 다 넣고 객체 생성
                    myReq = (HttpWebRequest)WebRequest.Create(Info.bagicPath + Info.searchText);
                }
                else
                {
                    string url = System.Web.HttpUtility.UrlEncode(Info.searchText, System.Text.Encoding.GetEncoding("euc-kr"));

                    //주소부분에 다 넣고 객체 생성
                    myReq = (HttpWebRequest)WebRequest.Create(Info.bagicPath + url);
                }
                //myReq.Accept = "*/*";
                //myReq.Method = "GET"; // 필요 없는듯?
                //myReq.Headers.Add("Accept-Language", "ko");
                myReq.Accept = "*/*";
                myReq.Headers["Accept-Language"] = "ko";
                myReq.Headers["Accept-Encoding"] = "gzip, deflate";
                myReq.UserAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
                myReq.KeepAlive = true;
                myReq.AllowAutoRedirect = true;
                myReq.Timeout = 60000;

                //HttpWebResponse 객체 받아옴
                HttpWebResponse wRes = (HttpWebResponse)myReq.GetResponse();

                // Response의 결과를 스트림을 생성합니다.
                Stream respGetStream = wRes.GetResponseStream();
                StreamReader readerGet = new StreamReader(respGetStream, Encoding.Default, true);

                // 생성한 스트림으로부터 string으로 변환합니다.
                string resultGet = readerGet.ReadToEnd();
                ReqResultTextBoxEvent(resultGet);

                HTMLDocument doc = new HTMLDocument();                
                doc.designMode = "on";
                object[] oPageText = { resultGet };
                IHTMLDocument2 oMyDoc = (IHTMLDocument2)doc;
                oMyDoc.write(oPageText);


                switch (Info.parserType)
                {
                    case PARSER_TYPE.SEARCH_STATION:
                        {
                            ClearItemListBoxEvent();
                            IHTMLElementCollection trs = doc.getElementsByTagName("td");
                            int count = 0;
                            foreach (IHTMLElement cel in trs)
                            {
                                string tt = cel.getAttribute("onClick", 0).ToString();
                                string dd = "'";
                                string[] test1Result = tt.Split(dd.ToCharArray(0, 1));

                                if (cel.innerText != null && test1Result[7] != "0")
                                {
                                    count++;
                                    AddItemListBoxEvent(count + ". " + test1Result[1]);
                                    AddItemListBoxEvent(test1Result[7]);
                                }
                            }
                            break;
                        }
                    case PARSER_TYPE.SEARCH_BUSNUM:
                        {
                            ClearItemListBoxEvent();
                            IHTMLElementCollection trs = doc.getElementsByTagName("td");
                            foreach (IHTMLElement cel in trs)
                            {
                                string tt = cel.getAttribute("onClick", 0).ToString();
                                string dd = "'";
                                string[] test1Result = tt.Split(dd.ToCharArray(0, 1));

                                if (cel.innerText.Length > 3 && test1Result[1].IndexOf("&nbsp;") == -1)
                                {
                                    AddItemListBoxEvent(cel.innerText);
                                    if(test1Result[1].IndexOf("node") == -1)
                                        AddItemListBoxEvent(test1Result[1]);
                                }
                            }
                            break;
                        }
                    case PARSER_TYPE.SEARCH_BUSLOCATION:
                        {
                            List<string> tempstr = new List<string>();
                            List<StateObject> templist = StateObjectManager.instence.GetList();
                            ClearItemListBoxEvent();
                            IHTMLElementCollection trs = doc.getElementsByTagName("td");

                            foreach (IHTMLElement cel in trs)
                            {
                                if (cel.innerText.Length > 3)
                                {
                                    if (Info.state == null)
                                        AddItemListBoxEvent(cel.innerText);
                                    else
                                    {
                                        tempstr.Add(cel.innerText);
                                    }
                                }
                            }

                            for (int n = 0; n < tempstr.Count; n++)
                            {
                                if (tempstr[n].IndexOf(Info.state.strBusNumber) != -1)
                                {
                                    for (int m = n; m < tempstr.Count; m++)
                                    {
                                        if (tempstr[m + 1].IndexOf(":") == -1 && m + 1 < tempstr.Count  )
                                        {
                                             Info.state.strNextBusStop = tempstr[m + 1];

                                            int sendDataPos = 0;

                                            if (Info.state != null)
                                            {
                                                string name = Info.state.strNextBusStop;
                                                string name2 = null;
                                                for (int l = 0; l < templist.Count; l++) 
                                                {
                                                    if (templist[l].strBusStopName != null && name.IndexOf(templist[l].strBusStopName) != -1)
                                                        name2 = templist[l].strBusStopNow;
                                                }

                                                if (name2 == null)
                                                    name2 = "0";

                                                Console.WriteLine("Log Next BusStop : " + name);

                                                byte[] pStrCount = BitConverter.GetBytes((ushort)2);

                                                byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                                byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);

                                                byte[] StrByte2 = Encoding.UTF8.GetBytes(name2);
                                                byte[] pDataFieldSize2 = BitConverter.GetBytes((ushort)StrByte2.Length);

                                                byte[] sendData = new byte[StrByte.Length + StrByte2.Length + 2 + 2 + 2];

                                                // 수
                                                Array.Reverse(pStrCount);
                                                Array.Copy(pStrCount, 0, sendData, sendDataPos, pStrCount.Length);
                                                sendDataPos += 2;
                                                // 1
                                                Array.Reverse(pDataFieldSize);
                                                Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                                sendDataPos += 2;
                                                Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                                sendDataPos += StrByte.Length;
                                                // 2
                                                Array.Reverse(pDataFieldSize2);
                                                Array.Copy(pDataFieldSize2, 0, sendData, sendDataPos, pDataFieldSize2.Length);
                                                sendDataPos += 2;
                                                Array.Copy(StrByte2, 0, sendData, sendDataPos, StrByte2.Length);
                                                sendDataPos += StrByte2.Length;

                                                AsyncNetServer.Send(sendData, PacketType.SEND_BUSLOCATIONUPDATE, Info.state);
                                            }
                                            n = tempstr.Count;
                                            break;
                                        }
                                    }
                                }
                            }

                            // Info.state = null;
                            break;
                        }
                    case PARSER_TYPE.SEARCH_BUSLOCATION2:
                        {
                            List<string> tempstr = new List<string>();
                            ClearItemListBoxEvent();
                            IHTMLElementCollection trs = doc.getElementsByTagName("td");

                            foreach (IHTMLElement cel in trs)
                            {
                                if (cel.innerText.Length > 3)
                                {
                                    if (cel.innerText.IndexOf(":") == -1)
                                    {
                                        if(Info.state == null)
                                            AddItemListBoxEvent(cel.innerText);
                                        else
                                            tempstr.Add(cel.innerText);
                                    }
                                }
                            }

                            //for (int iCnt = 0; iCnt < tempstr.Count;iCnt++ )
                            //{
                            //    Console.WriteLine((iCnt + 1) + " : " + tempstr[iCnt]);
                            //}


                            if (Info.state != null)
                            {
                                // Front
                                int sendDataPos = 0;
                                int totalLen = 2;
                                for (int n = 0; n < tempstr.Count; n++)
                                {
                                    string name = tempstr[n];
                                    byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                    totalLen += StrByte.Length + 2;
                                }

                                byte[] sendData = new byte[totalLen + 13];
                                byte[] pStrCount = BitConverter.GetBytes((ushort)(tempstr.Count + 2));
                                Array.Reverse(pStrCount);
                                Array.Copy(pStrCount, 0, sendData, sendDataPos, pStrCount.Length);
                                sendDataPos += 2;

                                for (int n = 0; n < tempstr.Count / 2; n++)
                                {
                                    string name = tempstr[n];
                                    byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                    byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);
                                    //Console.WriteLine("Name : " + name + ", strlen : " + StrByte.Length + ", reqlen : " + BitConverter.ToInt16(pDataFieldSize,0));

                                    Array.Reverse(pDataFieldSize);
                                    Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                    sendDataPos += 2;
                                    Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                    sendDataPos += StrByte.Length;
                                    
                                }

                                for (int n = tempstr.Count / 2; n < tempstr.Count; n++)
                                {
                                    string name = tempstr[n];
                                    byte[] StrByte = Encoding.UTF8.GetBytes(name);
                                    byte[] pDataFieldSize = BitConverter.GetBytes((ushort)StrByte.Length);
                                    //Console.WriteLine("Name : " + name + ", strlen : " + StrByte.Length + ", reqlen : " + BitConverter.ToInt16(pDataFieldSize, 0));

                                    Array.Reverse(pDataFieldSize);
                                    Array.Copy(pDataFieldSize, 0, sendData, sendDataPos, pDataFieldSize.Length);
                                    sendDataPos += 2;
                                    Array.Copy(StrByte, 0, sendData, sendDataPos, StrByte.Length);
                                    sendDataPos += StrByte.Length;
                                }

                                AsyncNetServer.Send(sendData, PacketType.REQ_BUSSETTINGINFO, Info.state);
                            }

                            // Info.state = null;
                            break;
                        }
                    case PARSER_TYPE.SEARCH_STATION2:
                        {
                            ClearItemListBoxEvent();
                            IHTMLElementCollection trs = doc.getElementsByTagName("td");

                            for (int num = 0; num < trs.length; num += 3)
                            {
                                IHTMLElement cel = trs.item(num);
                                string tt = cel.getAttribute("onClick", 0).ToString();
                                string dd = "'";
                                string[] test1Result = tt.Split(dd.ToCharArray(0, 1));
                                if (test1Result.Length > 6)
                                {
                                    AddItemListBoxEvent(cel.innerText);
                                    AddItemListBoxEvent(test1Result[1]);
                                }
                            }
                            //foreach (IHTMLElement cel in trs)
                            //{
                            //    string tt = cel.getAttribute("onClick", 0).ToString();
                            //    string dd = "'";
                            //    string[] test1Result = tt.Split(dd.ToCharArray(0, 1));
                            //
                            //    if (cel.innerText != null && test1Result[7] != "0")
                            //    {
                            //        AddListBox_Control(this.listBox1, test1Result[1]);
                            //        AddListBox_Control(this.listBox1, test1Result[7]);
                            //    }
                            //}
                            break;
                        }
                }
                //IHTMLElement cel = trs.item(1);
            }
            catch (Exception e)
            {
                AddItemListBoxEvent(e.Message.ToString());
            }
        }
    }
}
