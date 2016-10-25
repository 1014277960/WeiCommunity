package com.wulinpeng.weicommunity.weibodetail;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.model.entity.Repost;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/24 下午3:27
 * @description:
 */
public class Contract {

    public interface IWeiboDetailView {
        void showLoading(boolean state);

        void notifyCommentChange(List<Comment> data);

        void notifyRepostChange(List<Repost> data);

        void changeRepostChount(int count);

        void changeCommentCount(int count);

        void showEmpty(boolean state);

        void showNoMoreData();

        void onError(String error);
    }

    public interface IWeiboDetailPresenter {
        void updateComment(Context context, Status status);

        void nextPageComment(Context context, Status status);

        void updateRepost(Context context, Status status);

        void nextPageRepost(Context context, Status status);
    }
}
