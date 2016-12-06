package com.example.dn.accounting.Activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.dn.accounting.R;
import com.example.dn.accounting.Service.AlarmService;
import com.zcw.togglebutton.ToggleButton;

public class AlarmActivity extends AppCompatActivity {

    private ToggleButton mToggleButton;
    private LinearLayout mEverydayAlarmLinearLayout;
    private LinearLayout mAddAlarmInformationLinearLayout;

    private String mTime[] = new String[]{"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00",
            "11:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    Log.d("out","on");
                    Intent serviceIntent = new Intent(AlarmActivity.this, AlarmService.class);
                    startService(serviceIntent);
                }else {
                    Log.d("out","off");
                    Intent serviceIntent = new Intent(AlarmActivity.this, AlarmService.class);
                    stopService(serviceIntent);
                }
            }
        });

        mEverydayAlarmLinearLayout = (LinearLayout) findViewById(R.id.everyday_alarm_layout);
        mEverydayAlarmLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AlarmActivity.this)
                        .setTitle("请选择提醒时间")
                        .setSingleChoiceItems(mTime, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("out","选择了"+which+"点");
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        mAddAlarmInformationLinearLayout = (LinearLayout) findViewById(R.id.addAlarmLayout);
        mAddAlarmInformationLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmActivity.this,addAlarmInformation.class);
                startActivity(intent);
            }
        });
    }
}
