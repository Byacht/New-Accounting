package com.example.dn.accounting.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dn.accounting.Activity.AlarmActivity;
import com.example.dn.accounting.Receiver.AlarmReceiver;
import com.example.dn.accounting.Utils.AlarmManagerUtil;

/**
 * Created by dn on 2016/12/5.
 */

public class AlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int alarmTime;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmTime = intent.getIntExtra("alarmTime",0);
        Log.d("out","时间是"+alarmTime);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("out","当前线程是："+Thread.currentThread().getId());
//            }
//        }).start();
        AlarmManagerUtil.setAlarm(this,alarmTime);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("out", "service destroy");
        Intent serviceIntent = new Intent(this, AlarmService.class);
        serviceIntent.putExtra("alarmTime", alarmTime);
        this.startService(serviceIntent);
    }
}
