package com.example.dn.accounting.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.dn.accounting.R;

/**
 * Created by dn on 2016/11/20.
 */

public class StatisticsView extends View {
    private String type;
    private String costText;
    private float cost = 0;
    private int color;
    private float length;
    private float rectWidth;
    private float width;
    private float height;
    private Paint mPaint;

    public void setCost(float cost) {
        this.cost = cost;
        costText = String.valueOf(cost);
    }

    public void setLength(float length) {
        this.length = length;
    }

    public StatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StatisticsView);
        type = ta.getString(R.styleable.StatisticsView_type);
        color = ta.getColor(R.styleable.StatisticsView_color,0);
        ta.recycle();
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        mPaint = new Paint();
        mPaint.setTextSize(45);
        mPaint.setColor(color);

        canvas.drawText(type,20,height-20,mPaint);

        rectWidth = length*(width-30-mPaint.measureText(type));
        canvas.drawRect(50+mPaint.measureText(type),20,rectWidth+30+mPaint.measureText(type),height,mPaint);

        if ((30+mPaint.measureText(type)+rectWidth)+mPaint.measureText(costText)<width){
            mPaint.setColor(Color.GRAY);
            canvas.drawText(costText,30+mPaint.measureText(type)+rectWidth,height-20,mPaint);
        } else {
            mPaint.setColor(Color.WHITE);
            canvas.drawText(costText,30+mPaint.measureText(type)-mPaint.measureText(costText)+rectWidth,height-20,mPaint);
        }
    }
}
