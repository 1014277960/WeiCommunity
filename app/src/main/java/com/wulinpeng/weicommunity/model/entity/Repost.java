package com.wulinpeng.weicommunity.model.entity;

import com.sina.weibo.sdk.openapi.models.User;

/**
 * @author wulinpeng
 * @datetime: 16/10/25 上午12:43
 * @description:
 */
public class Repost {

    private User user;

    private String create_at;

    private String text;

    private String id;

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
