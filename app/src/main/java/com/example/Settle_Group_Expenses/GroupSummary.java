package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupSummary extends AppCompatActivity {

    public static DatabaseHelper myDB;
    public static ArrayList<String> ForWhomNameList, ForWhomAmountList;
    TextView tv_group_name, tv_group_currency, tv_group_destination, tv_group_description;
    String groupId, groupName;
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
        }
        setContentView(R.layout.activity_group_summary);
        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        ForWhomNameList=new ArrayList<String>();
        ForWhomAmountList=new ArrayList<String>();
        tv_group_name=(TextView)findViewById(R.id.tv_group_name);
        tv_group_currency=(TextView)findViewById(R.id.tv_group_currency);
        tv_group_destination=(TextView)findViewById(R.id.tv_group_destination);
        tv_group_description=(TextView)findViewById(R.id.tv_group_description);

        getExpense();
    }
    protected void getExpense(){
        Cursor cursor=myDB.getGroupDetails(groupId);
        if (cursor.getCount()==0){
        }
        else {
            String name, currency, destination, description;
            cursor.moveToFirst();
            name=cursor.getString(1);
            currency=cursor.getString(2);
            destination=cursor.getString(3);
            description=cursor.getString(4);
            tv_group_name.setText("Group name : "+name);
            tv_group_currency.setText("Currency : "+currency);
            tv_group_destination.setText("Destination : "+destination);
            tv_group_description.setText("Description :"+description);
        }
    }
}