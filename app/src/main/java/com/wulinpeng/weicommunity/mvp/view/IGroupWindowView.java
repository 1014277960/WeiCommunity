package com.wulinpeng.weicommunity.mvp.view;

import com.sina.weibo.sdk.openapi.models.Group;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/8 下午10:00
 * @description:
 */
public interface IGroupWindowView {

    public void updateGroup(List<Group> groups);

    public void showError(String error);
}
