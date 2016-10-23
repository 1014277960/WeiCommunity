package com.wulinpeng.weicommunity.login.home;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.Group;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/20 下午7:35
 * @description:
 */
public class Contract {

    public interface IHomeFragmentView {

        public void showLoading(boolean loading);

        public void setUsername(String username);

        public void setTopGroupName(String userName);

        public void showEmptyError(boolean state);

        /**
         * 滑倒顶部并判断是否刷新
         * @param refresh
         */
        public void scrollToTop(boolean refresh);

        public void update(List<Status> data);

        public void showLoadToast(String message);

        public void showLoadingFooter();

        public void showNoMoreFooter();
    }

    public interface IGroupWindowView {

        public void updateGroup(List<Group> groups);

        public void showError(String error);
    }

    public interface IHomeFragmentPresenter {

        public void updateUserName(Context context);

        public void firstLoadStatus(Context context, boolean fromLogin);

        public void refreshStatus(Context context, int group);

        public void loadMoreStatus(Context context, int group);
    }

    public interface IGroupWindowPresenter {
        public void updateGroup(Context context);
    }
}
