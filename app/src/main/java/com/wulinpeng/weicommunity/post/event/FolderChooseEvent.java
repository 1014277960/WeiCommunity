package com.wulinpeng.weicommunity.post.event;

import com.wulinpeng.weicommunity.post.entity.ImageFolder;

/**
 * @author wulinpeng
 * @datetime: 16/10/23 下午6:55
 * @description:
 */
public class FolderChooseEvent {

    private ImageFolder folder;

    public ImageFolder getFolder() {
        return folder;
    }

    public void setFolder(ImageFolder folder) {
        this.folder = folder;
    }
}
