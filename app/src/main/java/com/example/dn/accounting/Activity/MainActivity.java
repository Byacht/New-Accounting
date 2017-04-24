package com.example.dn.accounting.Activity;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dn.accounting.Adapter.AccountAdapter;
import com.example.dn.accounting.Api.ApiManager;
import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.Model.DailySentence;
import com.example.dn.accounting.R;
import com.example.dn.accounting.Utils.DBUtil;
import com.example.dn.accounting.Utils.TimeUtil;
import com.example.dn.accounting.View.YearMonthPickerDialog;
import com.zhy.changeskin.SkinManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.income_tv)
    TextView mIncomeTv;
    @BindView(R.id.cost_tv)
    TextView mCostTv;
    @BindView(R.id.rv_account_item)
    RecyclerView mRvAccountItem;
    @BindView(R.id.select_all_btn)
    Button mSelectAllBtn;
    @BindView(R.id.delete_btn)
    Button mDeleteBtn;
    @BindView(R.id.batch_delete_layout)
    LinearLayout mBatchDeleteLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.float_btn_add_account)
    FloatingActionButton mBtnAddAccount;
    @BindView(R.id.drawerlayout)
    DrawerLayout mDrawerlayout;

    private String mCurrentYearAndMonth;
    private String mSearchYear;
    private String mSearchMonth;
    private float mIncome;
    private float mCost;

    private SQLiteDatabase mDataBase;
    private List<Account> accounts;

    private AccountAdapter adapter;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private List<Account> selectedAccounts;  //记录被选中的accounts
    private boolean isShowCheckBox;  //标记checkBox是否显示，显示时，按下返回键，将隐藏checkBox，而不会退出Activity
    private boolean isSelectAll = true;

    private SharedPreferences mSharedPreferences;
    private boolean firstOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SkinManager.getInstance().register(this);
        ButterKnife.bind(this);

        initData();
        setupToolBar();
        setupWindowAnimations();
        setupDataBase();
        getDatasFromDataBase();
        setIncomeAndCost();  //计算当月的收入和支出，并显示在textview中
        setupRecylerView();
        setupDrawer();
        setupDrawerContent();

