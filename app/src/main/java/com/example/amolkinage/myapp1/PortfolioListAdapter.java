package com.example.amolkinage.myapp1;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by amolkinage on 26/11/15.
 */
public class PortfolioListAdapter extends BaseAdapter {

    /*********** Declare Used Variables *********/
    private Activity activity;
    //private Vector<Portfolio> data;
    private int mSelectedItem=0;
    private static LayoutInflater inflater=null;

    public PortfolioListAdapter(Activity a, int resourceId, Vector<Portfolio> d) {

        /********** Take passed values **********/
        activity = a;
        //data=d;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = a.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return MainActivity.npsDb.Portfolios.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.portfolio_list, null, true);
        }
        TextView txtPortfolioName = (TextView) view.findViewById(R.id.textPortfolioName);
        TextView txtMktValue      = (TextView) view.findViewById(R.id.textPortfolioMktValue);
        TextView txtCAGR          = (TextView) view.findViewById(R.id.textCAGR);
        TextView txtInvestValue   = (TextView) view.findViewById(R.id.textInvestValue);
        TextView txtAbsReturn = (TextView) view.findViewById(R.id.textAbsRet);

        Portfolio port =  MainActivity.npsDb.Portfolios.elementAt(position);

        DecimalFormat myFormat = new DecimalFormat("##,##,###.##");
        txtPortfolioName.setText(port.getName());
        txtMktValue.setText(myFormat.format(port.getMarketValue()));
        txtCAGR.setText(String.format("%.2f",port.getCAGR())+"%");
        txtInvestValue.setText(myFormat.format(port.getAmountInvested()));
        txtAbsReturn.setText(String.format("%.2f",port.getGain())+"%");

        if(port.getMarketValue() > port.getAmountInvested() )
        {
            txtMktValue.setTextColor(Color.GREEN);
        }
        else
        {
            txtMktValue.setTextColor(Color.RED);
        }

        if(port.getCAGR() >= 0)
        {
            txtCAGR.setTextColor(Color.GREEN);
        }
        else
        {
            txtCAGR.setTextColor(Color.RED);
        }

        if(port.getGain() >= 0)
        {
            txtAbsReturn.setTextColor(Color.GREEN);
        }
        else
        {
            txtAbsReturn.setTextColor(Color.RED);
        }

        if(mSelectedItem==position) {
            view.setBackgroundColor(Color.LTGRAY);
        }
        else
        {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    /*public void setData(Vector<Portfolio> newdata)
    {
        data = newdata;
    }*/

    public void setSelected(int position){
        mSelectedItem = position;
    }
}
