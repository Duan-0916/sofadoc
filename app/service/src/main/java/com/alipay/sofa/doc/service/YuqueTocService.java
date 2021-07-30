package com.alipay.sofa.doc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;
import com.alipay.sofa.doc.utils.StringUtils;
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

    /**
     * 同步整个目录，先删后增
     *
     * @param repo
     * @param toc
     */
    public void syncToc(YuqueClient client, Repo repo, TOC toc, SyncMode syncTocMode) {
        LOGGER.info("Sync toc of {} with mode: {}", repo.getNamespace(), syncTocMode);
        if (syncTocMode.equals(SyncMode.IGNORE)) {
            return;
        }
        Assert.notNull(client, "client is null");
        Assert.notNull(toc, "toc is null");

        // 全覆盖模式
        if (SyncMode.OVERRIDE.equals(syncTocMode)) {
            // 先清空再同步
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
        // 合并模式
        else if (SyncMode.MERGE.equals(syncTocMode)) {
            List<MenuItem> subs = toc.getSubMenuItems();
            String currentTocJson = queryTocJson(client, repo.getNamespace());
            String lastUuid = null;
            for (int i = 0; i < subs.size(); i++) {
                MenuItem item = subs.get(i);
                String uuid = queryUuidOnDepth(currentTocJson, item, 1);
                item.setUuid(uuid);
                lastUuid = insertWithChild(client, repo.getNamespace(), item, new MenuItem(), lastUuid);
            }
        }
    }

    /**
     * 在覆盖模式下，清空全部目录
     *
     * @param client    语雀API 客户端
     * @param namespace 命名空间
     * @return 影响的条目数
     */
    protected void removeAll(YuqueClient client, String namespace) {
        Assert.notNull(client, "client is null");
        LOGGER.info("Remove all toc of {} start.", namespace);
        removeChildren(client, namespace, null);
        LOGGER.info("Remove all toc of {} end.", namespace);
    }

    /**
     * 删除子节点（不含自己）
     *
     * @param client    语雀API 客户端
     * @param namespace 命名空间
     * @param nodeUuid  目标节点的 uuid
     */
    protected void removeChildren(YuqueClient client, String namespace, String nodeUuid) {
        Assert.notNull(client, "client is null");
        String url = "/repos/" + namespace + "/toc";
        String json = client.get(url);
        JSONObject res = JSONObject.parseObject(json);
        JSONArray data = res.getJSONArray("data");
        for (Object datum : data) {
            JSONObject menuItem = (JSONObject) datum;
            if (StringUtils.isBlank(nodeUuid)) { // 代表清空根目录
                if (menuItem.getInteger("level") == 0) {
                    removeWithChildren(client, namespace, menuItem.getString("uuid"));
                }
            } else {
                if (trimToEmpty(menuItem.getString("parent_uuid")).equals(trimToEmpty(nodeUuid))) {
                    removeWithChildren(client, namespace, menuItem.getString("uuid"));
                }
            }
        }
    }

    /**
     * 删除节点以及其子节点
     *
     * @param client    语雀API 客户端
     * @param namespace 命名空间
     * @param nodeUuid  目标节点的 uuid
     */
    protected void removeWithChildren(YuqueClient client, String namespace, String nodeUuid) {
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
    protected String insert(YuqueClient client, String namespace, MenuItem menuItem, MenuItem parentMenuItem, String lastUuid) {
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
        map.put("url", menuItem.getSlug()); // 不是原始路径，是相对路径
        map.put("type", menuItem.getType().name());
        String res = client.put(url, null, JSON.toJSONString(map));
        return queryUuid(res, menuItem);
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
    protected String insertWithChild(YuqueClient client, String namespace, MenuItem menuItem, MenuItem parentMenuItem, String lastUuid) {
        Assert.notNull(client, "client is null");

        if (menuItem.getUuid() != null) {
            LOGGER.info("remove children of menu item: {}, url: {},  {}, parent: {}", menuItem.getTitle(),
                    menuItem.getSlug(), menuItem.getType().name(), parentMenuItem.getTitle());
            // 复用当前目录，但清空子目录
            removeChildren(client, namespace, menuItem.getUuid());
        } else {
            // 新建一个目录
            LOGGER.info("insert menu item: {}, {}, {}, parent:{}", menuItem.getTitle(), menuItem.getSlug(),
                    menuItem.getType().name(), parentMenuItem.getTitle());
            String uuid = insert(client, namespace, menuItem, parentMenuItem, lastUuid);
            menuItem.setUuid(uuid);
        }

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
     * 由于每次请求的返回结果是整个 toc，所以从返回结果中遍历查找中上一次操作的 uuid
     *
     * @param tocJson
     * @param item    要匹配的目录节点，需要title+url+type都匹配
     * @return 对应的 uuid
     * @throws RuntimeException 找不到结果抛异常
     */
    protected String queryUuid(String tocJson, MenuItem item) {
        Assert.notNull(item, "item is null!");

        JSONObject obj = (JSONObject) JSONObject.parse(tocJson);
        JSONArray data = obj.getJSONArray("data");
        if (data != null) {
            for (Object datum : data) {
                JSONObject mi = (JSONObject) datum;
                if (trimToEmpty(mi.getString("title")).equals(trimToEmpty(item.getTitle()))
                        && trimToEmpty(mi.getString("type")).equals(trimToEmpty(item.getType().name()))
                        && trimToEmpty(mi.getString("url")).equals(trimToEmpty(item.getSlug()))) {
                    return mi.getString("uuid");
                }
            }
        }
        throw new RuntimeException("Failed to query last uuid from result: " +
                item.getTitle() + "/" + item.getUrl() + "/" + item.getType());
    }

    /**
     * 由于每次请求的返回结果是整个 toc，所以从返回结果中遍历查找中上一次操作的 uuid
     *
     * @param tocJson 全部对象
     * @param item    要匹配的目录节点，需要title+url+type都匹配
     * @param depth   深度
     * @return 对应的 uuid
     */
    protected String queryUuidOnDepth(String tocJson, MenuItem item, int depth) {
        Assert.notNull(item, "item is null!");

        JSONObject obj = (JSONObject) JSONObject.parse(tocJson);
        JSONArray data = obj.getJSONArray("data");
        for (Object datum : data) {
            JSONObject mi = (JSONObject) datum;
            if (trimToEmpty(mi.getString("title")).equals(trimToEmpty(item.getTitle()))
                    && trimToEmpty(mi.getString("type")).equals(trimToEmpty(item.getType().name()))
                    && trimToEmpty(mi.getString("url")).equals(trimToEmpty(item.getSlug()))
                    && mi.getInteger("depth").equals(depth)) {
                return mi.getString("uuid");
            }
        }
        return null;
    }

    /**
     * 解析 json 到 TOC 对象
     *
     * @param tocJson toc json
     * @return TOC 对象
     */
    public TOC tocJsonToMenu(String tocJson) {
        JSONObject obj = (JSONObject) JSONObject.parse(tocJson);
        JSONArray data = obj.getJSONArray("data");
        TOC toc = new TOC();
        Map<String, MenuItem> map = new HashMap<>(); // {uuid, menuItem}
        for (Object datum : data) {
            JSONObject mi = (JSONObject) datum;
            MenuItem menuItem = new MenuItem();
            menuItem.setType(MenuItem.MenuItemType.parse(mi.getString("type")));
            menuItem.setTitle(mi.getString("title"));
            String uuid = mi.getString("uuid");
            menuItem.setUuid(uuid);
            menuItem.setUrl(mi.getString("url"));
            menuItem.setLevel(mi.getInteger("level"));
            menuItem.setSlug(mi.getString("slug")); // 分组情况下json 里 slug 是 #，先用 url
            String parentUuid = mi.getString("parent_uuid");
            if (StringUtils.isNotBlank(parentUuid)) {
                MenuItem pm = map.get(parentUuid);
                Assert.notNull(pm, "uuid: " + parentUuid + " is null!");
                menuItem.setParentMenuItem(pm);
                pm.getSubMenuItems().add(menuItem);
            }
            if (menuItem.getLevel() == 0) {
                toc.getSubMenuItems().add(menuItem);
            }
            map.put(uuid, menuItem);
        }
        return toc;
    }


    /**
     * 查询语雀知识库的现有数据
     *
     * @param client
     * @param namespace
     * @return
     */
    protected String queryTocJson(YuqueClient client, String namespace) {
        String url = "/repos/" + namespace + "/toc";
        return client.get(url);
    }

    enum SyncMode {
        /**
         * 不同步，只根据目录文件来同步文档，需要手动到语雀知识库里维护目录
         */
        IGNORE,
        /**
         * 全覆盖模式，推荐！适合全部文档托管到git库的场景，先清空原有目录再同步新目录
         */
        OVERRIDE,
        /**
         * 合并模式，适合部分文档托管到git库，部分直接语雀维护的场景，自动按照一级目录进行合并，如一级目录名变化可能存在垃圾数据
         */
        MERGE
    }
}
