package com.example.dn.accounting.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.example.dn.accounting.Adapter.TagChoiceAdapter;
import com.example.dn.accounting.DataBase.DBManager;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.Model.Tag;
import com.example.dn.accounting.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddCostActivity extends AppCompatActivity {

    private ImageView mImageView;
    private EditText informationText;
    private EditText costText;

    private RecyclerView tagChoiceView;
    private TagChoiceAdapter mAdapter;

    private int type;  //收入or支出
    private ArrayList<Tag> tags;
    private SQLiteDatabase mDataBase;
    private float cost;
    private String information;
    private String time;

    private Intent intent;
    private List<Account> newAccounts;
    private int tagPosition = 0;
    private Button mCommitBtn;

//    private final int maxLen = 16;
//    private InputFilter filter = new InputFilter() {
//
//        @Override
//        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//            int dindex = 0;
//            int count = 0;
//
//            while (count <= maxLen && dindex < dest.length()) {
//                Log.d("out", "first count:" + count);
//                Log.d("out", "dindex:" + dindex);
//                char c = dest.charAt(dindex++);
//                if (c < 128) {
//                    count = count + 1;
//                } else {
//                    count = count + 2;
//                }
//            }
//
//            if (count > maxLen) {
//                return dest.subSequence(0, dindex - 1);
//            }
//
//            return dest.subSequence(0, dindex);
//
//            int sindex = 0;
//            while (count <= maxLen && sindex < source.length()) {
//                Log.d("out", "second");
//                char c = source.charAt(sindex++);
//                if (c < 128) {
//                    count = count + 1;
//                } else {
//                    count = count + 2;
//                }
//            }
//
//            if (count > maxLen) {
//                sindex--;
//            }
//
//            return source.subSequence(0, sindex);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cost);

        type = getIntent().getIntExtra("TYPE", Account.TYPE_COST);
        tags = new ArrayList<Tag>();
        if (type == Account.TYPE_COST){
            initCostTags();
        } else {
            initIncomeTags();
        }

        initView();

        mAdapter = new TagChoiceAdapter(this, tags);
        mAdapter.setOnItemClickListener(new TagChoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Tag tag = tags.get(position);
                mImageView.setImageDrawable(tag.getTagImage());
                mAdapter.notifyDataSetChanged();
                tagPosition = position;
            }
        });
        tagChoiceView.setLayoutManager(new GridLayoutManager(this,5));
        tagChoiceView.setAdapter(mAdapter);

        setupDataBase();
        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitInformation(type, tagPosition);
            }
        });

    }

    private void initView(){
        Resources resources = getResources();
        mImageView = (ImageView) findViewById(R.id.tag_imageview);
        if (type == Account.TYPE_COST) {
            mImageView.setImageDrawable(resources.getDrawable(R.drawable.food));
        } else {
            mImageView.setImageDrawable(resources.getDrawable(R.drawable.wages));
        }
        informationText = (EditText) findViewById(R.id.information_edittext);
//        informationText.setFilters(new InputFilter[]{filter});
        costText = (FormEditText) findViewById(R.id.cost_edittext);
        tagChoiceView = (RecyclerView) findViewById(R.id.tag_view);
        mCommitBtn = (Button) findViewById(R.id.commit_btn);
    }

    private void commitInformation(int type, int tagPosition) {
        if (intent == null || newAccounts == null){
            intent = new Intent(this,MainActivity.class);
            newAccounts = new ArrayList<Account>();
        }
        if (!TextUtils.isEmpty(informationText.getText()) && !TextUtils.isEmpty(costText.getText())){
            information = informationText.getText().toString();
            cost = Float.valueOf(costText.getText().toString());
            time = getCurrentTime();
            String tagName = tags.get(tagPosition).getTagName();
            ContentValues values = new ContentValues();
            values.put("information",information);
            values.put("cost", cost);
            values.put("time", time);
            values.put("tag", tagName);
            values.put("type", type);
            mDataBase.insert("Account",null,values);

            Account account = new Account();
            account.setInformation(information);
            account.setCost(cost);
            account.setTime(time);
            account.setTagName(tagName);
            account.setType(type);
            newAccounts.add(account);

            informationText.setText("");
            costText.setText("");
            informationText.requestFocus();
            Toast.makeText(AddCostActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddCostActivity.this,"信息不全，请完善信息",Toast.LENGTH_SHORT).show();
        }

    }

    private void setupDataBase() {
        mDataBase = DBManager.getInstance(AddCostActivity.this).getDataBase();
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
            startActivity(intent);
        }
    }

    private void initCostTags(){
        Resources resources = getResources();

        Tag foodTag = new Tag();
        foodTag.setTagName("吃喝");
        foodTag.setTagImage(resources.getDrawable(R.drawable.food));

        Tag transportationTag = new Tag();
        transportationTag.setTagName("交通");
        transportationTag.setTagImage(resources.getDrawable(R.drawable.transportation));

        Tag clothesTag = new Tag();
        clothesTag.setTagName("服饰");
        clothesTag.setTagImage(resources.getDrawable(R.drawable.clothes));

        Tag dailyExpensesTag = new Tag();
        dailyExpensesTag.setTagName("日用");
        dailyExpensesTag.setTagImage(resources.getDrawable(R.drawable.dailyexpenses));

        Tag redPacketTag = new Tag();
        redPacketTag.setTagName("红包");
        redPacketTag.setTagImage(resources.getDrawable(R.drawable.redpacket));

        Tag childrenTag = new Tag();
        childrenTag.setTagName("孩子");
        childrenTag.setTagImage(resources.getDrawable(R.drawable.children));

        Tag shoppingTag = new Tag();
        shoppingTag.setTagName("网购");
        shoppingTag.setTagImage(resources.getDrawable(R.drawable.shopping));

        Tag telephoneFareTag = new Tag();
        telephoneFareTag.setTagName("话费");
        telephoneFareTag.setTagImage(resources.getDrawable(R.drawable.telephonefare));

        Tag amusementTag = new Tag();
        amusementTag.setTagName("娱乐");
        amusementTag.setTagImage(resources.getDrawable(R.drawable.amusement));

        Tag medicalTreatmentTag = new Tag();
        medicalTreatmentTag.setTagName("医疗");
        medicalTreatmentTag.setTagImage(resources.getDrawable(R.drawable.medicaltreatment));

        Tag makeupTag = new Tag();
        makeupTag.setTagName("化妆品");
        makeupTag.setTagImage(resources.getDrawable(R.drawable.makeup));

        Tag othersTag = new Tag();
        othersTag.setTagName("其他");
        othersTag.setTagImage(resources.getDrawable(R.drawable.others));

        tags.add(foodTag);
        tags.add(transportationTag);
        tags.add(dailyExpensesTag);
        tags.add(clothesTag);
        tags.add(redPacketTag);
        tags.add(childrenTag);
        tags.add(shoppingTag);
        tags.add(telephoneFareTag);
        tags.add(amusementTag);
        tags.add(makeupTag);
        tags.add(medicalTreatmentTag);
        tags.add(othersTag);
    }

    private void initIncomeTags(){
        Resources resources = getResources();

        Tag wagesTag = new Tag();
        wagesTag.setTagName("工资");
        wagesTag.setTagImage(resources.getDrawable(R.drawable.wages));

        Tag redPacketTag = new Tag();
        redPacketTag.setTagName("红包");
        redPacketTag.setTagImage(resources.getDrawable(R.drawable.redpacket));

        Tag parttimeTag = new Tag();
        parttimeTag.setTagName("兼职");
        parttimeTag.setTagImage(resources.getDrawable(R.drawable.part_time));

        Tag investmentTag = new Tag();
        investmentTag.setTagName("投资");
        investmentTag.setTagImage(resources.getDrawable(R.drawable.investment));

        Tag bonusTag = new Tag();
        bonusTag.setTagName("奖金");
        bonusTag.setTagImage(resources.getDrawable(R.drawable.bonus));

        Tag othersTag = new Tag();
        othersTag.setTagName("其他");
        othersTag.setTagImage(resources.getDrawable(R.drawable.others));

        tags.add(wagesTag);
        tags.add(redPacketTag);
        tags.add(parttimeTag);
        tags.add(investmentTag);
        tags.add(bonusTag);
        tags.add(othersTag);
    }
}
