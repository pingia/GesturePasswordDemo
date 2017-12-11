package com.pingia.cn.gesturepassword.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * author：pingia@163.com on 2017/9/25.
 * function: 九宫格布局中的 一个圆圈
 */
public class NineCircularView extends View {
    private int mCenterX;
    private int mCenterY;
    private int mCircularRadius;
    private int mInnerCircularRadius;

    private int mNormalPaintStrokeWidth;
    private int mNormalCircularColor;
    public int mFingerOnColor;
    public int mErrorLinetoCircularColor;

    public static final int TOUCH_STATE_NORMAL = 0;
    public static final int TOUCH_STATE_FINGER_DOWN = 1;
    public static final int TOUCH_STATE_FINGER_MOVE = 2;
    public static final int TOUCH_STATE_FINGER_UP = 3;
    private int mTouchState = TOUCH_STATE_NORMAL;


    public static final int STATE_INIT = 0;
    public static final int ERROR_STATE = 1;
    private int mCheckState = STATE_INIT;

    private Paint mPaint ;

    public NineCircularView(Context context) {
        this(context,null);
    }

    public NineCircularView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NineCircularView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
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
            mInnerCircularRadius = mCircularRadius/4;

        }


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onDraw(Canvas canvas){
        if(mCheckState == ERROR_STATE){
            drawErrorState(canvas);
        }else{
            switch (mTouchState){
                case TOUCH_STATE_NORMAL:
                    drawNormalTouchState(canvas);
                    break;
                case TOUCH_STATE_FINGER_DOWN:
                    drawFingerDownTouchState(canvas);
                    break;
                case TOUCH_STATE_FINGER_MOVE:
                    drawFingerMoveTouchState(canvas);
                    break;
                case TOUCH_STATE_FINGER_UP:
                    drawFingerUpTouchState(canvas);
                    break;
            }
        }
    }


    public void setTouchState(int touchState,boolean immediatelyRefresh){
        this.mTouchState = touchState;
        if(immediatelyRefresh){
            invalidate();
        }
    }

    public void setState(int state, boolean immediatelyRefresh){
        mCheckState = state;

        if(immediatelyRefresh){
            invalidate();
        }
    }

    public void setNormalCircularColor(int normalCircularColor) {
        this.mNormalCircularColor = normalCircularColor;
    }

    public void setFingerOnColor(int fingerOnColor) {
        this.mFingerOnColor = fingerOnColor;
    }

    public void setErrorLinetoCircularColor(int errorLinetoCircularColor) {
        this.mErrorLinetoCircularColor = errorLinetoCircularColor;
    }

    public void setPaintStrokeWidth(int paintStrokeWidth) {
        this.mNormalPaintStrokeWidth = paintStrokeWidth;
    }

    private void drawNormalTouchState(Canvas canvas){
        mPaint.setStyle(Paint.Style.STROKE);        //画圆周
        mPaint.setStrokeWidth(mNormalPaintStrokeWidth);
        mPaint.setColor(mNormalCircularColor);

        canvas.drawCircle(mCenterX, mCenterY, mCircularRadius, mPaint);
    }

    private void drawFingerOnTouchState(Canvas canvas){
        mPaint.setStyle(Paint.Style.STROKE);        //画圆周
        mPaint.setStrokeWidth(mNormalPaintStrokeWidth);
        mPaint.setColor(mFingerOnColor);

        canvas.drawCircle(mCenterX, mCenterY, mCircularRadius, mPaint);

        //画内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mFingerOnColor);
        canvas.drawCircle(mCenterX,mCenterY,mInnerCircularRadius,mPaint);
    }

    private void drawFingerDownTouchState(Canvas canvas){
        //手指按下的时候不绘制
    }

    private void drawFingerMoveTouchState(Canvas canvas){
        drawFingerOnTouchState(canvas);
    }

    private void drawFingerUpTouchState(Canvas canvas){
        drawFingerOnTouchState(canvas);
    }

    private void drawErrorState(Canvas canvas){
        mPaint.setStyle(Paint.Style.STROKE);        //画圆周
        mPaint.setStrokeWidth(mNormalPaintStrokeWidth);
        mPaint.setColor(mErrorLinetoCircularColor);

        canvas.drawCircle(mCenterX, mCenterY, mCircularRadius, mPaint);

        //画内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mErrorLinetoCircularColor);
        canvas.drawCircle(mCenterX,mCenterY,mInnerCircularRadius,mPaint);
    }
}
