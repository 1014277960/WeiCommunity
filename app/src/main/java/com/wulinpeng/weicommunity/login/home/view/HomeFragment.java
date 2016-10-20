package com.wulinpeng.weicommunity.login.home.view;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.wulinpeng.weicommunity.Constans;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.adapter.GroupAdapter;
import com.wulinpeng.weicommunity.login.home.adapter.StatusListAdapter;
import com.wulinpeng.weicommunity.login.home.event.BarStatusEvent;
import com.wulinpeng.weicommunity.login.home.event.GroupItemClickEvent;
import com.wulinpeng.weicommunity.login.home.view.custom.GroupWindow;
import com.wulinpeng.weicommunity.login.home.view.custom.LoadToast;
import com.wulinpeng.weicommunity.mvp.entity.StatusList;
import com.wulinpeng.weicommunity.mvp.presenter.IHomeFragmentPresenter;
import com.wulinpeng.weicommunity.mvp.presenter.imp.HomeFragmentPresenter;
import com.wulinpeng.weicommunity.mvp.view.IHomeFragmentView;
import com.wulinpeng.weicommunity.util.AccessTokenKeeper;
import com.wulinpeng.weicommunity.util.ScreenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wulinpeng.weicommunity.mvp.model.IStatusListModel.TYPE_BILATERAL;
import static com.wulinpeng.weicommunity.mvp.model.IStatusListModel.TYPE_FRIEND_CIRCLE;

/**
 * @author wulinpeng
 * @datetime: 16/10/7 下午8:03
 * @description:
 */
public class HomeFragment extends Fragment implements IHomeFragmentView {

    private View mView;

    @BindView(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;

    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    private List<Status> mData;

    private StatusListAdapter mAdapter;

    @BindView(R.id.name)
    public TextView mName;

    @BindView(R.id.tool_bar)
    public RelativeLayout mToolbar;

    @BindView(R.id.group)
    public LinearLayout mGroup;

    @BindView(R.id.error_page)
    public RelativeLayout mEmptyPage;

    private GroupWindow mGroupWindow;

    private IHomeFragmentPresenter mPresenter;

    private boolean isTopBarShow = true;

    private int mCurrentGroup = TYPE_FRIEND_CIRCLE;

    private LoadToast mToast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_home_fragment, container, false);
        ButterKnife.bind(this, mView);
        EventBus.getDefault().register(this);

        initGroupWindow();
        initRefreshLayout();
        initRecyclerView();

        mPresenter = new HomeFragmentPresenter(this);
        mPresenter.updateUserName(getContext());
        //testData();
        mPresenter.firstLoadStatus(getContext(), false);

        return mView;
    }

    private void testData() {
        StatusesAPI api = new StatusesAPI(getContext(), Constans.APP_KEY, AccessTokenKeeper.getAccessToken(getContext()));
        api.friendsTimeline(0, 0, 6, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                StatusList list = StatusList.parse(s);
                mData.clear();
                mData.addAll(list.statusList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
    }

    private void initGroupWindow() {
        mGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroupWindow == null) {
                    mGroupWindow = GroupWindow.getInstance(getContext(), ScreenUtil.getScreenWidth(getContext()) * 3 / 5, ScreenUtil.getScreenHeight(getContext()) * 2 / 3);
                }
                int offX = (mGroupWindow.getWidth() - mGroup.getMeasuredWidth()) / 2 * -1;
                mGroupWindow.showAsDropDown(mGroup, offX, 0);
            }
        });
    }

    private void initRefreshLayout() {
        // FrameLayout布局,被toolbar遮住,所以向下偏移
        mRefreshLayout.setProgressViewOffset(false, 30, 225);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshStatus(getContext(), mCurrentGroup);
            }
        });
    }

    private void initRecyclerView() {
        mData = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StatusListAdapter(getContext(), mData, mRecyclerView);
        // 添加header
        mAdapter.setHeader(LayoutInflater.from(getContext()).inflate(R.layout.search_view_layout, null, false));

        mRecyclerView.setAdapter(mAdapter);
        // 设置item间距
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if(parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = 30;
                }
            }
        });

        setAdapterListener();
    }

    private void setAdapterListener() {
        mAdapter.setHeaderClickListener(new StatusListAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick() {
                // 打开搜索界面
                Log.d("Debug", "search");
            }
        });

        mAdapter.setLoadMoreListener(new StatusListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(View footView) {
                // more
                mPresenter.loadMoreStatus(getContext(), mCurrentGroup);
            }
        });
    }

    @Subscribe
    public void onTopBarStatusChange(BarStatusEvent event) {
        if (isTopBarShow && !event.isTopBarShow()) {
            setTopbarState(false);
            return;
        }
        if (!isTopBarShow && event.isTopBarShow()) {
            setTopbarState(true);
            return;
        }
    }

    private void setTopbarState(boolean state) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mToolbar, "translationY", state ? 0 : -mToolbar.getMeasuredHeight());
        animator.setDuration(300);
        animator.start();
        isTopBarShow = state;
    }

    @Subscribe
    public void onGroupItemClick(GroupItemClickEvent event) {
        // 刷新数据
        // ...
        Log.d("Debug", event.getName());
        if (event.getType() == GroupAdapter.TYPE_FIRST) {
            mCurrentGroup = TYPE_FRIEND_CIRCLE;
            mPresenter.refreshStatus(getContext(), mCurrentGroup);
        } else if (event.getType() == GroupAdapter.TYPE_SECOND) {
            mCurrentGroup = TYPE_BILATERAL;
            mPresenter.refreshStatus(getContext(), mCurrentGroup);
        }
        mGroupWindow.dismiss();
    }

    @Override
    public void showLoading(boolean loading) {
        mRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void setUsername(String username) {
        mName.setText(username);
    }

    @Override
    public void setTopGroupName(String userName) {

    }

    @Override
    public void showEmptyError(boolean state) {
        if (state == true) {
            mRefreshLayout.setVisibility(View.INVISIBLE);
            mEmptyPage.setVisibility(View.VISIBLE);
        } else {
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyPage.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 滑动到top,如果true就刷新数据
     * @param refresh
     */
    @Override
    public void scrollToTop(boolean refresh) {
        // 数据太多直接跳到顶部
        if (mLayoutManager.findFirstVisibleItemPosition() >= 10) {
            mRecyclerView.scrollToPosition(0);
        } else {
            mRecyclerView.smoothScrollToPosition(0);
        }
        if (refresh) {
            // refresh
            mPresenter.refreshStatus(getContext(), mCurrentGroup);
        }
    }

    @Override
    public void update(List<Status> data) {
        mData.clear();
        mData.addAll(data);
        mAdapter.setLoading(false);
        mAdapter.setFooter(null);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadToast(String message) {
        if (mToast == null) {
            mToast = new LoadToast(getContext());
        }
        mToast.show(message);
    }

    @Override
    public void showLoadingFooter() {
        // 设置刷新进度条
        mAdapter.setFooter(LayoutInflater.from(getContext()).inflate(R.layout.loading_circle_layout, null, false));
    }

    @Override
    public void showNoMoreFooter() {
        mAdapter.setLoading(false);
        mAdapter.setFooter(LayoutInflater.from(getContext()).inflate(R.layout.load_no_more_layout, null, false));
        mAdapter.notifyDataSetChanged();
    }
}
