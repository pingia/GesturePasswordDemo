package com.pingia.cn.gesturepassword.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * author：pingia@163.com on 2017/9/25.
 * function:  九宫格布局
 *
 *
 *
 * 0   1    2     3
 * 4   5    6     7
 * 8   9    10    11
 *
 */
public class NineCircularGridLayout extends RelativeLayout {
    private static final String TAG = "NineCircularGridLayout";
    private Context mContext;

    private int mRowsCount = 3;
    private int mColsCount = 3;
    private int mMinLineToNums = 4;

    private float mEventDownX;      //第一次按下的坐标点
    private float mEventDownY;

    private float mLastLinePathX;
    private float mLastLinePathY;

    private float mFingerMoveX;
    private float mFingerMoveY;

    private int mViewCircularColor;
    private int mViewFingerOnColor;
    private int mFingerLineToColor;
    public int mErrorLinetoColor;
    private int mNormalCircularStrokeWidth;
    private int mLineToPathStrokeWidth;

    // TODO: 2017/9/29 参数配置待优化。考虑从xml中读取属性
    private Path circularViewLinePath;
    private Paint mLinePathPaint;
    private Path linepath2;
    private Paint linepathpaint2;

    private NineCircularView[] mCircularViews;
    private ArrayList<Integer> pathItemIdList = new ArrayList<>();


    private OnSelectListener mSelectedListener;
    private CheckStateEnum mCheckState = CheckStateEnum.STATE_INIT;

    private OnLayoutParamsListener mLayoutParamsListener;

    enum CheckStateEnum{
        STATE_INIT,
        STATE_NOT_CORRECT //手势密码不正确，可能是设置手势密码时两次设置的不一致，也有可能是手势密码登录时校验不通过
    }

    public interface OnSelectListener{
        boolean onOkSelect(String pathItemIds, List<Integer> pathItemIdList);
        void onLessSelectMin(String pathItemIds, int min);  //不满足最少连线个数的时候触发这个方法
    }

    public interface  OnLayoutParamsListener{
        void onLayoutMargin(int marginHorizontal, int marginVertical);
    }

    public NineCircularGridLayout(Context context) {
        this(context,null);
    }

    public NineCircularGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NineCircularGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setOnSelectListener(OnSelectListener listener){
        this.mSelectedListener = listener;
    }

    public void setOnLayoutParamsListener(OnLayoutParamsListener listener){
        this.mLayoutParamsListener = listener;
    }