//        ViewGroup.LayoutParams layoutParams = topView.getLayoutParams();
//        int height = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            height = getResources().getDimensionPixelSize(resourceId);
//        }
//        layoutParams.height = height;
//        topView.setLayoutParams(layoutParams);
//        topView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void initData() {
        mCurrentYearAndMonth = TimeUtil.getCurrentTime().substring(0, 7);  //取得当前年月，查找数据库中符合这个时间的数据
        mSearchYear = mCurrentYearAndMonth.substring(0, 4);
        mSearchMonth = mCurrentYearAndMonth.substring(5, 7);

        mSharedPreferences = getSharedPreferences("overCost", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        /*
         * 如果是新的一个月,则重置超额提醒标识"isAlarmed"
         */
        if (TimeUtil.getCurrentTime().substring(8, 10).equals("01")){
            if (mSharedPreferences.getBoolean("isNextMonth", true)){
                editor.putBoolean("isShowSnackBar", true);
                editor.putBoolean("isNextMonth", false);
                editor.commit();
            }
        } else {
            editor.putBoolean("isNextMonth", true);
            editor.commit();
        }
    }

    @OnClick(R.id.show_cost_income_layout)
    public void showTimePickerDialog() {
        new YearMonthPickerDialog(MainActivity.this, android.app.AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mSearchYear = year + "";
                        month += 1;
                        mSearchMonth = month + "";
                        if (mSearchMonth.length() == 1) {
                            mSearchMonth = "0" + mSearchMonth;
                        }
                        mCurrentYearAndMonth = mSearchYear + "-" + mSearchMonth;
                        notifyTimeChange();
                    }
                },
                Integer.valueOf(mSearchYear), Integer.valueOf(mSearchMonth) - 1, 1)
                .show();
    }

    @OnClick(R.id.float_btn_add_account)
    public void addAccount(){
        Intent intent = new Intent(MainActivity.this, AddAccountActivity.class);
        String transitionName = getString(R.string.add);
        ActivityOptions shareElement = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, mBtnAddAccount, transitionName);
        startActivityForResult(intent, 1000, shareElement.toBundle());
    }

    private void setAlarm() {
        if (mSharedPreferences.getBoolean("isAlarmed", false) && firstOpen) {
            String overCostText = mSharedPreferences.getString("overCost", "1000");
            float overCost = Float.valueOf(overCostText);
            if (overCost < mCost) {
                mCostTv.setTextColor(Color.RED);
                if (mSharedPreferences.getBoolean("isShowSnackBar", false)) {
                    Snackbar.make(mCostTv, "当月支出已超出限额", Snackbar.LENGTH_LONG)
                            .setAction("不再提醒", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.putBoolean("isShowSnackBar", false);
                                    editor.commit();
                                }
                            })
                            .show();
                }
            } else {
                mCostTv.setTextColor(Color.WHITE);
            }
            firstOpen = false;
        }
    }

    private void setupToolBar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupWindowAnimations() {
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(500);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
    }

    private void setupDataBase() {
        mDataBase = DBManager.getInstance(MainActivity.this).getDataBase();
        accounts = new ArrayList<Account>();
    }

    private void getDatasFromDataBase() {
        Cursor cursor = mDataBase.query("Account", null, "time LIKE ?", new String[]{mCurrentYearAndMonth + "%"}, null, null, null);
        accounts.addAll(DBUtil.query(cursor));
        cursor.close();
    }

    private void setIncomeAndCost() {
        mIncome = 0;
        mCost = 0;
        for (Account account : accounts) {
            if (account.getType() == Account.TYPE_INCOME) {
                mIncome += account.getCost();
            } else {
                mCost += account.getCost();
            }
        }
        mIncomeTv.setText(mCurrentYearAndMonth.substring(5, 7) + "月收入" + "\n" + String.format("%.1f", mIncome));
        mCostTv.setText(mCurrentYearAndMonth.substring(5, 7) + "月支出" + "\n" + String.format("%.1f", mCost));
    }

    private void setupRecylerView() {
        adapter = new AccountAdapter(MainActivity.this, accounts);
        mRvAccountItem.setLayoutManager(new LinearLayoutManager(this));
        mRvAccountItem.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new AccountAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(new String[]{"删除数据", "更改数据", "批量删除"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    deleteData(position);
                                } else if (which == 1) {
                                    updateData(position);
                                } else if (which == 2) {
                                    deleteDataInBatches();
                                }
                            }
                        })
                        .show();
            }
        });
        if (accounts.size() != 0) {
            mRvAccountItem.smoothScrollToPosition(accounts.size() - 1);
        }
    }

    private void deleteData(final int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("删除")
                .setMessage("确认删除所选条目？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String time = accounts.get(position).getTime();
                        mDataBase.delete("Account", "time == ?", new String[]{time});
                        accounts.remove(position);
                        adapter.notifyItemChanged(position);
                        setIncomeAndCost();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateData(final int position) {
        final View view = LayoutInflater.from(this).inflate(R.layout.add_account_dialog, null);
        final EditText informationText = (EditText) view.findViewById(R.id.information_edittext);
        final EditText costText = (EditText) view.findViewById(R.id.cost_edittext);
        costText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 1) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 2);
                        costText.setText(s);
                        costText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    costText.setText(s);
                    costText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        costText.setText(s.subSequence(0, 1));
                        costText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final String time = accounts.get(position).getTime();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("请输入信息")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values = new ContentValues();
                        Account account = accounts.get(position);
                        if (!TextUtils.isEmpty(informationText.getText()) && !TextUtils.isEmpty(costText.getText())) {
                            String information = informationText.getText().toString();
                            float cost = Float.valueOf(costText.getText().toString());
                            values.put("information", information);
                            values.put("cost", cost);
                            account.setInformation(information);
                            account.setCost(cost);
                        } else if (TextUtils.isEmpty(informationText.getText()) && TextUtils.isEmpty(costText.getText())) {
                            return;
                        } else if (TextUtils.isEmpty(costText.getText())) {
                            String information = informationText.getText().toString();
                            values.put("information", information);
                            account.setInformation(information);
                        } else if (TextUtils.isEmpty(informationText.getText())) {
                            float cost = Float.valueOf(costText.getText().toString());
                            values.put("cost", cost);
                            account.setCost(cost);
                        }

                        mDataBase.update("Account", values, "time == ?", new String[]{time});
                        accounts.set(position, account);
                        adapter.notifyItemChanged(position);
                        setIncomeAndCost();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteDataInBatches() {
        isShowCheckBox = true;
        for (Account account : accounts) {
            account.setShowCheckBox(true);
        }
        adapter.notifyDataSetChanged();
        if (selectedAccounts == null) {
            selectedAccounts = new ArrayList<Account>();
        }
        adapter.setOnItemIsCheckedListener(new AccountAdapter.OnItemIsCheckedListener() {
            @Override
            public void onItemIsChecked(Account account) {
                if (account.getIsCheck() && !selectedAccounts.contains(account)) {
                    selectedAccounts.add(account);
                } else if (!account.getIsCheck() && selectedAccounts.contains(account)) {
                    selectedAccounts.remove(account);
                }
            }
        });
        mBatchDeleteLayout.setVisibility(View.VISIBLE);
        mSelectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectAll) {
                    for (Account account : accounts) {
                        account.setIsCheck(true);
                        if (!selectedAccounts.contains(account)) {
                            selectedAccounts.add(account);
                        }
                    }
                    isSelectAll = false;
                } else {
                    for (Account account : accounts) {
                        account.setIsCheck(false);
                        if (selectedAccounts.contains(account)) {
                            selectedAccounts.remove(account);
                        }
                    }
                    isSelectAll = true;
                }
                adapter.notifyDataSetChanged();
            }
        });
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAccounts != null && selectedAccounts.size() > 0) {
                    showAlertDialog();

                } else {
                    Toast.makeText(MainActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showAlertDialog() {
        TextView textView = new TextView(MainActivity.this);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 10, 0, 0);
        textView.setText("删除");
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setCustomTitle(textView)
                .setMessage("确定删除所选条目？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Account account : selectedAccounts) {
                            String time = account.getTime();
                            mDataBase.delete("Account", "time == ?", new String[]{time});
                            if (accounts.contains(account)) {
                                accounts.remove(account);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        mBatchDeleteLayout.setVisibility(View.GONE);
                        for (Account account : accounts) {
                            account.setShowCheckBox(false);
                        }
                        adapter.notifyDataSetChanged();
                        setIncomeAndCost();
                        isShowCheckBox = false;
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void notifyTimeChange() {
        accounts.clear();
        getDatasFromDataBase();
        setIncomeAndCost();
        adapter.notifyDataSetChanged();
        if (accounts.size() > 0) {
            mRvAccountItem.smoothScrollToPosition(accounts.size() - 1);
        }
    }

    private void setupDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerlayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Call<DailySentence> call = ApiManager.getInstance().getDailySentenceApi().getDailySentence();
                call.enqueue(new Callback<DailySentence>() {
                    @Override
                    public void onResponse(Call<DailySentence> call, Response<DailySentence> response) {
                        String sentence = response.body().getSentence();
                        TextView dailySentenceTv = (TextView) findViewById(R.id.tv_nav_header);
                        dailySentenceTv.setText(sentence);
                        Log.d("out", "sentence:" + sentence);
                    }

                    @Override
                    public void onFailure(Call<DailySentence> call, Throwable t) {
                        Log.d("out", "fail:" + t.toString());
                    }
                });
                ImageView imageView = (ImageView) findViewById(R.id.navigation_iv);
                Bitmap bt = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + "head.jpg");// 从SD卡中找头像，转换成Bitmap
                if (bt != null) {
                    @SuppressWarnings("deprecation")
                    Drawable drawable = new BitmapDrawable(bt);// 转换成drawable
                    imageView.setImageDrawable(drawable);
                } else {
                    /**
                     * 如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
                     */
                    imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();
        mDrawerlayout.setDrawerListener(actionBarDrawerToggle);
    }

    private void setupDrawerContent() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.nav_statistics:
                        Intent statisticsIntent = new Intent(MainActivity.this, StatisticsActivity.class);
                        startActivity(statisticsIntent);
                        break;
                    case R.id.nav_alarm:
                        Intent alarmIntent = new Intent(MainActivity.this, AlarmActivity.class);
                        startActivity(alarmIntent);
                        break;
                    case R.id.nav_setting:
                        Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(settingIntent);
                        break;
                    case R.id.nav_about:
                        break;
                }
                mDrawerlayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mCurrentYearAndMonth.equals(TimeUtil.getCurrentTime().substring(0, 7))) {
            ArrayList<Account> newAccounts = (ArrayList<Account>) intent.getSerializableExtra("newAccounts");
            if (newAccounts != null){
                for (Account account : newAccounts) {
                    accounts.add(account);
                    adapter.notifyDataSetChanged();
                }

                if (accounts.size() > 0) {
                    mRvAccountItem.smoothScrollToPosition(accounts.size() - 1);
                }
            }
        }
        setIncomeAndCost();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setAlarm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (isShowCheckBox) {
            selectedAccounts.clear();
            for (Account account : accounts) {
                account.setShowCheckBox(false);
            }
            adapter.notifyDataSetChanged();
            mBatchDeleteLayout.setVisibility(View.GONE);
            isShowCheckBox = false;
        } else {
            super.onBackPressed();
        }
    }

}
