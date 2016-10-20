package com.wulinpeng.weicommunity.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.MainActivity;
import com.wulinpeng.weicommunity.unlogin.view.UnloginActivity;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;

/**
 * @author wulinpeng
 * @datetime: 16/10/7 下午5:21
 * @description:
 */
public class SplashActivity extends AppCompatActivity {

    private Intent intent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Oauth2AccessToken token = AccessTokenKeeper.getAccessToken(this);
        if (token.isSessionValid()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            Log.d("Debug", token.getToken());
        } else {
            intent = new Intent(SplashActivity.this, UnloginActivity.class);
        }
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public void onBackPressed() {
        return;
    }
}
