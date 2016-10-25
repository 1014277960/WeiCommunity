package com.wulinpeng.weicommunity.weibodetail.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.view.custom.CircleTransformation;
import com.wulinpeng.weicommunity.model.entity.Repost;
import com.wulinpeng.weicommunity.util.StatusFillUtil;
import com.wulinpeng.weicommunity.weibodetail.event.RepostOrCommentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wulinpeng
 * @datetime: 16/10/24 下午9:34
 * @description:
 */
public class WeiboDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    public static final int TYPE_REPOST = 0;
    public static final int TYPE_COMMENT = 1;

    public interface onLoadMoreListener {
        public void onLoadMore(View footView);
    }

    public interface onItemClickListener {
        public void onItemClick(View view, int position);
    }

    private int mType = TYPE_COMMENT;

    private Status mStatus;

    private List<Comment> mCommentData = new ArrayList<>();

    private List<Repost> mRepostData = new ArrayList<>();

    private Context mContext;

    private LayoutInflater mInflater;

    private View mFooter;

    private onLoadMoreListener mLoadMoreListener;

    private onItemClickListener mClickListener;

    private RecyclerView mRecyclerView;

    private boolean isLoading = false;

    private LinearLayoutManager mLayoutManager;

    public WeiboDetailAdapter(Context context, Status status, RecyclerView recyclerView) {
        mContext = context;
        this.mStatus = status;
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
                if (!isLoading && mLayoutManager.findLastVisibleItemPosition() == getItemCount() - 1 && mLoadMoreListener != null) {
                    isLoading = true;
                    mLoadMoreListener.onLoadMore(mFooter);
                }
            }
        });
    }

    public void setCommentData(List<Comment> data) {
        this.mCommentData = data;
        mType = TYPE_COMMENT;
        notifyDataSetChanged();
    }

    public void setRepostData(List<Repost> data) {
        this.mRepostData = data;
        mType = TYPE_REPOST;
        notifyDataSetChanged();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setLoadMoreListener(onLoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public void setFooter(View footer) {
        this.mFooter = footer;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHodler(mInflater.inflate(R.layout.weibo_detail_header, parent, false));

            case TYPE_FOOTER:
                return new FooterViewHodler(mFooter);

            case TYPE_NORMAL:
                return new ItemViewHodler(mInflater.inflate(R.layout.weibo_detail_item, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            HeaderViewHodler viewHodler = (HeaderViewHodler) holder;
            StatusFillUtil.fillTopBar(mContext, mStatus, viewHodler.profileImg, viewHodler.profileVefity, viewHodler.profileName, viewHodler.statusTime, viewHodler.statusFrom);
            StatusFillUtil.fillStatusContent(mContext, viewHodler.statusContent, mStatus.text);
            if (mType == TYPE_COMMENT) {
                viewHodler.commentLine.setVisibility(View.VISIBLE);
                viewHodler.repostLine.setVisibility(View.GONE);
            } else {
                viewHodler.repostLine.setVisibility(View.VISIBLE);
                viewHodler.commentLine.setVisibility(View.GONE);
            }
            if (mStatus.retweeted_status == null) {
                viewHodler.imgs.setVisibility(View.VISIBLE);
                viewHodler.retweetLayout.setVisibility(View.GONE);
                StatusFillUtil.fillImageList(mContext, mStatus, viewHodler.imgs);
            } else {
                viewHodler.imgs.setVisibility(View.GONE);
                viewHodler.retweetLayout.setVisibility(View.VISIBLE);
                StatusFillUtil.fillRetweetContent(mContext, mStatus, viewHodler.retweetContent);
                StatusFillUtil.fillImageList(mContext, mStatus.retweeted_status, viewHodler.retweetImgs);
            }
        }
        if (type == TYPE_NORMAL) {
            ItemViewHodler viewHolder = (ItemViewHodler)holder;
            if (mType == TYPE_COMMENT) {
                Comment comment = mCommentData.get(getRealPosition(position));
                StatusFillUtil.fillCommentOrRepost(mContext, comment.user, comment.created_at, comment.text, viewHolder.image, viewHolder.profileName, viewHolder.time, viewHolder.content);
            } else {
                Repost repost = mRepostData.get(getRealPosition(position));
                StatusFillUtil.fillCommentOrRepost(mContext, repost.getUser(), repost.getCreate_at(), repost.getText(), viewHolder.image, viewHolder.profileName, viewHolder.time, viewHolder.content);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && mFooter != null) {
            return TYPE_FOOTER;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (mType == TYPE_COMMENT) {
            return (mFooter == null ? 0 : 1) + mCommentData.size() + 1;
        } else {
            return (mFooter == null ? 0 : 1) + mRepostData.size() + 1;
        }
    }

    class ItemViewHodler extends RecyclerView.ViewHolder {

        public ImageView image;

        public TextView profileName;

        public TextView time;

        public TextView content;

        public ItemViewHodler(final View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.profile_img);
            profileName = (TextView) itemView.findViewById(R.id.profile_name);
            time = (TextView) itemView.findViewById(R.id.time);
            content = (TextView) itemView.findViewById(R.id.content);

        }
    }

    class HeaderViewHodler extends RecyclerView.ViewHolder {

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
        @BindView(R.id.status_imgs)
        public RecyclerView imgs;

        @BindView(R.id.retweetStatus_layout)
        public LinearLayout retweetLayout;
        @BindView(R.id.retweet_contentWithName)
        public TextView retweetContent;
        @BindView(R.id.retweet_imageList)
        public RecyclerView retweetImgs;

        @BindView(R.id.repost)
        public TextView repost;
        @BindView(R.id.comment)
        public TextView comment;
        @BindView(R.id.like_count)
        public TextView likeCount;

        @BindView(R.id.comment_line)
        public View commentLine;
        @BindView(R.id.repost_line)
        public View repostLine;

        public HeaderViewHodler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            repost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentLine.setVisibility(View.GONE);
                    repostLine.setVisibility(View.VISIBLE);
                    RepostOrCommentEvent event = new RepostOrCommentEvent();
                    event.setType(TYPE_REPOST);
                    EventBus.getDefault().post(event);
                    mType = TYPE_REPOST;
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentLine.setVisibility(View.VISIBLE);
                    repostLine.setVisibility(View.GONE);
                    RepostOrCommentEvent event = new RepostOrCommentEvent();
                    event.setType(TYPE_COMMENT);
                    EventBus.getDefault().post(event);
                    mType = TYPE_COMMENT;
                }
            });
        }
    }
    class FooterViewHodler extends RecyclerView.ViewHolder {

        public FooterViewHodler(final View itemView) {
            super(itemView);
        }
    }

    private int getRealPosition(int position) {
        return position - 1;
    }
}
