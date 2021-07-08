package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class TOCChecker {

    /**
     * 日志
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(TOCChecker.class);

    /**
     * 提前检查 Repo 和 Toc
     *
     * @param repo 语雀知识库
     * @param toc 文档
     */
    public void check(Repo repo, TOC toc) {
        List<MenuItem> subs = toc.getSubMenuItems();
        List<String> errors = new ArrayList<>();
        Set<String> slugs = new HashSet<>();
        for (MenuItem item : subs) {
            checkMenuItemWithChild(errors, repo, toc, item, slugs);
        }
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                sb.append(i + 1).append(": ").append(errors.get(i)).append("; ");
            }
            sb.deleteCharAt(sb.length() - 1);
            LOGGER.error("Pre-check error({}): {}", errors.size(), sb);
            throw new RuntimeException("Pre-check error: " + sb);
        }
    }

    /**
     * 插入一个节点及其所有子节点
     *  @param repo     文档仓库
     * @param toc      目录
     * @param menuItem 要添加的目标节点
     * @param slugs 已存在的文章列表
     */
    protected void checkMenuItemWithChild(List<String> errors, Repo repo, TOC toc, MenuItem menuItem, Set<String> slugs) {
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");
        Assert.notNull(repo.getNamespace(), "namespace is null");
        // 检查自己
        checkMenuItem(errors, repo, toc, menuItem, slugs);
        // 然后遍历子目录
        List<MenuItem> subs = menuItem.getSubMenuItems();
        if (!subs.isEmpty()) {
            for (MenuItem subMenuItem : subs) {
                checkMenuItemWithChild(errors, repo, toc, subMenuItem, slugs);
            }
        }
    }

    /**
     * 同步一个文章
     *
     * @param toc      目标对象
     * @param menuItem 目录节点
     */
    protected void checkMenuItem(List<String> errors, Repo repo, TOC toc, MenuItem menuItem, Set<String> slugs) {
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");
        if (!MenuItem.MenuItemType.DOC.equals(menuItem.getType())) {
            return;
        }
        // 检查是否符合格式
        String url = menuItem.getUrl();
        if (url.contains("../") || url.contains("./")) {
            errors.add("[" + url + "] 不能使用 ../ 或者 ./");
            return;
        }
        // 检查文件是否存在
        File file = new File(repo.getLocalPath(), menuItem.getUrl());
        if (!file.exists()) {
            errors.add("[" + url + "]" + file.getAbsolutePath() + " 不存在!");
        }

        String slug = menuItem.url2Slug(url);
        if(slugs.contains(slug)){
            errors.add("[" + url + "]" + "存在同名文件，请检查！");
        } else {
            slugs.add(slug);
        }
        // TODO: 检查其它文件内容
    }
}
