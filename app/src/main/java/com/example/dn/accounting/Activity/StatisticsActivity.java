package com.example.dn.accounting.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dn.accounting.Adapter.MyFragmentPagerAdapter;
import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.Model.TagInformation;
import com.example.dn.accounting.R;
import com.example.dn.accounting.Utils.DBUtil;
import com.example.dn.accounting.Utils.TimeUtil;
import com.example.dn.accounting.View.MyFragment;
import com.example.dn.accounting.View.PieChartView;
import com.example.dn.accounting.View.StatisticsView;
import com.example.dn.accounting.View.YearPickerDialog;
import com.zhy.changeskin.SkinManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_statistics)
    Toolbar mToolbar;
    @BindView(R.id.showtime_tv)
    TextView mShowTimeTv;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.cost_statistics_view)
    StatisticsView mCostStatisticsView;
    @BindView(R.id.income_statistics_view)
    StatisticsView mIncomeStatisticsView;
    @BindView(R.id.balance_statistics_view)
    StatisticsView mBalanceStatisticsView;
    @BindView(R.id.pie_chart_cost)
    PieChartView mCostPieChartView;
    @BindView(R.id.pie_chart_income)
    PieChartView mIncomePieChartView;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    @BindColor(R.color.costColor)
    int costColor;
    @BindColor(R.color.incomeColor)
    int incomeColor;

    private SQLiteDatabase mDataBase;
    private String mSearchTime;
    private String mSelectedYear;
    private String mSelectedMonth;
    private String mStartTime;
    private String mEndTime;

    private MyFragmentPagerAdapter adapter;
    private int viewHeight;  //选择年月视图的高度
    private boolean isSelected = false;  //是否处于选择时间的状态，若是，按下返回键将隐藏选择视图，否则退出Activity

    private float income = 0;
    private float cost = 0;
    private float balance;

    private List<Account> accounts;
    private ArrayList<TagInformation> mCostTagInformations;
    private ArrayList<TagInformation> mIncomeTagInformations;
    private ArrayList<String> mCostTagNames;
    private ArrayList<String> mIncomeTagNames;

    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);
        SkinManager.getInstance().register(this);

        initView();
        setupToolBar();
        setupTabLayout();
        setupDataBase();
        getDataFromDataBase();
        showStatisticsView();
        makeStatisticsOfTag();
        setupPieChart();
        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isSelected) {
                    hideChooseTimeLayout();
                    isSelected = false;
                }
                return false;
            }
        });
    }

    private void initView() {
        viewHeight = 400;
        mSearchTime = TimeUtil.getCurrentTime().substring(0, 7);
        mSelectedYear = mSearchTime.substring(0, 4);
        mSelectedMonth = mSearchTime.substring(5, 7);
        mShowTimeTv.setText(mSelectedYear + "-" + mSelectedMonth);
    }

    private void setupToolBar() {
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("统计分析");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupTabLayout() {
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(Color.alpha(0));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex = mViewPager.getCurrentItem();
                /* 得到当前的fragment，通过接口回调响应fragment的点击事件 */
                MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + mIndex);
                myFragment.setOnShowTimeListener(new MyFragment.OnShowTime() {
                    @Override
                    public void showTime(String time) {
                        mSelectedMonth = time;
                        mSearchTime = mSearchTime.substring(0, 5) + mSelectedMonth;
                        mShowTimeTv.setText(mSearchTime);
                        hideChooseTimeLayout();
                        notifyTimeChange(0);
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
                if (tab.getPosition() == 0) {
                    hideChooseTimeLayout();
                    showYearPickerDialog();
                }
                if (tab.getPosition() == 1) {
                    isSelected = true;
                    showChooseTimeLayout();
                }
                if (tab.getPosition() == 2) {
                    hideChooseTimeLayout();
                    showDoubleDatePicker();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showYearPickerDialog();
                }
                if (tab.getPosition() == 1) {
                    isSelected = true;
                    showChooseTimeLayout();
                }
                if (tab.getPosition() == 2) {
                    showDoubleDatePicker();
                }
            }
        });
    }

    private void showYearPickerDialog() {
        YearPickerDialog dialog = new YearPickerDialog(StatisticsActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mShowTimeTv.setText(year + "年");
                        mSelectedYear = String.valueOf(year);
                        mSearchTime = mSelectedYear + "-";
                        notifyTimeChange(0);
                    }
                },
                Integer.valueOf(mSelectedYear), 1, 1);
        dialog.show();
    }

    private void showDoubleDatePicker() {
        View view = LayoutInflater.from(StatisticsActivity.this).inflate(R.layout.double_datepicker_layout, null);
        final DatePicker startDatePicker = (DatePicker) view.findViewById(R.id.StartDate_dp);
        final DatePicker endDatePicker = (DatePicker) view.findViewById(R.id.EndDate_dp);

        final AlertDialog dialog = new AlertDialog.Builder(StatisticsActivity.this)
                .setView(view)
                .setTitle("请选择查询时间段")
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startYear = startDatePicker.getYear();
                int startMonth = startDatePicker.getMonth() + 1;
                int startDay = startDatePicker.getDayOfMonth();
                int endYear = endDatePicker.getYear();
                int endMonth = endDatePicker.getMonth() + 1;
                int endDay = endDatePicker.getDayOfMonth();
                if (endYear >= startYear && endMonth >= startMonth && endDay >= startDay){
                    String startM = formDate(startMonth);
                    String startD = formDate(startDay);
                    String endM = formDate(endMonth);
                    String endD = formDate(endDay);

                    mStartTime = startYear + "-" + startM + "-" + startD;
                    mEndTime = endYear + "-" + endM + "-" + endD;
                    mShowTimeTv.setText(mStartTime + " ~ " + mEndTime);
                    notifyTimeChange(1);
                    dialog.dismiss();
                } else {
                    Toast.makeText(StatisticsActivity.this, "起始时间不能晚于终止时间", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String formDate(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return time + "";
    }

    private void searchDataOfCustomTime() {
        String sql = "select * from Account where time between '" + mStartTime + "' and '" + mEndTime + " 23:59:59'";
        Cursor cursor = mDataBase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
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

    private void showChooseTimeLayout() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.height = viewHeight;
        mViewPager.setLayoutParams(layoutParams);
    }

    private void hideChooseTimeLayout() {
        isSelected = false;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.height = 0;
        mViewPager.setLayoutParams(layoutParams);
    }

    private void showStatisticsView() {
        calculateCostAndIncome();

        mCostStatisticsView.setCost(cost);
        if (cost == 0) {
            mCostStatisticsView.setLength(0);
        } else {
            mCostStatisticsView.setLength(cost / (cost + income));
        }

        mIncomeStatisticsView.setCost(income);
        if (income == 0) {
            mIncomeStatisticsView.setLength(0);
        } else {
            mIncomeStatisticsView.setLength(income / (cost + income));
        }

        balance = cost - income;
        if (balance > 0) {
            mBalanceStatisticsView.setColor(costColor);
        } else {
            mBalanceStatisticsView.setColor(incomeColor);
        }
        mBalanceStatisticsView.setCost(balance);
        if (balance == 0) {
            mBalanceStatisticsView.setLength(0);
        } else {
            mBalanceStatisticsView.setLength(Math.abs(balance) / (cost + income));
        }
    }

    private void calculateCostAndIncome() {
        cost = 0;
        income = 0;
        for (Account account : accounts) {
            if (account.getType() == Account.TYPE_COST) {
                cost += account.getCost();
            } else if (account.getType() == Account.TYPE_INCOME) {
                income += account.getCost();
            }
        }
    }

    private void getDataFromDataBase() {
        if (mDataBase != null) {
            Cursor cursor = mDataBase.query("Account", null, "time LIKE ?", new String[]{mSearchTime + "%"}, null, null, null);
            accounts.addAll(DBUtil.query(cursor));
            cursor.close();
        }
    }

    private void makeStatisticsOfTag() {
        for (Account account : accounts) {
            if (account.getType() == Account.TYPE_COST) {
                addTagInformationsToList(account, mCostTagNames, mCostTagInformations);
            } else {
                addTagInformationsToList(account, mIncomeTagNames, mIncomeTagInformations);
            }
        }
    }

    private void addTagInformationsToList(Account account, ArrayList<String> tagNames, ArrayList<TagInformation> tagInformations) {
        if (!tagNames.contains(account.getTagName())) {
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
        if (mCostTagInformations.size() == 0) {
            mCostPieChartView.setVisibility(View.GONE);
        } else {
            mCostPieChartView.setVisibility(View.VISIBLE);
        }
        if (mIncomeTagInformations.size() == 0) {
            mIncomePieChartView.setVisibility(View.GONE);
        } else {
            mIncomePieChartView.setVisibility(View.VISIBLE);
        }
    }

    private void notifyTimeChange(int searchWay) {
        accounts.clear();
        mCostTagInformations.clear();
        mIncomeTagInformations.clear();
        mCostTagNames.clear();
        mIncomeTagNames.clear();
        if (searchWay == 0) {
            getDataFromDataBase();
        } else if (searchWay == 1) {
            searchDataOfCustomTime();
        }
        showStatisticsView();
        makeStatisticsOfTag();
        setupPieChart();
        mCostStatisticsView.invalidate();
        mIncomeStatisticsView.invalidate();
        mBalanceStatisticsView.invalidate();
        mCostPieChartView.invalidate();
        mIncomePieChartView.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (isSelected) {
            hideChooseTimeLayout();
            isSelected = false;
        } else {
            super.onBackPressed();
        }
    }
}
