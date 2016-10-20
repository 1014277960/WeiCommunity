package com.wulinpeng.weicommunity.mvp.presenter.imp;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.Group;
import com.wulinpeng.weicommunity.mvp.model.IGroupModel;
import com.wulinpeng.weicommunity.mvp.model.imp.GroupModel;
import com.wulinpeng.weicommunity.mvp.presenter.IGroupWindowPresenter;
import com.wulinpeng.weicommunity.mvp.view.IGroupWindowView;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/8 下午10:03
 * @description:
 */
public class GroupWindowPresenter implements IGroupWindowPresenter {

    private IGroupWindowView rootView;

    private GroupModel model;

    public GroupWindowPresenter(IGroupWindowView rootView) {
        this.rootView = rootView;
        model = new GroupModel();
    }

    @Override
    public void updateGroup(Context context) {
        model.getGroups(context, new IGroupModel.OnGroupFinishListener() {
            @Override
            public void onGroupFinish(List<Group> data) {
                rootView.updateGroup(data);
            }

            @Override
            public void onError(String error) {
                rootView.showError(error);
            }
        });
    }
}
