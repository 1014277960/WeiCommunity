package com.wulinpeng.weicommunity.login.home.event;

import com.wulinpeng.weicommunity.login.home.adapter.GroupAdapter;

/**
 * @author wulinpeng
 * @datetime: 16/10/12 下午10:43
 * @description:
 */
public class GroupItemClickEvent {

    private int position;

    private int type;

    private String name;

    public GroupItemClickEvent(int position, int type, String name) {
        this.name = name;
        this.position = position;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
