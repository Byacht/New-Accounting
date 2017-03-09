package com.example.dn.accounting.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dn.accounting.Adapter.MyFragmentPagerAdapter;
import com.example.dn.accounting.DataBase.AccountDataBase;
import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.Model.TagInformation;
import com.example.dn.accounting.R;
import com.example.dn.accounting.Utils.TimeUtil;
import com.example.dn.accounting.View.MyFragment;
import com.example.dn.accounting.View.PieChartView;
import com.example.dn.accounting.View.StatisticsView;
import com.example.dn.accounting.View.YearPickerDialog;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private SQLiteDatabase mDataBase;
    private String mSearchTime;
    private String mSelectedYear;
    private String mSelectedMonth;

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

    private int mIndex = 0;

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
        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isSelected){
                    hideChooseTimeLayout();
                    isSelected = false;
                }
                return false;
            }
        });
    }

    private void initView(){
        viewHeight = 350;
        mSearchTime = TimeUtil.getCurrentTime().substring(0, 7);
        mSelectedYear = mSearchTime.substring(0, 4);
        mSelectedMonth = mSearchTime.substring(5, 7);

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
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex = viewPager.getCurrentItem();
                /* 得到当前的fragment，通过接口回调响应fragment的点击事件 */
                MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + mIndex);
                myFragment.setOnShowTimeListener(new MyFragment.OnShowTime() {
                    @Override
                    public void showTime(String time) {
                        mSelectedMonth = time;
                        mSearchTime = mSearchTime.substring(0, 5) + mSelectedMonth;
                        mShowTimeTextView.setText(mSearchTime);
                        hideChooseTimeLayout();
                        notifyTimeChange();
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isSelected = true;
                showChooseTimeLayout();
                if (tab.getPosition() == 0){
                    hideChooseTimeLayout();
                    showYearPickerDialog();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                isSelected = true;
                showChooseTimeLayout();
                if (tab.getPosition() == 0){
                    hideChooseTimeLayout();
                    showYearPickerDialog();
                }
            }
        });
    }

    private void showYearPickerDialog() {
        YearPickerDialog dialog = new YearPickerDialog(StatisticsActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mShowTimeTextView.setText(year + "年");
                        mSelectedYear = String.valueOf(year);
                        mSearchTime = mSelectedYear + "-";
                        notifyTimeChange();
                    }
                },
                Integer.valueOf(mSelectedYear), 1, 1);
        dialog.show();
    }

    private void showChooseTimeLayout() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams.height = viewHeight;
        viewPager.setLayoutParams(layoutParams);
    }

    private void hideChooseTimeLayout(){
        isSelected = false;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams.height = 0;
        viewPager.setLayoutParams(layoutParams);
    }

    private void showStatisticsView() {
        calculateCostAndIncome();

        all_cost = (StatisticsView) findViewById(R.id.cost_statistics);
        all_cost.setCost(cost);
        if (cost == 0){
            all_cost.setLength(0);
        } else {
            all_cost.setLength(cost/(cost+income));
        }

        all_income = (StatisticsView) findViewById(R.id.income_statistics);
        all_income.setCost(income);
        if (income == 0){
            all_income.setLength(0);
        } else {
            all_income.setLength(income/(cost+income));
        }

        balance = cost-income;
        all_banlance = (StatisticsView) findViewById(R.id.balance_statistics);
        if (balance>0){
            all_banlance.setColor(getResources().getColor(R.color.costColor));
        }else {
            all_banlance.setColor(getResources().getColor(R.color.incomeColor));
        }
        all_banlance.setCost(balance);
        if (balance == 0){
            all_banlance.setLength(0);
        } else {
            all_banlance.setLength(Math.abs(balance)/(cost+income));
        }
    }

    private void calculateCostAndIncome() {
        getDataFromDataBase();

        cost = 0;
        income = 0;
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

        Cursor cursor = mDataBase.query("Account",null,"time LIKE ?", new String[]{mSearchTime + "%"},null,null,null);
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
        if (mCostTagInformations.size() == 0){
            mCostPieChartView.setVisibility(View.GONE);
        } else {
            mCostPieChartView.setVisibility(View.VISIBLE);
        }
        if (mIncomeTagInformations.size() == 0){
            mIncomePieChartView.setVisibility(View.GONE);
        } else {
            mIncomePieChartView.setVisibility(View.VISIBLE);
        }
    }

    private void notifyTimeChange(){
        accounts.clear();
        mCostTagInformations.clear();
        mIncomeTagInformations.clear();
        mCostTagNames.clear();
        mIncomeTagNames.clear();
        showStatisticsView();
        makeStatisticsOfTag();
        setupPieChart();
        all_cost.invalidate();
        all_income.invalidate();
        all_banlance.invalidate();
        mCostPieChartView.invalidate();
        mIncomePieChartView.invalidate();
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
