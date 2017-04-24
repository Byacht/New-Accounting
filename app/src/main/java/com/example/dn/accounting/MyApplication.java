package com.example.dn.accounting;

import android.app.Application;
import android.content.Context;

import com.zhy.changeskin.SkinManager;


/**
 * Created by dn on 2017/4/21.
 */

public class MyApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sContext = getApplicationContext();
        SkinManager.getInstance().init(this);
    }

    public static Context getContext(){
        return sContext;
    }
}
