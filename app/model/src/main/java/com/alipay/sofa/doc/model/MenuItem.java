package com.alipay.sofa.doc.model;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    private MenuItemType type = MenuItemType.OTHER;
    private String title;
    private String url;
    private List<MenuItem> subMenuItems = new ArrayList<>();
    private MenuItem parentMenuItem;
    private int level = 0;
    /**
     * 语雀的 uuid，运行时设置
     */
    private transient String uuid;
    /**
     * 语雀的 slug，运行时设置
     */
    private transient String slug;
    private transient int ltrim = 0;

    public MenuItemType getType() {
        return type;
    }

    public MenuItem setType(MenuItemType type) {
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

    public int getLevel() {
        return level;
    }

    public MenuItem setLevel(int level) {
        this.level = level;
        return this;
    }

    /**
     * 根据路径名获取文件关键字
     *
     * @param url 文件路径，包含文件夹
     * @return 文件关键字
     */
    public String url2Slug(String url) {
        String[] arr = url.toLowerCase().split("/");
        String slug = "";
        for (int i=arr.length - 1; i>=0; i--) {
            StringBuilder sb = new StringBuilder(arr[i].length());
            for(int j=0; j<arr[i].length(); j++) {
                char c = arr[i].charAt(j);
                if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.') {
                    sb.append(c);
                }
            }
            if (sb.length() == 0) {
                continue;
            }
            if (slug.isEmpty()) {
                slug = sb.toString();
                if (slug.endsWith(".md")) {
                    slug = slug.substring(0, slug.length() - 3);
                }
            } else if (sb.length() + slug.length() >= 36) {
                return slug;
            } else {
                slug = sb.toString() + "-" + slug;
            }
        }
        if (slug.length() > 36) {
            slug = slug.substring(0, 36);
        }
        return slug;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "title='" + title + '\'' +
                ", slug=" + slug +
                ", subMenuItems=" + subMenuItems +
                '}';
    }

    public enum MenuItemType {
        /**
         * 本知识库文档
         */
        DOC,
        /**
         * 分组
         */
        TITLE,
        /**
         * 链接
         */
        LINK,
        /**
         * 其它
         */
        OTHER;

        public static MenuItemType parse(String src) {
            try {
                return MenuItemType.valueOf(src);
            } catch (Exception e) {
                return MenuItemType.OTHER;
            }
        }
    }
}
