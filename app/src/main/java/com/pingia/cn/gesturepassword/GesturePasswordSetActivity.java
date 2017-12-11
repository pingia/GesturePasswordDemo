package com.pingia.cn.gesturepassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.pingia.cn.gesturepassword.lib.NineCircularGridLayout;
import com.pingia.cn.gesturepassword.lib.NineCircularLittleGridLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * author：admin on 2017/9/26.
 * mail:pingia@163.com
 * function:    手势密码设置界面demo
 */
public class GesturePasswordSetActivity extends AppCompatActivity implements
        NineCircularGridLayout.OnSelectListener{

    @BindView(R.id.set_gesture_tip_tv)
    TextView setGestureTipTv;
    @BindView(R.id.nine_grid_layout)
    NineCircularGridLayout nineGridLayout;
    @BindView(R.id.set_gesture_agin_tv)
    TextView setGestureAginTv;

    @BindView(R.id.nine_grid_little_layout)
    NineCircularLittleGridLayout nineGridLittleLayout;

    private String mPreviousPathItemIds;
    private String mFirstOkPathItemIds;

    private Unbinder unbinder;

   public void onCreate(Bundle savedBundleInstance){
       super.onCreate(savedBundleInstance);
       setContentView(R.layout.gesture_password_set);
       ButterKnife.bind(this);
       init();
   }

    public void onDestroy(){
        super.onDestroy();

        if(unbinder != null){
            unbinder.unbind();
        }

        unbinder = null;
    }

    private void init(){
        setGestureAginTv.setVisibility(View.GONE);
        nineGridLayout.setOnSelectListener(this);
    }

    @OnClick(R.id.set_gesture_agin_tv)
    public void onViewClicked() {
        //重新设置手势，清除掉原有界面上的小图手势，还原提示文字，隐藏重新设置手势按钮

        //first step 清除小图手势

        //second step 还原提示文字
        setGestureTipTv.setText("绘制手势密码");
        setGestureTipTv.setTextColor(ContextCompat.getColor(this, R.color.light_gray));

        setGestureAginTv.setVisibility(View.GONE);

        //清除引导图的手势指引
        nineGridLittleLayout.reset();
        //清除上次的路径
        mPreviousPathItemIds = null;
    }

    @Override
    public boolean onOkSelect(String pathItemIds, List<Integer> pathItemIdList) {
        boolean delayed = false;
        if(!TextUtils.isEmpty(mPreviousPathItemIds)){
            if(pathItemIds.equals(mFirstOkPathItemIds)){
                ToastUtil.showToast(this, "手势设置成功");
                forwardToLogin();
            }else {
                if (!pathItemIds.equals(mPreviousPathItemIds)) {
                    //如果当前手势和上一次手势路径不一致，那么提示用户两次手势密码不一致,并在屏幕底部显示重新绘制手势的文字
                    setGestureTipTv.setText("与上次绘制不一致，请重新绘制");
                    setGestureTipTv.setTextColor(ContextCompat.getColor(this, R.color.light_orange));

                    nineGridLayout.notifyErrorDelayed();
                    setGestureAginTv.setVisibility(View.VISIBLE);

                    delayed = true;
                }
            }
        }else {
            //第一次成功设置手势，此时提示再次确认手势密码
            setGestureTipTv.setText("再次确认手势密码");
            setGestureTipTv.setTextColor(ContextCompat.getColor(this, R.color.light_gray));

            nineGridLittleLayout.setSelectedPathItemIdList(pathItemIdList);

            mFirstOkPathItemIds = pathItemIds;
        }
        mPreviousPathItemIds = pathItemIds;

        return delayed;
    }

    @Override
    public void onLessSelectMin(String pathItemIds, int min) {
        //提示至少链接四个点
        setGestureTipTv.setText("至少链接四个点，请重新绘制");
        setGestureTipTv.setTextColor(ContextCompat.getColor(this,R.color.light_orange));

    }

    private void forwardToLogin(){
        //跳转你自己的登录界面
        startActivity(new Intent(this, LoginActivity.class));
    }
}
