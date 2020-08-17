package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Transactions extends AppCompatActivity {

    public static TextView tv_expenses, tv_mm_expenses, tv_payments;
    public static ListView lv_expenses, lv_mm_expenses, lv_payments;
    public static String groupId,groupName, currency;
    public static ArrayList<String>  ExpenseIdList, ExpensePurposeList, ExpenseAmountList, ExpenseWhoPaidList,
            MMExpenseIdList, MMExpensePurposeList, MMExpenseAmountList,
            PaymentIdList, PaymentPurposeList, PaymentAmountList, PaymentWhoPaidList, PaymentTOWhomList;
    public static LinearLayout parentLayout;
    public static DatabaseHelper myDB;
    public static Integer who_paid_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

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
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        tv_expenses=(TextView)findViewById(R.id.tv_expenses);
        tv_mm_expenses=(TextView)findViewById(R.id.tv_mm_expenses);
        tv_payments=(TextView)findViewById(R.id.tv_payments);

        lv_expenses=(ListView)findViewById(R.id.lv_expenses);
        lv_mm_expenses=(ListView)findViewById(R.id.lv_mm_expenses);
        lv_payments=(ListView)findViewById(R.id.lv_payments);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        currency();
        selectTransaction();
        fillExpenseinList(Transactions.this);
        fillMMExpenseinList(Transactions.this);
        fillPaymentinList(Transactions.this);
    }
    protected void currency() {
        Cursor cursor = myDB.getGroupDetails(groupId);
        if (cursor.getCount() == 0) {
        } else {
            cursor.moveToFirst();
            currency = cursor.getString(2);
        }
    }

    protected static void fillExpenseinList(Activity context){
        ExpenseIdList=new ArrayList<>();
        ExpensePurposeList=new ArrayList<String>();
        ExpenseAmountList=new ArrayList<String>();
        ExpenseWhoPaidList=new ArrayList<String>();

        getAllExpenses();

        CustomExpenseListadapter adapter=new CustomExpenseListadapter(context, groupId, groupName, ExpenseIdList,
                ExpensePurposeList, ExpenseAmountList, ExpenseWhoPaidList,currency);
        lv_expenses.setAdapter(adapter);
        if (lv_expenses.getAdapter().getCount()>0){
            lv_expenses.setVisibility(View.VISIBLE);
        }
        else {
            Snackbar.make(parentLayout, "No Expense added yet", Snackbar.LENGTH_LONG).show();
        }
    }

    protected static void fillMMExpenseinList(Activity context){
        MMExpenseIdList=new ArrayList<>();
        MMExpensePurposeList=new ArrayList<String>();
        MMExpenseAmountList=new ArrayList<String>();
        getAllMMExpenses();

        Log.d("poid",""+MMExpensePurposeList);
        Log.d("poid2",""+MMExpenseAmountList);
        Log.d("poid3",""+MMExpenseIdList);

        CustomMMExpenseListAdapter adapter = new CustomMMExpenseListAdapter(context,groupId, groupName, MMExpenseIdList,
                MMExpensePurposeList, MMExpenseAmountList, currency);
        lv_mm_expenses.setAdapter(adapter);
    }

    protected static void fillPaymentinList(Activity context){
        PaymentIdList=new ArrayList<>();
        PaymentPurposeList=new ArrayList<String>();
        PaymentAmountList=new ArrayList<String>();
        PaymentWhoPaidList=new ArrayList<String>();
        PaymentTOWhomList=new ArrayList<String>();

        getAllPayments();

        CustomPaymentListAdapter adapter=new CustomPaymentListAdapter(context, groupId, groupName, PaymentIdList,
                PaymentPurposeList, PaymentAmountList, PaymentWhoPaidList, PaymentTOWhomList, currency);
        lv_payments.setAdapter(adapter);
    }

    protected static void getAllExpenses(){
        Cursor cursor=myDB.getAllExpense(groupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String id=cursor.getString(0);
                String who_paid=cursor.getString(2);
                String exp_amt=cursor.getString(3);
                String purpose=cursor.getString(4);

                ExpenseIdList.add(id);
                ExpenseWhoPaidList.add(who_paid);
                ExpensePurposeList.add(purpose);
                ExpenseAmountList.add(exp_amt);

                Log.d("eid",""+ExpenseIdList);
                Log.d("mname",""+ExpenseWhoPaidList);
                Log.d("eamt",""+ExpenseAmountList);
                Log.d("purpose",""+ExpensePurposeList);

                //getMemName();
            }while (cursor.moveToNext());
        }
    }

    protected static void getAllMMExpenses(){
        Cursor cursor=myDB.getAllMMExpense(groupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String id=cursor.getString(0);
                String exp_amt=cursor.getString(2);
                String purpose=cursor.getString(3);

                MMExpenseIdList.add(id);
                MMExpensePurposeList.add(purpose);
                MMExpenseAmountList.add(exp_amt);

                Log.d("eid1",""+MMExpenseIdList);
                Log.d("eamt1",""+MMExpenseAmountList);
                Log.d("purpose1",""+MMExpensePurposeList);
            }while (cursor.moveToNext());
        }
    }

    protected static void getAllPayments(){
        Cursor cursor=myDB.getAllPayment(groupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String id=cursor.getString(0);
                String purpose=cursor.getString(2);
                String exp_amt=cursor.getString(1);
                String who_paid=cursor.getString(3);
                String to_whom=cursor.getString(4);

                PaymentIdList.add(id);
                PaymentPurposeList.add(purpose);
                PaymentAmountList.add(exp_amt);
                PaymentWhoPaidList.add(who_paid);
                PaymentTOWhomList.add(to_whom);

            }while (cursor.moveToNext());
        }
    }

    protected void selectTransaction(){
        tv_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            // ----- was trying to delete last exp status but didn't work -----
            public void onClick(View v) {
                if (lv_expenses.getAdapter().getCount()>0){
                    lv_expenses.setVisibility(View.VISIBLE);
                }
                else {
                    Snackbar.make(parentLayout, "No Expense added yet", Snackbar.LENGTH_LONG).show();
                }
                lv_mm_expenses.setVisibility(View.GONE);
                lv_payments.setVisibility(View.GONE);
                tv_expenses.setTypeface(tv_expenses.getTypeface(), Typeface.BOLD_ITALIC);
                tv_expenses.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv_mm_expenses.setTypeface(tv_mm_expenses.getTypeface(),Typeface.ITALIC);
                tv_mm_expenses.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv_payments.setTypeface(tv_payments.getTypeface(),Typeface.ITALIC);
                tv_payments.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        });
        tv_mm_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv_mm_expenses.getAdapter().getCount()>0){
                    lv_mm_expenses.setVisibility(View.VISIBLE);
                }
                else {
                    Snackbar.make(parentLayout, "No Expense added yet", Snackbar.LENGTH_LONG).show();
                }
                lv_expenses.setVisibility(View.GONE);
                lv_payments.setVisibility(View.GONE);
                tv_expenses.setTypeface(tv_expenses.getTypeface(), Typeface.ITALIC);
                tv_expenses.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv_mm_expenses.setTypeface(tv_mm_expenses.getTypeface(),Typeface.BOLD_ITALIC);
                tv_mm_expenses.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv_payments.setTypeface(tv_payments.getTypeface(),Typeface.ITALIC);
                tv_payments.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        });
        tv_payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv_payments.getAdapter().getCount()>0){
                    lv_payments.setVisibility(View.VISIBLE);
                }
                else {
                    Snackbar.make(parentLayout, "No Payment added yet", Snackbar.LENGTH_LONG).show();
                }
                lv_expenses.setVisibility(View.GONE);
                lv_mm_expenses.setVisibility(View.GONE);
                tv_expenses.setTypeface(tv_expenses.getTypeface(), Typeface.ITALIC);
                tv_expenses.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv_mm_expenses.setTypeface(tv_mm_expenses.getTypeface(),Typeface.ITALIC);
                tv_mm_expenses.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv_payments.setTypeface(tv_payments.getTypeface(),Typeface.BOLD_ITALIC);
                tv_payments.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
        });
    }
}
// ----- When I was inserting IDs instead of names
    /*public static void getMemName(){
        Cursor cursor1=myDB.getMemberNameFromId(who_paid_id);
        if (cursor1.getCount()==0){
        }
        else {
            cursor1.moveToFirst();
            do{
                String who_paid_name=cursor1.getString(1);
                ExpenseWhoPaidNameList.add(who_paid_name);
                Log.d("name",""+ExpenseWhoPaidNameList);
            }while (cursor1.moveToNext());
        }
    }*/