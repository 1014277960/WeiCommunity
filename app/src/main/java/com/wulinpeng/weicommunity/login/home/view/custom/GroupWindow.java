package com.wulinpeng.weicommunity.login.home.view.custom;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.openapi.models.Group;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.adapter.GroupAdapter;
import com.wulinpeng.weicommunity.mvp.presenter.IGroupWindowPresenter;
import com.wulinpeng.weicommunity.mvp.presenter.imp.GroupWindowPresenter;
import com.wulinpeng.weicommunity.mvp.view.IGroupWindowView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/8 下午9:59
 * @description:
 */
public class GroupWindow extends PopupWindow implements IGroupWindowView {

    private static GroupWindow mGroupWindow;

    private IGroupWindowPresenter mPresenter;

    private RecyclerView mRecyclerView;

    private GroupAdapter mAdapter;

    private List<Group> mData;

    private int mWidth;

    private int mHeight;

    private Context mContext;

    private View mView;

    private TextView mEdit;

    public static GroupWindow getInstance(Context context, int width, int height) {
        if (mGroupWindow == null) {
            synchronized (GroupWindow.class) {
                if (mGroupWindow == null) {
                    mGroupWindow = new GroupWindow(context, width, height);
                }
            }
        }
        return mGroupWindow;
    }

    private GroupWindow(Context context, int width, int height) {
        super(context);
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.group_window_layout, null);
        setContentView(mView);
        mWidth = width;
        mHeight = height;
        initGroupWdinow();
        initRecyclerView();
        setupListener();
        mPresenter = new GroupWindowPresenter(this);
        // Groups权限没有获得,暂时没有该功能
        // mPresenter.updateGroup(context);
    }

    private void initGroupWdinow() {
        this.setWidth(mWidth);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mData = new ArrayList<>();
        mData.add(new Group());
        mData.add(new Group());
        mAdapter = new GroupAdapter(mContext, mData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupListener() {
        mEdit = (TextView) mView.findViewById(R.id.group_edit);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "编辑分组", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void updateGroup(List<Group> groups) {
        mData.addAll(groups);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
    }
}
