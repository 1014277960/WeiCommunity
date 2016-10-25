package com.wulinpeng.weicommunity.model.imp;

import android.content.Context;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.model.entity.Repost;
import com.wulinpeng.weicommunity.model.entity.RepostList;
import com.wulinpeng.weicommunity.model.entity.StatusList;
import com.wulinpeng.weicommunity.model.interf.ICommentModel;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/24 下午4:17
 * @description:
 */
public class CommentModel implements ICommentModel {

    private List<Comment> mCommentList = new ArrayList<>();
    private List<Repost> mRepostList = new ArrayList<>();

    @Override
    public void getRepostCommentCount(Context context, Status status, final OnCountListener listener) {
        StatusesAPI api = new StatusesAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        api.show(Long.parseLong(status.id), new RequestListener() {
            @Override
            public void onComplete(String s) {
                Status status = Status.parse(s);
                listener.onFinish(status.reposts_count, status.comments_count);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Log.d("Debug", "获取数量失败");
            }
        });
    }

    @Override
    public void firstLoadComment(Context context, Status status, final OnCommentListener listener) {
        CommentsAPI api = new CommentsAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        api.show(Long.parseLong(status.id), 0, 0, 20, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                List<Comment> list = CommentList.parse(s).commentList;
                if (list != null) {
                    mCommentList.clear();
                    mCommentList.addAll(list);
                }
                listener.onFinish(mCommentList);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onFailed(e.getMessage());
            }
        });
    }

    @Override
    public void nextCommentPage(Context context, Status status, final OnCommentListener listener) {
        CommentsAPI api = new CommentsAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        String maxId = "";
        if (mCommentList.size() == 0) {
            maxId = "0";
        } else {
            maxId = mCommentList.get(mCommentList.size() - 1).id;
        }
        api.show(Long.parseLong(status.id), 0, Long.parseLong(maxId), 20, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                List<Comment> list = CommentList.parse(s).commentList;
                if (list != null) {
                    if (list.size() <= 1) {
                        listener.onNomMoreData();
                    } else {
                        list.remove(0);
                        mCommentList.addAll(list);
                        listener.onFinish(mCommentList);
                    }
                } else {
                    listener.onNomMoreData();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onFailed(e.getMessage());
            }
        });
    }

    @Override
    public void firstLoadRepost(Context context, Status status, final OnRepostListener listener) {
        Log.d("Debug", "id" + status.id);
        StatusesAPI api = new StatusesAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        api.repostTimelineIds(Long.parseLong(status.id), 0, 0, 20, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                List<Repost> list = RepostList.parse(s).getRepostList();
                if (list != null) {
                    mRepostList.clear();
                    mRepostList.addAll(list);
                }
                listener.onFinish(mRepostList);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onFailed(e.getMessage());
            }
        });
        /*
        api.repostTimeline(Long.parseLong(status.id), 0, 0, 20, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                List<Repost> list = RepostList.parse(s).getRepostList();
                if (list != null) {
                    mRepostList.clear();
                    mRepostList.addAll(list);
                }
                listener.onFinish(mRepostList);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onFailed(e.getMessage());
            }
        });
        */
    }

    @Override
    public void nextRepostPage(Context context, Status status, final OnRepostListener listener) {
        StatusesAPI api = new StatusesAPI(context, Constans.APP_KEY, AccessTokenKeeper.getAccessToken(context));
        String maxId = "";
        if (mRepostList.size() == 0) {
            maxId = "0";
        } else {
            maxId = mRepostList.get(mRepostList.size() - 1).getId();
        }
        api.repostTimeline(Long.parseLong(status.id), 0, Long.parseLong(maxId), 20, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                List<Repost> list = RepostList.parse(s).getRepostList();
                if (list != null) {
                    if (list.size() <= 1) {
                        listener.onNomMoreData();
                    } else {
                        list.remove(0);
                        mRepostList.addAll(list);
                        listener.onFinish(mRepostList);
                    }
                } else {
                    listener.onNomMoreData();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onFailed(e.getMessage());
            }
        });
    }
}
