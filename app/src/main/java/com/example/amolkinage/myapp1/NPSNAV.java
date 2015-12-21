package com.example.amolkinage.myapp1;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amolkinage on 22/11/15.
 */
public class NPSNAV {

    public static String strHTML;
    public static String strNAVHTML;

    public static boolean GetData()
    {
        boolean bRetStatus = false;

        try {
            URL url = new URL("http://www.hdfcpension.com/about-hdfc-pmc/nav/");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = in.read()) != -1) {
                sb.append((char) ch);
            }
            strHTML = sb.toString();

            bRetStatus = true;
        }
        catch(Exception ex)
        {
            Log.i("NAV Exception", ex.getMessage());
        }
        return bRetStatus;
    }

    public static double GetLatestNAV(int schemeCode)
    {
        boolean bStatus = GetData();

        if(false==bStatus)
            return 0;

        String Scheme,Tier;

        switch(schemeCode)
        {
            case 1:
                Scheme = "HDFC Pension Fund Scheme E -Tier I";
                break;
            case 2:
                Scheme = "HDFC Pension Fund Scheme C -Tier I";
                break;
            case 3:
                Scheme = "HDFC Pension Fund Scheme G -Tier I";
                break;
            case 4:
                Scheme = "HDFC Pension Fund Scheme E -Tier II";
                break;
            case 5:
                Scheme = "HDFC Pension Fund Scheme C -Tier II";
                break;
            case 6:
                Scheme = "HDFC Pension Fund Scheme G -Tier II";
                break;
            default:
                return (float)0;
        }

        double fNAV = (float)0.0;

        int StartIndex = strHTML.indexOf("id=\"div_dailyNav\"");
        if(StartIndex != -1)
        {
            int EndIndex = strHTML.indexOf("</div>",StartIndex);

            if(-1 != EndIndex)
            {
                strNAVHTML = strHTML.substring(StartIndex - 5);

                String strNAV = ReadNAV(Scheme);

                fNAV = Double.parseDouble(strNAV);
            }
        }

        return fNAV;
    }

    private static String ReadNAV(String Scheme)
    {
        int startfound = strNAVHTML.indexOf(Scheme);
        int endfound;

        if (startfound != -1)
        {
            endfound = strNAVHTML.indexOf("</p>",startfound);

            if(endfound != -1)
            {
                String strTemp2 = strNAVHTML.substring(startfound, endfound - 1);

                startfound = strTemp2.indexOf("class=");
                if (startfound != -1)
                {
                    String strTemp3 = strTemp2.substring(startfound+5);

                    startfound = strTemp3.indexOf(">");

                    if (startfound != -1)
                    {
                        endfound = strTemp3.indexOf("<");

                        if(endfound != -1)
                        {
                            String strNAV = strTemp3.substring(startfound+1,endfound);

                            return strNAV;
                        }
                    }
                }
            }
        }
        return "0.0";
    }
}
