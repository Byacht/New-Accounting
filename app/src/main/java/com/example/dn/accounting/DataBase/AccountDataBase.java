package com.example.dn.accounting.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dn on 2016/11/14.
 */

public class AccountDataBase extends SQLiteOpenHelper {

    public static final String CREATE_ACCOUNT = "create table account ("
            + "id integer primary key autoincrement,"
            + "cost real,"
            + "information text,"
            + "time text,"
            + "tag text,"
            + "type integer)";

    public AccountDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Account");
        onCreate(db);
    }
}
