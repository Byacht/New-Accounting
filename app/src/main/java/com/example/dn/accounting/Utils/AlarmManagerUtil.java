package com.example.dn.accounting.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import com.example.dn.accounting.Receiver.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by dn on 2016/12/8.
 */

public class AlarmManagerUtil {
    public static final String ALARM_ACTION = "com.example.dn.alarm";
    public static final int ONE_DAY = 24*60*60*1000;

    public static void setAlarmTime(Context context,Intent intent){
        PendingIntent pi = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,10,pi);
    }

    public static void setAlarm(Context context, int alarmTime){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long triggerAtTime = getTriggerTime(alarmTime);
        Log.d("out","calendar:"+triggerAtTime);
        Intent alarmIntent = new Intent(ALARM_ACTION);
        alarmIntent.setClass(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,triggerAtTime,10,pi);
    }

    public static void unsetAlarm(Context context){
        Intent alarmIntent = new Intent(ALARM_ACTION);
        alarmIntent.setClass(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pi);
        Log.d("out","have cancel alarm");
    }

    private static long getTriggerTime(int alarmTime){
        long time = System.currentTimeMillis();
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int mHour=mCalendar.get(Calendar.HOUR_OF_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,alarmTime);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Log.d("out","now:"+mHour+"alarmTime:"+alarmTime);
        if (mHour < alarmTime){
            return calendar.getTimeInMillis();
        }else {
            return calendar.getTimeInMillis()+ONE_DAY;
        }
    }
}
