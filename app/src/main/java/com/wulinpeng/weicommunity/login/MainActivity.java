package com.wulinpeng.weicommunity.login;

import android.animation.ObjectAnimator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.discover.view.DiscoverFragment;
import com.wulinpeng.weicommunity.login.home.event.BarStatusEvent;
import com.wulinpeng.weicommunity.login.home.view.HomeFragment;
import com.wulinpeng.weicommunity.login.message.view.MessageFragment;
import com.wulinpeng.weicommunity.login.profile.view.ProfileFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /**
     * 使用String是为了为Fragment添加tag,在回收复原后得到Fragment
     */
    private static final String HOME_FRAGMENT = "home_fragment";
    private static final String MESSAGE_FRAGMENT = "message_fragment";
    private static final String DISCOVERY_FRAGMENT = "discover_Fragment";
    private static final String PROFILE_FRAGMENT = "profile_fragment";

    private String mCurrentFragment = "";

    private FragmentManager mFragmentManager;

    private HomeFragment mHomeFragment;
    private MessageFragment mMessageFragment;
    private DiscoverFragment mDiscoverFragment;
    private ProfileFragment mProfileFragment;

    @BindView(R.id.bottom_bar)
    public LinearLayout mBottomBar;
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

    private boolean isBottomBarShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mFragmentManager = getSupportFragmentManager();
        // crash后恢复
        if (savedInstanceState != null) {
            restoreFragment(savedInstanceState);
        } else {
            selectFragment(HOME_FRAGMENT);
        }
        setupListener();
    }

    /**
     * crash中恢复当前操作
     * @param s
     */
    private void restoreFragment(Bundle s) {
        mCurrentFragment = s.getString("fragment");
        mHomeFragment = (HomeFragment) mFragmentManager.findFragmentByTag(HOME_FRAGMENT);
        mMessageFragment = (MessageFragment) mFragmentManager.findFragmentByTag(MESSAGE_FRAGMENT);
        mDiscoverFragment = (DiscoverFragment) mFragmentManager.findFragmentByTag(DISCOVERY_FRAGMENT);
        mProfileFragment = (ProfileFragment) mFragmentManager.findFragmentByTag(PROFILE_FRAGMENT);
        selectFragment(mCurrentFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("fragment", mCurrentFragment);
        super.onSaveInstanceState(outState);
    }

    private void selectFragment(String fragment) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            hideFragment(mCurrentFragment);
            switch (fragment) {
                case HOME_FRAGMENT:
                    if (mHomeFragment == null) {
                        mHomeFragment = new HomeFragment();
                        transaction.add(R.id.content, mHomeFragment, HOME_FRAGMENT);
                    } else {
                        transaction.show(mHomeFragment);
                    }
                    mHome.setSelected(true);
                    break;
                case MESSAGE_FRAGMENT:
                    if (mMessageFragment == null) {
                        mMessageFragment = new MessageFragment();
                        transaction.add(R.id.content, mMessageFragment, MESSAGE_FRAGMENT);
                    } else {
                        transaction.show(mMessageFragment);
                    }
                    mMessage.setSelected(true);
                    break;
                case DISCOVERY_FRAGMENT:
                    if (mDiscoverFragment == null) {
                        mDiscoverFragment = new DiscoverFragment();
                        transaction.add(R.id.content, mDiscoverFragment, DISCOVERY_FRAGMENT);
                    } else {
                        transaction.show(mDiscoverFragment);
                    }
                    mDiscover.setSelected(true);
                    break;
                case PROFILE_FRAGMENT:
                    if (mProfileFragment == null) {
                        mProfileFragment = new ProfileFragment();
                        transaction.add(R.id.content, mProfileFragment, PROFILE_FRAGMENT);
                    } else {
                        transaction.show(mProfileFragment);
                    }
                    mProfile.setSelected(true);
                    break;
            }
            transaction.commit();
            mCurrentFragment = fragment;
        }
    }

    private void hideFragment(String fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (fragment) {
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
                if (mCurrentFragment == HOME_FRAGMENT) {
                    mHomeFragment.scrollToTop(true);
                } else {
                    selectFragment(HOME_FRAGMENT);
                }
            }
        });
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentFragment == MESSAGE_FRAGMENT) {
                    // 滑动到顶部
                    mHomeFragment.scrollToTop(true);
                } else {
                    selectFragment(MESSAGE_FRAGMENT);
                }
            }
        });
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentFragment == DISCOVERY_FRAGMENT) {
                    // 滑动到顶部
                } else {
                    selectFragment(DISCOVERY_FRAGMENT);
                }
            }
        });
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentFragment == PROFILE_FRAGMENT) {
                    // 滑动到顶部
                } else {
                    selectFragment(PROFILE_FRAGMENT);
                }
            }
        });
    }

    @Subscribe
    public void onBottomBarStatusChange(BarStatusEvent event) {
        if (isBottomBarShow && !event.isBottomBarShow()) {
            setTopbarState(false);
            return;
        }
        if (!isBottomBarShow && event.isBottomBarShow()) {
            setTopbarState(true);
            return;
        }
    }

    private void setTopbarState(boolean state) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomBar, "translationY", state ? 0 : mBottomBar.getMeasuredHeight());
        animator.setDuration(300);
        animator.start();
        isBottomBarShow = state;
    }
}
