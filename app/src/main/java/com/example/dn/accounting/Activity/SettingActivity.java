package com.example.dn.accounting.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.dn.accounting.R;

public class SettingActivity extends AppCompatActivity {
    private LinearLayout mSetPortraitLayout;
    private LinearLayout mSetBackgroundLayout;
    private ImageView mPortraitImageView;
    private ImageView mBackgroundImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSetPortraitLayout = (LinearLayout) findViewById(R.id.set_portrait_layout);
        mSetBackgroundLayout = (LinearLayout) findViewById(R.id.set_background_layout);
        mPortraitImageView = (ImageView) findViewById(R.id.portrait_imageview);
        mBackgroundImageView = (ImageView) findViewById(R.id.background_imageview);

        mSetPortraitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSetBackgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