    private void initViews(){
        mCircularViews = new NineCircularView[mRowsCount * mColsCount];
        int len = mCircularViews.length;
        for (int i =0 ;i <len; i++){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            NineCircularView circularView = new NineCircularView(this.getContext());
            circularView.setLayoutParams(lp);
            circularView.setId(i+1);    //注意id千万不能设置为0，否则无法绘制
            circularView.setTouchState(NineCircularView.TOUCH_STATE_NORMAL,false);
            circularView.setNormalCircularColor(mViewCircularColor);
            circularView.setFingerOnColor(mViewFingerOnColor);
            circularView.setErrorLinetoCircularColor(mErrorLinetoColor);
            circularView.setPaintStrokeWidth(mNormalCircularStrokeWidth);

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

    private void init(AttributeSet attrs){
        mContext = getContext();
        initValueAttrs(attrs);

        mLinePathPaint = new Paint();
        mLinePathPaint.setDither(true);
        mLinePathPaint.setAntiAlias(true);
        mLinePathPaint.setStyle(Paint.Style.STROKE);
        mLinePathPaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePathPaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePathPaint.setStrokeWidth(mLineToPathStrokeWidth);

        linepathpaint2 = new Paint();
        linepathpaint2.setDither(true);
        linepathpaint2.setAntiAlias(true);
        linepathpaint2.setStyle(Paint.Style.STROKE);
        linepathpaint2.setStrokeCap(Paint.Cap.ROUND);
        linepathpaint2.setStrokeJoin(Paint.Join.ROUND);
        linepathpaint2.setStrokeWidth(mLineToPathStrokeWidth);

        circularViewLinePath = new Path();
        circularViewLinePath.reset();

        linepath2 = new Path();
        linepath2.reset();

        initViews();
    }

    private void initValueAttrs(AttributeSet attrs){
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.GesturePasswordView);
        mViewCircularColor = array.getColor(R.styleable.GesturePasswordView_normalColor,
                ContextCompat.getColor(mContext, R.color.default_normal_color));
        mViewFingerOnColor = array.getColor(R.styleable.GesturePasswordView_fingerOnColor,
                ContextCompat.getColor(mContext, R.color.default_finger_on_color));
        mFingerLineToColor = array.getColor(R.styleable.GesturePasswordView_fingerLineToColor,
                ContextCompat.getColor(mContext, R.color.default_finger_lineto_color));
        mErrorLinetoColor = array.getColor(R.styleable.GesturePasswordView_wrongLineToColor,
                ContextCompat.getColor(mContext, R.color.default_wrong_lineto_color));

        mNormalCircularStrokeWidth = array.getDimensionPixelSize(R.styleable.GesturePasswordView_normalCircularLineWidth, 6);
        mLineToPathStrokeWidth = array.getDimensionPixelSize(R.styleable.GesturePasswordView_fingerLineToWidth, 10);
        int minLineToNum = array.getDimensionPixelSize(R.styleable.GesturePasswordView_minFingerLineToNums, 4);

        setMinLineToCircularNumber(minLineToNum);

    }

    private void setCheckState(CheckStateEnum checkState){
        this.mCheckState = checkState;
        postInvalidate();
    }

    public void setMinLineToCircularNumber(int min){
        if(min <= mRowsCount * mColsCount){
            this.mMinLineToNums = min;
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

            int horizontal_margin = (dimension - totalWidth)/2 ;
            int vertical_margin = 0;
            if(mLayoutParamsListener != null){
                mLayoutParamsListener.onLayoutMargin(horizontal_margin,vertical_margin );
            }
        }else{
            setMeasuredDimension(totalWidth ,dimension );

            int horizontal_margin = 0;
            int vertical_margin = (dimension- totalHeight)/2;
            if(mLayoutParamsListener != null){
                mLayoutParamsListener.onLayoutMargin(horizontal_margin, vertical_margin);
            }
        }

        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(newWidthMeasureSpec,newHeightMeasureSpec);
    }

    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        float eventX = event.getX();
        float eventY = event.getY();

        NineCircularView view = getChildByPosition(eventX,eventY);

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mEventDownX = eventX;
                mEventDownY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = eventX - mEventDownX;
                float moveY = eventY  - mEventDownY;

                if(null != view) {

                    Log.d(TAG, "id: " + view.getId() + "; moveX:" + moveX + "; moveY=" + moveY + "; fineron:");
                    if(!pathItemIdList.contains(view.getId())) {
                        linepath2.reset();
                        pathItemIdList.add(view.getId());
                        float centerX = view.getLeft() / 2 + view.getRight() / 2;
                        float centerY = view.getTop() / 2 + view.getBottom() / 2;

                        if(pathItemIdList.size() <= 1){
                            view.setTouchState(NineCircularView.TOUCH_STATE_FINGER_MOVE, true);
                            circularViewLinePath.moveTo(centerX, centerY);
                        }else {
                            view.setTouchState(NineCircularView.TOUCH_STATE_FINGER_MOVE, true);
                            circularViewLinePath.lineTo(centerX, centerY);
                        }

                        mLastLinePathX = centerX;
                        mLastLinePathY = centerY;
                    }




                }else{
                    linepath2.reset();
                    if(pathItemIdList.size() >= 1) {

                        linepath2.moveTo(mLastLinePathX, mLastLinePathY);
                        linepath2.lineTo(eventX, eventY);
                        postInvalidate();
                    }
                }

                mFingerMoveX = eventX;
                mFingerMoveY = eventY;
                break;
            case MotionEvent.ACTION_UP:
                if(pathItemIdList.size() <=1){
                    //单个圆圈的点击
                    if(null != view) {
                        view.setTouchState(NineCircularView.TOUCH_STATE_FINGER_UP, true);
                        if(null != mSelectedListener){
                            mSelectedListener.onLessSelectMin(StringUtils.joinListElementsToString(pathItemIdList), mMinLineToNums);
                        }

                            resetDelayed();

                    }else{
                        reset();
                    }
                }else {

                    if(pathItemIdList.size() >=mMinLineToNums){     //满足最少点击个数的手势绘制
                        boolean delayed = false;
                        if(null != mSelectedListener){
                            delayed = mSelectedListener.onOkSelect(StringUtils.joinListElementsToString(pathItemIdList), pathItemIdList);
                        }

                        if(delayed){
                            resetDelayed();
                        }else {
                            reset();
                        }
                    }else {                 //不符合最少点击个数的绘制
                        if(null != mSelectedListener){
                            mSelectedListener.onLessSelectMin(StringUtils.joinListElementsToString(pathItemIdList), mMinLineToNums);
                        }


                         resetDelayed();



                    }
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                reset();
                break;

        }

        invalidate();

        return true;
    }


