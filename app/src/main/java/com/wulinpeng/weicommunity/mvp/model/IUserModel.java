package com.wulinpeng.weicommunity.mvp.model;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.User;

/**
 * @author wulinpeng
 * @datetime: 16/10/14 下午12:21
 * @description:
 */
public interface IUserModel {

    public interface UserDetailListener {
        public void onComplete(User user);

        public void onError(String errMsg);
    }

    public void show(long uid, Context context, UserDetailListener listener);
}
