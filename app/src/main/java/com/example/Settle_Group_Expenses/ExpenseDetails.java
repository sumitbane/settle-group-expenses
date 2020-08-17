package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpenseDetails extends AppCompatActivity {
    public static ListView lv_expense_list_1, lv_expense_list_2;
    Context context;
    public static DatabaseHelper myDB;
    public static ArrayList<String> MemberIdList, MemberNameList0, MemberNameList1, MemberNameList2, CheckList, CheckedMembersList;
    public static ArrayList<Double> MemberAmountList2;
    public static CustomEqualAmountListAdapter adapter1;
    public static String groupId, expId, mid, currency;
    public static Double MemberAmt;
    public static Cursor cursor;
    //public static Integer MemberAmt;

    TextView tv_who_paid,tv_amount_2,tv_exp_currency_2, tv_for_whom, tv_date, tv_time, tv_equal_amt, tv_split_amt;
    EditText et_purpose;
    Button btn_exp_calendar, btn_add_expense, btn_add_eq_status, btn_add_sp_status;
    LinearLayout parentLayout, ll_who_paid, LL_whole_exp, LL_status_1, LL_status_2;
    int mYear, mMonth, mDay, mHour, mMinute;
    String name, groupName;
    Double amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);
        Bundle bundle=getIntent().getExtras();
        if(bundle==null)
        {
            return;
        }
        else
        {
            groupId=bundle.getString("groupID");
            groupName=bundle.getString("groupNAME");
            name=bundle.getString("NAME").replace("'","''");
            amount=bundle.getDouble("AMOUNT");
            currency=bundle.getString("CURRENCY");
            CheckList=bundle.getStringArrayList("CheckLIST");
        }
        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        tv_who_paid=(TextView)findViewById(R.id.tv_who_paid);
        tv_amount_2=(TextView)findViewById(R.id.tv_amount_2);
        tv_exp_currency_2=(TextView)findViewById(R.id.tv_exp_currency_2);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_for_whom=(TextView)findViewById(R.id.tv_for_whom);
        tv_equal_amt=(TextView)findViewById(R.id.tv_equal_amt);
        tv_split_amt=(TextView)findViewById(R.id.tv_split_amt);
        et_purpose=(EditText)findViewById(R.id.et_purpose);
        btn_exp_calendar=(Button)findViewById(R.id.btn_exp_calendar);
        btn_add_expense=(Button)findViewById(R.id.btn_add_expense);
        btn_add_eq_status=(Button)findViewById(R.id.btn_add_eq_status);
        btn_add_sp_status=(Button)findViewById(R.id.btn_add_sp_status);
        lv_expense_list_1=(ListView)findViewById(R.id.lv_expense_list_1);
        lv_expense_list_2= (ListView)findViewById(R.id.lv_expense_list_2);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        tv_who_paid.setText(name);
        tv_amount_2.setText(amount.toString());
        tv_exp_currency_2.setText(currency);
        ll_who_paid=(LinearLayout)findViewById(R.id.ll_who_paid);
        LL_whole_exp=(LinearLayout)findViewById(R.id.LL_whole_exp);
        LL_status_1=(LinearLayout)findViewById(R.id.LL_status_1);
        //LL_status_2=(LinearLayout)findViewById(R.id.LL_status_2);
        MemberAmt=amount;

        btn_exp_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDateTime();
            }
        });
        addExpense();
        getLastExpId();
        fillMemberinList1(ExpenseDetails.this);
        moveToExpenses();
        addStatus();
    }

    protected void addDateTime(){
        Calendar c = Calendar.getInstance();

        // Get Current Date
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DATE);

        // Get Current Time
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year,
                                          int monthOfYear, int dayOfMonth) {
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Launch Time Picker Dialog
                tv_date.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                boolean is24HrFormat= DateFormat.is24HourFormat(ExpenseDetails.this);;
                TimePickerDialog timePickerDialog = new TimePickerDialog(ExpenseDetails.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override

                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                tv_time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, is24HrFormat);
                timePickerDialog.show();
            }
        });
    }

    protected static void fillMemberinList1(Activity context){
        MemberIdList=new ArrayList<>();
        MemberNameList1=new ArrayList<String>();
        retrieveMembers1();
        //Log.d("names",MemberNameList1+"");
         adapter1=new CustomEqualAmountListAdapter(context,groupId,MemberIdList,
                MemberNameList1,MemberAmt);
        lv_expense_list_1.setAdapter(adapter1);
    }

    protected static void fillMemberinList2(Activity context){
        MemberNameList2=new ArrayList<String>();
        MemberAmountList2=new ArrayList<Double>();
        //retrieveMembers();
        //retrieveToWhomAmount();
        //retrieveExpenseStatus();String eid;
        retrieveExpenseStatus();
        CustomSplitAmountAdapterList adapter1 =new CustomSplitAmountAdapterList(context,groupId, MemberNameList2,
                MemberAmountList2, currency);
        lv_expense_list_2.setAdapter(adapter1);
    }

    protected static void retrieveMembers0(){
        cursor=myDB.getMemberName(groupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String name=cursor.getString(1);
                MemberNameList0.add(name);
            }while (cursor.moveToNext());
        }
    }

    protected static void retrieveMembers1(){
        MemberNameList1.clear();
        Cursor cursor1=myDB.getMemberName(groupId);
        if (cursor1.getCount()==0){
        }
        else {
            cursor1.moveToFirst();
            do{
                String id=cursor1.getString(0);
                String name=cursor1.getString(1);
                MemberIdList.add(id);
                MemberNameList1.add(name);
                //Log.d("qazx",""+MemberNameList1);
            }while (cursor1.moveToNext());
        }
    }

    protected static void retrieveExpenseStatus(){
        MemberNameList2.clear();
        MemberAmountList2.clear();
        //getLastExpId();
       // Log.d("eid2",""+expId);
        cursor=myDB.getExpStatus(expId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String for_whom=cursor.getString(1);
                double amount=cursor.getDouble(2);
                MemberNameList2.add(for_whom);
                MemberAmountList2.add(amount);
              //  Log.d("qaz",""+MemberNameList2);
                //Log.d("amt",""+MemberAmountList2);
            }while (cursor.moveToNext());
        }
    }

    protected void moveToExpenses(){
        ll_who_paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void addExpense(){
        btn_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String purpose="", date="", time="";

                    purpose=et_purpose.getText().toString().replace("'","''");
                    date=tv_date.getText().toString();
                    time=tv_time.getText().toString();

                    if (date.isEmpty()){
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        tv_date.setText(currentDate);
                        date=tv_date.getText().toString();
                    }
                    if (time.isEmpty()){
                        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        tv_time.setText(currentTime);
                        time=tv_time.getText().toString();
                    }

                if (!purpose.isEmpty()){
                    DecimalFormat df = new DecimalFormat(".##");
                    df.setRoundingMode(RoundingMode.DOWN);
                    boolean check=myDB.insertExpenses(groupId, name, Double.parseDouble(df.format(amount)), purpose, date, time);
                    closeKeyboard();
                    int budget=0;
                    Cursor cursor=myDB.getMemberBudget(groupId, name);
                    if (cursor.getCount()== 0){
                    }
                    else {
                        cursor.moveToFirst();
                        budget=cursor.getInt(3);
                    }
                    if (budget != 0){
                        myDB.updateBudgets(groupId, name, amount);
                    }

                    et_purpose.setText("");
                    tv_date.setText("");
                    tv_time.setText("");

                    fillMemberinList1(ExpenseDetails.this);
                    LL_whole_exp.setVisibility(View.GONE);
                    LL_whole_exp.setEnabled(false);
                    LL_status_1.setVisibility(View.VISIBLE);
                    tv_for_whom.setVisibility(View.VISIBLE);
                    lv_expense_list_2.setVisibility(View.GONE);
                    lv_expense_list_1.setVisibility(View.VISIBLE);
                    btn_add_eq_status.setVisibility(View.VISIBLE);
                    btn_add_sp_status.setVisibility(View.GONE);
                    tv_equal_amt.setTypeface(tv_equal_amt.getTypeface(), Typeface.BOLD_ITALIC);
                    tv_equal_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    tv_split_amt.setTypeface(tv_split_amt.getTypeface(),Typeface.ITALIC);
                    tv_split_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if (check){
                        getLastExpId();
                        showBudget();
                        MemberNameList0=new ArrayList<>();
                        retrieveMembers0();
                       // Log.d("lastid",expId+"");
                        //Log.d("maylist",MemberNameList0+"");
                        for (int i=0;i<MemberNameList0.size();i++){
                            myDB.insertExpStatus2(expId,MemberNameList0.get(i).replace("'","''"),0d, groupId);
                        }
                        selectAmountSplitType();
                    }
                }
                else {
                    Toast.makeText(ExpenseDetails.this, "Enter Purpose", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void showBudget(){
        double budget=0f, updated_bud=0f;
        Cursor cursor=myDB.getMemberBudget(groupId, name);
        if (cursor.getCount()== 0){
        }
        else {
            cursor.moveToFirst();
            budget=cursor.getDouble(3);
            updated_bud=cursor.getDouble(5);
        }

        if (budget != 0){
            double percentage;
            percentage = ((updated_bud / budget) * 100);
            Log.d("percentage",""+percentage);
            Log.d("updated_bud",""+updated_bud);
            Log.d("budget",""+budget);
            if (updated_bud <= 0){
                Snackbar.make(parentLayout, name+", with this expense your budget will be 0", Snackbar.LENGTH_LONG).show();
            }
            else if (percentage < 10.0){
                Snackbar.make(parentLayout, name+", with this expense you will spend more than 90% of your total budget",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    protected void selectAmountSplitType(){
        tv_equal_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            // ----- was trying to delete last exp status but didn't work -----
            public void onClick(View v) {
                getLastExpId();
                myDB.updateExpSplitAmtTo0(expId);
                fillMemberinList1(ExpenseDetails.this);
                lv_expense_list_1.setVisibility(View.VISIBLE);
                lv_expense_list_2.setVisibility(View.GONE);
                btn_add_eq_status.setVisibility(View.VISIBLE);
                btn_add_sp_status.setVisibility(View.GONE);
                tv_equal_amt.setTypeface(tv_equal_amt.getTypeface(), Typeface.BOLD_ITALIC);
                tv_equal_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv_split_amt.setTypeface(tv_split_amt.getTypeface(),Typeface.ITALIC);
                tv_split_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        });
        tv_split_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastExpId();
                myDB.updateExpSplitAmtTo0(expId);
                fillMemberinList2(ExpenseDetails.this);
                lv_expense_list_1.setVisibility(View.GONE);
                lv_expense_list_2.setVisibility(View.VISIBLE);
                btn_add_eq_status.setVisibility(View.GONE);
                btn_add_sp_status.setVisibility(View.VISIBLE);
                tv_equal_amt.setTypeface(tv_equal_amt.getTypeface(),Typeface.ITALIC);
                tv_equal_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv_split_amt.setTypeface(tv_split_amt.getTypeface(), Typeface.BOLD_ITALIC);
                tv_split_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
        });
    }

    // ----- Just an Intent after verifying amount and deleting irrelevant rows
    protected void addStatus(){
        btn_add_eq_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastExpId();
                Double split_amt;
                ArrayList<String> CheckedForWhomList = new ArrayList<>();
                for (int i=0; i<CustomEqualAmountListAdapter.CheckedForWhomList.size(); i++){
                    CheckedForWhomList.add(CustomEqualAmountListAdapter.CheckedForWhomList.get(i));
                }
                Log.d("CheckedForWhomList",""+CheckedForWhomList);
                Log.d("CheckedForWhomList2",""+CustomEqualAmountListAdapter.CheckedForWhomList);
                int count=CheckedForWhomList.size();
                split_amt=MemberAmt/count;
                DecimalFormat df = new DecimalFormat(".##");
                df.setRoundingMode(RoundingMode.DOWN);
                for (int i=0;i<count;i++){
                    myDB.updateExpSplitAmt(expId,CheckedForWhomList.get(i).replace("'","''"), Double.parseDouble(df.format(split_amt)));
                }
                if (count != 0){
                    updateDebts();
                    Toast.makeText(ExpenseDetails.this, "Expense Added", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ExpenseDetails.this,Expenses.class);
                    intent.putExtra("groupID", groupId);
                    intent.putExtra("groupNAME", groupName);
                    intent.putExtra("NAME", name);
                    amount=0d;
                    intent.putExtra("AMOUNT", amount);

                    Intent intentTrans=new Intent(ExpenseDetails.this,Transactions.class);
                    intentTrans.putExtra("expID", expId);
                    intentTrans.putExtra("mID", mid);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ExpenseDetails.this, "Select al least one member", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_add_sp_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Note ----- To verify that the total amount equals exp amount
                getLastExpId();
                double exp_amt=0, verify_amt=0;

                cursor=myDB.getExpenseDetails(expId);
                if (cursor.getCount()==0){
                }
                else {
                    cursor.moveToFirst();
                    exp_amt=cursor.getDouble(3);
                    //Toast.makeText(ExpenseDetails.this, ""+exp_amt, Toast.LENGTH_SHORT).show();
                }
                // ----- Here the 'equal amount' was not matching with the total amount when odd no of members were checked,
                // because I used Integer instead of float/double

                Cursor cursor1=myDB.getExpStatus(expId);
                if (cursor1.getCount()==0){
                }
                else {
                    cursor1.moveToFirst();
                    do {
                        verify_amt+=cursor1.getDouble(2);
                    }while (cursor1.moveToNext());
                }
                //Toast.makeText(ExpenseDetails.this, ""+verify_amt, Toast.LENGTH_SHORT).show();

                if (exp_amt==verify_amt){
                    // ----- Deleting the rows where people are not borrowing money for last expense
                    //myDB.deleteExpStatusWhere0();
                    updateDebts();

                    Toast.makeText(ExpenseDetails.this, "Expense Added", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ExpenseDetails.this,Expenses.class);
                    intent.putExtra("groupID", groupId);
                    intent.putExtra("NAME", name);
                    amount=0d;
                    intent.putExtra("AMOUNT", amount);

                    Intent intentTrans=new Intent(ExpenseDetails.this,Transactions.class);
                    intentTrans.putExtra("expID", expId);
                    intentTrans.putExtra("mID", mid);

                    startActivity(intent);
                }
                else {
                    Toast.makeText(ExpenseDetails.this, "Total amount differs with Expense Amount",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void closeKeyboard(){
        View view=this.getCurrentFocus();
        if (view!=null){
            InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    protected static void getLastExpId(){
        Cursor cursor=myDB.getLastExpenseId();
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToLast();
            expId=cursor.getString(0);
        }
    }

    protected void updateDebts(){
        getLastExpId();

        String who_paid="", for_whom="";
        double total_amt=0, individual_amt, who_paid_debt_amt=0;
        ArrayList <String> ForWhomNameList=new ArrayList<>();
        ArrayList <Double> ForWhomAmountList=new ArrayList<>();
        Cursor cursor=myDB.getExpenseDetails(expId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            who_paid=cursor.getString(2).replace("'","''");
            total_amt=cursor.getDouble(3);
        }

        Cursor cursor2=myDB.getExpStatus(expId);
        if (cursor2.getCount()==0){
        }
        else {
            cursor2.moveToFirst();
            do {
                for_whom=cursor2.getString(1).replace("'","''");
                individual_amt=cursor2.getDouble(2);

                ForWhomNameList.add(for_whom);
                ForWhomAmountList.add(individual_amt);
            }while (cursor2.moveToNext());
        }
        Log.d("bacde",who_paid+" "+total_amt+" "+ForWhomNameList+" "+ForWhomAmountList);

        if (ForWhomNameList.contains(who_paid)){
            int position=ForWhomNameList.indexOf(who_paid);
            Log.d("qazw",""+who_paid);
            Log.d("qaz",""+position);

            // ----- Removing the members who paid for themselves
            ForWhomNameList.remove(who_paid);

            who_paid_debt_amt=ForWhomAmountList.get(position);
            ForWhomAmountList.remove(position);
            Log.d("vbnm",""+ForWhomNameList+" "+ForWhomAmountList);
        }
        // ----- updating who_paid's debt
        myDB.updateDebts(groupId,who_paid,who_paid_debt_amt);

        // ----- updating for_whom's debt
        for (int i=0; i<ForWhomNameList.size();i++){
            myDB.updateDebts(groupId, ForWhomNameList.get(i), total_amt + ForWhomAmountList.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        if (LL_whole_exp.isEnabled()){
            Intent intent=new Intent(ExpenseDetails.this,Expenses.class);
            intent.putExtra("groupID", groupId);
            intent.putExtra("groupNAME",groupName);
            intent.putExtra("NAME", name);
            intent.putExtra("AMOUNT", amount);
            startActivity(intent);
        }
        else {
            // ----- if expense is added in expense table and back pressed
            getLastExpId();
            myDB.deleteLastExpense(expId);
            myDB.updateBudgets(groupId, name, -amount);
            //myDB.deleteLastExpStatus(expId);
        }
        LL_status_1.setVisibility(View.GONE);
        LL_whole_exp.setVisibility(View.VISIBLE);
        tv_for_whom.setVisibility(View.GONE);
        btn_add_eq_status.setVisibility(View.GONE);
        btn_add_sp_status.setVisibility(View.GONE);
        LL_whole_exp.setEnabled(true);
    }

}
// Note ----- cursor.getCount()==0 is imp when you install app for the first time