package com.wulinpeng.weicommunity.login.home.presenter;

import android.content.Context;
import android.widget.Toast;

import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.wulinpeng.weicommunity.login.home.Contract;
import com.wulinpeng.weicommunity.model.interf.IStatusListModel;
import com.wulinpeng.weicommunity.model.interf.IUserModel;
import com.wulinpeng.weicommunity.model.imp.StatusListModel;
import com.wulinpeng.weicommunity.model.imp.UserModel;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/14 下午12:18
 * @description:
 */
public class HomeFragmentPresenter implements Contract.IHomeFragmentPresenter {

    private Contract.IHomeFragmentView mRootView;

    private IUserModel mUserModel;

    private IStatusListModel mStatusModel;

    public HomeFragmentPresenter(Contract.IHomeFragmentView rootView) {
        mRootView = rootView;
        mUserModel = new UserModel();
        mStatusModel = new StatusListModel();
    }

    @Override
    public void updateUserName(final Context context) {
        mUserModel.show(Long.valueOf(AccessTokenKeeper.getAccessToken(context).getUid()), context, new IUserModel.UserDetailListener() {
            @Override
            public void onComplete(User user) {
                mRootView.setUsername(user.name);
            }

            @Override
            public void onError(String errMsg) {
                mRootView.setUsername("首页");
                Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void firstLoadStatus(Context context, final boolean fromLogin) {
        // 有缓存先读取
        if (fromLogin == false) {
            mStatusModel.loadStatusListCache(IStatusListModel.TYPE_FRIEND_CIRCLE, context, listener);
        }
        mRootView.showLoading(true);
        mStatusModel.friendsLimeLine(context, listener);
    }

    @Override
    public void refreshStatus(Context context, int group) {
        mRootView.showLoading(true);
        mRootView.scrollToTop(false);
        if (group == IStatusListModel.TYPE_FRIEND_CIRCLE) {
            mStatusModel.friendsLimeLine(context, listener);
        } else {
            mStatusModel.bilateralTimeline(context, listener);
        }
    }

    @Override
    public void loadMoreStatus(Context context, int group) {
        mRootView.showLoadingFooter();
        mStatusModel.loadMoreData(context, listener);
    }

    private IStatusListModel.OnStatusFinishListener listener = new IStatusListModel.OnStatusFinishListener() {
        @Override
        public void onDataFinish(List<Status> statuses, String message) {
            mRootView.showEmptyError(false);
            mRootView.update(statuses);
            mRootView.showLoading(false);
            if (message != null) {
                mRootView.showLoadToast(message);
            }
        }

        @Override
        public void onNoMoreData() {
            mRootView.showEmptyError(false);
            mRootView.showLoading(false);
            mRootView.showLoadToast("更新0条微博");
        }

        @Override
        public void onNoMoreNextData() {
            mRootView.showNoMoreFooter();
        }

        @Override
        public void onStatusError(String error) {
            mRootView.showLoading(false);
        }

        @Override
        public void onNoData() {
            mRootView.showEmptyError(true);
            mRootView.showLoading(false);
        }
    };
}
