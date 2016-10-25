package com.wulinpeng.weicommunity.weibodetail.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.model.entity.Repost;
import com.wulinpeng.weicommunity.repost.view.PostActivity;
import com.wulinpeng.weicommunity.weibodetail.Contract;
import com.wulinpeng.weicommunity.weibodetail.adapter.WeiboDetailAdapter;
import com.wulinpeng.weicommunity.weibodetail.event.RepostOrCommentEvent;
import com.wulinpeng.weicommunity.weibodetail.presenter.WeiboDetailPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wulinpeng.weicommunity.weibodetail.adapter.WeiboDetailAdapter.TYPE_COMMENT;
import static com.wulinpeng.weicommunity.weibodetail.adapter.WeiboDetailAdapter.TYPE_REPOST;

/**
 * @author wulinpeng
 * @datetime: 16/10/24 下午9:19
 * @description:
 */
public class WeiboDetailActivity extends AppCompatActivity implements Contract.IWeiboDetailView {

    @BindView(R.id.back)
    public ImageView mBack;

    @BindView(R.id.more)
    public ImageView mMore;

    @BindView(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @BindView(R.id.bottom_retweet)
    public LinearLayout mRetweet;

    @BindView(R.id.bottom_comment)
    public LinearLayout mComment;

    @BindView(R.id.bottom_like)
    public LinearLayout mLike;

    private List<Comment> mCommentList = new ArrayList<>();

    private List<Repost> mRepostList = new ArrayList<>();

    private int mType = TYPE_COMMENT;

    private WeiboDetailAdapter mAdapter;

    private Status mStatus;

    private Contract.IWeiboDetailPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mStatus = (Status) getIntent().getSerializableExtra("status");
        mPresenter = new WeiboDetailPresenter(this);

        initRecyclerView();
        setupRefreshLayout();
        setupListener();

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mPresenter.updateComment(WeiboDetailActivity.this, mStatus);
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WeiboDetailAdapter(this, mStatus, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setLoadMoreListener(new WeiboDetailAdapter.onLoadMoreListener() {
            @Override
            public void onLoadMore(View footView) {
                mAdapter.setFooter(LayoutInflater.from(WeiboDetailActivity.this).inflate(R.layout.loading_circle_layout, null));
                if (mType == TYPE_COMMENT) {
                    mPresenter.nextPageComment(WeiboDetailActivity.this, mStatus);
                } else {
                    mPresenter.nextPageRepost(WeiboDetailActivity.this, mStatus);
                }
            }
        });
    }

    private void setupRefreshLayout() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mType == TYPE_COMMENT) {
                    mPresenter.updateComment(WeiboDetailActivity.this, mStatus);
                } else {
                    mPresenter.updateRepost(WeiboDetailActivity.this, mStatus);
                }
            }
        });
    }

    private void setupListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeiboDetailActivity.this, PostActivity.class);
                intent.putExtra("type", PostActivity.TYPE_REPOST);
                intent.putExtra("status", mStatus);
                startActivity(intent);
            }
        });

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Subscribe
    public void onTypeChange(RepostOrCommentEvent event) {
        int type = event.getType();
        if (type == TYPE_COMMENT && mType != TYPE_COMMENT) {
            mPresenter.updateComment(WeiboDetailActivity.this, mStatus);
        } else if (type == TYPE_REPOST && mType != TYPE_REPOST) {
            mPresenter.updateRepost(WeiboDetailActivity.this, mStatus);
            Toast.makeText(WeiboDetailActivity.this, "由于API限制无法获取大部分转发数据", Toast.LENGTH_SHORT).show();
        }
        mType = type;
    }

    @Override
    public void showLoading(boolean state) {
        mRefreshLayout.setRefreshing(state);
    }

    @Override
    public void notifyCommentChange(List<Comment> data) {
        mType = TYPE_COMMENT;
        mCommentList = data;
        mAdapter.setCommentData(mCommentList);
        mAdapter.setLoading(false);
    }

    @Override
    public void notifyRepostChange(List<Repost> data) {
        mType = TYPE_REPOST;
        mRepostList = data;
        mAdapter.setRepostData(mRepostList);
        mAdapter.setLoading(false);
    }

    @Override
    public void changeRepostChount(int count) {

    }

    @Override
    public void changeCommentCount(int count) {

    }

    @Override
    public void showEmpty(boolean state) {
    }

    @Override
    public void showNoMoreData() {
        mAdapter.setFooter(null);
    }

    @Override
    public void onError(String error) {
        mAdapter.setFooter(null);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
