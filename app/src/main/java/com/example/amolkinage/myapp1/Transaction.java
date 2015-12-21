package com.example.amolkinage.myapp1;

/**
 * Created by amolkinage on 21/11/15.
 */

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Transaction {
    public Transaction()
    {
        m_SchemeCode = 0;
        m_Amount = 0;
        m_NAV = 0;
        m_Units = 0;
        m_bUpdatePending=false;
        m_NewTransaction = false;
        m_id = 0;
    }
    public Transaction(int SchemeCode,Date date,double amount, double nav, double units,int _id)
    {
        m_SchemeCode = SchemeCode;
        m_Date = date;
        m_Amount = amount;
        m_NAV = nav;
        m_Units = units;
        m_bUpdatePending=false;
        m_NewTransaction = false;
        m_id = _id;
    }

    public Date GetDate(){return m_Date;}
    public double GetAmount(){return m_Amount;}
    public double GetNAV(){return m_NAV;}
    public double GetUnits(){return m_Units;}
    public int GetSchemeCode(){return m_SchemeCode;}
    public int GetId(){return m_id;}

    public String GetStrDate()
    {
        String strDate= new String();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyy");
        strDate = df.format(m_Date);
        return strDate;
    }
    public void setDate(Date Date)
    {
        m_Date = Date;
    }
    public void setAmount(double amount)
    {
        m_Amount=amount;
    }
    public void setNAV(double nav)
    {
        m_NAV=nav;
    }
    public void setUnits(double units)
    {
        m_Units=units;
    }
    public void setSchemeCode(int Code){m_SchemeCode=Code;}

    public void UpdateTransactionInDB(SQLiteDatabase db)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = df.format(m_Date);
        String strOldDate = df.format(m_OldDate);

        ContentValues updValues = new ContentValues();
        updValues.put("Date",strDate);
        updValues.put("Amount",m_Amount);
        updValues.put("NAV",m_NAV);
        updValues.put("Units",m_Units);

        String whereClause = new String();
        whereClause = "_id="+m_id;
        /*whereClause += "and Date='"+ strOldDate+"'";
        whereClause += "and Amount="+m_OldAmount;
        whereClause += "and NAV=" + m_OldNAV;
        whereClause += "and Units=" + m_OldUnits;*/

        db.update("Transactions",updValues,whereClause,null);
    }
    public void AddTransactionInDB(SQLiteDatabase db)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = df.format(m_Date);

        ContentValues newValues = new ContentValues();
        newValues.put("SchemeCode",m_SchemeCode);
        newValues.put("Date",strDate);
        newValues.put("Amount",m_Amount);
        newValues.put("NAV",m_NAV);
        newValues.put("Units",m_Units);

        try {
            db.insertOrThrow("Transactions", null, newValues);
        }
        catch(SQLException e)
        {
            Log.d("SQl Exception", e.getMessage());
        }
    }

    public void UpdateTransaction(Transaction updTrans)
    {
        m_OldDate = m_Date;
        m_OldAmount = m_Amount;
        m_OldNAV = m_NAV;
        m_OldUnits = m_Units;

        m_Date   = updTrans.GetDate();
        m_Amount = updTrans.GetAmount();
        m_NAV    = updTrans.GetNAV();
        m_Units  = updTrans.GetUnits();
    }
    public void setUpdatePending(){m_bUpdatePending=true;}
    public void setNew(){m_NewTransaction = true;}
    public boolean isUpdatePending(){return m_bUpdatePending;}
    public boolean isNew(){return m_NewTransaction;}

    private  int     m_id;
    private  int     m_SchemeCode;
    private  Date    m_Date;
    private  double  m_Amount;
    private  double  m_NAV;
    private  double  m_Units;
    private  boolean m_bUpdatePending;
    private  boolean m_NewTransaction;

    private  Date   m_OldDate;
    private  double m_OldAmount;
    private  double m_OldNAV;
    private  double m_OldUnits;
}
