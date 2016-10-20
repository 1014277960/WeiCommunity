package com.wulinpeng.weicommunity.login.home.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.util.ImageUtil;


/**
 * @author wulinpeng
 * @datetime: 16/10/19 下午9:57
 * @description:
 */
public class ImageMoreWindow extends PopupWindow {

    private Context mContext;

    private String mUrl;

    private TextView mSave;

    private TextView mRepost;

    private TextView mCancel;

    public ImageMoreWindow(Context context, String url) {
        mContext = context;
        mUrl = url;
        init();
        setupListener();
    }

    private void init() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.imageMoreAnim);

        View view = LayoutInflater.from(mContext).inflate(R.layout.image_more_pop_layout, null);
        setContentView(view);

        mSave = (TextView) view.findViewById(R.id.save);
        mRepost = (TextView) view.findViewById(R.id.repost);
        mCancel = (TextView) view.findViewById(R.id.cancel);
    }

    private void setupListener() {
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Glide.with(mContext).load(ImageUtil.getScaleImageUrl(mUrl, ImageUtil.IMG_TYPE_MIDDLE)).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ImageUtil.saveBitmap(mContext, resource);
                        // 同步保存,需要时间,完成后通知
                        Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mRepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
