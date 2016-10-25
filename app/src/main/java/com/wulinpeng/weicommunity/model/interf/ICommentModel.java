package com.wulinpeng.weicommunity.model.interf;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.model.entity.Repost;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/24 下午4:03
 * @description:
 */
public interface ICommentModel {

    public interface OnCountListener {
        void onFinish(int repost, int comment);
    }

    public interface OnCommentListener {
        void onFinish(List<Comment> data);

        void onFailed(String error);

        void onNomMoreData();
    }

    public interface OnRepostListener {
        void onFinish(List<Repost> data);

        void onFailed(String error);

        void onNomMoreData();
    }

    public void getRepostCommentCount(Context context, Status status, OnCountListener listener);

    public void firstLoadComment(Context context, Status status, OnCommentListener listener);

    public void nextCommentPage(Context context, Status status, OnCommentListener listener);

    public void firstLoadRepost(Context context, Status status, OnRepostListener listener);

    public void nextRepostPage(Context context, Status status, OnRepostListener listener);

}
