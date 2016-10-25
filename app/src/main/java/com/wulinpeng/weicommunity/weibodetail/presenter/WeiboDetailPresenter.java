package com.wulinpeng.weicommunity.weibodetail.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.model.entity.Repost;
import com.wulinpeng.weicommunity.model.imp.CommentModel;
import com.wulinpeng.weicommunity.model.interf.ICommentModel;
import com.wulinpeng.weicommunity.weibodetail.Contract;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/24 下午9:09
 * @description:
 */
public class WeiboDetailPresenter implements Contract.IWeiboDetailPresenter {

    private Contract.IWeiboDetailView rootView;

    private ICommentModel commentModel;

    private Context context;

    public WeiboDetailPresenter(Contract.IWeiboDetailView rootView) {
        this.rootView = rootView;
        commentModel = new CommentModel();
    }

    @Override
    public void updateComment(final Context context, Status status) {
        this.context = context;
        rootView.showLoading(true);
        commentModel.firstLoadComment(context, status, commentListener);
    }

    @Override
    public void nextPageComment(Context context, Status status) {
        this.context = context;
        rootView.showLoading(true);
        commentModel.nextCommentPage(context, status, commentListener);
    }

    @Override
    public void updateRepost(Context context, Status status) {
        this.context = context;
        rootView.showLoading(true);
        commentModel.firstLoadRepost(context, status, repostListener);
    }

    @Override
    public void nextPageRepost(Context context, Status status) {
        this.context = context;
        rootView.showLoading(true);
        commentModel.nextRepostPage(context, status, repostListener);
    }

    private ICommentModel.OnCommentListener commentListener = new ICommentModel.OnCommentListener() {
        @Override
        public void onFinish(List<Comment> data) {
            rootView.showEmpty(false);
            rootView.notifyCommentChange(data);
            rootView.showLoading(false);
        }

        @Override
        public void onFailed(String error) {
            rootView.showLoading(false);
            rootView.onError(error);
        }

        @Override
        public void onNomMoreData() {
            rootView.showNoMoreData();
            rootView.showLoading(false);
        }
    };

    private ICommentModel.OnRepostListener repostListener = new ICommentModel.OnRepostListener() {
        @Override
        public void onFinish(List<Repost> data) {
            rootView.showEmpty(false);
            rootView.notifyRepostChange(data);
            rootView.showLoading(false);
        }

        @Override
        public void onFailed(String error) {
            rootView.showLoading(false);
            rootView.onError(error);
        }

        @Override
        public void onNomMoreData() {
            rootView.showNoMoreData();
            rootView.showLoading(false);
        }
    };
}
