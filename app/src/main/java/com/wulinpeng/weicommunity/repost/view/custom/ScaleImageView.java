package com.wulinpeng.weicommunity.repost.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author wulinpeng
 * @datetime: 16/10/23 下午4:39
 * @description:
 */
public class ScaleImageView extends ImageView {
    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
