package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Member;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomEqualAmountListAdapter extends ArrayAdapter {
    ArrayList<String> ArrMemberID, ArrMemberName;
    public static ArrayList<String> AddForWhomList, CheckedForWhomList = new ArrayList<>(), UncheckedForWhomList;
    Activity context;
    String GroupId, expId;
    Double MemberAmt;
    public static DatabaseHelper myDB;
    public boolean isCheck[];

    public CustomEqualAmountListAdapter(Activity context, String GroupId, ArrayList<String> ArrMemberID,
                                        ArrayList<String> ArrMemberName, Double MemberAmt) {
        super(context,R.layout.custom_equal_amount_list,ArrMemberID);
        this.context = context;
        this.GroupId = GroupId;
        this.ArrMemberID = ArrMemberID;
        this.ArrMemberName = ArrMemberName;
        this.MemberAmt=MemberAmt;
        CheckedForWhomList.clear();

        if (ArrMemberName != null){
            isCheck = new boolean[ArrMemberName.size()];
        }

    }

    static class MyViewHolder{
        TextView tv_for_whom_members;
        CheckBox cb_for_whom_members;
        LinearLayout LL_for_whom_members;
        int position;

        MyViewHolder(View v){
            tv_for_whom_members=(TextView) v.findViewById(R.id.tv_for_whom_members);
            cb_for_whom_members=(CheckBox) v.findViewById(R.id.cb_for_whom_members);
            LL_for_whom_members=(LinearLayout)v.findViewById(R.id.LL_for_whom_members);
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView,@NonNull ViewGroup parent) {
        myDB=new DatabaseHelper(context);
        View row= convertView;
        final MyViewHolder holder;
        if (row == null){
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_equal_amount_list, parent, false);
            holder = new MyViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder=(MyViewHolder) row.getTag();
            if (isCheck[position]) {
                holder.cb_for_whom_members.setChecked(true);
            } else {
                holder.cb_for_whom_members.setChecked(false);
            }
        }
        holder.position = position;
        holder.tv_for_whom_members.setText(ArrMemberName.get(position));

        /*LL_for_whom_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You selected "+ArrMemberID.get(position), Toast.LENGTH_SHORT).show();
                String member_id=ArrMemberID.get(position);

                if (CheckList.contains(member_id)){
                    CheckList.remove(member_id);
                }
                else {
                    CheckList.add(member_id);
                }
                //if (member_id)
                //cb_for_whom_members.isChecked();
            }
        });*/

        getLastExpId();
        holder.cb_for_whom_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String for_whom_name=ArrMemberName.get(position);
                if (holder.cb_for_whom_members.isChecked()){
                    CheckedForWhomList.add(for_whom_name);
                    //UncheckedForWhomList.remove(for_whom_name);
                    isCheck[position] = true;
                }
                else {
                    //UncheckedForWhomList.add(for_whom_name);
                    CheckedForWhomList.remove(for_whom_name);
                    isCheck[position] = false;
                }
            }
        });
        return row;
    }

    public void getLastExpId(){
        Cursor cursor=myDB.getLastExpenseId();
        cursor.moveToLast();
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToLast();
            do{
                // retrieving from db
                expId=cursor.getString(0);
            }while (cursor.moveToNext());
        }
    }

    /*public ArrayList<String> getArrayList(){
        Log.d("aasdf6",""+CheckedForWhomList);
        return CheckedForWhomList;
    }*/

    public void insertAmt(){
        getLastExpId();
        Cursor cursor=myDB.getExpStatus(expId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            // ----- get for whom count
            Integer exp_count=cursor.getCount();

            Cursor cursorAmt=myDB.getExpenseDetails(expId);
            if (cursorAmt.getCount()==0){
            }
            else {
                cursorAmt.moveToFirst();
                Integer amt=cursorAmt.getInt(3);
                //Log.d("amt",""+amt);
                Integer splitedAmount;
                try {
                    splitedAmount=amt/exp_count;
                    //myDB.updateExpEqualAmt(expId,splitedAmount);
                    //Log.d("eid",""+expId);
                    Toast.makeText(context, ""+splitedAmount, Toast.LENGTH_SHORT).show();
                }
                catch (ArithmeticException e){
                    Toast.makeText(context, "At least one member should be selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
     /*
     //myDB.insertExpStatus(expId,for_whom_name);

     try {
        boolean check=myDB.insertExpStatus(expId,for_whom_name);
        if (check){
            //Toast.makeText(context, ""+1, Toast.LENGTH_SHORT).show();
        }
    }
                    catch (SQLiteConstraintException e){
        //Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
    }
    myDB.deleteExpStatus(for_whom_name);
    //CheckList.add(member_id);
    //Toast.makeText(context, ""+CheckList, Toast.LENGTH_SHORT).show();*/
}

// **** Note - Previously when I was updating directly

  /*holder.cb_for_whom_members.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String for_whom_name=ArrMemberName.get(position);
        Double MemberAmt1;
        if (isChecked){

        //count+=1;
        //Log.d("amount1",""+MemberAmt);
        //MemberAmt1=MemberAmt/count;
        //Toast.makeText(context, count+" "+MemberAmt1+" "+for_whom_name, Toast.LENGTH_SHORT).show();
        CheckedForWhomList.add(for_whom_name);
        UncheckedForWhomList.remove(for_whom_name);
        //CheckForWhomList.add(for_whom_name);
        //Log.d("asdf1",""+CheckForWhomList);
        }
        else {
        //count--;
        //Log.d("amount2",""+MemberAmt);
        //MemberAmt2=MemberAmt/count;
        //Toast.makeText(context, count+" "+MemberAmt2+" "+for_whom_name, Toast.LENGTH_SHORT).show();
        UncheckedForWhomList.add(for_whom_name);
        CheckedForWhomList.remove(for_whom_name);

        //CheckForWhomList.remove(for_whom_name);
        //Log.d("asdf2",""+CheckForWhomList);
        }
        //Toast.makeText(context, ""+CheckedForWhomList+" ", Toast.LENGTH_SHORT).show();
        //Log.d("newlist1",""+CheckedForWhomList);
        //Log.d("newlist2",""+UncheckedForWhomList);

        int count1=CheckedForWhomList.size();
        int count2=UncheckedForWhomList.size();
        //Log.d("counter",""+count1);
        try {
        MemberAmt1=MemberAmt/count1;
        DecimalFormat df = new DecimalFormat(".##");
        df.setRoundingMode(RoundingMode.DOWN);
        for (int i=0;i<count1;i++){
        myDB.updateExpSplitAmt(expId,CheckedForWhomList.get(i), Double.parseDouble(df.format(MemberAmt1)));
        }
        for (int i=0;i<count2;i++){
        myDB.updateExpSplitAmt(expId,UncheckedForWhomList.get(i),0d);
        }
        }
        catch (ArithmeticException e){
        // ----- here again nullifying the amount column because the control was escaping out of try block
        // due to divide by 0 exception. So that last unchecked member's amount column needs to be made null
        for (int i=0;i<count2;i++){
        myDB.updateExpSplitAmt(expId,UncheckedForWhomList.get(i),0d);
        }
        Toast.makeText(context, "No member is selected", Toast.LENGTH_SHORT).show();
        }

        //insertAmt();
        //Intent intent=new Intent(context,ExpenseDetails.class);
        //intent.putExtra("CheckLIST",CheckList);
        }
        });
        */