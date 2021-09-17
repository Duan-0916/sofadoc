package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class YuqueSlugGenerator {

    public String url2Slug(String url, Context.SlugGenMode mode) {
        if (mode == Context.SlugGenMode.FILENAME) {
            return url2SlugByFileName(url);
        } else if (mode == Context.SlugGenMode.DIRS_FILENAME) {
            return url2SlugByDirsAndFileName(url);
        } else {
            throw new IllegalArgumentException("Unsupported SlugGenMode: " + mode);
        }
    }

    /**
     * 跟进文件名获取文件关键字 slug
     *
     * @param url
     * @return
     */
    private String url2SlugByFileName(String url) {
        int idx = url.lastIndexOf("/");
        if (idx > -1) {
            url = url.substring(idx + 1);
        }
        url = url.toLowerCase();
        if (url.endsWith(".md")) {
            url = url.substring(0, url.length() - 3);
        }
        return url;
    }

    /**
     * 根据路径名获取文件关键字 slug
     *
     * @param url 文件路径，包含文件夹
     * @return 文件关键字
     */
    private String url2SlugByDirsAndFileName(String url) {
        String[] arr = url.toLowerCase().split("/");
        String slug = "";
        for (int i = arr.length - 1; i >= 0; i--) {
            StringBuilder sb = new StringBuilder(arr[i].length());
            for (int j = 0; j < arr[i].length(); j++) {
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
                slug = sb + "-" + slug;
            }
        }
        if (slug.length() > 36) {
            slug = slug.substring(0, 36);
        }
        return slug;
    }
}
