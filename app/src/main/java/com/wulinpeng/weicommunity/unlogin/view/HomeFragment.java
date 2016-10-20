package com.wulinpeng.weicommunity.unlogin.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.wulinpeng.weicommunity.R;

/**
 * @author wulinpeng
 * @datetime: 16/10/6 下午10:02
 * @description:
 */
public class HomeFragment extends Fragment {

    private View view;

    private ImageView circleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.unlogin_home_fragment, container, false);
        circleView = (ImageView) view.findViewById(R.id.img_circle);
        RotateAnimation animation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(20000);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());
        circleView.startAnimation(animation);
        return view;
    }
}
