package com.wulinpeng.weicommunity.model.imp;

import android.content.Context;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.model.entity.StatusList;
import com.wulinpeng.weicommunity.model.interf.IStatusListModel;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;
import com.wulinpeng.weicommunity.util.SDCardUtil;
import com.wulinpeng.weicommunity.util.SharedUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/17 下午3:57
 * @description:
 */
public class StatusListModel implements IStatusListModel {

    private int currentGroup = TYPE_FRIEND_CIRCLE;

    private OnStatusFinishListener onStatusFinishListener;

    private Context context;

    /**
     * 记录当前数据的maxId,每次获取新数据更新mStatuses的时候由于不保存StatusList,所以保存其中的maxId
     */
    private long maxId = 0;

    /**
     * 保存最新数据
     */
    private List<Status> mStatuses = new ArrayList<>();

    @Override
    public void friendsLimeLine(Context context, OnStatusFinishListener listener) {
        StatusesAPI api = new StatusesAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        this.context = context;
        onStatusFinishListener = listener;
        long sinceId = getSinceId(TYPE_FRIEND_CIRCLE);
        api.friendsTimeline(sinceId, 0, 10, 1, false, 0, false, refreshDataListener);
    }

    @Override
    public void bilateralTimeline(Context context, OnStatusFinishListener listener) {
        StatusesAPI api = new StatusesAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        this.context = context;
        onStatusFinishListener = listener;
        long sinceId = getSinceId(TYPE_BILATERAL);
        api.bilateralTimeline(sinceId, 0, 10, 1, false, 0, false, refreshDataListener);
    }

    /**
     * 使用不同的listener
     * @param context
     * @param listener
     */
    @Override
    public void loadMoreData(Context context, OnStatusFinishListener listener) {
        StatusesAPI api = new StatusesAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        this.context = context;
        onStatusFinishListener = listener;
        if (currentGroup == TYPE_FRIEND_CIRCLE) {
            api.friendsTimeline(0, maxId, 10, 1, false, 0, false, moreDataListener);
        } else {
            api.bilateralTimeline(0, maxId, 10, 1, false, 0, false, moreDataListener);
        }
    }

    /**
     * 如果不同组就刷新全部数据,sinceId = 0
     * 如果同一group且有数据,就拿到最大的id💺sinceId
     * @param type
     * @return
     */
    private long getSinceId(int type) {
        // 全部刷新
        if (currentGroup != type) {
            return 0;
        }
        if (mStatuses.size() > 0) {
            // 局部刷新
            return Long.parseLong(mStatuses.get(0).id);
        }
        currentGroup = type;
        return 0;
    }

    /**
     * 保存StatusList对象到本地,每次更新数据都会清空本地文件保存新数据
     * @param groupType
     * @param context
     * @param statusList
     */
    @Override
    public void saveStatusList(int groupType, Context context, StatusList statusList) {
        if (groupType == TYPE_FRIEND_CIRCLE) {
            SDCardUtil.putObject(context, Constans.CACHE_PATH + "/statusList", "friends_status_list", statusList);
        } else {
            SDCardUtil.putObject(context, Constans.CACHE_PATH + "/statusList", "bilateral_status_list", statusList);
        }
        // 每次保存本地都要更新本地的maxId,以便下次进入的时候加载缓存后知道maxId,避免重复
        SharedUtil.putLong(context, "max_id", maxId);
    }

    /**
     * 加载本地缓存数据,一般是在第一次进入(非login)的时候先获取缓存,再从网络获取
     * @param groupType
     * @param context
     * @param listener
     */
    @Override
    public void loadStatusListCache(int groupType, Context context, OnStatusFinishListener listener) {
        onStatusFinishListener = listener;
        StatusList list;
        if (groupType == TYPE_FRIEND_CIRCLE) {
            list = (StatusList) SDCardUtil.getObject(context, Constans.CACHE_PATH + "/statusList", "friends_status_list");
        } else {
            list = (StatusList) SDCardUtil.getObject(context, Constans.CACHE_PATH + "/statusList", "bilateral_status_list");
        }
        if (list != null && list.statusList != null) {
            mStatuses = list.statusList;
            Log.d("Debug", "not null");
        }
        onStatusFinishListener.onDataFinish(mStatuses, null);
        maxId = SharedUtil.getLong(context, "max_id", 0);
    }


    private RequestListener refreshDataListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            StatusList statusList = StatusList.parse(s);
            List<Status> statuses = statusList.statusList;
            if (statuses == null || statuses.size() == 0) {
                // 没有数据
                if (mStatuses.size() != 0) {
                    // 更新0条微博
                    Log.d("Debug", "更新0条");
                    onStatusFinishListener.onNoMoreData();
                } else {
                    // 该分组没有成员
                    onStatusFinishListener.onNoData();
                }
            } else {
                maxId = statusList.max_id;
                //更新statuses.size()条微博
                int size = statuses.size();
                if (mStatuses.size() == 0 || statusList.max_id == Long.parseLong(mStatuses.get(0).id)) {
                    // 局部更新
                    mStatuses.addAll(0, statuses);
                    statusList.statusList = (ArrayList<Status>) mStatuses;
                } else {
                    // 全局刷新
                    mStatuses.clear();
                    mStatuses.addAll(statuses);
                }
                // 缓存
                saveStatusList(currentGroup, context, statusList);
                onStatusFinishListener.onDataFinish(mStatuses, "更新" + size + "条微博");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            onStatusFinishListener.onStatusError(e.getMessage());
        }
    };

    private RequestListener moreDataListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            StatusList statusList = StatusList.parse(s);
            List<Status> statuses = statusList.statusList;
            if (statuses == null || statuses.size() == 0) {
                onStatusFinishListener.onNoMoreNextData();
            } else {
                // 每次刷新数据都更新maxId
                maxId = statusList.max_id;
                mStatuses.addAll(statuses);
                statusList.statusList = (ArrayList<Status>) mStatuses;
                saveStatusList(currentGroup, context, statusList);
                onStatusFinishListener.onDataFinish(mStatuses, null);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            onStatusFinishListener.onStatusError(e.getMessage());
        }
    };
}
