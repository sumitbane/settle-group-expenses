package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomMMWhoPaidListAdapter extends ArrayAdapter {
    ArrayList<String> ArrMemberID, ArrMemberName;
    ArrayList<Double> ArrMemberAmount;
    Activity context;
    public static String GroupId, Currency, mm_expId;
    LinearLayout LL_mm;
    public static DatabaseHelper myDB;

    public CustomMMWhoPaidListAdapter(Activity context, String GroupId, ArrayList<String> ArrMemberName,
                                      ArrayList<Double> ArrMemberAmount, String Currency) {
        super(context,R.layout.custom_mm_who_paid_list,ArrMemberName);
        this.context = context;
        this.GroupId = GroupId;
        this.ArrMemberID = ArrMemberID;
        this.ArrMemberName = ArrMemberName;
        this.ArrMemberAmount=ArrMemberAmount;
        this.Currency=Currency;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View ListViewItem = inflater.inflate(R.layout.custom_mm_who_paid_list, null, true);

        final TextView tv_mm_who_paid=(TextView)ListViewItem.findViewById(R.id.tv_mm_who_paid);
        final TextView tv_mm_amount1=(TextView)ListViewItem.findViewById(R.id.tv_mm_amount1);
        final TextView tv_mm_currency1=(TextView)ListViewItem.findViewById(R.id.tv_mm_currency1);
        Button btn_edit_amt1=(Button)ListViewItem.findViewById(R.id.btn_edit_amt1);
        Log.d("789",""+ArrMemberName);
        Log.d("987",""+ArrMemberAmount);
        tv_mm_who_paid.setText(ArrMemberName.get(position));
        tv_mm_amount1.setText(ArrMemberAmount.get(position).toString());
        tv_mm_currency1.setText(Currency);
        LL_mm=(LinearLayout)ListViewItem.findViewById(R.id.LL_mm_who_paid);

        tv_mm_who_paid.setText(ArrMemberName.get(position));
        tv_mm_currency1.setText(Currency);
        myDB=new DatabaseHelper(context);



        btn_edit_amt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMMLastExpId();
                Log.d("567",""+mm_expId);
                LayoutInflater rename_inflater= context.getLayoutInflater();
                View tView=rename_inflater.inflate(R.layout.edit_for_whom_amount,null,true);

                final EditText et_split_amount=(EditText)tView.findViewById(R.id.et_split_amount);
                Button btn_save_amount=(Button)tView.findViewById(R.id.btn_save_amount);

                AlertDialog.Builder alert_builder=new AlertDialog.Builder(context);
                alert_builder.setView(tView);
                alert_builder.setCancelable(true);

                final AlertDialog dialog=alert_builder.create();
                dialog.show();

                //et_split_amount.setText(Amount);

                btn_save_amount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String who_paid=ArrMemberName.get(position).replace("'","''");
                        String str_updateAmount=et_split_amount.getText().toString();
                        double updateAmount=0;

                        if (str_updateAmount.equals("")){
                            Toast.makeText(context, "Enter a valid amount ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            updateAmount=Double.parseDouble(str_updateAmount);
                            DecimalFormat df = new DecimalFormat(".##");
                            df.setRoundingMode(RoundingMode.DOWN);
                            Log.d("56789",""+mm_expId);
                            myDB.updateMMExpWhoPaidAmt(mm_expId, who_paid, Double.parseDouble(df.format(updateAmount)));
                            MultipleMembersExpenses.fillMemberinList1(context);
                            //Log.d("amount5",""+ArrMemberAmount.get(position));
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return ListViewItem;
    }
    protected static void getMMLastExpId(){
        Cursor cursor=myDB.getMMLastExpenseId();
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToLast();
            mm_expId=cursor.getString(0);
            Log.d("lastq",mm_expId);
        }
    }
}