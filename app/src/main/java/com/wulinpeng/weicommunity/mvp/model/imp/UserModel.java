package com.wulinpeng.weicommunity.mvp.model.imp;

import android.content.Context;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.mvp.model.IUserModel;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;
import com.wulinpeng.weicommunity.util.SDCardUtil;

/**
 * @author wulinpeng
 * @datetime: 16/10/14 下午12:21
 * @description:
 */
public class UserModel implements IUserModel {

    private Context mContext;

    /**
     * 当前请求的uid
     */
    private long mUid;

    /**
     * 当前user请求传递过来的listner
     */
    private UserDetailListener mUserDetailListener;

    @Override
    public void show(long uid, Context context, UserDetailListener listener) {
        mContext = context;
        mUid = uid;
        UsersAPI api = new UsersAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        mUserDetailListener = listener;
        api.show(uid, mUserListener);
    }

    /**
     * 通过uid保存用户信息
     * @param s
     * @param context
     */
    private void saveUserCache(String s, Context context) {
        SDCardUtil.putString(context, Constans.CACHE_PATH + "/user", AccessTokenKeeper.getAccessToken(context).getUid() + ".txt", s);
    }

    /**
     * 从本地得到用户数据
     * @param context
     * @param uid
     */
    private void getUserCache(Context context, String uid, UserDetailListener listener) {
        String result = SDCardUtil.getString(context, Constans.CACHE_PATH + "/user", AccessTokenKeeper.getAccessToken(context).getUid() + ".txt");
        if (result != null) {
            listener.onComplete(User.parse(result));
        }
    }

    /**
     * 返回用户信息,得到信息保存到本地,下次请求若是失败直接读取本地信息
     */
    private RequestListener mUserListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            User user = User.parse(s);
            if (user != null) {
                mUserDetailListener.onComplete(user);
                // 保存信息
                saveUserCache(s, mContext);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            mUserDetailListener.onError(e.getMessage());
            // 本地读取信息
            getUserCache(mContext, String.valueOf(mUid), mUserDetailListener);
        }
    };
}
