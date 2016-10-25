package com.wulinpeng.weicommunity.post.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.post.entity.ImageInfo;
import com.wulinpeng.weicommunity.post.event.ImageSelectChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/23 下午3:10
 * @description:
 */
public class ImgSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ImageInfo> data;

    private Context context;

    private LayoutInflater inflater;

    public ImgSelectAdapter(Context context, List<ImageInfo> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.select_img_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageInfo imageInfo = data.get(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        Glide.with(context).load(imageInfo.getPath()).into(((MyViewHolder) holder).imageView);
        if (imageInfo.isSelected()) {
            viewHolder.selectImg.setImageResource(R.drawable.select_img_circle_light);
        } else {
            viewHolder.selectImg.setImageResource(R.drawable.select_img_circle_normal);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ImageView selectImg;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            selectImg = (ImageView) itemView.findViewById(R.id.select_img);
            selectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.get(getLayoutPosition()).isSelected()) {
                        selectImg.setImageResource(R.drawable.select_img_circle_normal);
                        data.get(getLayoutPosition()).setSelected(false);
                    } else {
                        selectImg.setImageResource(R.drawable.select_img_circle_light);
                        data.get(getLayoutPosition()).setSelected(true);
                    }
                    EventBus.getDefault().post(new ImageSelectChangeEvent());
                }
            });
        }
    }
}
