package com.example.dn.accounting.Receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dn.accounting.Activity.AddAccountActivity;
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
        Intent addAccountIntent = new Intent(context, AddAccountActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,addAccountIntent,0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("TickerText:" + "您有新短消息，请注意查收！")
                .setContentTitle("Notification Title")
                .setContentText("This is the notification message")
                .setContentIntent(pi)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(0, notification);
    }
}
