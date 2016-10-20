package com.wulinpeng.weicommunity.login.home.event;

/**
 * @author wulinpeng
 * @datetime: 16/10/15 下午7:40
 * @description:
 */
public class BarStatusEvent {

    private boolean topBarShow;

    private boolean bottomBarShow;

    public boolean isBottomBarShow() {
        return bottomBarShow;
    }

    public void setBottomBarShow(boolean bottomBarShow) {
        this.bottomBarShow = bottomBarShow;
    }

    public boolean isTopBarShow() {
        return topBarShow;
    }

    public void setTopBarShow(boolean topBarShow) {
        this.topBarShow = topBarShow;
    }
}
