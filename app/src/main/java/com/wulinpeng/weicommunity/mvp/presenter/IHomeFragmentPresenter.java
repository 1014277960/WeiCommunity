package com.wulinpeng.weicommunity.mvp.presenter;

import android.content.Context;

/**
 * @author wulinpeng
 * @datetime: 16/10/7 下午8:28
 * @description:
 */
public interface IHomeFragmentPresenter {

    public void updateUserName(Context context);

    public void firstLoadStatus(Context context, boolean fromLogin);

    public void refreshStatus(Context context, int group);

    public void loadMoreStatus(Context context, int group);

}
