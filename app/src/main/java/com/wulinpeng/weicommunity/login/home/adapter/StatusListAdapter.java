package com.wulinpeng.weicommunity.login.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.event.BarStatusEvent;
import com.wulinpeng.weicommunity.repost.view.PostActivity;
import com.wulinpeng.weicommunity.util.StatusFillUtil;
import com.wulinpeng.weicommunity.weibodetail.view.WeiboDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wulinpeng
 * @datetime: 16/10/15 下午12:33
 * @description:
 */
public class StatusListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ORIGIN  = 0;
    private static final int TYPE_RETWEET = 1;
    private static final int TYPE_HEADER  = 2;
    private static final int TYPE_FOOTER  = 3;

    public interface OnLoadMoreListener {
        public void onLoadMore(View footView);
    }

    public interface OnHeaderClickListener {
        public void onHeaderClick();
    }

    public interface OnFooterClickListener {
        public void onFooterClick();
    }

    private List<Status> mData;

    private Context mContext;

    private LayoutInflater mInflater;

    private View mHeader;

    private View mFooter;

    private OnLoadMoreListener mLoadMoreListener;

    private OnHeaderClickListener mHeaderClickListener;

    private OnFooterClickListener mFooterClickListener;

    private RecyclerView mRecyclerView;

    private boolean isLoading = false;

    private LinearLayoutManager mLayoutManager;

    private int mTotalDistanceY = 0;

    private static final int HIDE_DISTANCE = 80;

    private boolean isBarShow = true;

    public StatusListAdapter(Context context, List<Status> data, RecyclerView recyclerView) {
        mContext = context;
        mData = data;
        mRecyclerView = recyclerView;
        mInflater = LayoutInflater.from(context);
        setupScrollListener();
    }

    private void setupScrollListener() {
        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 判断到达底部的条件,如果此时没有数据说明还为加载数据,不是loadmore
                if (mData != null && mData.size() > 0 && mLoadMoreListener != null) {
                    if (mRecyclerView.computeVerticalScrollExtent() + mRecyclerView.computeVerticalScrollOffset() >= mRecyclerView.computeVerticalScrollRange()) {
                        isLoading = true;
                        mLoadMoreListener.onLoadMore(mFooter);
                    }
                }

                // 判断是否应该隐藏或者显示bar
                if (mTotalDistanceY > HIDE_DISTANCE && isBarShow) {
                    BarStatusEvent event = new BarStatusEvent();
                    event.setBottomBarShow(false);
                    event.setTopBarShow(false);
                    EventBus.getDefault().post(event);
                    isBarShow = false;
                    mTotalDistanceY = 0;
                } else if (mTotalDistanceY < -HIDE_DISTANCE && !isBarShow) {
                    BarStatusEvent event = new BarStatusEvent();
                    event.setBottomBarShow(true);
                    event.setTopBarShow(true);
                    EventBus.getDefault().post(event);
                    isBarShow = true;
                    mTotalDistanceY = 0;
                } else if (isBarShow && dy > 0 || !isBarShow && dy < 0) {
                    // 保证显示的时候只记录向上滑的操作,不显示的时候只记录向下的操作
                    mTotalDistanceY += dy;
                }
            }
        });
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setHeaderClickListener(OnHeaderClickListener mClickListener) {
        this.mHeaderClickListener = mClickListener;
    }

    public void setFooterClickListener(OnFooterClickListener mClickListener) {
        this.mFooterClickListener = mClickListener;
    }

    public void setLoadMoreListener(OnLoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public void setHeader(View header) {
        this.mHeader = header;
        mHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeaderClickListener != null) {
                    mHeaderClickListener.onHeaderClick();
                }
            }
        });
        notifyDataSetChanged();
    }

    public void setFooter(View footer) {
        this.mFooter = footer;
        if (footer != null) {
            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFooterClickListener != null) {
                        mFooterClickListener.onFooterClick();
                    }
                }
            });
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHodler(mHeader);

            case TYPE_FOOTER:
                return new FooterViewHolder(mFooter);

            case TYPE_ORIGIN:
                return new OriginViewHodler(mInflater.inflate(R.layout.status_origin, parent, false));

            case TYPE_RETWEET:
                return new RetweetViewHodler(mInflater.inflate(R.layout.status_retweet, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_ORIGIN) {
            Status status = mData.get(getRealPosition(position));
            OriginViewHodler viewHolder = (OriginViewHodler)holder;
            // top bar
            StatusFillUtil.fillTopBar(mContext, status, viewHolder.profileImg, viewHolder.profileVefity, viewHolder.profileName, viewHolder.statusTime, viewHolder.statusFrom, viewHolder.more);
            //content
            StatusFillUtil.fillStatusContent(mContext, viewHolder.statusContent, status.text);
            // bottom
            StatusFillUtil.fillBottomBar(mContext, status, viewHolder.retweet, viewHolder.comment, viewHolder.like);
            // images
            StatusFillUtil.fillImageList(mContext, status, viewHolder.imgs);
            // bottom listener
            StatusFillUtil.setBottomClickListener(mContext, status, viewHolder.bottomRetweetLayout, viewHolder.bottomCommentLayout, viewHolder.bottomLikeLayout);

        }
        if (type == TYPE_RETWEET) {
            Status status = mData.get(getRealPosition(position));
            RetweetViewHodler viewHolder = (RetweetViewHodler)holder;
            // top bar
            StatusFillUtil.fillTopBar(mContext, status, viewHolder.profileImg, viewHolder.profileVefity, viewHolder.profileName, viewHolder.statusTime, viewHolder.statusFrom, viewHolder.more);
            //content
            StatusFillUtil.fillStatusContent(mContext, viewHolder.statusContent, status.text);
            StatusFillUtil.fillRetweetContent(mContext, status, viewHolder.originContent);
            // bottom
            StatusFillUtil.fillBottomBar(mContext, status, viewHolder.retweet, viewHolder.comment, viewHolder.like);
            StatusFillUtil.fillImageList(mContext, status.retweeted_status, viewHolder.imgs);
            // bottom listener
            StatusFillUtil.setBottomClickListener(mContext, status, viewHolder.bottomRetweetLayout, viewHolder.bottomCommentLayout, viewHolder.bottomLikeLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeader == null && mFooter == null) {
            return mData.get(position).retweeted_status != null ? TYPE_RETWEET : TYPE_ORIGIN;
        } else if (mHeader == null) {
            return position == getItemCount() - 1 ? TYPE_FOOTER : (mData.get(position).retweeted_status != null ? TYPE_RETWEET : TYPE_ORIGIN);
        } else if (mFooter == null) {
            return position == 0 ? TYPE_HEADER : (mData.get(position - 1).retweeted_status != null ? TYPE_RETWEET : TYPE_ORIGIN);
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            }
            if (position == getItemCount() - 1) {
                return TYPE_FOOTER;
            }
            return mData.get(position - 1).retweeted_status != null ? TYPE_RETWEET : TYPE_ORIGIN;
        }
    }

    @Override
    public int getItemCount() {
        return (mHeader == null ? 0 : 1) + (mFooter == null ? 0 : 1) + mData.size();
    }

    class OriginViewHodler extends RecyclerView.ViewHolder {

        @BindView(R.id.status_origin_layout)
        public LinearLayout originLayout;
        @BindView(R.id.status_topbar)
        public LinearLayout topBar;
        @BindView(R.id.profile_img)
        public ImageView profileImg;
        @BindView(R.id.profile_verify)
        public ImageView profileVefity;
        @BindView(R.id.status_more)
        public ImageView more;
        @BindView(R.id.profile_name)
        public TextView profileName;
        @BindView(R.id.status_time)
        public TextView statusTime;
        @BindView(R.id.status_from)
        public TextView statusFrom;
        @BindView(R.id.status_content)
        public TextView statusContent;
        @BindView(R.id.bottom_retweet)
        public LinearLayout bottomRetweetLayout;
        @BindView(R.id.bottom_comment)
        public LinearLayout bottomCommentLayout;
        @BindView(R.id.bottom_like)
        public LinearLayout bottomLikeLayout;
        @BindView(R.id.retweet)
        public TextView retweet;
        @BindView(R.id.comment)
        public TextView comment;
        @BindView(R.id.like)
        public TextView like;
        @BindView(R.id.status_imgs)
        public RecyclerView imgs;

        public OriginViewHodler(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            originLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WeiboDetailActivity.class);
                    intent.putExtra("status", mData.get(getRealPosition(getLayoutPosition())));
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class RetweetViewHodler extends RecyclerView.ViewHolder {

        @BindView(R.id.status_retweet_layout)
        public LinearLayout originLayout;
        @BindView(R.id.retweetStatus_layout)
        public LinearLayout retweetLayout;
        @BindView(R.id.status_topbar)
        public LinearLayout topBar;
        @BindView(R.id.profile_img)
        public ImageView profileImg;
        @BindView(R.id.profile_verify)
        public ImageView profileVefity;
        @BindView(R.id.status_more)
        public ImageView more;
        @BindView(R.id.profile_name)
        public TextView profileName;
        @BindView(R.id.status_time)
        public TextView statusTime;
        @BindView(R.id.status_from)
        public TextView statusFrom;
        @BindView(R.id.status_content)
        public TextView statusContent;
        @BindView(R.id.origin_contentWithName)
        public TextView originContent;
        @BindView(R.id.bottom_retweet)
        public LinearLayout bottomRetweetLayout;
        @BindView(R.id.bottom_comment)
        public LinearLayout bottomCommentLayout;
        @BindView(R.id.bottom_like)
        public LinearLayout bottomLikeLayout;
        @BindView(R.id.retweet)
        public TextView retweet;
        @BindView(R.id.comment)
        public TextView comment;
        @BindView(R.id.like)
        public TextView like;
        @BindView(R.id.origin_imageList)
        public RecyclerView imgs;

        public RetweetViewHodler(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            originLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WeiboDetailActivity.class);
                    intent.putExtra("status", mData.get(getRealPosition(getLayoutPosition())));
                    mContext.startActivity(intent);
                }
            });
            retweetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WeiboDetailActivity.class);
                    intent.putExtra("status", mData.get(getRealPosition(getLayoutPosition())).retweeted_status);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class HeaderViewHodler extends RecyclerView.ViewHolder {

        public HeaderViewHodler(View itemView) {
            super(itemView);
        }
    }
    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(final View itemView) {
            super(itemView);
        }
    }

    private int getRealPosition(int position) {
        return mHeader == null ? position : position - 1;
    }


}