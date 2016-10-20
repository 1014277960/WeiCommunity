package com.wulinpeng.weicommunity.mvp.model;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.mvp.entity.StatusList;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/17 下午3:50
 * @description:
 */
public interface IStatusListModel {

    public static int TYPE_FRIEND_CIRCLE = 0;
    public static int TYPE_BILATERAL     = 1;

    interface OnStatusFinishListener {

        // 完成数据加载并告知增加多少数据,传递message
        void onDataFinish(List<Status> statuses, String message);

        // 刷新获取0条数据
        void onNoMoreData();

        // 加载更多失败
        void onNoMoreNextData();

        void onStatusError(String error);

        void onNoData();
    }

    public void friendsLimeLine(Context context, OnStatusFinishListener listener);

    public void bilateralTimeline(Context context, OnStatusFinishListener listener);

    public void loadMoreData(Context context, OnStatusFinishListener listener);

    public void saveStatusList(int groupType, Context context, StatusList statusList);

    public void loadStatusListCache(int groupType, Context context, OnStatusFinishListener listener);
}
