package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

public class CustomGroupListAdapter  extends ArrayAdapter {
    ArrayList<String> ArrGroupId, ArrGroupName, ArrGroupDestinaion;
    Activity Context;
    String grpId,grpName, destination;
    LinearLayout LL_group_name;
    public static DatabaseHelper myDB;

    public CustomGroupListAdapter(Activity Context,ArrayList<String> ArrGroupId,ArrayList<String> ArrGroupName,
                                  ArrayList<String> ArrGroupDestinaion){
        super(Context,R.layout.custom_memberlist,ArrGroupId );
        this.Context=Context;
        this.ArrGroupId=ArrGroupId;
        this.ArrGroupName=ArrGroupName;
        this.ArrGroupDestinaion=ArrGroupDestinaion;
    }
    public View getView(final int postion, View convertView, ViewGroup parent){
        LayoutInflater inflater=Context.getLayoutInflater();
        View ListViewItem=inflater.inflate(R.layout.custom_grouplist,null,true);

        TextView Groupname=(TextView)ListViewItem.findViewById(R.id.groupName);
        TextView grp_dest=(TextView)ListViewItem.findViewById(R.id.grp_dest);
        Button btn_edit_group=(Button)ListViewItem.findViewById(R.id.btn_edit_group);
        Button btn_del_group=(Button)ListViewItem.findViewById(R.id.btn_del_group);

        Groupname.setText(ArrGroupName.get(postion));
        grp_dest.setText(ArrGroupDestinaion.get(postion));
        LL_group_name=(LinearLayout)ListViewItem.findViewById(R.id.LL_group_name);
        myDB=new DatabaseHelper(Context);

        LL_group_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(Context,GroupSummary.class);
                intent.putExtra("groupID",ArrGroupId.get(postion));
                intent.putExtra("groupNAME",ArrGroupName.get(postion));
                Context.startActivity(intent);
                return true;
            }
        });

        LL_group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Context, AddMembers.class);
                intent.putExtra("groupID",ArrGroupId.get(postion));
                intent.putExtra("groupNAME",ArrGroupName.get(postion));
                Context.startActivity(intent);
            }
        });

        btn_edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grpId=ArrGroupId.get(postion);
                grpName=ArrGroupName.get(postion);
                destination=ArrGroupDestinaion.get(postion);

                LayoutInflater rename_inflater=Context.getLayoutInflater();
                View tView=rename_inflater.inflate(R.layout.edit_group,null,true);

                final EditText et_group_rename=(EditText)tView.findViewById(R.id.et_group_rename);
                final EditText et_dest_rename=(EditText)tView.findViewById(R.id.et_dest_rename);
                Button btn_save_group_name=(Button)tView.findViewById(R.id.btn_save_group_name);

                AlertDialog.Builder alert_builder=new AlertDialog.Builder(Context);
                alert_builder.setView(tView);
                alert_builder.setCancelable(true);

                final AlertDialog dialog=alert_builder.create();
                dialog.show();

                et_group_rename.setText(grpName);
                et_dest_rename.setText(destination);

                btn_save_group_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String updateGroupName=et_group_rename.getText().toString().replace("'","''");
                        String updateDestination=et_dest_rename.getText().toString().replace("'","''");

                        if (updateGroupName.equals("")){
                            Toast.makeText(Context, "Please Enter a name", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            myDB.updateGroupName(grpId,updateGroupName);
                            MainActivity.getGroupData(Context);
                            if (updateDestination.equals("")){
                                myDB.updateDestination(grpId,"Destination not added");
                                MainActivity.getGroupData(Context);
                                dialog.dismiss();
                            }
                            else{
                                myDB.updateDestination(grpId,updateDestination);
                                MainActivity.getGroupData(Context);
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

        btn_del_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert_builder=new AlertDialog.Builder(Context);
                alert_builder.setCancelable(true);
                alert_builder.setTitle("Delete Member");
                alert_builder.setMessage("Do you want to delete this member ?");
                alert_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        grpId=ArrGroupId.get(postion);
                        Log.d("gid",grpId);
                        myDB.deleteGroup(grpId);
                        MainActivity.getGroupData(Context);
                    }
                });
                final AlertDialog dialog=alert_builder.create();
                dialog.show();
            }
        });
        return ListViewItem;
    }
}