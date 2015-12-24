using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GetDB
{
    public class GeoPoint
    {
        public double x;
        public double y;
        public double z;

        public GeoPoint()
        {
        }

        public GeoPoint(double x, double y)
        {
            this.x = x;
            this.y = y;
            this.z = 0;
        }

        public double getX()
        {
            return x;
        }

        public double getY()
        {
            return y;
        }
    }
}
