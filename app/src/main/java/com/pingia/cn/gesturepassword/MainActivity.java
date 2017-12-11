package com.pingia.cn.gesturepassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * author：admin on 2017/9/26.
 * mail:pingia@163.com
 * function: 手势密码demo界面
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.gesturePwdSetBtn)
    Button mGesturePwdSetBtn;

    @BindView(R.id.gesturePwdLoginBtn)
    Button mGesturePwdLoginBtn;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public void onDestroy(){
        super.onDestroy();

        if(unbinder != null){
            unbinder.unbind();
        }

        unbinder = null;
    }

    @OnClick({R.id.gesturePwdSetBtn, R.id.gesturePwdLoginBtn})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.gesturePwdSetBtn:
                startActivity(new Intent(this, GesturePasswordSetActivity.class));
                break;
            case R.id.gesturePwdLoginBtn:
                startActivity(new Intent(this, GesturePasswordLoginActivity.class));
                break;
        }
    }
}
