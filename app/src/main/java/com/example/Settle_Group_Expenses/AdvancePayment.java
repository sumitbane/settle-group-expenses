package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdvancePayment extends AppCompatActivity {

    public static String groupId, payId;
    TextView  tv_ap_currency, tv_exp_mem_name, tv_date, tv_time;
    EditText et_purpose, et_ap_amount;
    Button btn_exp_calendar, btn_add_ap;
    Spinner sp_ap_who_paid, sp_ap_to_whom;
    String currency, groupName, sp1, sp2, str_amt, who_paid, to_whom;
    Double amt;
    int mYear, mMonth, mDay, mHour, mMinute;
    public static DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_payment);

        Bundle bundle=getIntent().getExtras();
        if(bundle==null)
        {
            return;
        }
        else
        {
            groupId=bundle.getString("groupID");
            groupName=bundle.getString("groupNAME");
            //Log.d("cur",currency);
        }
        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        tv_ap_currency=(TextView)findViewById(R.id.tv_ap_currency);
        tv_exp_mem_name=(TextView)findViewById(R.id.tv_exp_mem_name);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_time=(TextView)findViewById(R.id.tv_time);
        et_purpose=(EditText)findViewById(R.id.et_purpose);
        et_ap_amount=(EditText)findViewById(R.id.et_ap_amount);
        btn_exp_calendar=(Button) findViewById(R.id.btn_exp_calendar);
        btn_add_ap=(Button) findViewById(R.id.btn_add_ap);
        sp_ap_who_paid=(Spinner)findViewById(R.id.sp_ap_who_paid);
        sp_ap_to_whom=(Spinner)findViewById(R.id.sp_ap_to_whom);

        btn_exp_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDateTime();
            }
        });

        currency();
        loadWhoSpinnerData();
        loadToWhomSpinnerData();
        addAdvancePayment();
    }
    public void currency() {
        Cursor cursor = myDB.getGroupDetails(groupId);
        if (cursor.getCount() == 0) {
        } else {
            cursor.moveToFirst();
            currency = cursor.getString(2);
            tv_ap_currency.setText(currency);
        }
    }
    public void loadWhoSpinnerData() {
        final List<String> Members1 = myDB.getAllMembers(groupId);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.expense_spinner_tv, Members1);
        sp_ap_who_paid.setAdapter(dataAdapter);
        Members1.add(0,"Select Member");
        sp_ap_who_paid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp1 =sp_ap_who_paid.getItemAtPosition(position).toString();
                if (sp1.equals("Select Member")){
                }
                else if (sp1.equals(sp2)){
                    Toast.makeText(AdvancePayment.this, "Selected for 'To Whom'", Toast.LENGTH_SHORT).show();
                    sp_ap_who_paid.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public void loadToWhomSpinnerData() {
        final List<String> Members2 = myDB.getAllMembers(groupId);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.expense_spinner_tv, Members2);
        sp_ap_to_whom.setAdapter(dataAdapter);
        Members2.add(0,"Select Member");
        sp_ap_to_whom.setSelection(0);
        sp_ap_to_whom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp2 =sp_ap_to_whom.getItemAtPosition(position).toString();
                if (sp2.equals("Select Member")){
                }
                else if (sp2.equals(sp1)){
                    Toast.makeText(AdvancePayment.this, "Selected for 'Who Paid'", Toast.LENGTH_SHORT).show();
                    sp_ap_to_whom.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                boolean is24HrFormat= DateFormat.is24HourFormat(AdvancePayment.this);;
                TimePickerDialog timePickerDialog = new TimePickerDialog(AdvancePayment.this,
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

    protected boolean checkAmount(){
        str_amt=et_ap_amount.getText().toString();
        if (str_amt.equals("")) {
            Toast.makeText(AdvancePayment.this, "Enter a Valid amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            amt = Double.parseDouble(et_ap_amount.getText().toString());
            if (amt<=0) {
                Toast.makeText(AdvancePayment.this, "Amount should be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
            else return true;
        }
    }

    protected boolean checkMember(){
        who_paid=sp_ap_who_paid.getSelectedItem().toString().replace("'","''");
        to_whom=sp_ap_to_whom.getSelectedItem().toString().replace("'","''");

        if (who_paid.equals("Select Member")){
            Toast.makeText(AdvancePayment.this, "Select 'who paid'", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (to_whom.equals("Select Member")){
            Toast.makeText(AdvancePayment.this, "Select 'to whom'", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    protected void addAdvancePayment(){
        btn_add_ap.setOnClickListener(new View.OnClickListener() {
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
                if (purpose.isEmpty()) {
                    Toast.makeText(AdvancePayment.this, "Enter Purpose", Toast.LENGTH_SHORT).show();
                }

                 if (!purpose.isEmpty()&&checkAmount()&&checkMember()) {
                     checkAmount();
                     checkMember();
                     boolean isInserted = myDB.insertAdvance_payments(amt, purpose, who_paid, to_whom, date, time, groupId);
                     if (isInserted) {
                         DecimalFormat df = new DecimalFormat(".##");
                         df.setRoundingMode(RoundingMode.DOWN);
                         double trun_amt=Double.parseDouble(df.format(amt));

                         // ----- updating who_paid's debt
                         myDB.updateDebts(groupId, who_paid, -trun_amt);
                         // ----- updating for_whom's debt
                         myDB.updateDebts(groupId,to_whom,trun_amt);

                         et_purpose.setText("");
                         tv_date.setText("");
                         tv_time.setText("");
                         et_ap_amount.setText("0");
                         sp_ap_who_paid.setSelection(0);
                         sp_ap_to_whom.setSelection(0);
                         Toast.makeText(AdvancePayment.this, "Payment Added", Toast.LENGTH_SHORT).show();
                     }
                 }
            }
        });
    }
}