package com.wulinpeng.weicommunity.login.home.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author wulinpeng
 * @datetime: 16/10/16 下午2:33
 * @description: 自动适应高度的图片,因为在recyclerview中,宽度是固定的,我们设为match_parent,这时候高度手动改为和宽度一样
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
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 4 / 5);
    }
}
