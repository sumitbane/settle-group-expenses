package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.NameList;

import java.util.ArrayList;

public class Summary extends AppCompatActivity {

    public static String groupId,groupName;
    public static DatabaseHelper myDB;
    public static ArrayList MemberList;
    public static ArrayList DebtList;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

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
        myDB=new DatabaseHelper(this);
        pieChart = findViewById(R.id.pieChart);
        createPie();
    }
    protected void createPie(){
        MemberList=new ArrayList();
        DebtList=new ArrayList();

        int i=0;
        Cursor cursor=myDB.getMemberName(groupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String name=cursor.getString(1);
                float debts=cursor.getFloat(4);
                MemberList.add(name);
                DebtList.add(new Entry(debts, i));
                i++;
            }while (cursor.moveToNext());
        }
        PieDataSet dataSet = new PieDataSet(DebtList, "Members");
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);
    }
}