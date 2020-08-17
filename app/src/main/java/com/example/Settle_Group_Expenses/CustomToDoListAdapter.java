package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class CustomToDoListAdapter extends ArrayAdapter {
    ArrayList<String> ArrToDoID, ArrToDoName, ArrToDoDate, ArrToDoTime;
    Activity Context;
    String todoId;
    LinearLayout LL_to_do_name;
    public static DatabaseHelper myDB;

    public CustomToDoListAdapter(Activity Context,ArrayList<String> ArrToDoID,ArrayList<String> ArrToDoName,
                                 ArrayList<String> ArrToDoDate, ArrayList<String> ArrToDoTime){
        super(Context,R.layout.custom_todolist,ArrToDoID);
        this.Context=Context;
        this.ArrToDoID=ArrToDoID;
        this.ArrToDoName=ArrToDoName;
        this.ArrToDoDate=ArrToDoDate;
        this.ArrToDoTime=ArrToDoTime;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=Context.getLayoutInflater();
        View ListViewItem=inflater.inflate(R.layout.custom_todolist,null,true);

        TextView budgets=(TextView)ListViewItem.findViewById(R.id.budgets);
        TextView tv_date=(TextView)ListViewItem.findViewById(R.id.tv_date);
        TextView tv_time=(TextView)ListViewItem.findViewById(R.id.tv_time);
        LL_to_do_name=(LinearLayout)ListViewItem.findViewById(R.id.LL_to_do_name);
        myDB=new DatabaseHelper(Context);

        budgets.setText(ArrToDoName.get(position));
        tv_date.setText(ArrToDoDate.get(position));
        tv_time.setText(ArrToDoTime.get(position));

        LL_to_do_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder alert_builder=new AlertDialog.Builder(Context);
                alert_builder.setCancelable(true);
                alert_builder.setTitle("Delete Reminder");
                alert_builder.setMessage("Do you want to delete this Reminder ?");
                alert_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        todoId=ArrToDoID.get(position);
                        myDB.deleteToDo(todoId);
                        MemberDetails.fillToDoinList(Context);
                    }
                });
                final AlertDialog dialog=alert_builder.create();
                dialog.show();
                return true;
            }
        });

        return ListViewItem;
    }
}