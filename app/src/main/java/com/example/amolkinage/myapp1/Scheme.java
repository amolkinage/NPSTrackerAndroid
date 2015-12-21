package com.example.amolkinage.myapp1;

/**
 * Created by amolkinage on 21/11/15.
 */
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.VelocityTracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import com.example.amolkinage.*;

public class Scheme {
    public Scheme()
    {
        m_name = "";
        m_code = 0;
        m_NAV  = 0;
        m_bUpateNewNAV = false;

        ResetCalculations();
    }
    public Scheme(String name,int code, double nav)
    {
        m_name= name;
        m_code = code;
        m_NAV = nav;
        m_bUpateNewNAV=false;
        m_DeletedTransactions = new Vector<>(2);
        ResetCalculations();
    }

    public int ReadFromDB(SQLiteDatabase db)
    {
        String strStmt= "Select * from Transactions where SchemeCode=";
        strStmt += m_code;

        Cursor sqlstmt = db.rawQuery(strStmt,null);

        m_Transactions = new Vector<>(sqlstmt.getCount());

        if(sqlstmt.getCount()==0)
        {
            sqlstmt.close();
            return -1;
        }

        sqlstmt.moveToFirst();

        do
        {
            int SchemeCode   = sqlstmt.getInt(0);
            String strDate   = sqlstmt.getString(1);
            double amount	 = sqlstmt.getDouble(2);
            double nav 		 = sqlstmt.getDouble(3);
            double units	 = sqlstmt.getDouble(4);
            int _id          = sqlstmt.getInt(5);



            Date date = null;

            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                date = df.parse(strDate);
            }
            catch(Exception ex)
            {
                Log.i("Date Exception",ex.getMessage());
            }

            Transaction trans = new Transaction(SchemeCode,date,amount,nav,units,_id);
            m_Transactions.add(trans);

            sqlstmt.moveToNext();
        }
        while(sqlstmt.getPosition() < sqlstmt.getCount());


        sqlstmt.close();

        ReadLatestNAV();

        DoCalculations();

        return 0;
    }

    public double GetAmoutInvested(){return m_AmtInvested;}
    public double GetMarketValue(){return m_CurrMarketValue;}
    public double GetGain(){return m_Gain;}
    public String getSchemeName(){return m_name;};
    public double getCAGR(){return m_CAGR;};
    public int getNumTransactions(){return (int)m_Transactions.size();}
    public Transaction getTransaction(int index){return m_Transactions.elementAt(index);}
    public int GetSchemeCode(){return m_code;}

    public int addTransaction(Transaction newTrans)
    {
        if(m_Transactions.add(newTrans))
            return 0;
        else
            return -1;
    }

    public void ResetCalculations()
    {
        m_AmtInvested = 0;
        m_CurrMarketValue=0;
        m_TotalUnits=0;
        m_AverageNAV=0;
        m_Gain=0;
        m_CAGR=0;
    }
    public void DoCalculations()
    {
        ResetCalculations();

        Vector<xirrTransaction>  xTransactions= new Vector<xirrTransaction>(2);

        for(int ii=0; ii < m_Transactions.size(); ii++)
        {
            m_AmtInvested += m_Transactions.elementAt(ii).GetAmount();
            m_TotalUnits  += m_Transactions.elementAt(ii).GetUnits();
            m_AverageNAV  += m_Transactions.elementAt(ii).GetNAV();

            if(m_Transactions.elementAt(ii).GetAmount() > 0)
            {
                xirrTransaction xtrans= new xirrTransaction();
                xtrans.amount = 0 - m_Transactions.elementAt(ii).GetAmount();
                xtrans.date = m_Transactions.elementAt(ii).GetDate();

                xTransactions.add(xtrans);
            }
        }

        m_AverageNAV = m_AverageNAV/m_Transactions.size() ;
        m_CurrMarketValue = m_TotalUnits * m_NAV;
        m_Gain = (100*(m_CurrMarketValue - m_AmtInvested))/m_AmtInvested;


        xirrTransaction latestValue = new xirrTransaction();
        Date today = new Date();

        latestValue.amount = m_CurrMarketValue;
        latestValue.date = today;

        xTransactions.add(latestValue);
        m_CAGR = xirrTransaction.CalculateXIRR(xTransactions, 0.01)*100;
    }

    public int ReadLatestNAV()
    {
        double latestNAV = NPSNAV.GetLatestNAV(m_code);

        if(0 != latestNAV && Math.abs(latestNAV-m_NAV) > 0.001)
        {
            m_NAV = latestNAV;
            m_bUpateNewNAV = true;
        }
        return  0;
    }

    public Vector<xirrTransaction> GetxirrTransactions()
    {
        Vector<xirrTransaction> xirrTrans = new Vector<xirrTransaction>(2);

        for(int ii=0;ii<m_Transactions.size(); ii++)
        {
            if(m_Transactions.elementAt(ii).GetAmount() != 0)
            {
                xirrTransaction xtrans = new xirrTransaction();
                xtrans.amount = 0 - m_Transactions.elementAt(ii).GetAmount();
                xtrans.date = m_Transactions.elementAt(ii).GetDate();
                xirrTrans.add(xtrans);
            }
        }

        return xirrTrans;
    }

    public void UpdateDB(SQLiteDatabase db)
    {
        try {
            for(int index=0; index<m_Transactions.size();index++)
            {
                boolean bUpdatePending = m_Transactions.elementAt(index).isUpdatePending();

                if(bUpdatePending)
                {
                    m_Transactions.elementAt(index).UpdateTransactionInDB(db);
                }
                else if(m_Transactions.elementAt(index).isNew())
                {
                    m_Transactions.elementAt(index).AddTransactionInDB(db);
                }
            }

            for(int index=0; index<m_DeletedTransactions.size();index++)
            {
                int _id = m_DeletedTransactions.elementAt(index).GetId();
                db.delete("Transactions","_id="+_id,null);
            }

            if (true == m_bUpateNewNAV) {
                //Update NAV
                Date today = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = df.format(today);

                ContentValues updValues = new ContentValues();
                updValues.put("NAVDate",strDate);
                updValues.put("NAV",m_NAV);
                db.update("Schemes",updValues,"Code="+m_code,null);

                //Add to NAV History;
                ContentValues values = new ContentValues();
                values.put("SchemeCode",m_code);
                values.put("Date",strDate);
                values.put("NAV", m_NAV);
                try {
                    long err = db.insertOrThrow("NAVHistory",null,values);
                }
                catch(SQLException e)
                {
                    Log.d("Failed ",e.getMessage());
                }
            }
        }
        catch(Exception e)
        {
            Log.d("Exception ",e.getMessage());
        }

    }

    public void DeleteTransaction(Transaction transaction,int index)
    {
        if(!transaction.isNew())
            m_DeletedTransactions.add(transaction);

        m_Transactions.remove(index);

        //ResetCalculations();
        //DoCalculations();
    }

    private String m_name;
    private int    m_code;
    private double m_NAV;
    private double m_AmtInvested;
    private double m_CurrMarketValue;
    private double m_TotalUnits;
    private double m_AverageNAV;
    private double m_Gain;
    private double m_CAGR;

    private boolean  m_bUpateNewNAV;

    private Vector<Transaction> m_Transactions;

    private Vector<Transaction> m_DeletedTransactions;
}
