package com.example.Settle_Group_Expenses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String dbname="settle.ge_db";
    public static final int dbversion=1;
    public static final String table1="groups";
    public static final String table2="members";
    public static final String table3="advance_payments";
    public static final String table4="expenses";
    public static final String table6="mm_expenses";
    public static final String table7="mm_expenses_bridge";
    public static final String table8="to_do";
    public static final String table9="expense-status";

    /*public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }*/

    public DatabaseHelper(Context context){
        super(context,dbname,null,dbversion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table table1(g_id INTEGER PRIMARY KEY AUTOINCREMENT, g_name VARCHAR," +
                "currency VARCHAR(3),destination VARCHAR(20),description VARCHAR(200) )");

        db.execSQL("create table table2(m_id INTEGER PRIMARY KEY AUTOINCREMENT, m_name VARCHAR ," +
                " g_id INTEGER, budget INTEGER, debt DOUBLE DEFAULT (0), updated_bud DOUBLE DEFAULT (0), " +
                "CONSTRAINT g_id FOREIGN KEY (g_id) REFERENCES table1(g_id) ON DELETE CASCADE)");

        db.execSQL("create table table3(p_id INTEGER PRIMARY KEY AUTOINCREMENT,p_amt DOUBLE(10),purpose VARCHAR, " +
                "who_paid VARCHAR ,to_whom VARCHAR, date VARCHAR(20),time VARCHAR(20), g_id INTEGER," +
                "CONSTRAINT g_id FOREIGN KEY (g_id) REFERENCES table1(g_id) ON DELETE CASCADE)");

        db.execSQL("create table table4(e_id INTEGER PRIMARY KEY AUTOINCREMENT, g_id INTEGER, who_paid VARCHAR , e_amt DOUBLE," +
                " purpose VARCHAR(20),date VARCHAR(20),time VARCHAR(20)," +
                "CONSTRAINT g_id FOREIGN KEY (g_id) REFERENCES table1(g_id) ON DELETE CASCADE)");

        db.execSQL("create table table6(mm_e_id INTEGER PRIMARY KEY AUTOINCREMENT, g_id INTEGER, e_amt DOUBLE," +
                " purpose VARCHAR(20),date VARCHAR(20),time VARCHAR(20)," +
                "CONSTRAINT g_id FOREIGN KEY (g_id) REFERENCES table1(g_id) ON DELETE CASCADE)");

        db.execSQL("create table table7(mm_e_id INTEGER,who_paid VARCHAR, amount1 DOUBLE, for_whom VARCHAR," +
                " amount2 DOUBLE, g_id INTEGER," +
                " PRIMARY KEY (mm_e_id, who_paid, for_whom)," +
                "CONSTRAINT mm_e_id FOREIGN KEY (mm_e_id) REFERENCES table6(mm_e_id) ON DELETE CASCADE)");

        db.execSQL("create table table8(to_do_id INTEGER PRIMARY KEY AUTOINCREMENT,m_id INTEGER,to_do VARCHAR(100)," +
                "to_do_date VARCHAR(20),to_do_time VARCHAR(20), g_id INTEGER, " +
                "CONSTRAINT g_id FOREIGN KEY (g_id) REFERENCES table1(g_id) ON DELETE CASCADE)");

        db.execSQL("create table table9(e_id INTEGER, for_whom VARCHAR, amount DOUBLE, g_id INTEGER, " +
                "PRIMARY KEY (e_id, for_whom)," +
                "CONSTRAINT e_id FOREIGN KEY (e_id) REFERENCES table4(e_id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+table1);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase myDB){
        super.onOpen(myDB);
        myDB.execSQL("PRAGMA foreign_keys=ON");
    }

    public boolean insertGroup(String g_name, String currency, String destination, String description){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table1(g_name,currency,destination,description)values('"+g_name+"','"+currency+"'," +
                " '"+destination+"','"+description+"')");
        return true;
    }

    public boolean insertMember(String m_name,Integer g_id , Double budget){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table2(m_name,g_id,budget, updated_bud)values('"+m_name+"','"+g_id+"' ,'"+budget+"','"+budget+"')");
        return true;
    }

    public boolean insertAdvance_payments(Double p_amt, String purpose, String who_paid, String to_whom,
                                          String date, String time, String gid ){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table3(p_amt, purpose, who_paid, to_whom, date, time, g_id)" +
                "values('"+p_amt+"','"+purpose+"','"+who_paid+"','"+to_whom+"','"+date+"','"+time+"','"+gid+"')");
        return true;
    }

    public boolean insertExpenses(String gid, String who_paid, Double eamt, String purpose, String date,
                                  String time){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table4(g_id, who_paid, e_amt, purpose, date, time)values('"+gid+"', '"+who_paid+"'," +
                "'"+eamt+"','"+purpose+"', '"+date+"','"+time+"')");
        return true;
    }

    public boolean insertMMExpenses(String gid, Double eamt, String purpose, String date,
                                  String time){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table6(g_id, e_amt, purpose, date, time)values('"+gid+"'," +
                "'"+eamt+"','"+purpose+"', '"+date+"','"+time+"')");
        return true;
    }


    public boolean insertTo_Do(Integer m_id,String to_do,String to_do_date,String to_do_time, String gid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table8(m_id,to_do,to_do_date,to_do_time, g_id)values ('"+m_id+"','"+to_do+"'," +
                "'"+to_do_date+"','"+to_do_time+"', '"+gid+"')");
        return true;
    }

    public boolean insertExpStatus(String eid,String for_whom){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table9(e_id,for_whom)values ('"+eid+"','"+for_whom+"')");
        return true;
    }

    public boolean insertExpStatus2(String eid,String for_whom, Double amount, String gid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table9(e_id,for_whom, amount, g_id)values ('"+eid+"','"+for_whom+"', '"+amount+"', '"+gid+"')");
        return true;
    }

    public boolean insertMMExpBridge(String mm_eid, String who_paid, Double amount1, String for_whom, Double amount2, String gid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("insert into table7(mm_e_id, who_paid, amount1, for_whom, amount2, g_id)values" +
                " ('"+mm_eid+"','"+who_paid+"', '"+amount1+"','"+for_whom+"', '"+amount2+"', '"+gid+"')");
        return true;
    }

    public Cursor getGroupName(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table1",null);
        return cursor;

    }

    public Cursor getMemberName(String gid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table2 where g_id="+gid,null);
        return cursor;
    }

    public Cursor getMemberBudget(String gid, String mname){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table2 where g_id='"+gid+"' and m_name='"+mname+"'",null);
        return cursor;
    }

    public Cursor getToDo(String mid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table8 where m_id="+mid,null);
        return cursor;
    }
    public List<String> getAllMembers(String gid) {
        List<String> Members = new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table2 where g_id="+gid,null);
        if (cursor.getCount()==0){
        }
        else {
            cursor.moveToFirst();
            do{
                Members.add(cursor.getString(1));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return Members;
    }
    public Cursor getGroupDetails(String gid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from table1 where g_id=" + gid, null);
        return cursor;
    }
    public Cursor getLastExpenseId(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table4",null);
        return cursor;
    }

    public Cursor getMMLastExpenseId(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table6",null);
        return cursor;
    }

    public Cursor getAllExpense(String gid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table4 where g_id=" + gid,null);
        return cursor;
    }

    public Cursor getAllMMExpense(String gid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table6 where g_id=" + gid,null);
        return cursor;
    }

    public Cursor getExpenseDetails(String eid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table4 where e_id=" + eid,null);
        return cursor;
    }

    public Cursor getPaymentDetails(String pid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table3 where p_id=" + pid,null);
        return cursor;
    }

    public Cursor getAllPayment(String gid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table3 where g_id=" + gid,null);
        return cursor;
    }

    public Cursor getExpStatus(String eid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table9 where e_id='"+eid+"'", null);
        return cursor;
    }

    public Cursor getExpStatusMembers(String gid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table9 where g_id='"+gid+"'", null);
        return cursor;
    }

    public Cursor getMMExpenses(String mm_eid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table6 where mm_e_id='"+mm_eid+"'", null);
        return cursor;
    }

    public Cursor getMMExpBridge(String mm_eid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table7 where mm_e_id='"+mm_eid+"'", null);
        return cursor;
    }

    public Cursor getWhoPaidBudget(String gid, String mm_eid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from table7 where mm_e_id = '"+mm_eid+"' and" +
                " g_id='"+gid+"' and amount1 > 0", null);
        return cursor;
    }

    public boolean updateGroupName(String gid, String gname){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" update table1 set g_name='"+gname+"' where g_id="+gid);
        return true;
    }
    public boolean updateDestination(String gid, String dest){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" update table1 set destination='"+dest+"' where g_id="+gid);
        return true;
    }

    public boolean updateMemberName(String gid, String mid,String mname){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" update table2 set m_name='"+mname+"' where m_id="+mid+" and g_id="+gid);
        return true;
    }

    public boolean updateAllMemberName(String gid,String mname,String updatedName){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" update table4 set who_paid='"+updatedName+"' where g_id="+gid+" and who_paid='"+mname+"'");
        db.execSQL(" update table9 set for_whom='"+updatedName+"' where g_id="+gid+" and for_whom='"+mname+"'");
        db.execSQL(" update table7 set who_paid='"+updatedName+"' where g_id="+gid+" and who_paid='"+mname+"'");
        db.execSQL(" update table7 set for_whom='"+updatedName+"' where g_id="+gid+" and for_whom='"+mname+"'");
        db.execSQL(" update table3 set who_paid='"+updatedName+"' where g_id="+gid+" and who_paid='"+mname+"'");
        db.execSQL(" update table3 set to_whom='"+updatedName+"' where g_id="+gid+" and to_whom='"+mname+"'");
        return true;
    }

    public boolean updateDebts(String gid, String mname,Double amount){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" update table2 set debt = debt + '"+amount+"' where g_id='"+gid+"' and m_name='"+mname+"'");
        return true;
    }

    public boolean updateBudgets(String gid, String mname,Double amount){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" update table2 set updated_bud = updated_bud - '"+amount+"' where g_id='"+gid+"' and m_name='"+mname+"'");
        return true;
    }

    public boolean updateExpSplitAmt(String eid, String for_whom, Double amt){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update table9 set amount='"+amt+"' where e_id='"+eid+"' and for_whom='"+for_whom+"'");
        return true;
    }
    public boolean updateMMExpSplitAmt(String mm_eid, String for_whom, Double amt){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update table7 set amount2='"+amt+"' where mm_e_id='"+mm_eid+"' and for_whom='"+for_whom+"'");
        return true;
    }

    public boolean updateMMExpWhoPaidAmt(String mm_eid, String who_paid, Double amt){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update table7 set amount1='"+amt+"' where mm_e_id='"+mm_eid+"' and who_paid='"+who_paid+"'");
        return true;
    }

    public boolean updateExpSplitAmtTo0(String eid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update table9 set amount='"+0+"' where e_id='"+eid+"'" );
        return true;
    }

    public boolean updateMMExpSplitAmtTo0(String mm_eid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update table7 set amount2='"+0+"' where mm_e_id='"+mm_eid+"'" );
        return true;
    }

    // Delete Functions
    public boolean deleteGroup(String gid){
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL("delete from table1 where g_id="+gid);
        return true;
    }

    public boolean deleteMember(String gid,String mid){
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL("delete from table2 where g_id="+gid+" and m_id="+mid);
        return true;
    }

    public boolean deleteToDo(String todoid){
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL("delete from table8 where to_do_id="+todoid);
        return true;
    }

    public boolean deleteLastExpense(String eid){
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL("delete from table4 where e_id="+eid);
        return true;
    }

    public boolean deleteMMLastExpense(String mm_eid){
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL("delete from table6 where mm_e_id="+mm_eid);
        return true;
    }
}