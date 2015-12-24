using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GetDB
{
    public class GeoTrans
    {

        private double x;
        private double y;

        public static int GEO = 0;
        public static int KATEC = 1;
        public static int TM = 2;

        private int srctype = KATEC;
        private int dsttype = GEO;

        private double[] m_Ind = new double[3];
        private double[] m_Es = new double[3];
        private double[] m_Esp = new double[3];
        private double[] src_m = new double[3];
        private double[] dst_m = new double[3];

        private static double EPSLN = 0.0000000001;
        private double[] m_arMajor = new double[3];
        private double[] m_arMinor = new double[3];
        //private static double m_arMajor = 6378137.0;
        //private static double m_arMinor = 6356752.3142;

        private double[] m_arScaleFactor = new double[3];
        private double[] m_arLonCenter = new double[3];
        private double[] m_arLatCenter = new double[3];
        private double[] m_arFalseNorthing = new double[3];
        private double[] m_arFalseEasting = new double[3];

        private double[] datum_params = new double[3];

        /**
         * @param srctype
         * @param dsttype
         */
        public GeoTrans(int srctype, int dsttype)
        {
            this.m_arScaleFactor[GEO] = 1;
            this.m_arLonCenter[GEO] = 0.0;
            this.m_arLatCenter[GEO] = 0.0;
            this.m_arFalseNorthing[GEO] = 0.0;
            this.m_arFalseEasting[GEO] = 0.0;
            this.m_arMajor[GEO] = 6378137.0;
            this.m_arMinor[GEO] = 6356752.3142;

            this.m_arScaleFactor[KATEC] = 0.9999;
            this.m_arLonCenter[KATEC] = 2.23402144255274;
            this.m_arLatCenter[KATEC] = 0.663225115757845;
            this.m_arFalseNorthing[KATEC] = 600000.0;
            this.m_arFalseEasting[KATEC] = 400000.0;
            this.m_arMajor[KATEC] = 6377397.155;
            this.m_arMinor[KATEC] = 6356078.9633422494;

            this.m_arScaleFactor[TM] = 1.0;
            //this.m_arLonCenter[TM] = 2.21656815003280;
            this.m_arLonCenter[TM] = 2.21661859489671;
            this.m_arLatCenter[TM] = 0.663225115757845;
            this.m_arFalseNorthing[TM] = 500000.0;
            this.m_arFalseEasting[TM] = 200000.0;
            this.m_arMajor[TM] = 6377397.155;
            this.m_arMinor[TM] = 6356078.9633422494;

            this.datum_params[0] = -146.43;
            this.datum_params[1] = 507.89;
            this.datum_params[2] = 681.46;

            this.srctype = srctype;
            this.dsttype = dsttype;

            double tmp = m_arMinor[GEO] / m_arMajor[GEO];
            this.m_Es[GEO] = 1.0 - tmp * tmp;
            this.m_Esp[GEO] = this.m_Es[GEO] / (1.0 - this.m_Es[GEO]);

            if (this.m_Es[GEO] < 0.00001)
            {
                this.m_Ind[GEO] = 1.0;
            }
            else
            {
                this.m_Ind[GEO] = 0.0;
            }

            tmp = m_arMinor[KATEC] / m_arMajor[KATEC];
            this.m_Es[TM] = this.m_Es[KATEC] = 1.0 - tmp * tmp;
            this.m_Esp[TM] = this.m_Esp[KATEC] = this.m_Es[KATEC] / (1.0 - this.m_Es[KATEC]);

            if (this.m_Es[KATEC] < 0.00001)
            {
                this.m_Ind[TM] = this.m_Ind[KATEC] = 1.0;
            }
            else
            {
                this.m_Ind[TM] = this.m_Ind[KATEC] = 0.0;
            }

            this.src_m[GEO] = this.m_arMajor[GEO] * this.mlfn(this.e0fn(this.m_Es[GEO]), this.e1fn(this.m_Es[GEO]), this.e2fn(this.m_Es[GEO]), this.e3fn(this.m_Es[GEO]), this.m_arLatCenter[srctype]);
            this.dst_m[GEO] = this.m_arMajor[GEO] * this.mlfn(this.e0fn(this.m_Es[GEO]), this.e1fn(this.m_Es[GEO]), this.e2fn(this.m_Es[GEO]), this.e3fn(this.m_Es[GEO]), this.m_arLatCenter[dsttype]);
            this.src_m[KATEC] = this.m_arMajor[KATEC] * this.mlfn(this.e0fn(this.m_Es[KATEC]), this.e1fn(this.m_Es[KATEC]), this.e2fn(this.m_Es[KATEC]), this.e3fn(this.m_Es[KATEC]), this.m_arLatCenter[srctype]);
            this.dst_m[KATEC] = this.m_arMajor[KATEC] * this.mlfn(this.e0fn(this.m_Es[KATEC]), this.e1fn(this.m_Es[KATEC]), this.e2fn(this.m_Es[KATEC]), this.e3fn(this.m_Es[KATEC]), this.m_arLatCenter[dsttype]);
            this.src_m[TM] = this.m_arMajor[TM] * this.mlfn(this.e0fn(this.m_Es[TM]), this.e1fn(this.m_Es[TM]), this.e2fn(this.m_Es[TM]), this.e3fn(this.m_Es[TM]), this.m_arLatCenter[srctype]);
            this.dst_m[TM] = this.m_arMajor[TM] * this.mlfn(this.e0fn(this.m_Es[TM]), this.e1fn(this.m_Es[TM]), this.e2fn(this.m_Es[TM]), this.e3fn(this.m_Es[TM]), this.m_arLatCenter[dsttype]);
        }

        private double D2R(double degree)
        {
            return degree * Math.PI / 180.0;
        }

        private double R2D(double radian)
        {
            return radian * 180.0 / Math.PI;
        }

        private double e0fn(double x)
        {
            return 1.0 - 0.25 * x * (1.0 + x / 16.0 * (3.0 + 1.25 * x));
        }

        private double e1fn(double x)
        {
            return 0.375 * x * (1.0 + 0.25 * x * (1.0 + 0.46875 * x));
        }

        private double e2fn(double x)
        {
            return 0.05859375 * x * x * (1.0 + 0.75 * x);
        }

        private double e3fn(double x)
        {
            return x * x * x * (35.0 / 3072.0);
        }

        private double mlfn(double e0, double e1, double e2, double e3, double phi)
        {
            return e0 * phi - e1 * Math.Sin(2.0 * phi) + e2 * Math.Sin(4.0 * phi) - e3 * Math.Sin(6.0 * phi);
        }

        private double asinz(double value)
        {
            if (Math.Abs(value) > 1.0) value = (value > 0 ? 1 : -1);
            return Math.Asin(value);
        }

        private GeoPoint conv(GeoPoint in_pt)
        {
            GeoPoint tmpPt = new GeoPoint();
            GeoPoint out_pt = new GeoPoint();

            if (this.srctype == GEO)
            {
                tmpPt.x = this.D2R(in_pt.x);
                tmpPt.y = this.D2R(in_pt.y);
            }
            else
            {
                this.tm2geo(in_pt, tmpPt);
            }

            if (this.dsttype == GEO)
            {
                out_pt.x = this.R2D(tmpPt.x);
                out_pt.y = this.R2D(tmpPt.y);
            }
            else
            {
                this.geo2tm(tmpPt, out_pt);
                out_pt.x = Math.Round(out_pt.x);
                out_pt.y = Math.Round(out_pt.y);
            }

            return out_pt;
        }

        public void geo2tm(GeoPoint in_pt, GeoPoint out_pt)
        {
            transform(srctype, dsttype, in_pt);
            double delta_lon = in_pt.x - this.m_arLonCenter[this.dsttype];
            double sin_phi = Math.Sin(in_pt.y);
            double cos_phi = Math.Cos(in_pt.y);

            if (this.m_Ind[this.dsttype] != 0)
            {
                double b = cos_phi * Math.Sin(delta_lon);

                if ((Math.Abs(Math.Abs(b) - 1.0)) < EPSLN)
                {
                    MessageBox.Show("무한대 에러");
                }
            }
            else
            {
                double b = 0;
                x = 0.5 * m_arMajor[this.dsttype] * this.m_arScaleFactor[this.dsttype] * Math.Log((1.0 + b) / (1.0 - b));
                double con = Math.Acos(cos_phi * Math.Cos(delta_lon) / Math.Sqrt(1.0 - b * b));

                if (in_pt.y < 0)
                {
                    con = con * -1;
                    y = m_arMajor[this.dsttype] * this.m_arScaleFactor[this.dsttype] * (con - this.m_arLatCenter[this.dsttype]);
                }
            }

            double al = cos_phi * delta_lon;
            double als = al * al;
            double c = this.m_Esp[this.dsttype] * cos_phi * cos_phi;
            double tq = Math.Tan(in_pt.y);
            double t = tq * tq;
            double con2 = 1.0 - this.m_Es[this.dsttype] * sin_phi * sin_phi;
            double n = m_arMajor[this.dsttype] / Math.Sqrt(con2);
            double ml = m_arMajor[this.dsttype] * this.mlfn(this.e0fn(this.m_Es[this.dsttype]),
                this.e1fn(this.m_Es[this.dsttype]), this.e2fn(this.m_Es[this.dsttype]), this.e3fn(this.m_Es[this.dsttype]), in_pt.y);

            out_pt.x = this.m_arScaleFactor[this.dsttype] * n * al * (1.0 + als /
                6.0 * (1.0 - t + c + als / 20.0 * (5.0 - 18.0 * t + t * t + 72.0 * c -
                58.0 * this.m_Esp[this.dsttype]))) + this.m_arFalseEasting[this.dsttype];
            out_pt.y = this.m_arScaleFactor[this.dsttype] * (ml - this.dst_m[this.dsttype] +
                n * tq * (als * (0.5 + als / 24.0 * (5.0 - t + 9.0 * c + 4.0 * c * c + als /
                30.0 * (61.0 - 58.0 * t + t * t + 600.0 * c - 330.0 * this.m_Esp[this.dsttype]))))) + this.m_arFalseNorthing[this.dsttype];
        }


        public void tm2geo(GeoPoint in_pt, GeoPoint out_pt)
        {
            int max_iter = 6;

            if (this.m_Ind[this.srctype] != 0)
            {
                double f = Math.Exp(in_pt.x / (m_arMajor[this.srctype] * this.m_arScaleFactor[this.srctype]));
                double g = 0.5 * (f - 1.0 / f);
                double temp = this.m_arLatCenter[this.srctype] + in_pt.y / (m_arMajor[this.srctype] * this.m_arScaleFactor[this.srctype]);
                double h = Math.Cos(temp);
                double con = Math.Sqrt((1.0 - h * h) / (1.0 + g * g));
                out_pt.y = asinz(con);

                if (temp < 0) out_pt.y *= -1;

                if ((g == 0) && (h == 0))
                {
                    out_pt.x = this.m_arLonCenter[this.srctype];
                }
                else
                {
                    out_pt.x = Math.Atan(g / h) + this.m_arLonCenter[this.srctype];
                }
            }

            in_pt.x -= this.m_arFalseEasting[this.srctype];
            in_pt.y -= this.m_arFalseNorthing[this.srctype];

            double con2 = (this.src_m[this.srctype] + in_pt.y / this.m_arScaleFactor[this.srctype]) / m_arMajor[this.srctype];
            double phi = con2;

            int i = 0;

            while (true)
            {
                double delta_Phi = ((con2 + this.e1fn(this.m_Es[this.srctype]) * Math.Sin(2.0 * phi) -
                    this.e2fn(this.m_Es[this.srctype]) * Math.Sin(4.0 * phi) + this.e3fn(this.m_Es[this.srctype]) *
                    Math.Sin(6.0 * phi)) / this.e0fn(this.m_Es[this.srctype])) - phi;
                phi = phi + delta_Phi;

                if (Math.Abs(delta_Phi) <= EPSLN) break;

                if (i >= max_iter)
                {
                    MessageBox.Show("무한대 에러");
                    break;
                }

                i++;
            }

            if (Math.Abs(phi) < (Math.PI / 2))
            {
                double sin_phi = Math.Sin(phi);
                double cos_phi = Math.Cos(phi);
                double tan_phi = Math.Tan(phi);
                double c = this.m_Esp[this.srctype] * cos_phi * cos_phi;
                double cs = c * c;
                double t = tan_phi * tan_phi;
                double ts = t * t;
                double cont = 1.0 - this.m_Es[this.srctype] * sin_phi * sin_phi;
                double n = m_arMajor[this.srctype] / Math.Sqrt(cont);
                double r = n * (1.0 - this.m_Es[this.srctype]) / cont;
                double d = in_pt.x / (n * this.m_arScaleFactor[this.srctype]);
                double ds = d * d;
                out_pt.y = phi - (n * tan_phi * ds / r) * (0.5 - ds / 24.0 *
                    (5.0 + 3.0 * t + 10.0 * c - 4.0 * cs - 9.0 * this.m_Esp[this.srctype] -
                    ds / 30.0 * (61.0 + 90.0 * t + 298.0 * c + 45.0 * ts - 252.0 * this.m_Esp[this.srctype] - 3.0 * cs)));
                out_pt.x = this.m_arLonCenter[this.srctype] + (d * (1.0 - ds / 6.0 *
                    (1.0 + 2.0 * t + c - ds / 20.0 * (5.0 - 2.0 * c + 28.0 * t - 3.0 * cs +
                    8.0 * this.m_Esp[this.srctype] + 24.0 * ts))) / cos_phi);
            }
            else
            {
                out_pt.y = Math.PI * 0.5 * Math.Sin(in_pt.y);
                out_pt.x = this.m_arLonCenter[this.srctype];
            }
            transform(srctype, dsttype, out_pt);
        }

        private double getDistancebyGeo(GeoPoint pt1, GeoPoint pt2)
        {
            double lat1 = this.D2R(pt1.y);
            double lon1 = this.D2R(pt1.x);
            double lat2 = this.D2R(pt2.y);
            double lon2 = this.D2R(pt2.x);

            double longitude = lon2 - lon1;
            double latitude = lat2 - lat1;

            double a = Math.Pow(Math.Sin(latitude / 2.0), 2) + Math.Cos(lat1) * Math.Cos(lat2) * Math.Pow(Math.Sin(longitude / 2.0), 2);
            return 6376.5 * 2.0 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1.0 - a));
        }

        private double getDistancebyKatec(GeoPoint pt1, GeoPoint pt2)
        {
            GeoTrans geo = new GeoTrans(KATEC, GEO);
            pt1 = geo.conv(pt1);
            pt2 = geo.conv(pt2);

            return this.getDistancebyGeo(pt1, pt2);
        }

        private double getTimebySec(double distance)
        {
            return Math.Round(3600 * distance / 4);
        }

        private long getTimebyMin(double distance)
        {
            return (long)(Math.Ceiling(this.getTimebySec(distance) / 60));
        }

        /**
         * convert between geodetic coordinates (longitude, latitude, height)
         * and gecentric coordinates (X, Y, Z)
         * ported from Proj 4.9.9 geocent.c
        */

        // following constants from geocent.c
        private double HALF_PI = 0.5 * Math.PI;
        private double COS_67P5 = 0.38268343236508977;  /* cosine of 67.5 degrees */
        private double AD_C = 1.0026000;
        /* Toms region 1 constant */

        private void transform(int srctype, int dsttype, GeoPoint point)
        {
            if (srctype == dsttype)
                return;

            if (srctype != 0 || dsttype != 0)
            {
                // Convert to geocentric coordinates.
                geodetic_to_geocentric(srctype, point);

                // Convert between datums
                if (srctype != 0)
                {
                    geocentric_to_wgs84(point);
                }

                if (dsttype != 0)
                {
                    geocentric_from_wgs84(point);
                }

                // Convert back to geodetic coordinates
                geocentric_to_geodetic(dsttype, point);
            }
        }

        private bool geodetic_to_geocentric(int type, GeoPoint p)
        {

            /*
             * The function Convert_Geodetic_To_Geocentric converts geodetic coordinates
             * (latitude, longitude, and height) to geocentric coordinates (X, Y, Z),
             * according to the current ellipsoid parameters.
             *
             *    Latitude  : Geodetic latitude in radians                     (input)
             *    Longitude : Geodetic longitude in radians                    (input)
             *    Height    : Geodetic height, in meters                       (input)
             *    X         : Calculated Geocentric X coordinate, in meters    (output)
             *    Y         : Calculated Geocentric Y coordinate, in meters    (output)
             *    Z         : Calculated Geocentric Z coordinate, in meters    (output)
             *
             */

            double Longitude = p.x;
            double Latitude = p.y;
            double Height = p.z;
            double X;  // output
            double Y;
            double Z;

            bool Error_Code = false;  //  GEOCENT_NO_ERROR;
            double Rn;            /*  Earth radius at location  */
            double Sin_Lat;       /*  Math.sin(Latitude)  */
            double Sin2_Lat;      /*  Square of Math.sin(Latitude)  */
            double Cos_Lat;       /*  Math.cos(Latitude)  */

            /*
            ** Don't blow up if Latitude is just a little out of the value
            ** range as it may just be a rounding issue.  Also removed longitude
            ** test, it should be wrapped by Math.cos() and Math.sin().  NFW for PROJ.4, Sep/2001.
            */
            if (Latitude < -HALF_PI && Latitude > -1.001 * HALF_PI)
                Latitude = -HALF_PI;
            else if (Latitude > HALF_PI && Latitude < 1.001 * HALF_PI)
                Latitude = HALF_PI;
            else if ((Latitude < -HALF_PI) || (Latitude > HALF_PI))
            { /* Latitude out of range */
                return true;
            }

            /* no errors */
            if (Longitude > Math.PI)
                Longitude -= (2 * Math.PI);
            Sin_Lat = Math.Sin(Latitude);
            Cos_Lat = Math.Cos(Latitude);
            Sin2_Lat = Sin_Lat * Sin_Lat;
            Rn = this.m_arMajor[type] / (Math.Sqrt(1.0e0 - this.m_Es[type] * Sin2_Lat));
            X = (Rn + Height) * Cos_Lat * Math.Cos(Longitude);
            Y = (Rn + Height) * Cos_Lat * Math.Sin(Longitude);
            Z = ((Rn * (1 - this.m_Es[type])) + Height) * Sin_Lat;

            p.x = X;
            p.y = Y;
            p.z = Z;
            return false;
        } // cs_geodetic_to_geocentric()


        /** Convert_Geocentric_To_Geodetic
         * The method used here is derived from 'An Improved Algorithm for
         * Geocentric to Geodetic Coordinate Conversion', by Ralph Toms, Feb 1996
         */
        private void geocentric_to_geodetic(int type, GeoPoint p)
        {

            double X = p.x;
            double Y = p.y;
            double Z = p.z;
            double Longitude;
            double Latitude = 0.0;
            double Height;

            double W;        /* distance from Z axis */
            double W2;       /* square of distance from Z axis */
            double T0;       /* initial estimate of vertical component */
            double T1;       /* corrected estimate of vertical component */
            double S0;       /* initial estimate of horizontal component */
            double S1;       /* corrected estimate of horizontal component */
            double Sin_B0;   /* Math.sin(B0), B0 is estimate of Bowring aux doubleiable */
            double Sin3_B0;  /* cube of Math.sin(B0) */
            double Cos_B0;   /* Math.cos(B0) */
            double Sin_p1;   /* Math.sin(phi1), phi1 is estimated latitude */
            double Cos_p1;   /* Math.cos(phi1) */
            double Rn;       /* Earth radius at location */
            double Sum;      /* numerator of Math.cos(phi1) */
            bool At_Pole;  /* indicates location is in polar region */

            At_Pole = false;
            if (X != 0.0)
            {
                Longitude = Math.Atan2(Y, X);
            }
            else
            {
                if (Y > 0)
                {
                    Longitude = HALF_PI;
                }
                else if (Y < 0)
                {
                    Longitude = -HALF_PI;
                }
                else
                {
                    At_Pole = true;
                    Longitude = 0.0;
                    if (Z > 0.0)
                    {  /* north pole */
                        Latitude = HALF_PI;
                    }
                    else if (Z < 0.0)
                    {  /* south pole */
                        Latitude = -HALF_PI;
                    }
                    else
                    {  /* center of earth */
                        Latitude = HALF_PI;
                        Height = -this.m_arMinor[type];
                        return;
                    }
                }
            }
            W2 = X * X + Y * Y;
            W = Math.Sqrt(W2);
            T0 = Z * AD_C;
            S0 = Math.Sqrt(T0 * T0 + W2);
            Sin_B0 = T0 / S0;
            Cos_B0 = W / S0;
            Sin3_B0 = Sin_B0 * Sin_B0 * Sin_B0;
            T1 = Z + this.m_arMinor[type] * this.m_Esp[type] * Sin3_B0;
            Sum = W - this.m_arMajor[type] * this.m_Es[type] * Cos_B0 * Cos_B0 * Cos_B0;
            S1 = Math.Sqrt(T1 * T1 + Sum * Sum);
            Sin_p1 = T1 / S1;
            Cos_p1 = Sum / S1;
            Rn = this.m_arMajor[type] / Math.Sqrt(1.0 - this.m_Es[type] * Sin_p1 * Sin_p1);
            if (Cos_p1 >= COS_67P5)
            {
                Height = W / Cos_p1 - Rn;
            }
            else if (Cos_p1 <= -COS_67P5)
            {
                Height = W / -Cos_p1 - Rn;
            }
            else
            {
                Height = Z / Sin_p1 + Rn * (this.m_Es[type] - 1.0);
            }
            if (At_Pole == false)
            {
                Latitude = Math.Atan(Sin_p1 / Cos_p1);
            }

            p.x = Longitude;
            p.y = Latitude;
            p.z = Height;
            return;
        } // cs_geocentric_to_geodetic()



        /****************************************************************/
        // pj_geocentic_to_wgs84(defn, p )
        //  defn = coordinate system definition,
        //  p = point to transform in geocentric coordinates (x,y,z)
        private void geocentric_to_wgs84(GeoPoint p)
        {

            //if( defn.datum_type == PJD_3PARAM )
            {
                // if( x[io] == HUGE_VAL )
                //    continue;
                p.x += this.datum_params[0];
                p.y += this.datum_params[1];
                p.z += this.datum_params[2];

            }
        } // cs_geocentric_to_wgs84

        /****************************************************************/
        // pj_geocentic_from_wgs84()
        //  coordinate system definition,
        //  point to transform in geocentric coordinates (x,y,z)
        private void geocentric_from_wgs84(GeoPoint p)
        {

            //if( defn.datum_type == PJD_3PARAM )
            {
                //if( x[io] == HUGE_VAL )
                //    continue;
                p.x -= this.datum_params[0];
                p.y -= this.datum_params[1];
                p.z -= this.datum_params[2];

            }
        } //cs_geocentric_from_wgs84()
    }
}
