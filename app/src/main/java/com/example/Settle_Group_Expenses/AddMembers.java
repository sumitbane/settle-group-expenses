package com.example.Settle_Group_Expenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddMembers extends AppCompatActivity {

    public static ListView member_list;
    Button btn_add_member;
    EditText et_members,et_budget;
    Context context;
    Double budget;
    public static LinearLayout parentLayout;
    public static DatabaseHelper myDB;
    public static ArrayList<String>  MemberIdList,memberList;
    //public static ArrayList<Integer>  Total;
    public static String groupId,groupName;
    FloatingActionButton fab1, fab2, fab3, fab4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

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
        member_list=(ListView)findViewById(R.id.MemberList);
        btn_add_member=(Button)findViewById(R.id.btn_add_member);
        //save_members=(Button)findViewById(R.id.save_members);
        et_members=(EditText)findViewById(R.id.et_members);
        et_budget=(EditText)findViewById(R.id.et_budget);
        parentLayout= (LinearLayout)findViewById(R.id.parentLayout); 
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //if (member_list.getAdapter().getCount()>2){}
        fab1=(FloatingActionButton)findViewById(R.id.fab_action_1);
        fab2=(FloatingActionButton)findViewById(R.id.fab_action_2);
        fab3=(FloatingActionButton)findViewById(R.id.fab_action_3);
        fab4=(FloatingActionButton)findViewById(R.id.fab_action_4);
        fillMemberinList(AddMembers.this);
        addData();
        fab_method(AddMembers.this);
    }
    protected static void fillMemberinList(Activity context){

        memberList=new ArrayList<String>();
        MemberIdList=new ArrayList<>();
        
    
        Cursor cursor=myDB.getMemberName(groupId);
        if (cursor.getCount()==0){
            Snackbar.make(parentLayout, "Great! Now start adding Members", Snackbar.LENGTH_LONG).show();
        }
        
        else {
            cursor.moveToFirst();
            do{
                String id=cursor.getString(0);
                String name=cursor.getString(1);
                MemberIdList.add(id);
                memberList.add(name);
            }while (cursor.moveToNext());
        }
        CustomMemberListAdapter adapter=new CustomMemberListAdapter(context, groupId, groupName, MemberIdList,memberList);
        member_list.setAdapter(adapter);
    }

    protected void addData(){
        btn_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_members.getText().toString().replace("'","''");
                try {
                    budget=Double.parseDouble(et_budget.getText().toString());
                }
                catch (NumberFormatException e){
                    budget=0d;
                }

            if (name.isEmpty()){
                Toast.makeText(AddMembers.this,"Enter Member Name",Toast.LENGTH_SHORT).show();
            }
            else if (memberList.size() == 15){
                Toast.makeText(AddMembers.this, "Maximum 15 members are allowed", Toast.LENGTH_SHORT).show();
            }

            else if (memberList.indexOf(name)==-1){
                myDB.insertMember(name,Integer.parseInt(groupId), budget);
                et_members.setText("");
                et_budget.setText("");
                fillMemberinList(AddMembers.this);

                double normal_amt0=0;
                Cursor cursor=myDB.getAllExpense(groupId);
                if (cursor.getCount()==0){
                }
                else {
                    cursor.moveToFirst();
                    do {
                        normal_amt0+= cursor.getDouble(3);
                    }while (cursor.moveToNext());
                }

                Cursor cursor1=myDB.getAllMMExpense(groupId);
                if (cursor1.getCount()==0){
                }
                else {
                    cursor1.moveToFirst();
                    do {
                        normal_amt0+= cursor1.getInt(2);
                    }while (cursor1.moveToNext());
                }
                DecimalFormat df = new DecimalFormat(".##");
                df.setRoundingMode(RoundingMode.DOWN);
                double normal_amt=Double.parseDouble(df.format(normal_amt0));
                Log.d("normal_amt_A",""+normal_amt);
                myDB.updateDebts(groupId, name, normal_amt);
            }
            else {
                Toast.makeText(AddMembers.this, "Member with same name already exist", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    protected void fab_method(Activity context){
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String member_count;
                Cursor cursor=myDB.getMemberName(groupId);
                if (cursor.getCount()<2){
                    Toast.makeText(AddMembers.this, "At least 2 Members needed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent(AddMembers.this,Expenses.class);
                    intent.putExtra("groupID",groupId);
                    intent.putExtra("groupNAME",groupName);
                    startActivity(intent);
                }
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=myDB.getMemberName(groupId);
                if (cursor.getCount()<2){
                    Toast.makeText(AddMembers.this, "At least 2 Members needed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent(AddMembers.this,AdvancePayment.class);
                    intent.putExtra("groupID",groupId);
                    intent.putExtra("groupNAME",groupName);
                    startActivity(intent);
                }
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=myDB.getMemberName(groupId);
                if (cursor.getCount()<2){
                    Toast.makeText(AddMembers.this, "At least 2 Members needed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent(AddMembers.this,Transactions.class);
                    intent.putExtra("groupID",groupId);
                    intent.putExtra("groupNAME",groupName);
                    startActivity(intent);
                }
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=myDB.getMemberName(groupId);
                if (cursor.getCount()<2){
                    Toast.makeText(AddMembers.this, "At least 2 Members needed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent(AddMembers.this,SettleDebts.class);
                    intent.putExtra("groupID",groupId);
                    intent.putExtra("groupNAME",groupName);
                    startActivity(intent);
                }
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(AddMembers.this, Summary.class);
        intent.putExtra("groupID",groupId);
        intent.putExtra("groupNAME",groupName);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddMembers.this,MainActivity.class);
        startActivity(intent);
    }
    
}
// Check if member is in current list