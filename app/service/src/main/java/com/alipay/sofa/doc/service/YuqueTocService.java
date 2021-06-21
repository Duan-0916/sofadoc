package com.alipay.sofa.doc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;
import com.alipay.sofa.doc.utils.YuqueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alipay.sofa.doc.utils.StringUtils.trimToEmpty;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class YuqueTocService {

    public static final Logger LOGGER = LoggerFactory.getLogger(YuqueTocService.class);

    public int removeAll(YuqueClient client, String namespace) {
        Assert.notNull(client, "client is null");
        LOGGER.info("Remove toc of {} start.", namespace);
        String url = "/repos/" + namespace + "/toc";
        String json = client.get(url);
        JSONObject res = JSONObject.parseObject(json);
        JSONArray data = res.getJSONArray("data");
        for (Object datum : data) {
            JSONObject menuItem = (JSONObject) datum;
            if (menuItem.getInteger("level") == 0) {
                // 可以减少调用次数
                removeWithChildren(client, namespace, menuItem.getString("uuid"));
            }
        }
        LOGGER.info("Remove toc of {} end, {} items deleted", namespace, data.size());
        return data.size();
    }

    /**
     * 删除节点以及其子节点
     *
     * @param namespace 命名空间
     * @param nodeUuid  目标节点的 uuid
     */
    public void removeWithChildren(YuqueClient client, String namespace, String nodeUuid) {
        Assert.notNull(client, "client is null");
        String url = "/repos/" + namespace + "/toc";

        Map<String, String> map = new HashMap<>();
        map.put("action", "removeWithChildren");
        map.put("node_uuid", nodeUuid);
        client.put(url, null, JSON.toJSONString(map));
    }

    /**
     * 单独插入一个子节点
     *
     * @param namespace      文档库命名空间
     * @param menuItem       要添加的目标节点
     * @param parentMenuItem 父节点
     * @param lastUuid       上一次操作的节点，用于判断是否是第一个
     * @return uuid
     */
    public String insert(YuqueClient client, String namespace, MenuItem menuItem, MenuItem parentMenuItem, String lastUuid) {
        Assert.notNull(client, "client is null!");
        Assert.notNull(parentMenuItem, "parent Menu Item is null!");
        String url = "/repos/" + namespace + "/toc";
        Map<String, String> map = new HashMap<>();
        if (lastUuid == null) { // 创建第一个子节点
            map.put("action", "insert");
            if (parentMenuItem.getUuid() != null) {
                map.put("target_uuid", parentMenuItem.getUuid());
            }
        } else { // 平级增加子节点
            map.put("action", "insertSibling");
            map.put("target_uuid", lastUuid);
        }
        map.put("title", menuItem.getTitle());
        map.put("url", menuItem.getUrl());
        map.put("type", menuItem.getType());
        String res = client.put(url, null, JSON.toJSONString(map));
        String uuid = queryUuid(res, menuItem);
        menuItem.setUuid(uuid);
        return uuid;
    }


    /**
     * 插入一个节点及其所有子节点
     *
     * @param namespace      文档库命名空间
     * @param menuItem       要添加的目标节点
     * @param parentMenuItem 父节点
     * @param lastUuid       上一次操作的节点，用于判断是否是第一个
     * @return
     */
    public String insertWithChild(YuqueClient client, String namespace, MenuItem menuItem, MenuItem parentMenuItem, String lastUuid) {
        Assert.notNull(client, "client is null");
        // 新建一行
        insert(client, namespace, menuItem, parentMenuItem, lastUuid);
        LOGGER.info("insert menu item: {}, {}, parent:{}", menuItem.getTitle(), menuItem.getUrl(), parentMenuItem.getTitle());

        // 然后遍历子目录
        List<MenuItem> subs = menuItem.getSubMenuItems();
        String lastSubUuid = null;
        if (!subs.isEmpty()) {
            for (int i = 0; i < subs.size(); i++) {
                //for (int i = subs.size() - 1; i >= 0; i--) {
                MenuItem subMenuItem = subs.get(i);
                lastSubUuid = insertWithChild(client, namespace, subMenuItem, menuItem, lastSubUuid);
            }
        }
        return menuItem.getUuid();
    }

    /**
     * 同步整个目录，先删后增
     *
     * @param repo
     * @param toc
     */
    public void syncToc(YuqueClient client, Repo repo, TOC toc) {
        Assert.notNull(client, "client is null");
        Assert.notNull(toc, "toc is null");
        removeAll(client, repo.getNamespace());

        List<MenuItem> subs = toc.getSubMenuItems();
        String lastUuid = null;
        for (int i = 0; i < subs.size(); i++) {
            //for (int i = subs.size() - 1; i >= 0; i--) {
            // 一级目录
            MenuItem item = subs.get(i);
            lastUuid = insertWithChild(client, repo.getNamespace(), item, new MenuItem(), lastUuid);
        }
    }

    /**
     * 由于每次请求的返回结果是整个 toc，所以从返回结果中遍历查找中上一次操作的 uuid
     *
     * @param tocJson
     * @param item    要匹配的目录节点，需要title+url+type都匹配
     * @return 对应的 uuid
     */
    private String queryUuid(String tocJson, MenuItem item) {
        Assert.notNull(item, "item is null!");

        JSONObject obj = (JSONObject) JSONObject.parse(tocJson);
        JSONArray data = obj.getJSONArray("data");
        for (Object datum : data) {
            JSONObject mi = (JSONObject) datum;
            if (trimToEmpty(mi.getString("title")).equals(trimToEmpty(item.getTitle()))
                    && trimToEmpty(mi.getString("type")).equals(trimToEmpty(item.getType()))
                    && trimToEmpty(mi.getString("url")).equals(trimToEmpty(item.getUrl()))) {
                return mi.getString("uuid");
            }
        }
        throw new RuntimeException("Failed to query last uuid from result: " +
                item.getTitle() + "/" + item.getUrl() + "/" + item.getType());
    }
}
