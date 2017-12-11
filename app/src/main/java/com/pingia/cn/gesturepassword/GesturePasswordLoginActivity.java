package com.pingia.cn.gesturepassword;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pingia.cn.gesturepassword.lib.NineCircularGridLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * author：admin on 2017/9/26.
 * mail:pingia@163.com
 * function: 手势密码登录界面demo
 */
public class GesturePasswordLoginActivity extends AppCompatActivity implements
        NineCircularGridLayout.OnSelectListener {

    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.error_tv)
    TextView errorTv;
    @BindView(R.id.nine_grid_layout)
    NineCircularGridLayout nineGridLayout;
    @BindView(R.id.gesture_pwd_forget_tv)
    TextView gesturePwdForgetTv;
    @BindView(R.id.other_account_login_tv)
    TextView otherAccountLoginTv;

    private int mCanTryTimes = MAX_TRY_TIMES_LIMIT;
    private static final int MAX_TRY_TIMES_LIMIT = 5;

    private Unbinder unbinder;

    public void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.gesture_password_login);
        unbinder = ButterKnife.bind(this);
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
        errorTv.setVisibility(View.INVISIBLE);

        String maskPhoneNo = "欢迎您: 138****4567";
        if(!TextUtils.isEmpty(maskPhoneNo)){
            userNameTv.setText(maskPhoneNo);
        }

        nineGridLayout.setOnSelectListener(this);
        nineGridLayout.setOnLayoutParamsListener(new NineCircularGridLayout.OnLayoutParamsListener() {
            @Override
            public void onLayoutMargin(int marginHorizontal, int marginVertical) {
                if(marginHorizontal == 0 && marginVertical != 0){
                    RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams)gesturePwdForgetTv.getLayoutParams();
                    lp1.topMargin = marginVertical;
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams)otherAccountLoginTv.getLayoutParams();
                    lp2.bottomMargin = marginVertical;
                }else if(marginHorizontal !=0 && marginVertical == 0){
                    RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams)gesturePwdForgetTv.getLayoutParams();
                    lp1.leftMargin = marginHorizontal;
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams)otherAccountLoginTv.getLayoutParams();
                    lp2.rightMargin = marginHorizontal;
                }

                nineGridLayout.setOnLayoutParamsListener(null);
            }
        });
    }

    @OnClick(R.id.gesture_pwd_forget_tv)
    public void onGesturePwdForgetTvClicked() {
        //用户选择“忘记手势密码”，先清除可能缓存的手势密码，然后跳转普通登录界面。
        clearCachedGesturePwd();
        forwardToLogin();
    }

    @OnClick(R.id.other_account_login_tv)
    public void onOtherAccountLoginTvClicked() {
        //用户选择“用其他账号登录”，跳转普通登录界面。
        forwardToLogin();
    }

    private void clearCachedGesturePwd(){
        //删除当前用户可能缓存在手机里的手势密码
    }

    @Override
    public boolean onOkSelect(String pathItemIds, List<Integer> pathItemIdList) {
        //判断当前的手势密码顺序pathItemIds和本地的是否一致，或者是发起请求判断和网络的是否一致。
        if(true){       //默认不一致，展示手势密码错误多次的ui效果
            mCanTryTimes --;
            showErrorTip(mCanTryTimes);
            nineGridLayout.notifyErrorDelayed();
            return true;
        }else{
            forwardToLogin();
        }

        return false;
    }

    @Override
    public void onLessSelectMin(String pathItemIds, int min) {
        //如果手势连接的点不足最少连接点个数，则直接提示密码不正确
        mCanTryTimes --;
        showErrorTip(mCanTryTimes);

        nineGridLayout.notifyErrorDelayed();

    }

    private void showErrorTip(int leftTimes){
        if(leftTimes <=0){
            String dialogMsg = "您已输入错误超过" + MAX_TRY_TIMES_LIMIT + "次，请重新输入登录密码设置";
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage(dialogMsg).setPositiveButton("确定",
                            new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                forwardToLogin();
                            }
                    }).create();

            dialog.setCancelable(false);
            dialog.show();
        }else {
            errorTv.setVisibility(View.VISIBLE);
            errorTv.setText("密码错误，还可以输入" + leftTimes + "次");
        }
    }

    private void forwardToLogin(){
        //跳转你自己的登录界面
        startActivity(new Intent(this, LoginActivity.class));
    }
}
