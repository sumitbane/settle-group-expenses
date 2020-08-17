package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomMMEqualAmtListAdapter extends ArrayAdapter {
    ArrayList<String> ArrMemberID, ArrMemberName, ArrMemberAmount;
    public static ArrayList<String> CheckedMMForWhomList= new ArrayList<>();
    Activity context;
    EditText et_amount;
    public static String GroupId, Currency, mm_expId;
    Double MemberAmt, splited_amt;
    LinearLayout LL_split_amount;
    public static DatabaseHelper myDB;
    public boolean isCheck[];

    public CustomMMEqualAmtListAdapter(Activity context, String GroupId, ArrayList<String> ArrMemberName, Double MemberAmt) {
        super(context,R.layout.custom_mm_equal_amt_list,ArrMemberName);
        this.context = context;
        this.GroupId = GroupId;
        this.ArrMemberName = ArrMemberName;
        this.MemberAmt=MemberAmt;
        CheckedMMForWhomList.clear();

        if (ArrMemberName != null){
            isCheck = new boolean[ArrMemberName.size()];
        }
    }

    static class MyViewHolder{
        TextView tv_mm_for_whom_members;
        CheckBox cb_mm_for_whom_members;
        LinearLayout LL_split_amount;
        int position;

        MyViewHolder(View v){
            tv_mm_for_whom_members=(TextView) v.findViewById(R.id.tv_mm_for_whom_members);
            cb_mm_for_whom_members=(CheckBox) v.findViewById(R.id.cb_mm_for_whom_members);
            LL_split_amount=(LinearLayout)v.findViewById(R.id.LL_split_amount);
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        myDB = new DatabaseHelper(context);
        View row= convertView;
        final CustomMMEqualAmtListAdapter.MyViewHolder holder;
        if (row == null){
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_mm_equal_amt_list, parent, false);
            holder = new CustomMMEqualAmtListAdapter.MyViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder=(CustomMMEqualAmtListAdapter.MyViewHolder) row.getTag();
            if (isCheck[position]) {
                holder.cb_mm_for_whom_members.setChecked(true);
            } else {
                holder.cb_mm_for_whom_members.setChecked(false);
            }
        }
        holder.position = position;
        holder.tv_mm_for_whom_members.setText(ArrMemberName.get(position));
        getMMLastExpId();
        holder.cb_mm_for_whom_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String for_whom_name=ArrMemberName.get(position);
                if (holder.cb_mm_for_whom_members.isChecked()){
                    CheckedMMForWhomList.add(for_whom_name);
                    isCheck[position] = true;
                }
                else {
                    CheckedMMForWhomList.remove(for_whom_name);
                    isCheck[position] = false;
                }
            }
        });

        return row;
    }
    protected static void getMMLastExpId(){
        Cursor cursor=myDB.getMMLastExpenseId();
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToLast();
            mm_expId=cursor.getString(0);
            Log.d("lastq",mm_expId);
        }
    }
}