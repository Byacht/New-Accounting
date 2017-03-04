package com.example.dn.accounting.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.example.dn.accounting.R;

/**
 * Created by dn on 2016/11/13.
 */

public class AccountView extends View {

    private String title;
    private int titleColor;
    private float cost;
    private int costColor;
    private String information;

    public void setInformation(String information) {
        this.information = information;
    }

    private String time;

    private float accountWidth;
    private float accountHeight;

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setCostColor(int costColor) {
        this.costColor = costColor;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public AccountView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getPropertiesFromXml(context,attrs);
    }

    private void getPropertiesFromXml(Context context,AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AccountView);
        title = ta.getString(R.styleable.AccountView_title);
        titleColor = ta.getColor(R.styleable.AccountView_titlebackground,0);
        cost = ta.getFloat(R.styleable.AccountView_cost,0);
        costColor = ta.getColor(R.styleable.AccountView_costcolor,0);
        information = ta.getString(R.styleable.AccountView_information);
        time = ta.getString(R.styleable.AccountView_time);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        accountWidth = (float)(getWidth()*0.9);
        accountHeight = (float)(accountWidth*0.8);

        Paint linePaint = new Paint();
        linePaint.setColor(titleColor);
        canvas.drawRect(20,20,20+accountWidth,(float)(20+accountHeight*0.2),linePaint);

        linePaint.setColor(getResources().getColor(R.color.black));
        linePaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(20,(float)(20+accountHeight*0.2),20,(float)(20+accountHeight*0.6),linePaint);
        canvas.drawLine(20+accountWidth,(float)(20+accountHeight*0.2),20+accountWidth,(float)(20+accountHeight*0.6),linePaint);

        canvas.drawArc(-20,(float)(20+accountHeight*0.6),60,(float)(20+accountHeight*0.6)+80,-90,180,false,linePaint);
        canvas.drawArc(-20+accountWidth,(float)(20+accountHeight*0.6),60+accountWidth,(float)(20+accountHeight*0.6)+80,-90,-180,false,linePaint);

        Path path = new Path();
        path.moveTo(20+40,20+40+(float)(accountHeight*0.6));
        path.lineTo(20+accountWidth-40,20+40+(float)(accountHeight*0.6));
        PathEffect effects = new DashPathEffect(new float[] {1,2,4,8}, 1);
        linePaint.setPathEffect(effects);
        canvas.drawPath(path,linePaint);

        canvas.drawLine(20,(float)(20+accountHeight*0.6+80),20,(float)(20+accountHeight*0.9+80),linePaint);
        canvas.drawLine(20+accountWidth,(float)(20+accountHeight*0.6)+80,20+accountWidth,(float)(20+accountHeight*0.9)+80,linePaint);
        canvas.drawLine(20,(float)(20+accountHeight*0.9)+80,20+accountWidth,(float)(20+accountHeight*0.9)+80,linePaint);

        linePaint.setColor(Color.WHITE);
        linePaint.setTextSize((float)(accountHeight*0.06));
        canvas.drawText(title,60,(float)(20+accountHeight*0.12),linePaint);

        linePaint.setColor(Color.GRAY);
        linePaint.setTextSize((float)(accountHeight*0.05));
        canvas.drawText("金额",20+accountWidth/2-linePaint.measureText("金额")/2,(float)(20+accountHeight*0.35),linePaint);

        linePaint.setColor(costColor);
        linePaint.setTextSize((float)(accountHeight*0.1));
        canvas.drawText("$"+cost,20+accountWidth/2-linePaint.measureText(cost+"$")/2,(float)(20+accountHeight*0.5),linePaint);

        linePaint.setColor(Color.GRAY);
        linePaint.setTextSize((float)(accountHeight*0.065));
        canvas.drawText("交易信息",20+(float)(accountHeight*0.17),(float)(20+accountHeight*0.7)+80,linePaint);

        linePaint.setTextSize((float)(accountHeight*0.065));
        canvas.drawText("时间日期",20+(float)(accountHeight*0.17),(float)(20+accountHeight*0.8)+80,linePaint);

        linePaint.setColor(Color.BLACK);
        linePaint.setTextSize((float)(accountHeight*0.065));
        canvas.drawText(information,20+accountWidth/2,(float)(20+accountHeight*0.7)+80,linePaint);

        linePaint.setTextSize((float)(accountHeight*0.065));
        canvas.drawText(time,20+accountWidth/2,(float)(20+accountHeight*0.8)+80,linePaint);

    }
}
