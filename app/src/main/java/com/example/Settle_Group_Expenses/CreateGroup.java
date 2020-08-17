package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateGroup extends AppCompatActivity {
    EditText et_gname, et_destination, et_description;
    Spinner sp_currency;
    Button btn_save_group;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        et_gname=(EditText)findViewById(R.id.et_gname);
        et_destination=(EditText)findViewById(R.id.et_destination);
        et_description=(EditText)findViewById(R.id.et_description);
        sp_currency=(Spinner)findViewById(R.id.sp_currency);
        btn_save_group=(Button)findViewById(R.id.btn_save_group);
        myDB=new DatabaseHelper(this);
        addData();

        String[] Currency={"INR","USD","EUR"};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(CreateGroup.this,
                android.R.layout.simple_spinner_dropdown_item,Currency);
        //styling and populating the spinner

        //dropdown layout style
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching dataAdapter to spinner
        sp_currency.setAdapter(arrayAdapter);

        sp_currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Choose Category")){
                    //do nothing
                }
                else{
                    //on selection
                    String item=parent.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void addData(){

        btn_save_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_gname.getText().toString().replace("'","''");
                String curr=sp_currency.getSelectedItem().toString();
                String dest=et_destination.getText().toString().replace("'","''");
                String desc=et_description.getText().toString().replace("'","''");
                if (name.isEmpty()){
                    Toast.makeText(CreateGroup.this,"Enter Group Name",Toast.LENGTH_LONG).show();
                }
                else{

                    myDB.insertGroup(name, curr, dest, desc);
                    openMainActivity();
                    et_gname.setText("");
                    et_destination.setText("");
                    et_description.setText("");
                }
            }
        });
    }
    public void openMainActivity(){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}