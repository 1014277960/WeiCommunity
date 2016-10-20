package com.wulinpeng.weicommunity.mvp.model;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.Group;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/8 下午10:08
 * @description:
 */
public interface IGroupModel {

    interface OnGroupFinishListener {
        public void onGroupFinish(List<Group> data);

        public void onError(String error);
    }

    public void getGroups(Context context, OnGroupFinishListener listener);
}
