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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MultipleMembersExpenses extends AppCompatActivity {
    Context context;
    public static DatabaseHelper myDB;
    public static ArrayList<String> MemberIdList, MemberNameList1=new ArrayList<String>(),
            MemberNameList2 =new ArrayList<String>(), InsertMembersList, BudgetNameList;
    public static ArrayList <Double> MemberAmountList1=new ArrayList<Double>(), MemberAmountList2=new ArrayList<Double>();
    public static ArrayList <Double> BudgetAmountList, BudgetUpdatedAmList;
    public static String groupId, mm_expId;
    public static ListView lv_mm_who_paid, lv_mm_for_whom, lv_mm_expense_list_1, lv_mm_expense_list_2;
    public static Cursor cursor;

    TextView tv_mm_amount, tv_mm_currency, tv_date, tv_time, tv_mm_for_whom, tv_mm_equal_amt, tv_mm_split_amt, tv_budget_list;
    EditText et_mm_purpose, et_mm_amount;
    Button btn_mm_calendar, btn_add_mm_exp, btn_mm_add_who_paid, btn_add_status, btn_mm_add_eq_status, btn_mm_add_sp_status;
    LinearLayout parentLayout, LL_purpose, LL_amount, LL_who_paid, LL_for_whom, LL_for_whom_1, LL_for_whom_2;
    View bottom_sheet;
    BottomSheetBehavior bottomSheetBehavior;
    static String currency, groupName;
    int mYear, mMonth, mDay, mHour, mMinute;
    static Double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_members_expenses);

        Bundle bundle=getIntent().getExtras();
        if(bundle==null)
        {
            return;
        }
        else
        {
            groupId=bundle.getString("groupID");
            groupName=bundle.getString("groupNAME");
            currency=bundle.getString("CURRENCY");
        }
        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_mm_currency=(TextView)findViewById(R.id.tv_mm_currency);
        tv_mm_for_whom=(TextView)findViewById(R.id.tv_mm_for_whom);
        tv_mm_equal_amt=(TextView)findViewById(R.id.tv_mm_equal_amt);
        tv_mm_split_amt=(TextView)findViewById(R.id.tv_mm_split_amt);
        tv_budget_list=(TextView)findViewById(R.id.tv_budget_list);
        et_mm_purpose=(EditText)findViewById(R.id.et_mm_purpose);
        et_mm_amount=(EditText)findViewById(R.id.et_mm_amount);
        btn_mm_calendar=(Button)findViewById(R.id.btn_mm_calendar);
        btn_add_mm_exp=(Button)findViewById(R.id.btn_add_mm_exp);
        btn_mm_add_who_paid=(Button)findViewById(R.id.btn_mm_add_who_paid);
        btn_mm_add_eq_status=(Button)findViewById(R.id.btn_mm_add_eq_status);
        btn_mm_add_sp_status=(Button)findViewById(R.id.btn_mm_add_sp_status);
        //btn_add_status=(Button)findViewById(R.id.btn_add_status);
        lv_mm_who_paid=(ListView)findViewById(R.id.lv_mm_who_paid);
        lv_mm_expense_list_1=(ListView)findViewById(R.id.lv_mm_expense_list_1);
        lv_mm_expense_list_2=(ListView)findViewById(R.id.lv_mm_expense_list_2);
        LL_purpose=(LinearLayout)findViewById(R.id.LL_purpose);
        LL_amount=(LinearLayout)findViewById(R.id.LL_amount);
        LL_who_paid=(LinearLayout)findViewById(R.id.LL_who_paid);
        LL_for_whom=(LinearLayout)findViewById(R.id.LL_for_whom);
        parentLayout = findViewById(R.id.parentLayout);
        bottom_sheet=findViewById(R.id.bottom_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(bottom_sheet);
        //lv_mm_for_whom=(ListView)findViewById(R.id.lv_mm_for_whom);

//        tv_mm_amount.setText(amt.toString());
        tv_mm_currency.setText(currency);
        btn_mm_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDateTime();
            }
        });
        addExpense();
        getMMLastExpId();
        addWhoPaid();
        addStatus();
    }

    protected static void fillMemberinList1(Activity context){
        retrieveMMExpBridge();

        //Log.d("123",""+MemberNameList1);
        //Log.d("12345",""+MemberAmountList1);
        CustomMMWhoPaidListAdapter adapter1 =new CustomMMWhoPaidListAdapter(context,groupId,
                MemberNameList1, MemberAmountList1,currency);
        lv_mm_who_paid.setAdapter(adapter1);
    }

    protected static void fillMemberinList2(Activity context){
        retrieveMMExpBridge();
        //Log.d("names1",MemberNameList1+"");
        CustomMMEqualAmtListAdapter adapter=new CustomMMEqualAmtListAdapter(context,groupId,
                MemberNameList1, amount);
        lv_mm_expense_list_1.setAdapter(adapter);
        //getCheckedMembersList();
    }

    protected static void fillMemberinList3(Activity context){
        //retrieveMembers();
        //retrieveToWhomAmount();
        //retrieveExpenseStatus();String eid;
        retrieveMMExpBridge();
        //Log.d("xcv",""+MemberAmountList2);
        CustomMMSplitAmtListAdapter adapter =new CustomMMSplitAmtListAdapter(context,groupId, MemberNameList1,
                MemberAmountList2, currency);
        lv_mm_expense_list_2.setAdapter(adapter);
    }

    protected static void retrieveMMExpBridge(){
        MemberNameList1.clear();
        MemberAmountList1.clear();
        MemberAmountList2.clear();
        //getMMLastExpId();
        //Log.d("eid2",""+mm_expId);
        cursor=myDB.getMMExpBridge(mm_expId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String who_paid=cursor.getString(1);
                double amount1=cursor.getDouble(2);
                double amount2=cursor.getDouble(4);
                MemberNameList1.add(who_paid);
                MemberAmountList1.add(amount1);
                MemberAmountList2.add(amount2);
                //Log.d("amt8",""+MemberAmountList1);
            }while (cursor.moveToNext());
        }
    }

    public void addDateTime(){
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
                boolean is24HrFormat= DateFormat.is24HourFormat(MultipleMembersExpenses.this);;
                TimePickerDialog timePickerDialog = new TimePickerDialog(MultipleMembersExpenses.this,
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

    protected void addExpense(){
        btn_add_mm_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String purpose="", date="", time="", str_amt;
                purpose=et_mm_purpose.getText().toString().replace("'","''");
                date=tv_date.getText().toString();
                time=tv_time.getText().toString();
                str_amt=et_mm_amount.getText().toString();

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
                if (purpose.isEmpty()) {
                    Toast.makeText(MultipleMembersExpenses.this, "Enter Purpose", Toast.LENGTH_SHORT).show();
                }
                else if (str_amt.equals("")) {
                    Toast.makeText(MultipleMembersExpenses.this, "Enter a Valid amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    amount=Double.parseDouble(str_amt);
                    Log.d("amount5",""+amount);
                    if (amount <= 0) {
                        Toast.makeText(MultipleMembersExpenses.this, "Amount should be greater than 0",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        DecimalFormat df = new DecimalFormat(".##");
                        df.setRoundingMode(RoundingMode.DOWN);
                        boolean check= myDB.insertMMExpenses(groupId, Double.parseDouble(df.format(amount)), purpose, date, time);
                        if (check){
                            et_mm_purpose.setText("");
                            tv_date.setText("");
                            tv_time.setText("");
                            et_mm_amount.setText(""+0);

                            LL_purpose.setVisibility(View.GONE);
                            LL_amount.setVisibility(View.GONE);
                            btn_add_mm_exp.setVisibility(View.GONE);
                            LL_purpose.setEnabled(false);
                            LL_who_paid.setVisibility(View.VISIBLE);
                            LL_who_paid.setEnabled(true);
                            btn_mm_add_who_paid.setVisibility(View.VISIBLE);

                            getMMLastExpId();
                            InsertMembersList=new ArrayList<>();
                            retrieveMembers();
                            //Log.d("lastid",mm_expId+"");
                            //Log.d("maylist",InsertMembersList+"");
                            for (int i=0;i<InsertMembersList.size();i++){
                                myDB.insertMMExpBridge(mm_expId,InsertMembersList.get(i).replace("'","''"),
                                        0d, InsertMembersList.get(i).replace("'","''"),0d, groupId);
                                fillMemberinList1(MultipleMembersExpenses.this);
                            }
                            selectAmountSplitType();
                        }
                    }
                }
            }
        });
    }

    protected static void retrieveMembers(){
        MemberNameList1.clear();
        MemberAmountList1.clear();
        cursor=myDB.getMemberName(groupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                String name=cursor.getString(1);
                MemberNameList1.add(name);
                InsertMembersList.add(name);
                //MemberNameList2.add(name);
            }while (cursor.moveToNext());
        }
    }

    protected void addWhoPaid(){
        btn_mm_add_who_paid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMMLastExpId();
                    int exp_amt=0, verify_amt=0;

                    Cursor cursor1=myDB.getMMExpenses(mm_expId);
                    if (cursor1.getCount()==0){
                    }
                    else {
                        cursor1.moveToFirst();
                        exp_amt=cursor1.getInt(2);
                        //Toast.makeText(ExpenseDetails.this, ""+exp_amt, Toast.LENGTH_SHORT).show();
                    }
                    Cursor cursor2=myDB.getMMExpBridge(mm_expId);
                    if (cursor2.getCount()==0){}
                    else {
                        cursor2.moveToFirst();
                        do {
                            verify_amt+=cursor2.getInt(2);
                            Log.d("vamt1",""+verify_amt);
                        }while (cursor2.moveToNext());
                    }

                    if (exp_amt==verify_amt){
                        ArrayList <String> NameList1=new ArrayList<>();
                        ArrayList <Double> BudgetList1=new ArrayList<>();
                        NameList1.clear();
                        BudgetList1.clear();

                        Cursor cursor3 = myDB.getWhoPaidBudget(groupId, mm_expId);
                        if (cursor3.getCount()== 0){
                        }
                        else {
                            cursor3.moveToFirst();
                            do {
                                String names= cursor3.getString(1);
                                double budget= cursor3.getDouble(2);
                                NameList1.add(names);
                                BudgetList1.add(budget);
                            }while (cursor3.moveToNext());
                        }
                        for (int i=0; i<NameList1.size(); i++){
                            myDB.updateBudgets(groupId, NameList1.get(i).replace("'","''"), BudgetList1.get(i));
                        }

                        showBudget();
                        LL_who_paid.setVisibility(View.GONE);
                        btn_mm_add_who_paid.setVisibility(View.GONE);
                        tv_mm_for_whom.setVisibility(View.VISIBLE);
                        LL_for_whom.setVisibility(View.VISIBLE);
                        fillMemberinList2(MultipleMembersExpenses.this);
                        lv_mm_expense_list_1.setVisibility(View.VISIBLE);
                        lv_mm_expense_list_2.setVisibility(View.GONE);
                        btn_mm_add_eq_status.setVisibility(View.VISIBLE);
                        btn_mm_add_sp_status.setVisibility(View.GONE);
                        tv_mm_equal_amt.setTypeface(tv_mm_equal_amt.getTypeface(), Typeface.BOLD_ITALIC);
                        tv_mm_equal_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        tv_mm_split_amt.setTypeface(tv_mm_split_amt.getTypeface(),Typeface.ITALIC);
                        tv_mm_split_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        tv_mm_for_whom.setEnabled(true);
                        LL_who_paid.setEnabled(false);
                    }
                    else {
                        Toast.makeText(MultipleMembersExpenses.this, "Total amount differs with Expense amount",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

   protected void showBudget(){
        BudgetNameList=new ArrayList<>();
        BudgetAmountList= new ArrayList<>();
       BudgetUpdatedAmList= new ArrayList<>();

       BudgetNameList.clear();
       BudgetAmountList.clear();
       BudgetUpdatedAmList.clear();

       getMMLastExpId();
       Cursor cursor1 = myDB.getWhoPaidBudget(groupId, mm_expId);
       if (cursor1.getCount()== 0){
       }
       else {
           cursor1.moveToFirst();
           do {
               String names= cursor1.getString(1).replace("'","''");
               BudgetNameList.add(names);
           }while (cursor1.moveToNext());
       }

        double budget, updated_bud;
        for (int i=0; i< BudgetNameList.size(); i++){
            Cursor cursor=myDB.getMemberBudget(groupId, BudgetNameList.get(i));
            if (cursor.getCount()== 0){
            }
            else {
                cursor.moveToFirst();
                do {
                    budget=cursor.getDouble(3);
                    updated_bud=cursor.getDouble(5);
                    BudgetAmountList.add(budget);
                    BudgetUpdatedAmList.add(updated_bud);
                }while (cursor.moveToNext());
            }
        }

        Log.d("BudgetNameList",""+BudgetNameList);
        Log.d("BudgetAmountList",""+BudgetAmountList);
        Log.d("BudgetUpdatedAmList",""+BudgetUpdatedAmList);

       ArrayList <String> HundredNameList =new ArrayList<>(), NinetyNameList=new ArrayList<>();
       HundredNameList.clear();
       NinetyNameList.clear();

        for (int i=0; i< BudgetNameList.size(); i++){
            if (BudgetAmountList.get(i) != 0){
                double percentage;
                percentage = ((BudgetUpdatedAmList.get(i) / BudgetAmountList.get(i)) * 100);
                if (BudgetUpdatedAmList.get(i) <= 0){
                   HundredNameList.add(BudgetNameList.get(i));
                }
                else if (percentage < 10.0){
                    NinetyNameList.add(BudgetNameList.get(i));
                }
            }
        }
        Log.d("HundredNameList ", ""+HundredNameList +" NinetyNameList "+ NinetyNameList +"");
        if (! HundredNameList.isEmpty() || !NinetyNameList.isEmpty()){
            bottom_sheet.setVisibility(View.VISIBLE);
            if (! HundredNameList.isEmpty()){
                for (int i=0; i< HundredNameList.size(); i++){
                    tv_budget_list.append(HundredNameList.get(i)+", ");
                }
                String s1= " with this expense will your budget will be 0";
                tv_budget_list.append(s1);
                tv_budget_list.append("\n\n");
            }
            if (!NinetyNameList.isEmpty()){
                for (int i=0; i< NinetyNameList.size(); i++){
                    tv_budget_list.append(NinetyNameList.get(i)+", ");
                }
                String s2= " with this expense you will spend more than 90% of your total budget";
                tv_budget_list.append(s2);
            }
        }

    }

    protected void selectAmountSplitType(){
        tv_mm_equal_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            // ----- was trying to delete last exp status but didn't work -----
            public void onClick(View v) {
                getMMLastExpId();
                myDB.updateMMExpSplitAmtTo0(mm_expId);
                fillMemberinList2(MultipleMembersExpenses.this);
                lv_mm_expense_list_1.setVisibility(View.VISIBLE);
                lv_mm_expense_list_2.setVisibility(View.GONE);
                btn_mm_add_eq_status.setVisibility(View.VISIBLE);
                btn_mm_add_sp_status.setVisibility(View.GONE);
                tv_mm_equal_amt.setTypeface(tv_mm_equal_amt.getTypeface(), Typeface.BOLD_ITALIC);
                tv_mm_equal_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv_mm_split_amt.setTypeface(tv_mm_split_amt.getTypeface(),Typeface.ITALIC);
                tv_mm_split_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        });
        tv_mm_split_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMMLastExpId();
                myDB.updateMMExpSplitAmtTo0(mm_expId);
                fillMemberinList3(MultipleMembersExpenses.this);
                lv_mm_expense_list_1.setVisibility(View.GONE);
                lv_mm_expense_list_2.setVisibility(View.VISIBLE);
                btn_mm_add_eq_status.setVisibility(View.GONE);
                btn_mm_add_sp_status.setVisibility(View.VISIBLE);
                tv_mm_equal_amt.setTypeface(tv_mm_equal_amt.getTypeface(),Typeface.ITALIC);
                tv_mm_equal_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv_mm_split_amt.setTypeface(tv_mm_split_amt.getTypeface(), Typeface.BOLD_ITALIC);
                tv_mm_split_amt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
        });
    }

    protected void addStatus(){

        btn_mm_add_eq_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMMLastExpId();
                Double split_amt;
                ArrayList<String> CheckedMMForWhomList = new ArrayList<>();
                for (int i=0; i<CustomMMEqualAmtListAdapter.CheckedMMForWhomList.size(); i++){
                    CheckedMMForWhomList.add(CustomMMEqualAmtListAdapter.CheckedMMForWhomList.get(i));
                }
                Log.d("CheckedForWhomList",""+CheckedMMForWhomList);
                Log.d("CheckedForWhomList2",""+CustomMMEqualAmtListAdapter.CheckedMMForWhomList);
                int count=CheckedMMForWhomList.size();
                split_amt=amount/count;
                DecimalFormat df = new DecimalFormat(".##");
                df.setRoundingMode(RoundingMode.DOWN);
                for (int i=0;i<count;i++){
                    myDB.updateMMExpSplitAmt(mm_expId,CheckedMMForWhomList.get(i).replace("'","''"),
                            Double.parseDouble(df.format(split_amt)));
                }

                if (count !=0){
                    updateDebts();

                    Toast.makeText(MultipleMembersExpenses.this, "Expense Added", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MultipleMembersExpenses.this,Expenses.class);
                    intent.putExtra("groupID", groupId);
                    intent.putExtra("groupNAME", groupName);
                    //intent.putExtra("NAME", name);
                    amount=0d;
                    intent.putExtra("AMOUNT", amount);

                    Intent intentTrans=new Intent(MultipleMembersExpenses.this,Transactions.class);
                    intentTrans.putExtra("expID", mm_expId);
                    //intentTrans.putExtra("mID", mid);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MultipleMembersExpenses.this, "Select al least one member", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_mm_add_sp_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Note ----- To verify that the total amount equals exp amount
                getMMLastExpId();
                double exp_amt=0, verify_amt=0;

                cursor=myDB.getMMExpenses(mm_expId);
                if (cursor.getCount()==0){
                }
                else {
                    cursor.moveToFirst();
                    exp_amt=cursor.getDouble(2);
                }
                // ----- Here the 'equal amount' was not matching with the total amount when odd no of members were checked,
                // because I used Integer instead of float/double

                Cursor cursor1=myDB.getMMExpBridge(mm_expId);
                if (cursor1.getCount()==0){
                }
                else {
                    cursor1.moveToFirst();
                    do {
                        verify_amt+=cursor1.getDouble(4);
                    }while (cursor1.moveToNext());
                }

                if (exp_amt==verify_amt){
                    // ----- Deleting the rows where people are not borrowing money for last expense
                    //myDB.deleteMMExpBridgeWhere0();

                    updateDebts();

                    Toast.makeText(MultipleMembersExpenses.this, "Expense Added", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MultipleMembersExpenses.this,Expenses.class);
                    intent.putExtra("groupID", groupId);
                    //intent.putExtra("NAME", name);
                    int amt=0;
                    intent.putExtra("AMOUNT", amt);

                    Intent intentTrans=new Intent(MultipleMembersExpenses.this,Transactions.class);
                    intentTrans.putExtra("expID", mm_expId);
                    //intentTrans.putExtra("mID", mid);

                    startActivity(intent);
                }
                else {
                    Toast.makeText(MultipleMembersExpenses.this, "Total amount differs with Expense Amount",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected static void getMMLastExpId(){
        Cursor cursor=myDB.getMMLastExpenseId();
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToLast();
            mm_expId=cursor.getString(0);
        }
    }

    protected void updateDebts(){
        getMMLastExpId();
        String who_paid="", for_whom="";
        double total_amt=0, who_paid_amt=0,  for_whom_amt=0;
        ArrayList <String> WhoPaidNameList=new ArrayList<>(), ForWhomNameList=new ArrayList<>();
        ArrayList <Double> WhoPaidAmountList=new ArrayList<>(), ForWhomAmountList=new ArrayList<>(), ForWhomAmountList2=new ArrayList<>();
        Cursor cursor=myDB.getMMExpenses(mm_expId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToNext();
            total_amt=cursor.getInt(2);
        }

        Cursor cursor2=myDB.getMMExpBridge(mm_expId);
        if (cursor2.getCount()==0){
        }
        else {
            cursor2.moveToFirst();
            do {
                who_paid=cursor2.getString(1);
                who_paid_amt=cursor2.getDouble(2);
                for_whom=cursor2.getString(3);
                for_whom_amt=cursor2.getDouble(4);

                WhoPaidNameList.add(who_paid);
                WhoPaidAmountList.add(who_paid_amt);
                ForWhomNameList.add(for_whom);
                ForWhomAmountList.add(for_whom_amt);
            }while (cursor2.moveToNext());
        }

        // ----- Removing the members who paid for themselves
        int j=WhoPaidAmountList.size();
        for (int i=0; i<j;i++){
            if (WhoPaidAmountList.contains(0)){
                int position=WhoPaidAmountList.indexOf(0);
                WhoPaidNameList.remove(position);
                WhoPaidAmountList.remove(position);
                // ----- Now adding those members who aren't paying for themselves to a new ForWhom list
                ForWhomAmountList2.add(ForWhomAmountList.get(position));
                // ----- Removing that member for old list
                ForWhomAmountList.remove(position);
            }
            j--;
        }
        //Log.d("bbbv1",""+WhoPaidNameList);
        //Log.d("bbbv2",""+ForWhomNameList);
        ForWhomNameList.removeAll(WhoPaidNameList);
        //Log.d("bbbv3",""+ForWhomNameList);
        //Log.d("bbbv4",""+ForWhomAmountList);
        //Log.d("bbbv5",""+ForWhomAmountList2);

        // ----- updating who_paid's debt
        for (int i=0; i<WhoPaidNameList.size();i++){
            myDB.updateDebts(groupId, WhoPaidNameList.get(i).replace("'","''"),
                    (total_amt-(WhoPaidAmountList.get(i) - ForWhomAmountList.get(i))));
        }

        // ----- updating for_whom's debt
        for (int i=0; i<ForWhomNameList.size();i++){
            myDB.updateDebts(groupId, ForWhomNameList.get(i).replace("'","''"),
                    total_amt + ForWhomAmountList2.get(i));
        }
    }

    public void onBackPressed() {
        if (LL_purpose.isEnabled()){
            Intent intent=new Intent(MultipleMembersExpenses.this,Expenses.class);
            intent.putExtra("groupID", groupId);
            intent.putExtra("groupNAME",groupName);
            startActivity(intent);
        }
        else if (LL_who_paid.isEnabled()){
            // ----- if expense is added in expense table and back pressed
            getMMLastExpId();
            myDB.deleteMMLastExpense(mm_expId);
            LL_who_paid.setVisibility(View.GONE);
            btn_mm_add_who_paid.setVisibility(View.GONE);
            LL_purpose.setVisibility(View.VISIBLE);
            LL_purpose.setEnabled(true);
            LL_amount.setVisibility(View.VISIBLE);
            btn_add_mm_exp.setVisibility(View.VISIBLE);
            btn_mm_add_sp_status.setVisibility(View.GONE);
        }
        else if (tv_mm_for_whom.isEnabled()){
            tv_mm_for_whom.setVisibility(View.GONE);
            LL_for_whom.setVisibility(View.GONE);
            btn_mm_add_eq_status.setVisibility(View.GONE);
            btn_mm_add_sp_status.setVisibility(View.GONE);
            LL_who_paid.setVisibility(View.VISIBLE);
            btn_mm_add_who_paid.setVisibility(View.VISIBLE);
            LL_who_paid.setEnabled(true);
            LL_purpose.setEnabled(false);

            ArrayList <String> NameList2=new ArrayList<>();
            ArrayList <Double> BudgetList2=new ArrayList<>();
            NameList2.clear();
            BudgetList2.clear();
            Cursor cursor1 = myDB.getWhoPaidBudget(groupId, mm_expId);
            if (cursor1.getCount()== 0){
            }
            else {
                cursor1.moveToFirst();
                do {
                    String names= cursor1.getString(1);
                    double budget= cursor1.getDouble(2);
                    NameList2.add(names);
                    BudgetList2.add(budget);
                }while (cursor1.moveToNext());
            }
            for (int i=0; i<NameList2.size(); i++){
                myDB.updateBudgets(groupId, NameList2.get(i), - BudgetList2.get(i));
            }
        }
    }
}