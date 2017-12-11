package com.pingia.cn.gesturepassword.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * author：pingia@163.com on 2017/9/29.
 * function: 手势小图实心圆
 */
public class NineCircularLittleView extends View {

    private int mNormalCircularColor;
    private int mSelectedFilledColor;
    private int mNormalPaintStrokeWidth;

    private int mCenterX;
    private int mCenterY;
    private int mCircularRadius;

    private Paint mPaint ;

    public static final int STATE_INIT = 0;
    public static final int STATE_SELECTED = 1;

    private int mSelectedState = STATE_INIT;


    public NineCircularLittleView(Context context) {
        this(context,null);
    }

    public NineCircularLittleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NineCircularLittleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }

    public void setNormalCircularColor(int normalCircularColor) {
        this.mNormalCircularColor = normalCircularColor;
    }

    public void setSelectedFilledColor(int selectedFilledColor) {
        this.mSelectedFilledColor = selectedFilledColor;
    }

    public void setNormalPaintStrokeWidth(int paintStrokeWidth) {
        this.mNormalPaintStrokeWidth = paintStrokeWidth;
    }


    public void setSelectState(int selectedState,boolean immediatelyRefresh){
        this.mSelectedState = selectedState;
        if(immediatelyRefresh){
            postInvalidate();
        }

    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width_mode = MeasureSpec.getMode(widthMeasureSpec);
        int height_mode = MeasureSpec.getMode(heightMeasureSpec);

        int constrained_width  = -1;
        int constrained_height = -1;

        if(width_mode == MeasureSpec.EXACTLY){
            constrained_width = MeasureSpec.getSize(widthMeasureSpec);
        }

        if(height_mode == MeasureSpec.EXACTLY){
            constrained_height = MeasureSpec.getSize(heightMeasureSpec);
        }

        if(constrained_width !=-1 && constrained_height !=-1){
            //对九宫格的每个元素指定宽高或使用match_parent时，计算
            int min = Math.min(constrained_width, constrained_height);

            int half_min = min/2;
            mCenterX = constrained_width/2;
            mCenterY = constrained_height/2;
            mCircularRadius = half_min- mNormalPaintStrokeWidth;

        }


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onDraw(Canvas canvas){

        if(mSelectedState == STATE_SELECTED){
            drawSelectedState(canvas);
        }else{
            drawNormalState(canvas);
        }
    }

    private void drawNormalState(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);        //画圆周
        mPaint.setStrokeWidth(mNormalPaintStrokeWidth);
        mPaint.setColor(mNormalCircularColor);

        canvas.drawCircle(mCenterX, mCenterY, mCircularRadius, mPaint);
    }

    private void drawSelectedState(Canvas canvas){
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSelectedFilledColor);

        canvas.drawCircle(mCenterX, mCenterY, mCircularRadius, mPaint);
    }
}
