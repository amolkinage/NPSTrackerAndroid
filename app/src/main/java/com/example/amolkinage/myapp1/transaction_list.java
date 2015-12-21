package com.example.amolkinage.myapp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;

public class transaction_list extends AppCompatActivity implements  TransactionEditDialogFragment.TransactionEditListner
{

    Scheme scheme;
    int currentPortfolio;
    int currentScheme;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onTransactionEdit(Transaction trans) {
        if(trans.isNew()) {
            MainActivity.npsDb.addTransaction(currentPortfolio, currentScheme, trans);
        }
        MainActivity.npsDb.Portfolios.elementAt(currentPortfolio).DoCalculations();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TransactionEditDialogFragment dlg = new TransactionEditDialogFragment();
                Transaction newTrans = new Transaction();
                newTrans.setNew();
                newTrans.setSchemeCode(scheme.GetSchemeCode());
                dlg.setNewTransaction(newTrans);
                dlg.show(getFragmentManager(),"New Transaction");

            }
        });

        Intent intent = getIntent();
        currentPortfolio = intent.getIntExtra("Portfolio",0);
        currentScheme    = intent.getIntExtra("Scheme", 0);

        Portfolio portfolio = MainActivity.npsDb.Portfolios.elementAt(currentPortfolio);
        scheme =    portfolio.m_Schemes.elementAt(currentScheme);


        recyclerView = (RecyclerView) findViewById(R.id.translist_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TransactionListAdapter(scheme);
        recyclerView.setAdapter(mAdapter);


    }

    public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionListViewHolder>
    {
        private Scheme scheme;

        public TransactionListAdapter(Scheme sch)
        {
            scheme = sch;
        }

        @Override
        public int getItemCount()
        {
            return scheme.getNumTransactions();
        }

        @Override
        public void onBindViewHolder(TransactionListViewHolder viewHolder,int i){
            final Transaction trans = scheme.getTransaction(i);
            final int Position = i;
            DecimalFormat amtformat = new DecimalFormat("##,##,###.##");
            DecimalFormat format = new DecimalFormat("##,##,###.####");

            viewHolder.vTransDate.setText(trans.GetStrDate());
            viewHolder.vTransAmount.setText(amtformat.format(trans.GetAmount()));
            viewHolder.vTransPrice.setText(format.format(trans.GetNAV()));
            viewHolder.vTransUnits.setText(format.format(trans.GetUnits()));

            viewHolder.imgEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionEditDialogFragment dlg = new TransactionEditDialogFragment();
                    dlg.setEditTransaction(trans);
                    dlg.show(getFragmentManager(), "Edit Transaction");
                }
            });

            viewHolder.imgDelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scheme.DeleteTransaction(trans,Position);
                    MainActivity.npsDb.Portfolios.elementAt(currentPortfolio).DoCalculations();
                    mAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public TransactionListViewHolder onCreateViewHolder(ViewGroup viewGroup,int i){
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_transaction_list, viewGroup, false);

            return new TransactionListViewHolder(itemView);
        }

        public class TransactionListViewHolder extends RecyclerView.ViewHolder{
            protected TextView vTransDate;
            protected TextView vTransAmount;
            protected TextView vTransPrice;
            protected TextView vTransUnits;
            protected ImageButton imgEditBtn;
            protected ImageButton imgDelBtn;


            public TransactionListViewHolder(View v)
            {
                super(v);

                vTransDate     = (TextView) v.findViewById(R.id.date);
                vTransAmount   = (TextView) v.findViewById(R.id.amount);
                vTransPrice    = (TextView) v.findViewById(R.id.price);
                vTransUnits    = (TextView) v.findViewById(R.id.units);

                imgEditBtn     = (ImageButton) v.findViewById(R.id.imageButtonEdit);
                imgDelBtn      = (ImageButton) v.findViewById(R.id.imageButtonDelete);
            }
        }
    }
}
