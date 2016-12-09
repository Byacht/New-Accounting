package com.example.dn.accounting.Receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dn.accounting.R;
import com.example.dn.accounting.Service.AlarmService;
import com.example.dn.accounting.Utils.AlarmManagerUtil;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by dn on 2016/12/5.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManagerUtil.setAlarmTime(context,intent);
        Log.d("out","a new broadcast");
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify3 = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("TickerText:" + "您有新短消息，请注意查收！")
                .setContentTitle("Notification Title")
                .setContentText("This is the notification message")
                .build();
        manager.notify(0, notify3);
    }
}
