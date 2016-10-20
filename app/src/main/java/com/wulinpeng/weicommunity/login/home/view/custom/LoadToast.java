package com.wulinpeng.weicommunity.login.home.view.custom;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wulinpeng.weicommunity.R;

/**
 * @author wulinpeng
 * @datetime: 16/10/17 下午6:31
 * @description:
 */
public class LoadToast extends Toast {

    private TextView textView;

    public LoadToast(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.load_toast_layout, null, false);
        textView = (TextView) view.findViewById(R.id.text);
        setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 45 * 3);
        setDuration(LENGTH_SHORT);
        setView(view);
    }

    public void show(String s) {
        textView.setText(s);
        this.show();
    }
}
