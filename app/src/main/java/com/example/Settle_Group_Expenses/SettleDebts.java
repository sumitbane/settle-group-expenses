package com.example.Settle_Group_Expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.TimingLogger;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SettleDebts extends AppCompatActivity {
    public static DatabaseHelper myDB;
    public static String groupId, currency;
    public static ArrayList<String> NameList, WhoPaysList, ToWhomList , PayersNameList, ReceiversNameList ;
    public static ArrayList<Double> DebtList, UpdatedDebtList, HowMuchList, PayersAmountList, ReceiversAmountList;
    String groupName;
    ListView lv_debts;
    LinearLayout parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_debts);
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

        lv_debts=(ListView)findViewById(R.id.lv_debts);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        SettleDebtsAlgorithm();
    }

    protected void SettleDebtsAlgorithm(){
        long startms= System.currentTimeMillis();
        long startns= System.nanoTime();

        NameList=new ArrayList<>();
        //NameList2=new ArrayList<>();
        DebtList=new ArrayList<>();
        //DebtList2=new ArrayList<>();
        UpdatedDebtList=new ArrayList<>();
        //UpdatedDebtList2=new ArrayList<>();
        WhoPaysList=new ArrayList<>();
        ToWhomList=new ArrayList<>();
        HowMuchList=new ArrayList<>();

        NameList.clear();
        //NameList2.clear();
        DebtList.clear();
        //DebtList2.clear();
        UpdatedDebtList.clear();
        //UpdatedDebtList2.clear();
        WhoPaysList.clear();
        ToWhomList.clear();
        HowMuchList.clear();

        DecimalFormat df = new DecimalFormat(".##");
        df.setRoundingMode(RoundingMode.DOWN);

        int count=0;
        Cursor cursor=myDB.getMemberName(groupId);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            count=cursor.getCount();
            do{
                String name=cursor.getString(1);
                double debts=cursor.getDouble(4);
                NameList.add(name);
                DebtList.add(debts);
            }while (cursor.moveToNext());
            Log.d("NameList1",NameList+"");
            Log.d("DebtList1",DebtList+"");
        }

        double total_amt=0;
        for (int i=0; i<DebtList.size(); i++){
            total_amt+=DebtList.get(i);
        }

        double normal_amt=total_amt/count;

        Log.d("total_amt",total_amt+"");
        Log.d("normal_amt",normal_amt+"");

        for (int i=0; i< DebtList.size(); i++){
            UpdatedDebtList.add(Double.parseDouble(df.format(DebtList.get(i) - normal_amt)));
            //UpdatedDebtList2.add(DebtList.get(i) - normal_amt);
        }
        //float normal_amt=Float.parseFloat(String.format("%.2f", (DebtList.get(i) - normal_amt)));
        Log.d("UpdatedDebtList",UpdatedDebtList+"");
        //Log.d("UpdatedDebtList2",UpdatedDebtList2+"");

        //int counter1=UpdatedDebtList.size();

        // ----- Directly settling among those members who have to pay or receive the same amount.
        for (int i=0; i< UpdatedDebtList.size(); i++){
            for (int j=0; j< UpdatedDebtList.size(); j++){
                if (UpdatedDebtList.get(i)!=0 && UpdatedDebtList.get(j)!=0 && UpdatedDebtList.get(i) + UpdatedDebtList.get(j) == 0){
                    if (UpdatedDebtList.get(i)>0){
                        //myDB.insertDebtSettlement(groupId, NameList.get(i), NameList.get(j), UpdatedDebtList.get(j) );
                        WhoPaysList.add(NameList.get(i));
                        ToWhomList.add(NameList.get(j));
                        HowMuchList.add(UpdatedDebtList.get(i));

                        Log.d("WhoPaysLi",WhoPaysList+"");
                        Log.d("ToWhomLi",ToWhomList+"");
                        Log.d("HowMuchLi",HowMuchList+"");
                        Log.d("Update",UpdatedDebtList+"");
                        Log.d("NameL",NameList+"");
                       // NameList.remove(j);
                        //NameList.remove(i);
                        //UpdatedDebtList.remove(j);
                        //UpdatedDebtList.remove(i);
                        //NameList.set(i,""+0);
                        //NameList.set(j,""+0);
                        UpdatedDebtList.set(i,0d);
                        UpdatedDebtList.set(j,0d);
                        //counter1--;

                    }
                    else if (UpdatedDebtList.get(i)<0){
                        //myDB.insertDebtSettlement(groupId, NameList.get(j), NameList.get(i), UpdatedDebtList.get(i) );
                        WhoPaysList.add(NameList.get(j));
                        ToWhomList.add(NameList.get(i));
                        HowMuchList.add(UpdatedDebtList.get(j));

                        Log.d("zzxxc","2");
                       // UpdatedDebtList.remove(i);
                        //UpdatedDebtList.remove(j-1);
                        //j--;
                        //NameList.remove(i);
                        //NameList.remove(j-1);
                        //counter1--;

                       // NameList.set(i,""+0);
                       // NameList.set(j,""+0);
                        UpdatedDebtList.set(i,0d);
                        UpdatedDebtList.set(j,0d);
                        Log.d("WhoPaysList",WhoPaysList+"");
                        Log.d("ToWhomList",ToWhomList+"");
                        Log.d("HowMuchList",HowMuchList+"");
                        Log.d("UpdatedDebtList",UpdatedDebtList+"");
                        Log.d("NameList",NameList+"");
                    }
                    //counter1--;
                    //counter2--;
                }
            }
        }

        // ----- Removing those members whose debts have been settled
        int counter1=NameList.size();
        int counter2=UpdatedDebtList.size();

        for (int i=0; i< counter2; i++){
            if (UpdatedDebtList.get(i) == 0){
                NameList.set(i, ""+0);
            }
        }

        for (int i=0; i< counter1; i++){
            if (NameList.get(i) == ""+0){
                NameList.remove(i);
                counter1--;
                i--;
            }
        }
        for (int i=0; i< counter2; i++){
            if (UpdatedDebtList.get(i) == 0){
                UpdatedDebtList.remove(i);
                counter2--;
                i--;
            }
        }
        Log.d("NameList5",""+NameList);
        Log.d("DebtList5",""+UpdatedDebtList);

        PayersNameList=new ArrayList<>();
        PayersAmountList=new ArrayList<>();
        ReceiversNameList=new ArrayList<>();
        ReceiversAmountList=new ArrayList<>();

        PayersNameList.clear();
        PayersAmountList.clear();
        ReceiversNameList.clear();
        ReceiversAmountList.clear();

        // Settling the debts of the remaining members.
        for (int i=0; i< UpdatedDebtList.size(); i++){
            if (UpdatedDebtList.get(i) > 0){
                PayersNameList.add(NameList.get(i));
                PayersAmountList.add(UpdatedDebtList.get(i));
            }
            else{
                ReceiversNameList.add(NameList.get(i));
                ReceiversAmountList.add(UpdatedDebtList.get(i));
            }
        }
        Log.d("PayersNameList",""+PayersNameList);
        Log.d("PayersAmountList",""+PayersAmountList);
        Log.d("ReceiversNameList",""+ReceiversNameList);
        Log.d("ReceiversAmountList",""+ReceiversAmountList);

        if (! NameList.isEmpty() && ! DebtList.isEmpty()){
            int counter;
            if (PayersAmountList.size() > ReceiversAmountList.size()){
                counter = PayersAmountList.size();
            }
            else if ((PayersAmountList.size() < ReceiversAmountList.size())){
                counter = ReceiversAmountList.size();
            }
            else {
                counter=PayersAmountList.size();
            }
            Log.d("counter",""+counter);

            try{
                for (int i=0; i<= counter; i++){
                    if (PayersAmountList.get(i) + ReceiversAmountList.get(i) > 0){
                        WhoPaysList.add(PayersNameList.get(i));
                        ToWhomList.add(ReceiversNameList.get(i));
                        HowMuchList.add(- ReceiversAmountList.get(i));
                        PayersNameList.add(PayersNameList.get(i));
                        PayersAmountList.add(PayersAmountList.get(i) + ReceiversAmountList.get(i));
                        ReceiversNameList.set(i, ""+0);
                        ReceiversAmountList.set(i, 0d);
                        PayersNameList.set(i, ""+0);
                        PayersAmountList.set(i, 0d);
                        Log.d("PayersNameList1",""+PayersNameList);
                        Log.d("PayersAmountList1",""+PayersAmountList);
                        Log.d("ReceiversNameList1",""+ReceiversNameList);
                        Log.d("ReceiversAmountList1",""+ReceiversAmountList);
                        Log.d("WhoPaysList1",""+WhoPaysList);
                        Log.d("ToWhomList1",""+ToWhomList);
                        Log.d("HowMuchList1",""+HowMuchList);
                    }
                    else {
                        WhoPaysList.add(PayersNameList.get(i));
                        ToWhomList.add(ReceiversNameList.get(i));
                        HowMuchList.add(PayersAmountList.get(i));
                        ReceiversNameList.add(ReceiversNameList.get(i));
                        ReceiversAmountList.add(PayersAmountList.get(i) + ReceiversAmountList.get(i));
                        PayersNameList.set(i, ""+0);
                        PayersAmountList.set(i, 0d);
                        ReceiversNameList.set(i, ""+0);
                        ReceiversAmountList.set(i, 0d);
                        Log.d("PayersNameList3",""+PayersNameList);
                        Log.d("PayersAmountList3",""+PayersAmountList);
                        Log.d("ReceiversNameList3",""+ReceiversNameList);
                        Log.d("ReceiversAmountList3",""+ReceiversAmountList);
                        Log.d("WhoPaysLis3",""+WhoPaysList);
                        Log.d("ToWhomList3",""+ToWhomList);
                        Log.d("HowMuchList3",""+HowMuchList);
                    }
                    counter++;
                }
            }
            catch (Exception e){
                //Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
        }
        for (int i=0; i<HowMuchList.size(); i++){
            HowMuchList.set(i,(Double.parseDouble(df.format(HowMuchList.get(i)))));
        }

        Log.d("WhoPaysLis4",""+WhoPaysList);
        Log.d("ToWhomList4",""+ToWhomList);
        Log.d("HowMuchList4",""+HowMuchList);

        long middlems= System.currentTimeMillis();
        long middlens= System.nanoTime();
        getCurrency();

        CustomSettleDebtsListAdapter adapter=new CustomSettleDebtsListAdapter( SettleDebts.this, groupId,
                WhoPaysList, HowMuchList, ToWhomList, currency);
        lv_debts.setAdapter(adapter);
        if (lv_debts.getAdapter().getCount()>0){
            lv_debts.setVisibility(View.VISIBLE);
        }
        else {
            Snackbar.make(parentLayout, "No Expense added yet", Snackbar.LENGTH_LONG).show();
        }
        long endms= System.currentTimeMillis();
        long endns= System.currentTimeMillis();
        Log.d("milli",""+(middlems-startms));
        Log.d("nano",""+(middlens-startns));
        Log.d("asdfz","as");
        //Log.d("2nd",""+(endms-startms));
    }


    protected void getCurrency(){
        Cursor cursor=myDB.getGroupDetails(groupId);
        if (cursor.getCount() == 0){
        }
        else {
            cursor.moveToFirst();
            currency=cursor.getString(2);
        }
    }
}