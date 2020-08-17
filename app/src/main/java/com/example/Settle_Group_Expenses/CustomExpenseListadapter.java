package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomExpenseListadapter extends ArrayAdapter {
    ArrayList<String> ArrExpenseID, ArrExpensePurpose, ArrExpenseAmt, ArrWhoPaid;
    Activity context;
    public static String GroupId, GroupName, Currency;
    public static DatabaseHelper myDB;

    public CustomExpenseListadapter(Activity context, String GroupId, String GroupName,
                                    ArrayList<String> ArrExpenseID, ArrayList<String> ArrExpensePurpose,
                                    ArrayList<String> ArrExpenseAmt, ArrayList<String> ArrWhoPaid, String Currency) {

        super(context,R.layout.custom_expenselist,ArrExpenseID);
        this.context = context;
        this.GroupId = GroupId;
        this.GroupName = GroupName;
        this.ArrExpenseID = ArrExpenseID;
        this.ArrExpensePurpose = ArrExpensePurpose;
        this.ArrExpenseAmt = ArrExpenseAmt;
        this.ArrWhoPaid = ArrWhoPaid;
        this.Currency=Currency;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        myDB = new DatabaseHelper(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View ListViewItem = inflater.inflate(R.layout.custom_expenselist, null, true);

        final TextView tv_exp_purpose = (TextView) ListViewItem.findViewById(R.id.tv_exp_purpose);
        final TextView tv_who_paid = (TextView) ListViewItem.findViewById(R.id.tv_who_paid);
        final TextView tv_exp_amt = (TextView) ListViewItem.findViewById(R.id.tv_exp_amt);
        final TextView tv_exp_currency = (TextView) ListViewItem.findViewById(R.id.tv_exp_currency);
        LinearLayout LL_expenses=(LinearLayout)ListViewItem.findViewById(R.id.LL_expenses);

        tv_exp_purpose.setText(ArrExpensePurpose.get(position));
        tv_exp_amt.setText(ArrExpenseAmt.get(position));
        tv_who_paid.setText("paid by "+ArrWhoPaid.get(position));
        tv_exp_currency.setText(Currency);

        LL_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ExpenseSummary.class);
                intent.putExtra("groupID",GroupId);
                intent.putExtra("groupNAME", GroupName);
                intent.putExtra("expID",ArrExpenseID.get(position));
                intent.putExtra("CURRENCY",Currency);
                context.startActivity(intent);
            }
        });

        return ListViewItem;
    }
}