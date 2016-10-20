package com.wulinpeng.weicommunity.mvp.model.imp;

import android.content.Context;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.GroupList;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.mvp.model.IGroupModel;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;
import com.wulinpeng.weicommunity.util.SDCardUtil;

/**
 * @author wulinpeng
 * @datetime: 16/10/8 下午10:09
 * @description:
 */
public class GroupModel implements IGroupModel {

    private boolean isFirst = true;

    private Context context;

    private OnGroupFinishListener listener;
    @Override
    public void getGroups(Context context, OnGroupFinishListener listener) {
        this.context = context;
        if (isFirst) {
            // 从网络获取数据
            getGroupsFromNet(context, listener);
        } else {
            // 从缓存获取数据
            getGroupsFromSDCard(context, listener);
        }
    }

    private void getGroupsFromNet(Context context, OnGroupFinishListener listener) {
        GroupAPI groupAPI = new GroupAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        this.listener = listener;
        groupAPI.groups(groupRquestListener);
    }

    private void getGroupsFromSDCard(Context context, OnGroupFinishListener listener) {
        String response = SDCardUtil.getString(context, Constans.CACHE_PATH + "/group", "group.txt");
        if (response != null) {
            listener.onGroupFinish(GroupList.parse(response).groupList);
        }
    }

    private void saveResponse(Context context, String response) {
        SDCardUtil.putString(context, Constans.CACHE_PATH + "/group", "group.txt", response);
    }

    private RequestListener groupRquestListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            isFirst = false;
            saveResponse(context, s);
            listener.onGroupFinish(GroupList.parse(s).groupList);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            listener.onError(e.getMessage());
        }
    };
}
