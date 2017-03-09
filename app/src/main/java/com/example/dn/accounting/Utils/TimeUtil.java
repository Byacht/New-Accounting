package com.example.dn.accounting.Utils;

import android.icu.text.SimpleDateFormat;

import java.util.Date;

/**
 * Created by dn on 2017/3/5.
 */

public class TimeUtil {
    public static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(curDate);
        return currentTime;
    }
}
