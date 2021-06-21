package com.alipay.sofa.doc.model;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    /*
    TITLE 分组
    DOC 本知识库文档
    LINK 链接
     */
    private String type;
    private String title;
    private String url;
    private String uuid;
    private String slug;
    private List<MenuItem> subMenuItems = new ArrayList<>();
    private MenuItem parentMenuItem;
    private transient int ltrim = 0;

    public String getType() {
        return type;
    }

    public MenuItem setType(String type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MenuItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MenuItem setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public MenuItem setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public MenuItem setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public MenuItem getParentMenuItem() {
        return parentMenuItem;
    }

    public MenuItem setParentMenuItem(MenuItem parentMenuItem) {
        this.parentMenuItem = parentMenuItem;
        return this;
    }

    public List<MenuItem> getSubMenuItems() {
        return subMenuItems;
    }

    public MenuItem setSubMenuItems(List<MenuItem> subMenuItems) {
        this.subMenuItems = subMenuItems;
        return this;
    }

    public int getLtrim() {
        return ltrim;
    }

    public MenuItem setLtrim(int ltrim) {
        this.ltrim = ltrim;
        return this;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "title='" + title + '\'' +
                ", subMenuItems=" + subMenuItems +
                ", ltrim=" + ltrim +
                '}';
    }
}
