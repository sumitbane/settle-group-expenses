package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomMMSplitAmtListAdapter extends ArrayAdapter {
    ArrayList<String> ArrMemberID, ArrMemberName;
    ArrayList<Double> ArrMemberAmount;
    Activity context;
    EditText et_amount;
    public static String GroupId, Currency, mm_expId;
    Integer splited_amt, Amount;
    LinearLayout LL_split_amount;
    public static DatabaseHelper myDB;

    public CustomMMSplitAmtListAdapter(Activity context, String GroupId, ArrayList<String> ArrMemberName,
                                       ArrayList<Double> ArrMemberAmount, String Currency) {
        super(context,R.layout.custom_mm_split_amt_list,ArrMemberName);
        this.context = context;
        this.GroupId = GroupId;
        this.ArrMemberName = ArrMemberName;
        this.ArrMemberAmount=ArrMemberAmount;
        this.Currency=Currency;
    }
    public View getView(final int position, final View convertView, ViewGroup parent) {
        myDB = new DatabaseHelper(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View ListViewItem = inflater.inflate(R.layout.custom_mm_split_amt_list, null, true);

        final TextView tv_mm_for_whom=(TextView)ListViewItem.findViewById(R.id.tv_mm_for_whom);
        final TextView tv_mm_amount=(TextView)ListViewItem.findViewById(R.id.tv_mm_amount);
        final TextView tv_mm_currency1=(TextView)ListViewItem.findViewById(R.id.tv_mm_currency1);
        Button btn_edit_amt=(Button)ListViewItem.findViewById(R.id.btn_edit_amt);
        tv_mm_for_whom.setText(ArrMemberName.get(position));
        Log.d("zxcv",""+ArrMemberAmount);
        tv_mm_amount.setText(ArrMemberAmount.get(position).toString());
        tv_mm_currency1.setText(Currency);

        btn_edit_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMMLastExpId();
                LayoutInflater rename_inflater= context.getLayoutInflater();
                View tView=rename_inflater.inflate(R.layout.edit_for_whom_amount,null,true);

                final EditText et_split_amount=(EditText)tView.findViewById(R.id.et_split_amount);
                Button btn_save_amount=(Button)tView.findViewById(R.id.btn_save_amount);

                AlertDialog.Builder alert_builder=new AlertDialog.Builder(context);
                alert_builder.setView(tView);
                alert_builder.setCancelable(true);

                final AlertDialog dialog=alert_builder.create();
                dialog.show();

                btn_save_amount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String for_whom=ArrMemberName.get(position);
                        String str_updateAmount=et_split_amount.getText().toString();
                        double updateAmount=0;

                        if (str_updateAmount.equals("")){
                            Toast.makeText(context, "Enter a valid amount ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            updateAmount=Integer.parseInt(str_updateAmount);
                            DecimalFormat df = new DecimalFormat(".##");
                            df.setRoundingMode(RoundingMode.DOWN);
                            myDB.updateMMExpSplitAmt(mm_expId, for_whom.replace("'","''"),
                                    Double.parseDouble(df.format(updateAmount)));
                            MultipleMembersExpenses.fillMemberinList3(context);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return ListViewItem;
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