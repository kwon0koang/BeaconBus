using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;
using GetDB.Network;
using System.Data.SQLite;

namespace GetDB
{
    public partial class GetDBForm : Form
    {
        public static AsyncNetServer server = new AsyncNetServer();

        private GeoTrans transTm = new GeoTrans(GeoTrans.GEO, GeoTrans.TM);
        private GeoTrans transGeo = new GeoTrans(GeoTrans.TM, GeoTrans.GEO);

        private DataTable dt;
        private DataTable dt2;

        public GetDBForm()
        {
            InitializeComponent();

            dt = new DataTable();
            dt2 = new DataTable();

            this.comboBox1.Items.Add("출발지 검색");
            APIManager.instence.pathlist.Add(AddURLInfo("http://businfo.daegu.go.kr/ba/route/sdroute.do?act=SrcStationResult&SearchName=", PARSER_TYPE.START_STATION));
            this.comboBox1.Items.Add("도착지 검색");
            APIManager.instence.pathlist.Add(AddURLInfo("http://businfo.daegu.go.kr/ba/route/sdroute.do?act=DstStationResult&SearchName=", PARSER_TYPE.END_STATION));
            this.comboBox1.Items.Add("버스 검색");
            APIManager.instence.pathlist.Add(AddURLInfo("http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByNo&routeNo=", PARSER_TYPE.SEARCH_BUSNUM));
            this.comboBox1.Items.Add("버스위치정보 검색");
            APIManager.instence.pathlist.Add(AddURLInfo("http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByPos&routeId=", PARSER_TYPE.SEARCH_BUSLOCATION));
            this.comboBox1.Items.Add("버스정보 검색");
            APIManager.instence.pathlist.Add(AddURLInfo("http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByPos&routeId=", PARSER_TYPE.SEARCH_BUSLOCATION2));
            this.comboBox1.Items.Add("정류소 검색");
            APIManager.instence.pathlist.Add(AddURLInfo("http://businfo.daegu.go.kr/ba/route/route.do?act=findByBS&bsNm=", PARSER_TYPE.SEARCH_STATION));
            this.comboBox1.Items.Add("검색");
            APIManager.instence.pathlist.Add(AddURLInfo("http://businfo.daegu.go.kr/ba/route/rtbsarr.do?act=findByArr&bsId=", PARSER_TYPE.SEARCH_STATION2));

            this.comboBox1.SelectedIndex = 0;

            //             GeoPoint in_pt = new GeoPoint(DegreeToRadian(127), DegreeToRadian(38));
            //             GeoPoint out_pt = new GeoPoint();
            // 
            //             transTm.geo2tm(in_pt, out_pt);
            //             MessageBox.Show(", xGeo="  + in_pt.getX() + ", yGeo=" + in_pt.getY() + ", xTM=" + out_pt.getX() + ", yTM=" + out_pt.getY());
            //             double xTm = out_pt.getX();
            //             double yTm = out_pt.getY();

            //             GeoPoint in_pt2 = new GeoPoint(166108.65365621, 368672.04358837);
            //             //GeoPoint in_pt2 = new GeoPoint(368672.04358837, 166108.65365621);
            //             GeoPoint out2_pt = new GeoPoint();
            //             transGeo.tm2geo(in_pt2, out2_pt);
            //             MessageBox.Show(", xTm=" + in_pt2.getX() + ", yTm=" + in_pt2.getY() + ", xGeo=" + out2_pt.getX() + ", yGeo=" + out2_pt.getY());
            /*
            GeoTrans2 transs = new GeoTrans2();
            GeoPoint in_pt2 = new GeoPoint(166108.65365621, 368672.04358837);
            //GeoPoint in_pt2 = new GeoPoint(368672.04358837, 166108.65365621);
            GeoPoint katec_pt = transs.convert(GeoTrans.TM, GeoTrans.GEO, in_pt2);
            MessageBox.Show("katec : xKATEC=" + katec_pt.getX() + ", yKATEC=" + katec_pt.getY());
            */
            //TCPServer.Instance.Startsever();
            //TCPServer.Instance.eventHandler += ProcessEvent;
            StateObjectManager.UserManagerUserAddIPData += ProcessEvent;
            StateObjectManager.UserManagerUserRemoveIPData += ProcessEvent2;
            APIManager.ReqResultTextBoxEvent += ProcessEvent3;
            APIManager.ClearItemListBoxEvent += ProcessEvent4;
            APIManager.AddItemListBoxEvent += ProcessEvent5;

            server.StartServer();

            //DBDateLoad();
            DBTEST();
        }

        public void DBDateLoad()
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Filter = "SQLite3 Database Files|*.db|All Files|*.*";
            ofd.FilterIndex = 1;
            ofd.RestoreDirectory = true;
            try
            {
                if (ofd.ShowDialog() == DialogResult.OK)
                {
                    //this.tbDBPath.Text = ofd.FileName;
                    MessageBox.Show("DB File Open :\n" + ofd.FileName);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("File Open Error :\n" + ex.Message);
            }
        }