    public void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);

        if(circularViewLinePath != null){
            if(mCheckState == CheckStateEnum.STATE_NOT_CORRECT) {
                mLinePathPaint.setColor(mErrorLinetoColor);
            }else{
                mLinePathPaint.setColor(mFingerLineToColor);
            }
            canvas.drawPath(circularViewLinePath, mLinePathPaint);
        }

        if(linepath2 != null){
            if(mCheckState == CheckStateEnum.STATE_NOT_CORRECT) {
                linepathpaint2.setColor(mErrorLinetoColor);
            }else {
                linepathpaint2.setColor(mFingerLineToColor);
            }
            canvas.drawPath(linepath2, linepathpaint2);
        }

    }

    /**
     * 通过坐标点找到当前坐标点在哪个九宫格圆圈范围内
     * @param x
     * @param y
     * @return
     */
    private NineCircularView getChildByPosition(float x, float y){
        if(null == mCircularViews) return null;
        for (NineCircularView view : mCircularViews){
            if(checkPositionInViewBounds(view, x, y)){
                return view;
            }
        }

        return null;
    }

    /**
     * 判断坐标点是否在指定的view范围内
     * @param view
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInViewBounds(NineCircularView view, float x, float y){
        float padding = view.getMeasuredWidth() * 0.1f;

        float left = view.getLeft() + padding;
        float right = view.getRight() - padding;
        float top = view.getTop() + padding;
        float bottom = view.getBottom() - padding;

        RectF rect = new RectF(left,top,right,bottom);

        return rect.contains(x, y);
    }

    public void notifyErrorDelayed(){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyError();
            }
        },150);
    }

    private void notifyError(){
        setCheckState(CheckStateEnum.STATE_NOT_CORRECT);

        for (NineCircularView itemView :mCircularViews ){
            if(pathItemIdList.contains(itemView.getId())) {
                itemView.setState(NineCircularView.ERROR_STATE, true);
            }
        }
    }

    private void resetDelayed(){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        },300);         //延迟200s后再清除绘制
    }

    private void reset(){
        //不绘制连接线
        pathItemIdList.clear();
        if(circularViewLinePath != null){
            circularViewLinePath.reset();
            invalidate();
        }

        if(linepath2 != null){
            linepath2.reset();
            invalidate();
        }

        //重置验证状态为初始态
        setCheckState(CheckStateEnum.STATE_INIT);

        //重置圆圈为未按下状态
        for (NineCircularView view : mCircularViews) {
            view.setState(NineCircularView.STATE_INIT, true);
            view.setTouchState(NineCircularView.TOUCH_STATE_NORMAL, true);
        }
    }
}
