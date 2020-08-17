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

public class CustomSettleDebtsListAdapter extends ArrayAdapter {
    ArrayList<String> ArrWhoPaysList, ArrToWhomList;
    ArrayList<Double> ArrHowMuchList;
    Activity context;
    public static String GroupId, Currency;
    public static DatabaseHelper myDB;

    public CustomSettleDebtsListAdapter(Activity context, String GroupId, ArrayList<String> ArrWhoPaysList,
                                      ArrayList<Double> ArrHowMuchList, ArrayList<String> ArrToWhomList, String Currency) {
        super(context,R.layout.custom_settle_debts_list,ArrWhoPaysList);
        this.context = context;
        this.GroupId = GroupId;
        this.ArrWhoPaysList = ArrWhoPaysList;
        this.ArrHowMuchList = ArrHowMuchList;
        this.ArrToWhomList = ArrToWhomList;
        this.Currency=Currency;
    }
    public View getView(final int position, final View convertView, ViewGroup parent) {
        myDB = new DatabaseHelper(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View ListViewItem = inflater.inflate(R.layout.custom_settle_debts_list, null, true);

        final TextView tv_who_pays=(TextView)ListViewItem.findViewById(R.id.tv_who_pays);
        final TextView tv_how_much=(TextView)ListViewItem.findViewById(R.id.tv_how_much);
        final TextView tv_to_whom=(TextView)ListViewItem.findViewById(R.id.tv_to_whom);

        tv_who_pays.setText(ArrWhoPaysList.get(position)+" should pay");
        Log.d("zxcv",""+Currency);
        tv_how_much.setText(ArrHowMuchList.get(position)+" "+Currency);
        tv_to_whom.setText("to "+ArrToWhomList.get(position));

        return ListViewItem;
    }
}