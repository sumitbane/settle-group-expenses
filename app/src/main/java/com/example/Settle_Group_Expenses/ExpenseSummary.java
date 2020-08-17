package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpenseSummary extends AppCompatActivity {

    public static DatabaseHelper myDB;
    public static ArrayList<String> ForWhomNameList;
    public static ArrayList<Double> ForWhomAmountList;
    TextView tv_who_paid, tv_for_whom, tv_for_whom_amount,tv_purpose, tv_amount, tv_datetime;
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
        setContentView(R.layout.activity_expense_summary);
        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ForWhomNameList=new ArrayList<String>();
        ForWhomAmountList=new ArrayList<Double>();
        tv_who_paid=(TextView)findViewById(R.id.tv_who_paid);
        tv_for_whom=(TextView)findViewById(R.id.tv_for_whom);
        tv_for_whom_amount=(TextView)findViewById(R.id.tv_for_whom_amount);
        tv_purpose=(TextView)findViewById(R.id.tv_purpose);
        tv_amount=(TextView)findViewById(R.id.tv_amount);
        tv_datetime=(TextView)findViewById(R.id.tv_datetime);
        getExpense();
        getExpenseStatus();

    }
    protected void getExpense(){
        Cursor cursor=myDB.getExpenseDetails(expid);
        if (cursor.getCount()==0){
        }
        else {
            String who_paid, purpose, date, time;
            double amount;
            cursor.moveToFirst();
            who_paid=cursor.getString(2);
            amount=cursor.getDouble(3);
            purpose=cursor.getString(4);
            date=cursor.getString(5);
            time=cursor.getString(6);
            tv_who_paid.setText("Paid by : "+who_paid);
            tv_amount.setText("Total amount : "+amount+" "+Currency);
            tv_purpose.setText("Purpose : "+purpose);
            tv_datetime.setText("On "+date+" at "+time);
        }
    }

    protected void getExpenseStatus(){
        Cursor cursor=myDB.getExpStatus(expid);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String for_whom=cursor.getString(1);
                double amount=cursor.getDouble(2);
                if (amount != 0){
                    ForWhomNameList.add(for_whom);
                    ForWhomAmountList.add(amount);
                }
            }while (cursor.moveToNext());
        }
        for (int i=0;i<ForWhomNameList.size();i++){
            tv_for_whom.append(ForWhomNameList.get(i)+"\n");
        }
        for (int i=0;i<ForWhomAmountList.size();i++){
            tv_for_whom_amount.append(ForWhomAmountList.get(i)+" "+Currency+"\n");
        }
    }
}