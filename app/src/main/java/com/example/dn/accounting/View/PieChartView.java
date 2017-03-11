package com.example.dn.accounting.View;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.dn.accounting.Model.TagInformation;
import com.example.dn.accounting.R;
import com.example.dn.accounting.SortByPrice;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dn on 2017/2/27.
 */

public class PieChartView extends View {
    private Paint mPaint = new Paint();
    private float mAllCost = 0;
    private ArrayList<TagInformation> mTagInformations;
    private int[] color = {getResources().getColor(R.color.tagColor0), getResources().getColor(R.color.tagColor6),
            getResources().getColor(R.color.tagColor2), getResources().getColor(R.color.tagColor3),
            getResources().getColor(R.color.tagColor1), getResources().getColor(R.color.tagColor5),
            getResources().getColor(R.color.tagColor4), getResources().getColor(R.color.tagColor7),
            getResources().getColor(R.color.tagColor8), getResources().getColor(R.color.tagColor9),
            getResources().getColor(R.color.tagColor4)};
    private float[] angle;
    private float mLastAngle;
    private float mCurrentAngle;
    private int length;
    private float startX[];
    private float startY[];
    private float endX[];
    private float endY[];
    public static final double PI = 3.14;
    private String tagName[];
    private String text = null;

    public PieChartView(Context context) {
        super(context);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PieChartView);
        text = ta.getString(R.styleable.PieChartView_pietext);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        length = getMeasuredWidth() / 16;
        mLastAngle = 0;
        for (int i = 0; i < mTagInformations.size(); i++){
            TagInformation tagInformation = mTagInformations.get(i);
            tagName[i] = tagInformation.getTagName();
            float proportion = tagInformation.getTagCost() / mAllCost;
            mCurrentAngle = 360 * proportion;
            mPaint.setColor(color[i]);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(150, 150, getMeasuredWidth()-150, getMeasuredWidth()-150, -90, mCurrentAngle, true, mPaint);
            canvas.rotate(mCurrentAngle, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
            canvas.save();
            canvas.restore();
            if (i == 0){
                angle[i] = mCurrentAngle / 2;
            } else {
                angle[i] = angle[i - 1] + (mCurrentAngle + mLastAngle) / 2;
            }
            mLastAngle = mCurrentAngle;
        }
        caculatePosition();
        mPaint.setStrokeWidth(5);
        mPaint.setTextSize(50);
        for (int i = 0; i < mTagInformations.size(); i++){
            mPaint.setColor(color[i]);
            canvas.drawLine(startX[i], startY[i], endX[i], endY[i], mPaint);
            if (endY[i] <= getMeasuredHeight() / 2){
                canvas.drawText(tagName[i], endX[i] - mPaint.measureText(tagName[i])/2, endY[i], mPaint);
            } else {
                canvas.drawText(tagName[i],endX[i] - mPaint.measureText(tagName[i])/2, endY[i] + 15, mPaint);
            }
        }
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, getMeasuredWidth()/4 - 75, mPaint);

        if (text != null){
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(80);
            canvas.drawText(text, getMeasuredWidth()/2 - mPaint.measureText(text)/2, getMeasuredHeight()/2, mPaint);
            canvas.drawText(String.format("%.1f", mAllCost), getMeasuredWidth()/2 - mPaint.measureText(String.format("%.1f", mAllCost))/2, getMeasuredHeight()/2 + mPaint.measureText("ab"), mPaint);
        }


    }

    private void caculatePosition(){
        for (int i = 0; i < mTagInformations.size(); i++){
            startX[i] = (float)(getMeasuredWidth() / 2 + (getMeasuredWidth() / 2 - 150)* Math.cos((double)(-(angle[i] - 90))/180 * PI));
            startY[i] = (float)(getMeasuredHeight() / 2 - (getMeasuredWidth() / 2 - 150) * Math.sin((double) (-(angle[i] - 90))/180 * PI));
            endX[i] = (float)(getMeasuredWidth() / 2 + ((getMeasuredWidth() / 2 - 150) + length) * Math.cos((double)(-(angle[i] - 90))/180 * PI));
            endY[i] = (float)(getMeasuredWidth() / 2 - ((getMeasuredWidth() / 2 - 150) + length) * Math.sin((double)(-(angle[i] - 90))/180 * PI));
        }
    }

    public void setTagInformation(ArrayList<TagInformation> tagInformations){
        Collections.sort(tagInformations, new SortByPrice());
        this.mTagInformations = tagInformations;
        mAllCost = 0;
        for (TagInformation tagInformation : mTagInformations){
            mAllCost += tagInformation.getTagCost();
        }
        int tagsSize = tagInformations.size();
        angle = new float[tagsSize];
        startX = new float[tagsSize];
        startY = new float[tagsSize];
        endX = new float[tagsSize];
        endY = new float[tagsSize];
        tagName = new String[tagsSize];
    }
}
