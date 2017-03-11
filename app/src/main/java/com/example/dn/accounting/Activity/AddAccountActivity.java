package com.example.dn.accounting.Activity;

import android.animation.Animator;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddAccountActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SQLiteDatabase mDataBase;
    private float cost;
    private String information;
    private String time;
    private Intent intent;
    private List<Account> newAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        setupToolBar();
        setupAnimations();
        setExitAnimations();
        setupDataBase();

        findViewById(R.id.add_cost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAddDialog(Account.TYPE_COST);
                Intent intent = new Intent(AddAccountActivity.this, AddCostActivity.class);
                intent.putExtra("TYPE", Account.TYPE_COST);
                startActivity(intent);
            }
        });

        findViewById(R.id.add_income).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAddDialog(Account.TYPE_INCOME);
                Intent intent = new Intent(AddAccountActivity.this, AddCostActivity.class);
                intent.putExtra("TYPE", Account.TYPE_INCOME);
                startActivity(intent);
            }
        });
    }

    private void showAddDialog(final int type) {
        final View view = LayoutInflater.from(AddAccountActivity.this).inflate(R.layout.add_account_dialog,null);
        final EditText informationText = (EditText) view.findViewById(R.id.information_edittext);
        final EditText costText = (EditText) view.findViewById(R.id.cost_edittext);
        if (intent == null || newAccounts == null){
            intent = new Intent();
            newAccounts = new ArrayList<Account>();
        }
        new AlertDialog.Builder(AddAccountActivity.this)
                .setView(view)
                .setTitle("请输入信息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(informationText.getText()) && !TextUtils.isEmpty(costText.getText())){
                            information = informationText.getText().toString();
                            cost = Float.valueOf(costText.getText().toString());
                            time = getCurrentTime();
                            ContentValues values = new ContentValues();
                            values.put("information",information);
                            values.put("cost",cost);
                            values.put("time",time);
                            values.put("type", type);
                            mDataBase.insert("Account",null,values);
                            Account account = new Account();
                            account.setInformation(information);
                            account.setCost(cost);
                            account.setTime(time);
                            account.setType(type);
                            newAccounts.add(account);
                        } else {
                            Toast.makeText(view.getContext(),"信息不全，请完善信息",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    private void setupDataBase() {
        mDataBase = DBManager.getInstance(AddAccountActivity.this).getDataBase();
    }

    private void setupToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupAnimations(){
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(toolbar);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    private void setExitAnimations(){
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setExitTransition(fade);
    }

    private void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }

    private String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(curDate);
        return currentTime;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (intent!=null){
            intent.putExtra("newAccounts", (Serializable) newAccounts);
            setResult(2000,intent);
        }
    }
}
