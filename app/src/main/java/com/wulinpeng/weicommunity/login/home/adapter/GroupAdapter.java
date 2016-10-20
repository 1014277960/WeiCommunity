package com.wulinpeng.weicommunity.login.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Group;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.event.GroupItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/12 上午11:51
 * @description:
 */
public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FIRST = 0;
    public static final int TYPE_SECOND = 1;
    public static final int TYPE_GROUP = 2;

    private List<Group> mData;

    private Context mContext;

    private LayoutInflater mInflater;

    private int selectedIndex = 0;

    public GroupAdapter(Context context, List<Group> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_FIRST:
                view = mInflater.inflate(R.layout.group_first_layout, parent, false);
                return new FirstViewHolder(view);

            case TYPE_SECOND:
                view = mInflater.inflate(R.layout.group_second_layout, parent, false);
                return new SecondViewHolder(view);

            case TYPE_GROUP:
                view = mInflater.inflate(R.layout.group_item_layout, parent, false);
                return new GroupViewHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_FIRST:
                FirstViewHolder firstViewHolder = (FirstViewHolder) holder;
                if (position == selectedIndex) {
                    firstViewHolder.groupAll.setSelected(true);
                } else {
                    firstViewHolder.groupAll.setSelected(false);
                }
                break;

            case TYPE_SECOND:
                SecondViewHolder secondViewHolder = (SecondViewHolder) holder;
                if (position == selectedIndex) {
                    secondViewHolder.groupFriend.setSelected(true);
                } else {
                    secondViewHolder.groupFriend.setSelected(false);
                }
                break;

            case TYPE_GROUP:
                GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
                groupViewHolder.groupItem.setText(mData.get(position).name);
                if (position == selectedIndex) {
                    groupViewHolder.groupItem.setSelected(true);
                } else {
                    groupViewHolder.groupItem.setSelected(false);
                }
                break;
            default:
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FIRST;
        } else if (position == 1) {
            return TYPE_SECOND;
        } else {
            return TYPE_GROUP;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void selectGroup(int layoutPosition) {
        selectedIndex = layoutPosition;
        notifyDataSetChanged();
    }

    class FirstViewHolder extends RecyclerView.ViewHolder {

        public TextView groupAll;

        public FirstViewHolder(View itemView) {
            super(itemView);
            groupAll = (TextView) itemView.findViewById(R.id.group_all);
            groupAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupItemClickEvent event = new GroupItemClickEvent(getLayoutPosition(), TYPE_FIRST, "全部");
                    EventBus.getDefault().post(event);
                    selectGroup(getLayoutPosition());
                }
            });
        }
    }

    class SecondViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout groupFriend;

        public SecondViewHolder(View itemView) {
            super(itemView);
            groupFriend = (LinearLayout) itemView.findViewById(R.id.group_friend);
            groupFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupItemClickEvent event = new GroupItemClickEvent(getLayoutPosition(), TYPE_SECOND, "朋友圈");
                    EventBus.getDefault().post(event);
                    selectGroup(getLayoutPosition());
                }
            });
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        public TextView groupItem;

        public GroupViewHolder(View itemView) {
            super(itemView);
            groupItem = (TextView) itemView.findViewById(R.id.group_item);
            groupItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupItemClickEvent event = new GroupItemClickEvent(getLayoutPosition(), TYPE_GROUP, mData.get(getLayoutPosition()).name);
                    EventBus.getDefault().post(event);
                    selectGroup(getLayoutPosition());
                }
            });
        }
    }
}
