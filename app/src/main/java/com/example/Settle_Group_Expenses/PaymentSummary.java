package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class PaymentSummary  extends AppCompatActivity {

    public static DatabaseHelper myDB;
    TextView tv_who_paid, tv_to_whom,tv_purpose, tv_amount, tv_datetime;
    String groupId, groupName, expid, Currency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getIntent().getExtras();
        if(bundle==null)
        {
            return;
        }
        else
        {
            groupId=bundle.getString("groupID");
            groupName=bundle.getString("groupNAME");
            expid=bundle.getString("expID");
            Currency=bundle.getString("CURRENCY");
        }
        setContentView(R.layout.activity_payment_summary);
        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        tv_who_paid=(TextView)findViewById(R.id.tv_who_paid);
        tv_to_whom=(TextView)findViewById(R.id.tv_to_whom);
        tv_purpose=(TextView)findViewById(R.id.tv_purpose);
        tv_amount=(TextView)findViewById(R.id.tv_amount);
        tv_datetime=(TextView)findViewById(R.id.tv_datetime);
        getPayment();

    }
    protected void getPayment(){
        Cursor cursor=myDB.getPaymentDetails(expid);
        if (cursor.getCount()==0){
        }
        else {
            String who_paid, to_whom, purpose, date, time;
            double amount;
            cursor.moveToFirst();
            amount=cursor.getDouble(1);
            purpose=cursor.getString(2);
            who_paid=cursor.getString(3);
            to_whom=cursor.getString(4);
            date=cursor.getString(5);
            time=cursor.getString(6);
            tv_who_paid.setText("Paid by : "+who_paid);
            tv_to_whom.setText("To : "+to_whom);
            tv_amount.setText("Amount : "+amount+" "+Currency);
            tv_purpose.setText("Purpose : "+purpose);
            tv_datetime.setText("On "+date+" at "+time);
        }
    }
}