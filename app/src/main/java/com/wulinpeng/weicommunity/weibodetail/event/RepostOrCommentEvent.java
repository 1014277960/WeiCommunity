package com.wulinpeng.weicommunity.weibodetail.event;

import static com.wulinpeng.weicommunity.weibodetail.adapter.WeiboDetailAdapter.TYPE_COMMENT;

/**
 * @author wulinpeng
 * @datetime: 16/10/24 下午11:49
 * @description:
 */
public class RepostOrCommentEvent {

    private int type = TYPE_COMMENT;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
