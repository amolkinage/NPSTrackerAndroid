/**
 * Created by amolkinage on 21/11/15.
 */

package com.example.amolkinage.myapp1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.*;

public class Portfolio {

    public Portfolio()
    {
        m_strName="";
        m_Code = 0;
        ResetCalculations();
        m_Schemes = new Vector<Scheme>(2);
    }

    public Portfolio(String name,int Code)
    {
        m_strName= name;
        m_Code = Code;
        ResetCalculations();
        m_Schemes = new Vector<Scheme>(2);
    }

    public void SetName(String name)
    {
        m_strName = name;
    }
    public void SetCode(int code)
    {
        m_Code = code;
    }

    public String getName() { return m_strName; };
    public double getAmountInvested(){return m_AmountInvested;};
    public double getMarketValue(){return m_MarketValue;}
    public double getGain(){return m_Gain;};
    public double getCAGR(){return m_CAGR;};

    public int getNumOfSchemes(){ return (int)m_Schemes.size();}
    //Scheme* getScheme(int index){return &m_Schemes[index];}

    int ReadFromDB(SQLiteDatabase db)
    {
        String str = "Select * from Schemes where PortfolioCode=";
        str = str + m_Code;

        Cursor rSet = db.rawQuery(str,null);

        m_Schemes = new Vector<>(rSet.getCount());

        rSet.moveToFirst();

        do {
            String name = rSet.getString(0);
            int Code    = rSet.getInt(1);
            double nav  = rSet.getDouble(3);

            Scheme sch = new Scheme(name,Code,nav);
            m_Schemes.add(sch);

            rSet.moveToNext();
        }
        while(rSet.getPosition() < rSet.getCount());

        for(int ii=0;ii<m_Schemes.size();ii++)
        {
            m_Schemes.elementAt(ii).ReadFromDB(db);
        }

        DoCalculations();
        rSet.close();
        return 0;
    }

    void UpdateDB(SQLiteDatabase db)
    {
        for(int index=0;index<m_Schemes.size();index++)
        {
            m_Schemes.elementAt(index).UpdateDB(db);
        }
    }

    public int DoCalculations()
    {
        Vector<xirrTransaction> xirrTransactions = new Vector<xirrTransaction>(2);

        ResetCalculations();

        for(int ii=0; ii < m_Schemes.size(); ii++)
        {
            m_Schemes.elementAt(ii).DoCalculations();
            m_AmountInvested += m_Schemes.elementAt(ii).GetAmoutInvested();
            m_MarketValue += m_Schemes.elementAt(ii).GetMarketValue();

            Vector<xirrTransaction> schemeTrans = m_Schemes.elementAt(ii).GetxirrTransactions();

            for(int jj=0; jj < schemeTrans.size(); jj++)
            {
                xirrTransactions.add(schemeTrans.elementAt(jj));
            }
        }
        m_Gain = (100*(m_MarketValue - m_AmountInvested))/m_AmountInvested;


        xirrTransaction latestValue = new xirrTransaction();

        Date today = new Date();

        latestValue.amount = m_MarketValue;
        latestValue.date = today;


        xirrTransactions.add(latestValue);
        m_CAGR = xirrTransaction.CalculateXIRR(xirrTransactions, 0.01)*100;


        return 0;
    }

    public int ResetCalculations()
    {
        m_AmountInvested = 0;
        m_MarketValue = 0;
        m_Gain = 0;
        m_CAGR = 0;

        return 0;
    }

    private String m_strName;
    private int    m_Code;
    private double m_AmountInvested;
    private double m_MarketValue;
    private double m_Gain;
    private double m_CAGR;

    Vector<Scheme> m_Schemes;
}
