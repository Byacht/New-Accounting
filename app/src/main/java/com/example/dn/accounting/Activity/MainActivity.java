package com.example.dn.accounting.Activity;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.andreabaccega.widget.FormEditText;
import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.Adapter.AccountAdapter;
import com.example.dn.accounting.DataBase.AccountDataBase;
import com.example.dn.accounting.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView add;
    private Toolbar toolbar;
    private List<Account> accounts;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private SQLiteDatabase mDataBase;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private String test = "anothertest2.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setupToolBar();
        setupWindowAnimations();
        setupDataBase();
        getDatasFromDataBase();
        setupRecylerView();
        setupDrawer();
        setupDrawerContent();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddAccountActivity.class);
                String transitionName = getString(R.string.add);
                ActivityOptions shareElement = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,add,transitionName);
                startActivityForResult(intent,1000,shareElement.toBundle());
            }
        });

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        add = (ImageView) findViewById(R.id.add);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
    }

    private void setupToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupWindowAnimations() {
        // Re-enter transition is executed when returning to this activity
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

    private void setupRecylerView() {
        adapter = new AccountAdapter(MainActivity.this,accounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new AccountAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(new String[]{"删除数据", "更新数据"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    deleteData(position);
                                } else if (which == 1){
                                    updateData(position);
                                }
                            }
                        })
                        .show();
            }
        });
        if (accounts.size()!=0){
            recyclerView.smoothScrollToPosition(accounts.size()-1);
        }
    }

    private void deleteData(int position){
        String time = accounts.get(position).getTime();
        mDataBase.delete("Account","time == ?",new String[]{time});
        accounts.remove(position);
        adapter.notifyItemChanged(position);
    }

    private void updateData(final int position){
        final View view = LayoutInflater.from(this).inflate(R.layout.add_account_dialog,null);
        final EditText informationText = (EditText) view.findViewById(R.id.information_edittext);
        final FormEditText costText = (FormEditText) view.findViewById(R.id.cost_edittext);
        final String time = accounts.get(position).getTime();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("请输入信息")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values = new ContentValues();
                        Account account = accounts.get(position);
                        if (!TextUtils.isEmpty(informationText.getText()) && !TextUtils.isEmpty(costText.getText())){
                            String information = informationText.getText().toString();
                            float cost = Float.valueOf(costText.getText().toString());
                            values.put("information",information);
                            values.put("cost",cost);
                            account.setInformation(information);
                            account.setCost(cost);
                        } else if (TextUtils.isEmpty(informationText.getText()) && TextUtils.isEmpty(costText.getText())){

                        } else if (TextUtils.isEmpty(costText.getText())){
                            String information = informationText.getText().toString();
                            values.put("information",information);
                            account.setInformation(information);
                        } else if (TextUtils.isEmpty(informationText.getText())){
                            float cost = Float.valueOf(costText.getText().toString());
                            values.put("cost",cost);
                            account.setCost(cost);
                        }

                        mDataBase.update("Account",values,"time == ?",new String[]{time});
                        accounts.set(position,account);
                        adapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2000){
            ArrayList<Account> newAccounts = (ArrayList<Account>) data.getSerializableExtra("newAccounts");
            for (Account account : newAccounts){
                Log.d("out",account.getInformation());
                accounts.add(account);
                adapter.notifyDataSetChanged();
            }

            recyclerView.smoothScrollToPosition(accounts.size()-1);
        }
    }

    private void setupDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.open,R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    private void setupDrawerContent() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()){
                    case R.id.nav_statistics:
                        Intent intent = new Intent(MainActivity.this,StatisticsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_setting:
                        break;
                    case R.id.nav_about:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
