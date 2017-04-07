package com.example.dn.accounting.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dn.accounting.R;
import com.example.dn.accounting.Service.AlarmService;
import com.example.dn.accounting.Utils.AlarmManagerUtil;
import com.zcw.togglebutton.ToggleButton;

public class AlarmActivity extends AppCompatActivity {

    private ToggleButton mToggleButton;
    private LinearLayout mEverydayAlarmLinearLayout;
    private LinearLayout mAddAlarmInformationLinearLayout;
    private TextView mAlarmTimeText;
    private int mAlarmTime = 0;
    private String mTime[] = new String[]{"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00",
            "12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mAlarmTimeText = (TextView) findViewById(R.id.alarmTimeText);

        final SharedPreferences sharedPreferences = getSharedPreferences("alarm",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        initAlarm(sharedPreferences);

        mToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    Log.d("out","on");
                    Intent serviceIntent = new Intent(AlarmActivity.this, AlarmService.class);
                    serviceIntent.putExtra("alarmTime", mAlarmTime);
                    startService(serviceIntent);
                    editor.putBoolean("isAlarmSet",true);
                    editor.commit();
                    mAlarmTimeText.setVisibility(View.VISIBLE);
                    Toast.makeText(AlarmActivity.this,"设置闹钟成功",Toast.LENGTH_SHORT).show();
                }else {
                    Log.d("out","off");
                    AlarmManagerUtil.unsetAlarm(AlarmActivity.this);
                    editor.putBoolean("isAlarmSet",false);
                    editor.commit();
                    mAlarmTimeText.setVisibility(View.INVISIBLE);
                    Toast.makeText(AlarmActivity.this,"取消闹钟",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mEverydayAlarmLinearLayout = (LinearLayout) findViewById(R.id.everyday_alarm_layout);
        mEverydayAlarmLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AlarmActivity.this)
                        .setTitle("请选择提醒时间")
                        .setSingleChoiceItems(mTime, sharedPreferences.getInt("alarmTime",0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("out","选择了"+which+"点");
                                mAlarmTime = which;
                                AlarmManagerUtil.unsetAlarm(AlarmActivity.this);
                                AlarmManagerUtil.setAlarm(AlarmActivity.this,mAlarmTime);
                                mToggleButton.setToggleOn();
                                editor.putBoolean("isAlarmSet",true);
                                editor.putInt("alarmTime",mAlarmTime);
                                editor.commit();
                                mAlarmTimeText.setText(mAlarmTime+":00");
                                mAlarmTimeText.setVisibility(View.VISIBLE);
                                Toast.makeText(AlarmActivity.this,"设置闹钟成功",Toast.LENGTH_SHORT).show();
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

    private void initAlarm(SharedPreferences sharedPreferences) {
        boolean isAlarmSet = sharedPreferences.getBoolean("isAlarmSet",false);
        int alarmTime = sharedPreferences.getInt("alarmTime",0);

        mAlarmTimeText.setText(alarmTime+":00");

        if (isAlarmSet){
            mToggleButton.setToggleOn();
            mAlarmTimeText.setVisibility(View.VISIBLE);
        }
    }
}
