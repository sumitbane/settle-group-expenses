package com.example.Settle_Group_Expenses;



import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomMMExpenseListAdapter extends ArrayAdapter {
    ArrayList<String> ArrMMExpenseID, ArrMMExpensePurpose, ArrMMExpenseAmt;
    Activity context;
    public static String GroupId, GroupName, Currency;
    public static DatabaseHelper myDB;

    public CustomMMExpenseListAdapter(Activity context, String GroupId, String GroupName, ArrayList<String> ArrExpenseID,
                                      ArrayList<String> ArrExpensePurpose, ArrayList<String> ArrExpenseAmt, String Currency) {
        super(context,R.layout.custom_mm_expenselist,ArrExpenseID);
        this.context = context;
        this.GroupId = GroupId;
        this.GroupName = GroupName;
        this.ArrMMExpenseID = ArrExpenseID;
        this.ArrMMExpensePurpose = ArrExpensePurpose;
        this.ArrMMExpenseAmt = ArrExpenseAmt;
        this.Currency=Currency;
    }
    public View getView(final int position, final View convertView, ViewGroup parent) {
        myDB = new DatabaseHelper(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View ListViewItem = inflater.inflate(R.layout.custom_mm_expenselist, null, true);

        final TextView tv_mm_exp_purpose=(TextView)ListViewItem.findViewById(R.id.tv_mm_exp_purpose);
        final TextView tv_mm_exp_amt=(TextView)ListViewItem.findViewById(R.id.tv_mm_exp_amt);
        final TextView tv_mm_exp_currency=(TextView)ListViewItem.findViewById(R.id.tv_mm_exp_currency);
        LinearLayout LL_mm_expenses=(LinearLayout)ListViewItem.findViewById(R.id.LL_mm_expenses);

        tv_mm_exp_purpose.setText(ArrMMExpensePurpose.get(position));
        Log.d("zxcv",""+Currency);
        tv_mm_exp_amt.setText(ArrMMExpenseAmt.get(position));
        tv_mm_exp_currency.setText(Currency);

        LL_mm_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MMExpenseSummary.class);
                intent.putExtra("groupID",GroupId);
                intent.putExtra("groupNAME", GroupName);
                intent.putExtra("mm_expID",ArrMMExpenseID.get(position));
                intent.putExtra("CURRENCY",Currency);
                context.startActivity(intent);
            }
        });
        return ListViewItem;
    }
}