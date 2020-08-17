package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MMExpenseSummary extends AppCompatActivity {
    public static DatabaseHelper myDB;
    public static ArrayList<String> WhoPaidNameList, ForWhomNameList;
    public static ArrayList<Double> WhoPaidAmountList, ForWhomAmountList;
    public static TextView tv_who_paid, tv_who_paid_amount,tv_for_whom, tv_for_whom_amount, tv_purpose, tv_amount, tv_datetime;
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
            expid=bundle.getString("mm_expID");
            Currency=bundle.getString("CURRENCY");
        }
        setContentView(R.layout.activity_mm_expense_summary);
        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        WhoPaidNameList=new ArrayList<String>();
        WhoPaidAmountList=new ArrayList<Double>();
        ForWhomNameList=new ArrayList<String>();
        ForWhomAmountList=new ArrayList<Double>();
        tv_who_paid=(TextView)findViewById(R.id.tv_who_paid);
        tv_who_paid_amount=(TextView)findViewById(R.id.tv_who_paid_amount);
        tv_for_whom=(TextView)findViewById(R.id.tv_for_whom);
        tv_for_whom_amount=(TextView)findViewById(R.id.tv_for_whom_amount);
        tv_purpose=(TextView)findViewById(R.id.tv_purpose);
        tv_amount=(TextView)findViewById(R.id.tv_amount);
        tv_datetime=(TextView)findViewById(R.id.tv_datetime);
        getMMExpense();
        getMMExpenseBridge();

    }
    protected void getMMExpense(){
        Cursor cursor=myDB.getMMExpenses(expid);
        if (cursor.getCount()==0){
        }
        else {
            String  purpose, date, time;
            Integer amount;
            cursor.moveToFirst();
            amount=cursor.getInt(2);
            purpose=cursor.getString(3);
            date=cursor.getString(4);
            time=cursor.getString(5);
            tv_amount.setText("Total amount : "+amount.toString()+" "+Currency);
            tv_purpose.setText("Purpose : "+purpose);
            tv_datetime.setText("On "+date+" at "+time);
        }
    }
    protected void getMMExpenseBridge(){
        Cursor cursor=myDB.getMMExpBridge(expid);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String who_paid=cursor.getString(1);
                double amount1=cursor.getDouble(2);
                String for_whom=cursor.getString(3);
                double amount2=cursor.getDouble(4);
                if (amount1 != 0){
                    WhoPaidNameList.add(who_paid);
                    WhoPaidAmountList.add(amount1);
                }
                if (amount2 != 0){
                    ForWhomNameList.add(for_whom);
                    ForWhomAmountList.add(amount2);
                }
            }while (cursor.moveToNext());
        }
        for (int i=0;i<WhoPaidNameList.size();i++){
            tv_who_paid.append(WhoPaidNameList.get(i)+"\n");
        }
        for (int i=0;i<WhoPaidAmountList.size();i++){
            tv_who_paid_amount.append(WhoPaidAmountList.get(i)+" "+Currency+"\n");
        }
        for (int i=0;i<ForWhomNameList.size();i++){
            tv_for_whom.append(ForWhomNameList.get(i)+"\n");
        }
        for (int i=0;i<ForWhomAmountList.size();i++){
            tv_for_whom_amount.append(ForWhomAmountList.get(i)+" "+Currency+"\n");
        }
    }
}