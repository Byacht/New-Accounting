package com.example.dn.accounting.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dn on 2016/11/20.
 */

public class DBManager {
    private static DBManager dbManager;

    public SQLiteDatabase getDataBase() {
        return mDataBase;
    }

    private SQLiteDatabase mDataBase;
    private Context mContext;
    private AccountDataBase mAccountDataBase;

    private DBManager(Context context){
        this.mContext = context;
        mAccountDataBase = new AccountDataBase(context,"Account.db",null,2);
        mDataBase = mAccountDataBase.getWritableDatabase();
    }

    public static DBManager getInstance(Context context){
        if (dbManager == null){
            dbManager = new DBManager(context);
        }
        return dbManager;
    }
}
