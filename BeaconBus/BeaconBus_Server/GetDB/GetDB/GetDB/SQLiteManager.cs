using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SQLite;

namespace GetDB
{
    public class SQLiteManager
    {
        public SQLiteConnection cnn;
        private static SQLiteManager _instance = new SQLiteManager();
        public static SQLiteManager instence
        {
            get
            {
                return _instance;
            }
        }

        private SQLiteManager()
        {
            cnn = new SQLiteConnection(String.Format("Data Source={0}", "beaconbus.db"));
            cnn.Open();
        }
    }
}