        public void DBTEST()
        {
//             string strConn = @"Data Source=beaconbus.db";
// 
//             using (SQLiteConnection conn = new SQLiteConnection(strConn))
//             {
//                 conn.Open();
//                 string sql = "INSERT INTO member VALUES (100, 'Tom')";
//                 SQLiteCommand cmd = new SQLiteCommand(sql, conn);
//                 cmd.ExecuteNonQuery();
// 
//                 cmd.CommandText = "DELETE FROM member WHERE Id=1";
//                 cmd.ExecuteNonQuery();
//             }

            try
            {
                // 1
                SQLiteCommand sqlcmd = new SQLiteCommand(SQLiteManager.instence.cnn);
                sqlcmd.CommandText = "select * from DAEGU_Route;";
                SQLiteDataReader reader = sqlcmd.ExecuteReader();
                this.dt.Load(reader);
                this.dgvTable.DataSource = dt;
                reader.Close();
                // 2
                SQLiteCommand sqlcmd2 = new SQLiteCommand(SQLiteManager.instence.cnn);
                sqlcmd2.CommandText = "select * from DAEGU_Stop;";
                SQLiteDataReader reader2 = sqlcmd2.ExecuteReader();
                this.dt2.Load(reader2);
                this.dgvTable2.DataSource = dt2;
                reader2.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("SQLite3 Database Connection Error :\n" + ex.Message);
            }

        }

        public URLInfo AddURLInfo(string path, PARSER_TYPE type)
        {
            URLInfo temp = new URLInfo();
            temp.bagicPath = path;
            temp.parserType = type;

            return temp;
        }

        private void searchAPI()
        {
            this.listBox1.Items.Clear();
            URLInfo Info = APIManager.instence.pathlist[this.comboBox1.SelectedIndex];
            Info.searchText = this.textBox2.Text;
            APIManager.instence.GetWebRequest(Info);
        }

        #region ProcessEvent
        public void ProcessEvent(string _event)
        {
            if (_event != null)
            {
                AddListBox_Control(this.listBox2, _event.ToString());
            }
        }

        public void ProcessEvent2(string _event)
        {
            if (_event != null)
            {
                RemoveListBox_Control(this.listBox2, _event.ToString());
            }
        }

        public void ProcessEvent3(string _event)
        {
            if (_event != null)
            {
                AddTextBox_Control(this.textBox3, _event);
            }
        }

        public void ProcessEvent4()
        {
            ClearListBox_Control(this.listBox1, "");
        }

        public void ProcessEvent5(string _event)
        {
            if (_event != null)
            {
                AddListBox_Control(this.listBox1, _event);
            }
        }
        #endregion

        #region delegate
        delegate void Ctr_Involk(Control ctr, string str);

        public void ClearListBox_Control(Control ctr, string str)
        {
            if (ctr.InvokeRequired)
            {
                Ctr_Involk CI = new Ctr_Involk(ClearListBox_Control);
                ctr.Invoke(CI, ctr, str);
            }
            else
            {
                ListBox listBox = (ListBox)ctr;
                listBox.Items.Clear();
            }
        }

        public void AddListBox_Control(Control ctr, string str)
        {
            if (ctr.InvokeRequired)
            {
                Ctr_Involk CI = new Ctr_Involk(AddListBox_Control);
                ctr.Invoke(CI, ctr, str);
            }
            else
            {
                ListBox listBox = (ListBox)ctr;
                listBox.Items.Add(str);
            }
        }

        public void RemoveListBox_Control(Control ctr, string str)
        {
            if (ctr.InvokeRequired)
            {
                Ctr_Involk CI = new Ctr_Involk(RemoveListBox_Control);
                ctr.Invoke(CI, ctr, str);
            }
            else
            {
                ListBox listBox = (ListBox)ctr;
                if(listBox.Items.IndexOf(str) != -1)
                    listBox.Items.Remove(str);
            }
        }

        public void AddTextBox_Control(Control ctr, string str)
        {
            if (ctr.InvokeRequired)
            {
                Ctr_Involk CI = new Ctr_Involk(AddTextBox_Control);
                ctr.Invoke(CI, ctr, str);
            }
            else
            {
                TextBox listBox = (TextBox)ctr;
                listBox.Text = str.ToString();
            }
        }
        #endregion

        #region Form Event
        private void button1_Click(object sender, EventArgs e)
        {
            searchAPI();
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            this.textBox1.Text = APIManager.instence.pathlist[this.comboBox1.SelectedIndex].bagicPath;
        }

        private void textBox2_KeyUp(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                searchAPI();
            }
        }
        #endregion
    }
}
