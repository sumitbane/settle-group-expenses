package com.example.Settle_Group_Expenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button create_group;
    public static ListView listView_groupList;
    public static DatabaseHelper myDB;
    public static ArrayList<String> groupIdList, groupNameList, groupDestList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB=new DatabaseHelper(this);

        create_group=(Button) findViewById(R.id.create_group);
        listView_groupList=(ListView) findViewById(R.id.listView_groupList);
        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CreateGroup.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        listView_groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,AddMembers.class);
                intent.putExtra("groupID",groupIdList.get(position));
                intent.putExtra("groupNAME",groupNameList.get(position));
                startActivity(intent);
            }
        });
        getGroupData(MainActivity.this);
        }

    public static void getGroupData(Activity context){
        groupIdList=new ArrayList<String>();
        groupNameList=new ArrayList<String>();
        groupDestList=new ArrayList<String>();
        Cursor cursor=myDB.getGroupName();
        cursor.moveToFirst();
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                // retrieving from db
                String id=cursor.getString(0);
                String name=cursor.getString(1);
                String dest=cursor.getString(3);
                // if it is Int the use .getInt
                //adding in arraylist
                groupIdList.add(id);
                groupNameList.add(name);
                if (dest.equals("")){
                    groupDestList.add("Destination not added");
                }
                else {
                    groupDestList.add(dest);
                }
            }while (cursor.moveToNext());
        }

        //Call Custom GL
        CustomGroupListAdapter adapter=new CustomGroupListAdapter(context,groupIdList,groupNameList,groupDestList);
        listView_groupList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.instructions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, "Item Selected", Toast.LENGTH_SHORT).show();
        LayoutInflater rename_inflater=MainActivity.this.getLayoutInflater();
        View tView=rename_inflater.inflate(R.layout.custom_instructions,null,true);

        androidx.appcompat.app.AlertDialog.Builder alert_builder=
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        alert_builder.setView(tView);
        alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        });

        final androidx.appcompat.app.AlertDialog dialog=alert_builder.create();
        dialog.show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_builder=new AlertDialog.Builder(MainActivity.this);
        alert_builder.setTitle("Close Settle Group Expenses").
                setMessage("Are you sure you want to close the app ?").
                setCancelable(true).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        final AlertDialog dialog=alert_builder.create();
        dialog.show();
    }
}

//1 create Activity ie CustomLayout
//2 update its xml
//3 update the CustomLayout.java
//4 create select query for retrieving data
//5 go to page where you want to display list and create arrayList, create function and create cursor and call the db function
//6 Call the customLayout class & setAdapter to the listview