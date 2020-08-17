package com.example.Settle_Group_Expenses;

import android.app.Activity;
import android.database.Cursor;
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

public class CustomSplitAmountAdapterList extends ArrayAdapter {
    ArrayList<String> ArrMemberID, ArrMemberName;
    ArrayList<Double> ArrMemberAmount;
    Activity context;
    EditText et_amount;
    String GroupId, Currency, expId;
    Integer splited_amt, Amount;
    LinearLayout LL_split_amount;
    public static DatabaseHelper myDB;

    public CustomSplitAmountAdapterList(Activity context, String GroupId, ArrayList<String> ArrMemberName,
                                        ArrayList<Double> ArrMemberAmount, String Currency) {
        super(context,R.layout.custom_split_amount_list,ArrMemberName);
        this.context = context;
        this.GroupId = GroupId;
        this.ArrMemberID = ArrMemberID;
        this.ArrMemberName = ArrMemberName;
        this.ArrMemberAmount=ArrMemberAmount;
        this.Currency=Currency;
    }
    public View getView(final int position, final View convertView, ViewGroup parent) {
        myDB = new DatabaseHelper(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View ListViewItem = inflater.inflate(R.layout.custom_split_amount_list, null, true);

        final TextView tv_for_whom_members=(TextView)ListViewItem.findViewById(R.id.tv_for_whom_members);
        final TextView tv_amount=(TextView)ListViewItem.findViewById(R.id.tv_amount);
        final TextView tv_currency=(TextView)ListViewItem.findViewById(R.id.tv_currency);
        Button btn_edit_amt=(Button)ListViewItem.findViewById(R.id.btn_edit_amt);
        tv_for_whom_members.setText(ArrMemberName.get(position));
        tv_amount.setText(ArrMemberAmount.get(position)+"");
        tv_currency.setText(Currency);
        //getLastExpId();

        /*try {
            boolean check=myDB.insertExpStatus(expId,ArrMemberName.get(position));
            if (check){

            }
        }
        catch (SQLiteConstraintException e){
            //Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }*/

        /*try {
            tv_amount.setText(ArrMemberAmount.get(position));
        }
        catch (Exception e){
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }*/

        /*if (ArrMemberAmount.get(position)==null){
            tv_amount.setText("0");
        }
        tv_currency.setText(Currency);*/

        //Toast.makeText(context, ""+ArrMemberAmount.get(position), Toast.LENGTH_SHORT).show();

        // ----- Used toast as insertExpStatus was getting called 6 or more times -----
        //Toast.makeText(context, ""+1, Toast.LENGTH_SHORT).show();
        // referred https://stackoverflow.com/questions/2618272/custom-listview-adapter-getview-method-being-called-multiple-times-and-in-no-co
        // finally it worked with try-catch

        //String demo=et_amount.getText(ArrMemberAmount.get(position));
        //Toast.makeText(context, ""+demo, Toast.LENGTH_SHORT).show();

        /*et_amount.addTextChangedListener(new TextWatcher() {
            private String old_amount = "";
            private String new_amount = "";
            String for_whom_name=ArrMemberName.get(position);
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getLastExpId();
                //Toast.makeText(context, ""+expId, Toast.LENGTH_SHORT).show();
                new_amount = s.toString().replace(old_amount, "").trim();
                //Log.d("amt6",""+new_amount);
                //Toast.makeText(context, newText, Toast.LENGTH_SHORT).show();
                //amt=Integer.parseInt(str_amt);
                //myDB.updateExpStatus(expId,for_whom_name);
                myDB.updateExpSplittedAmt(expId, for_whom_name,new_amount);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

        btn_edit_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Amount=ArrMemberAmount.get(position);
                getLastExpId();

                LayoutInflater rename_inflater= context.getLayoutInflater();
                View tView=rename_inflater.inflate(R.layout.edit_for_whom_amount,null,true);

                final EditText et_split_amount=(EditText)tView.findViewById(R.id.et_split_amount);
                Button btn_save_amount=(Button)tView.findViewById(R.id.btn_save_amount);

                AlertDialog.Builder alert_builder=new AlertDialog.Builder(context);
                alert_builder.setView(tView);
                alert_builder.setCancelable(true);

                final AlertDialog dialog=alert_builder.create();
                dialog.show();

                //et_split_amount.setText(Amount);

                btn_save_amount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String for_whom=ArrMemberName.get(position).replace("'","''");
                        String str_updateAmount=et_split_amount.getText().toString();
                        double updateAmount;
                        if (str_updateAmount.equals("")){
                            Toast.makeText(context, "Enter a valid amount ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            updateAmount=Double.parseDouble(str_updateAmount);
                            DecimalFormat df = new DecimalFormat(".##");
                            df.setRoundingMode(RoundingMode.DOWN);
                            myDB.updateExpSplitAmt(expId, for_whom, Double.parseDouble(df.format(updateAmount)));
                            ExpenseDetails.fillMemberinList2(context);
                            //Log.d("amount5",""+ArrMemberAmount.get(position));
                            dialog.dismiss();
                        }
                    }
                });
            }
        });


        return ListViewItem;
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
}