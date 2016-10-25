package com.wulinpeng.weicommunity.post.entity;

import java.util.List;

/**
 * @author wulinpeng
 * @datetime: 16/10/23 下午3:45
 * @description:
 */
public class ImageFolder {

    private String name;

    private List<ImageInfo> images;

    public List<ImageInfo> getImages() {
        return images;
    }

    public void setImages(List<ImageInfo> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
