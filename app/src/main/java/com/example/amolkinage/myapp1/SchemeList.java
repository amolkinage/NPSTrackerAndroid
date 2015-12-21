package com.example.amolkinage.myapp1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class SchemeList extends AppCompatActivity {

    int Position = 0;
    Portfolio portfolio;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Position = intent.getIntExtra("Portfolio", 0);

        portfolio = MainActivity.npsDb.Portfolios.elementAt(Position);

        setTitle(portfolio.getName());

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PorfolioDetailsAdapter(portfolio);
        recyclerView.setAdapter(mAdapter);
    }


    public class PorfolioDetailsAdapter extends RecyclerView.Adapter<PorfolioDetailsAdapter.PortfolioDetailViewHolder>
    {
        private Portfolio portfolio;

        public PorfolioDetailsAdapter(Portfolio portf)
        {
            portfolio = portf;
        }

        @Override
        public int getItemCount()
        {
            return portfolio.getNumOfSchemes();
        }

        @Override
        public void onBindViewHolder(PortfolioDetailViewHolder viewHolder,int i){
            viewHolder.currentItem = i;
            viewHolder.currentPortfolio = Position;
            Scheme scheme = portfolio.m_Schemes.elementAt(i);
            DecimalFormat format = new DecimalFormat("##,##,###.##");

            if(scheme.GetMarketValue() >= scheme.GetAmoutInvested())
            {
                viewHolder.vSchemeMarketValue.setTextColor(Color.GREEN);
            }
            else
            {
                viewHolder.vSchemeMarketValue.setTextColor(Color.RED);
            }

            if(scheme.getCAGR() > 0)
                viewHolder.vSchemeCAGR.setTextColor(Color.GREEN);
            else
                viewHolder.vSchemeCAGR.setTextColor(Color.RED);

            if(scheme.GetGain() > 0)
                viewHolder.vSchemeAbsRet.setTextColor(Color.GREEN);
            else
                viewHolder.vSchemeAbsRet.setTextColor(Color.RED);

            viewHolder.vSchemeName.setText(scheme.getSchemeName());
            viewHolder.vSchemeMarketValue.setText(format.format(scheme.GetMarketValue()));
            viewHolder.vSchemeInvestValue.setText(format.format(scheme.GetAmoutInvested()));
            viewHolder.vSchemeCAGR.setText(format.format(scheme.getCAGR())+"%");
            viewHolder.vSchemeAbsRet.setText(format.format(scheme.GetGain())+"%");
        }

        @Override
        public PortfolioDetailViewHolder onCreateViewHolder(ViewGroup viewGroup,int i){
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_scheme_list, viewGroup, false);

            return new PortfolioDetailViewHolder(itemView);
        }

        public class PortfolioDetailViewHolder extends RecyclerView.ViewHolder{
            protected TextView vSchemeName;
            protected TextView vSchemeMarketValue;
            protected TextView vSchemeInvestValue;
            protected TextView vSchemeCAGR;
            protected TextView vSchemeAbsRet;
            public    int  currentItem;
            public    int  currentPortfolio;
            public View view;

            public PortfolioDetailViewHolder(View v)
            {
                super(v);

                vSchemeName          = (TextView) v.findViewById(R.id.scheme_name);
                vSchemeMarketValue   = (TextView) v.findViewById(R.id.scheme_marketvalue);
                vSchemeInvestValue   = (TextView) v.findViewById(R.id.scheme_investValue);
                vSchemeCAGR          = (TextView) v.findViewById(R.id.scheme_CAGR);
                vSchemeAbsRet        = (TextView) v.findViewById(R.id.scheme_absRet);

                view = v;
                view.setOnClickListener(new View.OnClickListener(){
                    @Override public void onClick(View v){
                        Intent intent = new Intent(view.getContext(),transaction_list.class);
                        intent.putExtra("Portfolio",currentPortfolio);
                        intent.putExtra("Scheme",currentItem);
                        startActivity(intent);
                    }
                });
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
