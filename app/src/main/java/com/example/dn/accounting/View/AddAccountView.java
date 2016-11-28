package com.example.dn.accounting.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.dn.accounting.R;

/**
 * Created by dn on 2016/11/14.
 */

public class AddAccountView extends View {

    private String text;
    private String detailText;
    private int textBackground;
    private float width;
    private float height;
    private Paint mPaint;

    public AddAccountView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getPropertiesFromXml(context,attrs);

    }

    private void getPropertiesFromXml(Context context,AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AddAccountView);
        text = ta.getString(R.styleable.AddAccountView_text);
        detailText = ta.getString(R.styleable.AddAccountView_detailtext);
        textBackground = ta.getColor(R.styleable.AddAccountView_textbackground, getResources().getColor(R.color.costColor));
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getWidth();
        height = getHeight();

        mPaint = new Paint();

        mPaint.setColor(textBackground);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width/2,(float)(height*0.4),(float)(height*0.15),mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize((float)(height*0.15));
        canvas.drawText(text,width/2-mPaint.measureText(text)/2,(float)(height*0.39)+mPaint.measureText(text)/2,mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize((float)(height*0.07));
        canvas.drawText(detailText,width/2-mPaint.measureText(detailText)/2,(float)(height*0.55)+mPaint.measureText(detailText)/2,mPaint);
    }
}
