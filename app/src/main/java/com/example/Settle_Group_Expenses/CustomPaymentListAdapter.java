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

public class CustomPaymentListAdapter extends ArrayAdapter {
    ArrayList<String> ArrPaymentID, ArrPaymentPurpose, ArrPaymentAmt, ArrWhoPaid, ArrToWhomPaid;
    Activity context;
    public static String GroupId, GroupName, Currency;
    public static DatabaseHelper myDB;

    public CustomPaymentListAdapter(Activity context, String GroupId, String GroupName, ArrayList<String> ArrPaymentID, ArrayList<String>
            ArrPaymentPurpose, ArrayList<String> ArrPaymentAmt, ArrayList<String> ArrWhoPaid, ArrayList<String> ArrToWhomPaid,
                                    String Currency) {
        super(context,R.layout.custom_payment_list,ArrPaymentID);
        this.context = context;
        this.GroupId = GroupId;
        this.GroupName = GroupName;
        this.ArrPaymentID = ArrPaymentID;
        this.ArrPaymentPurpose = ArrPaymentPurpose;
        this.ArrPaymentAmt = ArrPaymentAmt;
        this.ArrWhoPaid = ArrWhoPaid;
        this.ArrToWhomPaid = ArrToWhomPaid;
        this.Currency=Currency;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        myDB = new DatabaseHelper(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View ListViewItem = inflater.inflate(R.layout.custom_payment_list, null, true);

        final TextView tv_ap_purpose = (TextView) ListViewItem.findViewById(R.id.tv_ap_purpose);
        final TextView tv_ap_amt = (TextView) ListViewItem.findViewById(R.id.tv_ap_amt);
        final TextView tv_ap_currency = (TextView) ListViewItem.findViewById(R.id.tv_ap_currency);
        final TextView tv_who_paid = (TextView) ListViewItem.findViewById(R.id.tv_who_paid);
        final TextView tv_to_whom = (TextView) ListViewItem.findViewById(R.id.tv_to_whom);
        LinearLayout LL_payments=(LinearLayout)ListViewItem.findViewById(R.id.LL_payments);

        Log.d("qwer1",""+ArrPaymentPurpose);

        tv_ap_purpose.setText(ArrPaymentPurpose.get(position));
        tv_ap_amt.setText(ArrPaymentAmt.get(position));
        tv_ap_currency.setText(Currency);
        tv_who_paid.setText(ArrWhoPaid.get(position));
        tv_to_whom.setText(ArrToWhomPaid.get(position));

        LL_payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,PaymentSummary.class);
                intent.putExtra("groupID",GroupId);
                intent.putExtra("groupNAME", GroupName);
                intent.putExtra("expID",ArrPaymentID.get(position));
                intent.putExtra("CURRENCY",Currency);
                context.startActivity(intent);
            }
        });

        return ListViewItem;
    }
}