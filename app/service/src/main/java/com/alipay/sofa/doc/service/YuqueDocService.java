package com.alipay.sofa.doc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.doc.model.Doc;
import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import com.alipay.sofa.doc.utils.YuqueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class YuqueDocService {

    public static final Logger LOGGER = LoggerFactory.getLogger(YuqueDocService.class);

    /**
     * 同步整个目录对应的文档
     *
     * @param client YuqueClient
     * @param repo   仓库对象
     * @param toc    目录对象
     */
    public void syncDocs(YuqueClient client, Repo repo, TOC toc) {
        List<MenuItem> subs = toc.getSubMenuItems();
        for (int i = 0; i < subs.size(); i++) {
            //for (int i = subs.size() - 1; i >= 0; i--) {
            // 一级目录
            MenuItem item = subs.get(i);
            syncWithChild(client, repo, toc, item);
        }
    }

    /**
     * 同步一个文章
     *
     * @param client   YuqueClient
     * @param toc      目标对象
     * @param menuItem 目录节点
     */
    public void sync(YuqueClient client, Repo repo, TOC toc, MenuItem menuItem) {

        Assert.notNull(client, "client is null");
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");
        String namespace = repo.getNamespace();
        Assert.notNull(namespace, "namespace is null");
        if (!"DOC".equals(menuItem.getType())) {
            LOGGER.info(" type is not DOC, continue.");
            return;
        }

        String newContent = getContent(repo, menuItem.getUrl(), menuItem.getTitle());

        String url = menuItem.getUrl();
        // 拼接，放回
        String slug = getSlug(url);

        Doc doc = query(client, namespace, slug);
        if (doc == null) { // 新增
            doc = new Doc();
            doc.setTitle(menuItem.getTitle());
            doc.setFormat("markdown");
            doc.setBody(newContent);
            doc.setSlug(slug);
            insert(client, namespace, doc);
        } else { // 更新
            doc.setFormat("markdown");
            doc.setBody(newContent);
            update(client, namespace, doc);
        }

        // 重要：设置回菜单列表
        menuItem.setUrl(slug);
    }

    /**
     * 插入一个节点及其所有子节点
     *
     * @param repo     文档仓库
     * @param toc      目录
     * @param menuItem 要添加的目标节点
     */
    public void syncWithChild(YuqueClient client, Repo repo, TOC toc, MenuItem menuItem) {
        Assert.notNull(client, "client is null");
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");
        Assert.notNull(repo.getNamespace(), "namespace is null");
        // 新建一行
        LOGGER.info("sync menu item: {}, {}", menuItem.getTitle(), menuItem.getUrl());
        sync(client, repo, toc, menuItem);

        // 然后遍历子目录
        List<MenuItem> subs = menuItem.getSubMenuItems();
        if (!subs.isEmpty()) {
            for (int i = 0; i < subs.size(); i++) {
                //for (int i = subs.size() - 1; i >= 0; i--) {
                MenuItem subMenuItem = subs.get(i);
                syncWithChild(client, repo, toc, subMenuItem);
            }
        }
    }

    /**
     * @param namespace repo 仓库
     * @param slug      唯一标识
     * @return 文档对象
     */
    public Doc query(YuqueClient client, String namespace, String slug) {
        Assert.notNull(client, "client is null");
        Assert.notNull(namespace, "namespace is null");
        Assert.notNull(slug, "slug is null");
        String url = "/repos/" + namespace + "/docs/" + slug;
        String json = client.get(url);
        JSONObject res = JSONObject.parseObject(json);
        JSONObject data = res.getJSONObject("data");
        return data == null ? null : json2Doc(data);
    }

    /**
     * 新增文档
     *
     * @param namespace repo 仓库
     * @param doc       文档对象
     */
    public void insert(YuqueClient client, String namespace, Doc doc) {
        Assert.notNull(client, "client is null");
        Assert.notNull(namespace, "namespace is null");
        Assert.notNull(doc, "doc is null");
        LOGGER.info("  insert doc: {}, {}", doc.getSlug(), doc.getTitle());

        String url = "/repos/" + namespace + "/docs";
        Map<String, String> map = new HashMap<>();
        map.put("title", doc.getTitle());
        map.put("slug", doc.getSlug());
        map.put("format", doc.getFormat());
        map.put("body", doc.getBody());

        String json = client.post(url, null, JSON.toJSONString(map));
        JSONObject res = JSONObject.parseObject(json);
        JSONObject data = res.getJSONObject("data");
        if (data == null) {
            // 创建不成功
            throw new RuntimeException("Failed to add doc: " + doc.getTitle() + ", response data is : " + json);
        } else {
            doc.setId(data.getInteger("id"));
        }
    }

    /**
     * 更新文档
     *
     * @param namespace repo 仓库
     * @param doc       文档对象
     */
    public void update(YuqueClient client, String namespace, Doc doc) {
        Assert.notNull(client, "client is null");
        Assert.notNull(namespace, "namespace is null");
        Assert.notNull(doc, "doc is null");
        LOGGER.info("  update doc: {}, {}", doc.getSlug(), doc.getTitle());

        // 强制更新的后面
        String url = "/repos/" + namespace + "/docs/" + doc.getId() + "?_force_asl=true";
        Map<String, Object> map = new HashMap<>();
        map.put("title", doc.getTitle());
        map.put("slug", doc.getSlug());
        map.put("format", doc.getFormat());
        map.put("body", doc.getBody());
        map.put("status", 1);

        String json = client.put(url, null, JSON.toJSONString(map));
        JSONObject res = JSONObject.parseObject(json);
        JSONObject data = res.getJSONObject("data");
        if (data == null) {
            // 更新不成功
            throw new RuntimeException("Failed to add doc: " + doc.getTitle() + ", response data is : " + json);
        }
    }

    protected String getSlug(String url) {
        if (url.startsWith(".")) {
            url = url.substring(1);
        }
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        url = url.replace("/", "-");
        url = url.toLowerCase();
        if (url.endsWith(".md")) {
            url = url.substring(0, url.length() - 3);
        }
        return url;
    }

    protected Doc json2Doc(JSONObject data) {
        Doc doc = new Doc();
        doc.setId(data.getInteger("id"));
        doc.setTitle(data.getString("title"));
        doc.setFormat(data.getString("format"));
        doc.setBody(data.getString("body"));
        doc.setSlug(data.getString("slug"));
        return doc;
    }

    protected String getContent(Repo repo, String filePath, String title) {
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        File file = new File(repo.getLocalPath(), filePath);
        try {
            List<String> lines = FileUtils.readLines(file);
            boolean removeTitle = false;
            StringBuilder content = new StringBuilder(128)
                    .append("[编辑本文档](").append(repo.getGitPath()).append("/edit/master/").append(filePath).append(")    ")
                    .append("[共建有奖](https://yuque.antfin-inc.com/middleware/improveue/ek95gl)")
                    .append("\n\n");
            for (String line : lines) {
                if (!removeTitle) {
                    if (StringUtils.isNotBlank(line)) {
                        if (line.contains("#") && line.contains(title)) {
                            // 第一行和标题重复，删掉
                            removeTitle = true;
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
