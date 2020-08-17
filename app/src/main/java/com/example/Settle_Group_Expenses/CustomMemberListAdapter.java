package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
// This class creates rows
public class CustomMemberListAdapter extends ArrayAdapter {
    ArrayList<String> ArrMemberID,ArrMemberName, MemberNameList, NameList;
    ArrayList <Double> DebtList;
    Activity Context;
    String GroupId, GroupName, memId,memName;
    LinearLayout parentLayout;
    public static DatabaseHelper myDB;

    public CustomMemberListAdapter(Activity Context,String GroupId, String GroupName, ArrayList<String> ArrMemberID,
                                   ArrayList<String> ArrMemberName){
        super(Context,R.layout.custom_memberlist,ArrMemberID );
        this.Context=Context;
        this.GroupId=GroupId;
        this.GroupName=GroupName;
        this.ArrMemberID=ArrMemberID;
        this.ArrMemberName=ArrMemberName;
    }
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater=Context.getLayoutInflater();
        View ListViewItem=inflater.inflate(R.layout.custom_memberlist,null,true);

        final TextView tv_membername=(TextView)ListViewItem.findViewById(R.id.tv_membername);
        Button btn_rename_members=(Button)ListViewItem.findViewById(R.id.btn_rename_members);
        Button btn_del_members=(Button)ListViewItem.findViewById(R.id.btn_del_member);
        tv_membername.setText(ArrMemberName.get(position));
        parentLayout=(LinearLayout)ListViewItem.findViewById(R.id.parentLayout);
        myDB=new DatabaseHelper(Context);

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Context, MemberDetails.class);
                intent.putExtra("groupID",GroupId);
                intent.putExtra("groupNAME", GroupName);
                intent.putExtra("memberID",ArrMemberID.get(position));
                intent.putExtra("memberNAME",ArrMemberName.get(position));
                //intent.putExtra("name",)
                Context.startActivity(intent);
            }
        });

        btn_rename_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memId=ArrMemberID.get(position);
                memName=ArrMemberName.get(position);

                LayoutInflater rename_inflater=Context.getLayoutInflater();
                View tView=rename_inflater.inflate(R.layout.edit_member,null,true);

                final EditText et_member_rename=(EditText)tView.findViewById(R.id.et_member_rename);
                Button btn_save_member_name=(Button)tView.findViewById(R.id.btn_save_member_name);

                AlertDialog.Builder alert_builder=new AlertDialog.Builder(Context);
                alert_builder.setView(tView);
                alert_builder.setCancelable(true);

                final AlertDialog dialog=alert_builder.create();
                dialog.show();

                et_member_rename.setText(memName);

                btn_save_member_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rep_name=et_member_rename.getText().toString();
                        String updateMemeberName = rep_name.replace("'","''");
                        String rep_memName = memName.replace("'","''");

                        if (updateMemeberName.equals("")){
                            Toast.makeText(Context, "Please Enter a name", Toast.LENGTH_SHORT).show();
                        }
                        else if (rep_memName.equals(updateMemeberName)){
                            Toast.makeText(Context, "Same name as previous", Toast.LENGTH_SHORT).show();
                        }
                        else if (ArrMemberName.indexOf(updateMemeberName)==-1){
                            myDB.updateMemberName(GroupId,memId,updateMemeberName);
                            myDB.updateAllMemberName(GroupId, rep_memName, updateMemeberName);
                            AddMembers.fillMemberinList(Context);
                            tv_membername.setText(ArrMemberName.get(position)+"1");
                            dialog.dismiss();
                        }

                        else {
                            Toast.makeText(Context, "Member with same name already exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_del_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //checkInvolvementInExpense();
                checkInvolvementInExpense();
                if (MemberNameList.contains(ArrMemberName.get(position))){
                    try {
                        Snackbar.make(parentLayout,"This member cannot be deleted as currently he has debts to settle",
                                Snackbar.LENGTH_LONG).show();
                    }
                    catch (Exception e){}
                }
                else {
                    final AlertDialog.Builder alert_builder=new AlertDialog.Builder(Context);
                    alert_builder.setCancelable(true);
                    alert_builder.setTitle("Delete Member");
                    alert_builder.setMessage("Do you want to delete this member ?");
                    alert_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            memId=ArrMemberID.get(position);
                            myDB.deleteMember(GroupId,memId);
                            AddMembers.fillMemberinList(Context);
                        }
                    });
                    final AlertDialog dialog=alert_builder.create();
                    dialog.show();
                }
            }
        });
        return ListViewItem;
    }

    protected void checkInvolvementInExpense(){
        NameList=new ArrayList<>();
        DebtList=new ArrayList<>();
        MemberNameList=new ArrayList<>();

        NameList.clear();
        DebtList.clear();
        MemberNameList.clear();

        DecimalFormat df = new DecimalFormat(".##");
        df.setRoundingMode(RoundingMode.DOWN);

        Cursor cursor=myDB.getMemberName(GroupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String name=cursor.getString(1);
                double debts=cursor.getDouble(4);
                NameList.add(name);
                DebtList.add(Double.parseDouble(df.format(debts)));
            }while (cursor.moveToNext());
        }
        //Log.d("azaz", ""+DebtList);

        // ----- further conversion added as newly added member's debt was not becoming equal to the normal amount


        double normal_amt0=0;
        Cursor cursor1=myDB.getAllExpense(GroupId);
        if (cursor1.getCount()==0){
        }
        else {
            cursor1.moveToFirst();
            do {
                normal_amt0+= cursor1.getDouble(3);
            }while (cursor1.moveToNext());
        }

        Cursor cursor2=myDB.getAllMMExpense(GroupId);
        if (cursor2.getCount()==0){
        }
        else {
            cursor2.moveToFirst();
            do {
                normal_amt0+= cursor2.getInt(2);
            }while (cursor2.moveToNext());
        }

        double normal_amt=Double.parseDouble(df.format(normal_amt0));

        Log.d("normal_amt_C", normal_amt+" "+DebtList);
        for (int i=0; i<DebtList.size(); i++){
            if (DebtList.get(i) != normal_amt){
                MemberNameList.add(NameList.get(i));
            }
        }
    }
}