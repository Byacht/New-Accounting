package com.example.dn.accounting.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dn.accounting.Adapter.MyFragmentPagerAdapter;
import com.example.dn.accounting.DataBase.AccountDataBase;
import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.Model.TagInformation;
import com.example.dn.accounting.R;
import com.example.dn.accounting.View.PieChartView;
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
    private ArrayList<TagInformation> mCostTagInformations;
    private ArrayList<TagInformation> mIncomeTagInformations;
    private float income = 0;
    private float cost = 0;
    private float balance;
    private Spinner mSpinner;
    private ArrayList<String> mCostTagNames;
    private ArrayList<String> mIncomeTagNames;
    private String mCurrentYearAndMonth = "2017-03";
    private PieChartView mCostPieChartView;
    private PieChartView mIncomePieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("out", "selected");
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
                layoutParams.height = 100;
                viewPager.setLayoutParams(layoutParams);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("out", "unselected");
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
                layoutParams.height = 0;
                viewPager.setLayoutParams(layoutParams);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("out", "reselected");
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
                layoutParams.height = 100;
                viewPager.setLayoutParams(layoutParams);
            }
        });

        mCostPieChartView = (PieChartView) findViewById(R.id.pie_chart);
        mIncomePieChartView = (PieChartView) findViewById(R.id.pie_chart_income);

        setupToolBar();
        setupChooseTimeView();
        showStatisticsView();
    }

    private void setupToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_statistics);
        toolbar.setTitle("统计分析");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showChooseTimeView(){
//        FrameLayout layout = (FrameLayout) findViewById(R.id.activity_statistics);
//        View view = LayoutInflater.from(this).inflate(R.layout.view_choose_time,null,false);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
//        layout.addView(view,layoutParams);

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

        mCostPieChartView.setTagInformation(mCostTagInformations);
        mIncomePieChartView.setTagInformation(mIncomeTagInformations);
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

        Cursor cursor = mDataBase.query("Account",null,"time LIKE ?", new String[]{mCurrentYearAndMonth+"%"},null,null,null);
        if (cursor.moveToFirst()){
            do{
                Account account = new Account();
                account.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                account.setTime(cursor.getString(cursor.getColumnIndex("time")));
                account.setCost(cursor.getFloat(cursor.getColumnIndex("cost")));
                account.setType(cursor.getInt(cursor.getColumnIndex("type")));
                account.setTagName(cursor.getString(cursor.getColumnIndex("tag")));
                accounts.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();

        doStatisticsOfTag();
    }

    private void setupChooseTimeView() {

    }

    private void doStatisticsOfTag(){
        for(Account account : accounts){
            if (account.getType() == Account.TYPE_COST){
                if (!mCostTagNames.contains(account.getTagName())){
                    mCostTagNames.add(account.getTagName());
                    Log.d("out", "tagName:" + account.getTagName() + "cost:" + account.getCost());
                    TagInformation tagInformation = new TagInformation();
                    tagInformation.setTagName(account.getTagName());
                    tagInformation.setTagCost(account.getCost());
                    mCostTagInformations.add(tagInformation);
                } else {
                    int position = mCostTagNames.indexOf(account.getTagName());
                    Log.d("out", "exist tagName:" + account.getTagName() + "cost:" + account.getCost());
                    TagInformation tagInformation = mCostTagInformations.get(position);
                    tagInformation.setTagCost(tagInformation.getTagCost() + account.getCost());
                    mCostTagInformations.set(position, tagInformation);
                }
            } else {
                if (!mIncomeTagNames.contains(account.getTagName())){
                    mIncomeTagNames.add(account.getTagName());
                    TagInformation tagInformation = new TagInformation();
                    tagInformation.setTagName(account.getTagName());
                    tagInformation.setTagCost(account.getCost());
                    mIncomeTagInformations.add(tagInformation);
                } else {
                    int position = mIncomeTagNames.indexOf(account.getTagName());
                    TagInformation tagInformation = mIncomeTagInformations.get(position);
                    tagInformation.setTagCost(tagInformation.getTagCost() + account.getCost());
                    mIncomeTagInformations.set(position, tagInformation);
                }
            }
        }
    }

//    private void addToTagInformationsList(Account account) {
//        if (!mTagNames.contains(account.getTagName())){
//            mTagNames.add(account.getTagName());
//            TagInformation tagInformation = new TagInformation();
//            tagInformation.setTagName(account.getTagName());
//            tagInformation.setTagCost(account.getCost());
//            mTagInformations.add(tagInformation);
//        } else {
//            int position = mTagNames.indexOf(account.getTagName());
//            TagInformation tagInformation = mTagInformations.get(position);
//            tagInformation.setTagCost(tagInformation.getTagCost() + account.getCost());
//            mTagInformations.set(position, tagInformation);
//        }
//    }

    private void setupDataBase() {
        mDataBase = DBManager.getInstance(StatisticsActivity.this).getDataBase();
        accounts = new ArrayList<Account>();
        mIncomeTagInformations = new ArrayList<TagInformation>();
        mIncomeTagNames = new ArrayList<String>();
        mCostTagInformations = new ArrayList<TagInformation>();
        mCostTagNames = new ArrayList<String>();
    }
}
