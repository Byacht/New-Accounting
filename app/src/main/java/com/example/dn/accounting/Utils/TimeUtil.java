package com.example.dn.accounting.Utils;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Date;

/**
 * Created by dn on 2017/3/5.
 */

public class TimeUtil {
    public static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000);
        String currentTime = formatter.format(curDate);
        return currentTime;
    }

    public static String getTime(Context context){
        int flagYear = DateUtils.FORMAT_SHOW_YEAR;
        int flagTime = DateUtils.FORMAT_SHOW_TIME ;
        String year = DateUtils.formatDateTime(context, System.currentTimeMillis(), flagYear);
        String time = DateUtils.formatDateTime(context, System.currentTimeMillis(), flagTime);
        Log.d("out", "year:" + year + " time:" + time);
        return year + " " + time;
    }
}
