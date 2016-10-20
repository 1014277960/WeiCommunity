package com.wulinpeng.weicommunity.unlogin.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.MainActivity;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wulinpeng
 * @datetime: 16/10/6 下午8:16
 * @description:
 */
public class UnloginActivity extends AppCompatActivity {

    private static final int HOME_FRAGMENT = 1;
    private static final int MESSAGE_FRAGMENT = 2;
    private static final int DISCOVERY_FRAGMENT = 3;
    private static final int PROFILE_FRAGMENT = 4;

    private int mCurrentIndex = 0;

    private FragmentManager mFragmentManager;

    private HomeFragment mHomeFragment;
    private MessageFragment mMessageFragment;
    private DiscoverFragment mDiscoverFragment;
    private ProfileFragment mProfileFragment;

    @BindView(R.id.rl_home)
    public RelativeLayout mHome;
    @BindView(R.id.rl_message)
    public RelativeLayout mMessage;
    @BindView(R.id.rl_discover)
    public RelativeLayout mDiscover;
    @BindView(R.id.rl_profile)
    public RelativeLayout mProfile;
    @BindView(R.id.img_add)
    public ImageView mAdd;

    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlogin_activity_layout);
        ButterKnife.bind(this);

        mFragmentManager = getSupportFragmentManager();
        selectFragment(HOME_FRAGMENT);
        setupListener();
        setupOAuth();
    }

    private void selectFragment(int index) {
        if (mCurrentIndex != index) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            hideFragment(mCurrentIndex);
            switch (index) {
                case HOME_FRAGMENT:
                    if (mHomeFragment == null) {
                        mHomeFragment = new HomeFragment();
                        transaction.add(R.id.content, mHomeFragment);
                    } else {
                        transaction.show(mHomeFragment);
                    }
                    mHome.setSelected(true);
                    break;
                case MESSAGE_FRAGMENT:
                    if (mMessageFragment == null) {
                        mMessageFragment = new MessageFragment();
                        transaction.add(R.id.content, mMessageFragment);
                    } else {
                        transaction.show(mMessageFragment);
                    }
                    mMessage.setSelected(true);
                    break;
                case DISCOVERY_FRAGMENT:
                    if (mDiscoverFragment == null) {
                        mDiscoverFragment = new DiscoverFragment();
                        transaction.add(R.id.content, mDiscoverFragment);
                    } else {
                        transaction.show(mDiscoverFragment);
                    }
                    mDiscover.setSelected(true);
                    break;
                case PROFILE_FRAGMENT:
                    if (mProfileFragment == null) {
                        mProfileFragment = new ProfileFragment();
                        transaction.add(R.id.content, mProfileFragment);
                    } else {
                        transaction.show(mProfileFragment);
                    }
                    mProfile.setSelected(true);
                    break;
            }
            transaction.commit();
            mCurrentIndex = index;
        }
    }

    private void hideFragment(int index) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (index) {
            case HOME_FRAGMENT:
                if (mHomeFragment != null) {
                    transaction.hide(mHomeFragment);
                }
                mHome.setSelected(false);
                break;
            case MESSAGE_FRAGMENT:
                if (mMessageFragment != null) {
                    transaction.hide(mMessageFragment);
                }
                mMessage.setSelected(false);
                break;
            case DISCOVERY_FRAGMENT:
                if (mDiscoverFragment != null) {
                    transaction.hide(mDiscoverFragment);
                }
                mDiscover.setSelected(false);
                break;
            case PROFILE_FRAGMENT:
                if (mProfileFragment != null) {
                    transaction.hide(mProfileFragment);
                }
                mProfile.setSelected(false);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void setupListener() {
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment(HOME_FRAGMENT);
            }
        });
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment(MESSAGE_FRAGMENT);
            }
        });
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UnloginActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        });
        mDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment(DISCOVERY_FRAGMENT);
            }
        });
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment(PROFILE_FRAGMENT);
            }
        });
    }

    private void setupOAuth() {
        AuthInfo info = new AuthInfo(this, Constans.APP_KEY, Constans.REDIRECT_URL, Constans.SCOPE);
        mSsoHandler = new SsoHandler(this, info);
    }

    public void openLogin(View v) {
        mSsoHandler.authorize(new AuthListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle bundle) {
            Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle);
            if (token.isSessionValid()) {
                AccessTokenKeeper.putAccessToken(UnloginActivity.this, token);
                startActivity(new Intent(UnloginActivity.this, MainActivity.class));
                finish();
            } else {
                Log.d("Debug", "session is not valid");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.d("Debug", e.toString());
        }

        @Override
        public void onCancel() {
            Log.d("Debug", "cancel");
        }
    }
}













