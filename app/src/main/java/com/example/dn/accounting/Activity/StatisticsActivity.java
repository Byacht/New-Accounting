package com.example.dn.accounting.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dn.accounting.DataBase.AccountDataBase;
import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.R;
import com.example.dn.accounting.View.StatisticsView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private StatisticsView all_cost;
    private StatisticsView all_income;
    private StatisticsView all_banlance;
    private SQLiteDatabase mDataBase;
    private List<Account> accounts;
    private float income = 0;
    private float cost = 0;
    private float balance;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setupToolBar();
        setupChooseTimeView();
        showStatisticsView();
    }

    private void setupToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_statistics);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setTitle("统计分析");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showChooseTimeView(){
        FrameLayout layout = (FrameLayout) findViewById(R.id.activity_statistics);
        View view = LayoutInflater.from(this).inflate(R.layout.view_choose_time,null,false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        layout.addView(view,layoutParams);

    }

    private void showStatisticsView() {
        calculateCostAndIncome();

        all_cost = (StatisticsView) findViewById(R.id.cost_statistics);
        all_cost.setCost(cost);
        all_cost.setLength(cost/(cost+income));

        all_income = (StatisticsView) findViewById(R.id.income_statistics);
        all_income.setCost(income);
        all_income.setLength(income/(cost+income));

        balance = cost-income;
        all_banlance = (StatisticsView) findViewById(R.id.balance_statistics);
        if (balance>0){
            all_banlance.setColor(getResources().getColor(R.color.costColor));
        }else {
            all_banlance.setColor(getResources().getColor(R.color.incomeColor));
        }
        all_banlance.setCost(balance);
        all_banlance.setLength(Math.abs(balance)/(cost+income));
    }

    private void calculateCostAndIncome() {
        getDataFromDataBase();

        for (Account account:accounts){
            if (account.getType()==Account.TYPE_COST){
                cost+=account.getCost();
            } else if (account.getType()==Account.TYPE_INCOME){
                income+=account.getCost();
            }
        }
    }

    private void getDataFromDataBase() {
        setupDataBase();

        Cursor cursor = mDataBase.query("Account",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                Account account = new Account();
                account.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                account.setTime(cursor.getString(cursor.getColumnIndex("time")));
                account.setCost(cursor.getFloat(cursor.getColumnIndex("cost")));
                account.setType(cursor.getInt(cursor.getColumnIndex("type")));
                accounts.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void setupChooseTimeView() {
        textView = (TextView) findViewById(R.id.textview_statistics);
        Drawable drawable = getResources().getDrawable(R.drawable.downarrows);
        drawable.setBounds(-40, -0, 0, 40);
        textView.setCompoundDrawables(null,null,drawable,null);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseTimeView();
            }
        });
    }

    private void setupDataBase() {
        mDataBase = DBManager.getInstance(StatisticsActivity.this).getDataBase();
        accounts = new ArrayList<Account>();
    }
}
