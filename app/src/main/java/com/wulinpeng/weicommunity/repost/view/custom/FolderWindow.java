package com.wulinpeng.weicommunity.repost.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.repost.entity.ImageFolder;
import com.wulinpeng.weicommunity.repost.event.FolderChooseEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/23 下午5:19
 * @description:
 */
public class FolderWindow extends PopupWindow {

    private Context context;

    private View view;

    private RecyclerView recyclerView;

    private MyAdapter adapter;

    private List<ImageFolder> folders = new ArrayList<>();

    private int currentSelectedIndex = 0;

    public FolderWindow(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.folder_popup_window, null);
        setContentView(view);
        initRecyclerView();
        initWindow();
    }

    private void initWindow() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.GRAY));
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    public void setFolders(List<ImageFolder> folders) {
        this.folders = folders;
        adapter.notifyDataSetChanged();
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.folder_popup_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            Glide.with(context).load(folders.get(position).getImages().get(0).getPath()).into(viewHolder.firstImg);
            viewHolder.folderName.setText(folders.get(position).getName());
            viewHolder.num.setText("(" + folders.get(position).getImages().size() + ")");
            viewHolder.layout.setSelected(currentSelectedIndex == position);
        }

        @Override
        public int getItemCount() {
            return folders.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public LinearLayout layout;

            public ImageView firstImg;

            public TextView folderName;

            public TextView num;

            public MyViewHolder(View itemView) {
                super(itemView);
                firstImg = (ImageView) itemView.findViewById(R.id.first_img);
                folderName = (TextView) itemView.findViewById(R.id.folder_name);
                num = (TextView) itemView.findViewById(R.id.num);
                layout = (LinearLayout) itemView.findViewById(R.id.ll_layout);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FolderChooseEvent event = new FolderChooseEvent();
                        event.setFolder(folders.get(getLayoutPosition()));
                        EventBus.getDefault().post(event);
                        currentSelectedIndex = getLayoutPosition();
                        notifyDataSetChanged();
                        dismiss();
                    }
                });
            }
        }
    }
}
