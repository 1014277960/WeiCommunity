package com.wulinpeng.weicommunity;

import com.wulinpeng.weicommunity.util.SDCardUtil;

/**
 * @author wulinpeng
 * @datetime: 16/10/6 下午7:40
 * @description:
 */
public class Constans {
    public static final String APP_KEY = "2542483675";
    public static final String APP_SECRET = "7bd3a5976b52b5eee19a38a2945d6fce";
    public static final String REDIRECT_URL = "http://www.baidu.com";
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    public static final String CACHE_PATH = SDCardUtil.getSDCardPath() + "/WeiCommunity";
}
