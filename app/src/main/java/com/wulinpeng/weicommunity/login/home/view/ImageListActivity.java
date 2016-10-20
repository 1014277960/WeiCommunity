package com.wulinpeng.weicommunity.login.home.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.wulinpeng.weicommunity.R;
import com.wulinpeng.weicommunity.login.home.view.custom.ImageMoreWindow;
import com.wulinpeng.weicommunity.login.home.view.custom.LongImageView;
import com.wulinpeng.weicommunity.util.ImageUtil;
import com.wulinpeng.weicommunity.util.ScreenUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author wulinpeng
 * @datetime: 16/10/18 下午4:28
 * @description:
 */
public class ImageListActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;

    @BindView(R.id.index_text)
    public TextView mIndexText;

    @BindView(R.id.more)
    public ImageView mMore;

    private ImageMoreWindow mMoreView;

    private List<String> mUrls;

    private int mCurrentIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);

        mUrls = getIntent().getStringArrayListExtra("urls");
        mCurrentIndex = getIntent().getIntExtra("position", 0);

        setupViewPager();
        setupMoreItem();
    }

    private void setupViewPager() {
        mViewPager.setAdapter(new ViewPagerAdapter());
        mViewPager.setCurrentItem(mCurrentIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mIndexText.setText((position + 1) + "/" + mUrls.size());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupMoreItem() {
        mMoreView = new ImageMoreWindow(this, mUrls.get(mCurrentIndex ));
        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoreView.showAtLocation(mViewPager, Gravity.BOTTOM, 0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mMoreView != null && mMoreView.isShowing()) {
            mMoreView.dismiss();
            return;
        }
        super.onBackPressed();
    }

    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(ImageListActivity.this).inflate( R.layout.image_list_item, null);
            final ImageView imageView = (ImageView) view.findViewById(R.id.img);
            final LongImageView longImageView  = (LongImageView) view.findViewById(R.id.long_img);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            final TextView errorText = (TextView) view.findViewById(R.id.error);

            Glide.with(ImageListActivity.this).load(ImageUtil.getScaleImageUrl(mUrls.get(position), ImageUtil.IMG_TYPE_MIDDLE)).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(final GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    progressBar.setVisibility(View.GONE);

                    if ((resource.getIntrinsicWidth() * 1.0f / resource.getIntrinsicHeight()) <
                            (ScreenUtil.getScreenWidth(ImageListActivity.this) * 1.0f / ScreenUtil.getScreenHeight(ImageListActivity.this))) {
                        // 长图
                        longImageView.setVisibility(View.VISIBLE);
                        longImageView.setDrawable(resource);
                    } else {
                        // 普通图片或者gif
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageDrawable(resource);
                        resource.start();
                    }
                    longImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Debug", resource.getIntrinsicWidth() + " " + longImageView.getWidth());
                        }
                    });
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    progressBar.setVisibility(View.GONE);
                    errorText.setVisibility(View.VISIBLE);
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
