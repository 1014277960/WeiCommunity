package com.wulinpeng.weicommunity.login.home.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

import butterknife.BindView;

/**
 * @author wulinpeng
 * @datetime: 16/10/18 下午7:01
 * @description: 用于展示图片长宽比大于屏幕长宽比的,这样的图片想要宽度填满高度必定有一部分无法展示,该view可以滑动
 */
public class LongImageView extends View {

    private Bitmap mBitmap;

    private Matrix mMatrix;

    /**
     * 宽度占满的比例
     */
    private float mScaleRatio = -1;

    /**
     * 总的滑动举例
     */
    private float mTranslationY = 0;

    /**
     * 上一次的x
     */
    private float mLastX;

    /**
     * 上一次的y
     */
    private float mLastY;

    public LongImageView(Context context) {
        this(context, null);
    }

    public LongImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatrix = new Matrix();
    }

    public void setDrawable(Drawable drawable) {
        mBitmap = getBitmap(drawable);
        invalidate();
    }

    /**
     * 转换bitmap
     * @param drawable
     * @return
     */
    private Bitmap getBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = null;
        /*
        try {
            // 如果oom了,那就很尴尬了
            bitmap = Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            Log.d("Debug", "long oom");
        }
       */
        bitmap = Bitmap.createBitmap(width, height, config);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    /**
     * 每次绘制重置mMatrix
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            if (mScaleRatio == -1) {
                // 未进行过赋值
                mScaleRatio = getMeasuredWidth() * 1.0f / mBitmap.getWidth();
            }
            mMatrix.reset();
            mMatrix.preScale(mScaleRatio, mScaleRatio);
            mMatrix.postTranslate(0, mTranslationY);
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }

    /**
     * long img太大了,及时recycle
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        System.gc();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (mBitmap != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = event.getX();
                    mLastY = event.getY();
                    // 返回true保证下一次的动作还会传递过来
                    result = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    if (Math.abs(x - mLastX) * 2 < Math.abs(y - mLastY)) {
                        result = true;
                        // 动作是向上滑动,阻止parent截断接下来的动作,接下来的所有事件(DOWN之前)都会传递过来(锁定),jviewpager无法滑动
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        result = false;
                    }
                    // 更新变量
                    mTranslationY += y - mLastY;
                    // 保证图片上下界
                    if (mTranslationY > 0) {
                        mTranslationY = 0;
                    } else if (-mTranslationY + getMeasuredHeight() >= mBitmap.getHeight() * mScaleRatio) {
                        mTranslationY = getMeasuredHeight() - mBitmap.getHeight() * mScaleRatio;
                    }
                    mLastX = x;
                    mLastY = y;
                    invalidate();
                    break;
            }
        }
        return result;
    }
}
