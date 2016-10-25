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

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/22 下午3:39
 * @description:
 */
public class UpLoadImgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private LayoutInflater inflater;

    private List<ImageInfo> data;

    public UpLoadImgAdapter(Context context, List<ImageInfo> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_image_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        Glide.with(context).load(data.get(position).getPath()).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ImageView deleteImg;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            deleteImg = (ImageView) itemView.findViewById(R.id.delete);
            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                }
            });
        }
    }
}
