package com.example.dn.accounting.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dn.accounting.Service.AlarmService;

/**
 * Created by dn on 2016/12/5.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, AlarmService.class);
        context.startService(startServiceIntent);
    }
}
