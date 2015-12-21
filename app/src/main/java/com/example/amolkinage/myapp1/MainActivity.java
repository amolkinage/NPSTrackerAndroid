package com.example.amolkinage.myapp1;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;



public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    class ReadNAVTask extends AsyncTask<Void, Void, NPSData> {

        protected NPSData doInBackground(Void...param) {

            NPSData npsDbHelper = MainActivity.npsDb;
            try {
                npsDbHelper.createDataBase();
                npsDbHelper.openDataBase();
                npsDbHelper.readDatabase();
            }
            catch (Exception ex)
            {
                npsDbHelper = null;
            }
            return npsDbHelper;
        }



        protected void onPreExecute(){

        }
        protected  void onPostExecute(NPSData dbhelper){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        npsDb = new NPSData(this);

        recyclerView = (RecyclerView) findViewById(R.id.portfolio_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PorfolioListAdapter();
        recyclerView.setAdapter(mAdapter);

        Toast toast = Toast.makeText(getApplicationContext(),"Loading Database and reading latest NAV",Toast.LENGTH_SHORT);
        toast.show();
        new ReadNAVTask().execute();
    }

    public class PorfolioListAdapter extends RecyclerView.Adapter<PorfolioListAdapter.PortfolioListViewHolder>
    {

        @Override
        public int getItemCount()
        {
            return MainActivity.npsDb.Portfolios.size();
        }

        @Override
        public void onBindViewHolder(PortfolioListViewHolder viewHolder,int i){
            viewHolder.currentPortfolio = i;
            Portfolio portfolio = MainActivity.npsDb.Portfolios.elementAt(i);
            DecimalFormat format = new DecimalFormat("##,##,###.##");

            if(portfolio.getMarketValue() >= portfolio.getAmountInvested())
            {
                viewHolder.vPortfolioMarketValue.setTextColor(Color.GREEN);
            }
            else
            {
                viewHolder.vPortfolioMarketValue.setTextColor(Color.RED);
            }

            if(portfolio.getCAGR() > 0)
                viewHolder.vPortfolioCAGR.setTextColor(Color.GREEN);
            else
                viewHolder.vPortfolioCAGR.setTextColor(Color.RED);

            if(portfolio.getGain() > 0)
                viewHolder.vPortfolioAbsRet.setTextColor(Color.GREEN);
            else
                viewHolder.vPortfolioAbsRet.setTextColor(Color.RED);

            viewHolder.vPortfolioName.setText(portfolio.getName());
            viewHolder.vPortfolioMarketValue.setText(format.format(portfolio.getMarketValue()));
            viewHolder.vPortfolioInvestValue.setText(format.format(portfolio.getAmountInvested()));
            viewHolder.vPortfolioCAGR.setText(format.format(portfolio.getCAGR())+"%");
            viewHolder.vPortfolioAbsRet.setText(format.format(portfolio.getGain())+"%");
        }

        @Override
        public PortfolioListViewHolder onCreateViewHolder(ViewGroup viewGroup,int i){
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_main, viewGroup, false);

            return new PortfolioListViewHolder(itemView);
        }

        public class PortfolioListViewHolder extends RecyclerView.ViewHolder{
            protected TextView vPortfolioName;
            protected TextView vPortfolioMarketValue;
            protected TextView vPortfolioInvestValue;
            protected TextView vPortfolioCAGR;
            protected TextView vPortfolioAbsRet;

            public    int  currentPortfolio;
            public View view;

            public PortfolioListViewHolder(View v)
            {
                super(v);

                vPortfolioName          = (TextView) v.findViewById(R.id.portfolio_name);
                vPortfolioMarketValue   = (TextView) v.findViewById(R.id.portfolio_marketvalue);
                vPortfolioInvestValue   = (TextView) v.findViewById(R.id.portfolio_investValue);
                vPortfolioCAGR          = (TextView) v.findViewById(R.id.portfolio_CAGR);
                vPortfolioAbsRet        = (TextView) v.findViewById(R.id.portfolio_absRet);

                view = v;
                view.setOnClickListener(new View.OnClickListener(){
                    @Override public void onClick(View v){
                        Intent intent = new Intent(view.getContext(),SchemeList.class);
                        intent.putExtra("Portfolio",currentPortfolio);
                        startActivity(intent);
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        npsDb.saveDatabase();
        npsDb.close();
    }

    public Portfolio getPortfolio(int position)
    {
        return npsDb.Portfolios.elementAt(position);
    }
    public static NPSData npsDb=null;
}
