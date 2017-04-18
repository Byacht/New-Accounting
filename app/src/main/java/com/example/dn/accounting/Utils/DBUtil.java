package com.example.dn.accounting.Utils;

import android.database.Cursor;

import com.example.dn.accounting.Model.Account;

import java.util.ArrayList;

/**
 * Created by dn on 2017/4/14.
 */

public class DBUtil {
    public static ArrayList<Account> query(Cursor cursor) {
        ArrayList<Account> accounts = new ArrayList<Account>();
        if (cursor.moveToFirst()) {
            do {
                Account account = new Account();
                account.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                account.setTime(cursor.getString(cursor.getColumnIndex("time")));
                account.setCost(cursor.getFloat(cursor.getColumnIndex("cost")));
                account.setType(cursor.getInt(cursor.getColumnIndex("type")));
                account.setTagName(cursor.getString(cursor.getColumnIndex("tag")));
                accounts.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }
}
