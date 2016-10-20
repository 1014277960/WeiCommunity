package com.wulinpeng.weicommunity.login.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.view.ImageListActivity;
import com.wulinpeng.weicommunity.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/15 下午11:17
 * @description:
 */
public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private LayoutInflater inflater;

    private List<String> data;

    public ImageListAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.status_img_layout, parent, false);
        return new ImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ImgViewHolder viewHolder = (ImgViewHolder) holder;
        final String url = ImageUtil.getScaleImageUrl(data.get(position), ImageUtil.IMG_TYPE_MIDDLE);

        viewHolder.imageView.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().override(viewHolder.imageView.getWidth(), viewHolder.imageView.getHeight())
                        .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                        viewHolder.imageView.setImageDrawable(resource);

                        int type = ImageUtil.getImageType(context, url, resource);
                        if (type == ImageUtil.IMG_TYPE_LONG) {
                            viewHolder.imageType.setVisibility(View.VISIBLE);
                            viewHolder.imageType.setText("长图");
                        } else if (type == ImageUtil.IMG_TYPE_GIF) {
                            viewHolder.imageType.setVisibility(View.VISIBLE);
                            viewHolder.imageType.setText("GIF");
                        } else {
                        }
                    }
                });
            }
        });
        //Glide.with(context).load(url).centerCrop().into(viewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ImgViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public TextView imageType;

        public ImgViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            imageType = (TextView) itemView.findViewById(R.id.img_type);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageListActivity.class);
                    intent.putExtra("urls", (ArrayList<String>) data);
                    intent.putExtra("position", getLayoutPosition());
                    context.startActivity(intent);
                }
            });
        }
    }
}
