package com.example.amolkinage.myapp1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by amolkinage on 30/11/15.
 */
public class TransactionEditDialogFragment extends DialogFragment {

    Transaction mTransaction;
    boolean     bNew=false;

    public void setNewTransaction(Transaction trans){
        mTransaction = trans;
        bNew = true;
    }

    public void setEditTransaction(Transaction trans){
        mTransaction = trans;
        bNew = false;
    }


    public interface  TransactionEditListner
    {
        public void onTransactionEdit(Transaction trans);
    }

    TransactionEditListner mListner;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListner = (TransactionEditListner) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(bNew)
            builder.setTitle("New Transaction");
        else
            builder.setTitle("Edit Transaction");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = inflater.inflate(R.layout.activity_transaction_edit, null);

        builder.setView(v)
            // Add action buttons
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // sign in the user ...
                    Log.d("OK", "Clicked");
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    TextView txtDate   = (TextView)  v.findViewById(R.id.textDate);
                    EditText edtAmount = (EditText)  v.findViewById(R.id.editAmount);
                    EditText edtPrice  = (EditText)  v.findViewById(R.id.editPrice);
                    EditText edtUnits  = (EditText)  v.findViewById(R.id.editUnits);

                    try {
                        Date dt       = df.parse(txtDate.getText().toString());
                        double amount = Double.parseDouble(edtAmount.getText().toString());
                        double price  = Double.parseDouble(edtPrice.getText().toString());
                        double units  = Double.parseDouble(edtUnits.getText().toString());

                        if(bNew) {
                            mTransaction.setDate(dt);
                            mTransaction.setAmount(amount);
                            mTransaction.setNAV(price);
                            mTransaction.setUnits(units);
                        }
                        else
                        {
                            Transaction edtTrans = new Transaction(mTransaction.GetSchemeCode(),dt,amount,price,units,mTransaction.GetId());
                            mTransaction.UpdateTransaction(edtTrans);
                            mTransaction.setUpdatePending();
                        }
                        mListner.onTransactionEdit(mTransaction);

                    }
                    catch(Exception e)
                    {

                    }
                }
            })
            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    TransactionEditDialogFragment.this.getDialog().cancel();
                }
            });

        final TextView txtDate   = (TextView) v.findViewById(R.id.textDate);
        final EditText edtAmount = (EditText) v.findViewById(R.id.editAmount);
        final EditText edtPrice  = (EditText) v.findViewById(R.id.editPrice);
        final EditText edtUnits  = (EditText) v.findViewById(R.id.editUnits);

        if(!bNew){
            txtDate.setText(mTransaction.GetStrDate());
            edtAmount.setText(String.format("%4f", mTransaction.GetAmount()));
            edtPrice.setText(String.format("%4f", mTransaction.GetNAV()));
            edtUnits.setText(String.format("%4f",mTransaction.GetUnits()));
        }

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String strMonth;
                                switch (monthOfYear)
                                {
                                    case 0:
                                        strMonth="Jan";
                                        break;
                                    case 1:
                                        strMonth="Feb";
                                        break;
                                    case 2:
                                        strMonth="Mar";
                                        break;
                                    case 3:
                                        strMonth="Apr";
                                        break;
                                    case 4:
                                        strMonth="May";
                                        break;
                                    case 5:
                                        strMonth="Jun";
                                        break;
                                    case 6:
                                        strMonth="Jul";
                                        break;
                                    case 7:
                                        strMonth="Aug";
                                        break;
                                    case 8:
                                        strMonth="Sep";
                                        break;
                                    case 9:
                                        strMonth="Oct";
                                        break;
                                    case 10:
                                        strMonth="Nov";
                                        break;
                                    case 11:
                                        strMonth="Dec";
                                        break;
                                    default:
                                        strMonth="Jan";
                                        break;
                                }
                                String str = String.format("%2d-%s-%4d",dayOfMonth,strMonth,year);
                                txtDate.setText(str);
                            }
                        }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                dpd.show();
            }
        });


        return builder.create();
    }
}
