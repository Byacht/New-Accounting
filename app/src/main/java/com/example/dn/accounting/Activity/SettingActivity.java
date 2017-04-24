package com.example.dn.accounting.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dn.accounting.R;
import com.zcw.togglebutton.ToggleButton;
import com.zhy.changeskin.SkinManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout mSetPortraitLayout;
    private LinearLayout mSetBackgroundLayout;
    private ImageView mPortraitImageView;
    private ImageView mBackgroundImageView;
    private Bitmap head;// 头像Bitmap
    private Toolbar mToolbar;
    private ToggleButton mToggleButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String path = Environment.getExternalStorageDirectory() + "/";// sd路径
    private View view;
    private EditText editText;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SkinManager.getInstance().register(this);
        initView();
        setupToolBar();
        initListener();

        sharedPreferences = getSharedPreferences("overCost", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mTextView = (TextView) findViewById(R.id.tv_over_cost);

        mToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    final AlertDialog dialog = createDialog();
                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputCostLimit(dialog);
                        }
                    });
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mToggleButton.setToggleOff();
                            dialog.dismiss();
                        }
                    });
                } else {
                    editor.clear();
                    editor.putBoolean("isAlarmed", false);
                    editor.putBoolean("isShowSnackBar", false);
                    editor.commit();
                    mTextView.setVisibility(View.GONE);
                }
            }
        });

        if (sharedPreferences.getBoolean("isAlarmed", false)) {
            mToggleButton.setToggleOn();
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(sharedPreferences.getString("overCost", "0") + "元");
        } else {
            mToggleButton.setToggleOff();
            mTextView.setVisibility(View.GONE);
        }

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean("isAlarmed", false)) {
                    final AlertDialog dialog = createDialog();
                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputCostLimit(dialog);
                        }
                    });
                }

            }
        });

        mSetPortraitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSetBackgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View chooseColorView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_choose_color, null);
                ImageView blueColor = (ImageView) chooseColorView.findViewById(R.id.img_bg_color_pink);
                blueColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SkinManager.getInstance().removeAnySkin();
                    }
                });
                new AlertDialog.Builder(SettingActivity.this)
                        .setView(chooseColorView)
                        .setTitle("请选择背景颜色")
                        .show();

            }

        });
    }

    private void inputCostLimit(AlertDialog dialog) {
        String overCost = editText.getText().toString();
        if (overCost.length() == 0) {
            Toast.makeText(SettingActivity.this, "请设置额度", Toast.LENGTH_SHORT).show();
        } else {
            editor.putString("overCost", overCost);
            editor.putBoolean("isAlarmed", true);
            editor.putBoolean("isShowSnackBar", true);
            editor.commit();
            mTextView.setText(overCost + "元");
            mTextView.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }
    }

    private AlertDialog createDialog() {
        view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_over_cost, null);
        editText = (EditText) view.findViewById(R.id.et_over_cost);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this)
                .setView(view)
                .setTitle("请设置每月额度")
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null);
        return builder.create();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        mSetPortraitLayout = (LinearLayout) findViewById(R.id.set_portrait_layout);
        mSetBackgroundLayout = (LinearLayout) findViewById(R.id.set_background_layout);
        mPortraitImageView = (ImageView) findViewById(R.id.portrait_imageview);
        mBackgroundImageView = (ImageView) findViewById(R.id.background_imageview);
        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton_setting);
        Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");// 从SD卡中找头像，转换成Bitmap
        if (bt != null) {
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);// 转换成drawable
            mPortraitImageView.setImageDrawable(drawable);
        } else {
            /**
             * 如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
             *
             */
        }
    }

    private void enableCloseDialog(DialogInterface dialogInterface, boolean able) {
        try {
            Log.d("out", "enable1:" + able);
            java.lang.reflect.Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, able);
            Log.d("out", "enable2:" + able);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("out", e.toString());
        }
    }

    private void setupToolBar(){
        mToolbar.setTitle("设置");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initListener() {
        mPortraitImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.portrait_imageview:// 更换头像
                showTypeDialog();
                break;
        }
    }

    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.select_photo_dialog, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.gallery_tv);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.camera_tv);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        setPicToView(head);// 保存在SD卡中
                        mPortraitImageView.setImageBitmap(head);// 用ImageView显示出来
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.d("out", "sdcard is wrong");
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        if (!file.exists()) {
            Log.d("out", "333");
            file.mkdirs();// 创建文件夹
            Log.d("out", "444");
        }
        String fileName = path + "head.jpg";// 图片名字
//        File finalImageFile = new File(file, "head.jpg");
//        if (finalImageFile.exists()) {
//            finalImageFile.delete();
//        }
//        try {
//            finalImageFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            Log.d("out", "111"+ fileName);
//            b = new FileOutputStream(fileName, true);
            b = new FileOutputStream(fileName);
            Log.d("out", "222");
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
