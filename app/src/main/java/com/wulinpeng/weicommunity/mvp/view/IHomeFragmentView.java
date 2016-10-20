package com.wulinpeng.weicommunity.mvp.view;

import com.sina.weibo.sdk.openapi.models.Status;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/7 下午8:28
 * @description:
 */
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
