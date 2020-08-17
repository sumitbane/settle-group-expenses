package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MemberDetails extends AppCompatActivity {
    TextView tv_date,tv_time, tv_bal_budget;
    public static ListView listView_to_do_list;
    EditText ed_todo;
    Button add_to_do,save_member_details, btn_calendar_img;
    int mYear, mMonth, mDay, mHour, mMinute, notificationId=1;
    String groupId, groupName, memberName;
    LinearLayout LL_bud;

    public static String memberID;
    public static ArrayList<String> ToDoIdList, ToDoList, ToDoDateList, ToDoTimeList;
    public static DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        Bundle bundle=getIntent().getExtras();
        if(bundle==null)
        {
            return;
        }
        else
        {
            groupId=bundle.getString("groupID");
            groupName=bundle.getString("groupNAME");
            memberID=bundle.getString("memberID");
            memberName=bundle.getString("memberNAME").replace("'","''");
            //Toast.makeText(this, memberID, Toast.LENGTH_SHORT).show();
        }
        listView_to_do_list=(ListView)findViewById(R.id.listView_to_do_list);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_bal_budget=(TextView)findViewById(R.id.tv_bal_budget);
        ed_todo=(EditText)findViewById(R.id.ed_todo);
        add_to_do=(Button)findViewById(R.id.add_to_do);
        //save_member_details=(Button)findViewById(R.id.save_member_details);
        //btn_date=(Button)findViewById(R.id.btn_date);
        //btn_time=(Button)findViewById(R.id.btn_time);
        btn_calendar_img=(Button)findViewById(R.id.btn_calendar_img);
        LL_bud=(LinearLayout)findViewById(R.id.LL_bud);

        myDB=new DatabaseHelper(this);
        getSupportActionBar().setTitle(memberName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        addTo_Do();
        setBal_budget();

        //saveDetails();
        /*btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDate();
            }
        });
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTime();
            }
        });*/
        btn_calendar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDateTime();
            }
        });
        fillToDoinList(MemberDetails.this);
        onStart();
    }

    protected void setBal_budget(){
        double bal_bud0 = 0;
        Cursor cursor=myDB.getMemberBudget(groupId, memberName);
        if (cursor.getCount() == 0){}
        else {
            cursor.moveToFirst();
            bal_bud0= cursor.getDouble(5);
        }
        Log.d("bal_bud",""+bal_bud0);
        if (bal_bud0 > 0){
            String currency="";
            Cursor cursor1=myDB.getGroupDetails(groupId);
            if (cursor1.getCount() == 0){
            }
            else {
                cursor1.moveToFirst();
                currency=cursor1.getString(2);
            }

            LL_bud.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat(".##");
            df.setRoundingMode(RoundingMode.DOWN);
            double bal_bud= Double.parseDouble(df.format(bal_bud0));
            Log.d("bal_bud2",""+bal_bud);
            tv_bal_budget.setText("Balance Budget : "+ bal_bud+" "+currency);
        }
    }

    protected void addTo_Do(){

        add_to_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to_do="", date="", time="";

                to_do=ed_todo.getText().toString().replace("'","''");
                date=tv_date.getText().toString();
                time=tv_time.getText().toString();

                if (to_do.isEmpty()){
                    Toast.makeText(MemberDetails.this, "Enter To-Do", Toast.LENGTH_SHORT).show();
                }
                else if (date.isEmpty()){
                    Toast.makeText(MemberDetails.this, "Select Date", Toast.LENGTH_SHORT).show();
                }
                else if (time.isEmpty()){
                    Toast.makeText(MemberDetails.this, "Select Time", Toast.LENGTH_SHORT).show();
                }
                else{
                    myDB.insertTo_Do(Integer.parseInt(memberID), to_do, date, time, groupId);
                    ed_todo.setText("");
                    tv_date.setText("");
                    tv_time.setText("");
                    fillToDoinList(MemberDetails.this);
                }
            }
        });
    }
    /*public void saveDetails(){
        save_member_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberDetails.this,AddMembers.class);
                intent.putExtra("groupID",groupId);
                startActivity(intent);
            }
        });
    }*/
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
                boolean is24HrFormat= DateFormat.is24HourFormat(MemberDetails.this);;
                TimePickerDialog timePickerDialog = new TimePickerDialog(MemberDetails.this,
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

        /*Intent intent=new Intent(MemberDetails.this, AlarmReceiver.class);
        intent.putExtra("NotificationId",notificationId);
        intent.putExtra("todo",ed_todo.getText().toString());

        PendingIntent pendingIntent=PendingIntent.getBroadcast(MemberDetails.this,0,intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar starTime=Calendar.getInstance();
        //starTime.set(Calendar.YEAR,mYear);
        //starTime.set(Calendar.MONTH,mMonth);
        //starTime.set(Calendar.DAY_OF_MONTH,mDay);
        starTime.set(Calendar.HOUR_OF_DAY,mHour);
        starTime.set(Calendar.MINUTE,mMinute);
        starTime.set(Calendar.SECOND,0);

        long alarmStartTime=starTime.getTimeInMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP,alarmStartTime,pendingIntent);
        //Toast.makeText(MemberDetails.this, "Done", Toast.LENGTH_SHORT).show();*/
    }

    protected static void fillToDoinList(Activity context){
        ToDoIdList=new ArrayList<>();
        ToDoList=new ArrayList<String>();
        ToDoDateList=new ArrayList<String>();
        ToDoTimeList=new ArrayList<String>();

        Cursor cursor=myDB.getToDo(memberID);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do {
                String id=cursor.getString(0);
                String todo=cursor.getString(2);
                String date=cursor.getString(3);
                String time=cursor.getString(4);
                ToDoIdList.add(id);
                ToDoList.add(todo);
                ToDoDateList.add(date);
                ToDoTimeList.add(time);
            }while (cursor.moveToNext());
            CustomToDoListAdapter adapter=new CustomToDoListAdapter(context,ToDoIdList, ToDoList, ToDoDateList, ToDoTimeList);
            listView_to_do_list.setAdapter(adapter);
        }
    }
    protected void onStart(){
        fillToDoinList(MemberDetails.this);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(MemberDetails.this,AddMembers.class);
        intent.putExtra("groupID",groupId);
        intent.putExtra("groupNAME",groupName);
        startActivity(intent);
    }
}
// Receive gid and mid from Add Members
// Send gid and mid to Add members again