package com.alipay.sofa.doc.integration.drm;

import com.alibaba.fastjson.JSON;
import com.alipay.drm.client.api.annotation.DAttribute;
import com.alipay.drm.client.api.annotation.DResource;
import com.alipay.sofa.specs.annotation.drm.DrmAttributeSpec;
import com.alipay.sofa.specs.annotation.drm.DrmResourceSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
@DResource(id = "com.alipay.sofa.doc.yuqueTokenDrm")
@DrmResourceSpec(name = "sofadoc 的语雀 token")
public class YuqueTokenDrm {

    public static final Logger LOGGER = LoggerFactory.getLogger(YuqueTokenDrm.class);

    @DAttribute
    @DrmAttributeSpec(name = "维护多个语雀token的json")
    private String tokenJson;

    Map<String, String> tokenCache = new ConcurrentHashMap<>();

    public String getTokenJson() {
        return tokenJson;
    }

    public YuqueTokenDrm setTokenJson(String tokenJson) {
        try {
            this.tokenJson = tokenJson;
            // 切换缓存
            Map<String, String> newTokenCache = new ConcurrentHashMap<>();
            newTokenCache.putAll(JSON.parseObject(tokenJson, Map.class));
            this.tokenCache = newTokenCache;
        } catch (Exception e) {
            LOGGER.error("Parse yuque token json error", e);
        }
        return this;
    }

    /**
     *
     * @param yuqueUser 语雀用户
     * @return 语雀 Token
     */
    public String getTokenByUser(String yuqueUser) {
        return tokenCache.get(yuqueUser);
    }
}
