package com.wulinpeng.weicommunity.login.home.presenter;

import android.content.Context;

import com.sina.weibo.sdk.openapi.models.Group;
import com.wulinpeng.weicommunity.login.home.Contract;
import com.wulinpeng.weicommunity.model.interf.IGroupModel;
import com.wulinpeng.weicommunity.model.imp.GroupModel;

import java.util.List;
/**
 * @author wulinpeng
 * @datetime: 16/10/8 下午10:03
 * @description:
 */
public class GroupWindowPresenter implements Contract.IGroupWindowPresenter {

    private Contract.IGroupWindowView rootView;

    private GroupModel model;

    public GroupWindowPresenter(Contract.IGroupWindowView rootView) {
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
