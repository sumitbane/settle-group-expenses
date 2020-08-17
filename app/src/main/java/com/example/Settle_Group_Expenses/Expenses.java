package com.example.Settle_Group_Expenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

public class Expenses extends AppCompatActivity {

    Spinner sp_expenses;
    EditText et_exp_amount;
    TextView tv_exp_currency;
    Button btn_next;
    DatabaseHelper myDB;
    String groupId, groupName, name;
    Double amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        } else {
            groupId = bundle.getString("groupID");
            groupName=bundle.getString("groupNAME");
            name = bundle.getString("NAME");
            amt = bundle.getDouble("AMOUNT");
        }
        myDB = new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        sp_expenses = (Spinner) findViewById(R.id.sp_expenses);
        et_exp_amount = (EditText) findViewById(R.id.et_exp_amount);
        tv_exp_currency = (TextView) findViewById(R.id.tv_exp_currency);
        btn_next = (Button) findViewById(R.id.btn_next);

        loadSpinnerData();
        setAmount();
        currency();
        moveToExpenseDetails();
        moveToMMExpenses();
    }



    public void loadSpinnerData() {
        final List<String> Members = myDB.getAllMembers(groupId);
        final ArrayAdapter<String> dataAdapter ;
        dataAdapter = new ArrayAdapter<String>(this,
                R.layout.expense_spinner_tv, Members){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == getCount() - 1) {
                    tv.setTextColor(Color.parseColor("#0e96f9"));
                }
                return view;
            }
        };
        String mul_mem=getResources().getString(R.string.mul_mem);
        Members.add("MULTIPLE MEMBERS");
        dataAdapter.setDropDownViewResource(R.layout.expense_spinner_tv);
        sp_expenses.setAdapter(dataAdapter);
    }

    public void setAmount(){
        if (amt != 0){
            et_exp_amount.setText(""+amt);
        }
        else {
            et_exp_amount.setText("0");
        }
    }

    public void currency() {
        Cursor cursor = myDB.getGroupDetails(groupId);
        if (cursor.getCount() == 0) {
        } else {
            cursor.moveToFirst();
            do {
                String currency = cursor.getString(2);
                tv_exp_currency.setText(currency);
            } while (cursor.moveToNext());
        }
    }

    public void moveToExpenseDetails() {

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_amt = et_exp_amount.getText().toString();
                name = sp_expenses.getSelectedItem().toString();
                if (str_amt.equals("")) {
                    Toast.makeText(Expenses.this, "Enter a Valid amount", Toast.LENGTH_SHORT).show();
                } else {
                    amt = Double.parseDouble(et_exp_amount.getText().toString());
                    if (amt > 0) {
                        Intent intent = new Intent(Expenses.this, ExpenseDetails.class);
                        intent.putExtra("groupID", groupId);
                        intent.putExtra("groupNAME",groupName);
                        intent.putExtra("NAME", name);
                        intent.putExtra("AMOUNT", amt);
                        intent.putExtra("CURRENCY", tv_exp_currency.getText());
                        startActivity(intent);
                    } else {
                        Toast.makeText(Expenses.this, "Amount should be greater than 0", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void moveToMMExpenses(){
        sp_expenses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("MULTIPLE MEMBERS"))
                {
                    Intent intent=new Intent(Expenses.this,MultipleMembersExpenses.class);
                    intent.putExtra("groupID", groupId);
                    intent.putExtra("groupNAME",groupName);
                    intent.putExtra("CURRENCY", tv_exp_currency.getText());
                    sp_expenses.setSelection(0);
                    startActivity(intent);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("NAME", name);
        //outState.putString("NAME", name);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Expenses.this, AddMembers.class);
        intent.putExtra("groupID", groupId);
        intent.putExtra("groupNAME",groupName);
        startActivity(intent);
    }
}