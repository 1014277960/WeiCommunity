package com.wulinpeng.weicommunity.unlogin.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wulinpeng.weicommunity.R;

/**
 * @author wulinpeng
 * @datetime: 16/10/6 下午10:03
 * @description:
 */
public class MessageFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.unlogin_message_fragment, container, false);
        return view;
    }
}
