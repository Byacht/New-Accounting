package com.example.dn.accounting.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.example.dn.accounting.View.MyFragment;
import com.example.dn.accounting.View.PieChartView;
import com.example.dn.accounting.View.StatisticsView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private SQLiteDatabase mDataBase;
    private String mCurrentYearAndMonth = "2017-03";

    private TextView mShowTimeTextView;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private TabLayout mTabLayout;
    private int viewHeight;  //选择年月视图的高度
    private boolean isSelected = false;  //是否处于选择时间的状态，若是，按下返回键将隐藏选择视图，否则退出Activity

    private StatisticsView all_cost;
    private StatisticsView all_income;
    private StatisticsView all_banlance;
    private float income = 0;
    private float cost = 0;
    private float balance;

    private List<Account> accounts;
    private ArrayList<TagInformation> mCostTagInformations;
    private ArrayList<TagInformation> mIncomeTagInformations;
    private ArrayList<String> mCostTagNames;
    private ArrayList<String> mIncomeTagNames;

    private PieChartView mCostPieChartView;
    private PieChartView mIncomePieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initView();
        setupToolBar();
        setupTabLayout();
        showStatisticsView();
        makeStatisticsOfTag();
        setupPieChart();
    }

    private void initView(){
        viewHeight = 350;

        mShowTimeTextView = (TextView) findViewById(R.id.showtime_textview);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mCostPieChartView = (PieChartView) findViewById(R.id.pie_chart);
        mIncomePieChartView = (PieChartView) findViewById(R.id.pie_chart_income);
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

    private void setupTabLayout() {
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setSelectedTabIndicatorColor(Color.alpha(0));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isSelected = true;
                showChooseTimeLayout();
                int index = viewPager.getCurrentItem();
                /* 得到当前的fragment，通过接口回调响应fragment的点击事件 */
                MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + index);
                myFragment.setOnShowTimeListener(new MyFragment.OnShowTime() {
                    @Override
                    public void showTime(String time) {
                        mShowTimeTextView.setText("2017-01");
                    }
                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                isSelected = true;
                showChooseTimeLayout();
            }
        });
    }

    private void showChooseTimeLayout() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams.height = viewHeight;
        viewPager.setLayoutParams(layoutParams);
    }

    private void hideChooseTimeLayout(){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams.height = 0;
        viewPager.setLayoutParams(layoutParams);
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
                cost += account.getCost();
            } else if (account.getType()==Account.TYPE_INCOME){
                income += account.getCost();
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

    }

    private void makeStatisticsOfTag(){
        for(Account account : accounts){
            if (account.getType() == Account.TYPE_COST){
                addTagInformationsToList(account, mCostTagNames, mCostTagInformations);
            } else {
                addTagInformationsToList(account, mIncomeTagNames, mIncomeTagInformations);
            }
        }
    }

    private void addTagInformationsToList(Account account, ArrayList<String> tagNames, ArrayList<TagInformation> tagInformations) {
        if (!tagNames.contains(account.getTagName())){
            tagNames.add(account.getTagName());
            TagInformation tagInformation = new TagInformation();
            tagInformation.setTagName(account.getTagName());
            tagInformation.setTagCost(account.getCost());
            tagInformations.add(tagInformation);
        } else {
            int position = tagNames.indexOf(account.getTagName());
            TagInformation tagInformation = tagInformations.get(position);
            tagInformation.setTagCost(tagInformation.getTagCost() + account.getCost());
            tagInformations.set(position, tagInformation);
        }
    }

    private void setupDataBase() {
        mDataBase = DBManager.getInstance(StatisticsActivity.this).getDataBase();
        accounts = new ArrayList<Account>();
        mIncomeTagInformations = new ArrayList<TagInformation>();
        mIncomeTagNames = new ArrayList<String>();
        mCostTagInformations = new ArrayList<TagInformation>();
        mCostTagNames = new ArrayList<String>();
    }

    private void setupPieChart() {
        mCostPieChartView.setTagInformation(mCostTagInformations);
        mIncomePieChartView.setTagInformation(mIncomeTagInformations);
    }

    @Override
    public void onBackPressed() {
        if (isSelected){
            hideChooseTimeLayout();
            isSelected = false;
        } else {
            super.onBackPressed();
        }
    }
}
