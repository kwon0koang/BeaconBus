using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Test
{
    class SC
    {
        public static bool isLog = false;
        public static bool isReSet = true;

        public static void log(String str)
        {
            if (isLog)
                Console.WriteLine(str);
        }

    }
}

