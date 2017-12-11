package com.pingia.cn.gesturepassword.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * author：pingia@163.com on 2017/9/29.
 * function:手势小图，作为用户正常设置的手势序列的回显
 */
public class NineCircularLittleGridLayout extends RelativeLayout {
    private Context mContext;

    private int mRowsCount = 3;
    private int mColsCount = 3;

    private int mLineToColor;
    private int mLittleViewNormalCircularColor;
    private int mLittleViewSelectFilledColor;
    private int mLittleViewNormalStrokeWidth;


    private Path circularViewLinePath;
    private Paint mLinePathPaint;

    private NineCircularLittleView[] mCircularViews;

    public NineCircularLittleGridLayout(Context context) {
        this(context,null);
    }

    public NineCircularLittleGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NineCircularLittleGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        mContext = this.getContext();
        initValueAttrs(attrs);

        mLinePathPaint = new Paint();
        mLinePathPaint.setDither(true);
        mLinePathPaint.setAntiAlias(true);
        mLinePathPaint.setStyle(Paint.Style.STROKE);
        mLinePathPaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePathPaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePathPaint.setStrokeWidth(mLittleViewNormalStrokeWidth);
        circularViewLinePath = new Path();
        circularViewLinePath.reset();

        initViews();
    }

    private void initValueAttrs(AttributeSet attrs){

        TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.GesturePasswordView);
        mLittleViewNormalCircularColor = array.getColor(R.styleable.GesturePasswordView_little_normalColor,
                ContextCompat.getColor(mContext, R.color.default_little_normal_color));
        mLittleViewSelectFilledColor = array.getColor(R.styleable.GesturePasswordView_little_fillColor,
                ContextCompat.getColor(mContext, R.color.default_little_fill_color));
        mLineToColor = array.getColor(R.styleable.GesturePasswordView_little_LineToColor,
                ContextCompat.getColor(mContext, R.color.default_little_lineto_color));

        mLittleViewNormalStrokeWidth = array.getDimensionPixelSize(R.styleable.GesturePasswordView_little_normalCircularLineWidth, 6);

        array.recycle();
    }

    private void initViews(){
        mCircularViews = new NineCircularLittleView[mRowsCount * mColsCount];
        int len = mCircularViews.length;
        for (int i =0 ;i <len; i++){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            NineCircularLittleView circularView = new NineCircularLittleView(this.getContext());
            circularView.setLayoutParams(lp);
            circularView.setId(i+1);    //注意id千万不能设置为0，否则无法绘制

            circularView.setNormalCircularColor(mLittleViewNormalCircularColor);
            circularView.setSelectedFilledColor(mLittleViewSelectFilledColor);
            circularView.setNormalPaintStrokeWidth(mLittleViewNormalStrokeWidth);

            int col = i % mColsCount;
            int row = i / mColsCount;
            if(col != 0){
                lp.addRule(RelativeLayout.RIGHT_OF, mCircularViews[i -1].getId() );
            }

            if(row != 0){
                lp.addRule(RelativeLayout.BELOW, mCircularViews[i - mColsCount].getId());
            }


            mCircularViews[i]   = circularView;

            addView(circularView);
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

        int dimension = -1;
        if(constrained_width ==- 1 && constrained_height !=-1){
            dimension = constrained_height;
        }else if(constrained_width != -1 && constrained_height == -1){
            dimension = constrained_width;
        }else if(constrained_width !=-1 ){
            dimension = Math.min(constrained_width, constrained_height);
        }

        int len = mCircularViews.length;
        int totalHeight = 0;
        int totalWidth = 0;

        int itemWidth = (int)(dimension / (0.6f * (mColsCount +1) + mColsCount));
        int itemHeight = (int)(dimension /(0.6f * (mRowsCount+1) + mRowsCount));

        int item_margin_horizontal =  (int)(itemWidth * 0.6f);
        int item_margin_vertical =  (int)(itemHeight * 0.6f);

        for (int i =0; i<len;i++ ){
            RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) mCircularViews[i].getLayoutParams();
            lp.width = itemWidth;
            lp.height = itemHeight;

            int col = i % mColsCount;
            int row = i / mColsCount;

            lp.leftMargin =item_margin_horizontal;

            if(row != 0){
                lp.topMargin =item_margin_vertical;
            }

            if(row == 0) {
                totalWidth += lp.width;

                if(col !=0) {
                    totalWidth += lp.leftMargin;
                }
            }

            if(col == 0) {
                totalHeight += lp.height;

                if(row!=0) {
                    totalHeight += lp.topMargin;
                }
            }
        }

        if(dimension == constrained_width){
            setMeasuredDimension(dimension, totalHeight );
        }else{
            setMeasuredDimension(totalWidth ,dimension );
        }

        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(newWidthMeasureSpec,newHeightMeasureSpec);
    }

    public void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);

        if(circularViewLinePath != null){
            mLinePathPaint.setColor(mLineToColor);
            canvas.drawPath(circularViewLinePath, mLinePathPaint);
        }

    }

    public void setSelectedPathItemIdList(List<Integer> pathItemIdList){
        int size = pathItemIdList.size();
        for (int i =0; i<size;i++){
            NineCircularLittleView view = mCircularViews[pathItemIdList.get(i)-1];

            int centerX  = view.getLeft() / 2 + view.getRight() / 2;
            int centerY = view.getTop() / 2 + view.getBottom() / 2;

            if(i == 0){
                circularViewLinePath.moveTo(centerX,centerY);
            }else{
                circularViewLinePath.lineTo(centerX, centerY);
            }

            view.setSelectState(NineCircularLittleView.STATE_SELECTED,true);
        }

        postInvalidate();
    }

    public void reset(){
        if(circularViewLinePath != null){
            circularViewLinePath.reset();
            invalidate();
        }

        //重置圆圈为未按下状态
        for (NineCircularLittleView view : mCircularViews) {
            view.setSelectState(NineCircularLittleView.STATE_INIT, true);
        }
    }

}
